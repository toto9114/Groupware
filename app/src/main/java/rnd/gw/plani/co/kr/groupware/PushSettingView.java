package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

/**
 * Created by RND on 2016-05-27.
 */
public class PushSettingView extends FrameLayout {
    Switch pushSwitch;

    public PushSettingView(Context context) {
        super(context);
        inflate(getContext(), R.layout.view_push_setting, this);
        pushSwitch = (Switch) findViewById(R.id.switch_push);
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){

               }else{

               }
            }
        });
    }
}
