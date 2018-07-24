package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public static final String TOKEN_PREFIX = "token_";

    public interface orderBy{
        Set<String> PRODUCT_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public interface RedisCacheExtime{
        int REDIS_EXTIME=60*30;
        int FORGET_TOKEN=60*60*12;
    }
    public interface Role {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public static final String FILE_UPLOAD_PATH="/img";

    public interface Cart{
        int PRODUCT_CHECKED=1;//购物车中的商品被选中
        int PRODUCT_UN_CHECKED=0;//购物车中的商品未被选中

        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private int code;
        private String value;
        ProductStatusEnum(int code, String value){
            this.code=code;
            this.value=value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum OrderStatus{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String value;
        OrderStatus(int code,String value){
            this.code=code;
            this.value=value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static OrderStatus codeOf(int code){
            for(OrderStatus orderStatus : values()){
                if(orderStatus.getCode() == code){
                    return orderStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatform{
        Alipay(1,"支付宝");

        private int code;
        private String value;
        PayPlatform(int code,String value){
            this.code=code;
            this.value=value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentType{
        ONLINE(1,"在线支付");

        private int code;
        private String value;
        PaymentType(int code,String value){
            this.code=code;
            this.value=value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static PaymentType codeOf(int code){
            for(PaymentType paymentType : values()){
                if(paymentType.getCode() == code){
                    return paymentType;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface RedisLock{
        String CLOSE_ORDER_TASK_LOCK="CLOSE_ORDER_TASK_LOCK";
    }
}
