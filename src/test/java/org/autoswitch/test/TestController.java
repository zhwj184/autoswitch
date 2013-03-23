package org.autoswitch.test;

import java.util.List;

import javax.annotation.Resource;

public class TestController {
	
	@Resource
	private TestService testService;

	public void test1(){
		testService.hello();
	}
	
	public void test2(){
		System.out.println(testService.sayHello());
	}
	
	public void test3(){
		List<TestBean> list = testService.getNames();
		for(TestBean bean: list){
			System.out.println(bean.getId() + bean.getName() + bean.getCatList());
		}
	}
	
	public void test4(){
		TestBean bean = testService.getBeans();
		System.out.println(bean.getId() + bean.getName() + bean.getCatList());
	}
}
