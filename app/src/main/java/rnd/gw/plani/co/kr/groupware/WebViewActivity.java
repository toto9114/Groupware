package rnd.gw.plani.co.kr.groupware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.htmlparser.jericho.HTMLElementName;

import org.apache.http.HttpRequest;
import org.jsoup.nodes.Element;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class WebViewActivity extends AppCompatActivity {

    MyWebView webView;
    LinearLayout linearLayout;
    EditText replyView;
    public static final String EXTRA_URL = "loadUrl";

    private String url;

    String sendUrl="";
    String homeUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = (MyWebView) findViewById(R.id.webView);
        replyView = (EditText) findViewById(R.id.edit_reply);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        } else {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        int height = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            height = getResources().getDimensionPixelSize(resourceId);
//        }
//        float density = getResources().getDisplayMetrics().density;
//        int paddingDp = (int) (height * density);
//
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
//        params.topMargin = height;
        homeUrl = PropertyManager.getInstance().getDomain();
        url = getIntent().getStringExtra(EXTRA_URL);

        webView.setOnScrollChangedCallback(new MyWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                int tek = (int) Math.floor(webView.getContentHeight() * webView.getScale());
                if (t - oldt > 0) {
                    if (tek - webView.getScrollY() < webView.getHeight() + 30) {
                        Log.i("WebView", "" + linearLayout.getHeight());
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (oldt - t > 15)
                        linearLayout.setVisibility(View.GONE);
                }
            }
        });

        sendUrl = PropertyManager.getInstance().getDomain()+"/reception/reception/comment_save/tableid/liaison/id/";
        Button btn = (Button)findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = replyView.getText().toString();
                if(webView.getUrl().contains(homeUrl+"/reception/reception/view/tableid/liaison/id/")){
                    String postId = webView.getUrl().replace(homeUrl+"/reception/reception/view/tableid/liaison/id/","");
                    sendUrl=sendUrl+postId;
                }
                NetworkManager.getInstance().sendReply(WebViewActivity.this, sendUrl, reply, new NetworkManager.OnResultListener<Boolean>() {
                    @Override
                    public void onSuccess(HttpRequest request, Boolean result) {
                        if(result){
                            Toast.makeText(WebViewActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WebViewActivity.this,"댓글 등록이 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpRequest request, int code, Throwable cause) {

                    }
                });
            }
        });
        NetworkManager.getInstance().getContactSection(this, url, new NetworkManager.OnResultListener<Element>() {
            @Override
            public void onSuccess(HttpRequest request, Element result) {
                Element content = result.getElementsByTag(HTMLElementName.TABLE).get(0);
//                String con = content.toString();
//                Element btn = content.getElementsByClass("mt20 pull-right").get(0);
//                String btnString = btn.toString();
//
//                if(con.contains(btnString)){
//                    Log.i("WebDialog","yes");
//                    String a = con.replace(btnString,"");
//                    Log.i("WebDialog",a);
//                }
                Element reply = result.getElementsByClass("comment_entries").get(0);

                String realSection = content.toString() + reply.toString();
                if (!TextUtils.isEmpty(realSection)) {
                    loadWebView(realSection);
                }
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {

            }
        });
    }

    public void loadWebView(String data) {
        webView.loadData(getString(R.string.html_header) + data, "text/html; charset=UTF-8", null);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}
