package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by RND on 2016-04-19.
 */
public class AndroidWebInterface {
    private final static String Tag = "WebAppInterface";
    Context mContext;
    WebView mWebView;

    AndroidWebInterface(Context c, WebView wv){
        mContext = c;
        mWebView = wv;
    }
    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
