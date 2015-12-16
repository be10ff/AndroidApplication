//package ru.tcgeo.application.project_management;
//
//import android.content.Intent;
//import android.os.IBinder;
//
//public class GIServerService extends android.app.Service
//{
//
//	@Override
//	public IBinder onBind(Intent intent)
//	{
//		return null;
//	}
//	public void onCreate()
//	{
//		super.onCreate();
//		//GIServer server = GIServer.Instance();
//	}
//
//	public int onStartCommand(Intent intent, int flags, int startId)
//	{
//		GIServer.Instance().Initialize();
//		return super.onStartCommand(intent, flags, startId);
//	}
//
//	public void onDestroy()
//	{
//		super.onDestroy();
//	}
//}
