package com.example.encrypt;

/**
 * 加密解密服务接口
 * <p>
 * 提供常用的加密、解密、编码、解码功能，包括：
 * <ul>
 *   <li>AES 对称加密/解密</li>
 *   <li>MD5 哈希加密（不可逆）</li>
 *   <li>SHA256 哈希加密（不可逆）</li>
 *   <li>Base64 编码/解码</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入加密服务
 * @Autowired
 * private EncryptService encryptService;
 * 
 * // AES 加密/解密
 * String encrypted = encryptService.encryptAES("原始数据", "密钥");
 * String decrypted = encryptService.decryptAES(encrypted, "密钥");
 * 
 * // MD5 加密（不可逆）
 * String md5 = encryptService.encryptMD5("原始数据");
 * 
 * // SHA256 加密（不可逆）
 * String sha256 = encryptService.encryptSHA256("原始数据");
 * 
 * // Base64 编码/解码
 * String encoded = encryptService.encodeBase64("原始数据");
 * String decoded = encryptService.decodeBase64(encoded);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EncryptService {
    
    /**
     * AES 对称加密
     * <p>
     * 使用 AES 算法对数据进行加密，返回 Base64 编码的加密结果
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>密钥长度必须是 16、24 或 32 字节（对应 AES-128、AES-192、AES-256）</li>
     *   <li>加密结果使用 Base64 编码，便于传输和存储</li>
     *   <li>相同的明文和密钥会产生不同的密文（由于随机 IV）</li>
     * </ul>
     * </p>
     * 
     * @param data 待加密的原始数据
     * @param key 加密密钥，长度必须是 16、24 或 32 字节
     * @return Base64 编码的加密结果
     * @throws IllegalArgumentException 如果密钥长度不正确
     */
    String encryptAES(String data, String key);

    /**
     * AES 对称解密
     * <p>
     * 使用 AES 算法对 Base64 编码的加密数据进行解密
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>密钥必须与加密时使用的密钥相同</li>
     *   <li>加密数据必须是 Base64 编码格式</li>
     * </ul>
     * </p>
     * 
     * @param encryptedData Base64 编码的加密数据
     * @param key 解密密钥，必须与加密时使用的密钥相同
     * @return 解密后的原始数据
     * @throws IllegalArgumentException 如果密钥长度不正确或加密数据格式错误
     */
    String decryptAES(String encryptedData, String key);

    /**
     * MD5 哈希加密
     * <p>
     * 使用 MD5 算法对数据进行哈希加密，返回 32 位十六进制字符串
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>MD5 是单向哈希函数，不可逆，无法解密</li>
     *   <li>常用于密码存储、数据完整性校验等场景</li>
     *   <li>MD5 已被认为不够安全，建议使用 SHA256 或更安全的算法</li>
     * </ul>
     * </p>
     * 
     * @param data 待加密的原始数据
     * @return 32 位十六进制 MD5 哈希值
     */
    String encryptMD5(String data);

    /**
     * SHA256 哈希加密
     * <p>
     * 使用 SHA256 算法对数据进行哈希加密，返回 64 位十六进制字符串
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>SHA256 是单向哈希函数，不可逆，无法解密</li>
     *   <li>比 MD5 更安全，推荐用于密码存储、数据完整性校验等场景</li>
     *   <li>常用于区块链、数字签名等领域</li>
     * </ul>
     * </p>
     * 
     * @param data 待加密的原始数据
     * @return 64 位十六进制 SHA256 哈希值
     */
    String encryptSHA256(String data);

    /**
     * Base64 编码
     * <p>
     * 将字符串编码为 Base64 格式，常用于数据传输和存储
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>Base64 不是加密算法，只是编码方式，可以轻松解码</li>
     *   <li>编码后的数据大小约为原数据的 4/3</li>
     *   <li>常用于在文本协议中传输二进制数据</li>
     * </ul>
     * </p>
     * 
     * @param data 待编码的原始数据
     * @return Base64 编码后的字符串
     */
    String encodeBase64(String data);

    /**
     * Base64 解码
     * <p>
     * 将 Base64 编码的字符串解码为原始数据
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>只能解码 Base64 格式的字符串</li>
     *   <li>如果输入不是有效的 Base64 字符串，会抛出异常</li>
     * </ul>
     * </p>
     * 
     * @param encodedData Base64 编码的字符串
     * @return 解码后的原始数据
     * @throws IllegalArgumentException 如果输入不是有效的 Base64 字符串
     */
    String decodeBase64(String encodedData);
}

