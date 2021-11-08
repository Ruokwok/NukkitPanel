package cc.ruok.nukkitpanel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Common {

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyz0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 中文转Unicode
     * @param str
     * @return
     */
    public static String ch2Unicode(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>>8); //取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); //取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    public static String decodeUnicode(String str) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = str.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = str.substring(start + 2, str.length());
            } else {
                charStr = str.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }


    public static String md5(File file){
        if(!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream fis;
        byte[] buffer =new byte[1024];
        try{
            if(!file.isFile()) {
                return"";
            }
            digest = MessageDigest.getInstance("MD5");
            fis =new FileInputStream(file);
            while(true) {
                int len;
                if((len = fis.read(buffer,0,1024)) == -1) {
                    fis.close();
                    break;
                }
                digest.update(buffer,0, len);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger var5 =new BigInteger(1, digest.digest());
        return String.format("%1$032x", var5);
    }


    public static String sha256(String str){
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }


    /**
         * 将byte转为16进制
         * @param bytes
         * @return
         */
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
            //1得到一位的进行补0操作
            stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    public static Map<String, String> ini2map(String content) {
            Map<String, String> d = new HashMap();
            String[] var4 = content.split("\n");
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String line = var4[var6];
                line = line.trim();
                if (!line.equals("") && line.charAt(0) != '#') {
                    String[] t = line.split("=");
                    if (t.length >= 2) {
                        String key = t[0];
                        String value = "";

                        for(int i = 1; i < t.length - 1; ++i) {
                            value = value + t[i] + "=";
                        }

                        value = value + t[t.length - 1];
                        if (!value.equals("")) {
                            d.put(key, value);
                        }
                    }
                }
            }
            return d;
    }

    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return null;
        }
        if (strEndIndex < 0) {
            return null;
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }

    public static Map<String, String> urlGet(String url) {
        String list = url.contains("?")?url.substring(url.indexOf("?")):url;
        String[] split = list.split("&");
        HashMap<String, String> map = new HashMap<>();
        for (String s : split) {
            int i = s.indexOf("=");
            map.put(s.substring(0, i), s.substring(i + 1));
        }
        return map;
    }

}
