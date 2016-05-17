package rnd.gw.plani.co.kr.groupware;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RND on 2016-05-16.
 */
public class UndecideAdapter extends BaseAdapter {
    List<UndecideData> items = new ArrayList<>();

    public void add(UndecideData data){
        items.add(data);
    }

    public void refresh(){
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UndecideView view;
        if (convertView == null) {
            view = new UndecideView(parent.getContext());
        } else {
            view = (UndecideView)convertView;
        }

        return view;
    }
}
