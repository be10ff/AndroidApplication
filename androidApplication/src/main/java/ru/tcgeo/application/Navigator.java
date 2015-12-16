package ru.tcgeo.application;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GITouchControl;
import ru.tcgeo.application.gilib.gps.GICompassView;
import ru.tcgeo.application.gilib.gps.GIGPSButtonView;
import ru.tcgeo.application.utils.ScreenUtils;


/**
 * Created by a_belov on 02.07.15.
 */
public class Navigator extends Activity{

    GIMap map;
    GITouchControl touchControl;
    SharedPreferences sp;
    final public String SAVED_PATH = "default_project_path";
    Dialog projects_dialog;
    Dialog markers_dialog;
    Dialog editablelayers_dialog;
    FloatingActionMenu actionMenu;
    Pubnub mPubnub;
    GICompassView action;

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        if(mPubnub != null) {
            mPubnub.unsubscribe("my_channel");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_activity);
        ButterKnife.bind(this);
        action = new GICompassView(this);

        FloatingActionButton.LayoutParams menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(60), ScreenUtils.dpToPx(60));
        menu_params.setMargins(ScreenUtils.dpToPx(12), ScreenUtils.dpToPx(12), ScreenUtils.dpToPx(12), ScreenUtils.dpToPx(12));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(action)
                .setBackgroundDrawable(R.drawable.range)
                .setPosition(FloatingActionButton.POSITION_TOP_LEFT)
                .setLayoutParams(menu_params)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        FloatingActionButton.LayoutParams action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(36), ScreenUtils.dpToPx(36));
        itemBuilder.setLayoutParams(action_params);
        // repeat many times:

        ImageView itemIconPoi = new ImageView(this);
        itemIconPoi.setImageResource(R.drawable.poi);
        SubActionButton create_poi = itemBuilder.setContentView(itemIconPoi).setBackgroundDrawable(getResources().getDrawable(R.drawable.range)).build();
        create_poi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Navigator.this, "poi", Toast.LENGTH_SHORT).show();
                mPubnub.publish("my_channel", "message", new Callback() {
                    public void successCallback(String channel, Object response) {
                        System.out.println(response.toString());
                    }
                    public void errorCallback(String channel, PubnubError error) {
                        System.out.println(error.toString());
                    }
                });
                actionMenu.close(true);
            }
        });

        ImageView itemIconOpen = new ImageView(this);
        itemIconOpen.setImageResource(R.drawable.open);
        SubActionButton open = itemBuilder.setContentView(itemIconOpen).build();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Navigator.this, "open", Toast.LENGTH_SHORT).show();
                init();
                actionMenu.close(true);
            }
        });

        ImageView itemIconGps= new ImageView(this);
        itemIconGps.setImageResource(R.drawable.gps_avaliable);
        SubActionButton gps_ = itemBuilder.setContentView(itemIconGps).build();
        gps_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Navigator.this, "gps_avaliable", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        ImageView itemIconfinish= new ImageView(this);
        itemIconfinish.setImageResource(R.drawable.finish_poi);
        SubActionButton finish = itemBuilder.setContentView(itemIconfinish).build();
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Navigator.this, "finish", Toast.LENGTH_SHORT).show();
                actionMenu.close(true);
            }
        });

        GICompassView mCompass = new GICompassView(this);
        SubActionButton compass = itemBuilder.setContentView(mCompass).build();
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Navigator.this, "compass", Toast.LENGTH_SHORT ).show();
                actionMenu.close(true);
            }
        });

//        GIGPSButtonView mGPS = new GIGPSButtonView(this);
//        SubActionButton gps = itemBuilder.setContentView(mGPS).build();
//        gps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(Navigator.this, "compass", Toast.LENGTH_SHORT ).show();
//                actionMenu.close(true);
//            }
//        });

        actionMenu = new FloatingActionMenu.Builder(this)

                .addSubActionView(create_poi)
                .addSubActionView(open)
                .addSubActionView(gps_)
                .addSubActionView(finish)
                .addSubActionView(compass)
//                .addSubActionView(gps)

                .attachTo(actionButton)
                .setRadius(ScreenUtils.dpToPx(120))
                .setStartAngle(0)
                .setEndAngle(90)
                .build();


    }



    public void init(){
        mPubnub = new Pubnub("pub-c-ea66cd82-c431-4d3c-9bbf-473db788f6d3", "sub-c-a082e0c8-2b7b-11e5-9cac-0619f8945a4f");
        try {
            mPubnub.subscribe("my_channel", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            mPubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(final String channel, final Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());

                            action.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Navigator.this, "SUBSCRIBE : " + channel + " : "
                                            + message.getClass() + " : " + message.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }


}
