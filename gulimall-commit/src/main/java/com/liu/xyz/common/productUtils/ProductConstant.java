package com.liu.xyz.common.productUtils;

/**
 * create liu 2022-10-05
 */
public class ProductConstant {
    public enum  AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;

        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum  StatusEnum{
        DOW_SPU(2,"下架"), UP_SPU(1,"上架"),NEW_SPU(0,"新建");

        private int code;
        private String msg;

        StatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
