package org.crudboy.cloud.mall.common.util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5工具
 */
public class MD5Utils {
    private static final String SALT = "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";
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        strValue += SALT;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }
}