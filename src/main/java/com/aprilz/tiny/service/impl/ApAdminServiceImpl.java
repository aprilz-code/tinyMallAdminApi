package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.JwtTokenUtil;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApAdminMapper;
import com.aprilz.tiny.mapper.ApPermissionMapper;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApPermission;
import com.aprilz.tiny.service.IApAdminService;
import com.aprilz.tiny.vo.Token;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
@Slf4j
@Service
public class ApAdminServiceImpl extends ServiceImpl<ApAdminMapper, ApAdmin> implements IApAdminService {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private ApAdminMapper adminMapper;
    @Resource
    private ApPermissionMapper apPermissionMapper;

    @Override
    public ApAdmin getAdminByUsernameOrMobile(String username) {
        LambdaQueryWrapper<ApAdmin> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApAdmin::getDeleteFlag, false).and(wrapper -> {
            wrapper.eq(ApAdmin::getUsername, username).or().eq(ApAdmin::getMobile, username);
        })
                .last("limit 1");
        return adminMapper.selectOne(queryWrapper);
    }

    @Override
    public ApAdmin register(ApAdmin apAdminParam) {
        ApAdmin apAdmin = new ApAdmin();
        BeanUtils.copyProperties(apAdminParam, apAdmin);
        apAdmin.setCreateTime(new Date());
        apAdmin.setStatus(true);
        //查询是否有相同用户名的用户
        LambdaQueryWrapper<ApAdmin> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ApAdmin::getUsername, apAdminParam.getUsername());
        List<ApAdmin> apAdminList = adminMapper.selectList(queryWrapper);
        if (apAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(apAdmin.getPassword());
        apAdmin.setPassword(encodePassword);
        adminMapper.insert(apAdmin);
        return apAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Token tk = jwtTokenUtil.generateToken(userDetails);
            token = tk.getToken();
        } catch (LockedException e) {
            throw new LockedException("用户帐号已锁定不可用");
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }


    @Override
    public List<ApPermission> getPermissionList(Long adminId) {
        return apPermissionMapper.getPermissionList(adminId);
    }

    @Override
    public Page<ApAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApAdmin> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(username)) {
            query.like(ApAdmin::getUsername, username);
        }
        query.eq(ApAdmin::getDeleteFlag, false);

        Page<ApAdmin> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
