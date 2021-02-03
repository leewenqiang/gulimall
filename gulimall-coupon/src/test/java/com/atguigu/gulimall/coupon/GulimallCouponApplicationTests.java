package com.atguigu.gulimall.coupon;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//@SpringBootTest
//@Test
public class GulimallCouponApplicationTests {

    @Test
   public void test1() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX);
        System.out.println(start);
        System.out.println(end);;
    }

}
