//package com.lidroid.fileexplorer.widget;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.HorizontalScrollView;
//import android.widget.LinearLayout;
//import com.lidroid.fileexplorer.model.FileBase;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//public class BreadcrumbView extends LinearLayout
//  implements View.OnClickListener, Breadcrumb
//{
//  HorizontalScrollView a;
//  private ViewGroup b;
//  private List<BreadcrumbListener> c;
//
//  public BreadcrumbView(Context paramContext)
//  {
//    super(paramContext);
//    a();
//  }
//
//  public BreadcrumbView(Context paramContext, AttributeSet paramAttributeSet)
//  {
//    super(paramContext, paramAttributeSet);
//    a();
//  }
//
//  @SuppressLint({"NewApi"})
//  public BreadcrumbView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
//  {
//    super(paramContext, paramAttributeSet, paramInt);
//    a();
//  }
//
//  private BreadcrumbItem a(FileBase paramFileBase)
//  {
//    BreadcrumbItem localBreadcrumbItem = (BreadcrumbItem)((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903066, this.b, false);
//    localBreadcrumbItem.setText(paramFileBase.getName());
//    localBreadcrumbItem.setItem(paramFileBase);
//    localBreadcrumbItem.setOnClickListener(this);
//    return localBreadcrumbItem;
//  }
//
//  private File a(String[] paramArrayOfString, int paramInt)
//  {
//    Object localObject = new File("/");
//    int i = 1;
//    while (i < paramInt)
//    {
//      File localFile = new File((File)localObject, paramArrayOfString[i]);
//      i++;
//      localObject = localFile;
//    }
//    return (File)new File((File)localObject, paramArrayOfString[paramInt]);
//  }
//
//  private void a()
//  {
//    this.c = Collections.synchronizedList(new ArrayList());
//    addView(View.inflate(getContext(), 2130903068, null));
//    this.a = ((HorizontalScrollView)findViewById(2131427395));
//    this.b = ((ViewGroup)findViewById(2131427396));
//  }
//
//  private View b()
//  {
//    return ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130903067, this.b, false);
//  }
//
//  public void addBreadcrumbListener(BreadcrumbListener paramBreadcrumbListener)
//  {
//    this.c.add(paramBreadcrumbListener);
//  }
//
//  public void changeBreadcrumbPath(FileBase paramFileBase, boolean paramBoolean)
//  {
//    ArrayList localArrayList = new ArrayList();
//    if (paramFileBase == null);
//    while (true)
//    {
//      return;
//      localArrayList.add(paramFileBase);
//      this.b.removeAllViews();
//      Object localObject = paramFileBase.getParentFile();
//      while (true)
//        if (localObject != null)
//          try
//          {
//            localArrayList.add(0, localObject);
//            FileBase localFileBase2 = ((FileBase)localObject).getParentFile();
//            localObject = localFileBase2;
//          }
//          catch (Exception localException)
//          {
//          }
//      Iterator localIterator = localArrayList.iterator();
//      for (int i = 1; localIterator.hasNext(); i = 0)
//      {
//        FileBase localFileBase1 = (FileBase)localIterator.next();
//        if (i == 0)
//          this.b.addView(b());
//        this.b.addView(a(localFileBase1));
//      }
//      this.a.post(new a(this));
//    }
//  }
//
//  public void onClick(View paramView)
//  {
//    BreadcrumbItem localBreadcrumbItem = (BreadcrumbItem)paramView;
//    int i = this.c.size();
//    for (int j = 0; j < i; j++)
//      ((BreadcrumbListener)this.c.get(j)).onBreadcrumbItemClick(localBreadcrumbItem);
//  }
//
//  public void removeBreadcrumbListener(BreadcrumbListener paramBreadcrumbListener)
//  {
//    this.c.remove(paramBreadcrumbListener);
//  }
//}