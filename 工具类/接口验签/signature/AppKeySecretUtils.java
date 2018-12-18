package signature;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * Description: 生成 appKey 和 appSecret 的工具类
 *
 *
 * @time: 2018/11/24 15:49
 * @version: V1.0
 */
public class AppKeySecretUtils {

    /** 加盐加密中, 生成随机数的长度 */
    private static final Integer SALT_LENGTH = 16;

    /** MD5_Appkey_Appsecret 加密后密文长度 */
    private static final Integer MD5_CIPHERTEXT_LENGTH = 32;

    /** 加盐 MD5_Appkey_Appsecret 算法后总长度 */
    private static final Integer SALT_MD5_CIPHERTEXT_LENGTH = 48;

    /**
     * 生成一个随机的 appKey
     *
     * @return appKey
     */
    public static String getAppKey() {
        return getUUID32();
    }

    /**
     * 生成一个随机的 appSecret
     *
     * @param appKey 传入的 appKey
     * @return appSecret
     */
    public static String getAppSecret(String appKey) {
        return getSaltMD5(appKey);
    }

    /**
     * 验证加盐后和原明文是否一致
     *
     * @param text 加密前明文
     * @param md5str 加密后密文
     * @return 是否一致
     */
    public static boolean getSaltVerifyMD5(String text, String md5str) {
        char[] cs1 = new char[MD5_CIPHERTEXT_LENGTH];
        char[] cs2 = new char[SALT_LENGTH];
        for (int i = 0; i < SALT_MD5_CIPHERTEXT_LENGTH; i += 3) {
            cs1[i / 3 * 2] = md5str.charAt(i);
            cs1[i / 3 * 2 + 1] = md5str.charAt(i + 2);
            cs2[i / 3] = md5str.charAt(i + 1);
        }
        String Salt = new String(cs2);
        return md5Hex(text + Salt).equals(String.valueOf(cs1));
    }

    /**
     * 生成空间唯一 uuid 值
     *
     * @return uuid
     */
    private static String getUUID32() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }

    /**
     * 生成含有随即盐的密文
     *
     * @param text 需要加密的明文
     * @return 加盐密文
     */
    private static String getSaltMD5(String text) {

        // 生成盐所需要的一个 16位 的随机数
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SALT_LENGTH);
        sb.append(random.nextInt(99999999)).append(random.nextInt(99999999));
        int len = sb.length();
        if (len < SALT_LENGTH) {
            for (int i = 0; i < SALT_LENGTH - len; i++) {
                sb.append("0");
            }
        }
        // 生成最终的盐
        String salt = sb.toString();

        String cipherText = md5Hex(text + salt);
        char[] cs = new char[SALT_MD5_CIPHERTEXT_LENGTH];
        for (int i = 0; i < cs.length; i += 3) {
            cs[i] = cipherText.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = cipherText.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }

    /**
     * MD5_Appkey_Appsecret 加密,并把结果由字节数组转换成 16进制 字符串
     *
     * @param string 要加密的字符串
     * @return 返回加密后 16进制 字符串
     */
    private static String md5Hex(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5_Appkey_Appsecret");
            byte[] digest = md.digest(string.getBytes());
            return hex(digest);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将 byte[] 字节数组转换成 16进制字节字符串
     *
     * @param arr 要转换的 byte[] 字节数组
     * @return 返回 16进制 字符串
     */
    private static String hex(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte anArr : arr) {
            sb.append(Integer.toHexString((anArr & 0xff) | 0x100), 1, 3);
        }
        return sb.toString();
    }
}
