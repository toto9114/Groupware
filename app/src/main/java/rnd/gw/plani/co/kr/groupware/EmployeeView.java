package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by RND on 2016-05-16.
 */
public class EmployeeView extends FrameLayout {

    TextView companyView,nameView, statusView;
    public EmployeeView(Context context) {
        super(context);
        inflate(getContext(),R.layout.view_customer,this);
        init();
    }
    public void init(){
        companyView = (TextView)findViewById(R.id.text_company);
        nameView = (TextView)findViewById(R.id.text_name);
        statusView = (TextView)findViewById(R.id.text_status);
    }

    EmployeeData data;
    public void setCustomer(EmployeeData data){
        this.data = data;
        companyView.setText(data.company);
        nameView.setText(data.name);
        statusView.setText(data.status);
    }
}
