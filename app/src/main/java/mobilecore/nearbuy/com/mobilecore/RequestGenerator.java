package mobilecore.nearbuy.com.mobilecore;

import com.nearbuy.mobilecore.models.HttpRequest;
import com.nearbuy.mobilecore.models.Method;
import com.nearbuy.mobilecore.models.RequestTypes;
import com.nearbuy.mobilecore.models.ResponseListener;
import com.nearbuy.mobilecore.network.HttpUtils;
import com.nearbuy.mobilecore.network.OkHTTPCallbackImpl;
import com.nearbuy.mobilecore.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ankur Singh on 12/04/16.
 */
public class RequestGenerator {
    public final String TAG = getClass().getName();
    public final int REQUEST_TIME_OUT = 2 * 60 * 1000; //2min
    public static final Map<String,String> defaultHeaders = new HashMap<>();
    static {
        defaultHeaders.put("x","y");
    }



    public void getData(RequestTypes pRequestTag,Object params,ResponseListener pResponseListener){
        LogUtils.info(TAG, "request is " + pRequestTag);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setTimeout(REQUEST_TIME_OUT);
        httpRequest.setContentType(HttpUtils.APPLICATION_JSON);
        httpRequest.setRequestType(Method.GET);
        httpRequest.setHeaderKeyValue(new HashMap<>(defaultHeaders));//send a copy !
        httpRequest.setTag(pRequestTag);
        switch ((RequestTag)pRequestTag){
            case GET_REQUEST_SAMPLE:
                String url = "http://square.github.io/retrofit/";
                httpRequest.setURL(url);
                break;
        }
        HttpUtils.getInstance().createRequest(httpRequest,new OkHTTPCallbackImpl(pRequestTag,pResponseListener));
    }





   /* public void getData(int requestType, Object object, Callback callback) {

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setTimeout(REQUEST_TIME_OUT);
        httpRequest.setContentType(HttpUtils.APPLICATION_JSON);
        httpRequest.setRequestType(HttpUtils.REQUEST_TYPE_POST);
        httpRequest.setTag(requestType);
        switch (requestType){*/
}
