package cn.itcast.rpc.common;

/**
 * @author y15079
 * @create 2018-04-01 1:24
 * @desc
 * 封装RPC响应
 * 封装相应object
 **/
public class RpcResponse {

	private String requestId;
	private Throwable error;
	private Object result;

	public boolean isError() {
		return error != null;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
