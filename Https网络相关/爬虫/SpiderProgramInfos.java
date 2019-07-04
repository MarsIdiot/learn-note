
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * \* Date: 2019/5/24
 * \* Time: 13:48
 * \* Description: 统计个项目数量，ip，主机
 * \
 */
public class SpiderProgramInfos {


    public static String cookie = "xxxx-intranet-test-sid=17bb5041-4c18-4888-aa4a-72887fe2f443.114; xxx-unionLogin-sid=7bcad03d-d1f0-41e4-8bd8-7b8a64d7b21b; csrftoken=7IFpVMwE1wJdfRgsZOBPCgtlXuDOdWMI; djangoauth=l79fq8ujvlfkk78hh49eo2i4j32pmx1u; sidebarStatus=1";


    //需要爬的业务线(格式：ycc,AMP,....)
    public static String tasks = "ycc";



    public static void main(String[] args){

        //获取所有节点的数据集合
        getList();

//        getData();

    }

    private static void getList() {

        String url = "http://xxxxx/utree_api/boltdb_trees/";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json, text/plain, */*");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Cookie", cookie);
        get.setHeader("Host", "xxxx");
        get.setHeader("Referer", "http://xxxx/v2/");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");

        try {
            CloseableHttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONArray data = jsonObject.getJSONArray("data");
            for(int i = 1; i < data.size(); i++){//预生产，生产，测试
                JSONObject eachEnvironment = data.getJSONObject(i);
                String text = eachEnvironment.getString("text");//名称
                if(text.contains("PRE")){
                    text = "pre";
                } else if(text.contains("PROD")){
                    text = "prod";
                } else if(text.contains("TEST")){
                    text = "test";
                }
                JSONArray children = eachEnvironment.getJSONArray("children");//每一个环境下的所有业务线
                for(int j = 0; j < children.size(); j++){
                    JSONObject task = children.getJSONObject(j); //业务线情况
                    String taskName = task.getString("text");//业务线名称
                    taskName = taskName.toLowerCase();//转成小写
                    JSONArray programList = task.getJSONArray("children");//每一个业务线下的所有项目集合
                    for(int p = 0; p < programList.size(); p++){
                        JSONObject allPrograms = programList.getJSONObject(p);
                        JSONArray programs = allPrograms.getJSONArray("children");
                        if(programs == null || programs.size() == 0){
                            continue;
                        }
                        for(int q = 0; q < programs.size(); q++){//每一个具体项目
                            JSONObject eachProgram = programs.getJSONObject(q);
                            String programName = eachProgram.getString("text");//项目名

                            //遍历获取各个业务线具体情况
                            String baseUrl = "http://xxx/utree_api/servers/?";
                            String query = "criteria=%7B%22EnvType%22:%22" + text + "%22,%22prod_type%22:%22" + taskName + "%22,%22AppName%22:%22" + programName + "%22%7D&type=";

                            if(tasks.contains(taskName)){
                                getData(baseUrl + query, text, taskName, programName);
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void getData(String url, String text, String taskName, String programName) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json, text/plain, */*");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Cookie", cookie);
        get.setHeader("Host", "xxxx");
        get.setHeader("Referer", "http://xxxx/v2/");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");

        try {
            CloseableHttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity());
            JSONArray arr = JSONArray.parseArray(result);
            String view = "";
            for(int i = 0; i < arr.size(); i++){
                JSONObject each = arr.getJSONObject(i);
                String internal_ip = each.getString("internal_ip");
                String name = each.getString("name");
                view += text + "    " + taskName + "      " + programName + "      " + internal_ip + "     " + name + "\r\n";
            }
            if(view.equals("")){
                System.out.println(text + "    " + taskName + "      " + programName + "      " + "数量为0");
            } else {
                System.out.println(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
