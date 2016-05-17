package rnd.gw.plani.co.kr.groupware;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by RND on 2016-05-17.
 */
public class NotifyViewHolder extends RecyclerView.ViewHolder{

    TextView categoryView, titleView, nameView;

    public NotifyViewHolder(View itemView) {
        super(itemView);
        categoryView = (TextView)itemView.findViewById(R.id.text_category);
        titleView = (TextView)itemView.findViewById(R.id.text_title);
        nameView = (TextView)itemView.findViewById(R.id.text_name);
    }
    NotifyData data;
    public void setNotify(NotifyData data){
        this.data = data;
        categoryView.setText(data.category);
        titleView.setText(data.title);
        nameView.setText(data.name);
    }
}
