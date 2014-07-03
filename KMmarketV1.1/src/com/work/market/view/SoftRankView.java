package com.work.market.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.byfen.market.R;

public class SoftRankView extends ObjectView
{
	private Context mContext;
	private Activity m_MainActivity;
	private ProgressDialog pd;
	private Button m_rank_week;
	private Button m_rank_month;
	private Button m_rank_all;
	private String m_url_week = "";
	private String m_url_month = "";
	private String m_url_all = "";

	private SoftlistView m_SoftlistView_week;
	private SoftlistView m_SoftlistView_month;
	private SoftlistView m_SoftlistView_total;
	private int mPageIndex = 0;

	public SoftRankView(Context context, Activity aMainActivity)
	{
		this(context, null, aMainActivity);
		mContext = context;
	}

	public SoftRankView(Context context, AttributeSet attrs,
			Activity aMainActivity)
	{
		super(context, attrs);
		mContext = context;
		m_MainActivity = aMainActivity;
		LayoutInflater.from(context).inflate(R.layout.softpaixu, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		mPageIndex = 1;
		m_rank_week = (Button) findViewById(R.id.softpaixu_rank_week);
		m_rank_week.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				m_rank_week
						.setBackgroundResource(R.drawable.toptab_left_pressed);
				m_rank_month.setBackgroundResource(R.drawable.toptab_mid_nor);
				m_rank_all.setBackgroundResource(R.drawable.toptab_right_nor);
				m_rank_week.setTextColor(getResources()
						.getColor(R.color.whites));
				m_rank_month.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));
				m_rank_all.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));
				m_SoftlistView_month.setVisibility(View.GONE);
				m_SoftlistView_week.setVisibility(View.GONE);
				m_SoftlistView_total.setVisibility(View.GONE);
				m_SoftlistView_week.setVisibility(View.VISIBLE);
				onResume(1);
				mPageIndex = 1;
			}
		});
		m_rank_month = (Button) findViewById(R.id.softpaixu_rank_month);
		m_rank_month.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				m_rank_week.setBackgroundResource(R.drawable.toptab_left_nor);
				m_rank_month
						.setBackgroundResource(R.drawable.toptab_mid_pressed);
				m_rank_all.setBackgroundResource(R.drawable.toptab_right_nor);

				m_rank_month.setTextColor(getResources().getColor(
						R.color.whites));
				m_rank_week.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));
				m_rank_all.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));

				onResume(2);
				mPageIndex = 2;
				m_SoftlistView_month.setVisibility(View.GONE);
				m_SoftlistView_week.setVisibility(View.GONE);
				m_SoftlistView_total.setVisibility(View.GONE);
				m_SoftlistView_month.setVisibility(View.VISIBLE);
			}
		});
		m_rank_all = (Button) findViewById(R.id.softpaixu_rank_all);
		m_rank_all.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				m_rank_week.setBackgroundResource(R.drawable.toptab_left_nor);
				m_rank_month.setBackgroundResource(R.drawable.toptab_mid_nor);
				m_rank_all
						.setBackgroundResource(R.drawable.toptab_right_pressed);

				m_rank_all
						.setTextColor(getResources().getColor(R.color.whites));
				m_rank_month.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));
				m_rank_week.setTextColor(mContext.getResources().getColor(
						R.color.toptab_TextColor_normal));
				onResume(3);
				m_SoftlistView_month.setVisibility(View.GONE);
				m_SoftlistView_week.setVisibility(View.GONE);
				m_SoftlistView_total.setVisibility(View.VISIBLE);
				mPageIndex = 3;
			}
		});

		m_SoftlistView_week = (SoftlistView) findViewById(R.id.m_SoftlistView_week);
		m_SoftlistView_month = (SoftlistView) findViewById(R.id.m_SoftlistView_month);
		m_SoftlistView_total = (SoftlistView) findViewById(R.id.m_SoftlistView_all);

		m_rank_week.setBackgroundResource(R.drawable.toptab_left_pressed);
		m_rank_month.setBackgroundResource(R.drawable.toptab_mid_nor);
		m_rank_all.setBackgroundResource(R.drawable.toptab_right_nor);
		m_rank_week.setTextColor(getResources().getColor(R.color.whites));
		m_rank_month.setTextColor(mContext.getResources().getColor(
				R.color.toptab_TextColor_normal));
		m_rank_all.setTextColor(mContext.getResources().getColor(
				R.color.toptab_TextColor_normal));

	}

	public void Init(String kind, String type,int is_modify,String lang,int m_min_file_size)
	{

		if (type.equals(" "))
		{
			type = "0";
		}
		String url="http://api.byfen.com/list/rank?" + "kind="
		+ kind +"&type=" + type+"&is_modify=" + is_modify+"&lang=" + lang+"&min_file_size=" + m_min_file_size + "&";
		
		m_url_week = url+"sort=week&";
		m_url_month = url+"sort=month&";
		m_url_all = url+"sort=total&";
		
		m_SoftlistView_week.SetUrl(m_url_week);
		m_SoftlistView_month.SetUrl(m_url_month);
		m_SoftlistView_total.SetUrl(m_url_all);
		//onResume(1);
	}

	private void onResume(int anum)
	{
		if (anum == 1)
		{
			m_SoftlistView_week.onResume();
		}
		else if (anum == 2)
		{
			m_SoftlistView_month.onResume();
		}
		else if (anum == 3)
		{
			m_SoftlistView_total.onResume();
		}
	}
	private void onPause(int anum)
	{
		if (anum == 1)
		{
			m_SoftlistView_week.onResume();
		}
		else if (anum == 2)
		{
			m_SoftlistView_month.onResume();
		}
		else if (anum == 3)
		{
			m_SoftlistView_total.onResume();
		}
	}
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		 onResume(mPageIndex);
	}
	public void NotifyDataSetChanged()
	{

		if (mPageIndex == 1)
		{
			m_SoftlistView_week.NotifyDataSetChanged();
		}
		else if (mPageIndex == 2)
		{
			m_SoftlistView_month.NotifyDataSetChanged();
		}
		else if (mPageIndex == 3)
		{
			m_SoftlistView_total.NotifyDataSetChanged();
		}
	}
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		onPause(mPageIndex);
	}

}
