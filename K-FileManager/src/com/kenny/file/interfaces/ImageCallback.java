package com.kenny.file.interfaces;

import android.graphics.drawable.Drawable;

public interface ImageCallback
{
    public void imageLoaded(Drawable imageDrawable, String imageUrl);
    
   // public void imageLoaded(String name,Drawable imageDrawable, String imageUrl);
}
