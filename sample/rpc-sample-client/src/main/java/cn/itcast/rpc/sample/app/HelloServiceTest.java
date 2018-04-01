package cn.itcast.rpc.sample.app;

import cn.itcast.rpc.client.RpcProxy;
import cn.itcast.rpc.simple.client.HelloService;
import cn.itcast.rpc.simple.client.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author y15079
 * @create 2018-04-01 13:50
 * @desc
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {

	@Autowired
	private RpcProxy rpcProxy;

	@Test
	public void helloTest1(){
		//调用代理的create方法，代理HelloService接口
		HelloService helloService = rpcProxy.create(HelloService.class);

		//调用代理的方法，执行invoke
		String result = helloService.hello("World");
		System.out.println("服务端返回结果：");
		System.out.println(result);
	}

	@Test
	public void helloTest2(){
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello(new Person("Yong", "Huang"));
		System.out.println("服务端返回结果：");
		System.out.println(result);
	}
}
