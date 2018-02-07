package com.alpha.hxq;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hukang on 2016/10/31.
 */
public class Utils {
    public static byte[] hexToReversedByteArray(String var0) {
        if(var0 != null && var0.length() != 0) {
            if(var0.length() % 2 == 1) {
                var0 = 0 + var0;
            }

            String[] var1;
            byte[] var2 = new byte[(var1 = new String[var0.length() / 2]).length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
                var1[var3] = var0.substring((2 * var3), 2 *(var3+1));

                try {
                    var2[var3] = (byte)Integer.parseInt(var1[var3], 16);
                } catch (NumberFormatException var4) {
                    var4.printStackTrace();
                }
            }

            return var2;
        } else {
            return null;
        }
    }
    public static String getCheckSum(String str){
        if (str != null&&str.length() != 0){
            byte [] var = new byte[str.length()/2];
            for (int i = 0;i<var.length;i++){
                var[i] = (byte) Integer.parseInt(str.substring(2*i,2*i+2),16);
            }
            int k = 0;
            for (int j =0;j<var.length;j++){
                k = k^var[j];
            }
            String s = Integer.toHexString(k);
            if (s.length()<2){
                s= "0"+s;
            }
            if (s.length()>=2){
                return str+s.substring(s.length()-2,s.length());
            }else {
                return null;
            }
        }else {
          return null;
        }
    }
    public static boolean getCheckSumResult(String str){
        if (str != null&&str.length() != 0){
            byte [] var = new byte[str.substring(0,str.length()-2).length()/2];
            for (int i = 0;i<var.length;i++){
                var[i] = (byte) Integer.parseInt(str.substring(2*i,2*i+2),16);
            }
            int k = 0;
            for (int j =0;j<var.length;j++){
                k = k^var[j];
            }
            String s = Integer.toHexString(k);
            if (s.length()<2){
                s= "0"+s;
            }
            if (s.length()>=2){
                return s.substring(s.length()-2,s.length()).equalsIgnoreCase(str.substring(str.length()-2,str.length()));
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
    public static String str2ascii(String str){
        char [] chars = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<chars.length;i++){
            sb.append(Integer.toHexString(chars[i]));
        }
        return sb.toString();
    }
    public static boolean checkPassword(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[0-9]{6}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
}
