package rnd.gw.plani.co.kr.groupware;

import android.provider.BaseColumns;

/**
 * Created by dongja94 on 2016-02-16.
 */
public class DBContants {
    public interface recieveContact extends BaseColumns {
        public static final String TABLE_NAME = "recieve";
        public static final String COLUMN_NUM = "number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LINK = "link";
    }
    public interface sendContact extends BaseColumns{
        public static final String TABLE_NAME = "send";
        public static final String COLUMN_NUM = "number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LINK = "link";
    }
}
