package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName DoneVo
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/29 11:43
 * @Version 1.0
 */
@Data
public class DoneVo {

    @NotNull
    private Long id;

    private List<ItemDoneVo> items;

}
