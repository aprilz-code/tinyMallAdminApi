package com.aprilz.tiny.mall.validator.annotion;

import com.aprilz.tiny.mall.validator.constraint.IsUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * @description: 用户名规则校验
 * @author: Aprilz
 * @since: 2022/10/18
 **/
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IsUsernameValidator.class})
public @interface  IsUsernameValid {

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
