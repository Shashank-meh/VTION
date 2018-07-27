package com.vtion.karvyinsights;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(check("Hello, thanok you for reg. Your OTP is 123456"), "123456");
    }

    public String check(String str) {
        String otp = str.substring(str.indexOf("OTP is ") + 7, str.indexOf("OTP is") + 13);
        return otp;
    }
}