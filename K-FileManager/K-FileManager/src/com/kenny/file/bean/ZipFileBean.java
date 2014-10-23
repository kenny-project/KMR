package com.kenny.file.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;

import com.kenny.file.sort.FileSort;

public class ZipFileBean extends FileBean
{
        private HashMap<String, ZipFileBean> item   = new HashMap<String, ZipFileBean>();
        protected void setItem(HashMap<String, ZipFileBean> item)
        {
                this.item = item;
        }

        public ZipFileBean(String fileName, String filePath,
	              HashMap<String, ZipFileBean> parent)
        {
	      super(null, fileName, filePath, false);
	      if (parent!=null&&!item.containsKey(".."))
	      {
		    ZipFileBean temp = new ZipFileBean("..", "..",
			          null);
		    temp.setDirectory(true);
		    temp.setItem(parent);
		    item.put("back", temp);
	      }
        }
        @Override
        public String getDesc()
        {
        	if (mDesc != null) 
        	{
    			return mDesc;
    		} else {
    			if (isDirectory()) 
    			{
    				mDesc = item.size()+ " 个文件";
    			} else {
    				mDesc =getLength();
    			}
    		}
    		return mDesc;
        }
        public File getFile()
        {
	      return null;
        }
        
        public boolean AddItem(String key, ZipEntry entry)
        {
	      int pos = key.indexOf("/");
	      
	      if (pos == -1 || pos + 1 == key.length())
	      {
		    if (pos != -1)
		    {
			  key = key.substring(0, pos);
		    }
		    if (!item.containsKey(key))
		    {
			  ZipFileBean temp = new ZipFileBean(key, key,
				        item);
			  temp.setDirectory(entry.isDirectory());
			  temp.setLength(entry.getSize());
			  item.put(key, temp);
		    }
		    return true;
	      }
	      String first = key.substring(0, pos);
	      String end = key.substring(pos + 1);
	      if (!item.containsKey(first))
	      {
		    ZipFileBean temp = new ZipFileBean(first, first, item);
		    temp.setDirectory(true);
		    item.put(first, temp);
		    temp.AddItem(end, entry);
	      }
	      else
	      {
		    item.get(first).AddItem(end, entry);
	      }
	      // FilePut(item, 1, key.split(File.pathSeparator), entry);
	      return true;
        }
        
        public int getItemCount()
        {
	      return item.size()-1;
        }
        
        public ZipFileBean GetItem(String key)
        {
	      return item.get(key);
        }
        
        public Collection<ZipFileBean> getCollectionItem()
        {
	      ArrayList<ZipFileBean> mList=new ArrayList<ZipFileBean>();
	      for(ZipFileBean temp:item.values())
	      {
		    mList.add(temp);
	      }
	      Collections.sort(mList, new FileSort());
	      return mList;
        }
	
}
