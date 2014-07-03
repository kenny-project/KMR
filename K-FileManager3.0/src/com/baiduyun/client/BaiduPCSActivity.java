package com.baiduyun.client;

import java.util.ArrayList;
import java.util.List;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSErrorCode;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.tools.SaveData;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class BaiduPCSActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baidu_pcs_activity);
         
        mbUiThreadHandler = new Handler();

        quota = (Button)findViewById(R.id.btnquota);
        upload = (Button)findViewById(R.id.btnupload);
        delete = (Button)findViewById(R.id.btndelete);
        download = (Button)findViewById(R.id.btndownload);
        foldermaker = (Button)findViewById(R.id.btnmkdir);
        meta = (Button)findViewById(R.id.btnmeta);
        list = (Button)findViewById(R.id.btnlist);
        move = (Button)findViewById(R.id.btnmove);  
        search = (Button)findViewById(R.id.btnsearch);  
        thumbnail = (Button)findViewById(R.id.btnthumbnail);
        iv = (ImageView)findViewById(R.id.imageView1);
        logout = (Button)findViewById(R.id.btnlogout);
        diff = (Button)findViewById(R.id.btndiff);
        copy = (Button)findViewById(R.id.btncopy);
        cloudmatch = (Button)findViewById(R.id.btncloudmatch);
        cloudmatchupload = (Button)findViewById(R.id.btncloudmatchupload);
        rename = (Button)findViewById(R.id.btnrename);
        clouddownload = (Button)findViewById(R.id.btnclouddownload);
        clouddownloadlist = (Button)findViewById(R.id.btnclouddownloadlist);
        queryclouddownloadtask = (Button)findViewById(R.id.btnclouddowntask);
        cancelclouddownloadtask = (Button)findViewById(R.id.btnclouddowncancel);
        

        mbOauth =SaveData.Read(BaiduPCSActivity.this, BaiduOauth, "");
        if(mbOauth.length()>0)
        {
        	test_list();
        }
        else
        {
        	test_login();
        }
        // click on quota button
        quota.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_getQuota();
            } 
        });
        
        upload.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_upload();
            } 
        });
        
        delete.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_delete();
            } 
        });
        
        download.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_download();
            } 
        });
        
        foldermaker.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_mkdir();
            } 
        });
        
        meta.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_meta();
            } 
        });
        
        list.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_list();
            } 
        });
        
        move.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_move();
            } 
        });
        
        search.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_search();
            } 
        });

        
        thumbnail.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_thumbnail();
            } 
        });
        
        logout.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_logout();
            } 
        });
        
        diff.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_diff();
            } 
        });
        
        copy.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_copy();
            } 
        });
        
        cloudmatch.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_cloudmatch();
            } 
        });
        
        cloudmatchupload.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_cloudmatchupload();
            } 
        });
        rename.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_rename();
            } 
        });
        clouddownload.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_clouddownload();
            }
        });
        
        clouddownloadlist.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_clouddownloadlist();
            }
        });
        queryclouddownloadtask.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_queryclouddownloadtask();
            }
        });
        cancelclouddownloadtask.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	test_cancelclouddownloadtask();
            }
        });
    }
    
    
    //
    // get quota
    //
    private void test_getQuota(){

    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		final BaiduPCSActionInfo.PCSQuotaResponse info = api.quota();

		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
				    		if(null != info){
				    			if(0 == info.status.errorCode){
				    				Toast.makeText(getApplicationContext(), "Quota :" + info.total + "  used: " + info.used, Toast.LENGTH_SHORT).show();
				    			}
				    			else{
				    				Toast.makeText(getApplicationContext(), "Quota failed: " + info.status.errorCode + "  " + info.status.message, Toast.LENGTH_SHORT).show();
				    			}
				    		}
		    			}
		    		});
				}
			});
			 
    		workThread.start();
    	}
    }
    private static final String BaiduOauth="BaiduOauth";
    //
    // login
    //
    private void test_login(){

		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(this, mbApiKey, new String[]{"basic", "netdisk"}, new BaiduOAuth.OAuthListener() {
			@Override
			public void onException(String msg) {
				Toast.makeText(getApplicationContext(), "Login failed " + msg, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onComplete(BaiduOAuthResponse response) {
				if(null != response){
					mbOauth = response.getAccessToken();
					SaveData.Write(BaiduPCSActivity.this, BaiduOauth, mbOauth);
					Toast.makeText(getApplicationContext(), "Token: " + mbOauth + "    User name:" + response.getUserName(), Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onCancel() {
				Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    //
    // get quota
    //
    private void test_upload(){

    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
					
					String tmpFile = "/mnt/sdcard/zzzz.jpg";
				//	String tmpFile = "/mnt/sdcard/DCIM/File/1.txt";
					
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		
		    		final BaiduPCSActionInfo.PCSFileInfoResponse response = api.uploadFile(tmpFile, mbRootPath + "/zzz.jpg", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
							
							
							final long bs = bytes;
							final long tl = total;

				    		mbUiThreadHandler.post(new Runnable(){
				    			public void run(){
				    				Toast.makeText(getApplicationContext(), "total: " + tl + "    sent:" + bs, Toast.LENGTH_SHORT).show();
				    			}
				    		});						
						}
						
						@Override
						public long progressInterval(){
							return 1000;
						}
		    		});
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), response.status.errorCode + "  " + response.status.message + "  " + response.commonFileInfo.blockList, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // test delete
    //
    private void test_delete(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		
		    		List<String> files = new ArrayList<String>();
		    		files.add(mbRootPath + "/" + "198.jpg");
		    		files.add(mbRootPath + "/" + "2.jpg");
		    		files.add(mbRootPath + "/" + "3.jpg");
		    		
		    		final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Delete files:  " + ret.errorCode + "  " + ret.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // download file
    //
    private void test_download(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String source = mbRootPath + "/189.jpg";
		    		String target = "/mnt/sdcard/DCIM/100MEDIA/yytest0801.mp4";
		    		final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.downloadFileFromStream(source, target, new BaiduPCSStatusListener(){
		    			//yangyangdd
						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
							final long bs = bytes;
							final long tl = total;

				    		mbUiThreadHandler.post(new Runnable(){
				    			public void run(){
				    				Toast.makeText(getApplicationContext(), "total: " + tl + "    downloaded:" + bs, Toast.LENGTH_SHORT).show();
				    			}
				    		});		
						}
						
						@Override
						public long progressInterval(){
							return 500;
						}
		    			
		    		});
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Download files:  " + ret.errorCode + "   " + ret.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    

    
    //
    // mkdir 
    //
    private void test_mkdir(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String path = mbRootPath + "/" + "JakeDu";

		    		final BaiduPCSActionInfo.PCSFileInfoResponse ret = api.makeDir(path);
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Mkdir:  " + ret.status.errorCode + "   " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // meta file
    //
    private void test_meta(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String path = mbRootPath + "/08_HTTP.mp4";

		    		final BaiduPCSActionInfo.PCSMetaResponse ret = api.meta(path);
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){

		    				String extra = null;

		    				switch(ret.type){
		    				case Media_Audio:
		    					BaiduPCSActionInfo.PCSAudioMetaResponse audioInfo = (BaiduPCSActionInfo.PCSAudioMetaResponse)ret;
		    					if(null != audioInfo){
		    						extra = audioInfo.trackTitle;
		    					}
		    					break;

		    				case Media_Video:
		    					BaiduPCSActionInfo.PCSVideoMetaResponse videoInfo = (BaiduPCSActionInfo.PCSVideoMetaResponse)ret;
		    					if(null != videoInfo){
		    						extra = videoInfo.resolution;
		    					}
		    					break;

		    				case Media_Image:
		    					BaiduPCSActionInfo.PCSImageMetaResponse imageInfo = (BaiduPCSActionInfo.PCSImageMetaResponse)ret;
		    					if(null != imageInfo){
		    						extra = imageInfo.latitude + "  " + imageInfo.longtitude;
		    					}
		    					break;
		    				}

		    				Toast.makeText(getApplicationContext(), "Meta:  " + ret.status.errorCode + "   " + ret.status.message + "  " + extra, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // list
    //
    private void test_list(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String path = mbRootPath;
//		    		path="/";
		    		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    		//final BaiduPCSActionInfo.PCSListInfoResponse ret = api.imageStream();
		    
		    		List<PCSCommonFileInfo> List=ret.list;
		    		SysEng.getInstance().addHandlerEvent(new AbsEvent()
					{
						
						@Override
						public void ok()
						{
							// TODO Auto-generated method stub
							
						}
					});
//		    		mbUiThreadHandler.post(new Runnable(){
//		    			public void run(){
//		    				Toast.makeText(getApplicationContext(), "List:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
//		    			}
//		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // move
    //
    private void test_move(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		//String from = mbRootPath + "/1.txt";
		    		//String to = mbRootPath + "/Jake/2.txt"; // test1122335665.jpg
		    		
		    		List<BaiduPCSActionInfo.PCSFileFromToInfo> info = new ArrayList<BaiduPCSActionInfo.PCSFileFromToInfo>();
		    		BaiduPCSActionInfo.PCSFileFromToInfo data1 = new BaiduPCSActionInfo.PCSFileFromToInfo();
		    		data1.from = mbRootPath + "/JakeDu/08_HTTP.jpg";
		    		data1.to = mbRootPath + "/08_HTTP.mp4";
		    			    		
		    	
		    		info.add(data1);
		    	//	info.add(data2);

		    		final BaiduPCSActionInfo.PCSFileFromToResponse ret = api.move(info);
		    		
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Move:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // move
    //
    private void test_copy(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		//String from = mbRootPath + "/1.txt";
		    		//String to = mbRootPath + "/Jake/2.txt"; // test1122335665.jpg
		    		
		    		List<BaiduPCSActionInfo.PCSFileFromToInfo> info = new ArrayList<BaiduPCSActionInfo.PCSFileFromToInfo>();
		    		BaiduPCSActionInfo.PCSFileFromToInfo data1 = new BaiduPCSActionInfo.PCSFileFromToInfo();
		    		data1.from = mbRootPath + "/08_HTTP.mp4";
		    		data1.to = mbRootPath + "/JakeDu/08_HTTP.jpg";
		    		
		    		BaiduPCSActionInfo.PCSFileFromToInfo data2 = new BaiduPCSActionInfo.PCSFileFromToInfo();
		    		data2.from = mbRootPath + "/1986.jpg";
		    		data2.to = mbRootPath + "/JakeDu/6.jpg";	    		
		    	
		    		info.add(data1);
		    	//	info.add(data2);

		    		final BaiduPCSActionInfo.PCSFileFromToResponse ret = api.copy(info);
		    		
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Copy:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // search
    //
    private void test_search(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);

		    		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.search(mbRootPath, "jpg", true);

		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Search:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    // diff
    //
    private void test_diff(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);

		    		final BaiduPCSActionInfo.PCSDiffResponse ret = api.diff("lPoXQ82tTNeQi17NfzbqlefWLhWlMZzDqioifhVxuA1ZMIOK3Da4gAEep+KIyVue3Iuy+tEQn9CpBPg8C4p8Imt7ypPiaLhF8ShPiNctAUBrtXcKhX/O80LUnmlhtwWosB3bJtl9i99y5QFE6zNAwEae5PL1JxAkxi3vQoNr2XYLnGv2r/u08o3SW0axqqj6qRo3f9rFxX36CkQhWZUGG7XOelgBPlus0d7CGObNs9ltH9OustCKLiTQXG2G96Ap");
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Diff:  " + ret.status.errorCode + "   " + ret.status.message + "  " + ret.entries.size(), Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //
    //cloud match
    //
    public void test_cloudmatch(){
    	if(null != mbOauth){
    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		final BaiduPCSActionInfo.PCSFileInfoResponse ret = api.cloudMatch("/mnt/sdcard/effff中国好声音.jar", mbRootPath + "/fhjk/effff中国好声音.jar");
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "CloudMatch:  " + ret.status.errorCode + "\n" + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    // move
    //
    private void test_rename(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
//		    		String from = mbRootPath + "/10mpic.jpg";
//		    		String to = "big10mpic.jpg"; // test1122335665.jpg
//		    		String from = null;
//		    		String to = mbRootPath + "/10mpic.jpg"; // test1122335665.jpg
//		    		final BaiduPCSActionInfo.PCSFileFromToResponse ret = api.rename(from, to);
		    		List<BaiduPCSActionInfo.PCSFileFromToInfo> info = new ArrayList<BaiduPCSActionInfo.PCSFileFromToInfo>();
		    		BaiduPCSActionInfo.PCSFileFromToInfo data1 = new BaiduPCSActionInfo.PCSFileFromToInfo();
		    		BaiduPCSActionInfo.PCSFileFromToInfo data2 = new BaiduPCSActionInfo.PCSFileFromToInfo();
		    		data1.from = mbRootPath + "/fhjk/effff中国好声音.jar";
		    		data1.to = "effff中国好声音1111.jar";
		    	//	data1.to = null;
		    		
		    	//	data2.from = mbRootPath + "/fhj/1111.jpg";
		        //	data2.to = "11newname.jpg";
		    		
		    			    		
		    	
		    		info.add(data1);
		    	//	info.add(data2);

		    		final BaiduPCSActionInfo.PCSFileFromToResponse ret = api.rename(info);
		    		
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Rename:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    //
    //cloud match and upload if cloud match false
    //
    public void test_cloudmatchupload(){
    	if(null != mbOauth){
    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		final BaiduPCSActionInfo.PCSFileInfoResponse ret = api.cloudMatchAndUploadFile("/mnt/sdcard/yy.rar", mbRootPath + "/yangyangdd/yy1.rar",new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
							final long bs = bytes;
							final long tl = total;

				    		mbUiThreadHandler.post(new Runnable(){
				    			public void run(){
				    				Toast.makeText(getApplicationContext(), "total: " + tl + "    upload:" + bs, Toast.LENGTH_SHORT).show();
				    			}
				    		});		
						}
						
						@Override
						public long progressInterval(){
							return 1000;
						}
		    			
		    		});
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "CloudMatchandUpload:  " + ret.status.errorCode + "\n" + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    //thumbnail
    //
    public void  test_thumbnail(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String picpath = mbRootPath + "/" + "198.jpg";
		    		final BaiduPCSActionInfo.PCSThumbnailResponse ret = api.thumbnail(picpath, 100, 90, 90);
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				if(BaiduPCSErrorCode.No_Error == ret.status.errorCode){
		    					Toast.makeText(getApplicationContext(), "Thumbnail   success" + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();

			    				if(null != ret && null != ret.bitmap){
			    					iv.setImageBitmap(ret.bitmap);
			    				}
		    				}else{
		    					Toast.makeText(getApplicationContext(), "Thumbnail   failed: " + ret.status.errorCode + "   " + ret.status.message, Toast.LENGTH_SHORT).show();
		    				}
		    			}
		    		});	
				}
    		});
    		
    		workThread.start();
    	}
    	
    }
    
    //
    //cloud download
    //
    private void test_clouddownload(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String destPath = mbRootPath + "/Skycn_1.2.1.exe";
		    		String sourceUrl = "http://tk.wangyuehd.com/soft/Skycn_1.2.1.exe";//http://59.108.246.24:82/down/kis12.0.0.374zh-Hans_cn.zip";
		    		//String sourceUrl = "http://59.108.246.24:82/down/QQsetup.zip";
		    		final BaiduPCSActionInfo.PCSCloudDownloadResponse ret = api.cloudDownload(sourceUrl, destPath);
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "cloudDownload:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    //cloud download list
    //
    private void test_clouddownloadlist(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		final BaiduPCSActionInfo.PCSCloudDownloadTaskListResponse ret = api.cloudDownloadTaskList();
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "cloudDownloadlist:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    // query task status
    //
    private void test_queryclouddownloadtask(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String [] queryTaskId = {"2404"};
		    		final BaiduPCSActionInfo.PCSCloudDownloadQueryTaskProgressResponse ret = api.queryCloudDownloadTaskProgress(queryTaskId);
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "querycloudDownloadtask:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    //cancel a task
    //
    private void test_cancelclouddownloadtask(){
    	if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String queryTaskId = "2312";
		    		final BaiduPCSActionInfo.PCSCloudDownloadResponse ret = api.cancelCloudDownloadTask(queryTaskId);
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "cancelcloudDownloadtask:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
				}
    		});
    		workThread.start();
    	}
    }
    
    //
    //logout
    //
    public void  test_logout(){
    	if(null != mbOauth){
    		/**
    		 * you can call this method to logout in Android 4.0.3
    		 */
//    		BaiduOAuth oauth = new BaiduOAuth();
//    	    oauth.logout(mbOauth, new BaiduOAuth.ILogoutListener(){
//
//				@Override
//				public void onResult(boolean success) {
//
//					Toast.makeText(getApplicationContext(), "Logout: " + success, Toast.LENGTH_SHORT).show();
//				}
//    			
//    		});
    	
    	    /**
    	     * you can call this method to logout in Android 2.X
    	     */
    		Thread workThread = new Thread(new Runnable(){
				@Override
				public void run() {
					
		    		BaiduOAuth oauth = new BaiduOAuth();
		    		final boolean ret = oauth.logout(mbOauth);
		    		mbUiThreadHandler.post(new Runnable(){
		    			@Override
						public void run(){
	    					Toast.makeText(getApplicationContext(), "Logout " + ret, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
    		});
    		
    		workThread.start();
    	}
    	
    }
    
    /**
     * Called by the system when the device configuration changes while your activity is running. 
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //don't reload the current page when the orientation is changed
        super.onConfigurationChanged(newConfig);
    }
    
    private String mbOauth = null;
    
    // the api key
    /*
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
    private final static String mbApiKey = "QbY3Gk2MiH4E9IkRPt1585q5"; //your app_key";
    
    // the default root folder
    /*
     * mbRootPath should be your app_path, please instead of "/apps/pcstest_oauth"
     */
    private final static String mbRootPath =  "/apps/K-FileManager";

    private Button quota = null;
    private Button upload = null;
    private Button delete = null;
    private Button download = null;
    private Button foldermaker = null;
    private Button meta = null;
    private Button list = null;
    private Button move = null; 
    private Button search = null;
    private Button thumbnail = null;
    private ImageView iv = null;
    private Button logout = null;
    private Button diff = null;
    private Button copy = null;
    private Button cloudmatch = null;
    private Button cloudmatchupload = null;
    private Button rename = null;
    private Button clouddownload = null;
    private Button clouddownloadlist = null;
    private Button queryclouddownloadtask = null;
    private Button cancelclouddownloadtask = null;
    
    // the handler
    private Handler mbUiThreadHandler = null;

}