package rnd.gw.plani.co.kr.groupware;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpRequest;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReplyFragment extends Fragment { //댓글 입력 창


    public ReplyFragment() {
        // Required empty public constructor
    }

    TextView textView;
    EditText replyView;

    String sendUrl = "";
    String tableId = "";
    String category = "";
    public static final String EXTRA_TABLE_ID = "tableId";
    public static final String EXTRA_CATEGORY = "category";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            tableId = getArguments().getString(EXTRA_TABLE_ID,"");
            category = getArguments().getString(EXTRA_CATEGORY,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reply, container, false);
        View toolbar = getLayoutInflater(savedInstanceState).inflate(R.layout.view_center_toolbar, null);
        textView = (TextView) toolbar.findViewById(R.id.text_title);
        replyView = (EditText)view.findViewById(R.id.edit_reply);
        textView.setText("댓글달기");
        setHasOptionsMenu(true);
        ((WebViewActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((WebViewActivity) getActivity()).getSupportActionBar().setCustomView(toolbar,
                new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        ((WebViewActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(category.equals("업무연락")|| category.equals("댓글알림")) {
            sendUrl = "http://" + PropertyManager.getInstance().getDomain() + "/reception/reception/comment_save/tableid/liaison/id/";
        }else if(category.equals("쪽지")){
            sendUrl = "http://" + PropertyManager.getInstance().getDomain() + "/message/box/save/target/";
        }else{
            //no action
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_regist_reply, menu);
    }

    ProgressDialog dialog = null;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.regist_reply) {
            String reply = replyView.getText().toString();
            replyView.setText("");
            InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); //댓글 입력후 키보드 내려감
            mInputMethodManager.hideSoftInputFromWindow(replyView.getWindowToken(), 0);
            String url = sendUrl + tableId;
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("");
            dialog.setMessage("댓글 등록중입니다");
            dialog.show();
            if(category.equals("업무연락") || category.equals("댓글알림")) {
                NetworkManager.getInstance().sendReply(getContext(), url, tableId, reply, new NetworkManager.OnResultListener<Boolean>() {
                    @Override
                    public void onSuccess(HttpRequest request, Boolean result) {
                        dialog.dismiss();
                        if (result) {
                            Toast.makeText(getContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "댓글 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpRequest request, int code, Throwable cause) {
                        dialog.dismiss();
                    }
                });
            }else if(category.equals("쪽지")){
                NetworkManager.getInstance().sendNote(getContext(), url, tableId, reply, new NetworkManager.OnResultListener<Boolean>() {
                    @Override
                    public void onSuccess(HttpRequest request, Boolean result) {
                        dialog.dismiss();
                        if (result) {
                            Toast.makeText(getContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "댓글 등록이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpRequest request, int code, Throwable cause) {
                        dialog.dismiss();
                    }
                });
            }else{

            }
        }
        if (id == android.R.id.home) {
            getActivity().getSupportFragmentManager()
                    .popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
