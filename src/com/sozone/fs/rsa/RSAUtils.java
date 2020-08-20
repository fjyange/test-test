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

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.fs.common.util.HttpClientUtils;

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
public class RSAUtils
{
	/**
	 * 加密算法RSA
	 */
	private static final String KEY_ALGORITHM = "RSA";
	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";
	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	/**
	 * 常量0
	 */
	private static final int ZERO = 0;
	/**
	 * RSA最大加密明文最大大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;
	/**
	 * RSA最大解密密文最大大小 当密钥位数为1024时,解密密文最大是 128 当密钥位数为2048时需要改为 256
	 * 不然会报错（Decryption error）
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;
	/**
	 * 默认key大小
	 */
	private static final int DEFAULT_KEY_SIZE = 1024;

	/**
	 * 生成密钥对(公钥和私钥)
	 *
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception
	{
		return initKey(DEFAULT_KEY_SIZE);
	}

	/**
	 * 生成密钥对(公钥和私钥)
	 *
	 * @return
	 * @throws Exception
	 */
	public static Record<String, Object> initKey(int keySize) throws Exception
	{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(keySize);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Record<String, Object> keyMap = new RecordImpl<String, Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 公钥加密
	 *
	 * @param data
	 *            源数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception
	{
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return encrypt(data, KeyFactory.getInstance(KEY_ALGORITHM), keyFactory.generatePublic(x509KeySpec));
	}

	/**
	 * 私钥加密
	 *
	 * @param data
	 *            源数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception
	{
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		return encrypt(data, keyFactory, privateK);
	}

	/**
	 * 私钥解密
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception
	{
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return decrypt(encryptedData, keyFactory, keyFactory.generatePrivate(pkcs8KeySpec));
	}

	/**
	 * 公钥解密
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception
	{
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		return decrypt(encryptedData, keyFactory, publicK);

	}

	/**
	 * 获取私钥
	 *
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception
	{
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 获取公钥
	 *
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception
	{
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 解密公共方法
	 */
	private static byte[] decrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception
	{

		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, key);
		return encryptAndDecrypt(data, cipher, MAX_DECRYPT_BLOCK);
	}

	/**
	 * 加密公共方法
	 */
	private static byte[] encrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception
	{
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return encryptAndDecrypt(data, cipher, MAX_ENCRYPT_BLOCK);
	}

	/**
	 * 加密解密分段处理公共方法
	 */
	private static byte[] encryptAndDecrypt(byte[] data, Cipher cipher, int maxSize) throws Exception
	{
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = ZERO;
		byte[] cache;
		int i = ZERO;
		// 对数据分段加密
		while (inputLen - offSet > ZERO)
		{
			if (inputLen - offSet > maxSize)
			{
				cache = cipher.doFinal(data, offSet, maxSize);
			}
			else
			{
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, ZERO, cache.length);
			i++;
			offSet = i * maxSize;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	public static void main(String[] args) throws Exception
	{
		// 公钥
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClyQKpo8a6zk4agPs/wL0qOO4bFyfohMUIKAlfqfqMauDp4lcSQ3NF7NEbvRQnkYIZFsd0jydM2c2OkpmACVgD8chdrRzEfzon5SYu640Qk6su5Ed+LlUp5zYobTkXgpXHqEzSGZZNo+1MXm8hMcLbNRZmBZb4HidB8OI9WAvopQIDAQAB";
		// 私钥
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKXJAqmjxrrOThqA+z/AvSo47hsXJ+iExQgoCV+p+oxq4OniVxJDc0Xs0Ru9FCeRghkWx3SPJ0zZzY6SmYAJWAPxyF2tHMR/OiflJi7rjRCTqy7kR34uVSnnNihtOReClceoTNIZlk2j7UxebyExwts1FmYFlvgeJ0Hw4j1YC+ilAgMBAAECgYAm/zmZHeVJW+4TXfO782KL5AheZvwEPfb7DC/oSNue3CU73voMWcFr2WD23Ws4Q4oOzMTuLh5YfYNU3jctXwVMxrsOZRUXEG6uZbdL3WfoIerfDl5rD1hOU5YW48TukevZNe/qLoNvBCSRvmqbhLZ4iUQ02r42pwk+0e+7d3U6AQJBAOXP8w6ydTHM+9TVLCc9pBh/0qC9VyqpwakQvNyEhrM+9ZSf4e2RW2Jmd0oi8rgkcPQ8dsNcdebPxQ3mP0MpS3ECQQC4rUWNOEopmDwQwNfpT/77t6H9K/BRVJAIZt+k97+XgJ20Z3TIBgzVMfoYNfawldNXW1xYXQa2WNoYA9+sME51AkEA0RE2OIenUFAARiZMjcJpF5SppGu78ecPdGPyvNafyE+dkMFHAx46ubEoErzqfRVB4R9kl+P0qq8XwMZXhRz7MQJAQRACjfND5Y2Vs81NBAzD54jVkC1XuD+TkvIzXppOLKEKbpF4SjQfd0jpNHhmleXjFEbCrPrxL3L0Ozu6JJ7MNQJARDPUrI5JuTeshNYFzDOPlVBr85//bYjk10NjJcrKvsSEH8upy0MPgxbC4dAWUSJsFZKe1/bf2i+eI1QQBspmtA==";
		System.out.println("公钥 长度: " + publicKey.length() + " 值: " + publicKey);
		System.out.println("私钥 长度: " + privateKey.length() + " 值: " + privateKey);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("money", "6000");
		map.put("orderno", String.valueOf(System.currentTimeMillis()));
		map.put("paytype", "01");
		map.put("notifyurl", "www.baidu.com");

		String str = JSONObject.toJSONString(map);
		byte[] bytes1 = RSAUtils.encryptByPublicKey(str.getBytes(), publicKey);
		byte[] bytes2 = RSAUtils.decryptByPrivateKey(Base64.decodeBase64("Ka965RkhQFehah+2XG7kWYAGwVXArku6qfj68kRrmgpFsaE9oF0EtIilfAbiwLhkjqAF8nyL2HNtOHm2/p9ihAQLMAe1IIJch3kQnm3rgxCAqnDXWRde47sGnaboYSCJ58WgaN+4+UVEIkkEi9kD/xai+DtBeh/nQWscSkmXm0w="),
				privateKey);

		System.out.println();
		System.out.println("****** 公钥加密 私钥解密 start ******");
		System.out.println("加密前长度 ：" + str.toString().length());
		System.out.println("加密后 ：" + Base64.encodeBase64String(bytes1));
		System.out.println("解密后 ：" + new String(bytes2));
		System.out.println("解密后长度 ：" + new String(bytes2).length());
		System.out.println("****** 公钥加密 私钥解密 end ******");

		System.out.println();
		byte[] bytes3 = RSAUtils.encryptByPrivateKey(str.getBytes(), privateKey);
		byte[] bytes4 = RSAUtils.decryptByPublicKey(Base64.decodeBase64(Base64.encodeBase64String(bytes3)), publicKey);

		System.out.println("****** 私钥加密 公钥解密 start ******");
		System.out.println("加密前长度 ：" + str.toString().length());
		System.out.println("加密后 ：" + Base64.encodeBase64String(bytes3));
		System.out.println("解密后 ：" + new String(bytes4));
		System.out.println("解密后长度 ：" + new String(bytes4).length());
		System.out.println("****** 私钥加密 公钥解密 end ******");

		byte[] dataByte = RSAUtils.encryptByPrivateKey(JSONObject.toJSONString(map).getBytes(),
				"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKXJAqmjxrrOThqA+z/AvSo47hsXJ+iExQgoCV+p+oxq4OniVxJDc0Xs0Ru9FCeRghkWx3SPJ0zZzY6SmYAJWAPxyF2tHMR/OiflJi7rjRCTqy7kR34uVSnnNihtOReClceoTNIZlk2j7UxebyExwts1FmYFlvgeJ0Hw4j1YC+ilAgMBAAECgYAm/zmZHeVJW+4TXfO782KL5AheZvwEPfb7DC/oSNue3CU73voMWcFr2WD23Ws4Q4oOzMTuLh5YfYNU3jctXwVMxrsOZRUXEG6uZbdL3WfoIerfDl5rD1hOU5YW48TukevZNe/qLoNvBCSRvmqbhLZ4iUQ02r42pwk+0e+7d3U6AQJBAOXP8w6ydTHM+9TVLCc9pBh/0qC9VyqpwakQvNyEhrM+9ZSf4e2RW2Jmd0oi8rgkcPQ8dsNcdebPxQ3mP0MpS3ECQQC4rUWNOEopmDwQwNfpT/77t6H9K/BRVJAIZt+k97+XgJ20Z3TIBgzVMfoYNfawldNXW1xYXQa2WNoYA9+sME51AkEA0RE2OIenUFAARiZMjcJpF5SppGu78ecPdGPyvNafyE+dkMFHAx46ubEoErzqfRVB4R9kl+P0qq8XwMZXhRz7MQJAQRACjfND5Y2Vs81NBAzD54jVkC1XuD+TkvIzXppOLKEKbpF4SjQfd0jpNHhmleXjFEbCrPrxL3L0Ozu6JJ7MNQJARDPUrI5JuTeshNYFzDOPlVBr85//bYjk10NjJcrKvsSEH8upy0MPgxbC4dAWUSJsFZKe1/bf2i+eI1QQBspmtA==");
		String data = Base64.encodeBase64String(dataByte);
		System.out.println(data);
		System.out.println(new String(bytes4));
		Map<String, Object> sendMap = new HashMap<String, Object>();
		sendMap.put("appid", "4a1cccf003a44a25858f67fad90fc3cb");
		sendMap.put("data", data);
		System.out.println(HttpClientUtils.sendJsonPostRequest("http://120.25.250.167/authorize/third/sdr",
				JSONObject.toJSONString(sendMap), "utf-8"));

	}
}
