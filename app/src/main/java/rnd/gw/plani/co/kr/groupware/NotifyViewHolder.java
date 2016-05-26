package rnd.gw.plani.co.kr.groupware;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by RND on 2016-05-17.
 */
public class NotifyViewHolder extends RecyclerView.ViewHolder {

    TextView categoryView, titleView, nameView, timeView;
    ImageView iconView;

    public NotifyViewHolder(View itemView) {
        super(itemView);
        categoryView = (TextView) itemView.findViewById(R.id.text_category);
        titleView = (TextView) itemView.findViewById(R.id.text_title);
        nameView = (TextView) itemView.findViewById(R.id.text_name);
        iconView = (ImageView) itemView.findViewById(R.id.image_icon);
        timeView = (TextView) itemView.findViewById(R.id.text_time);
    }

    NotifyData data;

    public void setNotify(NotifyData data) {
        this.data = data;
        categoryView.setText(data.category);
        titleView.setText(data.title);

        switch (data.category) {
            case "업무연락":
                iconView.setImageResource(R.drawable.ic_email_black_48dp);
                break;
            case "댓글알림":
                iconView.setImageResource(R.drawable.ic_comment_black_48dp);
                break;
            case "쪽지":
                iconView.setImageResource(R.drawable.ic_textsms_black_48dp);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_info_black_48dp);
                break;
        }
        nameView.setText(data.name);
//        timeView.setText(data.time);
    }
}
