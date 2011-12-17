package net.virifi.android.navigationbarsettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class NavigationBarSettingsActivity extends Activity {
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.main);
	    Intent intent = new Intent();
	    intent.setClassName(this, NavigationBarKeySettingActivity.class.getName());
	    startActivity(intent);
	}

}