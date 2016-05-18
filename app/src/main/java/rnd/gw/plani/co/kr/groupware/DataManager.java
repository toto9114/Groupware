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
        String sqlRecieve = "CREATE TABLE " + DBContants.recieveContact.TABLE_NAME + "(" +
                DBContants.recieveContact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContants.recieveContact.COLUMN_NUM + " INTEGER," +
                DBContants.recieveContact.COLUMN_NAME + " TEXT NOT NULL," +
                DBContants.recieveContact.COLUMN_TITLE + " TEXT," +
                DBContants.recieveContact.COLUMN_DATE + " TEXT," +
                DBContants.recieveContact.COLUMN_LINK + " TEXT" +
                ");";
        db.execSQL(sqlRecieve);
        String sqlSend = "CREATE TABLE " + DBContants.sendContact.TABLE_NAME + "(" +
                DBContants.sendContact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContants.sendContact.COLUMN_NUM + " INTEGER," +
                DBContants.sendContact.COLUMN_NAME + " TEXT NOT NULL," +
                DBContants.sendContact.COLUMN_TITLE + " TEXT," +
                DBContants.sendContact.COLUMN_DATE + " TEXT," +
                DBContants.sendContact.COLUMN_LINK + " TEXT" +
                ");";
        db.execSQL(sqlSend);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion == 1) {
//            String sql = "ALTER TABLE " + DBContants.recieveContact.TABLE_NAME;
//            db.execSQL(sql);
//        }
    }

    public static final String TYPE_RECIEVE = "recieveContact";
    public static final String TYPE_SEND = "sendContact";

    public Cursor getContactCursor(String type) {
        SQLiteDatabase db = getReadableDatabase();
        if(type.equals(TYPE_RECIEVE)) {
            String[] columns = {DBContants.recieveContact.TABLE_NAME + "." + DBContants.recieveContact._ID,
                    DBContants.recieveContact.COLUMN_NUM,
                    DBContants.recieveContact.COLUMN_NAME,
                    DBContants.recieveContact.COLUMN_TITLE,
                    DBContants.recieveContact.COLUMN_DATE,
                    DBContants.recieveContact.COLUMN_LINK};
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = DBContants.recieveContact._ID + " COLLATE LOCALIZED ASC";
            Cursor rc = db.query(DBContants.recieveContact.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
//            String query = "SELECT  * FROM " + DBContants.recieveContact.TABLE_NAME;
            return rc;
        }else{
            String[] columns = {DBContants.sendContact.TABLE_NAME + "." + DBContants.sendContact._ID,
                    DBContants.sendContact.COLUMN_NUM,
                    DBContants.sendContact.COLUMN_NAME,
                    DBContants.sendContact.COLUMN_TITLE,
                    DBContants.sendContact.COLUMN_DATE,
                    DBContants.sendContact.COLUMN_LINK};
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = DBContants.sendContact._ID + " COLLATE LOCALIZED ASC";
            Cursor sc = db.query(DBContants.sendContact.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
            return sc;
        }
    }

    public List<RecieveData> getRecieveContactList() {
        List<RecieveData> list = new ArrayList<RecieveData>();
        Cursor c = getContactCursor(TYPE_RECIEVE);

        int idIndex = c.getColumnIndex(DBContants.recieveContact._ID);
        int numIndex = c.getColumnIndex(DBContants.recieveContact.COLUMN_NUM);
        int nameIndex = c.getColumnIndex(DBContants.recieveContact.COLUMN_NAME);
        int titleIndex = c.getColumnIndex(DBContants.recieveContact.COLUMN_TITLE);
        int dateIndex = c.getColumnIndex(DBContants.recieveContact.COLUMN_DATE);
        int linkIndex = c.getColumnIndex(DBContants.recieveContact.COLUMN_LINK);
        while (c.moveToNext()) {
            RecieveData data = new RecieveData();
            data._id = c.getLong(idIndex);
            data.num = c.getInt(numIndex);
            data.name = c.getString(nameIndex);
            data.title = c.getString(titleIndex);
            data.date = c.getString(dateIndex);
            data.link = c.getString(linkIndex);
            list.add(data);
        }
        c.close();
        return list;
    }
    public List<RecieveData> getSendContactList() {
        List<RecieveData> list = new ArrayList<RecieveData>();
        Cursor c = getContactCursor(TYPE_SEND);
        int idIndex = c.getColumnIndex(DBContants.sendContact._ID);
        int numIndex = c.getColumnIndex(DBContants.sendContact.COLUMN_NUM);
        int nameIndex = c.getColumnIndex(DBContants.sendContact.COLUMN_NAME);
        int titleIndex = c.getColumnIndex(DBContants.sendContact.COLUMN_TITLE);
        int dateIndex = c.getColumnIndex(DBContants.sendContact.COLUMN_DATE);
        int linkIndex = c.getColumnIndex(DBContants.sendContact.COLUMN_LINK);
        while (c.moveToNext()) {
            RecieveData data = new RecieveData();
            data._id = c.getLong(idIndex);
            data.num = c.getInt(numIndex);
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

    public void insertReceiveData(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(DBContants.recieveContact.COLUMN_NUM, data.num);
            values.put(DBContants.recieveContact.COLUMN_NAME, data.name);
            values.put(DBContants.recieveContact.COLUMN_TITLE, data.title);
            values.put(DBContants.recieveContact.COLUMN_DATE, data.date);
            values.put(DBContants.recieveContact.COLUMN_LINK, data.link);
            db.insert(DBContants.recieveContact.TABLE_NAME, null, values);
        } else {
            updateContact(data, TYPE_RECIEVE);
        }
    }
    public void insertSendData(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(DBContants.sendContact.COLUMN_NUM, data.num);
            values.put(DBContants.sendContact.COLUMN_NAME, data.name);
            values.put(DBContants.sendContact.COLUMN_TITLE, data.title);
            values.put(DBContants.sendContact.COLUMN_DATE, data.date);
            values.put(DBContants.sendContact.COLUMN_LINK, data.link);
            db.insert(DBContants.sendContact.TABLE_NAME, null, values);
        } else {
            updateContact(data, TYPE_SEND);
        }
    }

    public void updateContact(RecieveData data, String type) {
        if(type.equals(TYPE_RECIEVE)) {
            if (data._id == RecieveData.INVALID_ID) {
                insertReceiveData(data);
                return;
            }
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(DBContants.recieveContact.COLUMN_NUM, data.num);
            values.put(DBContants.recieveContact.COLUMN_NAME, data.name);
            values.put(DBContants.recieveContact.COLUMN_TITLE, data.title);
            values.put(DBContants.recieveContact.COLUMN_DATE, data.date);
            values.put(DBContants.recieveContact.COLUMN_LINK, data.link);

            String where = DBContants.recieveContact._ID + " = ?";
            String[] args = new String[]{"" + data._id};
            db.update(DBContants.recieveContact.TABLE_NAME, values, where, args);
        }else{
            if (data._id == RecieveData.INVALID_ID) {
                insertSendData(data);
                return;
            }
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(DBContants.sendContact.COLUMN_NUM, data.num);
            values.put(DBContants.sendContact.COLUMN_NAME, data.name);
            values.put(DBContants.sendContact.COLUMN_TITLE, data.title);
            values.put(DBContants.sendContact.COLUMN_DATE, data.date);
            values.put(DBContants.sendContact.COLUMN_LINK, data.link);

            String where = DBContants.sendContact._ID + " = ?";
            String[] args = new String[]{"" + data._id};
            db.update(DBContants.sendContact.TABLE_NAME, values, where, args);
        }
    }

    public void deleteAddress(RecieveData data) {
        if (data._id == RecieveData.INVALID_ID) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String where = DBContants.recieveContact._ID + " = ?";
        String[] args = new String[]{"" + data._id};
        db.delete(DBContants.recieveContact.TABLE_NAME, where, args);
    }
}
