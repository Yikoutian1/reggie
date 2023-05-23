package com.reggie.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
public class ReggieApplicationTests {
    /**
     * 截取后缀
     */
    @Test
    public void contextLoads() {
        String fileName = "abcdef.png";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
