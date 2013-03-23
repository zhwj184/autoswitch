autoswitch
==========

java 服务屏蔽开关系统，可以手工降级服务，关闭服务
基于spring AOP机制，可以在特殊情况下屏蔽相关service类的某些返回，并且支持定义默认返回结果，随机屏蔽某些异常服务。
通过启动一个内置的http server来监听外部指令。

对当前应用的影响。

使用指南：

1.在spring配置文件中添加如下，其中switch-service-pointcut是添加紧急情况下需要屏蔽的方法列表

    <aop:config proxy-target-class="true"></aop:config>
    
    <bean id="switchInteceptor" class="org.autoswitch.SwitchInteceptor">
    </bean>
    <bean id="switch-service-pointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut">
        <property name="patterns">
            <list>
                <value>org.autoswitch.test.*</value>
            </list>
        </property>
    </bean>
    <aop:config>
        <aop:advisor advice-ref="switchInteceptor" pointcut-ref="switch-service-pointcut"/>
    </aop:config>
    
    <bean id="wwitchControlHttpServer" class="org.autoswitch.SwitchControlHttpServer" init-method="init"></bean>
    
    <bean id="testService" class="org.autoswitch.test.TestServiceImpl" />
    
    <bean id="testController" class="org.autoswitch.test.TestController" />

2. 例如下面的service，上面注释分别是在应用启动后手工屏蔽该服务调用，以后每次调用直接用参数的jsonResult反序列后返回，
classmethod是具体到某个方法名称，status为open关闭该服务，close表示重新打开服务，jsonResult是mock返回结果的json串，
如果是基本类型，则必须用ret作为key，其他list，bean之类的就直接用json串，type表示如果list有泛型的话则是返回的类完整类型；
   
  
		public class TestServiceImpl implements TestService{
	  
		//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.hello&status=open&jsonResult=1
		public void hello(){
			System.out.println("hello");
		}
		
		//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.sayHello&status=open&jsonResult={ret:%22goodbuy%22}
		public String sayHello(){
			return "sayHello";
		}
		//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.getNames&status=open&jsonResult=[{"catList":[],"id":1,"name":"aaa"},{"catList":[],"id":1,"name":"aaa"},{"catList":[],"id":1,"name":"aaa"}]&type=org.autoswitch.test.TestBean
		public List<TestBean> getNames(){
			return null;
		}
		
		//http://localhost:8080/control/a.htm?classmethod=org.autoswitch.test.TestServiceImpl.getBeans&status=open&jsonResult={"catList":["123","456","789"],"id":1,"name":"aaa"}
		public TestBean getBeans(){
			return null;
		}
		
		}


3调用示例代码
	
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
					System.out.println(testControl.sayHello());
	//				List<TestBean> list = testControl.getNames();
	//				for(TestBean bean: list){
	//					System.out.println(bean.getId() + bean.getName() + bean.getCatList());
	//				}
	//				TestBean bean = testControl.getBeans();
	//				System.out.println(bean.getId() + bean.getName() + bean.getCatList());
				}catch(Exception e){
					e.printStackTrace();
				}	
			}
	
		}
	}

4.输出
  
	Listening on port 8080
	hello
	sayHello
	Incoming connection from /127.0.0.1
	New connection thread
	goodbuy
	goodbuy
	Incoming connection from /127.0.0.1
	New connection thread
	sayHello
	sayHello
	sayHello
	sayHello
	sayHello
	sayHello
	sayHello
	sayHello

这里只是提供一种示例，如果要在生产环境中使用，则需要对并发控制，返回结果的序列化，方法名称一致参数不一致等各种情况进行控制，
同时还需要对权限，后台管理系统等可以做优化。
