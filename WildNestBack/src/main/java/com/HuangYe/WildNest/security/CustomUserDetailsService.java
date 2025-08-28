package com.HuangYe.WildNest.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 * 目前使用硬编码的管理员账户，后续可扩展为数据库查询
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final PasswordEncoder passwordEncoder;
    
    // 硬编码的管理员账户（后续可改为数据库查询）
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123"; // 实际密码，会被编码
    
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        // 目前只支持一个管理员账户
        if (ADMIN_USERNAME.equals(username)) {
            return User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        }
        
        throw new UsernameNotFoundException("用户不存在: " + username);
    }
    
    /**
     * 验证用户密码
     */
    public boolean validatePassword(String username, String rawPassword) {
        if (ADMIN_USERNAME.equals(username)) {
            return ADMIN_PASSWORD.equals(rawPassword);
        }
        return false;
    }
}