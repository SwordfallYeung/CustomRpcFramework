package cn.itcast.rpc.sample.server;

import cn.itcast.rpc.server.RpcService;
import cn.itcast.rpc.simple.client.HelloService;
import cn.itcast.rpc.simple.client.Person;

/**
 * @author y15079
 * @create 2018-04-01 13:43
 * @desc
 **/
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{

	public String hello(String name) {
		System.out.println("已经调用服务端接口实现，业务处理结果为：");
		System.out.println("Hello! " + name);
		return "Hello! " + name;
	}

	public String hello(Person person) {
		System.out.println("已经调用服务端接口实现，业务处理为：");
		System.out.println("Hello! " + person.getFirstName() + " " + person.getLastName());
		return "Hello! " + person.getFirstName() + " " + person.getLastName();
	}
}
