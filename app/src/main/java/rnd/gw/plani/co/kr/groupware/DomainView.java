package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by RND on 2016-05-27.
 */
public class DomainView extends FrameLayout {
    TextView domainView;
    public DomainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(),R.layout.view_domain,this);
        domainView = (TextView)findViewById(R.id.text_domain);
    }
    public void setDomain(String domain){
        domainView.setText(domain);
    }
    public String getDomain(){
        return domainView.getText().toString();
    }
}
