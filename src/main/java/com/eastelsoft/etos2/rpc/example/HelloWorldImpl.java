package com.eastelsoft.etos2.rpc.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloWorldImpl implements HelloWorld {
	static AtomicInteger count = new AtomicInteger(0);

	public String sayHello(String who) {
//		System.out.println("Hello world, " + who + ", "
//				+ count.incrementAndGet());
		return "Wellcome, " + who;
	}

	public int add(int i, int k) {
		// TODO Auto-generated method stub
		return i + k;
	}

	public List<String> list() {
		// TODO Auto-generated method stub
		String[] ss = new String[]{"1","2","3"};
		return Arrays.asList(ss);
	}
}
