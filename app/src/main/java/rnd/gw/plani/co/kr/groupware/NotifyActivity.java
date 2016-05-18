package rnd.gw.plani.co.kr.groupware;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.htmlparser.jericho.HTMLElementName;

import org.apache.http.HttpRequest;
import org.jsoup.nodes.Element;

import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

public class NotifyActivity extends AppCompatActivity {


    private static final String FEED_URL = "http://gw.plani.co.kr/feeds";
//    private static final int VERTICAL_ITEM_SPACE = 48;
//    RecyclerView recyclerView;
    NotifyAdapter mAdapter;
    FamiliarRecyclerView recyclerView;
    WebDialog webDialog;
    String homeUrl ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        homeUrl = PropertyManager.getInstance().getDomain();

        recyclerView = (FamiliarRecyclerView) findViewById(R.id.recycler);
        mAdapter = new NotifyAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.GRAY)
                .marginResId(R.dimen.divider_horizontal_margin)
                .sizeResId(R.dimen.divider_size)
                .build());

        recyclerView.setAdapter(mAdapter);

        webDialog = new WebDialog();
        recyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                NotifyData data = mAdapter.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString(WebDialog.EXTRA_URL,data.link);
                webDialog.setArguments(args);
                webDialog.show(getSupportFragmentManager(),"webDialog");
                Log.i("Notify",data.link);
            }
        });

        NetworkManager.getInstance().getAllNotify(this, FEED_URL, new NetworkManager.OnResultListener<Element>() {
            @Override
            public void onSuccess(HttpRequest request, Element result) {
                parseToList(result);
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.clear();
        NetworkManager.getInstance().getAllNotify(this, FEED_URL, new NetworkManager.OnResultListener<Element>() {
            @Override
            public void onSuccess(HttpRequest request, Element result) {
                parseToList(result);
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {

            }
        });
    }

    private void parseToList(Element liList) {
        List<Element> li = liList.getElementsByTag(HTMLElementName.LI);
        for (int i = 0; i < li.size(); i++) {
            NotifyData data = new NotifyData();
            data.category = li.get(i).getElementsByTag(HTMLElementName.STRONG).text();
            data.title = li.get(i).getElementsByTag(HTMLElementName.A).get(0).text();
            data.link = "http://"+ homeUrl+li.get(i).getElementsByTag(HTMLElementName.A).get(0).attr("href");
            data.name = li.get(i).getElementsByClass("feed_name").text();
            mAdapter.add(data);
        }
    }
}
