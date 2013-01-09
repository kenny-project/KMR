/**
 * 
 */
package com.kuaipan.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import com.framework.log.P;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.model.KuaipanPublicLink;
import com.kuaipan.client.model.KuaipanURL;
import com.kuaipan.client.model.KuaipanUser;
import com.kuaipan.client.session.Session;

/**
 * @author Ilcwd
 * 
 */
public class KuaipanAPI
{
   private static final String TAG = "kuaipan";
   private static Session session;
   
   public static void setSession(Session mSession)
   {
      session = mSession;
   }
   
   public static Session getSession()
   {
      return session;
   }
   
   /**
    * @return user authorize URL.
    * @throws KuaipanIOException
    * @throws KuaipanServerException
    * @throws KuaipanAuthExpiredException
    */
   public static String requestToken() throws KuaipanIOException,
         KuaipanServerException, KuaipanAuthExpiredException
   {
      String host = KuaipanHTTPUtility.API_HOST;
      // session.unAuth(); by wmh 清空属性
      KuaipanURL url = OauthUtility.buildGetURL(host, "/open/requestToken",
	  null, session.consumer, null, true);
      P.v("session.consumer=" + session.consumer);
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      
      String key = (String) result.get("oauth_token");
      String secret = (String) result.get("oauth_token_secret");
      Boolean confirmed = (Boolean) result.get("oauth_callback_confirmed");
      
      if (key == null || secret == null)
      {
         throw new KuaipanIOException(resp.toString());
      }
      session.setTempToken(key, secret);
      return KuaipanHTTPUtility.AUTH_URL + key;
   }
   
   public static Map<String, Object> accessToken() throws KuaipanIOException,
         KuaipanServerException, KuaipanAuthExpiredException
   {
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, "/open/accessToken",
	  null, session.consumer, session.getToken(), true);
      
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      
      String key = (String) result.get("oauth_token");
      String secret = (String) result.get("oauth_token_secret");
      if (key == null || secret == null)
      {
         throw new KuaipanIOException(resp.toString());
      }
      else
      {
         return result;
      }
   }
   
   public static KuaipanUser accountInfo()
   {
      try
      {
         session.assertAuth();
         String host = KuaipanHTTPUtility.API_HOST;
         KuaipanURL url = OauthUtility.buildGetURL(host, "/1/account_info",
	     null, session.consumer, session.getToken(), false);
         
         KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
         Map<String, Object> result = parseResponseToMap(resp);
         
         KuaipanUser user = new KuaipanUser(result);
         return user;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return null;
      }
      
   }
   
   public static KuaipanPublicLink shares(String path, String name,
         String access_code)
   {
      try
      {
      StringBuffer location = new StringBuffer("/1/shares/");
      location.append(session.root);
      if (!path.startsWith("/")) location.append("/");
      location.append(path);
      
      Map<String, String> params = new TreeMap<String, String>();
      if (name != null) params.put("name", name);
      if (access_code != null) params.put("access_code", access_code);
      
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, location.toString(),
	  params, session.consumer, session.getToken(), false);
      
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      KuaipanPublicLink link = new KuaipanPublicLink(parseResponseToMap(resp));
      return link;
      }
      catch(Exception e)
      {
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    * 
    * @param path
    * @param list
    *           set true to show its sub-files if {@path} indicates a folder.
    * @return
    * @throws KuaipanIOException
    * @throws KuaipanServerException
    * @throws KuaipanAuthExpiredException
    */
   public static KuaipanFile metadata(String path, Boolean list)
   {
      try
      {
         String host = KuaipanHTTPUtility.API_HOST;
         session.assertAuth();
         StringBuffer location = new StringBuffer("/1/metadata/");
         location.append(session.root);
         if (!path.startsWith("/"))
         {
	  location.append("/");
         }
         location.append(path);
         Map<String, String> params = new TreeMap<String, String>();
         params.put("list", list.toString());
         P.v(TAG, "buildGetURL start");
         KuaipanURL url = OauthUtility.buildGetURL(host, location.toString(),
	     params, session.consumer, session.getToken(), false);
         P.v(TAG, "requestByGET start");
         KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
         P.v(TAG, "parseResponseToMap start");
         Map<String, Object> result = parseResponseToMap(resp);
         P.v(TAG, "KuaipanFile start");
         KuaipanFile file = new KuaipanFile(result);
         return file;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return null;
      }
      
   }
   
   public static KuaipanFile createFolder(String path)
         throws KuaipanIOException, KuaipanServerException,
         KuaipanAuthExpiredException
   {
      String location = "/1/fileops/create_folder";
      Map<String, String> params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("path", path);
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, location, params,
	  session.consumer, session.getToken(), false);
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      KuaipanFile file = new KuaipanFile(result);
      return file;
   }
   
   public static KuaipanFile move(String from_path, String to_path)
         throws KuaipanIOException, KuaipanServerException,
         KuaipanAuthExpiredException
   {
      String location = "/1/fileops/move";
      Map<String, String> params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("from_path", from_path);
      params.put("to_path", to_path);
      
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, location, params,
	  session.consumer, session.getToken(), false);
      
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      
      KuaipanFile file = new KuaipanFile(result);
      
      return file;
   }
   
   public static KuaipanFile copy(String from_path, String to_path)
         throws KuaipanIOException, KuaipanServerException,
         KuaipanAuthExpiredException
   {
      String location = "/1/fileops/copy";
      Map<String, String> params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("from_path", from_path);
      params.put("to_path", to_path);
      
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, location, params,
	  session.consumer, session.getToken(), false);
      
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      
      KuaipanFile file = new KuaipanFile(result);
      
      return file;
   }
   
   public static KuaipanFile delete(String path) throws KuaipanIOException,
         KuaipanServerException, KuaipanAuthExpiredException
   {
      String location = "/1/fileops/delete";
      Map<String, String> params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("path", path);
      
      String host = KuaipanHTTPUtility.API_HOST;
      KuaipanURL url = OauthUtility.buildGetURL(host, location, params,
	  session.consumer, session.getToken(), false);
      
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.requestByGET(url);
      Map<String, Object> result = parseResponseToMap(resp);
      
      KuaipanFile file = new KuaipanFile(result);
      
      return file;
   }
   
   public static KuaipanHTTPResponse thumbnail(String path, OutputStream os,
         ProgressListener lr) throws KuaipanAuthExpiredException,
         KuaipanServerException, KuaipanIOException
   {
      return thumbnail(128, 128, path, os, lr);
   }
   
   public static KuaipanHTTPResponse thumbnail(int width, int height,
         String path, OutputStream os, ProgressListener lr)
         throws KuaipanAuthExpiredException, KuaipanServerException,
         KuaipanIOException
   {
      TreeMap<String, String> params = new TreeMap<String, String>();
      params.put("width", Integer.toString(width));
      params.put("height", Integer.toString(height));
      return do_download(path, KuaipanHTTPUtility.CONV_HOST,
	  "/1/fileops/thumbnail", os, lr, params);
   }
   
   public static KuaipanHTTPResponse documentView(ConvType type, ConvView view,
         String path, OutputStream os, ProgressListener lr)
         throws KuaipanAuthExpiredException, KuaipanServerException,
         KuaipanIOException
   {
      TreeMap<String, String> params = new TreeMap<String, String>();
      params.put("type", type.toString());
      params.put("view", view.toString());
      return do_download(path, KuaipanHTTPUtility.CONV_HOST,
	  "/1/fileops/documentView", os, lr, params);
   }
   
   public static KuaipanHTTPResponse downloadFile(String path, OutputStream os,
         ProgressListener lr) throws KuaipanIOException,
         KuaipanAuthExpiredException, KuaipanServerException
   {
      return do_download(path, KuaipanHTTPUtility.CONTENT_HOST,
	  "/1/fileops/download_file", os, lr, null);
   }
   
   /**
    * 
    * @param path
    * @param host
    * @param location
    * @param os
    * @param lr
    * @param otherParams
    *           - the method will change the value of this parameter
    * @return
    * @throws KuaipanIOException
    * @throws KuaipanAuthExpiredException
    * @throws KuaipanServerException
    */
   private static KuaipanHTTPResponse do_download(String path, String host,
         String location, OutputStream os, ProgressListener lr,
         Map<String, String> otherParams) throws KuaipanIOException,
         KuaipanAuthExpiredException, KuaipanServerException
   {
      Map<String, String> params = otherParams;
      if (params == null) params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("path", path);
      
      KuaipanURL url = OauthUtility.buildGetURL(host, location, params,
	  session.consumer, session.getToken(), false);
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.doDownload(url, os, lr);
      parseResponseToMap(resp);
      return resp;
   }
   
   public static boolean uploadFile(String path, InputStream is, long size,
         boolean overwrite, ProgressListener lr) throws KuaipanIOException,
         KuaipanServerException, KuaipanAuthExpiredException
   {
      String host = KuaipanHTTPUtility.UploadHostFactory.getUploadHost();
      Map<String, String> params = new TreeMap<String, String>();
      params.put("root", session.root);
      params.put("path", path);
      params.put("overwrite", Boolean.toString(overwrite));
      
      KuaipanURL url = OauthUtility.buildPostURL(host,
	  "/1/fileops/upload_file", params, session.consumer,
	  session.getToken(), false);
      KuaipanHTTPResponse resp = KuaipanHTTPUtility.doUpload(url, is, size, lr);
      
      Map<String, Object> result = parseResponseToMap(resp);
      if (resp.code == KuaipanHTTPResponse.KUAIPAN_OK)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * parse the response content and throw the right exception.
    * 
    * @param resp
    * @param url
    * @return
    * @throws KuaipanAuthExpiredException
    * @throws KuaipanServerException
    */
   private static Map<String, Object> parseResponseToMap(
         KuaipanHTTPResponse resp) throws KuaipanAuthExpiredException,
         KuaipanServerException
   {
      Map<String, Object> result = JSONUtility.parse(resp.content);
      if (resp.code == KuaipanHTTPResponse.KUAIPAN_OK) return result;
      
      if (resp.code == KuaipanHTTPResponse.KUAIPAN_AUTHORIZATION_ERROR)
      {
         if (result != null)
         {
	  String msg = (String) result.get("msg");
	  if (msg != null
	        && msg.equals(KuaipanHTTPResponse.MSG_AUTHORIZATION_EXPIRED)) throw new KuaipanAuthExpiredException();
         }
      }
      throw new KuaipanServerException(resp);
   }
   
   public static enum HttpMethod
   {
      GET, POST;
      public String toString()
      {
         return this.name();
      }
   }
   
   public static enum ConvType
   {
      PDF, DOC, WPS, CSV, PRN, XLS, ET, PPT, DPS, TXT, RTF;
      public String toString()
      {
         return this.name().toLowerCase();
      }
   }
   
   public static enum ConvView
   {
      NORMAL, ANDROID, IPAD, IPHONE;
      public String toString()
      {
         // this is a hack!
         if (this == IPAD) return "iPad";
         return this.name().toLowerCase();
      }
   }
   
}
