package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.common.utils.JwtTokenUtil;
import com.aprilz.tiny.common.utils.SpringContextUtil;
import com.aprilz.tiny.mapper.ApUserMapper;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApAdService;
import com.aprilz.tiny.service.IApUserService;
import com.aprilz.tiny.vo.Token;
import com.aprilz.tiny.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
@Slf4j
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements IApUserService {

    @Resource
    private ApUserMapper apUserMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IApAdService adService;

    @Override
    public ApUser getUserByUsernameOrMobile(String username) {
        LambdaQueryWrapper<ApUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApUser::getDeleteFlag, false).eq(ApUser::getStatus, 1);
        wrapper.and(w -> {
            w.eq(ApUser::getUsername, username)
                    .or().eq(ApUser::getMobile, username);
        });
        List<ApUser> apUsers = apUserMapper.selectList(wrapper);
        if (apUsers != null && apUsers.size() > 0) {
            return apUsers.get(0);
        }
        return null;
    }

    @Override
    public Token login(String username, String password) {
        Token token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Transactional
    @Override
    public void ts() {
        ApUser apUser = new ApUser();
        apUser.setUsername("test").setPassword("123456").setAvatar("1111");
        this.save(apUser);
        //想让子方法不影响父方法，需使用try，catch
        //父方法不影响子方法，使用propagation = Propagation.REQUIRES_NEW
        try {
            //使用this.test()会导致事务隔离失效
            IApUserService bean = SpringContextUtil.getBean(IApUserService.class);
            bean.test();
            //  adService.test();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        IApUserService bean = SpringContextUtil.getBean(IApUserService.class);
//        bean.test();


        throw new RuntimeException();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test() {
        ApUser apUser = new ApUser();
        apUser.setUsername("test22").setPassword("123456").setAvatar("1111");
        this.save(apUser);
    }

    @Override
    public UserVo findUserVoById(Long userId) {
        ApUser user = this.getById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }
}
