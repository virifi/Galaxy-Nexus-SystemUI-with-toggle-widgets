package net.virifi.android.navigationbarsettings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationBarKeySettingActivity extends Activity {
	static final String PREF_KEY = "NavigationBarSettings"; 
	
	String[] prefs = { "Menu", "Back", "Home", "Search", "RecentApps", "Optional", "Menu2", "Menu3", "Spacer1", "Spacer2"};
	int[] resids = {R.drawable.ic_sysbar_menu, R.drawable.ic_sysbar_back, R.drawable.ic_sysbar_home, R.drawable.ic_sysbar_search, R.drawable.ic_sysbar_recent, R.drawable.ic_sysbar_menu, R.drawable.ic_sysbar_menu, R.drawable.ic_sysbar_menu2, 0, 0};
	boolean[] checked = {true, true, true, true, true, true, false, false, false, false};
	int[] spinnerSelected = { 0, 1, 1, 1, 1, 0, 0, 1, 0, 0};
	boolean[] initialized = new boolean[prefs.length];
	
	static final int[] spinnerSize = { 40, 60, 80 };

	int mDraggingPosition = -1;
	SampleAdapter mAdapter;
	SortableListView mListView;
	boolean mIsDragging = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    for (int i = 0; i < prefs.length; i++) {
	    	initialized[i] = false;
	    }
	    setContentView(R.layout.navigation_bar_key_setting_activity);
	    mAdapter = new SampleAdapter();
	    mListView = (SortableListView) findViewById(R.id.listView);
	    mListView.setDragListener(new DragListener());
	    mListView.setSortable(true);
	    mListView.setAdapter(mAdapter);
	    loadKeySettings();
	    View container = findViewById(R.id.button_container);
	    Button saveButton = (Button) container.findViewById(R.id.saveButton);
	    saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndNotify();
			}
	    });
	    Button resetButton = (Button) container.findViewById(R.id.resetButton);
	    resetButton.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		loadKeySettings();
	    		mListView.invalidateViews();
	    	}
	    });
	}
	
	private void saveAndNotify() {
		storeKeySettings();
		Intent intent = new Intent("com.android.systemui.statusbar.NAVIGATION_BAR_SETTING_CHANGED");
		sendBroadcast(intent);
	}
	
	private void loadKeySettings() {
		SharedPreferences pref = getSharedPreferences(PREF_KEY, Activity.MODE_WORLD_READABLE | Activity.MODE_MULTI_PROCESS);
		if (pref == null || pref.getString("keyname1", null) == null) {
			return;
		}
		String[] prefsOrig = new String[prefs.length];
		boolean[] checkedOrig = new boolean[checked.length];
		int[] residsOrig = new int[resids.length];
		int[] spinnerSelectedOrig = new int[spinnerSelected.length];
		for (int i = 0; i < prefs.length; i++) {
			prefsOrig[i] = new String(prefs[i]);
			checkedOrig[i] = checked[i];
			residsOrig[i] = resids[i];
			spinnerSelectedOrig[i] = spinnerSelected[i];
		}
		boolean failed = false;
		for (int i = 0; i < prefs.length; i++) {
			prefs[i] = pref.getString("keyname" + String.valueOf(i), null);
			if (prefs[i] == null) {
				failed = true;
				break;
			}
			checked[i] = pref.getBoolean("show" + String.valueOf(i), true);
			resids[i] = getResId(prefs[i]);
			int size = pref.getInt("size" + String.valueOf(i), -1);
			if (size < 0) {
				failed = true;
				break;
			}
			for (int j = 0; j < spinnerSize.length; j++) {
				if (size == spinnerSize[j]) {
					spinnerSelected[i] = j;
					break;
				}
			}
		}
		if (failed) {
			prefs = prefsOrig;
			checked = checkedOrig;
			resids = residsOrig;
			spinnerSelected = spinnerSelectedOrig;
		}
	}
	
	private int getResId(String keyName) {
		if ("Menu".equals(keyName)) {
			return R.drawable.ic_sysbar_menu;
		} else if ("Back".equals(keyName)) {
			return R.drawable.ic_sysbar_back;
		} else if ("Home".equals(keyName)) {
			return R.drawable.ic_sysbar_home;
		} else if ("Search".equals(keyName)) {
			return R.drawable.ic_sysbar_search;
		} else if ("RecentApps".equals(keyName)) {
			return R.drawable.ic_sysbar_recent;
		} else if ("Optional".equals(keyName)) {
			return R.drawable.ic_sysbar_menu;	
		} else if ("Menu2".equals(keyName)) {
			return R.drawable.ic_sysbar_menu;
		} else if ("Menu3".equals(keyName)) {
			return R.drawable.ic_sysbar_menu2;
		} else {
			return 0;
		}
	}
	
	private void storeKeySettings() {
		SharedPreferences pref = getSharedPreferences(PREF_KEY, Activity.MODE_WORLD_READABLE | Activity.MODE_MULTI_PROCESS);
		if (pref == null) {
			Toast.makeText(this, "can't save key settings", Toast.LENGTH_LONG).show();
		}
		SharedPreferences.Editor editor = pref.edit();
		if (editor == null) {
			Toast.makeText(this, "can't save key settings", Toast.LENGTH_LONG).show();
		}
		int showKeyCount = 0;
		for (int i = 0; i < prefs.length; i++) {
			editor.putString("keyname" + String.valueOf(i), prefs[i]);
			editor.putBoolean("show" + String.valueOf(i), checked[i]);
			editor.putInt("size" + String.valueOf(i), spinnerSize[spinnerSelected[i]]);
			if (checked[i]) showKeyCount++;
		}
		editor.putInt("showKeyCount", showKeyCount);
		editor.commit();
		Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
	}

	class SampleAdapter extends BaseAdapter {
	    @Override
	    public int getCount() {
	        return prefs.length;
	    }
	    
	    @Override
	    public String getItem(int position) {
	        return prefs[position];
	    }
	    
	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            convertView = getLayoutInflater().inflate(
	                    R.layout.list_view_item, null);
	        }
	        final TextView view = (TextView) convertView.findViewById(R.id.key_name);
	        final String keyName = prefs[position];
	        view.setText(keyName);
	        view.setVisibility(position == mDraggingPosition ? View.INVISIBLE
	                : View.VISIBLE);
	        final ImageView imgView = (ImageView) convertView.findViewById(R.id.imageView1);
	       
	        imgView.setImageResource(resids[position]);
	        imgView.setVisibility(position == mDraggingPosition ? View.INVISIBLE
	                : View.VISIBLE);
	        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
	        checkBox.setVisibility(position == mDraggingPosition ? View.INVISIBLE
	                : View.VISIBLE);
	        
	        final int p = position;
    		// 必ずsetChecked前にリスナを登録(convertView != null の場合は既に別行用のリスナが登録されている！)
    		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (keyName.equals("Home") && !isChecked) {
						checkBox.setChecked(true);
						Toast.makeText(getApplicationContext(), "You cannot disable home key", Toast.LENGTH_LONG).show();
						return;
					} else if (keyName.equals("Back") && !isChecked) {
						checkBox.setChecked(true);
						Toast.makeText(getApplicationContext(), "You cannot disable back key", Toast.LENGTH_LONG).show();
						return;
					}
					checked[p] = isChecked;
					/*
					if (!mIsDragging)
						saveAndNotify();
						*/
						
				}

    		});
    		checkBox.setChecked(checked[position]);
    		
    		
    		
    		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        // アイテムを追加します
	        adapter.add("small");
	        adapter.add("midium");
	        adapter.add("large");
	        Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner1);
	        
	        // アダプターを設定します
	        spinner.setAdapter(adapter);
	        spinner.setSelection(spinnerSelected[p]);
	        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
	        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view,
	                    int position, long id) {
	            	
	            	
	            	
	            	if (position == spinnerSelected[p]) return;
	            	
	            	spinnerSelected[p] = position;
	            }
	            @Override
	            public void onNothingSelected(AdapterView<?> arg0) {
	            }
	        });
	        spinner.setVisibility(position == mDraggingPosition ? View.INVISIBLE
	                : View.VISIBLE);
	        spinner.setFocusable(false);
    		
	       return convertView;
	    }
	}
    
    
    class DragListener extends SortableListView.SimpleDragListener {
        @Override
        public int onStartDrag(int position) {
            mDraggingPosition = position;
            mListView.invalidateViews();
            mIsDragging = true;
            return position;
        }
        
        @Override
        public int onDuringDrag(int positionFrom, int positionTo) {
            if (positionFrom < 0 || positionTo < 0
                    || positionFrom == positionTo) {
                return positionFrom;
            }
            int i;
            if (positionFrom < positionTo) {
                final int min = positionFrom;
                final int max = positionTo;
                final String data = prefs[min];
                final int resId = resids[min];
                final boolean isChecked = checked[min];
                final int spinnerSel = spinnerSelected[min];
                i = min;
                while (i < max) {
                    prefs[i] = prefs[i+1];
                    resids[i] = resids[i+1];
                    checked[i] = checked[i+1];
                    spinnerSelected[i] = spinnerSelected[i+1];
                    i++;
                }
                prefs[max] = data;
                resids[max] = resId;
                checked[max] = isChecked;
                spinnerSelected[max] = spinnerSel;
            } else if (positionFrom > positionTo) {
                final int min = positionTo;
                final int max = positionFrom;
                final String data = prefs[max];
                final int resId = resids[max];
                final boolean isChecked = checked[max];
                final int spinnerSel = spinnerSelected[max];
                i = max;
                while (i > min) {
                    prefs[i] = prefs[i-1];
                    resids[i] = resids[i-1];
                    checked[i] = checked[i-1];
                    spinnerSelected[i] = spinnerSelected[i-1];
                    i--;
                }
                prefs[min] = data;
                resids[min] = resId;
                checked[min] = isChecked;
                spinnerSelected[min] = spinnerSel;
            }
            mDraggingPosition = positionTo;
            mListView.invalidateViews();
            return positionTo;
        }
        
        @Override
        public boolean onStopDrag(int positionFrom, int positionTo) {
            mDraggingPosition = -1;
            mListView.invalidateViews();
            mIsDragging = false;
            return super.onStopDrag(positionFrom, positionTo);
        }
    }
}
