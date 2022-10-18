package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApCommentMapper;
import com.aprilz.tiny.mbg.entity.ApComment;
import com.aprilz.tiny.service.IApCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApCommentServiceImpl extends ServiceImpl<ApCommentMapper, ApComment> implements IApCommentService {

}
