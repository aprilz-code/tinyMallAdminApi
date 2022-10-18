package com.aprilz.tiny.mall.validator.constraint;

import com.aprilz.tiny.common.utils.RegexUtil;
import com.aprilz.tiny.mall.validator.annotion.IsUsernameValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description: 用户名规则校验
 * @author: Aprilz
 * @since: 2022/10/18
 **/
public class IsUsernameValidator implements ConstraintValidator<IsUsernameValid, String> {
    @Override
    public void initialize(IsUsernameValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (!RegexUtil.isUsername(name)) {
            //禁用默认的message的值
            context.disableDefaultConstraintViolation();
            //重新添加错误提示语句
            context.buildConstraintViolationWithTemplate("管理员名称不符合规定，请修改！").addConstraintViolation();
            return  false;
        }
        return true;
    }
}
