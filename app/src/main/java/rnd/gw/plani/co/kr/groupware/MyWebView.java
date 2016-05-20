package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by RND on 2016-04-19.
 */
public class MyWebView extends WebView {
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private OnScrollChangedCallback mOnScrollChangedCallback;

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t, oldl, oldt);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public static interface OnScrollChangedCallback {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
}
