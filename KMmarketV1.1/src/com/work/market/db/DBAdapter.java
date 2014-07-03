package com.work.market.db;

import java.util.ArrayList;

import com.work.market.bean.AppListBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库工具类
 * @author zhou
 *
 */

public class DBAdapter {

	private Context mContext;
	private static DBAdapter dbAdapter;
	private ShanpaiDBHelper fashionDBHelper;
	private SQLiteDatabase db;
	
	/**
	 * 创建数据库
	 * @param context
	 * @return
	 */
	public static DBAdapter createDBAdapter(Context context) {
		if(dbAdapter == null) {
			dbAdapter = new DBAdapter(context);
			dbAdapter.openDB();
		}
		return dbAdapter;
	}
	
	/**
	 * 关闭数据库
	 */
	public static void close() {
		if(dbAdapter != null) {
			dbAdapter.closeDB();
			dbAdapter = null;
		}
	}
	
	
	private DBAdapter(Context context) {
		this.mContext = context;
		fashionDBHelper = new ShanpaiDBHelper(mContext);
	}
	
	public void openDB() {
		db = fashionDBHelper.getWritableDatabase();
	}
	
	private void closeDB() {
		fashionDBHelper.close();
	}

	
	/**
	 * Message 插入
	 */
	public boolean insertMessage(int messageId, String messagepn, String messageTitle, String userKey) {
//		if(!queryMessageById(messageId, userKey)) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(ShanpaiDBHelper.MESSAGE_ID, messageId);
			initialValues.put(ShanpaiDBHelper.MESSAGE_TIME, messageTitle);
			initialValues.put(ShanpaiDBHelper.MESSAGE_INFO, messagepn);
			initialValues.put(ShanpaiDBHelper.MESSAGE_USERKEY, userKey);
			return db.insert(ShanpaiDBHelper.TABLE_MESSAGE, null, initialValues) > 0;
//		}
//		return false;
	}
	
	/**
	 * 添加已经下载的软件
	 * @param id
	 * @param title
	 * @param pn
	 * @param logo
	 * @param size
	 * @param appurl
	 * @param version
	 * @param appfileext
	 * @param userKey 调用Key
	 * @return
	 */
	public boolean insertfinish(AppListBean bean,String userKey) 
	{
//		if(!queryMessageById(messageId, userKey)) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(ShanpaiDBHelper.DISCOUNT_ID,bean.getId());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_TITLE,bean.getTitle());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_PN,bean.getPn());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_LOGO, bean.getLogourl());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_SIZE, bean.getSize());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_APPURL,bean.getAppurl());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_VERSION,bean.getVercode());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_FILENAME,bean.getFileName());
			initialValues.put(ShanpaiDBHelper.DISCOUNT_APPFILEEXT,bean.getAppFileExt());
			initialValues.put(ShanpaiDBHelper.MESSAGE_USERKEY, userKey);
			return db.insert(ShanpaiDBHelper.TABLE_FINISH, null, initialValues) > 0;
		
//		}
//		return false;
	}
	
	/**
	 * Message 查询所有已经下载的软件
	 */
	public ArrayList<DBdatafinishModel> queryAllFinish(String userKey) {
		ArrayList<DBdatafinishModel> result = new ArrayList<DBdatafinishModel>();
		Cursor cursor = db.query(ShanpaiDBHelper.TABLE_FINISH, new String[]{
				ShanpaiDBHelper.DISCOUNT_ID, ShanpaiDBHelper.DISCOUNT_TITLE, 
				ShanpaiDBHelper.DISCOUNT_PN, ShanpaiDBHelper.DISCOUNT_LOGO, 
				ShanpaiDBHelper.DISCOUNT_SIZE, ShanpaiDBHelper.DISCOUNT_APPURL, 
				ShanpaiDBHelper.DISCOUNT_VERSION,ShanpaiDBHelper.DISCOUNT_FILENAME, ShanpaiDBHelper.DISCOUNT_APPFILEEXT,
				ShanpaiDBHelper.MESSAGE_USERKEY
		}, ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null, null, null, "_id desc");
		if(cursor == null) {
			return null;
		}
		while(cursor.moveToNext()) {
			DBdatafinishModel model = new DBdatafinishModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_ID)));
			model.setTitle(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_TITLE)));
			model.setPn(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_PN)));			
			model.setlogurl(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_LOGO)));
			model.setsize(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_SIZE)));
			model.setappurl(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_APPURL)));
			model.setversioncode(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_VERSION)));
			model.setFileName(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_FILENAME)));
			model.setAppFileExt(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_APPFILEEXT)));
			result.add(model);
		}
		cursor.close();
		return result;
	}
	
	/**
	 * Message 通过id删除
	 */
	public boolean deleteFinishById(int Id, String userKey) {
		return db.delete(ShanpaiDBHelper.TABLE_FINISH, ShanpaiDBHelper.DISCOUNT_ID + "='" + Id + 
				"' and " + ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null) > 0;
	}
	
	/**
	 * Message 删除所有
	 */
	public boolean deleteAllFinish(String userKey) {
		return db.delete(ShanpaiDBHelper.TABLE_FINISH, ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null) > 0;
	}
	
	/**
	 * Message 通过id删除
	 */
	public boolean deleteMessageById(String messageId, String userKey) 
	{
		return deleteMessageById(String.valueOf(messageId), userKey);
	}
	/**
	 * Message 通过id删除
	 */
	public boolean deleteMessageById(int messageId, String userKey) {
		boolean result= db.delete(ShanpaiDBHelper.TABLE_MESSAGE, ShanpaiDBHelper.MESSAGE_ID + "='" + messageId + 
				"' and " + ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null) > 0;
				return result;
	}
	
	/**
	 * Message 删除所有
	 */
	public boolean deleteAllMessage(String userKey) {
		return db.delete(ShanpaiDBHelper.TABLE_MESSAGE, ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null) > 0;
	}
	
	/**
	 * Message 通过id查询
	 */
	public boolean queryMessageById(String messageId, String userKey) {
		boolean result = false;
		Cursor cursor = db.query(ShanpaiDBHelper.TABLE_MESSAGE, new String[]{
				ShanpaiDBHelper.MESSAGE_ID, ShanpaiDBHelper.MESSAGE_TIME, ShanpaiDBHelper.MESSAGE_INFO
		}, ShanpaiDBHelper.MESSAGE_ID + "='" + messageId + "' and " + 
				ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null, null, null, null);
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				result = true;
			}
		}
		cursor.close();
		return result;
	}
	
	/**
	 * Message 查询所有
	 */
	public ArrayList<DBdataModel> queryAllMessage(String userKey) {
		ArrayList<DBdataModel> result = new ArrayList<DBdataModel>();
		Cursor cursor = db.query(ShanpaiDBHelper.TABLE_MESSAGE, new String[]{
				ShanpaiDBHelper.MESSAGE_ID, ShanpaiDBHelper.MESSAGE_TIME, ShanpaiDBHelper.MESSAGE_INFO
		}, ShanpaiDBHelper.MESSAGE_USERKEY + "='" + userKey + "'", null, null, null, "_id desc");
		if(cursor == null) {
			return null;
		}
		while(cursor.moveToNext()) {
			DBdataModel model = new DBdataModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.MESSAGE_ID)));
			model.setTitle(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.MESSAGE_TIME)));
			model.setPn(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.MESSAGE_INFO)));			
			result.add(model);
		}
		cursor.close();
		return result;
	}
	/**
	 * Photo 插入
	 */
	public boolean insertPhoto(String photoPath, String photoKey, String photoT, String photoUrl, String photoTime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(ShanpaiDBHelper.PHOTO_PATH, photoPath);
		initialValues.put(ShanpaiDBHelper.PHOTO_KEY, photoKey);
		initialValues.put(ShanpaiDBHelper.PHOTO_T, photoT);
		initialValues.put(ShanpaiDBHelper.PHOTO_URL, photoUrl);
		initialValues.put(ShanpaiDBHelper.PHOTO_TIME, photoTime);
		return db.insert(ShanpaiDBHelper.TABLE_PHOTO, null, initialValues) > 0;
	}
	
	/**
	 * Photo 通过id删除
	 */
	public boolean deletePhotoBy_Id(String _id) {
		return db.delete(ShanpaiDBHelper.TABLE_PHOTO, "_id='" + _id + "'", null) > 0;
	}
	
	/**
	 * Photo 删除所有
	 */
	public boolean deleteAllPhoto() {
		return db.delete(ShanpaiDBHelper.TABLE_PHOTO, null, null) > 0;
	}
	
	/**
	 * Photo 通过id查询
	 */
//	public PhotoModel queryMessageBy_Id(String _id) {
//		PhotoModel model = null;
//		Cursor cursor = db.query(ShanpaiDBHelper.TABLE_PHOTO, new String[]{
//				ShanpaiDBHelper.PHOTO_PATH, ShanpaiDBHelper.PHOTO_KEY, 
//				ShanpaiDBHelper.PHOTO_T, ShanpaiDBHelper.PHOTO_URL, ShanpaiDBHelper.PHOTO_TIME
//		}, "_id=" + _id, null, null, null, null);
//		if(cursor == null) {
//			return null;
//		}
//		while(cursor.moveToNext()) {
//			model = new PhotoModel();
//			model.set_id(_id);
//			model.setPhotoPath(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_PATH)));
//			model.setPhotoKey(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_KEY)));
//			model.setT(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_T)));
//			model.setUrl(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_URL)));
//			model.setTime(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_TIME)));
//		}
//		cursor.close();
//		return model;
//	}
	
//	/**
//	 * Photo 查询所有
//	 */
//	public ArrayList<PhotoModel> queryAllPhoto() {
//		ArrayList<PhotoModel> result = new ArrayList<PhotoModel>();
//		Cursor cursor = db.query(ShanpaiDBHelper.TABLE_PHOTO, new String[]{
//				"_id", ShanpaiDBHelper.PHOTO_PATH, ShanpaiDBHelper.PHOTO_KEY, 
//				ShanpaiDBHelper.PHOTO_T, ShanpaiDBHelper.PHOTO_URL, ShanpaiDBHelper.PHOTO_TIME
//		}, null, null, null, null, "_id desc");
//		if(cursor == null) {
//			return null;
//		}
//		while(cursor.moveToNext()) {
//			PhotoModel model = new PhotoModel();
//			model.set_id(cursor.getString(cursor.getColumnIndex("_id")));
//			model.setPhotoPath(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_PATH)));
//			model.setPhotoKey(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_KEY)));
//			model.setT(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_T)));
//			model.setUrl(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_URL)));
//			model.setTime(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.PHOTO_TIME)));
//			result.add(model);
//		}
//		cursor.close();
//		return result;
//	}
	
	
//	/**
//	 * Discount 添加优惠券
//	 */
//	public boolean addDiscount(boolean unUse, String discountId, String discountImg, String discountName, 
//			String discountInfo, String discountHelp, String userKey, String time, String price, int one) {
//		DiscountModel model = queryDiscountById(unUse, discountId, userKey);
//		if(model != null) {
//			int num = model.getDiscountNum();
//			num++;
//			return updateDiscount(unUse, discountId, num, userKey);
//		} else {
//			return insertDiscount(unUse, discountId, discountImg, discountName, discountInfo, discountHelp, userKey, time, price, one);
//		}
//	}
	
//	/**
//	 * Discount 使用优惠券
//	 */
//	public boolean useDiscount(String discountId, String userKey) {
//		DiscountModel model = queryDiscountById(true, discountId, userKey);
//		if(model != null) {
//			boolean flag = addDiscount(false, discountId, model.getDiscountImgName(), model.getDiscountName(), 
//					model.getDiscountInfo(), model.getDiscountHelp(), userKey, model.getDiscountTime(), 
//					model.getDiscountPrice(), model.getDiscountOne());
//			if(flag) {
//				return deleteDiscountOneById(true, discountId, userKey);
//			}
//		}
//		return false;
//	}
	
//	/**
//	 * Discount 删除单张优惠券
//	 */
//	public boolean deleteDiscountOneById(boolean unUse, String discountId, String userKey) {
//		DiscountModel model = queryDiscountById(unUse, discountId, userKey);
//		if(model != null) {
//			int num = model.getDiscountNum();
//			if(num > 1) {
//				num--;
//				return updateDiscount(unUse, discountId, num, userKey);
//			} else {
//				return deleteDiscountById(unUse, discountId, userKey);
//			}
//		}
//		return false;
//	}
	
//	/**
//	 * Discount 通过id删除优惠券信息
//	 */
//	public boolean deleteDiscountById(boolean unUse, String discountId, String userKey) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		
//		return db.delete(table, ShanpaiDBHelper.DISCOUNT_ID + "='" + discountId + 
//				"' and " + ShanpaiDBHelper.DISCOUNT_USERKEY + "='" + userKey + "'", null) > 0;
//	}
//	
//	/**
//	 * Discount 通过id查询优惠券信息
//	 */
//	public DiscountModel queryDiscountById(boolean unUse, String discountId, String userKey) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		
//		DiscountModel model = null;
//		Cursor cursor = db.query(table, new String[]{
//				ShanpaiDBHelper.DISCOUNT_ID, ShanpaiDBHelper.DISCOUNT_IMG, ShanpaiDBHelper.DISCOUNT_NAME, 
//				ShanpaiDBHelper.DISCOUNT_INFO, ShanpaiDBHelper.DISCOUNT_HELP, ShanpaiDBHelper.DISCOUNT_NUM, 
//				ShanpaiDBHelper.DISCOUNT_ONE, ShanpaiDBHelper.DISCOUNT_TIME, ShanpaiDBHelper.DISCOUNT_PRICE
//		}, ShanpaiDBHelper.DISCOUNT_ID + "='" + discountId + 
//		"' and " + ShanpaiDBHelper.DISCOUNT_USERKEY + "='" + userKey + "'", null, null, null, null);
//		if(cursor == null) {
//			return null;
//		}
//		while(cursor.moveToNext()) {
//			model = new DiscountModel();
//			model.setDiscountId(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_ID)));
//			model.setDiscountImgName(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_IMG)));
//			model.setDiscountName(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_NAME)));
//			model.setDiscountInfo(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_INFO)));
//			model.setDiscountHelp(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_HELP)));
//			model.setDiscountNum(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_NUM)));
//			model.setDiscountOne(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_ONE)));
//			model.setDiscountTime(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_TIME)));
//			model.setDiscountPrice(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_PRICE)));
//		}
//		cursor.close();
//		return model;
//	}
//	
//	/**
//	 * Discount 查询所有优惠券信息
//	 */
//	public ArrayList<DiscountModel> queryAllDiscount(boolean unUse, String userKey) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		
//		ArrayList<DiscountModel> result = new ArrayList<DiscountModel>();
//		Cursor cursor = db.query(table, new String[]{
//				ShanpaiDBHelper.DISCOUNT_ID, ShanpaiDBHelper.DISCOUNT_IMG, ShanpaiDBHelper.DISCOUNT_NAME, 
//				ShanpaiDBHelper.DISCOUNT_INFO, ShanpaiDBHelper.DISCOUNT_HELP, ShanpaiDBHelper.DISCOUNT_NUM, 
//				ShanpaiDBHelper.DISCOUNT_ONE, ShanpaiDBHelper.DISCOUNT_TIME, ShanpaiDBHelper.DISCOUNT_PRICE
//		}, ShanpaiDBHelper.DISCOUNT_USERKEY + "='" + userKey + "'", null, null, null, "_id desc");
//		if(cursor == null) {
//			return null;
//		}
//		while(cursor.moveToNext()) {
//			DiscountModel model = new DiscountModel();
//			model.setDiscountId(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_ID)));
//			model.setDiscountImgName(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_IMG)));
//			model.setDiscountName(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_NAME)));
//			model.setDiscountInfo(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_INFO)));
//			model.setDiscountHelp(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_HELP)));
//			model.setDiscountNum(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_NUM)));
//			model.setDiscountOne(cursor.getInt(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_ONE)));
//			model.setDiscountTime(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_TIME)));
//			model.setDiscountPrice(cursor.getString(cursor.getColumnIndex(ShanpaiDBHelper.DISCOUNT_PRICE)));
//			result.add(model);
//		}
//		cursor.close();
//		return result;
//	}
//	
//	/**
//	 * Discount 删除所有优惠券信息
//	 */
//	public boolean deleteAllDiscount(boolean unUse, String userKey) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		return db.delete(table, ShanpaiDBHelper.DISCOUNT_USERKEY + "='" + userKey + "'", null) > 0;
//	}
//	
//	/**
//	 * Discount 插入优惠券
//	 */
//	private boolean insertDiscount(boolean unUse, String discountId, String discountImg, String discountName, 
//			String discountInfo, String discountHelp, String userKey, String time, String price, int one) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		
//		ContentValues initialValues = new ContentValues();
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_ID, discountId);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_IMG, discountImg);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_NAME, discountName);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_INFO, discountInfo);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_HELP, discountHelp);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_NUM, 1);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_USERKEY, userKey);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_ONE, one);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_TIME, time);
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_PRICE, price);
//		return db.insert(table, null, initialValues) > 0;
//	}
//	
//	/**
//	 * Discount 更新优惠券
//	 */
//	private boolean updateDiscount(boolean unUse, String discountId, int num, String userKey) {
//		String table = null;
//		if(unUse) {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT1;
//		} else {
//			table = ShanpaiDBHelper.TABLE_DISCOUNT2;
//		}
//		
//		ContentValues initialValues = new ContentValues();
//		initialValues.put(ShanpaiDBHelper.DISCOUNT_NUM, num);
//		return db.update(table, initialValues, 
//				ShanpaiDBHelper.DISCOUNT_ID + "='" + discountId + 
//				"' and " + ShanpaiDBHelper.DISCOUNT_USERKEY + "='" + userKey + "'", null) > 0;
//	}
	
}
