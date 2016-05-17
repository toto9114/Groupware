package rnd.gw.plani.co.kr.groupware;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class NotifyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotifyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new NotifyAdapter();
        recyclerView.setAdapter(mAdapter);
        
    }
}
