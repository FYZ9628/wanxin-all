package cn.itcast.wanxindepository.service;

//import com.ctrip.framework.apollo.Config;
//import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * <P>
 * 本类用于获取配置文件中的配置, 封装成service方便调用
 * </p>
 *
 * @author zhupeiyuan@itcast.cn
 * @since 2019/5/22
 */
@Service
//@EnableApolloConfig
@RefreshScope //实时刷新nacos配置信息
public class ConfigService {

//	@ApolloConfig
//	private Config config;

	@Autowired
	private Environment env;

	/**
	 * 获取银行存管系统公钥
	 * @return
	 */
	public String getDepositoryPublicKey() {
		return env.getProperty("depository.publicKey");
//		return config.getProperty("depository.publicKey", null);
	}

	/**
	 * 获取银行存管系统私钥
	 * @return
	 */
	public String getDepositoryPrivateKey() {
		return env.getProperty("depository.privateKey");
//		return config.getProperty("depository.privateKey", null);
	}

	/**
	 * 获取P2P平台公钥
	 * @return
	 */
	public String getP2PPublicKey(String platformNo) {
		return env.getProperty("client-info.clients[" + platformNo +  "].publicKey");
//		return config.getProperty("client-info.clients[" + platformNo +  "].publicKey", null);
	}

	/**
	 * 获取P2P平台公钥
	 * @return
	 */
	public String getP2PNotifyUrl(String platformNo) {
		return env.getProperty("client-info.clients[" + platformNo +  "].notifyUrl");
//		return config.getProperty("client-info.clients[" + platformNo +  "].notifyUrl", null);
	}

}
