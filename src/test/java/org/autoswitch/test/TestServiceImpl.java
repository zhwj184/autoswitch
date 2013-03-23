package org.autoswitch.test;

import java.util.List;

public class TestServiceImpl implements TestService{
	
	//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.hello&status=open&jsonResult=1
	public void hello(){
		System.out.println("hello");
	}
	
	//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.sayHello&status=open&jsonResult={ret:%22goodbuy%22}
	public String sayHello(){
		return "sayHello";
	}
	
	public List<TestBean> getNames(){
		return null;
	}
	
//	http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.getBeans&status=open&jsonResult={"catList":["123","456","789"],"id":1,"name":"aaa"}
	public TestBean getBeans(){
		return null;
	}

}
