package com.anteklantek.ppl_a;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void deserializationIsCorrect1() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);

        String serializedList = list.toString();
        serializedList = serializedList.substring(1, serializedList.length() - 1);

        ArrayList<Integer> deserialized = Helpers.parseIntegerList(serializedList);
        System.out.println(list);
        System.out.println(deserialized);
        assertEquals(list, deserialized);
    }

    @Test
    public void deserializationIsCorrect2() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(1);

        String serializedList = list.toString();
        serializedList = serializedList.substring(1, serializedList.length() - 1);

        ArrayList<Integer> deserialized = Helpers.parseIntegerList(serializedList);
        System.out.println(list);
        System.out.println(deserialized);
        assertEquals(list, deserialized);
    }

    @Test
    public void deserializationIsCorrect3() {
        ArrayList<Integer> list = new ArrayList<>();

        String serializedList = list.toString();
        serializedList = serializedList.substring(1, serializedList.length() - 1);

        ArrayList<Integer> deserialized = Helpers.parseIntegerList(serializedList);
        System.out.println(list);
        System.out.println(deserialized);
        assertEquals(list, deserialized);
    }

    @Test
    public void deserializationIsCorrect4() {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i<12; i++){
            list.add(i);
        }

        String serializedList = list.toString();
        serializedList = serializedList.substring(1, serializedList.length() - 1);

        ArrayList<Integer> deserialized = Helpers.parseIntegerList(serializedList);
        System.out.println(list);
        System.out.println(deserialized);
        assertEquals(list, deserialized);
    }


}