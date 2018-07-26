package ru.tcgeo.application.views.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.Attribute;
import ru.tcgeo.application.views.callback.AttributesCallback;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesHolder extends RecyclerView.ViewHolder implements TextWatcher {

    @BindView(R.id.etName)
    public EditText etName;

    @BindView(R.id.etValue)
    public EditText etValue;

    private Context context;

    private boolean ignore = true;

    private AttributesCallback callback;

    public AttributesHolder(Context context, View itemView, AttributesCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.callback = callback;

        etName.addTextChangedListener(this);
        etValue.addTextChangedListener(this);
    }

    public void bind(Attribute a) {
        ignore = true;
        etName.setText(a.name);
        etValue.setText(a.value);
        ignore = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!ignore) {
            callback.onFieldChanged(this);
        }
    }
}
