package ru.tcgeo.application.utils;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.ParseException;


/**
 * Created by a_belov on 18.03.15.
 * http://horribile.blogspot.co.nz/2011/12/using-mask-with-edittext.html
 */
public class MaskedWatcher implements TextWatcher {

    private String mMask;
    String mResult = "";
    MaskedTextEditComplitListener mListener;

    public MaskedWatcher(String mask, MaskedTextEditComplitListener listener) {
        mMask = mask;
        mListener = listener;
    }

    @Override
    public void afterTextChanged(Editable s) {

        String mask = mMask;
        String value = s.toString();

        if (value.equals(mResult))
            return;

        try {

            // prepare the formatter
            MaskedFormatter formatter = new MaskedFormatter(mask);
            formatter.setValueContainsLiteralCharacters(false);
            formatter.setPlaceholderCharacter((char) 1);

            // get a string with applied mask and placeholder chars
            value = formatter.valueToString(value);

            try {

                // find first placeholder
                value = value.substring(0, value.indexOf((char) 1));

                //process a mask char
                //if
                while (value.charAt(value.length() - 1) == mask.charAt(value.length() - 1))
                {
                    value = value.substring(0, value.length() - 1);
                }

            } catch (Exception e) {
            }

            mResult = value;

            s.replace(0, s.length(), value);

            if(mListener != null && s.length() == mMask.length())
            {
                mListener.doOnComplited();
            }



        } catch (ParseException e) {

            //the entered value does not match a mask
            int offset = e.getErrorOffset();
            value = removeCharAt(value, offset);
            s.replace(0, s.length(), value);

        }


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {
    }

    public static String removeCharAt(String s, int pos) {

        StringBuffer buffer = new StringBuffer(s.length() - 1);
        buffer.append(s.substring(0, pos)).append(s.substring(pos + 1));
        return buffer.toString();

    }

}
