package rnd.gw.plani.co.kr.groupware;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;

import net.htmlparser.jericho.FormField;
import net.htmlparser.jericho.FormFields;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class MainActivity extends AppCompatActivity
        implements MenuFragment.OnMenuItemSelectedListener {

    MyWebView mWebView;
    ImageView splashView;
    TabLayout tabLayout;
    public static final String EXTRA_URL = "url";

    private String HOME_URL = "";
    private String deviceId = "";
    private String appId = "";
    private static final String ANDROID = "android";

    private static final String MAIN_URL = "http://gw.plani.co.kr/main";
    private static final String RECIEVE_URL = "http://gw.plani.co.kr/reception";
    private static final String SEND_URL = "http://gw.plani.co.kr/transmission";
    private static final String SEND_CONTACT_URL = "http://gw.plani.co.kr/send";
    private static final String UNDECIDE_URL = "http://gw.plani.co.kr/undecided";
    private static final String RESERVATION_URL = "http://gw.plani.co.kr/vehicle";
    private static final String FEEDS_URL = "http://gw.plani.co.kr/feeds";

    private AndroidWebInterface mWebInterface;
    MaterialMenuDrawable materialMenu;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    AppBarLayout appBarLayout;
    FrameLayout frameLayout;

    CookieManager cookieManager;

    boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("플랜아이");

        HOME_URL = PropertyManager.getInstance().getDomain();
        if (!HOME_URL.contains("http://")) {
            HOME_URL = "http://" + HOME_URL; //스플래시 화면으로부터 호스트주소 가져옴
        }

        cookieManager = new CookieManager(new PersistentCookieStore(this), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        frameLayout = (FrameLayout) findViewById(R.id.subToolbar);
        mWebView = (MyWebView) findViewById(R.id.webView);
        splashView = (ImageView) findViewById(R.id.image_splash);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(SEND_CONTACT_URL);
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up);
                mWebView.startAnimation(anim);
            }
        });

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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tabLayout.addTab(tabLayout.newTab().setText("수신함"));
        tabLayout.addTab(tabLayout.newTab().setText("송신함"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mWebView.loadUrl(RECIEVE_URL);
                } else {
                    mWebView.loadUrl(SEND_URL);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        PropertyManager.getInstance().setBadgeCount(0);

        deviceId = PropertyManager.getInstance().getRegistrationToken();

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        mWebInterface = new AndroidWebInterface(this, mWebView);
        mWebView.addJavascriptInterface(mWebInterface, "Android");
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

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
        appBarLayout.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        Button btn = (Button) findViewById(R.id.btn_get);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getFormData().execute(mWebView.getUrl());
            }
        });
//        try {
//            new getFormData().execute("http://gw.plani.co.kr");
//            if (new doLogin().execute().get()) { //로그인이 성공하면
//                if (new updateDeviceId().execute().get()) { //사용자 등록이 제대로 완료되면
//                    mWebView.loadUrl(HOME_URL);
//
//                    Toast.makeText(MainActivity.this, "update", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "update fail", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(MainActivity.this, "login fail", Toast.LENGTH_SHORT).show();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        try {
            if (new login().execute().get()) {
                if (new updateDeviceId().execute().get()) { //사용자 등록이 제대로 완료되면
                    mWebView.loadUrl(HOME_URL);
                    Toast.makeText(MainActivity.this, "update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "update fail", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "login fail", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        new updateDeviceId().execute();
        //mWebView.loadUrl(HOME_URL);
        mWebView.setWebViewClient(new MyWebViewCient());
    }

    private class login extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String id = PropertyManager.getInstance().getUserId();
            String password = PropertyManager.getInstance().getPassword();
            DefaultHttpClient httpClient = new DefaultHttpClient();
//            CookieStore cookieStore = httpClient.getCookieStore();
            try {
                ArrayList<NameValuePair> nameValuePairs =
                        new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userid", id));
                nameValuePairs.add(new BasicNameValuePair("passwd", password));
                HttpParams param = httpClient.getParams();

                HttpConnectionParams.setConnectionTimeout(param, 5000);
                HttpConnectionParams.setSoTimeout(param, 5000);

                HttpPost httpPost = new HttpPost(DO_LOGIN_URL);
                UrlEncodedFormEntity entityRequest =
                        new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

                httpPost.setEntity(entityRequest);
                HttpResponse responsePost = httpClient.execute(httpPost);

                List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                if (cookies.isEmpty()) {
                    Log.i("MainActivity", "None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        if (cookies.get(i).getName().equals("app_id")) {
                            Log.i("MainActivity", "app_id=" + cookies.get(i).getValue());
//                            BasicClientCookie cookie = new BasicClientCookie("app_id",cookies.get(i).getValue());
//                            cookieStore.addCookie(cookie);
//                            cookie.setDomain("gw.plani.co.kr");
//                            cookie.setPath("/");
                            appId = cookies.get(i).getValue();
                        }
                    }
                }
                HttpEntity resEntity = responsePost.getEntity();
                httpClient.getConnectionManager().shutdown();
                return true;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static final String DO_LOGIN_URL = "http://gw.plani.co.kr/login/accounts/do_login/redirect/eNortjK0UtJXsgZcMAkSAcc.";

    private class doLogin extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String id = PropertyManager.getInstance().getUserId();
                String password = PropertyManager.getInstance().getPassword();

                URL url = new URL(DO_LOGIN_URL);
                String param = "userid=" + id + "&" + "passwd=" + password;

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(param.getBytes("UTF-8"));
                int code = conn.getResponseCode();
                String cookie = "";
                if (code >= HttpURLConnection.HTTP_OK && code < HttpURLConnection.HTTP_MULT_CHOICE) {
                    List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
                    if (cookies != null) {
                        for (String c : cookies) {
                            Log.i("MainActivity", c.split(";\\s*")[0]);
                        }
                    }
                }
                Log.i("MainAcitivity", cookie);

                PropertyManager.getInstance().setUserId(id);
                PropertyManager.getInstance().setPassword(password);
                PropertyManager.getInstance().setUser(true);
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class getFormData extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                Source source = new Source(new URL("http://gw.plani.co.kr"));
                source.fullSequentialParse();
                FormFields formFields = source.getFormFields();
                for (FormField formField : formFields) {
                    if (!formField.getValues().isEmpty())
                        builder.append(formField.getName() + "=" + formField.getValues().get(0) + "\n");
                    else
                        builder.append(formField.getName() + "=\"\"\n");
                }
                Log.i("MainActivity", builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    boolean isMenuSelect = false;

    @Override
    public void onMenuItemSelected(@MenuFragment.MenuMode int menuId) {//네비게이션 메뉴 선택시
        tabLayout.setVisibility(View.GONE);
        switch (menuId) {
            case MenuFragment.MENU_ID_MAIN:
                mWebView.loadUrl(MAIN_URL);
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_CONTACT:
                mWebView.loadUrl(RECIEVE_URL);
                tabLayout.setVisibility(View.VISIBLE);
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_UNDECIDE:
                mWebView.loadUrl(UNDECIDE_URL);
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_RESERVATION:
                mWebView.loadUrl(RESERVATION_URL);
                isMenuSelect = true;
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private static final String SERVER_URL = "http://gw.plani.co.kr/app/android/index";
    private static final String METHOD2 = "UpdateDeviceID";

    class updateDeviceId extends AsyncTask<String, Integer, Boolean> { //사용자등록
        boolean isUpdate = false;

        @Override
        protected Boolean doInBackground(String... params) {
            XMLRPCClient client = new XMLRPCClient(SERVER_URL);
            try {
                //appId = getAppId(HOME_URL);
                HashMap<String, Boolean> result = (HashMap<String, Boolean>) client.call(METHOD2, appId, deviceId, ANDROID);
                Log.i("MainActivity", appId + "\n" + deviceId);
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

        }
    }


    protected String getAppId(String url) { //사용자 app_id 쿠키에서 빼내오기
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
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

    boolean isLoading = false; //페이지 로딩중인지 판단
    private static final String LOGIN_URL = "/login/accounts/login/redirect/eNortjK0UtJXsgZcMAkSAcc."; //로그인 페이지
    MyProgressDialog dialog = new MyProgressDialog();

    private class MyWebViewCient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            if (!dialog.isAdded())
//                dialog.show(getSupportFragmentManager(), "dialog");
            isLoading = true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isLoading = false;
            Log.i("MainAcitivity", url);
            //dialog.dismiss();
            if (isMenuSelect) {
                mWebView.clearHistory();
                isMenuSelect = false;
            }
//            appId = getAppId(url);
            if (!isUpdate) {
                new updateDeviceId().execute();
                isUpdate = true;
            }
            //splashView.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(HOME_URL + LOGIN_URL)) {//로그인페이지일때 툴바 보이지 않게
                appBarLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else {
                appBarLayout.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
            Log.i("MainActivity", url);
            if (Uri.parse(url).getHost().equals("gw.plani.co.kr")) {
                // This is my web site, so do not override; let my WebView load the page
                if (url.equals("http://gw.plani.co.kr/")) {
//                    new updateDeviceId().execute();
                    Log.i("MainActivity", "test");
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
        if (isLoading) { //로딩중에 back키 먹히지 않게
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
        if (id == R.id.notify) { //전체알림보기
            mWebView.loadUrl(FEEDS_URL);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
