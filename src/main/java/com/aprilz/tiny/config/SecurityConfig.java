package com.aprilz.tiny.config;

import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.common.properties.IgnoredUrlsProperties;
import com.aprilz.tiny.component.JwtAuthenticationTokenFilter;
import com.aprilz.tiny.component.RestAuthenticationEntryPoint;
import com.aprilz.tiny.dto.AdminUserDetails;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApPermission;
import com.aprilz.tiny.service.IApAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * SpringSecurity?????????
 * Created by aprilz on 2018/4/26.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private IApAdminService adminService;

    /**
     * ??????????????????
     */
    @Resource
    private IgnoredUrlsProperties ignoredUrlsProperties;


    @Autowired
    private Cache<String> cache;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        //?????????url ???????????????
        for (String url : ignoredUrlsProperties.getUrls()) {
            registry.antMatchers(url).permitAll();
        }

        httpSecurity
                //????????????iframe
                .headers().frameOptions().disable()
                .and()
                .logout()
                .permitAll()
                .and()
                // ??????????????????JWT????????????????????????csrf
                .csrf().disable()
                .sessionManagement()// ??????token??????????????????session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //????????????
                .and()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeRequests()
//                .antMatchers("/sso/login", "/sso/register")// ????????????????????????????????????
//                .permitAll()
                .antMatchers(HttpMethod.OPTIONS)//??????????????????????????????options??????
                .permitAll()
//                .antMatchers("/**")//???????????????????????????
//                .permitAll()
                .anyRequest()// ???????????????????????????????????????????????????
                .authenticated();
        // ????????????
        httpSecurity.headers().cacheControl();
        // ??????JWT filter
        httpSecurity.addFilter(jwtAuthenticationTokenFilter());
        //????????????????????????????????????????????????
        httpSecurity.exceptionHandling()
                //??????????????????????????????
                //    .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ???????????????????????????
        //    if (environment.acceptsProfiles(Profiles.of("dev"))) {
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        //     } else {
        //          configuration.setAllowedOrigins(Collections.singletonList("http://??????"));
        //     }
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addExposedHeader(tokenHeader);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        //????????????????????????
        return username -> {
            ApAdmin admin = adminService.getAdminByUsernameOrMobile(username);
            if (admin != null) {
                if(!admin.getStatus()){
                    throw new LockedException("??????????????????????????????");
                }
                List<ApPermission> permissionList = adminService.getPermissionList(admin.getId());
                return new AdminUserDetails(admin, permissionList);
            }
            throw new UsernameNotFoundException("????????????????????????");
        };
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
        return new JwtAuthenticationTokenFilter(authenticationManager(), cache);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
