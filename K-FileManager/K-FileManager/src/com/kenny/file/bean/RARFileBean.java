package com.kenny.file.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.kenny.file.sort.FileSort;

import de.innosystec.unrar.rarfile.FileHeader;

public class RARFileBean extends FileBean {
	private HashMap<String, RARFileBean> item = new HashMap<String, RARFileBean>();

	protected void setItem(HashMap<String, RARFileBean> item) {
		this.item = item;
	}

	public RARFileBean(String fileName, String filePath,
			HashMap<String, RARFileBean> parent) {
		super(null, fileName, filePath, false);
		if (parent != null && !item.containsKey("..")) {
			RARFileBean temp = new RARFileBean("..", "..", null);
			temp.setDirectory(true);
			temp.setItem(parent);
			item.put("back", temp);
		}
	}

	public File getFile() {
		return null;
	}

	public boolean AddItem(String key, FileHeader entry) {
		int pos = key.indexOf("\\");

		if (pos == -1 || pos + 1 == key.length()) {// 根目录
			if (pos != -1) {
				key = key.substring(0, pos);
			}
			if (!item.containsKey(key)) {
				RARFileBean temp = new RARFileBean(key, key, item);
				temp.setDirectory(entry.isDirectory());
				temp.setLength(Long.valueOf(entry.getDataSize()));
				item.put(key, temp);
			}
			return true;
		}
		else 
		{
			String first = key.substring(0, pos);
			String end = key.substring(pos + 1);
			if (!item.containsKey(first)) 
			{
				RARFileBean temp = new RARFileBean(first, first, item);
				temp.setDirectory(true);
				item.put(first, temp);
				temp.AddItem(end, entry);
			}
			else 
			{
				item.get(first).AddItem(end, entry);
			}
			return true;
		}
	}

	public int getItemCount() {
		return item.size() - 1;
	}

	public RARFileBean GetItem(String key) {
		return item.get(key);
	}

	public Collection<RARFileBean> getCollectionItem() {
		ArrayList<RARFileBean> mList = new ArrayList<RARFileBean>();
		for (RARFileBean temp : item.values()) {
			mList.add(temp);
		}
		Collections.sort(mList, new FileSort());
		return mList;
	}

}
