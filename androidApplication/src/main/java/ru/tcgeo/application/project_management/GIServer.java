//package ru.tcgeo.application.project_management;
//
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.BufferedHttpEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//
//import ru.tcgeo.application.gilib.parser.GIProjectProperties;
//import ru.tcgeo.application.gilib.parser.GIPropertiesFile;
//import ru.tcgeo.application.local_project_management.GILocalProjectInfo;
//import ru.tcgeo.application.local_project_management.GIProjectInfo;
//
//public class GIServer
//{
//	final String LOG_TAG = "LOG_TAG";
//	/**
//	 * асинхронный класс
//	 * получения сессионного ключа от сервера. при неудаче - список проектов на сервере и все операции с ним недоступны
//	 * @author artem
//	 *
//	 */
//	public class LoginTask extends AsyncTask <Void, Void, String>
//	{
//	    @Override
//	    protected void onPreExecute() {
//	      super.onPreExecute();
//	      Log.d(LOG_TAG, "LoginTask: onPreExecute");
//	    }
//
//		@Override
//		protected String doInBackground(Void... arg0)
//		{
//			//String m_login = getResources().getString(R.string.hello_world);//"login";
//			//String m_password = "pass";
//			//String m_r = "Users/IosAuth";
//	        String result = "";
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/IosAuth&login=user&pass=user");
//			try
//			{
//				/*List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//				post_params.add(new BasicNameValuePair("r", m_r));
//				post_params.add(new BasicNameValuePair("login", m_login));
//				post_params.add(new BasicNameValuePair("pass", m_password));
//				httppost.setEntity(new UrlEncodedFormEntity(post_params));*/
//				HttpResponse response = httpclient.execute(httppost);
//		        if (response.getStatusLine().getStatusCode() == 200)
//		        {
//		            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
//		            InputStream instream = bufHttpEntity.getContent();
//					String line;
//					BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//					while ( (line = serverResponse.readLine() ) != null )
//					{
//						result = result + line;
//					}
//		        }
//		    }
//			catch (ClientProtocolException e) {}
//			catch (IOException e) {}
//			return result;
//		}
//
//		@Override
//	    protected void onPostExecute(String result) {
//	      super.onPostExecute(result);
//	      m_session_key = result;
//	      new LocalProjectsListTaskLite().execute();
//	      new ProjectsListTaskLite().execute();
//	      Log.d(LOG_TAG, "LoginTask: onPostExecute");
//	    }
//
//	}
//	/**
//	 * асинхронный класс устаревшее
//	 * получения списка проектов  от сервера.
//	 * @author artem
//	 *
//	 */
//	public class ProjectsListTask extends AsyncTask <ArrayList<GIProjectInfo>, Void, ArrayList<GIProjectInfo>>
//	{
//		@Override
//		protected ArrayList<GIProjectInfo> doInBackground(ArrayList<GIProjectInfo>... arg0)
//		{
//			arg0[0] = getRemoteProjectsList(arg0[0]);
//			return arg0[0];
//		}
//	}
//	/**
//	 * асинхронный класс
//	 * получения списка проектов  от сервера.
//	 * @author artem
//	 *
//	 */
//	public class ProjectsListTaskLite extends AsyncTask <Void, Void, Void>
//	{
//		@Override
//		protected Void doInBackground(Void... arg0)
//		{
//			getRemoteProjectsList(m_remotes);
//			return null;
//		}
//	    @Override
//	    protected void onPostExecute(Void result)
//	    {
//	    	m_presenter.SomethingChanged();
//	    }
//	}
//	/**
//	 * асинхронный класс
//	 * получения списка локальных проектов.
//	 * @author artem
//	 *
//	 */
//	public class LocalProjectsListTaskLite extends AsyncTask <Void, Void, Void>
//	{
//		@Override
//		protected Void doInBackground(Void... arg0)
//		{
//			AddLocalProjects();
//			return null;
//		}
//	    @Override
//	    protected void onPostExecute(Void result)
//	    {
//	    	m_presenter.SomethingChanged();
//	    	for(GIProjectInfo proj : m_locals)
//	    	{
//	    		new CheckingLocalProjectTask().execute(proj);
//
//	    	}
//	    }
//	}
//	/**
//	 * асинхронный класс
//	 * получения информации о проекте( включая список package) от сервера.
//	 * @author artem
//	 *
//	 */
//	public class ProjectInfoTask extends AsyncTask <GIProjectInfo, Void, Void>
//	{
//		@Override
//		protected Void doInBackground(GIProjectInfo... arg0)
//		{
//			for(GIProjectInfo project : arg0)
//			{
//				getRemoteProjectInfo(project);
//				project.m_inState = GIPresenter.ProjectInState.READY;
//			}
//			return null;
//		}
//	    @Override
//	    protected void onPostExecute(Void result) {
//	      m_presenter.SomethingHappend();
//	    }
//	}
//
//	/**
//	 * асинхронный класс
//	 * получения  package от сервера.
//	 * @author artem
//	 *
//	 */
//	public class GetPackageTask extends AsyncTask <GIProjectInfo, Integer, Boolean>
//	{
//		int task_progress;
//		GIProjectInfo current;
//		@Override
//	    protected void onPreExecute()
//		{
//			super.onPreExecute();
//			task_progress = 0;
//	    }
//
//		@Override
//		protected Boolean doInBackground(GIProjectInfo... arg0)
//		{
//			current = (GIProjectInfo)arg0[0];
//			long all_total_size_precount = 0;
//			long all_total_size = 0;
//			long all_total_downloaded = 0;
//			for(GIPropertiesPackage pack: arg0[0].m_project_properties.m_Entries)
//			{
//				if(IsLocalPackageNeeded(pack) <= 0)
//				{
//					all_total_size_precount = all_total_size_precount + pack.m_size;
//				}
//			}
//			for(int i = 0; i < arg0[0].m_project_properties.m_Entries.size(); i++)
//			{
//				GIPropertiesPackage pack = arg0[0].m_project_properties.m_Entries.get(i);
//				if(IsLocalPackageNeeded(pack) <= 0)
//				{
//					HttpClient httpclient = new DefaultHttpClient();
//					HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getPackage&key=" + m_session_key + "&id=" + pack.m_id);
//					try
//					{
//						HttpResponse response = httpclient.execute(httpget);
//				        if (response.getStatusLine().getStatusCode() == 200)
//				        {
//				            HttpEntity entity = response.getEntity();
//				            Header[] clHeaders = response.getHeaders("Content-Length");
//				            Header header = clHeaders[0];
//				            long totalSize = Integer.parseInt(header.getValue());
//				            all_total_size = all_total_size + totalSize;
//				            if(i == arg0[0].m_project_properties.m_Entries.size() - 1)
//				            {
//				            	all_total_size_precount = all_total_size;
//				            }
//				        	InputStream instream = entity.getContent();
//							File zipFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + pack.m_name/* + ".zip"*/);
//							FileOutputStream file = new FileOutputStream(zipFile);
//							byte[] buffer = new byte[1024];
//							int length = 0;
//							int prev_task_progress = 0;
//							while ( (length = instream.read(buffer)) > 0)
//							{
//								file.write(buffer, 0, length);
//								all_total_downloaded += length;
//								task_progress = 1 + (int) (((float)all_total_downloaded/all_total_size_precount)*100);
//								if(task_progress - prev_task_progress > 5)
//								{
//									prev_task_progress = task_progress;
//									publishProgress(task_progress);
//								}
//							}
//							file.close();
//							if(zipFile.exists())
//							{
//								unzip(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + pack.m_name, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//								zipFile.delete();
//							}
//				        }
//				    }
//					catch (ClientProtocolException e)
//				    {
//						e.printStackTrace();
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//			return true;
//		}
//
//	    @Override
//	    protected void onPostExecute(Boolean result) {
//		    new LocalProjectsListTaskLite().execute();
//		    new ProjectsListTaskLite().execute();
//	    	current.m_inState = GIPresenter.ProjectInState.READY;
//	    	m_presenter.SomethingChanged();
//	    }
//	    @Override
//	    protected void onProgressUpdate(Integer... progress) {
//	      current.m_inState = GIPresenter.ProjectInState.DOWNLOADING;
//	      current.m_progress_percents = progress[0];
//	      m_presenter.SomethingChanged();
//	    }
//	}
//	/**
//	 * асинхронный класс
//	 * получения  файла проекта от сервера.
//	 * @author artem
//	 *
//	 */
//	public class GetProjectTask extends AsyncTask <GIProjectInfo, Integer, Boolean>
//	{
//		GIProjectInfo current;
//		int task_progress;
//		@Override
//		protected Boolean doInBackground(GIProjectInfo... arg0)
//		{
//			current = arg0[0];
//			current.m_inState = GIPresenter.ProjectInState.DOWNLOADING;
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProjectFile&key=" + m_session_key + "&id=" + current.m_id);
//			boolean result = false;
//			try
//			{
//				HttpResponse response = httpclient.execute(httpget);
//		        if (response.getStatusLine().getStatusCode() == 200)
//		        {
//		            HttpEntity entity = response.getEntity();
//		            Header[] clHeaders = response.getHeaders("Content-Length");
//		            Header header = clHeaders[0];
//		            int totalSize = Integer.parseInt(header.getValue());
//		            int downloadedSize = 0;
//		        	InputStream instream = entity.getContent();
//					FileOutputStream file = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current.m_file));
//					byte[] buffer = new byte[1024];
//					int length = 0;
//					int prev_task_progress = 0;
//					while ( (length = instream.read(buffer)) > 0)
//					{
//						file.write(buffer, 0, length);
//						downloadedSize += length;
//						task_progress = 1 + (int) Math.round(((float)downloadedSize/totalSize)*100);
//						if(task_progress - prev_task_progress > 5)
//						{
//							prev_task_progress = task_progress;
//							publishProgress(task_progress);
//						}
//					}
//					file.close();
//					current.m_project_properties = new GIProjectProperties(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current.m_file);
//
//					result = true;
//		        }
//		        return result;
//		    }
//
//			catch (ClientProtocolException e)
//		    {
//				return false;
//			}
//			catch (IOException e)
//			{
//				return false;
//			}
//			/**/
//		}
//	    @Override
//	    protected void onPostExecute(Boolean result) {
//	    	m_presenter.SomethingChanged();
//	    }
//
//	    @Override
//	    protected void onProgressUpdate(Integer... progress) {
//		      current.m_inState = GIPresenter.ProjectInState.DOWNLOADING;
//		      current.m_progress_percents = progress[0];
//		      m_presenter.SomethingChanged();
//	    }
//	}
//	/**
//	 * асинхронный класс
//	 * чтения проекта на сервере без сохранения в локальной памяти.
//	 * @author artem
//	 *
//	 */
//	public class readProjectTask extends AsyncTask <GIProjectInfo, Integer, Void>
//	{
//		GIProjectInfo current;
//		int task_progress;
//		@Override
//		protected Void doInBackground(GIProjectInfo... arg0)
//		{
//			current = arg0[0];
//
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProjectFile&key=" + m_session_key + "&id=" + current.m_id);
//			try
//			{
//				HttpResponse response = httpclient.execute(httpget);
//		        if (response.getStatusLine().getStatusCode() == 200)
//		        {
//		            HttpEntity entity = response.getEntity();
//		        	InputStream instream = entity.getContent();
//					current.m_project_properties = new GIProjectProperties(instream);
//		        }
//		        return null;
//		    }
//			catch (ClientProtocolException e)
//		    {
//				return null;
//			}
//			catch (IOException e)
//			{
//				return null;
//			}
//			/**/
//		}
//	    @Override
//	    protected void onPostExecute(Void result) {
//	    	m_presenter.SomethingHappend();
//	    }
//
//	    @Override
//	    protected void onProgressUpdate(Integer... values) {
//	    }
//	}
//	/**
//	 * асинхронный класс
//	 * проверки чексуммы локального пака на сервере.
//	 * @author artem
//	 *
//	 */
//	public class CheckPackageTask extends AsyncTask <GIPropertiesPackage, Integer, Boolean>
//	{
//		@Override
//	    protected void onPreExecute() {
//	      super.onPreExecute();
//	      Log.d(LOG_TAG, "CheckPackageTask: onPreExecute");
//	    }
//
//		@Override
//		protected Boolean doInBackground(GIPropertiesPackage... arg0)
//		{
//			for(GIPropertiesPackage pack : arg0)
//			{
//				try
//				{
//					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + pack.m_name);
//					if(file.exists())
//					{
//						FileInputStream stream = new FileInputStream(file);
//						String md5checksum = md5(stream);
//				        String result = "";
//						HttpClient httpclient = new DefaultHttpClient();
//						HttpPost httppost = new HttpPost("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/checkPackage&key=" + m_session_key + "&id=" + pack.m_id + "&md5=" + md5checksum);
//						try
//						{
//							HttpResponse response = httpclient.execute(httppost);
//					        if (response.getStatusLine().getStatusCode() == 200)
//					        {
//					            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
//					            InputStream instream = bufHttpEntity.getContent();
//								String line;
//								BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//								while ( (line = serverResponse.readLine() ) != null )
//								{
//									result = result + line;
//								}
//								if(result.equalsIgnoreCase("latest"))
//								{
//
//									return true;
//								}
//					        }
//					    }
//						catch (ClientProtocolException e)
//					    {
//							e.printStackTrace();
//					    }
//						catch (IOException e)
//						{
//							e.printStackTrace();
//						}
//						//return result;
//					}
//				}
//				catch (Exception e) { return false;}
//			}
//			return false;
//		}
//	}
//	/**
//	* асинхронный класс
//	 * наличия локального проекта среди проектов на сервере
//	 * @author artem
//	 *
//	 */
//	public class CheckingLocalProjectTask extends AsyncTask <GIProjectInfo, Void, GIProjectInfo>
//	{
//		@Override
//		protected GIProjectInfo doInBackground(GIProjectInfo... arg0)
//		{
//			Log.d(LOG_TAG, "CheckingLocalProjectTask: doInBackground");
//			ArrayList<GIProjectInfo> remotes = new ArrayList<GIProjectInfo>();
//			StringBuilder builder = new StringBuilder();
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProjects&key=" + m_session_key);
//			try
//			{
//				HttpResponse response = httpclient.execute(httpget);
//		        if (response.getStatusLine().getStatusCode() == 200)
//		        {
//		            HttpEntity entity = response.getEntity();
//		        	InputStream instream = entity.getContent();
//					String line;
//					BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//					while ( (line = serverResponse.readLine() ) != null )
//					{
//						builder.append(line);
//					}
//		        }
//		    }
//			catch (ClientProtocolException e)
//		    {
//				e.printStackTrace();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//
//			String result = builder.toString();
//			try
//			{
//				JSONArray jsonArray = new JSONArray(result);
//				for(int i = 0; i < jsonArray.length(); i++)
//				{
//					JSONObject obj = jsonArray.getJSONObject(i);
//
//					int id = obj.getInt("id");
//					String name = obj.getString("name");
//					String file = obj.getString("file");
//					if(file!= null)
//					{
//						GIProjectInfo item = new GIRemoteProjectInfo(id, name, file, GIProjectInfo.GIProjectStatus.UNKNOWN);
//						//item.m_inState = ProjectInState.READY;
//						remotes.add(item);
//					}
//				}
//
//			}catch (Exception e) {}
//
//			arg0[0].m_status = GIProjectInfo.GIProjectStatus.FAIL;
//			for(GIProjectInfo item : remotes)
//			{
//				if(item.m_name.equalsIgnoreCase(arg0[0].m_name))
//				{
//					if(item.m_file.equalsIgnoreCase(arg0[0].m_file))
//					{
//						arg0[0].m_status = GIProjectInfo.GIProjectStatus.UPTODATE;
//
//					}
//				}
//			}
//			arg0[0].m_inState = GIPresenter.ProjectInState.READY;
//			return arg0[0];
//
//		}
//	    @Override
//	    protected void onPostExecute(GIProjectInfo result) {
//
//	    	m_presenter.SomethingHappend();
//	    }
//
//	}
//
//	private String m_session_key;
//	public ArrayList<GIProjectInfo> m_remotes;
//	public ArrayList<GIProjectInfo> m_locals;
//	public boolean m_busy;
//	GIPresenter m_presenter;
//
//	private static GIServer m_instance;
//	public static GIServer Instance()
//	{
//		if(m_instance == null)
//		{
//			m_instance = new GIServer();
//		}
//		return m_instance;
//	}
//
//	private GIServer()
//	{
//		m_presenter = new GIPresenter(this);
//		m_remotes = new ArrayList<GIProjectInfo>();
//		m_locals = new ArrayList<GIProjectInfo>();
//		Initialize();
//	}
//	public void Initialize()
//	{
//		if(m_session_key != null)
//		{
//		      new LocalProjectsListTaskLite().execute();
//		      new ProjectsListTaskLite().execute();
//		}
//		else
//		{
//			new LoginTask().execute();
//		}
//	}
//	public GIPresenter getPresenter()
//	{
//		return m_presenter;
//	}
//
//	private static char[] hexDigits = "0123456789abcdef".toCharArray();
//
//    /**
//     * подсчет контрольной суммы
//     * @param is входной поток
//     * @return
//     * @throws IOException
//     */
//	public String md5(InputStream is) throws IOException
//    {
//        String md5 = "";
//
//        try {
//            byte[] bytes = new byte[4096];
//            int read = 0;
//            MessageDigest digest = MessageDigest.getInstance("MD5");
//
//            while ((read = is.read(bytes)) != -1) {
//                digest.update(bytes, 0, read);
//            }
//
//            byte[] messageDigest = digest.digest();
//
//            StringBuilder sb = new StringBuilder(32);
//
//            for (byte b : messageDigest) {
//                sb.append(hexDigits[(b >> 4) & 0x0f]);
//                sb.append(hexDigits[b & 0x0f]);
//            }
//
//            md5 = sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return md5;
//    }
//
//   /**
//    * процедура распаковывания ZIP
//    * @param zipFile
//    * @param location
//    * @throws IOException
//    */
//	public void unzip(String zipFile, String location) throws IOException {
//        try {
//            File f = new File(location);
//            if(!f.isDirectory()) {
//                f.mkdirs();
//            }
//            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
//            try {
//                ZipEntry ze = null;
//                while ((ze = zin.getNextEntry()) != null)
//                {
//                    String path = location + ze.getName();
//
//                    if (ze.isDirectory()) {
//                        File unzipFile = new File(path);
//                        if(!unzipFile.isDirectory())
//                        {
//                            unzipFile.mkdirs();
//              		      Log.d(LOG_TAG, "Unzip: " + path);
//                        }
//                    }
//                    else {
//                    	byte[] buffer = new byte[2048];
//                    	int size;
//                        FileOutputStream fout = new FileOutputStream(path, false);
//                        BufferedOutputStream bos = new BufferedOutputStream(fout, buffer.length);
//                        try
//                        {
//                            while ((size = zin.read(buffer, 0, buffer.length)) != -1)
//                            {
//                                bos.write(buffer, 0, size);
//                            }
//                            Log.d(LOG_TAG, "Unzip: " + path);
//                            bos.flush();
//                            bos.close();
//
//                            fout.flush();
//                            fout.close();
//                            zin.closeEntry();
//                        }
//                        finally {
//                            fout.close();
//                        }
//                    }
//                }
//            }
//            finally {
//                zin.close();
//            }
//        }
//        catch (Exception e) {}
//    }
//
//	/**
//	 * получение списка проектов с сервера
//	 * @param list
//	 * @return
//	 */
//	private ArrayList<GIProjectInfo> getRemoteProjectsList(ArrayList<GIProjectInfo> list)
//	{
//
//		StringBuilder builder = new StringBuilder();
//		HttpClient httpclient = new DefaultHttpClient();
//		list.clear();
//
//		HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProjects&key=" + m_session_key);
//		try
//		{
//			/*List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//			post_params.add(new BasicNameValuePair("key=", m_session_key));
//			httpget.setEntity(new UrlEncodedFormEntity(post_params));*/
//
//			HttpResponse response = httpclient.execute(httpget);
//	        if (response.getStatusLine().getStatusCode() == 200)
//	        {
//	            //BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
//	            HttpEntity entity = response.getEntity();
//
//	        	InputStream instream = entity.getContent();
//				String line;
//				BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//				while ( (line = serverResponse.readLine() ) != null )
//				{
//					builder.append(line);
//				}
//	        }
//
//	    }
//		catch (ClientProtocolException e)
//	    {
//			e.printStackTrace();
//			}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
//		String result = builder.toString();
//		try
//		{
//			JSONArray jsonArray = new JSONArray(result);
//			for(int i = 0; i < jsonArray.length(); i++)
//			{
//				JSONObject obj = jsonArray.getJSONObject(i);
//
//				int id = obj.getInt("id");
//				String name = obj.getString("name");
//				String file = obj.getString("file");
//				if(file!= null)
//				{
//					GIRemoteProjectInfo proj = new GIRemoteProjectInfo(id, name, file, GIProjectInfo.GIProjectStatus.UNKNOWN);
//					proj.getStatus(this);
//					list.add(proj);
//				}
//			}
//
//		}catch (Exception e) {}
//		return list;
//	}
//	/**
//	 * получение информации о проекте с сервера
//	 * @param
//	 * @return
//	 */
//	private GIProjectInfo getRemoteProjectInfo(GIProjectInfo project)
//	{
//
//		StringBuilder builder = new StringBuilder();
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProject&key=" + m_session_key + "&id=" + project.m_id);
//		try
//		{
//			/*List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//			post_params.add(new BasicNameValuePair("key=", m_session_key));
//			httpget.setEntity(new UrlEncodedFormEntity(post_params));*/
//
//			HttpResponse response = httpclient.execute(httpget);
//	        if (response.getStatusLine().getStatusCode() == 200)
//	        {
//	            //BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
//	            HttpEntity entity = response.getEntity();
//
//	        	InputStream instream = entity.getContent();
//				String line;
//				BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//				while ( (line = serverResponse.readLine() ) != null )
//				{
//					builder.append(line);
//				}
//	        }
//
//	    }
//		catch (ClientProtocolException e)
//	    {
//			e.printStackTrace();
//			}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
//		String result = builder.toString();
//		try
//		{
//			JSONObject proj = new JSONObject(result);
//			int id = proj.getInt("id");
//			String name = proj.getString("name");
//			String file = proj.getString("file");
//			String pgk = proj.getString("pgk");
//			JSONArray jsonArray = new JSONArray(pgk);
//			if(!project.m_name.equalsIgnoreCase(name) || !project.m_file.equalsIgnoreCase(file) || project.m_id != id)
//			{
//				return null;
//			}
//			project.m_packets.clear();
//			for(int i = 0; i < jsonArray.length(); i++)
//			{
//				JSONObject pack = jsonArray.getJSONObject(i);
//
//				int pack_id = pack.getInt("id");
//				String pack_name = pack.getString("name");
//
//				GIPropertiesPackage packet = new GIPropertiesPackage(pack_id, pack_name);
//				project.m_packets.add(packet);
//			}
//
//		}catch (Exception e) {}
//
//		return project;
//	}
//
//	/**
//	 * список локальных проектов. синхронно
//	 */
//	public void AddLocalProjects ()
//	{
//		File dir = (Environment.getExternalStorageDirectory());
//		m_locals.clear();
//		for(File file : dir.listFiles())
//		{
//			if(file.isFile())
//			{
//				if(file.getName().endsWith(".pro"))
//				{
//					String file_name = file.getName();
//					if(file_name!= null)
//					{
//						GILocalProjectInfo proj = new GILocalProjectInfo(file_name);
//						proj.m_packets = proj.m_project_properties.m_Entries;
//						m_locals.add(proj);
//					}
//				}
//			}
//		}
//	}
//
//	public int IsLocalPackageNeeded(GIPropertiesPackage pack)
//	{
//		int amount = 0;
//		for(GIProjectInfo local : m_locals)
//		{
//			GILocalProjectInfo proj = (GILocalProjectInfo)local;
//			if(proj!= null)
//			{
//				for(GIPropertiesPackage proj_pack : proj.m_project_properties.m_Entries)
//				{
//					if(proj_pack.equal(pack))
//					{
//						amount = amount + 1;
//					}
//				}
//			}
//		}
//		return amount;
//	}
//	public void RefreshStatuses()
//	{
//		for(GIProjectInfo proj : m_locals)
//		{
//			new CheckingLocalProjectTask().execute(proj);
//		}
//		for(GIProjectInfo proj : m_remotes)
//		{
//			proj.getStatus(this);
//		}
//	}
//
//	public void DeleteLocalProject(GILocalProjectInfo proj)
//	{
//		proj.m_inState = GIPresenter.ProjectInState.DELETING;
//		File proj_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + proj.m_file);
//		Log.d(LOG_TAG, "to delete: " + proj.m_file);
//		if(proj_file.delete())
//		{
//			for(GIPropertiesPackage proj_pack : proj.m_project_properties.m_Entries)
//			{
//				if(IsLocalPackageNeeded(proj_pack) <= 1)
//				{
//					for(GIPropertiesFile file : proj_pack.m_Entries)
//					{
//						File data_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + file.m_name);
//					    Log.d(LOG_TAG, "to delete: " + file.m_name);
//						data_file.delete();
//					}
//				}
//			}
//			m_locals.remove(proj);
//		}
//		RefreshStatuses();
//		m_presenter.SomethingChanged();
//	}
//
//	public void AddRemoteProjects ()
//	{
//		new ProjectsListTask().execute(m_remotes);
//	}
//	public void getProjectInfo(GIProjectInfo project)
//	{
//		project.m_inState = GIPresenter.ProjectInState.DOWNLOADING;
//		new ProjectInfoTask().execute(project);
//	}
//
//	public void getProject(GIProjectInfo proj)
//	{
//		proj.m_inState = GIPresenter.ProjectInState.DOWNLOADING;
//		new GetProjectTask().execute(proj);
//	}
//	public boolean checkPackage(GIPropertiesPackage pack)
//	{
//		CheckPackageTask task = new CheckPackageTask();
//		boolean res = false;
//		task.execute(pack);
//		try
//		{
//			res = task.get();
//		} catch (InterruptedException e) {} catch (ExecutionException e) {}
//		return res;
//	}
//
//	public void checkLocalProjectStatus(GIProjectInfo pack)
//	{
//		new CheckingLocalProjectTask().execute(pack);
//	}
//	public GIProjectInfo.GIProjectStatus getLocalProjectStatus(GIProjectInfo proj)
//	{
//		ArrayList<GIProjectInfo> remotes = new ArrayList<GIProjectInfo>();
//		StringBuilder builder = new StringBuilder();
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpGet httpget = new HttpGet("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/getProjects&key=" + m_session_key);
//		try
//		{
//			HttpResponse response = httpclient.execute(httpget);
//	        if (response.getStatusLine().getStatusCode() == 200)
//	        {
//	            HttpEntity entity = response.getEntity();
//	        	InputStream instream = entity.getContent();
//				String line;
//				BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//				while ( (line = serverResponse.readLine() ) != null )
//				{
//					builder.append(line);
//				}
//	        }
//	    }
//		catch (ClientProtocolException e)
//	    {
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
//		String result = builder.toString();
//		try
//		{
//			JSONArray jsonArray = new JSONArray(result);
//			for(int i = 0; i < jsonArray.length(); i++)
//			{
//				JSONObject obj = jsonArray.getJSONObject(i);
//
//				int id = obj.getInt("id");
//				String name = obj.getString("name");
//				String file = obj.getString("file");
//				if(file!= null)
//				{
//					remotes.add(new GIRemoteProjectInfo(id, name, file, GIProjectInfo.GIProjectStatus.UNKNOWN));
//				}
//			}
//
//		}catch (Exception e) {}
//
//		proj.m_status = GIProjectInfo.GIProjectStatus.UNKNOWN;
//		//---------------------------
//		boolean checking_res = true;
//		for(GIPropertiesPackage pack : proj.m_project_properties.m_Entries)
//		{
//			try
//			{
//				//File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + pack.m_name);
//				//if(file.exists())
//				{
//					//FileInputStream stream = new FileInputStream(file);
//					//String md5checksum = md5(stream);
//					String md5checksum = pack.m_crc;
//			        String responce = "";
//					HttpClient httpclient1 = new DefaultHttpClient();
//					HttpPost httppost = new HttpPost("http://geoportal.tc-geo.ru/gis/ios.php?r=Users/checkPackage&key=" + m_session_key + "&id=" + pack.m_id + "&md5=" + md5checksum);
//					try
//					{
//						HttpResponse response = httpclient1.execute(httppost);
//				        if (response.getStatusLine().getStatusCode() == 200)
//				        {
//				            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
//				            InputStream instream = bufHttpEntity.getContent();
//							String line;
//							BufferedReader serverResponse = new BufferedReader( new InputStreamReader(instream) );
//							while ( (line = serverResponse.readLine() ) != null )
//							{
//								responce = responce + line;
//							}
//							if(!responce.equalsIgnoreCase("latest"))
//							{
//								checking_res = false;
//								break;
//								//return true;
//							}
//				        }
//				    }
//					catch (ClientProtocolException e)
//				    {
//						e.printStackTrace();
//				    }
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//			catch (Exception e) { }
//		}
//		boolean present_res = false;
//		for(GIProjectInfo item : remotes)
//		{
//			if(item.m_name.equalsIgnoreCase(proj.m_name) && item.m_file.equalsIgnoreCase(proj.m_file) &&  (item.m_id == proj.m_id))
//			{
//				present_res = true;
//			}
//
//		}
//		if(checking_res && present_res)
//		{
//			proj.m_status = GIProjectInfo.GIProjectStatus.UPTODATE;
//		}
//		if(checking_res && !present_res)
//		{
//			proj.m_status = GIProjectInfo.GIProjectStatus.STRANGE;
//		}
//		if(!checking_res && present_res)
//		{
//			proj.m_status = GIProjectInfo.GIProjectStatus.FAIL;
//		}
//		//---------------------------
//		return proj.m_status;
//	}
//
//	public void getProjectPackages(GIProjectInfo proj)
//	{
//		proj.m_inState = GIPresenter.ProjectInState.UPDATING;
//		new GetPackageTask().execute(proj);
//	}
//
//	public void ReadProject(GIProjectInfo proj)
//	{
//		new readProjectTask().execute(proj);
//	}
//	private GIProjectInfo getLocalAt(int i)
//	{
//		if(m_locals != null)
//		{
//			if(i >= 0 && i < m_locals.size())
//			{
//				return m_locals.get(i);
//			}
//		}
//		return null;
//	}
//	private GIProjectInfo getRemotelAt(int i)
//	{
//		if(m_remotes != null)
//		{
//			if(i >= 0 && i < m_remotes.size())
//			{
//				return m_remotes.get(i);
//			}
//		}
//		return null;
//	}
//	public GIProjectInfo getAt(GIPresenter.ListType type, int i)
//	{
//		if(type == GIPresenter.ListType.REMOTE)
//		{
//			return getRemotelAt(i);
//		}
//		if(type == GIPresenter.ListType.LOCAL)
//		{
//			return getLocalAt(i);
//		}
//		return null;
//	}
//
//}
