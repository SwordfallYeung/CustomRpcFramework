package cn.itcast.rpc.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author y15079
 * @create 2018-04-01 13:25
 * @desc
 *
 * RPC 请求注解（标注在服务实现类上）
 **/
@Target({ElementType.TYPE}) //注解用在接口上
@Retention(RetentionPolicy.RUNTIME) ////VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Component
public @interface RpcService {
	Class<?> value();
}
