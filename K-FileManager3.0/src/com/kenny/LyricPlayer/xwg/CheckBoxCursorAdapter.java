package com.kenny.LyricPlayer.xwg;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;

public class CheckBoxCursorAdapter extends SimpleCursorAdapter {
	private ArrayList<Integer> selection = new ArrayList<Integer>(); 
	private int mCheckBoxId = 0;
	private String mIdColumn;
	
	public CheckBoxCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int checkBoxId, String idColumn) {
		super(context, layout, c, from, to);
		mCheckBoxId = checkBoxId;
		mIdColumn = idColumn;
	}

	
	public int getCount() {
		return super.getCount();
	}

	
	public Object getItem(int position) {
		return super.getItem(position);
	}

	
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		CheckBox checkbox = (CheckBox)view.findViewById(mCheckBoxId);
		checkbox.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				Cursor cursor = getCursor();
				cursor.moveToPosition(position);
				int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(mIdColumn));
				int index = selection.indexOf(rowId);
				if (index != -1) {  
					selection.remove(index);  
				} else {  
					selection.add(rowId);  
				}  
			}
		});
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(mIdColumn));
		if (selection.indexOf(rowId)!= -1) {  
            checkbox.setChecked(true);  
        } else {  
        	checkbox.setChecked(false);  
		}  
		return view;
	}
	
	ArrayList<Integer> getSelectedItems(){
		return selection;
	}
}
