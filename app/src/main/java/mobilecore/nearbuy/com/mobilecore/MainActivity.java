package mobilecore.nearbuy.com.mobilecore;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nearbuy.mobilecore.models.RequestTypes;
import com.nearbuy.mobilecore.models.ResponseListener;
import com.nearbuy.mobilecore.utils.AppUtils;
import com.nearbuy.mobilecore.utils.LogUtils;
import com.squareup.okhttp.Response;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements ResponseListener {
    private static final String TAG = "MainActivity";
    RequestGenerator mRequestGenerator ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mRequestGenerator = new RequestGenerator();
        mRequestGenerator.getData(RequestTag.GET_REQUEST_SAMPLE,null,this);
    }

    @Override
    public boolean parseResponse(RequestTypes pRequestTag,Message pMessage, InputStream inputStream) {
        pMessage.obj = AppUtils.convertStreamToString(inputStream);
        return true;
    }

    @Override
    public void onResponse(RequestTypes pRequestTag, Object pResponse) {
        LogUtils.info(TAG,(String)pResponse);
        ((TextView)findViewById(R.id.text)).setText((String)pResponse);
    }

    @Override
    public void onError(RequestTypes pRequestTag, Exception error) {
        LogUtils.info(TAG,error.toString());
    }
}
