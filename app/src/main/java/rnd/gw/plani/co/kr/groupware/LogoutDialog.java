package rnd.gw.plani.co.kr.groupware;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutDialog extends DialogFragment {


    public LogoutDialog() {
        // Required empty public constructor
    }

    TextView titleView, messageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout_dialog, container, false);
        titleView = (TextView)view.findViewById(R.id.text_title);
        messageView = (TextView)view.findViewById(R.id.text_message);
        Button btn = (Button)view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropertyManager.getInstance().setUser(false);
                PropertyManager.getInstance().setAppId("");
                Intent i = new Intent(getContext(), RegistActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().finish();
                dismiss();
            }
        });
        btn = (Button)view.findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

}
