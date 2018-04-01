package cn.itcast.rpc.simple.client;

/**
 * @author y15079
 * @create 2018-04-01 13:32
 * @desc
 **/
public interface HelloService {
	String hello(String name);

	String hello(Person person);
}
