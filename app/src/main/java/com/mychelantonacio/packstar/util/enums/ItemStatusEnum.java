package com.mychelantonacio.packstar.util.enums;


public enum ItemStatusEnum {

    NEED_TO_BUY("B"),
    ALREADY_HAVE("A"),
    NON_INFORMATION("N");

    private final String statusCode;

    ItemStatusEnum(String status){
        this.statusCode = status;
    }

    public String getStatusCode(){
        return this.statusCode;
    }

}
