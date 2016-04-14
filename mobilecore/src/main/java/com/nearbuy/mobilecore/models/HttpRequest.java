package com.nearbuy.mobilecore.models;

import java.util.Map;

/**
 * Created by Ankur Singh on 12/04/16.
 */

public class HttpRequest {

    private String mURL;
    private Method mRequestType;
    private String mContentType;
    private String mRequestBody;
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private String mHeaderNamesAndValues;

    private Map<String,String> headerKeyValue;

    private Map mExtraValue;
    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public HttpRequest() {

    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public Method getRequestType() {
        return mRequestType;
    }

    public void setRequestType(Method mRequestType) {
        this.mRequestType = mRequestType;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String mContentType) {
        this.mContentType = mContentType;
    }

    public String getRequestBody() {
        return mRequestBody;
    }

    public void setRequestBody(String mRequestBody) {
        this.mRequestBody = mRequestBody;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public void setConnectTimeout(long mConnectTimeout) {
        this.mConnectTimeout = mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public void setReadTimeout(long mReadTimeout) {
        this.mReadTimeout = mReadTimeout;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public void setWriteTimeout(long mWriteTimeout) {
        this.mWriteTimeout = mWriteTimeout;
    }



    public void setTimeout(long mTimeout) {
        this.mConnectTimeout = mTimeout;
        this.mWriteTimeout = mTimeout;
        this.mReadTimeout = mTimeout;
    }



    public String getHeaderNamesAndValues() {
        return mHeaderNamesAndValues;
    }

    public void setHeaderNamesAndValues(String mHeaderNamesAndValues) {
        this.mHeaderNamesAndValues = mHeaderNamesAndValues;
    }



    public Map getmExtraValue() {
        return mExtraValue;
    }

    public void setmExtraValue(Map mExtraValue) {
        this.mExtraValue = mExtraValue;
    }

    public Map<String, String> getHeaderKeyValue() {
        return headerKeyValue;
    }

    public void setHeaderKeyValue(Map<String, String> headerKeyValue) {
        this.headerKeyValue = headerKeyValue;
    }
}
