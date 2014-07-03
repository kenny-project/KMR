package com.byfen.market;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.work.market.db.DBAdapter;
import com.work.market.db.DBdataModel;
import com.work.market.net.Common;

public class FavoritesActivity extends Activity {

	private ArrayList<DBdataModel> m_collect_data;
	private ListView lv;
	private MyAdapter mMyAdapter;
	private Context mContext;
	private LinearLayout m_back;
	private Button m_dele_button;

	/**
	 * 进入页面
	 * 
	 * @param savedInstanceState
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		// 加载页面
		setContentView(R.layout.collection);
		mContext = this;
		// 异常处理
		m_collect_data = new ArrayList<DBdataModel>();
		lv = (ListView) findViewById(R.id.collection_ls);
		mMyAdapter = new MyAdapter(this);
		lv.setAdapter(mMyAdapter);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 提示用户是否删除消息
				new AlertDialog.Builder(mContext)
						.setTitle("提示")
						.setMessage("是否删除")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int arg1) {
										dialog.dismiss();
										DBAdapter.createDBAdapter(mContext)
												.deleteMessageById(
														m_collect_data.get(
																position)
																.getId(),
														Common.user_key);
										m_collect_data.remove(position);
										mMyAdapter.notifyDataSetChanged();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int arg1) {
										dialog.dismiss();
									}
								}).show();
				return false;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent seta = new Intent(mContext, productActivity.class);
				Bundle bundle = new Bundle();
				String dialogstring = m_collect_data.get(pos).getTitle();
				bundle.putString("title", dialogstring);
				dialogstring = m_collect_data.get(pos).getPn();
				bundle.putString("pn", dialogstring);
				int itemid = Integer.valueOf(m_collect_data.get(pos).getId());
				bundle.putInt("id", itemid);
				seta.putExtras(bundle);
				startActivity(seta);// SoftclassActivity
			}
		});

		m_back = (LinearLayout) findViewById(R.id.collection_back);// @+id/collection_back
		m_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		m_dele_button = (Button) findViewById(R.id.collection_dele);// @+id/collection_dele
		m_dele_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(mContext)
						.setTitle("提示")
						.setMessage("是否全部删除")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int arg1) {
										dialog.dismiss();
										DBAdapter.createDBAdapter(mContext)
												.deleteAllMessage(
														Common.user_key);
										m_collect_data.clear();
										mMyAdapter.notifyDataSetChanged();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int arg1) {
										dialog.dismiss();
									}
								}).show();

			}
		});

		IniData();

	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		public Context mcontext;

		public MyAdapter(Context context) {

			super();

			mcontext = context;
			inflater = LayoutInflater.from(mcontext);
		}

		public int getCount() {

			// TODO Auto-generated method stub
			return m_collect_data.size();
		}

		public Object getItem(int arg0) {

			// TODO Auto-generated method stub
			return arg0;
		}

		public long getItemId(int arg0) {

			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(final int position, View view, ViewGroup arg2) {

			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.collectionitem, null);
			}

			if (m_collect_data.size() > 0) {
				final TextView myButton = (TextView) view
						.findViewById(R.id.collection_item_text1);// <Button
																	// @+id/collection_item_text1
				int nums = position + 1;
				myButton.setText(nums + "");

				final TextView mytext = (TextView) view
						.findViewById(R.id.collection_item_text);// @+id/zhaongjiang_item1_text1
																	// @+id/collection_item_text
				mytext.setText(m_collect_data.get(position).getTitle());

			}

			return view;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	void IniData() {
		ArrayList<DBdataModel> list = DBAdapter.createDBAdapter(mContext)
				.queryAllMessage(Common.user_key);
		boolean templog = false;
		for (int i = list.size() - 1; i >= 0; i--) {
			DBdataModel model = list.get(i);
			m_collect_data.add(model);

		}
		mMyAdapter.notifyDataSetChanged();
	}

}
