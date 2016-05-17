package rnd.gw.plani.co.kr.groupware;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RND on 2016-05-17.
 */
public class NotifyAdapter extends RecyclerView.Adapter{
    List<NotifyData> items = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_notify, parent, false);
        NotifyViewHolder holder = new NotifyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotifyData data = items.get(position);
        ((NotifyViewHolder)holder).setNotify(data);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
