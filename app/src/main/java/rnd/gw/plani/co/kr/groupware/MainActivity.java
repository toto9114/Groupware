package rnd.gw.plani.co.kr.groupware;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class MainActivity extends AppCompatActivity
        implements MenuFragment.OnMenuItemSelectedListener {

    ImageView splashView;

    private String HOME_URL = "";
    private String deviceId = "";
    private String appId = "";
    private String userName = "";
    private static final String ANDROID = "android";

    MaterialMenuDrawable materialMenu;
    DrawerLayout drawerLayout;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("i-Smart Groupware");

        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
        }

        HOME_URL = PropertyManager.getInstance().getDomain();
        if (!HOME_URL.contains("http://")) {
            HOME_URL = "http://" + HOME_URL; //저장된 호스트주소 가져옴 ex)http://gw.plani.co.kr
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
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NotifyFragment())
                    .commit();
        }

        try {
//                    userName = result.getElementsByClass("desk_name").text();
            appId = PropertyManager.getInstance().getAppId();
            if (new updateDeviceId().execute().get()) { //사용자 등록이 제대로 완료되면
                PropertyManager.getInstance().setUser(true);
            } else {
                Toast.makeText(MainActivity.this, "디바이스 등록이 실패했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
    }

    public String getUserName() {
        if (!TextUtils.isEmpty(userName))
            return userName;
        else {
            return "";
        }
    }

    boolean isMenuSelect = false;

    @Override
    public void onMenuItemSelected(@MenuFragment.MenuMode int menuId) {//네비게이션 메뉴 선택시
        switch (menuId) {
            case MenuFragment.MENU_ID_MAIN: //뉴스피드 페이지
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new NotifyFragment())
                        .commit();
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_PC_VERSION: //pc버전보기
                startActivity(new Intent(MainActivity.this, PcVersionActivity.class));
                isMenuSelect = true;
                break;
            case MenuFragment.MENU_ID_LOGOUT:  //로그아웃
                LogoutDialog dialog = new LogoutDialog();
                dialog.show(getSupportFragmentManager(),"logout");
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
        protected void onPreExecute() {
            super.onPreExecute();
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
        }
    }
    boolean isBackPressed = false;
    public static final int MESSAGE_BACK_KEY_TIMEOUT =0;
    public static final int BACK_KEY_TIME = 2000;
    Handler mHandler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_BACK_KEY_TIMEOUT:
                    isBackPressed = false;
                    return true;
            }
            return false;
        }
    });
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!isBackPressed){
                Toast.makeText(this,R.string.back_pressed_message,Toast.LENGTH_SHORT).show();
                isBackPressed = true;
                mHandler.sendEmptyMessageDelayed(MESSAGE_BACK_KEY_TIMEOUT, BACK_KEY_TIME);
            }
            else {
                mHandler.removeMessages(MESSAGE_BACK_KEY_TIMEOUT);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
