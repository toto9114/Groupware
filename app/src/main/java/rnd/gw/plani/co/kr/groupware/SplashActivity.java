package rnd.gw.plani.co.kr.groupware;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.util.HashMap;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;
import rnd.gw.plani.co.kr.groupware.GCM.RegistrationIntentService;

public class SplashActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    EditText editDomain;
    Button domainView;
    private static final String SERVER_URL = "http://gw.plani.co.kr/app/android/index";
    private static final String METHOD1 = "CheckHosting";

    boolean isUser;
    private String domain = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        editDomain = (EditText) findViewById(R.id.edit_domain);
        domainView = (Button) findViewById(R.id.btn_domain);
        ConnectivityManager manager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(this, activeNetwork.getTypeName()+"에 연결되었습니다", Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(this, activeNetwork.getTypeName()+"에 연결되었습니다", Toast.LENGTH_SHORT).show();
            }
        } else {
            // not connected to the internet
            Toast.makeText(SplashActivity.this, "인터넷에 연결되지 않았습니다. 연결 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button btn = (Button) findViewById(R.id.btn_regist);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                domain = editDomain.getText().toString();
                new checkHosting().execute(domain);
                editDomain.setText("");
            }
        });

        if (!TextUtils.isEmpty(PropertyManager.getInstance().getDomain())) {
            domainView.setText(PropertyManager.getInstance().getDomain());
        }

        domainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new checkHosting().execute(domainView.getText().toString());
            }
        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                doRealStart();
            }
        };
        setUpIfNeeded();
    }

    private static final String LOGIN_URL = "http://gw.plani.co.kr/login/accounts/do_login/redirect/eNortjK0UtJXsgZcMAkSAcc.";

    LoginDialog dialog;
    MyProgressDialog progressDialog;
    class checkHosting extends AsyncTask<String, Integer, Boolean> {
        boolean isConn = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new MyProgressDialog();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(),"progress");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            XMLRPCClient client = new XMLRPCClient(SERVER_URL);
            try {
                if (params[0].contains("http://")) {
                    StringBuilder sb = new StringBuilder(params[0]);
                    params[0] = sb.substring(7);
                }
                HashMap<String, Boolean> result = (HashMap<String, Boolean>) client.call(METHOD1, params[0]);
                if (!result.isEmpty()) {
                    isConn = result.get("result").booleanValue();
                }
            } catch (XMLRPCException e) {
                e.printStackTrace();
            }
            return isConn;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (isConn) {
                if (!TextUtils.isEmpty(domain)) {
                    domainView.setText(domain);
                }
                PropertyManager.getInstance().setDomain(domainView.getText().toString());
                Toast.makeText(SplashActivity.this, "도메인이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                dialog = new LoginDialog();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "dialog");
                //로그인 다이얼로그 띄우고
            } else {
                Toast.makeText(SplashActivity.this, "잘못된 도메인입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void doRealStart() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String registrationToken = PropertyManager.getInstance().getRegistrationToken();

                isUser = PropertyManager.getInstance().isUser();

                if (isUser) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
                return null;
            }
        }.execute();
    }

    private void setUpIfNeeded() {
        if (checkPlayServices()) {
            String regId = PropertyManager.getInstance().getRegistrationToken();
            if (!regId.equals("")) {
                doRealStart();
            } else {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST &&
                resultCode == Activity.RESULT_OK) {
            setUpIfNeeded();
        }
    }
}
