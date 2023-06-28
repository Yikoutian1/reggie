package com.reggie.reggie;

import com.ithang.reggie.ReggieApplication;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName redisTest
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/28 028 10:41
 * @Version 1.0
 */
@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class redistest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * String 类型的操作
     */
    @Test
    public void testString(){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("qiulihang","6666");
        String name = valueOperations.get("qiulihang");
        System.out.println(name);

        // 设置key,value,10秒过期
        redisTemplate.opsForValue()
                .set("key","value",10l, TimeUnit.SECONDS);
        // 设置存在就执行操作
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("key", "myValue");
        System.out.println(aBoolean);

    }
    /**
     * 操作hash类型
     */
    @Test
    public void testHash(){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        // 一个字段一个字段的存
        hashOperations.put("001","name","xiaomin");
        hashOperations.put("001","age","20");
        hashOperations.put("001","addr","beijin");
        // 一个字段一个字段的取
        String name = (String) hashOperations.get("001", "name");
        System.out.println(name);
        // 获取hash结构中的所有字段
        Set keys = hashOperations.keys("001");
        System.out.println("---------------");
        for (Object key:
             keys) {
            System.out.println(key);
        }
        // 获取hash结构中的所有值
        System.out.println("---------------");
        List<Object> values = hashOperations.values("001");
        for(Object value:values){
            System.out.println(value);
        }
    }
    /**
     * 操作list类型的数据
     */
    @Test
    public void testList(){
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        // 可以有重复元素
        listOperations.leftPush("mylist","a");
        listOperations.leftPushAll("mylist","b","c","d");//leftPushAll存多个

        // 取值
        List<String> mylist = listOperations.range("mylist", 0, -1);
        for(String list : mylist){
            System.out.println(list);
        }

        // 获得列表长度
        Long size = listOperations.size("mylist");
        int lsize = size.intValue();
        for (int i = 0; i < lsize; i++) {
            // 出队列(队尾)
            String all = listOperations.rightPop("mylist");
            System.out.println("出队列元素为" + all + "  " + i);
        }
    }
    /**
     * 操作set类型数据
     */
    @Test
    public void testSet(){
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        //存储(没有重复的值)
        setOperations.add("myset","a","b","c","a");
        //取值
        Set<String> myset = setOperations.members("myset");
        for(String o:myset){
            System.out.println(o);
        }
        //删除成员
        setOperations.remove("myset","a","b");
        System.out.println("set操作完成");
    }
    /**
     * 操作ZSet类型的数据
     */
    @Test
    public void testZSet(){
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        //存储(没有重复的值)
        zSetOperations.add("myZSet","a",10.0);
        zSetOperations.add("myZSet","b",11.0);
        zSetOperations.add("myZSet","c",12.0);
        zSetOperations.add("myZSet","d",13.0);
        //取值(所有)
        Set<String> myZSet = zSetOperations.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
        //修改分数
        zSetOperations.incrementScore("myZSet","b",20);

        //删除成员
        zSetOperations.remove("myZSet","d","b");

        // 再次查看取值
        myZSet = zSetOperations.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
        // 删除所有
        zSetOperations.removeRange("myZSet",0,-1);
        System.out.println("全部删除");
        myZSet = zSetOperations.range("myZSet", 0, -1);
        myZSet.forEach(System.out::println);
    }
    /**
     * 迪用操作,针对不同的数据类型都可以操作
     */
    @Test
    public void testCommon(){
        // 获取redis所有的key
        Set<String> keys = redisTemplate.keys("*");
        keys.forEach(System.out::println);
        // 判断某个key是否存在
        Boolean aBoolean = redisTemplate.hasKey("idcast");
        System.out.println(aBoolean);
        // 删除指定key
        redisTemplate.delete("myZSet");
        // 获取指定key对应的value的数据类型
        DataType type = redisTemplate.type("myset");
        System.out.println(type.name());
    }
}
