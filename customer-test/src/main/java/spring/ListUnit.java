package spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ListUnit {

    // 生成一个随机排列的列表
    public static List<Integer> getRandomList(int maxNumber) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= maxNumber; i++) {
            list.add(i);
        }

        Collections.shuffle(list, new Random());
        return list;
    }

    // 测试
    public static void main(String[] args) {
        List<Integer> list = getRandomList(9);
        System.out.println(list);
    }

}

