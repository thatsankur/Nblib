package com.nearbuy.mobilecore.network;

import android.app.Activity;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.nearbuy.mobilecore.models.RequestTypes;
import com.nearbuy.mobilecore.models.ResponseListener;
import com.nearbuy.mobilecore.utils.LogUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ankur Singh on 13/04/16.
 */
public class OkHTTPCallbackImpl implements Callback {
    private final String TAG = LogUtils.makeLogTag(OkHTTPCallbackImpl.class);
    private RequestTypes mRequestTypes;
    private WeakReference<ResponseListener> mWeakReference;
    private WeakReference<Activity> mActivityWeakReference;
    public OkHTTPCallbackImpl(RequestTypes pRequestTypes,
                              ResponseListener pResponseListener){
        mRequestTypes = pRequestTypes;
        mWeakReference = new WeakReference<>(pResponseListener);
        if(pResponseListener instanceof  Activity) {
            mActivityWeakReference = new WeakReference<>(((Activity)pResponseListener));
        }
        if(pResponseListener instanceof Fragment){
            mActivityWeakReference = new WeakReference<>((Activity)((Fragment)pResponseListener).getActivity());
        }
        if(pResponseListener instanceof android.app.Fragment){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mActivityWeakReference = new WeakReference<>(((android.app.Fragment)pResponseListener).getActivity());
            }
        }
    }
    @Override
    public void onFailure(Request request,final IOException e) {
        final ResponseListener rl = mWeakReference.get();
        if(rl!=null){
            if(mActivityWeakReference.get()!=null) {
                mActivityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl.onError(mRequestTypes, e);
                    }
                });
            }
        }
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        LogUtils.info(TAG, "onResponse");
        final ResponseListener rl = mWeakReference.get();
        if(rl!=null){
            InputStream inputStream=null;
            try {
                if (response.code() != HttpUtils.HTTP_OK_200) {
                    onFailure(response.request(), null);
                    return;
                }
                inputStream = response.body().byteStream();
                String contentEncoding = response.header(HttpUtils.CONTENT_ENCODING);
                // handling for gzip data
                if (contentEncoding != null && contentEncoding.equalsIgnoreCase(HttpUtils.GZIP)) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                RequestTypes tag = (RequestTypes) response.request().tag();
                LogUtils.info(TAG, "onResponse response tag " + tag);
                final Message message = new Message();
                boolean success = rl.parseResponse(mRequestTypes,message, inputStream);

                if(success && mActivityWeakReference.get()!=null) {
                    mActivityWeakReference.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl.onResponse(mRequestTypes, message.obj);
                        }
                    });
                }else {
                    rl.onResponse(mRequestTypes,response);
                }
            }catch(Exception e){
                LogUtils.error(TAG, e.toString());
            }finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }catch(IOException ioEx) {
                    }
                }
            }
        }

    }
}
