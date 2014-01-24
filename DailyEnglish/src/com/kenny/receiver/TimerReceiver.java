package com.kenny.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;

import com.kenny.activity.V6LoadPage;
import com.kenny.dailyenglish.R;
import com.kenny.util.Const;
import com.kenny.util.Log;
import com.kenny.util.Utils;

public class TimerReceiver extends BroadcastReceiver {

	private Notification noti;
	private NotificationManager notiManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int minute = calendar.get(Calendar.MINUTE);
		int hour = calendar.get(Calendar.HOUR);
		boolean isAutoPush = Utils.get(context, "autoPush", "0")
				.equals("1");
		boolean isNightReceive = Utils.get(context, "nightReceive",
				"0").equals("1");
		if (isAutoPush == true) {
		if (intent == null || intent.getAction() == null){
			return;
		}
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				// 自动推送开
				if (hour % 2 == 0
						&& minute == Integer.valueOf(Utils.get(context,
								"pushMinute", "0"))
						&& (hour <= 22 || hour >= 6)) {
					// 每两小时一次，并且在白天(7点到22点), 随机一个分钟数，避免同一时间大量请求
					// 推送
					push(context);
				}

				if (hour % 2 == 0
						&& minute == Integer.valueOf(Utils.get(context,
								"pushMinute", "0"))
						&& (hour >= 22 || hour <= 6) && isNightReceive == true) {
					// 每两小时一次，并且是晚上(23点到6点),并且夜间推送要开, 随机一个分钟数，避免同一时间大量请求
					// 推送
					push(context);
				}
			}
		}
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			// 网络联接状态改变
			if (!Utils.isNetConnectNoMsg(context)){
				return;
			}
			if (isAutoPush == true){
				if ((hour <= 22 || hour >= 6)) {
					// 每两小时一次，并且在白天(7点到22点), 随机一个分钟数，避免同一时间大量请求
					// 推送
					push(context);
				}

				if ((hour >= 22 || hour <= 6) && isNightReceive == true) {
					// 每两小时一次，并且是晚上(23点到6点),并且夜间推送要开, 随机一个分钟数，避免同一时间大量请求
					// 推送
					push(context);
				}	
			}
		}
	}

	public void push(final Context context) {
		if (noti == null) {
			noti = new Notification();
		}

		if (notiManager == null) {
			String service = Context.NOTIFICATION_SERVICE;
			notiManager = (NotificationManager) context
					.getSystemService(service);
		}
		new Thread() {
			@Override
			public void run() {
				if (Utils.isNetConnectNoMsg(context) == false) {
					// 如果没有联网，退出
					return;
				}
				String gname = "";
				String url = "";
				int dataType = 0;
				int nFlag = 0;
				String itemTitle = "";
				String sendType = "";
				try {
					StringBuffer sbResult = new StringBuffer();
					URL urlU;
					String strUrl = Const.PUSH_URL;
					if (Utils.get(context, "dailySentence", "").equals(
							"1")) {
						// 请求每日一句
						sendType = sendType + "3,";
					}
					if (Utils.get(context, "bilingualInfo", "").equals(
							"1")) {
						// 请求双语资讯
						sendType = sendType + "2,";
					}
					if (Utils.get(context, "systemMessage", "").equals(
							"1")) {
						// 请求系统消息
						sendType = sendType + "5,";
					}
					if (sendType.length() != 0) {
						// 把最后一个逗号截掉
						sendType = sendType.substring(0, sendType.length() - 1);
						strUrl = strUrl + "?type=" + sendType;
					} else {
						return;
					}

					String pushTime = Utils.get(context, "getPushTime",
							"");

					if (!pushTime.equals("")) {
						// 上回取得过推送（有取得推送时间）
						strUrl = strUrl + "&t=" + pushTime;
					}

					urlU = new URL(strUrl);
					HttpURLConnection conn = (HttpURLConnection) urlU
							.openConnection();
					// 这个要打开
					conn.setDoOutput(true);
					conn.setDoInput(true);
					// 处理POST的数据
					conn.setRequestMethod("GET");
					conn.getOutputStream().close();
					InputStream inputStream = conn.getInputStream();

					BufferedReader br = new BufferedReader(
							new InputStreamReader(inputStream, "utf-8"));
					String data = "";

					if (sbResult.toString().equalsIgnoreCase("")) {
						while ((data = br.readLine()) != null) {
							sbResult.append(data);
						}
					}
					inputStream.close();
					if (!(sbResult.toString().length() == 0)) {
						JSONObject json = new JSONObject(sbResult.toString());
						String type = json.getString("type");
						url = json.getString("url");
						itemTitle = json.getString("title");
						if ("1".equals(type)) {
							// 系统消息
							gname = "系统消息";
						} else if ("2".equals(type)) {
							// 双语资讯
							gname = "双语资讯";
							dataType = Const.Net_Bilingual_Data;
							nFlag = Const.Net_Bilingual_Data;
						} else if ("3".equals(type)) {
							// 每日一句
							gname = "每日一句";
							dataType = Const.Net_Dailysentence_Data;
							nFlag = Const.Net_Dailysentence_Data;
						} else if ("4".equals(type)) {
							// 情景对话
							gname = "情景对话";
						} else if ("5".equals(type)){
							// 系统消息
							url = json.getString("url");
							itemTitle = json.getString("title");
							Intent in = new Intent();
							in.setData(Uri.parse(url));
							in.setAction(Intent.ACTION_VIEW);
							PendingIntent pIntent = PendingIntent.getActivity(
									context, 0, in,
									PendingIntent.FLAG_UPDATE_CURRENT);
							noti.icon = R.drawable.notification_icon;
							noti.when = System.currentTimeMillis();
							noti.tickerText = itemTitle;
							noti.setLatestEventInfo(context,
									context.getString(R.string.app_name) + " "
											+ gname, itemTitle, pIntent);
							noti.flags |= Notification.FLAG_AUTO_CANCEL;
							noti.defaults = Notification.DEFAULT_SOUND;
							notiManager.notify(1009, noti);
							Utils.save(context, "getPushTime",
									(System.currentTimeMillis() / 1000) + "");
							return;
						}

						Intent acIntent = new Intent(context, V6LoadPage.class);
						acIntent.putExtra("gname", gname);
						acIntent.putExtra("url", url);
						acIntent.putExtra("imageUrl", "");
						acIntent.putExtra("dataType", dataType);
						acIntent.putExtra("nFlag", nFlag);
						acIntent.putExtra("itemTitle", itemTitle);
						acIntent.setData(Uri.parse("custom://"
								+ System.currentTimeMillis()));

						// Utils.saveString(context, "pushGName", "每日一句");
						// Utils.saveString(context, "pushUrl",
						// "http://dict-mobile.iciba.com/new/index.php?act=showContent&sid=266");
						// Utils.saveString(context, "pushImageUrl",
						// "http://dict-mobile.iciba.com/new/dailysentence/2012-08-28.jpg");
						// Utils.saveString(context, "pushDataType",
						// String.valueOf(Const.Net_Dailysentence_Data));
						// Utils.saveString(context, "pushNFlag", "16");
						// Utils.saveString(context, "pushItemTitle",
						// "没什么可大惊小怪的。");

						PendingIntent pIntent = PendingIntent.getActivity(
								context, 0, acIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						noti.icon = R.drawable.notification_icon;
						noti.when = System.currentTimeMillis();
						noti.tickerText = itemTitle;
						noti.setLatestEventInfo(context,
								context.getString(R.string.app_name) + " "
										+ gname, itemTitle, pIntent);
						noti.flags |= Notification.FLAG_AUTO_CANCEL;
						noti.defaults = Notification.DEFAULT_SOUND;
						notiManager.notify(1009, noti);
						Utils.save(context, "getPushTime",
								(System.currentTimeMillis() / 1000) + "");
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
}
