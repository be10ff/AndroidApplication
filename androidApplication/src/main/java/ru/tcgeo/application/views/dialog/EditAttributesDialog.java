package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.Attribute;
import ru.tcgeo.application.views.adapter.AttributesAdapter;
import ru.tcgeo.application.views.callback.AttributesCallback;
import ru.tcgeo.application.views.viewholder.AttributesHolder;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GI_WktGeometry;

public class EditAttributesDialog extends Dialog {

    @Bind(R.id.rvAttributes)
    RecyclerView rvAttributes;

    private AttributesAdapter mAdapter;

    private Map<String, GIDBaseField> attributes;

    public EditAttributesDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, Map<String, GIDBaseField> attributes) {
        super(context, cancelable, cancelListener);
        attributes = GIEditLayersKeeper.Instance().m_geometry.m_attributes;
        this.attributes = attributes;
    }

    @OnClick(R.id.tvSave)
    public void onSave() {
        for (int i = 1; i < mAdapter.getItemCount() - 1; i++) {
            Attribute a = mAdapter.getItem(i);
            if (attributes.containsKey(a.name)) {
                attributes.get(a.name).m_value = a.value;
            } else {
                if (!a.name.isEmpty() && !a.value.isEmpty()) {
                    GIDBaseField new_field = new GIDBaseField();
                    new_field.m_value = a.value;
                    new_field.m_name = a.name;
                    attributes.put(a.name, new_field);
                }
            }

        }

        GIEditLayersKeeper.Instance().m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
        GIEditLayersKeeper.Instance().m_layer.Save();
        dismiss();

    }

    @OnClick(R.id.tvDiscard)
    public void onDiscard() {
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_edit_attributes);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        List<Attribute> data = new ArrayList<>();
        data.add(new Attribute(getContext().getResources().getString(R.string.new_attribute_name), getContext().getResources().getString(R.string.new_attribute_value)));
        for (String name : attributes.keySet()) {
            GIDBaseField value = attributes.get(name);
            Attribute item = new Attribute(name, value.m_value.toString());
            data.add(item);
        }
        data.add(null);
        mAdapter = new AttributesAdapter.Builder(getContext())
                .callback(new AttributesCallback() {
                    @Override
                    public void onClick() {

                    }

                    @Override
                    public void onAddClick() {
                        mAdapter.addAttribute();
                    }

                    @Override
                    public void onFieldChanged(AttributesHolder holder) {

                        Attribute a = mAdapter.getItem(holder.getAdapterPosition());
                        a.value = holder.etValue.getText().toString();
                        a.name = holder.etName.getText().toString();
                    }
                })
                .data(data)
                .build();

        rvAttributes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvAttributes.setAdapter(mAdapter);
    }

//    public static class Builder{
//        private Context context;
//
//        public Builder(Context context){
//            this.context = context;
//        }
//
//        public
//    }

}
