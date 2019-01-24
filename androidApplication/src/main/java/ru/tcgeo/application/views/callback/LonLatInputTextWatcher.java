package ru.tcgeo.application.views.callback;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by artem on 16.04.18.
 */

public abstract class LonLatInputTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
