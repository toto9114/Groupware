package rnd.gw.plani.co.kr.groupware;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.util.HashMap;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class MainActivity extends AppCompatActivity {

    MyWebView mWebView;
    ImageView splashView;
    TabLayout tabLayout;
    private static final String HOME_URL = "http://gw.plani.co.kr";

    private AndroidWebInterface mWebInterface;


    private String deviceId = "";
    private String appId = "";
    private static final String ANDROID = "android";

    private static final String MAIN_URL = "http://gw.plani.co.kr/main";
    private static final String RECIEVE_URL = "http://gw.plani.co.kr/reception";
    private static final String SEND_URL = "http://gw.plani.co.kr/send";
    private static final String UNDECIDE_URL = "http://gw.plani.co.kr/undecided";
    private static final String RESERVATION_URL = "http://gw.plani.co.kr/vehicle";
    private static final String FEEDS_URL = "http://gw.plani.co.kr/feeds";

    MaterialMenuDrawable materialMenu;
    FloatingActionButton fab;
    boolean isNotiClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("플랜아이");

        mWebView = (MyWebView) findViewById(R.id.webView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("http://gw.plani.co.kr/send");
                fab.setVisibility(View.GONE);
            }
        });
//        View customBar = getLayoutInflater().inflate(R.layout.view_center_toolbar, null);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(customBar,
//                new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (materialMenu.getIconState() == MaterialMenuDrawable.IconState.BURGER) {
                    materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                } else {
                    materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
                }
            }
        });
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon(materialMenu);
        PropertyManager.getInstance().setBadgeCount(0);

        deviceId = PropertyManager.getInstance().getRegistrationToken();


        splashView = (ImageView) findViewById(R.id.image_splash);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.addTab(tabLayout.newTab().setText("홈"));
        tabLayout.addTab(tabLayout.newTab().setText("업무연락"));
        tabLayout.addTab(tabLayout.newTab().setText("미결함"));
        tabLayout.addTab(tabLayout.newTab().setText("회의실예약"));
        if (savedInstanceState == null) {

        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fab.setVisibility(View.GONE);
                switch (tab.getPosition()) {
                    case 0:
                        mWebView.loadUrl(MAIN_URL);
                        break;
                    case 1:
                        mWebView.loadUrl(RECIEVE_URL);
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mWebView.loadUrl(UNDECIDE_URL);
                        break;
                    case 3:
                        mWebView.loadUrl(RESERVATION_URL);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mWebInterface = new AndroidWebInterface(this, mWebView);
        mWebView.addJavascriptInterface(mWebInterface, "Android");

        mWebView.setWebChromeClient
                (
                        new WebChromeClient() {
                            //alter process
                            @Override
                            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Alert")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.ok,
                                                new AlertDialog.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        result.confirm();
                                                    }
                                                })
                                        .setCancelable(false)
                                        .create()
                                        .show();

                                return true;
                            }

                            ;

                            //confirm process
                            @Override
                            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Confirm")
                                        .setMessage(message)
                                        .setPositiveButton("네",
                                                new AlertDialog.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        result.confirm();
                                                    }
                                                })
                                        .setNegativeButton("아니오",
                                                new AlertDialog.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        result.cancel();
                                                    }
                                                })
                                        .setCancelable(false)
                                        .create()
                                        .show();

                                return true;
                            }
                        }
                );
        new checkHosting().execute(URL_PARAM);
        appId = getAppId(HOME_URL);
        mWebView.setWebViewClient(new MyWebViewCient());

    }

    private static final String URL_PARAM = "gw.plani.co.kr";

    private static final String SERVER_URL = "http://gw.plani.co.kr/app/android/index";
    private static final String METHOD1 = "CheckHosting";
    private static final String METHOD2 = "UpdateDeviceID";

    class checkHosting extends AsyncTask<String, Integer, Boolean> {
        boolean isConn = false;

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
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            super.onPostExecute(aBoolean);
            if (isConn) {
                mWebView.loadUrl(HOME_URL);
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        }

    }


    class updateDeviceId extends AsyncTask<String, Integer, Boolean> {
        boolean isUpdate = false;

        @Override
        protected Boolean doInBackground(String... params) {
            XMLRPCClient client = new XMLRPCClient(SERVER_URL);
            try {
                HashMap<String, Boolean> result = (HashMap<String, Boolean>) client.call(METHOD2, appId, deviceId, ANDROID);
                if (!result.isEmpty()) {
                    isUpdate = result.get("result").booleanValue();
                }
            } catch (XMLRPCException e) {
                e.printStackTrace();
            }
            return isUpdate;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isUpdate) {
                Toast.makeText(MainActivity.this, "update ," + "deviceId : " + deviceId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "update fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected String getAppId(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(url);
        String temp[] = cookie.split(";");
        for (String s : temp) {
            String temp1[] = s.split("=");
            if (temp1[0].equals(" app_id")) {
                return temp1[1];
            }
        }
        return null;
    }

    private class MyWebViewCient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            splashView.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("MainActivity", url);
            if (Uri.parse(url).getHost().equals("gw.plani.co.kr")) {
                // This is my web site, so do not override; let my WebView load the page
                if (url.equals("http://gw.plani.co.kr/")) {
                    new checkHosting().execute(HOME_URL);
                    new updateDeviceId().execute();
                }
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_finish)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.notify) {
            mWebView.loadUrl("http://gw.plani.co.kr/feeds");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
