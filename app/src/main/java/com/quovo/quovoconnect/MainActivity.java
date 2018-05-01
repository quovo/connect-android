package com.quovo.quovoconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.quovo.sdk.QuovoConnectSdk;
import com.quovo.sdk.listeners.OnCompleteListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button mConnectQuovo;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnectQuovo = (Button) findViewById(R.id.btn_quovo);

        QuovoConnectSdk.Builder quovoConnectSdk = new QuovoConnectSdk.Builder(this);
        quovoConnectSdk.customTitle("Connect your accounts");
        quovoConnectSdk.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(String callback, String response) {
                Log.d("callback", callback);
                Log.d("response", response);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mConnectQuovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mConnectQuovo.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                String apiToken = "0407577204ebb2ec5cd43da385ad57237abc5aff22f7dfa6b92be9b1291f7cdf";
                int userId = 4082606;

                new UserTokenAsyncTask(new UserTokenListener() {
                    @Override
                    public void onUserToken(String userToken) {

                        HashMap<String, Object> options = new HashMap<>();
                        options.put("syncType", "auth");
                        options.put("searchTest", 1);
                        mConnectQuovo.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        quovoConnectSdk.launch(userToken, options);
                    }

                    @Override
                    public void onError() {
                        mConnectQuovo.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }).execute(apiToken, String.valueOf(userId));

            }
        });

    }

    public interface UserTokenListener {
        void onUserToken(String userToken);

        void onError();
    }

}
