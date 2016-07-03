package com.example.kimcj.db;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

	/** SharedPreferences 이름 */
	String sharedPreferencesName = "com.onethefull.ttstt";
	
    Context mContext;

    public SPManager(Context c) {
        mContext = c;
    }

	public void clear(){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/*user 등록.*/
	public void setUser(String key, String value){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(!getUser().equals("")){
			editor.remove(key);
		}
		editor.putString(key, value);
		editor.commit();
	}

	public String getUser(){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return pref.getString("user", "");
	}
	/*
	* 푸디 session
	* */
	public void setSession(String key, String value){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(!getSession().equals("")){
			editor.remove(key);
		}
		editor.putString(key, value);
		editor.commit();
	}

	public String getSession(){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return pref.getString("session", "");
	}

	public void setFoodiOneCard(boolean flag) {
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(!getFoodiNextMessage().equals("")){
			editor.remove("FoodiOneCard");
		}
		editor.putString("FoodiOneCard", String.valueOf(flag));
		editor.commit();
	}

	public boolean getFoodiOneCard() {
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		if(pref.getString("FoodiOneCard", "") == null || pref.getString("FoodiOneCard", "").isEmpty()) {
			return false;
		}
		return Boolean.valueOf(pref.getString("FoodiOneCard", ""));
	}

	/*푸디 내용*/
	public void setFoodiMessage(String key, String value){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(!getFoodiMessage().equals("")){
			editor.remove(key);
		}
		editor.putString(key, value);
		editor.commit();
	}

	public String getFoodiMessage(){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return pref.getString("foodimessage", "");
	}

	/*푸디 다음 질문 내용*/
	public void setFoodiNextMessage(String key, String value){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		if(!getFoodiNextMessage().equals("")){
			editor.remove(key);
		}
		editor.putString(key, value);
		editor.commit();
	}

	public String getFoodiNextMessage(){
		SharedPreferences pref = mContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		return pref.getString("foodinextmessage", "");
	}
}
