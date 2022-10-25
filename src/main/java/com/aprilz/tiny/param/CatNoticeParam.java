package com.aprilz.tiny.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: Aprilz
 * @since: 2022/10/18
 **/
@Data
public class CatNoticeParam {

    @NotNull
    private Long noticeId;
}
