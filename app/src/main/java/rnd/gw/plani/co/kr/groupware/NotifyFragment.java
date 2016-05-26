package rnd.gw.plani.co.kr.groupware;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.htmlparser.jericho.HTMLElementName;

import org.apache.http.HttpRequest;
import org.jsoup.nodes.Element;

import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {


    public NotifyFragment() {
        // Required empty public constructor
    }

    private String FEED_URL = "http://gw.plani.co.kr/feeds/feed/index/page/";

    NotifyAdapter mAdapter;
    FamiliarRecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    private static String HOME_URL = "";

    boolean isLast = false;
    int page = 1; //페이징 처리하기 위함
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notify, container, false);
        HOME_URL = "http://" + PropertyManager.getInstance().getDomain();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        recyclerView = (FamiliarRecyclerView) view.findViewById(R.id.recycler);
        mAdapter = new NotifyAdapter();

        layoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.LTGRAY)
                .marginResId(R.dimen.divider_left_margin, R.dimen.divider_right_margin)
                .sizeResId(R.dimen.divider_size)
                .build());

        recyclerView.setAdapter(mAdapter);

        initData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLast && newState == recyclerView.SCROLL_STATE_IDLE) {
                    getMoreItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //check for scroll down
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
                    isLast = true;
                } else {
                    isLast = false;
                }

            }
        });
        recyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                NotifyData data = mAdapter.getItemAtPosition(position);
                if (data.category.equals("업무연락") || data.category.equals("댓글알림")
                        || data.category.equals("쪽지") || data.category.contains("커뮤니티")) {
                    Intent i = new Intent(getContext(), WebViewActivity.class);
                    i.putExtra(WebViewActivity.EXTRA_URL, data.link);
                    i.putExtra(WebViewActivity.EXTRA_CATEGORY, data.category);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), "지원하지않는 페이지입니다", Toast.LENGTH_SHORT).show();
                }
                Log.i("Notify", data.link);
            }
        });

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.splash));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                NetworkManager.getInstance().getAllNotify(getContext(), FEED_URL + 1, new NetworkManager.OnResultListener<NewsFeedResult>() {
                    @Override
                    public void onSuccess(HttpRequest request, NewsFeedResult result) {
                        mAdapter.clear();
                        parseToList(result.element);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(HttpRequest request, int code, Throwable cause) {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        return view;
    }

    ProgressDialog dialog = null;
    int lastPage;

    private void initData() {

        NetworkManager.getInstance().getAllNotify(getContext(), FEED_URL + page, new NetworkManager.OnResultListener<NewsFeedResult>() {
            @Override
            public void onSuccess(HttpRequest request, NewsFeedResult result) {
                parseToList(result.element);
                lastPage = result.page;
                dialog.dismiss();
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {
                dialog.dismiss();
            }
        });
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setMessage("loading...");
        dialog.setCancelable(false);
        dialog.show();
    }

    boolean isMoreData = false;

    private void getMoreItem() {

        if (page >= lastPage) {
            return;
        } else {
            page++;
        }

        NetworkManager.getInstance().getAllNotify(getContext(), FEED_URL + page, new NetworkManager.OnResultListener<NewsFeedResult>() {
            @Override
            public void onSuccess(HttpRequest request, NewsFeedResult result) {
                parseToList(result.element);
                isMoreData = false;
                dialog.dismiss();
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {
                isMoreData = false;
                dialog.dismiss();
            }
        });
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setMessage("loading...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void parseToList(Element liList) {
        List<Element> li = liList.getElementsByTag(HTMLElementName.LI);
        for (int i = 0; i < li.size(); i++) {
            NotifyData data = new NotifyData();
            data.category = li.get(i).getElementsByTag(HTMLElementName.STRONG).text();
            data.title = li.get(i).getElementsByTag(HTMLElementName.A).get(0).text();
            data.link = HOME_URL + li.get(i).getElementsByTag(HTMLElementName.A).get(0).attr("href");
            data.name = li.get(i).getElementsByClass("feed_name").text();
            mAdapter.add(data);
        }
    }
}
