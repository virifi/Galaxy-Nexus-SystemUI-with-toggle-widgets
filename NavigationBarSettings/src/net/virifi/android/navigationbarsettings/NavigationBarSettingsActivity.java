package net.virifi.android.navigationbarsettings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;



public class NavigationBarSettingsActivity extends PreferenceActivity {
	static final String PREF_KEY = "NavigationBarSettings";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.main);
	   
	    getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
	    getPreferenceManager().setSharedPreferencesName(PREF_KEY);
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				if (key.equals("always_show_menu") || key.equals("open_statusbar_left") || key.equals("open_statusbar_right")) {
					sendSetting(sharedPreferences, key);
				}
			}
	    });
	   addPreferencesFromResource(R.xml.main_preference);
	}
	private void sendSetting(SharedPreferences sharedPreferences, String key) {
		Intent intent = new Intent("com.android.systemui.statusbar.MAIN_SETTING_CHANGED");
		intent.putExtra(key, true);
		intent.putExtra("value", sharedPreferences.getBoolean(key, true));
		sendBroadcast(intent);
		Log.d("NavigationBarSettingsActivity", "send Intent");
	}

}