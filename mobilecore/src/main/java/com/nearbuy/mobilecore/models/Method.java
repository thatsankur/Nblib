package com.nearbuy.mobilecore.models;

/**
 * Created by Ankur Singh(ankur.singh@nearbuy.com) on 02/03/16.
 */
public enum Method {
    DEPRECATED_GET_OR_POST(-1),
    GET(0),
    POST(1),
    PUT(2),
    DELETE(3),
    HEAD(4),
    OPTIONS(5),
    TRACE(6),
    PATCH(7);
    private int value;
    Method(int val){
        value = val;
    }

    public int getValue() {
        return value;
    }
}