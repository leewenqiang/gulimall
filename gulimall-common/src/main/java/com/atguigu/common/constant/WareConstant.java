package com.atguigu.common.constant;

/**
 * @ClassName ProductConstant
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/21 17:41
 * @Version 1.0
 */
public class WareConstant {
    
    public enum PurchaseStatusEnum{
        /**
         *
         */
        CREATED(0,"新建"),
        AGGIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),
        FINISHED(3,"已完成"),
        ERROR(4,"有异常"),
        ;
        /**
         * 销售属性
         */
        private int code;
        private String msg;
        PurchaseStatusEnum(int code,String msg){
            this.code=code;
            this.msg=msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailStatusEnum{
        /**
         *
         */
        CREATED(0,"新建"),
        AGGIGNED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISHED(3,"已完成"),
        ERROR(4,"采购失败"),
        ;
        /**
         * 销售属性
         */
        private int code;
        private String msg;
        PurchaseDetailStatusEnum(int code,String msg){
            this.code=code;
            this.msg=msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
