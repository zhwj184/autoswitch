package org.autoswitch;

import java.util.List;

import org.autoswitch.test.TestBean;
import org.autoswitch.test.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-bean.xml");
		TestService testControl = (TestService) context.getBean("testService");
		try{
			testControl.hello();
			System.out.println(testControl.sayHello());
			List<TestBean> list = testControl.getNames();
			for(TestBean bean: list){
				System.out.println(bean.getId() + bean.getName() + bean.getCatList());
			}
			TestBean bean = testControl.getBeans();
			System.out.println(bean.getId() + bean.getName() + bean.getCatList());
		}catch(Exception e){}

		
		for(int i = 0; i < 10; i++){
			try{
//				testControl.hello();
//				System.out.println(testControl.sayHello());
				List<TestBean> list = testControl.getNames();
				for(TestBean bean: list){
					System.out.println(bean.getId() + bean.getName() + bean.getCatList());
				}
//				TestBean bean = testControl.getBeans();
//				System.out.println(bean.getId() + bean.getName() + bean.getCatList());
			}catch(Exception e){
				e.printStackTrace();
			}	
		}

	}
}
