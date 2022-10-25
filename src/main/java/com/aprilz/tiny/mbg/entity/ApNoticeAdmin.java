package com.aprilz.tiny.mbg.entity;

import com.aprilz.tiny.mbg.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 通知管理员表
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ap_notice_admin")
@ApiModel(value = "ApNoticeAdmin对象", description = "通知管理员表")
public class ApNoticeAdmin extends BaseEntity<ApNoticeAdmin> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("通知ID")
    @TableField("notice_id")
    private Long noticeId;

    @ApiModelProperty("通知标题")
    @TableField("notice_title")
    private String noticeTitle;

    @ApiModelProperty("接收通知的管理员ID")
    @TableField("admin_id")
    private Long adminId;

    @ApiModelProperty("阅读时间，如果是NULL则是未读状态")
    @TableField("read_time")
    private Date readTime;


    @Override
    public Serializable pkVal() {
        return null;
    }

}
