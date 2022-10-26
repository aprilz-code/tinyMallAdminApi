package com.aprilz.tiny.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description: 权限变更
 * @author: Aprilz
 * @since: 2022/10/26
 **/
@Data
public class UpdatePermissionsParam {

    @NotNull
    private Long roleId;

    @NotEmpty
    private List<Long> permissions;
}
