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
	public void setUser(Boolean isReg){
		mEditor.putBoolean(REG_USER,isReg);
		mEditor.commit();
	}
	public Boolean isUser(){
		return mPrefs.getBoolean(REG_USER,false);
	}

	private static final String REG_DOMAIN = "domain";

	public void setDomain(String domain){
		mEditor.putString(REG_DOMAIN, domain);
		mEditor.commit();
	}
	public String getDomain(){
		return mPrefs.getString(REG_DOMAIN, "");
	}

	private static final String REG_TOKEN = "regToken";

	public void setRegistrationToken(String regId) {
		mEditor.putString(REG_TOKEN, regId);
		mEditor.commit();
	}
	
	public String getRegistrationToken() {
		return mPrefs.getString(REG_TOKEN, "");
	}

	private static final String FIELD_PUSH = "push";
	public boolean isPush(){
		return mPrefs.getBoolean(FIELD_PUSH,true);
	}

	public void setPush(boolean push){
		mEditor.putBoolean(FIELD_PUSH,push);
		mEditor.commit();
	}

	private static final String BADGE_COUNT = "badge";
	public void setBadgeCount(int count){
		mEditor.putInt(BADGE_COUNT,count);
		mEditor.commit();
	}

	public int getBadgeCount(){
		return mPrefs.getInt(BADGE_COUNT,0);
	}

	private static final String REG_ID = "userid";
	public void setUserId(String id){
		mEditor.putString(REG_ID, id);
		mEditor.commit();
	}
	public String getUserId(){
		return mPrefs.getString(REG_ID, "");
	}

	private static final String REG_PW = "password";
	public void setPassword(String password){
		mEditor.putString(REG_PW,password);
		mEditor.commit();
	}
	public String getPassword(){
		return mPrefs.getString(REG_PW,"");
	}
}
