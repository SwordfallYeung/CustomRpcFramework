package cn.itcast.rpc.server;

import cn.itcast.rpc.common.RpcDecoder;
import cn.itcast.rpc.common.RpcEncoder;
import cn.itcast.rpc.common.RpcRequest;
import cn.itcast.rpc.common.RpcResponse;
import cn.itcast.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author y15079
 * @create 2018-04-01 13:02
 * @desc
 * 框架的RPC服务器（用于将用户系统的业务类发布为RPC服务）
 * 使用时可由用户通过spring-bean的方法注入到用户的业务系统中
 * 由于本类实现了ApplicationContextAware InitializingBean
 *
 * spring构造本对象时会调用setApplicationContext()方法，从而可以在方法中通过自定义注解获得用户的业务接口和实现
 *
 *
 **/
public class RpcServer implements ApplicationContextAware, InitializingBean{

	private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;

	//用于存储业务接口和实现类的实例对象（由spring所构造）
	private Map<String, Object> handlerMap = new HashMap<String, Object>();

	public RpcServer(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	//服务器绑定的地址和端口由spring在构造本类时从配置文件中传入
	public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		//用于向zookeeper注册名称服务的工具类
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * 通过注解，获取标注了rpc服务注解的业务类的-----接口及impl对象，将它放到handlerMap中
	 * @param applicationContext
	 * @throws BeansException
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)){
			for (Object serviceBean: serviceBeanMap.values()){
				//从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, serviceBean);
			}
		}
	}

	/**
	 * 在此启动netty服务，绑定handle流水线：
	 * 1、接收请求数据进行反序列化得到request对象
	 *
	 * 2、根据request中的参数，让RpcHandler从handlerMap中找到对应的业务impl，调用指定方法，获取返回结果
	 *
	 * 3、将业务调用结果封装到response并序列化后发往客户端
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline()
										  .addLast(new RpcDecoder(RpcRequest.class)) // 注册解码 IN-1
										  .addLast(new RpcEncoder(RpcResponse.class)) //注册编码 OUT
										  .addLast(new RpcHandler(handlerMap)); //注册RpcHandler IN-2
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);

			ChannelFuture future = bootstrap.bind(host, port).sync();
			logger.debug("server started on port {}", port);

			if (serviceRegistry != null){
				serviceRegistry.register(serverAddress);
			}

			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
