package cn.itcast.rpc.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CountDownLatch;


/**
 * @author y15079
 * @create 2018-04-01 2:07
 * @desc
 *
 * 服务注册，ZK在该架构中扮演了“服务注册表”的角色，用于注册所有服务器的地址与端口，
 * 并对客户端提供服务发现的功能
 *
 * 与zookeeper的分布式应用系统服务器上下线动态感知demo类似
 **/
public class ServiceRegistry {

	private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private String registryAddress;

	public ServiceRegistry(String registryAddress) {
		//zookeeper的地址
		this.registryAddress = registryAddress;
	}

	/**
	 * 创建zookeeper连接
	 * @param data
	 */
	public void register(String data){
		if (data != null){
			ZooKeeper zk = connectServer();
			if (zk != null){
				createNode(zk, data);
			}
		}
	}

	/**
	 * 创建zookeeper连接，监听
	 * @return
	 */
	private ZooKeeper connectServer(){
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
				public void process(WatchedEvent watchedEvent) {
					if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			logger.error("", e);
		}
		return zk;
	}

	/**
	 * 创建节点
	 * @param zk
	 * @param data
	 */
	private void createNode(ZooKeeper zk, String data){
		try {
			byte[] bytes = data.getBytes();
			//创建父节点
			if (zk.exists(Constant.ZK_REGISTRY_PATH, null) == null){
				zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}

			//创建子节点
			String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("create zookeeper node ({} => {})", path, data);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
