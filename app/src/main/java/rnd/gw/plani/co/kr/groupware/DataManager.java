package rnd.gw.plani.co.kr.groupware;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongja94 on 2016-02-16.
 */
public class DataManager extends SQLiteOpenHelper {
    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private static final String DB_NAME = "contact";
    private static final int DB_VERSION = 2;

    private DataManager() {
        super(MyApplication.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + DBContants.Contact.TABLE_NAME + "(" +
                DBContants.Contact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContants.Contact.COLUMN_NAME + " TEXT NOT NULL," +
                DBContants.Contact.COLUMN_TITLE + " TEXT," +
                DBContants.Contact.COLUMN_DATE + " TEXT," +
                DBContants.Contact.COLUMN_LINK + " TEXT" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion == 1) {
//            String sql = "ALTER TABLE " + DBContants.Contact.TABLE_NAME;
//            db.execSQL(sql);
//        }
    }

    public Cursor getAddressCursor() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DBContants.Contact.TABLE_NAME + "." + DBContants.Contact._ID,
                DBContants.Contact.COLUMN_NAME,
                DBContants.Contact.COLUMN_TITLE,
                DBContants.Contact.COLUMN_DATE,
                DBContants.Contact.COLUMN_LINK};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = DBContants.Contact.COLUMN_NAME + " COLLATE LOCALIZED ASC";
        Cursor c = db.query(DBContants.Contact.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
        return c;
    }

    public List<RecieveData> getAddressList() {
        List<RecieveData> list = new ArrayList<RecieveData>();
        Cursor c = getAddressCursor();
        int idIndex = c.getColumnIndex(DBContants.Contact._ID);
        int nameIndex = c.getColumnIndex(DBContants.Contact.COLUMN_NAME);
        int titleIndex = c.getColumnIndex(DBContants.Contact.COLUMN_TITLE);
        int dateIndex = c.getColumnIndex(DBContants.Contact.COLUMN_DATE);
        int linkIndex = c.getColumnIndex(DBContants.Contact.COLUMN_LINK);
        while (c.moveToNext()) {
            RecieveData data = new RecieveData();
            data._id = c.getLong(idIndex);
            data.name = c.getString(nameIndex);
            data.title = c.getString(titleIndex);
            data.date = c.getString(dateIndex);
            data.link = c.getString(linkIndex);
            list.add(data);
        }
        c.close();
        return list;
    }

    ContentValues values = new ContentValues();

    public void insertAddress(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(DBContants.Contact.COLUMN_NAME, data.name);
            values.put(DBContants.Contact.COLUMN_TITLE, data.title);
            values.put(DBContants.Contact.COLUMN_DATE, data.date);
            values.put(DBContants.Contact.COLUMN_LINK, data.link);
            db.insert(DBContants.Contact.TABLE_NAME, null, values);
        } else {
            updateAddress(data);
        }
    }

    public void updateAddress(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            insertAddress(data);
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(DBContants.Contact.COLUMN_NAME, data.name);
        values.put(DBContants.Contact.COLUMN_TITLE, data.title);
        values.put(DBContants.Contact.COLUMN_DATE, data.date);
        values.put(DBContants.Contact.COLUMN_LINK, data.link);

        String where = DBContants.Contact._ID + " = ?";
        String[] args = new String[]{"" + data._id};
        db.update(DBContants.Contact.TABLE_NAME, values, where, args);
    }

    public void deleteAddress(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String where = DBContants.Contact._ID + " = ?";
        String[] args = new String[]{"" + data._id};
        db.delete(DBContants.Contact.TABLE_NAME, where, args);
    }
}
