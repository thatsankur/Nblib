package com.nearbuy.mobilecore.models;

import android.os.Message;

import java.io.InputStream;

/**
 * Created by Ankur Singh on 12/04/16.
 */

public interface ResponseListener {
    /**
     * This will be called in background thread
     * @param pMessage
     * @param inputStream
     * @return
     */
    boolean parseResponse(RequestTypes pRequestTag,Message pMessage,InputStream inputStream);

    /**
     * in case of Activity or fragment this will be called on UI Thread
     * in other cases it will in thread other then background thread
     * @param pRequestTag
     * @param pResponse
     */
    void onResponse(RequestTypes pRequestTag, Object pResponse);

    /**
     * in case of Activity or fragment this will be called on UI Thread
     * in other cases it will in thread other then background thread
     * @param pRequestTag
     * @param error
     */
    void onError(RequestTypes pRequestTag, Exception error);
}
