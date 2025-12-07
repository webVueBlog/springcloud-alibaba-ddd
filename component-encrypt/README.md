# 加密组件 (Component Encrypt)

## 概述

Component Encrypt 是一个加密解密组件，提供常用的加密、解密、编码、解码功能。基于 Hutool 工具库实现，支持 AES 对称加密、MD5/SHA256 哈希加密、Base64 编码等功能。

## 功能特性

### 核心功能

- **AES 对称加密/解密**：支持 AES-128、AES-192、AES-256
- **MD5 哈希加密**：单向哈希函数，不可逆
- **SHA256 哈希加密**：比 MD5 更安全的单向哈希函数
- **Base64 编码/解码**：用于数据传输和存储

### 技术特性

- 基于 Hutool 工具库实现
- 完善的异常处理和日志记录
- 参数验证和错误提示
- 线程安全，支持并发访问

## 技术栈

- Spring Boot 2.7.18
- Hutool 5.8.20
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用加密组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-encrypt</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用组件

确保 Spring Boot 能够扫描到加密组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.encrypt"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 使用加密服务

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final EncryptService encryptService;
    
    public void saveUser(UserDTO userDTO) {
        // 加密密码
        String encryptedPassword = encryptService.encryptMD5(userDTO.getPassword() + salt);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }
}
```

## 使用指南

### AES 对称加密/解密

#### 基本使用

```java
@Service
@RequiredArgsConstructor
public class DataService {
    
    private final EncryptService encryptService;
    
    /**
     * 加密敏感数据
     */
    public String encryptSensitiveData(String data) {
        // 密钥长度必须是 16、24 或 32 字节
        String key = "1234567890123456";  // 16 字节（AES-128）
        return encryptService.encryptAES(data, key);
    }
    
    /**
     * 解密敏感数据
     */
    public String decryptSensitiveData(String encryptedData) {
        String key = "1234567890123456";
        return encryptService.decryptAES(encryptedData, key);
    }
}
```

#### 密钥长度说明

AES 支持三种密钥长度：
- **16 字节**：AES-128（推荐用于一般场景）
- **24 字节**：AES-192
- **32 字节**：AES-256（推荐用于高安全场景）

```java
// AES-128（16 字节）
String key128 = "1234567890123456";

// AES-192（24 字节）
String key192 = "123456789012345678901234";

// AES-256（32 字节）
String key256 = "12345678901234567890123456789012";
```

#### 完整示例

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionService {
    
    private final EncryptService encryptService;
    
    /** AES 加密密钥（建议从配置文件读取） */
    private static final String AES_KEY = "my-secret-key-1234567890123456";  // 32 字节
    
    /**
     * 加密用户敏感信息
     */
    public String encryptUserInfo(String userInfo) {
        try {
            String encrypted = encryptService.encryptAES(userInfo, AES_KEY);
            log.info("用户信息加密成功");
            return encrypted;
        } catch (Exception e) {
            log.error("用户信息加密失败", e);
            throw new BusinessException("加密失败");
        }
    }
    
    /**
     * 解密用户敏感信息
     */
    public String decryptUserInfo(String encryptedUserInfo) {
        try {
            String decrypted = encryptService.decryptAES(encryptedUserInfo, AES_KEY);
            log.info("用户信息解密成功");
            return decrypted;
        } catch (Exception e) {
            log.error("用户信息解密失败", e);
            throw new BusinessException("解密失败");
        }
    }
}
```

### MD5 哈希加密

#### 基本使用

```java
@Service
@RequiredArgsConstructor
public class PasswordService {
    
    private final EncryptService encryptService;
    
    /**
     * 加密密码（使用 MD5 + 盐值）
     */
    public String encryptPassword(String password, String salt) {
        // MD5(密码 + 盐值)
        return encryptService.encryptMD5(password + salt);
    }
    
    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword, String storedPassword, String salt) {
        String encrypted = encryptService.encryptMD5(inputPassword + salt);
        return encrypted.equals(storedPassword);
    }
}
```

#### 注意事项

- MD5 是单向哈希函数，不可逆，无法解密
- 常用于密码存储、数据完整性校验等场景
- MD5 已被认为不够安全，建议使用 SHA256 或更安全的算法

### SHA256 哈希加密

#### 基本使用

```java
@Service
@RequiredArgsConstructor
public class PasswordService {
    
    private final EncryptService encryptService;
    
    /**
     * 加密密码（使用 SHA256 + 盐值，推荐）
     */
    public String encryptPassword(String password, String salt) {
        // SHA256(密码 + 盐值)
        return encryptService.encryptSHA256(password + salt);
    }
    
    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword, String storedPassword, String salt) {
        String encrypted = encryptService.encryptSHA256(inputPassword + salt);
        return encrypted.equals(storedPassword);
    }
}
```

#### 完整示例

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final EncryptService encryptService;
    
    /**
     * 创建用户（加密密码）
     */
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        
        // 生成盐值
        String salt = UUID.randomUUID().toString().replace("-", "");
        user.setSalt(salt);
        
        // 使用 SHA256 加密密码（推荐）
        String encryptedPassword = encryptService.encryptSHA256(userDTO.getPassword() + salt);
        user.setPassword(encryptedPassword);
        
        return userRepository.save(user);
    }
    
    /**
     * 验证用户密码
     */
    public boolean verifyPassword(Long userId, String inputPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        
        String encrypted = encryptService.encryptSHA256(inputPassword + user.getSalt());
        return encrypted.equals(user.getPassword());
    }
}
```

### Base64 编码/解码

#### 基本使用

```java
@Service
@RequiredArgsConstructor
public class DataService {
    
    private final EncryptService encryptService;
    
    /**
     * Base64 编码
     */
    public String encodeData(String data) {
        return encryptService.encodeBase64(data);
    }
    
    /**
     * Base64 解码
     */
    public String decodeData(String encodedData) {
        return encryptService.decodeBase64(encodedData);
    }
}
```

#### 使用场景

- 在文本协议中传输二进制数据
- 在 URL 中传递数据
- 存储二进制数据到文本字段

#### 注意事项

- Base64 不是加密算法，只是编码方式，可以轻松解码
- 编码后的数据大小约为原数据的 4/3
- 不要用于敏感数据的加密，仅用于编码

## 完整示例

### 示例1：密码加密和验证

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final EncryptService encryptService;
    
    /**
     * 用户注册（加密密码）
     */
    public User registerUser(RegisterDTO registerDTO) {
        // 生成盐值
        String salt = generateSalt();
        
        // 使用 SHA256 加密密码（推荐）
        String encryptedPassword = encryptService.encryptSHA256(
            registerDTO.getPassword() + salt);
        
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录（验证密码）
     */
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        // 使用相同的加密方式验证密码
        String encrypted = encryptService.encryptSHA256(password + user.getSalt());
        return encrypted.equals(user.getPassword());
    }
    
    /**
     * 生成盐值
     */
    private String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
```

### 示例2：敏感数据加密存储

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SensitiveDataService {
    
    private final DataRepository dataRepository;
    private final EncryptService encryptService;
    
    /** AES 加密密钥（建议从配置文件读取） */
    @Value("${encrypt.aes.key:default-key-1234567890123456}")
    private String aesKey;
    
    /**
     * 保存敏感数据（加密存储）
     */
    public void saveSensitiveData(Long userId, String sensitiveData) {
        // 加密数据
        String encrypted = encryptService.encryptAES(sensitiveData, aesKey);
        
        // 存储加密后的数据
        SensitiveData data = new SensitiveData();
        data.setUserId(userId);
        data.setEncryptedData(encrypted);
        dataRepository.save(data);
    }
    
    /**
     * 获取敏感数据（解密）
     */
    public String getSensitiveData(Long userId) {
        SensitiveData data = dataRepository.findByUserId(userId);
        if (data == null) {
            return null;
        }
        
        // 解密数据
        return encryptService.decryptAES(data.getEncryptedData(), aesKey);
    }
}
```

### 示例3：数据完整性校验

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DataIntegrityService {
    
    private final EncryptService encryptService;
    
    /**
     * 计算数据哈希值（用于完整性校验）
     */
    public String calculateHash(String data) {
        // 使用 SHA256 计算哈希值
        return encryptService.encryptSHA256(data);
    }
    
    /**
     * 验证数据完整性
     */
    public boolean verifyIntegrity(String data, String expectedHash) {
        String actualHash = encryptService.encryptSHA256(data);
        return actualHash.equals(expectedHash);
    }
    
    /**
     * 传输数据（包含哈希值用于校验）
     */
    public DataWithHash transferData(String data) {
        String hash = encryptService.encryptSHA256(data);
        
        DataWithHash result = new DataWithHash();
        result.setData(data);
        result.setHash(hash);
        return result;
    }
}
```

## 最佳实践

### 1. 密钥管理

**不要硬编码密钥**，应该从配置文件或密钥管理服务获取：

```java
// ✅ 正确：从配置文件读取
@Value("${encrypt.aes.key}")
private String aesKey;

// ❌ 错误：硬编码密钥
private String aesKey = "my-secret-key";
```

### 2. 密码加密

**使用 SHA256 + 盐值**，不要使用 MD5：

```java
// ✅ 推荐：SHA256 + 盐值
String salt = generateSalt();
String encrypted = encryptService.encryptSHA256(password + salt);

// ⚠️ 不推荐：MD5（不够安全）
String encrypted = encryptService.encryptMD5(password);

// ❌ 错误：直接存储明文密码
user.setPassword(password);
```

### 3. AES 密钥长度

**根据安全需求选择合适的密钥长度**：

```java
// 一般场景：AES-128（16 字节）
String key = "1234567890123456";

// 高安全场景：AES-256（32 字节）
String key = "12345678901234567890123456789012";
```

### 4. 异常处理

**妥善处理加密解密异常**：

```java
// ✅ 正确：捕获异常并处理
try {
    String encrypted = encryptService.encryptAES(data, key);
    return encrypted;
} catch (Exception e) {
    log.error("加密失败", e);
    throw new BusinessException("加密失败");
}

// ❌ 错误：不处理异常
String encrypted = encryptService.encryptAES(data, key);  // 可能抛出异常
```

### 5. Base64 使用场景

**Base64 仅用于编码，不用于加密**：

```java
// ✅ 正确：用于编码二进制数据
String encoded = encryptService.encodeBase64(binaryData);

// ❌ 错误：不要用于加密敏感数据
String encrypted = encryptService.encodeBase64(sensitiveData);  // 不安全
```

### 6. 哈希值比较

**使用安全的比较方式**：

```java
// ✅ 正确：使用 equals 比较（对于固定长度的哈希值）
String hash1 = encryptService.encryptSHA256(data1);
String hash2 = encryptService.encryptSHA256(data2);
boolean match = hash1.equals(hash2);

// ⚠️ 注意：对于可变长度数据，考虑使用 MessageDigest.isEqual() 防止时序攻击
```

## 安全建议

### 1. 密钥管理

- 密钥应该存储在安全的密钥管理系统中
- 不要将密钥提交到代码仓库
- 定期轮换密钥
- 使用环境变量或配置中心管理密钥

### 2. 密码存储

- 使用 SHA256 或更安全的算法（如 bcrypt、Argon2）
- 必须使用盐值（salt）
- 不要使用 MD5 存储密码
- 考虑使用专业的密码加密库（如 Spring Security 的 BCryptPasswordEncoder）

### 3. 数据传输

- 使用 HTTPS 传输敏感数据
- 不要在 URL 中传递敏感数据
- 使用 AES 加密敏感数据后再传输

### 4. 数据存储

- 敏感数据应该加密存储
- 使用强密钥（AES-256）
- 定期备份和恢复测试

## 常见问题

### 1. AES 加密失败：密钥长度不正确？

**答**：AES 密钥长度必须是 16、24 或 32 字节。检查密钥长度：

```java
String key = "your-key";
int length = key.getBytes().length;
// length 必须是 16、24 或 32
```

### 2. MD5 和 SHA256 的区别？

**答**：
- **MD5**：32 位十六进制，已被认为不够安全
- **SHA256**：64 位十六进制，更安全，推荐使用

### 3. Base64 是加密算法吗？

**答**：不是。Base64 只是编码方式，可以轻松解码，不要用于加密敏感数据。

### 4. 如何选择合适的加密方式？

**答**：
- **需要可逆加密**：使用 AES
- **密码存储**：使用 SHA256 + 盐值（或更安全的 bcrypt）
- **数据完整性校验**：使用 SHA256
- **编码二进制数据**：使用 Base64

### 5. 加密后的数据长度是多少？

**答**：
- **AES 加密**：Base64 编码后约为原数据的 1.33 倍
- **MD5**：固定 32 位十六进制字符串
- **SHA256**：固定 64 位十六进制字符串
- **Base64 编码**：约为原数据的 1.33 倍

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供 AES 对称加密/解密
  - 提供 MD5/SHA256 哈希加密
  - 提供 Base64 编码/解码

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

