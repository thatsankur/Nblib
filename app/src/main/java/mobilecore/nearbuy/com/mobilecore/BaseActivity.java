package mobilecore.nearbuy.com.mobilecore;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.nearbuy.mobilecore.network.HttpUtils;
import com.nearbuy.mobilecore.utils.LogUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.zip.GZIPInputStream;

public abstract class BaseActivity extends AppCompatActivity implements Callback {
    private final String TAG = LogUtils.makeLogTag(BaseActivity.class);
    protected final int DATA_FETCHED = 0;
    protected final int DATA_NOT_FETCHED = 1;
    protected final int CONNECTION_ERROR = 2;
    protected ResponseHandler mResponseHandler;

    abstract public void createHttpRequest(int requestType, Object object);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResponseHandler = new ResponseHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
    }

    @Override
    public void onBackPressed() {
        if(!isFinishing()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed()) {
               return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        LogUtils.error(TAG, "onFailure");
        Message message = new Message();
        int tag = (int) request.tag();
        LogUtils.info(TAG, "onFailure response tag " + tag);
        message.arg1 = tag;
        message.arg2 = CONNECTION_ERROR;
        mResponseHandler.sendMessage(message);
        LogUtils.error(TAG, e.toString());
    }

    @Override
    public void onResponse(Response response) throws IOException {
        LogUtils.info(TAG, "onResponse");
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
            int tag = (int) response.request().tag();
            LogUtils.info(TAG, "onResponse response tag " + tag);
            Message message = new Message();
            message.arg1 = tag;
            boolean success = onHttpResponseProcessData(message, inputStream);
            if (success) {
                message.arg2 = DATA_FETCHED;
            } else {
                message.arg2 = DATA_NOT_FETCHED;
            }
            mResponseHandler.sendMessage(message);
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

    private void clearReferences(){

    }

    /**
     * Method to handle  Http response
     *
     * @param msg
     * @param inputStream
     */
    protected abstract boolean onHttpResponseProcessData(Message msg, InputStream inputStream);

    /**
     * Method for handling  UI update
     *
     * @param message
     */
    protected abstract void onHttpResponseUpdateUI(Message message);

    /**
     * Inner handler class to pass message to UI thread
     */
    protected static class ResponseHandler extends Handler {
        private final String TAG = LogUtils.makeLogTag(ResponseHandler.class);
        private final WeakReference<BaseActivity> mActivity;

        ResponseHandler(BaseActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LogUtils.info(TAG, "handleMessage");
            super.handleMessage(msg);

            if(mActivity != null && mActivity.get() != null && !mActivity.get().isFinishing()){
                mActivity.get().onHttpResponseUpdateUI(msg);
            }
         }
    }
}
