package signature;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 接口签名验证
 *
 * @date: 2018/11/26
 * @version: 1.0
 */
public class SignatureUtil {
    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String sign(Map<String, String> params, String appKey, String appSecret, String timestamp, String url) {
       //1、对传入的参数的key进行排序
        Set<String> paramsKeySet = params.keySet();
        String[] keys = paramsKeySet.toArray(new String[paramsKeySet.size()]);
        Arrays.sort(keys);//字母顺序
        //2、拼接
        String joinedParams = joinRequestParams(params, appKey, appSecret, timestamp, url, keys);
        //3、MD5加密
        byte[] abstractMessage = digest(joinedParams);
        //4、字节数组转换为16进制，得到签名sign
        return byte2Hex(abstractMessage);
    }
    /*
    Set和Map类似，也是一组key的集合，但不存储value。由于key不能重复，所以，在Set中，没有重复的key
     */

    /*
    字节数组转换为16位字符串
    byte[] 8位二进制数组
     */
    private static String byte2Hex(byte[] data) {
        int length = data.length;
        char hexChars[] = new char[length * 2];
        int index = 0;
        for (byte value : data) {
            hexChars[index++] = HEX_DIGITS[value >>> 4 & 0xf];
            hexChars[index++] = HEX_DIGITS[value & 0xf];
        }
        return new String(hexChars);
    }

    private static byte[] digest(String message) {
        try {
            MessageDigest md5Instance = MessageDigest.getInstance("MD5");
            md5Instance.update(message.getBytes("UTF-8"));
            return md5Instance.digest();
        } catch (Exception e) {
            throw new RuntimeException("签名验证失败", e);
        }
    }

    private static String joinRequestParams(Map<String, String> params, String appKey, String appSecret, String timestamp, String url, String[] sortedKeys) {

        StringBuilder sb = new StringBuilder();
        sb.append(timestamp); // 拼接timestamp
        sb.append(appKey); // appKey
        sb.append(appSecret); // appSecret
        sb.append(url);
        for (String key : sortedKeys) {
            String value = params.get(key);
            if (isNotEmpty(key) && isNotEmpty(value)) {
                sb.append(key).append(value);
            }
        }
        return sb.toString();
    }

    private static boolean isNotEmpty(String s) {
        return null != s && !"".equals(s);
    }
}