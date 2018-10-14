package com.test;

import com.util.MD5Util;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;

public class test_md5 {

    @Test
    public void test_md5(){
        String result= MD5Util.getMD5Str("HELLa WORLD");
        String result1= MD5Util.getMD5Str("104615");
        System.out.print(result1);
    }
}
