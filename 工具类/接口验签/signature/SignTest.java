package signature;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignTest {
    @Test
    public void  MD5Test(){
        String appkey = MD5_Appkey_Appsecret.getAppkey();
        String appScrect = MD5_Appkey_Appsecret.getAppScrect();
        System.out.println("appkey:"+appkey+"  length="+appkey.length());
        System.out.println("appScrect:"+appScrect+"  length="+appScrect.length());
    }

    @Test
    public void signTest(){
        Map<String,String> params=new HashMap<String,String>();
        params.put("userName","ucxxxxxx");
        params.put("password","123456");
        params.put("accountType","1");
        String  appkey="66407340-1f98-4689-8090-93835b2a11ff";
        String  appSecret="af74617d909551c2f5469d33580a6be0";
        String  timestamp="201811290830";
        String  url="htpp://127.0.0.1/getProduct";
        String signature = SignatureUtil.sign(params, appkey, appSecret, timestamp, url);
        System.out.println("signature: "+signature+"  length="+signature.length());

    }
}
