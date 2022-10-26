package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.common.utils.JwtTokenUtil;
import com.aprilz.tiny.common.utils.SpringContextUtil;
import com.aprilz.tiny.mapper.ApOrderGoodsMapper;
import com.aprilz.tiny.mapper.ApOrderMapper;
import com.aprilz.tiny.mapper.ApUserMapper;
import com.aprilz.tiny.mbg.entity.ApOrderGoods;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApAdService;
import com.aprilz.tiny.service.IApStatService;
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
import java.util.Map;

/**
 * <p>
 * 统计service 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
@Slf4j
@Service
public class ApStatServiceImpl implements IApStatService {

    @Resource
    private  ApUserMapper userMapper;
    @Resource
    private ApOrderMapper orderMapper;

    @Resource
    private ApOrderGoodsMapper goodsMapper;

    @Override
    public List<Map> statUser() {
        return userMapper.statUser();
    }

    @Override
    public List<Map> statOrder() {
        return orderMapper.statOrder();
    }

    @Override
    public List<Map> statGoods() {
        return goodsMapper.statGoods();
    }
}
