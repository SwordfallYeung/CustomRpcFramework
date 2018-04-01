package cn.itcast.rpc.common;

/**
 * @author y15079
 * @create 2018-04-01 1:22
 * @desc
 * 封装RPC请求
 * 封装发送的object的反射属性
 **/
public class RpcRequest {

	private String requestId;
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
