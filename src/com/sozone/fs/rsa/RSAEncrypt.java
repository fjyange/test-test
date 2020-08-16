/**
 * 包名：com.sozone.fs.rsa
 * 文件名：RSAEncrypt.java<br/>
 * 创建时间：2020年8月16日 下午9:03:41<br/>
 * 创建者：86159<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.rsa;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;

/**
 * TODO 一句话描述类的主要作用<br/>
 * <p>
 * TODO 该类的详细描述<br/>
 * </p>
 * Time：2020年8月16日 下午9:03:41<br/>
 * 
 * @author 86159
 * @version 1.0.0
 * @since 1.0.0
 */
public class RSAEncrypt
{
	public static void main(String[] args) throws Exception
	{
		// 生成公钥和私钥
		Record<String, Object> record = genKeyPair();
		// 加密字符串
		System.out.println("随机生成的公钥为:" + record.get("RSA_APP"));
		System.out.println("随机生成的私钥为:" + record.get("RSA_KEY"));
		String message = "{\"orderno\":\"1597586724117\",\"money\":\"6000\",\"notifyurl\":\"www.baidu.com\",\"paytype\":\"01\"}";
		String messageEn = encryptPri(message, record.getString("RSA_KEY"));
		System.out.println(message + "\t加密后的字符串为:" + messageEn);
		String messageDe = decryptPub(
				"bzS6miNyJqiEKUHH+CCzjDw4REUyPdFbpYMtnMgm6QAb1FafMf8yRtG6iqz15XXCUJtwpYc/tMazS10sG9XzJaruApHAUF2C/xfwejL/3Q48hP+koFrXZELfTw2wwPn3oEpXXJUMfUmO02K0EcRZJL8QIj0rBWcjIPiom39GDso=",
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCk7XeO8bbFB5moyc8PguJuXqCCUWCZkTowwz4nkgYqf5jCpwzpyNvxARiR9NpkSn6Fd3SOkP50gr9Wd+HM4bZiVFnkGw33pb2GjpIr5SsrB0y1hj3+rlCoJuKYdXChJqZChklAIkDDZ7GimyN2k8k9GctYI3wZjFoz53qSAglM0wIDAQAB");
		System.out.println("还原后的字符串为:" + messageDe);
	}

	/**
	 * 随机生成密钥对
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public static Record<String, Object> genKeyPair() throws NoSuchAlgorithmException
	{
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
		// 得到私钥字符串
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
		// 将公钥和私钥保存到Map
		Record<String, Object> key = new RecordImpl<String, Object>();
		key.setColumn("RSA_APP", publicKeyString);
		key.setColumn("RSA_KEY", privateKeyString);
		return key;
	}

	/**
	 * RSA公钥加密
	 * 
	 * @param str
	 *            加密字符串
	 * @param publicKey
	 *            公钥
	 * @return 密文
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static String encryptPub(String str, String publicKey) throws Exception
	{
		// base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		// RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	/**
	 * RSA私钥加密
	 * 
	 * @param str
	 *            加密字符串
	 * @param publicKey
	 *            公钥
	 * @return 密文
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static String encryptPri(String str, String key) throws Exception
	{
		byte[] decoded = Base64.decodeBase64(key);
		RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	/**
	 * RSA私钥解密
	 * 
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥
	 * @return 铭文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decryptPri(String str, String key) throws Exception
	{
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		// base64编码的私钥
		byte[] decoded = Base64.decodeBase64(key);
		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		// RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}

	/**
	 * RSA公钥解密
	 * 
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥
	 * @return 铭文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decryptPub(String str, String key) throws Exception
	{
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		// base64编码的私钥
		byte[] decoded = Base64.decodeBase64(key);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		// RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}
}
