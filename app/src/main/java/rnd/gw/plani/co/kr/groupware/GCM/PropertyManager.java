package rnd.gw.plani.co.kr.groupware.GCM;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import rnd.gw.plani.co.kr.groupware.MyApplication;

public class PropertyManager {
    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        mEditor = mPrefs.edit();
    }

    private static final String REG_USER = "regit";

    public void setUser(Boolean isReg) {
        mEditor.putBoolean(REG_USER, isReg);
        mEditor.commit();
    }

    public Boolean isUser() {
        return mPrefs.getBoolean(REG_USER, false);
    }

    private static final String REG_DOMAIN = "domain"; //도메인주소 관리

    public void setDomain(String domain) {
        mEditor.putString(REG_DOMAIN, domain);
        mEditor.commit();
    }

    public String getDomain() {
        return mPrefs.getString(REG_DOMAIN, "");
    }

    private static final String REG_TOKEN = "regToken";  //registration token 정보 관리

    public void setRegistrationToken(String regId) {
        mEditor.putString(REG_TOKEN, regId);
        mEditor.commit();
    }

    public String getRegistrationToken() {
        return mPrefs.getString(REG_TOKEN, "");
    }

    private static final String FIELD_PUSH = "push";

    public boolean isPush() {
        return mPrefs.getBoolean(FIELD_PUSH, true);
    }

    public void setPush(boolean push) {
        mEditor.putBoolean(FIELD_PUSH, push);
        mEditor.commit();
    }

    private static final String BADGE_COUNT = "badge"; //뱃지카운트 관리

    public void setBadgeCount(int count) {
        mEditor.putInt(BADGE_COUNT, count);
        mEditor.commit();
    }

    public int getBadgeCount() {
        return mPrefs.getInt(BADGE_COUNT, 0);
    }

    private static final String REG_ID = "userid";  //유저아이디 관리

    public void setUserId(String id) {
        mEditor.putString(REG_ID, id);
        mEditor.commit();
    }

    public String getUserId() {
        return mPrefs.getString(REG_ID, "");
    }

    private static final String REG_PW = "password"; //패스워드 관리

    public void setPassword(String password) {
        mEditor.putString(REG_PW, password);
        mEditor.commit();
    }

    public String getPassword() {
        return mPrefs.getString(REG_PW, "");
    }

    private static final String REG_APP_ID = "app_id"; //쿠키에서 추출한 appId 관리

    public void setAppId(String appId) {
        mEditor.putString(REG_APP_ID, appId);
        mEditor.commit();
    }
    public String getAppId() {
        return mPrefs.getString(REG_APP_ID, "");
    }


}
