package rnd.gw.plani.co.kr.groupware;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "loadUrl";
    public static final String EXTRA_CATEGORY = "category";
    private String url;
    private String category;
    String homeUrl = "";
    String tableId = "";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.toolBar));
        }
        homeUrl = PropertyManager.getInstance().getDomain();
        url = getIntent().getStringExtra(EXTRA_URL);
        category = getIntent().getStringExtra(EXTRA_CATEGORY);

        if (savedInstanceState == null) {
            Fragment f = new WebContentFragment();
            Bundle args = new Bundle();
            args.putString(WebContentFragment.EXTRA_URL, url);
            args.putString(WebContentFragment.EXTRA_CATEGORY,category);
            f.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }

    public void changeReply() {
        toolbar.setVisibility(View.VISIBLE);
        Fragment f = new ReplyFragment();
        Bundle args = new Bundle();
        args.putString(ReplyFragment.EXTRA_TABLE_ID, tableId);
        args.putString(ReplyFragment.EXTRA_CATEGORY, category);
        f.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,R.anim.slide_down)
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    public void setTableid(String id) {
        if (!TextUtils.isEmpty(id)) {
            tableId = id;
        }
    }
}
