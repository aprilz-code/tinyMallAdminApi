package com.aprilz.tiny.mbg.entity;

import com.aprilz.tiny.mbg.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 通知表
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ap_notice")
@ApiModel(value = "ApNotice对象", description = "通知表")
public class ApNotice extends BaseEntity<ApNotice> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("通知标题")
    @TableField("title")
    @NotBlank(message = "通知标题不能为空")
    private String title;

    @ApiModelProperty("通知内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("创建通知的管理员ID，如果是系统内置通知则是0.")
    @TableField("admin_id")
    private Long adminId;


    @Override
    public Serializable pkVal() {
        return null;
    }

}
