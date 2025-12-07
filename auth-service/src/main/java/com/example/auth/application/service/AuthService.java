package com.example.auth.application.service;

import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.LoginResultDTO;
import com.example.auth.application.dto.RegisterDTO;
import com.example.auth.domain.model.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.domain.service.AuthDomainService;
import com.example.cache.CacheService;
import com.example.common.exception.BusinessException;
import com.example.encrypt.EncryptService;
import com.example.ratelimit.RateLimitService;
import com.example.sso.SSOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 认证应用服务
 * <p>
 * 负责处理用户认证相关的业务逻辑，包括：
 * <ul>
 *   <li>多种登录方式的处理</li>
 *   <li>验证码的生成和验证</li>
 *   <li>Token 的生成和管理</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /** 用户仓储接口 */
    private final UserRepository userRepository;
    
    /** 认证领域服务 */
    private final AuthDomainService authDomainService;
    
    /** 单点登录服务 */
    private final SSOService ssoService;
    
    /** 缓存服务（可选，用于验证码存储） */
    @Autowired(required = false)
    private CacheService cacheService;
    
    /** 加密服务 */
    private final EncryptService encryptService;
    
    /** 限流服务（可选，用于验证码发送限流） */
    @Autowired(required = false)
    private RateLimitService rateLimitService;

    /**
     * 账号密码登录
     * <p>
     * 验证用户名和密码，验证成功后生成并返回 JWT Token
     * </p>
     * 
     * @param loginDTO 登录请求参数，必须包含 username 和 password
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当用户名不存在或密码错误时抛出
     */
    @Transactional// 开启事务
    public LoginResultDTO loginByUsername(LoginDTO loginDTO) {
        try {
            log.info("开始登录: username={}", loginDTO.getUsername());
            
            User user = userRepository.findByUsername(loginDTO.getUsername());
            if (user == null) {
                log.warn("用户不存在: username={}", loginDTO.getUsername());
                throw new BusinessException("用户名或密码错误");
            }

            log.debug("找到用户: userId={}, username={}", user.getId(), user.getUsername());

            if (!authDomainService.validatePassword(user, loginDTO.getPassword())) {
                log.warn("密码验证失败: username={}", loginDTO.getUsername());
                throw new BusinessException("用户名或密码错误");
            }

            log.info("密码验证成功，开始生成Token: userId={}", user.getId());
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录异常: username={}", loginDTO.getUsername(), e);
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 手机号密码登录
     * <p>
     * 验证手机号和密码，验证成功后生成并返回 JWT Token
     * </p>
     * 
     * @param loginDTO 登录请求参数，必须包含 phone 和 password
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当手机号不存在或密码错误时抛出
     */
    @Transactional
    public LoginResultDTO loginByPhone(LoginDTO loginDTO) {
        User user = userRepository.findByPhone(loginDTO.getPhone());
        if (user == null) {
            throw new BusinessException("手机号或密码错误");
        }

        if (!authDomainService.validatePassword(user, loginDTO.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }

        return generateToken(user);
    }

    /**
     * 手机号验证码登录
     * <p>
     * 验证手机号和验证码，如果用户不存在会自动注册新用户（默认密码：123456）
     * 验证成功后生成并返回 JWT Token
     * </p>
     * 
     * @param phone 手机号，11位数字
     * @param code 验证码，6位数字，有效期5分钟
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当验证码错误或已过期时抛出
     */
    @Transactional
    public LoginResultDTO loginByPhoneCode(String phone, String code) {
        // 验证验证码（如果缓存服务可用）
        if (cacheService != null) {
            String cacheKey = "verify_code:phone:" + phone;
            String cachedCode = cacheService.get(cacheKey, String.class);
            if (cachedCode == null || !cachedCode.equals(code)) {
                throw new BusinessException("验证码错误或已过期");
            }
            // 删除验证码
            cacheService.delete(cacheKey);
        } else {
            log.warn("缓存服务未配置，跳过验证码验证");
        }

        User user = userRepository.findByPhone(phone);
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            String salt = generateSalt();
            user.setSalt(salt);
            // 默认密码
            user.setPassword(authDomainService.encryptPassword("123456", salt));
            user = userRepository.save(user);
        }

        return generateToken(user);
    }

    /**
     * 发送手机验证码
     * <p>
     * 生成6位数字验证码并缓存（有效期5分钟），然后发送到指定手机号
     * 包含限流检查，防止频繁发送验证码
     * </p>
     * <p>
     * 限流规则：
     * <ul>
     *   <li>每个手机号每分钟最多发送1次</li>
     *   <li>每个手机号每小时最多发送5次</li>
     * </ul>
     * </p>
     * 
     * @param phone 手机号，11位数字
     * @throws BusinessException 当手机号格式错误、限流或发送失败时抛出
     */
    public void sendPhoneVerifyCode(String phone) {
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessException("手机号不能为空");
        }
        
        // 验证手机号格式（11位数字）
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式错误，请输入11位有效手机号");
        }
        
        try {
            // 限流检查：每分钟最多1次
            if (rateLimitService != null) {
                String minuteKey = "verify_code:phone:minute:" + phone;
                boolean allowed = rateLimitService.slidingWindowLimit(minuteKey, 1, 60);
                if (!allowed) {
                    log.warn("验证码发送过于频繁（分钟限制）: phone={}", phone);
                    throw new BusinessException("验证码发送过于频繁，请稍后再试");
                }
                
                // 限流检查：每小时最多5次
                String hourKey = "verify_code:phone:hour:" + phone;
                boolean hourAllowed = rateLimitService.slidingWindowLimit(hourKey, 5, 3600);
                if (!hourAllowed) {
                    log.warn("验证码发送过于频繁（小时限制）: phone={}", phone);
                    throw new BusinessException("今日验证码发送次数已达上限，请稍后再试");
                }
            }
            
            // 生成验证码
            String code = generateVerifyCode();
            
            // 缓存验证码（有效期5分钟）
            if (cacheService != null) {
                String cacheKey = "verify_code:phone:" + phone;
                cacheService.set(cacheKey, code, 5, TimeUnit.MINUTES);
                log.debug("验证码已缓存: phone={}, code={}", phone, code);
            } else {
                log.warn("缓存服务未配置，验证码无法缓存: phone={}, code={}", phone, code);
                throw new BusinessException("系统配置错误，无法发送验证码");
            }
            
            // 发送验证码
            // 注意：实际项目中应该调用短信服务发送验证码
            // 可以通过消息队列异步发送，或直接调用短信服务API
            sendSms(phone, code);
            
            log.info("手机验证码发送成功: phone={}", phone);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送手机验证码失败: phone={}", phone, e);
            throw new BusinessException("发送验证码失败，请稍后重试");
        }
    }
    
    /**
     * 发送短信
     * <p>
     * 实际项目中应该调用短信服务发送验证码
     * 可以通过消息队列异步发送，或直接调用短信服务API
     * </p>
     * 
     * @param phone 手机号
     * @param code 验证码
     */
    private void sendSms(String phone, String code) {
        // TODO: 集成短信服务
        // 方式1：直接调用短信服务API
        // smsService.sendVerifyCode(phone, code);
        
        // 方式2：通过消息队列异步发送
        // messageProducer.send("sms-topic", "verify-code", new SmsDTO(phone, code));
        
        // 当前实现：仅记录日志（开发/测试环境）
        log.info("发送短信验证码: phone={}, code={}", phone, code);
    }

    /**
     * 发送邮箱验证码
     * <p>
     * 生成6位数字验证码并缓存（有效期5分钟），然后发送到指定邮箱
     * 包含限流检查，防止频繁发送验证码
     * </p>
     * <p>
     * 限流规则：
     * <ul>
     *   <li>每个邮箱每分钟最多发送1次</li>
     *   <li>每个邮箱每小时最多发送5次</li>
     * </ul>
     * </p>
     * 
     * @param email 邮箱地址
     * @throws BusinessException 当邮箱格式错误、限流或发送失败时抛出
     */
    public void sendEmailVerifyCode(String email) {
        // 参数验证
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("邮箱地址不能为空");
        }
        
        // 验证邮箱格式
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("邮箱格式错误，请输入有效的邮箱地址");
        }
        
        try {
            // 限流检查：每分钟最多1次
            if (rateLimitService != null) {
                String minuteKey = "verify_code:email:minute:" + email;
                boolean allowed = rateLimitService.slidingWindowLimit(minuteKey, 1, 60);
                if (!allowed) {
                    log.warn("验证码发送过于频繁（分钟限制）: email={}", email);
                    throw new BusinessException("验证码发送过于频繁，请稍后再试");
                }
                
                // 限流检查：每小时最多5次
                String hourKey = "verify_code:email:hour:" + email;
                boolean hourAllowed = rateLimitService.slidingWindowLimit(hourKey, 5, 3600);
                if (!hourAllowed) {
                    log.warn("验证码发送过于频繁（小时限制）: email={}", email);
                    throw new BusinessException("今日验证码发送次数已达上限，请稍后再试");
                }
            }
            
            // 生成验证码
            String code = generateVerifyCode();
            
            // 缓存验证码（有效期5分钟）
            if (cacheService != null) {
                String cacheKey = "verify_code:email:" + email;
                cacheService.set(cacheKey, code, 5, TimeUnit.MINUTES);
                log.debug("验证码已缓存: email={}, code={}", email, code);
            } else {
                log.warn("缓存服务未配置，验证码无法缓存: email={}, code={}", email, code);
                throw new BusinessException("系统配置错误，无法发送验证码");
            }
            
            // 发送验证码
            // 注意：实际项目中应该调用邮件服务发送验证码
            // 可以通过消息队列异步发送，或直接调用邮件服务API
            sendEmail(email, code);
            
            log.info("邮箱验证码发送成功: email={}", email);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送邮箱验证码失败: email={}", email, e);
            throw new BusinessException("发送验证码失败，请稍后重试");
        }
    }
    
    /**
     * 发送邮件
     * <p>
     * 实际项目中应该调用邮件服务发送验证码
     * 可以通过消息队列异步发送，或直接调用邮件服务API
     * </p>
     * 
     * @param email 邮箱地址
     * @param code 验证码
     */
    private void sendEmail(String email, String code) {
        // TODO: 集成邮件服务
        // 方式1：直接调用邮件服务API
        // emailService.sendVerifyCode(email, code);
        
        // 方式2：通过消息队列异步发送
        // messageProducer.send("email-topic", "verify-code", new EmailDTO(email, code));
        
        // 当前实现：仅记录日志（开发/测试环境）
        log.info("发送邮件验证码: email={}, code={}", email, code);
    }

    /**
     * 微信登录（PC端）
     * <p>
     * 通过微信授权码获取用户信息并登录，需要用户已绑定微信账号
     * </p>
     * <p>
     * 流程说明：
     * <ol>
     *   <li>使用授权码调用微信API获取 openId 和 access_token</li>
     *   <li>使用 access_token 调用微信API获取用户信息</li>
     *   <li>根据 openId 查找已绑定的用户</li>
     *   <li>如果用户未绑定，抛出异常提示用户先绑定</li>
     *   <li>生成并返回 Token</li>
     * </ol>
     * </p>
     * 
     * @param code 微信授权码，由微信OAuth2.0授权后返回
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当授权码无效、微信API调用失败或用户未绑定时抛出
     */
    @Transactional
    public LoginResultDTO loginByWechat(String code) {
        // 参数验证
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("微信授权码不能为空");
        }
        
        try {
            log.info("开始微信登录: code={}", code);
            
            // 调用微信API获取 openId 和 access_token
            WechatAccessTokenResponse tokenResponse = getWechatAccessToken(code);
            if (tokenResponse == null || tokenResponse.getOpenid() == null) {
                log.error("获取微信 access_token 失败: code={}", code);
                throw new BusinessException("微信授权失败，请重新授权");
            }
            
            String openId = tokenResponse.getOpenid();
            String accessToken = tokenResponse.getAccess_token();
            log.debug("获取微信 openId 成功: openId={}", openId);
            
            // 根据 openId 查找用户
            User user = userRepository.findByWechatOpenId(openId);
            if (user == null) {
                log.warn("微信用户未绑定: openId={}", openId);
                throw new BusinessException("微信用户未绑定，请先绑定账号");
            }
            
            // 可选：更新用户微信信息（如昵称、头像等）
            updateWechatUserInfo(user, accessToken, openId);
            
            log.info("微信登录成功: userId={}, openId={}", user.getId(), openId);
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常: code={}", code, e);
            throw new BusinessException("微信登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取微信 access_token
     * <p>
     * 调用微信API，使用授权码换取 access_token 和 openId
     * </p>
     * 
     * @param code 微信授权码
     * @return 微信 access_token 响应，包含 openId 和 access_token
     */
    private WechatAccessTokenResponse getWechatAccessToken(String code) {
        // TODO: 调用微信API获取 access_token
        // 实际实现需要：
        // 1. 从配置中获取 appId 和 appSecret
        // 2. 调用微信API: https://api.weixin.qq.com/sns/oauth2/access_token
        // 3. 解析响应并返回 WechatAccessTokenResponse
        
        // 当前实现：简化处理（开发/测试环境）
        log.warn("微信API调用未实现，使用简化处理: code={}", code);
        WechatAccessTokenResponse response = new WechatAccessTokenResponse();
        response.setOpenid(code);  // 简化处理，实际应该从微信API获取
        response.setAccess_token("mock_access_token");
        return response;
    }
    
    /**
     * 更新用户微信信息
     * <p>
     * 可选操作，使用 access_token 获取用户微信信息并更新到数据库
     * </p>
     * 
     * @param user 用户对象
     * @param accessToken 微信 access_token
     * @param openId 微信 openId
     */
    private void updateWechatUserInfo(User user, String accessToken, String openId) {
        // TODO: 调用微信API获取用户信息并更新
        // 实际实现需要：
        // 1. 调用微信API: https://api.weixin.qq.com/sns/userinfo
        // 2. 解析用户信息（昵称、头像等）
        // 3. 更新用户信息到数据库
        
        log.debug("更新用户微信信息: userId={}, openId={}", user.getId(), openId);
    }
    
    /**
     * 微信 access_token 响应对象
     */
    private static class WechatAccessTokenResponse {
        private String access_token;
        private String openid;
        private String unionid;
        
        public String getAccess_token() {
            return access_token;
        }
        
        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
        
        public String getOpenid() {
            return openid;
        }
        
        public void setOpenid(String openid) {
            this.openid = openid;
        }
        
        public String getUnionid() {
            return unionid;
        }
        
        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }

    /**
     * 小程序微信登录
     * <p>
     * 通过微信小程序授权码获取用户信息并登录，如果用户不存在会自动注册
     * </p>
     * <p>
     * 流程说明：
     * <ol>
     *   <li>使用授权码调用微信小程序API获取 openId 和 session_key</li>
     *   <li>根据 openId 查找用户</li>
     *   <li>如果用户不存在，自动创建新用户（小程序支持自动注册）</li>
     *   <li>生成并返回 Token</li>
     * </ol>
     * </p>
     * 
     * @param code 微信小程序授权码，由微信小程序 wx.login() 获取
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当授权码无效或微信API调用失败时抛出
     */
    @Transactional
    public LoginResultDTO loginByMiniProgram(String code) {
        // 参数验证
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("微信小程序授权码不能为空");
        }
        
        try {
            log.info("开始小程序微信登录: code={}", code);
            
            // 调用微信小程序API获取 openId 和 session_key
            MiniProgramLoginResponse loginResponse = getMiniProgramOpenId(code);
            if (loginResponse == null || loginResponse.getOpenid() == null) {
                log.error("获取微信小程序 openId 失败: code={}", code);
                throw new BusinessException("微信授权失败，请重新授权");
            }
            
            String openId = loginResponse.getOpenid();
            String sessionKey = loginResponse.getSession_key();
            log.debug("获取微信小程序 openId 成功: openId={}", openId);
            
            // 根据 openId 查找用户
            User user = userRepository.findByWechatOpenId(openId);
            if (user == null) {
                // 小程序可以自动注册
                log.info("小程序用户不存在，自动注册: openId={}", openId);
                user = new User();
                user.setWechatOpenId(openId);
                user.setStatus(1);
                // 生成默认用户名
                user.setUsername("wx_" + openId.substring(0, Math.min(8, openId.length())));
                user = userRepository.save(user);
                log.info("小程序用户自动注册成功: userId={}, openId={}", user.getId(), openId);
            }
            
            log.info("小程序微信登录成功: userId={}, openId={}", user.getId(), openId);
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("小程序微信登录异常: code={}", code, e);
            throw new BusinessException("小程序登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取微信小程序 openId
     * <p>
     * 调用微信小程序API，使用授权码换取 openId 和 session_key
     * </p>
     * 
     * @param code 微信小程序授权码
     * @return 微信小程序登录响应，包含 openId 和 session_key
     */
    private MiniProgramLoginResponse getMiniProgramOpenId(String code) {
        // TODO: 调用微信小程序API获取 openId
        // 实际实现需要：
        // 1. 从配置中获取 appId 和 appSecret
        // 2. 调用微信API: https://api.weixin.qq.com/sns/jscode2session
        // 3. 解析响应并返回 MiniProgramLoginResponse
        
        // 当前实现：简化处理（开发/测试环境）
        log.warn("微信小程序API调用未实现，使用简化处理: code={}", code);
        MiniProgramLoginResponse response = new MiniProgramLoginResponse();
        response.setOpenid(code);  // 简化处理，实际应该从微信API获取
        response.setSession_key("mock_session_key");
        return response;
    }
    
    /**
     * 微信小程序登录响应对象
     */
    private static class MiniProgramLoginResponse {
        private String openid;
        private String session_key;
        private String unionid;
        
        public String getOpenid() {
            return openid;
        }
        
        public void setOpenid(String openid) {
            this.openid = openid;
        }
        
        public String getSession_key() {
            return session_key;
        }
        
        public void setSession_key(String session_key) {
            this.session_key = session_key;
        }
        
        public String getUnionid() {
            return unionid;
        }
        
        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }

    /**
     * 生成Token
     * <p>
     * 根据用户信息生成 JWT Token，Token 中包含用户ID、用户名、手机号等信息
     * </p>
     * 
     * @param user 用户领域模型，必须包含有效的用户ID
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当用户信息为空或用户ID为空时抛出
     */
    private LoginResultDTO generateToken(User user) {
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }
        if (user.getId() == null) {
            throw new BusinessException("用户ID不能为空，请检查数据库用户数据");
        }
        
        try {
            // 创建一个HashMap用于存储JWT的声明信息
            Map<String, Object> claims = new HashMap<>();
            // 添加用户名到声明中
            claims.put("username", user.getUsername());
            // 添加手机号到声明中
            claims.put("phone", user.getPhone());
            // 使用SSO服务生成包含用户ID和声明信息的令牌
            String token = ssoService.generateToken(user.getId(), claims);

            // 创建登录结果DTO对象
            LoginResultDTO result = new LoginResultDTO();
            // 设置令牌到结果对象中
            result.setToken(token);
            // 设置用户ID到结果对象中
            result.setUserId(user.getId());
            // 设置用户名到结果对象中
            result.setUsername(user.getUsername());
            // 返回登录结果
            return result;
        } catch (Exception e) {
            log.error("生成Token失败: userId={}, username={}", user.getId(), user.getUsername(), e);
            throw new BusinessException("生成Token失败: " + e.getMessage());
        }
    }

    /**
     * 生成验证码
     * <p>
     * 生成6位随机数字验证码
     * </p>
     * 
     * @return 6位数字验证码字符串
     */
    private String generateVerifyCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 用户注册
     * <p>
     * 创建新用户账号，包括用户名、密码等信息
     * 注册成功后自动登录并返回 Token
     * </p>
     * 
     * @param registerDTO 注册请求参数，包含用户名、密码等信息
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当用户名已存在、手机号已存在、邮箱已存在或密码不一致时抛出
     */
    @Transactional
    public LoginResultDTO register(RegisterDTO registerDTO) {
        try {
            log.info("开始注册: username={}", registerDTO.getUsername());
            
            // 验证密码一致性
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                log.warn("密码不一致: username={}", registerDTO.getUsername());
                throw new BusinessException("两次输入的密码不一致");
            }
            
            // 检查用户名是否已存在
            User existingUser = userRepository.findByUsername(registerDTO.getUsername());
            if (existingUser != null) {
                log.warn("用户名已存在: username={}", registerDTO.getUsername());
                throw new BusinessException("用户名已存在");
            }
            
            // 检查手机号是否已存在（如果提供了手机号）
            if (registerDTO.getPhone() != null && !registerDTO.getPhone().trim().isEmpty()) {
                existingUser = userRepository.findByPhone(registerDTO.getPhone());
                if (existingUser != null) {
                    log.warn("手机号已存在: phone={}", registerDTO.getPhone());
                    throw new BusinessException("手机号已被注册");
                }
            }
            
            // 检查邮箱是否已存在（如果提供了邮箱）
            if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()) {
                existingUser = userRepository.findByEmail(registerDTO.getEmail());
                if (existingUser != null) {
                    log.warn("邮箱已存在: email={}", registerDTO.getEmail());
                    throw new BusinessException("邮箱已被注册");
                }
            }
            
            // 创建新用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPhone(registerDTO.getPhone());
            user.setEmail(registerDTO.getEmail());
            user.setStatus(1); // 默认状态为正常
            
            // 生成盐值并加密密码
            String salt = generateSalt();
            user.setSalt(salt);
            user.setPassword(authDomainService.encryptPassword(registerDTO.getPassword(), salt));
            
            // 保存用户
            user = userRepository.save(user);
            log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
            
            // 注册成功后自动登录，返回 Token
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("注册异常: username={}", registerDTO.getUsername(), e);
            throw new BusinessException("注册失败: " + e.getMessage());
        }
    }

    /**
     * 邮箱注册
     * <p>
     * 使用邮箱和验证码进行注册，注册成功后自动登录并返回 Token
     * </p>
     * 
     * @param registerDTO 邮箱注册请求参数
     * @return 登录结果，包含 Token、用户ID和用户名
     * @throws BusinessException 当验证码错误、邮箱已存在或用户名已存在时抛出
     */
    @Transactional
    public LoginResultDTO registerByEmail(com.example.auth.application.dto.EmailRegisterDTO registerDTO) {
        try {
            log.info("开始邮箱注册: email={}, username={}", registerDTO.getEmail(), registerDTO.getUsername());
            
            // 验证密码一致性
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                throw new BusinessException("两次输入的密码不一致");
            }
            
            // 验证邮箱验证码
            if (cacheService != null) {
                String cacheKey = "verify_code:email:" + registerDTO.getEmail();
                String cachedCode = cacheService.get(cacheKey, String.class);
                if (cachedCode == null || !cachedCode.equals(registerDTO.getEmailCode())) {
                    throw new BusinessException("邮箱验证码错误或已过期");
                }
                // 删除验证码
                cacheService.delete(cacheKey);
            } else {
                log.warn("缓存服务未配置，跳过验证码验证");
            }
            
            // 检查用户名是否已存在
            User existingUser = userRepository.findByUsername(registerDTO.getUsername());
            if (existingUser != null) {
                throw new BusinessException("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            existingUser = userRepository.findByEmail(registerDTO.getEmail());
            if (existingUser != null) {
                throw new BusinessException("邮箱已被注册");
            }
            
            // 创建新用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setEmail(registerDTO.getEmail());
            user.setStatus(1);
            
            // 生成盐值并加密密码
            String salt = generateSalt();
            user.setSalt(salt);
            user.setPassword(authDomainService.encryptPassword(registerDTO.getPassword(), salt));
            
            // 保存用户
            user = userRepository.save(user);
            log.info("邮箱注册成功: userId={}, username={}, email={}", user.getId(), user.getUsername(), user.getEmail());
            
            // 注册成功后自动登录
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("邮箱注册异常: email={}", registerDTO.getEmail(), e);
            throw new BusinessException("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 忘记密码
     * <p>
     * 发送密码重置邮件，包含重置令牌链接
     * </p>
     * 
     * @param forgetPasswordDTO 忘记密码请求参数
     * @throws BusinessException 当邮箱不存在或发送失败时抛出
     */
    public void forgetPassword(com.example.auth.application.dto.ForgetPasswordDTO forgetPasswordDTO) {
        try {
            String email = forgetPasswordDTO.getEmail();
            log.info("开始忘记密码流程: email={}", email);
            
            // 查找用户
            User user = userRepository.findByEmail(email);
            if (user == null) {
                // 为了安全，不提示用户是否存在
                log.warn("邮箱不存在: email={}", email);
                throw new BusinessException("如果该邮箱已注册，重置链接已发送到您的邮箱");
            }
            
            // 生成重置令牌
            String resetToken = generateResetToken();
            
            // 保存重置令牌到数据库（如果实现了数据库存储）
            // 这里简化处理，使用缓存存储
            if (cacheService != null) {
                String cacheKey = "reset_token:" + resetToken;
                // 存储用户ID和邮箱，有效期30分钟
                Map<String, Object> resetInfo = new HashMap<>();
                resetInfo.put("userId", user.getId());
                resetInfo.put("email", email);
                cacheService.set(cacheKey, resetInfo, 30, TimeUnit.MINUTES);
            }
            
            // 发送重置密码邮件
            sendResetPasswordEmail(email, resetToken);
            
            log.info("忘记密码邮件发送成功: email={}", email);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("忘记密码异常: email={}", forgetPasswordDTO.getEmail(), e);
            throw new BusinessException("发送重置密码邮件失败，请稍后重试");
        }
    }
    
    /**
     * 重置密码
     * <p>
     * 使用重置令牌重置用户密码
     * </p>
     * 
     * @param resetPasswordDTO 重置密码请求参数
     * @throws BusinessException 当重置令牌无效、已过期或密码不一致时抛出
     */
    @Transactional
    public void resetPassword(com.example.auth.application.dto.ResetPasswordDTO resetPasswordDTO) {
        try {
            log.info("开始重置密码: resetToken={}", resetPasswordDTO.getResetToken());
            
            // 验证密码一致性
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                throw new BusinessException("两次输入的密码不一致");
            }
            
            // 验证重置令牌
            Map<String, Object> resetInfo = null;
            if (cacheService != null) {
                String cacheKey = "reset_token:" + resetPasswordDTO.getResetToken();
                resetInfo = cacheService.get(cacheKey, Map.class);
                if (resetInfo == null) {
                    throw new BusinessException("重置令牌无效或已过期");
                }
                // 删除令牌（只能使用一次）
                cacheService.delete(cacheKey);
            } else {
                throw new BusinessException("系统配置错误，无法重置密码");
            }
            
            // 获取用户ID
            Long userId = Long.valueOf(resetInfo.get("userId").toString());
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 更新密码
            String salt = generateSalt();
            user.setSalt(salt);
            user.setPassword(authDomainService.encryptPassword(resetPasswordDTO.getNewPassword(), salt));
            userRepository.save(user);
            
            log.info("密码重置成功: userId={}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("重置密码异常: resetToken={}", resetPasswordDTO.getResetToken(), e);
            throw new BusinessException("重置密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成重置令牌
     * <p>
     * 生成一个唯一的重置令牌，用于密码重置
     * </p>
     * 
     * @return 重置令牌字符串
     */
    private String generateResetToken() {
        return encryptService.encryptMD5(System.currentTimeMillis() + "" + Math.random());
    }
    
    /**
     * 发送重置密码邮件
     * <p>
     * 发送包含重置令牌链接的邮件
     * </p>
     * 
     * @param email 邮箱地址
     * @param resetToken 重置令牌
     */
    private void sendResetPasswordEmail(String email, String resetToken) {
        // TODO: 集成邮件服务发送重置密码邮件
        // 邮件内容应包含重置链接，如：https://example.com/reset-password?token=xxx
        log.info("发送重置密码邮件: email={}, resetToken={}", email, resetToken);
    }

    /**
     * 生成盐值
     * <p>
     * 使用当前时间戳生成MD5加密的盐值，用于密码加密
     * </p>
     * 
     * @return MD5加密后的盐值字符串
     */
    private String generateSalt() {
        return encryptService.encryptMD5(String.valueOf(System.currentTimeMillis()));
    }
}

