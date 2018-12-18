package signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @Description:
 * @Auther:
 * @Date: 2018/11/26 14:08
 */
public class MD5_Appkey_Appsecret {
    private String appkey;
    private String appScrect;

    /**
     * UUID随机生成appKey
     */
    public  static String  getAppkey(){
        String appKey = UUID.randomUUID().toString();
        return appKey;
    }

    /**
     * MD5加密生成appScrect
     */
    public   static String  getAppScrect() {
        String appScrect="";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //digest要求传入byte，返回byte[]字节数组
            byte[] digest = md5.digest(MD5_Appkey_Appsecret.getAppkey().getBytes());
            //做相应的转化（十六进制）
            String MD5String = hex(digest);
            appScrect=MD5String;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return  appScrect;
    }

    /**
     * 将字节数组转换为16进制字符串
     */
    public static String hex(byte[] bytes){
        StringBuffer sb=new StringBuffer("");
        int i=0;//接收bytes元素
        for(int offset=0;offset<bytes.length;offset++){
            i=bytes[offset];
            if (i < 0){
                i += 256;
            }
            if (i < 16) {
                sb.append("0");
            }
            String hexString = Integer.toHexString(i);
            sb.append(hexString);
        }
        return sb.toString();
    }

}


/**
 * public String substring(int beginIndex, int endIndex)
 * 第一个int为开始的索引，对应String数字中的开始位置，
 * 第二个是截止的索引位置，对应String中的结束位置
 * 1、取得的字符串长度为：endIndex - beginIndex;
 * 2、从beginIndex开始取，到endIndex结束，从0开始数，其中不包括endIndex位置的字符
 * 如：
 * "hamburger".substring(4, 8) returns "urge"
 *  "smiles".substring(1, 5) returns "mile"
 * 取长度大于等于3的字符串a的后三个子字符串，只需a.subString(a.length()-3, a.length());
 *
 * */


    /**
     * 字节数组转成十六进制字符串
     * @param byte[]
     * @return HexString
     */
   /* public static String toHexString1(byte[] b){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i){
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }
    public static String toHexString1(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }*/


