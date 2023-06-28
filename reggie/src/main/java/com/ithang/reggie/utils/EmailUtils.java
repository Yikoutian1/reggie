package com.ithang.reggie.utils;


import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * @ClassName EmailUtils
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/2 002 21:39
 * @Version 1.0
 */

public class EmailUtils {
    public static void sendAuthCodeEmail(String email, String authCode) {
        try {
            SimpleEmail mail = new SimpleEmail();
            // 发送邮件的服务器,这个是qq邮箱的，不用修改
            mail.setHostName("smtp.qq.com");
            // 第一个参数是对应的邮箱用户名一般就是自己的邮箱第二个参数就是SMTP的密码,我们上面获取过了
            mail.setAuthentication("485544150@qq.com", "pdcwpuxqdztgcbah");
            // 发送邮件的邮箱和发件人
            mail.setFrom("485544150@qq.com","测试邮箱名字");
            //使用安全链接
            mail.setSSLOnConnect(false);
            //接收的邮箱
            mail.addTo(email);
            //设置邮件的主题
            mail.setSubject("验证码");
            //设置邮件的内容
            mail.setMsg("尊敬的用户:你好!\n 登陆验证码为:" + authCode+"\n"+"     (有效期为五分钟)");
            mail.send();//发送
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

}
