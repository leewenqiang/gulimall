package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @ClassName ItemDoneVo
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/29 11:44
 * @Version 1.0
 */
@Data
public class ItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;

}
