package com.nearbuy.mobilecore.network;

import android.os.Build;

import com.nearbuy.mobilecore.models.HttpRequest;
import com.nearbuy.mobilecore.models.Method;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.nearbuy.mobilecore.utils.LogUtils;

public class HttpUtils {
    private static final String TAG ="HttpUtils";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType PLAIN
            = MediaType.parse("text/plain; charset=utf-8");

    public static final String CONTENT_ENCODING ="Content-Encoding";
    public static final String GZIP ="gzip";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String ACCEPT_ENCODING ="Accept-Encoding";
    public static final String ACCEPT ="Accept";
    public static final String COOKIE = "Cookie";
    public static final String CONTENT_TYPE ="Content-type";

    public static final int HTTP_OK_200 = 200;
    public static final int HTTP_OK_100 = 100;

    public static final long LOW_PRIORITY_TIMEOUT = 30 * 1000; // 30 Seconds
    public static final long MEDIUM_PRIORITY_TIMEOUT = 60 * 1000; // 60 Seconds
    public static final long HIGH_PRIORITY_TIMEOUT = 120 * 1000; // 120 Seconds

    public static final long LOW_PRIORITY_CONNECT_TIMEOUT = 30 * 1000; // 30 Seconds
    public static final long MEDIUM_PRIORITY_CONNECT_TIMEOUT = 60 * 1000; // 60 Seconds
    public static final long HIGH_PRIORITY_CONNECT_TIMEOUT = 120 * 1000; // 120 Seconds

    public static final long LOW_PRIORITY_READ_TIMEOUT = 30 * 1000; // 30 Seconds
    public static final long MEDIUM_PRIORITY_READ_TIMEOUT = 60 * 1000; // 60 Seconds
    public static final long HIGH_PRIORITY_READ_TIMEOUT = 120 * 1000; // 120 Seconds

    public static final long LOW_PRIORITY_WRITE_TIME_OUT = 30 * 1000; // 30 Seconds
    public static final long MEDIUM_PRIORITY_WRITE_TIME_OUT = 60 * 1000; // 60 Seconds
    public static final long HIGH_PRIORITY_WRITE_TIME_OUT = 120 * 1000; // 120 Seconds

    public static final int REQUEST_TYPE_GET = 0;
    public static final int REQUEST_TYPE_POST = 1;
    public static final int REQUEST_TYPE_DELETE = 2;
    public static final int REQUEST_TYPE_PUT = 3;

    private static final int MAX_IDLE_CONNECTIONS = 5; //okhttp Default 5
    private static final long KEEP_ALIVE_DURATION_IN_MILLISECONDS = 5 * 60 * 1000; //okhttp Default 5 min

    private static HttpUtils sInstance;
    private OkHttpClient mOkHttpClient;
    private ConnectionPool mConnectionPool;
    private Hashtable<String, Call> mHashtableOkHttpCall;


    private HttpUtils() {
        LogUtils.enter(TAG, LogUtils.getMethodName());
        mOkHttpClient = new OkHttpClient();
        mConnectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION_IN_MILLISECONDS);
        mOkHttpClient.setConnectionPool(mConnectionPool);
        mHashtableOkHttpCall = new Hashtable<String, Call>();
        mOkHttpClient.getDispatcher().setMaxRequestsPerHost(8); //increasing from default 5
        setDefaultCookieHandler();

        LogUtils.exit(TAG, LogUtils.getMethodName());
    }

    public static synchronized HttpUtils getInstance() {
        if (sInstance == null) {
            sInstance = new HttpUtils();
        }
        return sInstance;
    }

    /**
     * Creating cookie manager and setting the cookie policy to okhttp client. It will
     * change default cookie manager.
     */
    public void setCookieHandler(CookieManager cookieManager) {
        mOkHttpClient.setCookieHandler(cookieManager);
    }

    /**
     * Cancels the request with the specified tag, if possible. R
     * Requests that are already complete cannot be canceled.
     * @param urlTag
     */
    public void cancelHttpRequest(String urlTag){
        if (mHashtableOkHttpCall != null) {
            final Call call = mHashtableOkHttpCall.get(urlTag);
            if (call != null && (false == call.isCanceled())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            call.cancel();
                        } catch (Exception e) {
                            // Ignore, not important
                        }
                    }
                }).start();
                mHashtableOkHttpCall.remove(urlTag);
            }
        }
    }

    /**
     * Add a http request to queque. This method can be used when we have multiple request in parallel.
     *
     * @param inRequest Request parameter of a request.
     * @param cb      Callback to notify success or failure of a request.
     */
    public void createRequest(HttpRequest inRequest, final Callback cb) {
        LogUtils.enter(TAG, LogUtils.getMethodName());
        initializeDefaultTimeOut();
        HttpRequest outRequest = initRequest();

        outRequest.setConnectTimeout(inRequest.getConnectTimeout());
        outRequest.setReadTimeout(inRequest.getReadTimeout());
        outRequest.setWriteTimeout(inRequest.getWriteTimeout());
        outRequest.setURL(inRequest.getURL());
        outRequest.setRequestType(inRequest.getRequestType());
        outRequest.setTag(inRequest.getTag());


        if (inRequest.getHeaderNamesAndValues() != null) {
            outRequest.setHeaderNamesAndValues(inRequest.getHeaderNamesAndValues());
        }
        if (inRequest.getHeaderKeyValue() != null) {
            outRequest.setHeaderKeyValue(inRequest.getHeaderKeyValue());
        }

        outRequest.setContentType(inRequest.getContentType());
        outRequest.setmExtraValue(inRequest.getmExtraValue());

        if(inRequest.getRequestBody() != null){
            outRequest.setRequestBody(inRequest.getRequestBody());
        } else {
            outRequest.setRequestBody(new JSONObject().toString());
        }

        createRequestInternal(outRequest, cb);
        LogUtils.exit(TAG, LogUtils.getMethodName());
    }

    /**
     * Add a http request to queque. It uses the request parameter supplied by setURL, setRequestType
     * etc method. This method should be call called when mini request parameters are set.     *
     *
     * @param cb Callback to notify success or failure of a request.
     */
    /**
     * Add a http request to queque. It uses the request parameter supplied by setURL, setRequestType
     * etc method. This method should be call called when mini request parameters are set.
     *
     *
     * @param outRequest
     * @param cb Callback to notify success or failure of a request.
     * @return Current http request reference. It can be used to cancel the current request.
     */
    private void createRequestInternal(HttpRequest outRequest, final Callback cb) {
        LogUtils.enter(TAG, LogUtils.getMethodName());

        //It will cancel the duplicate request. No duplicate request will be available in queque.
        try {
//            cancelHttpRequest(outRequest.getURL() + outRequest.getTag()); //TODO ankur
        } catch (Exception e) {
            // The cancellation of previous request can be ignored in case of exception
        }

        Request.Builder requestBuilder = new Request.Builder();
        setDefaultHeaders(requestBuilder);
        requestBuilder.url(outRequest.getURL());
        requestBuilder.tag(outRequest.getTag());

        if(outRequest.getHeaderNamesAndValues() != null){
            Headers headers = Headers.of(outRequest.getHeaderNamesAndValues());
            requestBuilder.headers(headers);
        }

        if (outRequest.getHeaderKeyValue()!= null) {
            for(String k : outRequest.getHeaderKeyValue().keySet()){
                String value = outRequest.getHeaderKeyValue().get(k);
                requestBuilder.addHeader(k,value);
            }

        }

        Request request = null;
        Call call = null;
        OkHttpClient httpClient = null;
        setRequestData(outRequest, requestBuilder);

        try {
            request = requestBuilder.build();
            if (Thread.interrupted()) {
                return;
            }

            synchronized (this) {
                httpClient = mOkHttpClient.clone();
            }

            httpClient.setConnectTimeout(outRequest.getConnectTimeout(), TimeUnit.MILLISECONDS);
            httpClient.setReadTimeout(outRequest.getReadTimeout(), TimeUnit.MILLISECONDS);
            httpClient.setWriteTimeout(outRequest.getWriteTimeout(), TimeUnit.MILLISECONDS);
            //change for latency tracking
            Map mExtraMap = outRequest.getmExtraValue();


            call = httpClient.newCall(request);
//            mHashtableOkHttpCall.put(outRequest.getURL() + outRequest.getTag(), call); //todo implement cancellation loggic

            final String urlTag = outRequest.getURL()/* + outRequest.getTag()*/;//todo ankur see impl
//            if (null != call) {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, final IOException exception) {
                        LogUtils.enter(TAG, LogUtils.getMethodName());

                        mHashtableOkHttpCall.remove(urlTag);
                        if (Thread.interrupted()) {
                            LogUtils.info(TAG, "Thread is interrupted");
                            return;
                        }
                        if (cb != null) {
                            cb.onFailure(request, exception);
                        }
                        LogUtils.exit(TAG, LogUtils.getMethodName());
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        LogUtils.enter(TAG, LogUtils.getMethodName());

                        mHashtableOkHttpCall.remove(urlTag);
                        if (Thread.interrupted()) {
                            LogUtils.info(TAG, "Thread is interrupted");
                            return;
                        }
                        if (cb != null) {
                            cb.onResponse(response);
                        }
                        LogUtils.exit(TAG, LogUtils.getMethodName());
                    }
                });
//            }
        } catch (Exception ex) {
            LogUtils.error(TAG, ex.toString());
        }

        LogUtils.exit(TAG, LogUtils.getMethodName());
    }

    /**
     * Close and remove all connections in the pool.
     */
    public void closeAllConnections() {
        mConnectionPool.evictAll();
    }

    /**
     * Sets the http request type to request builder.
     */
    private void setRequestData(HttpRequest outRequest, Request.Builder requestBuilder) {
        RequestBody requestBody;
        switch (outRequest.getRequestType()) {

            /* GET method is used to retrieve information from the given server using a given URI.
            * Requests using GET should only retrieve data and should have no other effect on the data.
             */
            case GET:
                requestBuilder.get();
                break;

            /* A POST request is used to send data to the server,
            * for example customer information, file upload etc using HTML forms.
             */
            case POST:
                requestBody = RequestBody.create(MediaType.parse(outRequest.getContentType()), outRequest.getRequestBody());
                requestBuilder.post(requestBody);
                break;

            /* Remove all current representations of the target resource given by URI. */
            case DELETE:
                requestBuilder.delete();
                break;

            /* Replace all current representations of the target resource with the uploaded content.
             */
            case PUT:
                requestBody = RequestBody.create(MediaType.parse(outRequest.getContentType()), outRequest.getRequestBody());
                requestBuilder.put(requestBody);
                break;

            default:
                requestBuilder.get();
                break;
        }
    }

    /**
     * Sets all default header for which are required for all requests
     */
    private void setDefaultHeaders(Request.Builder requestBuilder) {
        final String DEVICE_OS = "os";
        final String APP_VERSION = "ver";
        final String VISITOR_ID = "vid";
        final String TID="tid";
        final String COOKIE = "cookie";

        requestBuilder.addHeader(ACCEPT_ENCODING, GZIP);
        requestBuilder.addHeader(DEVICE_OS, "Android " + Build.VERSION.RELEASE);
//        requestBuilder.addHeader(APP_VERSION, AppUtils.getInstance().getAppVersionName());

    }

    /**
     * Sets the default cookie setting all requests
     */
    private void setDefaultCookieHandler() {
        final String JSESSIONID = "JSESSIONID";
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(new CookiePolicy() {
            @Override
            public boolean shouldAccept(URI uri, HttpCookie cookie) {
                if (cookie.getName().equalsIgnoreCase(JSESSIONID)) {
                    return false;
                } else {
                    return true;
                }
            }
        });
        mOkHttpClient.setCookieHandler(cookieManager);
    }

    private void initializeDefaultTimeOut() {
        mOkHttpClient.setConnectTimeout(LOW_PRIORITY_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        mOkHttpClient.setReadTimeout(LOW_PRIORITY_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        mOkHttpClient.setWriteTimeout(LOW_PRIORITY_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private HttpRequest initRequest(){
        HttpRequest request = new HttpRequest();
        request.setURL(null);
        request.setRequestType(Method.GET);
        request.setContentType(APPLICATION_JSON);
        request.setRequestBody(null);
        request.setConnectTimeout(LOW_PRIORITY_CONNECT_TIMEOUT);
        request.setReadTimeout(LOW_PRIORITY_READ_TIMEOUT);
        request.setWriteTimeout(LOW_PRIORITY_WRITE_TIME_OUT);

        request.setHeaderNamesAndValues(null);
        request.setHeaderKeyValue(null);

        return request;
    }
}

