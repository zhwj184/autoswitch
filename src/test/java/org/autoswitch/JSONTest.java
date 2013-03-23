package org.autoswitch;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONSerializer;

import org.autoswitch.test.TestBean;

public class JSONTest {

	public static void main(String[] args) {
		TestBean testBean1 = new TestBean();
		testBean1.setId(1);
		testBean1.setName("aaa");
		TestBean testBean2 = new TestBean();
		testBean2.setId(1);
		testBean2.setName("aaa");
		TestBean testBean3 = new TestBean();
		testBean3.setId(1);
		testBean3.setName("aaa");
		List<TestBean> list = new ArrayList<TestBean>();
		list.add(testBean1);
		list.add(testBean2);
		list.add(testBean3);
		System.out.println(JSONSerializer.toJSON(list));
	}
}
