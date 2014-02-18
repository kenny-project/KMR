package com.kenny.file.bean;

import java.util.ArrayList;
import java.util.List;

//KMenuGroup
public class KMenuGroupBean implements CharSequence {
	private int ID = 0;
	private String title = "";//分组Title
	private ArrayList<KMenuItemBean> items = new ArrayList<KMenuItemBean>();

	public int AddDictBean(KMenuItemBean bean) {
		items.add(bean);
		return 1;
	}

	public int AddAllDictBean(List<KMenuItemBean> beans) {
		items.addAll(beans);
		return 1;
	}
	public int ItemSize() {
		return items.size();
	}

	public KMenuItemBean get(int pos) {
		if (items.size() > pos) {
			return items.get(pos);
		} else {
			return null;
		}
	}

	public void Clear() {
		items.clear();
	}

	public int getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = Integer.valueOf(iD);
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public char charAt(int index) {
		// TODO Auto-generated method stub
		return title.charAt(index);
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return title.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return title.subSequence(start, end);
	}
}
