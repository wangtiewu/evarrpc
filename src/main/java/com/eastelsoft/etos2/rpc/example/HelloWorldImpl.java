package com.eastelsoft.etos2.rpc.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloWorldImpl implements HelloWorld {
	static AtomicInteger count = new AtomicInteger(0);

	public String sayHello(String who) {
		// System.out.println("Hello world, " + who + ", "
		// + count.incrementAndGet());
		return "Wellcome, " + who;
	}

	public int add(int i, int k) {
		// TODO Auto-generated method stub
		return i + k;
	}

	public List<String> list() {
		// TODO Auto-generated method stub
		String[] ss = new String[] { "1", "2", "3" };
		return Arrays.asList(ss);
	}

	public void test1(String str, int i) {
		System.out.println("test1 with 2 args");
		return;
	}

	public void test1(int k, int j, String str) {
		System.out.println("test1 with 3 args");
	}

	public void test_void() {
		System.out.println("test_void");
	}
	
	public boolean test_0() {
		System.out.println("return boolean");
		return true;
	}

	public byte test_1() {
		System.out.println("return byte");
		return (byte) 0;
	}

	public char test_2() {
		System.out.println("return char");
		return 'a';
	}

	public short test_3() {
		System.out.println("return short");
		return Short.MAX_VALUE;
	}

	public int test_4() {
		System.out.println("return int");
		return Integer.MAX_VALUE;
	}

	public long test_5() {
		System.out.println("return long");
		return Long.MAX_VALUE;
	}

	public double test_6() {
		System.out.println("return double");
		return 9.9D;
	}

	public float test_7() {
		System.out.println("return float");
		return 0.111F;
	}

	public List<Integer> test_list() {
		System.out.println("return list");
		List<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(100);
		return a;
	}

	public Map test_map() {
		System.out.println("return map");
		Map a = new HashMap();
		a.put("1", "string");
		a.put("2", 2);
		return a;
	}

	public String[] test_ary_string() {
		System.out.println("return string_ary");
		return new String[] { "str1", "str2" };
	}
}
