package rnd.gw.plani.co.kr.groupware;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RND on 2016-05-16.
 */
public class EmployeeAdapter extends BaseAdapter {
    List<EmployeeData> items = new ArrayList<>();

    public void add(EmployeeData data){
        items.add(data);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
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
        EmployeeView view;
        if (convertView == null) {
            view = new EmployeeView(parent.getContext());
        } else {
            view = (EmployeeView)convertView;
        }
        view.setCustomer(items.get(position));
        return view;
    }
}
