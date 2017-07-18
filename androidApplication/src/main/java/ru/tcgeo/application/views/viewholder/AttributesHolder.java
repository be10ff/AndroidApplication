package ru.tcgeo.application.views.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.AttributesCallback;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etValue)
    public EditText etValue;

    private Context context;

    private AttributesCallback callback;

    public AttributesHolder(Context context, View itemView, AttributesCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.callback = callback;
    }
}
