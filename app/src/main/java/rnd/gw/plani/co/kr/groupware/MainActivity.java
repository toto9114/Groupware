package rnd.gw.plani.co.kr.groupware;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;

import org.apache.http.HttpRequest;
import org.jsoup.nodes.Element;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class MainActivity extends AppCompatActivity
        implements MenuFragment.OnMenuItemSelectedListener {

    MyWebView mWebView;
    ImageView splashView;

    public static final String EXTRA_URL = "url";

    private String HOME_URL = "";
    private String deviceId = "";
    private String appId = "";
    private static final String ANDROID = "android";

//    private static final String MAIN_URL = "http://gw.plani.co.kr/main";

    //    private static final String SEND_CONTACT_URL = "http://gw.plani.co.kr/send";

    //    private AndroidWebInterface mWebInterface;
    MaterialMenuDrawable materialMenu;
    DrawerLayout drawerLayout;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("플랜아이");

        HOME_URL = PropertyManager.getInstance().getDomain();
        if (!HOME_URL.contains("http://")) {
            HOME_URL = "http://" + HOME_URL; //저장된 호스트주소 가져옴
        }
        String id = PropertyManager.getInstance().getUserId();
        String password = PropertyManager.getInstance().getPassword();

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        splashView = (ImageView) findViewById(R.id.image_splash);

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

        PropertyManager.getInstance().setBadgeCount(0);

        deviceId = PropertyManager.getInstance().getRegistrationToken();
        appBarLayout.setVisibility(View.VISIBLE);

        NetworkManager.getInstance().login(this,HOME_URL, id, password, new NetworkManager.OnResultListener<Element>() {
            @Override
            public void onSuccess(HttpRequest request, Element result) {
                try {
                    appId = PropertyManager.getInstance().getAppId();
                    if (new updateDeviceId().execute().get()) { //사용자 등록이 제대로 완료되면
                        PropertyManager.getInstance().setUser(true);
                        Toast.makeText(MainActivity.this, "update", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "update fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {

            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,new NotifyFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
//        cookieManager.setAcceptCookie(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        }else{
            CookieManager.getInstance().removeAllCookie();
        }
    }

    boolean isMenuSelect = false;
    @Override
    public void onMenuItemSelected(@MenuFragment.MenuMode int menuId) {//네비게이션 메뉴 선택시
        switch (menuId) {
            case MenuFragment.MENU_ID_MAIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new NotifyFragment())
                        .commit();
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_CONTACT:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new EmployeeFragment())
                        .addToBackStack(null)
                        .commit();
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_UNDECIDE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("로그아웃");
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PropertyManager.getInstance().setUser(false);
                        Intent i = new Intent(MainActivity.this,SplashActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_RESERVATION:
                startActivity(new Intent(MainActivity.this,PcVersionActivity.class));
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
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("디바이스 등록중..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            XMLRPCClient client = new XMLRPCClient(SERVER_URL);
            try {
                HashMap<String, Boolean> result = (HashMap<String, Boolean>) client.call(METHOD2, appId, deviceId, ANDROID);
                if (!result.isEmpty()) {
                    isUpdate = result.get("result").booleanValue();
                } else {
                    return false;
                }
            } catch (XMLRPCException e) {
                e.printStackTrace();
            }
            return isUpdate;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
        }
    }

    boolean isLoading = false; //페이지 로딩중인지 판단
    private static final String LOGIN_URL = "/login/accounts/login/redirect/eNortjK0UtJXsgZcMAkSAcc."; //로그인 페이지
    MyProgressDialog dialog = new MyProgressDialog();
//    private class getContactSection extends AsyncTask<String, Integer, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            Document doc = null;
//            try {
//                doc = Jsoup.connect(params[0]).get();
//                org.jsoup.nodes.Element element = doc.select("section#content").get(0);
//                return element.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

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
//        getMenuInflater().inflate(R.menu.menu_notify, menu);
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
//        if (id == R.id.notify) { //전체알림보기
//            mWebView.loadUrl(FEEDS_URL);
//            startActivity(new Intent(this, NotifyActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
