package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.LonLatEvent;

public class GIGPSButtonView extends RelativeLayout 
{
	@BindView(R.id.imageViewStatus)
	public ImageView ivStatus;
	int speed;
	@BindView(R.id.textViewAccurancy)
	TextView m_textViewAccurancy;
	@BindView(R.id.tvSpeed)
	TextView tvSpeed;
	private View layoutView;
	private boolean blink;
	protected CompositeDisposable subscription = new CompositeDisposable();

    Geoinfo activity;

	public GIGPSButtonView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	public GIGPSButtonView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	public GIGPSButtonView(Context context) 
	{
		super(context);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	private void Init(Context context)
	{
		ButterKnife.bind(this, layoutView);
		ivStatus.setImageResource(R.drawable.gps_disabeled);
		m_textViewAccurancy.setText("-- m");
		tvSpeed.setVisibility(INVISIBLE);
		blink = false;
        activity = (Geoinfo) context;
        subscription.add(
                Observable.combineLatest(
                        activity.getEnabledBehaviorSubject(),
                        activity.getRunnigSubject(),
                        new BiFunction<Boolean, Integer, Pair<Boolean, Integer>>() {
                            @Override
                            public Pair<Boolean, Integer> apply(Boolean aBoolean, Integer integer) {
                                return new Pair<>(aBoolean, integer);
                            }
                        })
                        .filter(new Predicate<Pair<Boolean, Integer>>() {
                            @Override
                            public boolean test(Pair<Boolean, Integer> pair) {
                                return (pair.second & LonLatEvent.FLAG_RUNNING) != 0;
                            }
                        })
                        .map(new Function<Pair<Boolean, Integer>, Boolean>() {
                            @Override
                            public Boolean apply(Pair<Boolean, Integer> integerIntegerPair) {
                                return integerIntegerPair.first;
                            }
                        })

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean enabeled) {
                                if (enabeled) {
                                    ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
                                } else {
                                    ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_disabeled));
                                    m_textViewAccurancy.setText("-- m");
                                }
                            }
                        }));

        subscription.add(
                Observable.combineLatest(
                        activity.getStatusBehaviorSubject().hide(),
                        activity.getNmeaBehaviorSubject().hide(),
                        activity.getLocationBehaviorSubject(),
                        activity.getRunnigSubject()
                        , new Function4<GpsStatus, String, Location, Integer, Pair<Integer, Integer>>() {
                            @Override
                            public Pair<Integer, Integer> apply(GpsStatus gpsStatus, String nmea, Location location, Integer running) {
						int countSatellite = 0;
						Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
						for (GpsSatellite sat : sats) {
							countSatellite++;
						}

						if (location.getAccuracy() < 15 || countSatellite > 5) {
                            return new Pair<Integer, Integer>(1, running);
						} else {
							int[] res = ParseNmea(nmea);
                            return new Pair<Integer, Integer>(res[1], running);
                        }
                            }
                        })
                        .filter(new Predicate<Pair<Integer, Integer>>() {
                            @Override
                            public boolean test(Pair<Integer, Integer> pair) {
                                return (pair.second & LonLatEvent.FLAG_RUNNING) != 0;
                            }
                        })
                        .map(new Function<Pair<Integer, Integer>, Integer>() {
                            @Override
                            public Integer apply(Pair<Integer, Integer> integerIntegerPair) {
                                return integerIntegerPair.first;
                            }
                        })
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Integer>() {
					@Override
					public void accept(Integer integer) {
						ShowGPSStatus(integer);
					}
				}));


        subscription.add(
                Observable.combineLatest(
                        activity.getLocation(),
                        activity.getRunnigSubject(),
                        new BiFunction<Location, Integer, Pair<Location, Integer>>() {
                            @Override
                            public Pair<Location, Integer> apply(Location aBoolean, Integer integer) {
                                return new Pair<>(aBoolean, integer);
                            }
                        })
                        .filter(new Predicate<Pair<Location, Integer>>() {
                            @Override
                            public boolean test(Pair<Location, Integer> pair) {
                                return (pair.second & LonLatEvent.FLAG_RUNNING) != 0;
                            }
                        })
                        .map(new Function<Pair<Location, Integer>, Location>() {
                            @Override
                            public Location apply(Pair<Location, Integer> integerIntegerPair) {
                                return integerIntegerPair.first;
                            }
                        })

				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Location>() {
					@Override
					public void accept(Location location) {
						float accurancy = location.getAccuracy();
						m_textViewAccurancy.setText(String.format("±%02d m", (int) accurancy));
						blink = !blink;
						if (blink) {
							m_textViewAccurancy.setTextColor(Color.argb(255, 63, 255, 63));
						} else {
							m_textViewAccurancy.setTextColor(Color.argb(255, 191, 63, 0));
						}

						speed = (int) Math.round(3.6 * location.getSpeed());
						if (speed < 10) {
							speed = 0;
							tvSpeed.setVisibility(INVISIBLE);
						} else {
							tvSpeed.setVisibility(VISIBLE);
							tvSpeed.setText(/*String.format("%03d", speed)*/String.valueOf(speed));
						}
					}
				})
		);
	}

    @Override
    protected void onDetachedFromWindow() {
        subscription.dispose();
        super.onDetachedFromWindow();
    }

    public void SetGPSEnabledStatus(boolean enabeled)
	{
		if(enabeled)
		{
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
		}
		else
		{
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_disabeled));
			m_textViewAccurancy.setText("-- m");
		}
	}
	public void ShowGPSStatus(int status)
	{
		switch(status)
		{
			case 0:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
				break;
			}
			case 2:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_unavaliable));
				break;
			}
			case 1:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_avaliable));
				break;
			}

		}

	}

	public int[] ParseNmea(String nmea)
	{
		/*
 *      1         Fix quality: 0 = invalid
                   1 = GPS fix (SPS)
                   2 = DGPS fix
                   3 = PPS fix
			       4 = Real Time Kinematic
			       5 = Float RTK
                   6 = estimated (dead reckoning) (2.3 feature)
			       7 = Manual input mode
			       8 = Simulation mode
		 */
		String[] fields = nmea.split(",");
		int quality = 0;
		int count = 0;
		
		if(fields != null)
		{
			if(fields.length > 0)
			{
				if(fields[0].equalsIgnoreCase("$GPGGA"))
				{
					if(!fields[7].equals(""))
					{
						count = Integer.valueOf(fields[7]);
					}
					if(!fields[6].equals(""))
					{
						quality = Integer.valueOf(fields[6]);
					}
				}
				/*if(fields[0].equalsIgnoreCase("$GPGSA"))
				{
					count = 0;
					if(!fields[2].equals(""))
					{
						if(Integer.valueOf(fields[2]) > 1)
						{
							quality = 1;
						}
					}
					else
					{
						quality = 2;
					}
				}
				if(fields[0].equalsIgnoreCase("$GPGSV"))
				{
					if(!fields[3].equals(""))
					{
						count = Integer.valueOf(fields[3]);
					}
					quality = 2;
				}
				if(fields[0].equalsIgnoreCase("$GPRMC"))
				{
					count = 0;
					quality = 1;
				}
				if(fields[0].equalsIgnoreCase("$GPGLL"))
				{
					count = 0;
					quality = 1;
				}
				if(fields[0].equalsIgnoreCase("$GPGLL"))
				{
					count = 0;
					quality = 1;
				}*/

			}
			
		}
		int[] res = new int[2];
		res[0] = count;
		res[1] = quality;
		return res;
	}
}
