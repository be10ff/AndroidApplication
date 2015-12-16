package ru.tcgeo.application.gilib;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import ru.tcgeo.application.R;
import ru.tcgeo.application.utils.GIYandexUtils;
import ru.tcgeo.application.utils.MaskedWatcher;
import ru.tcgeo.application.wkt.GI_WktPoint;

public class GILonLatInputDialog extends DialogFragment 
{
	private EditText m_lon_dec;
	private EditText m_lat_dec;
	private EditText m_lon_can;
	private EditText m_lat_can;
	private EditText m_lon_grad_min;
	private EditText m_lat_grad_min;
	GI_WktPoint m_point;
	GIGeometryPointControl m_control;
//	public GILonLatInputDialog()
//	{
//	}

	public GILonLatInputDialog(GIGeometryPointControl control)
	{
		m_control = control;
		m_point = m_control.m_WKTPoint;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
//		getDialog().setTitle("Dialog");
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.lonlat_input_layout, null);
		m_lon_dec = (EditText) v.findViewById(R.id.lon_decimal);
		m_lat_dec = (EditText) v.findViewById(R.id.lat_decimal);
		m_lon_grad_min = (EditText) v.findViewById(R.id.lon_grad_min);
		m_lat_grad_min = (EditText) v.findViewById(R.id.lat_grad_min);
		m_lon_can = (EditText) v.findViewById(R.id.lon_can);
		m_lat_can = (EditText) v.findViewById(R.id.lat_can);

		m_lon_dec.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lon_dec.addTextChangedListener(new MaskedWatcher("##.########°", null));
		m_lon_dec.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void afterTextChanged(Editable s) {
				if (m_lon_dec.hasFocus()) {
					try {
						m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(s.toString());
						m_lon_can.setText(getCanonicalCoordString(m_point.m_lon));
						m_lon_grad_min.setText(getGradMinCoordString(m_point.m_lon));

					} catch (NumberFormatException e) {
					}
				}
			}
		});
		m_lat_dec.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lat_dec.addTextChangedListener(new MaskedWatcher("##.########°", null));
		m_lat_dec.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (m_lat_dec.hasFocus()) {
					try {
						//m_point.m_lat = Double.valueOf(s.toString());
						m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(s.toString());
						m_lat_can.setText(getCanonicalCoordString(m_point.m_lat));
						m_lat_grad_min.setText(getGradMinCoordString(m_point.m_lat));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
		/**/
		m_lon_grad_min.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lon_grad_min.addTextChangedListener(new MaskedWatcher("##° ##.######\'", null));
		m_lon_grad_min.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (m_lon_grad_min.hasFocus()) {
					try {
//						int index = m_lon_grad_min.getSelectionEnd();
						m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(s.toString().replaceAll("[^0-9.,]", ""));
						m_lon_can.setText(getCanonicalCoordString(m_point.m_lon));
						m_lon_dec.setText(String.valueOf(m_point.m_lon));
//						m_lon_grad_min.setSelection(index);
					} catch (NumberFormatException e) {
					}
				}
			}
		});

		m_lat_grad_min.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lat_grad_min.addTextChangedListener(new MaskedWatcher("##° ##.######\'", null));
		m_lat_grad_min.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (m_lat_grad_min.hasFocus()) {
					try {
//						int index = m_lon_grad_min.getSelectionEnd();
						m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(s.toString().replaceAll("[^0-9.,]", ""));
						m_lat_can.setText(getCanonicalCoordString(m_point.m_lat));
						m_lat_dec.setText(String.valueOf(m_point.m_lat));
//						m_lon_grad_min.setSelection(index);
					} catch (NumberFormatException e) {
					}
				}
			}
		});
		/**/
		m_lon_can.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lon_can.addTextChangedListener(new MaskedWatcher("##° ##ʹ ##.####\"", null));
		m_lon_can.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (m_lon_can.hasFocus()) {
					try {
						int index = m_lon_can.getSelectionEnd();
						m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(s.toString().replaceAll("[^0-9.,]", ""));
						m_lon_grad_min.setText(getGradMinCoordString(m_point.m_lon));
						m_lon_dec.setText(String.valueOf(m_point.m_lon));
						m_lon_can.setSelection(index);
					} catch (NumberFormatException e) {
					}
				}
			}
		});

		m_lat_can.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		m_lat_can.addTextChangedListener(new MaskedWatcher("##° ##ʹ ##.####\"", null));
		m_lat_can.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (m_lat_can.hasFocus()) {
					try {
						m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(s.toString().replaceAll("[^0-9.,]", ""));
						m_lat_grad_min.setText(getGradMinCoordString(m_point.m_lat));
						m_lat_dec.setText(String.valueOf(m_point.m_lat));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
		/**/
		m_lon_dec.setText(String.format("%.8f", m_point.m_lon));
		m_lat_dec.setText(String.format("%.8f", m_point.m_lat));

		m_lon_grad_min.setText(getGradMinCoordString(m_point.m_lon));
		m_lat_grad_min.setText(getGradMinCoordString(m_point.m_lat));

		m_lon_can.setText(getCanonicalCoordString(m_point.m_lon));
		m_lat_can.setText(getCanonicalCoordString(m_point.m_lat));


		return v;
	}


	public void onCancel(DialogInterface dialog)
	{
		try
		{
			m_lon_dec.setTextColor(Color.RED);
			m_lon_dec.requestFocus();
			//m_point.m_lon = Double.valueOf(m_lon_dec.getText().toString());
			m_lon_dec.setTextColor(Color.BLACK);
			m_lat_dec.setTextColor(Color.RED);
			m_lat_dec.requestFocus();
			//m_point.m_lat = Double.valueOf(m_lat_dec.getText().toString());
			m_lat_dec.setTextColor(Color.BLACK);
			
//			m_control.m_WKTPoint.m_lon = Double.valueOf(m_lon_dec.getText().toString());
//			m_control.m_WKTPoint.m_lat = Double.valueOf(m_lat_dec.getText().toString());
			m_control.m_WKTPoint.m_lon = m_point.m_lon;
			m_control.m_WKTPoint.m_lat = m_point.m_lat;
			
			m_control.setWKTPoint(m_control.m_WKTPoint);
			//m_control.setWKTPoint(m_point);
			GIEditLayersKeeper.Instance().m_current_geometry_editing_control.invalidate();
			super.onCancel(dialog);
		}
		catch(NumberFormatException e)
		{}
	}
    public String getCanonicalCoordString(double coord)
    {
    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
    	int mins = (int)Math.floor((coord - degrees)*60);
    	double secs = ((coord - degrees)*60-mins)*60;
//		String res =  String.format("%02d° %02d\' %07.4f\"", 1, 1, 0.1f);

    	String res =  String.format("%02d° %02d' %07.4f\"", degrees, mins, secs);

//		String res =  customFormat("00", degrees) + "° " + customFormat("00", mins) + "' " + customFormat("00.####", secs)  + "\"";
//		getDoubleCoordFromGGMMSSString(res);
		return res;
    }
    public String getGradMinCoordString(double coord)
    {
    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
    	float mins = (float) ((coord - degrees)*60);
		String res =  String.format("%02d° %09.6f'", degrees, mins);
//		String res =  customFormat("00", degrees)+ "° " + customFormat("00.######", mins) + "'";
//		getDoubleCoordFromGGMMMMtring(res);
		return res;

    }

	public double getDoubleCoordFromGGMMSSString(String input){

		String s_grad = input.substring(0, 2);
		String s_min = input.substring(4, 6);
		double sec;
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		try {
			sec = nf.parse(input.substring(8, 15)).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
			sec = 0.0;
		}
		double grad = Double.valueOf(s_grad);
		double min = Double.valueOf(s_min);
		double res = grad + (1f/60)*(min +(1f/60)*sec);
		return res;
	}
	public double getDoubleCoordFromGGMMMMtring(String input){

		String s_grad = input.substring(0, 2);

		double min;
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		try {
			min = nf.parse(input.substring(4, 13)).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
			min = 0.0;
		}
		double grad = Double.valueOf(s_grad);

		double res = grad + (1f/60)*(min);
		return res;
	}
	static public String customFormat(String pattern, double value ) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(value);
	}

}
