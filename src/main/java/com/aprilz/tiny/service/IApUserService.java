package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.vo.Token;
import com.aprilz.tiny.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
public interface IApUserService extends IService<ApUser> {

    ApUser getUserByUsernameOrMobile(String username);

    Token login(String username, String password);

    void ts();

    void test();

    UserVo findUserVoById(Long userId);
}
