package com.eastelsoft.etos2.rpc.example;

import java.util.List;
import java.util.Map;

public interface HelloWorld {
	public String sayHello(String who);
	public int add(int i, int k);
	public List<String> list();
	public void test1(String str, int i);
	public void test1(int k, int j, String str);
	void test_void();
	boolean test_0();
	byte test_1();
	char test_2();
	short test_3();
	int test_4();
	long test_5();
	double test_6();
	float test_7();
	List<Integer> test_list();
	Map test_map();
	String[] test_ary_string();
}
