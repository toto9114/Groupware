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

	private static final String REG_ID = "regToken";

	public void setRegistrationToken(String regId) {
		mEditor.putString(REG_ID, regId);
		mEditor.commit();
	}
	
	public String getRegistrationToken() {
		return mPrefs.getString(REG_ID, "");
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

}
