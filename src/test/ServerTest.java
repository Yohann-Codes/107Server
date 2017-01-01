package test;

import com.google.gson.Gson;
import org.junit.Test;
import utils.StringUtils;

import java.util.*;

/**
 * Created by Yohann on 2016/8/11.
 */
public class ServerTest {
    @Test
    public void test() {
//        List<Map> list = new ArrayList<>();
        List<String> list = new ArrayList<>();

//        Map map1 = new HashMap();
//        map1.put("name", "张三");
//        map1.put("phone", 123);
//
//        Map map2 = new HashMap();
//        map2.put("name", "李四");
//        map2.put("phone", 321);
//
//        list.add(map1);
//        list.add(map2);


        list.add("张三");
        list.add("李四");
        list.add("李四");

        System.out.println(list);
    }
}
