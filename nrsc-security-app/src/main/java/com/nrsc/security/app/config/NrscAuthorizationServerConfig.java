package com.nrsc.security.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

/**
 * @author : Sun Chuan
 * @date : 2019/10/11 22:25
 * Description：认证服务器
 */
@Configuration
@EnableAuthorizationServer
public class NrscAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    //对正在进行授权的用户进行认证+校验时需要用到
    @Autowired
    private UserDetailsService NRSCDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore redisTokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;


    /***
     * 入口点相关配置  ---  token的生成，存储方式等在这里配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //指定使用的TokenStore，tokenStore用来存取token，默认使用InMemoryTokenStore
                //这里使用redis存储token
                .tokenStore(redisTokenStore)
                //下面的配置主要用来指定"对正在进行授权的用户进行认证+校验"的类
                //在实现了AuthorizationServerConfigurerAdapter适配器类后，必须指定下面两项
                .authenticationManager(authenticationManager)
                .userDetailsService(NRSCDetailsService);
    }

    /***
     * 第三方客户端相关的配置在这里进行配置 ,之前我们在yml配置文件里对客户端进行过简单的配置
     * 在这里进行配置会覆盖yml中的配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //直接将dataSource给clients就行了 --- jdbc会直接去库里拿客户端信息
        clients.jdbc(dataSource);
    }

    /***
     * 用来打印一下密码，因为数据spring-security-oauth要求这个密码必须被加密过
     * @param args
     */
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
    }
}