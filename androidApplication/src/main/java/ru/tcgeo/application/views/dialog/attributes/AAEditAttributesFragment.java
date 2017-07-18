package ru.tcgeo.application.views.dialog.attributes;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.Attribute;
import ru.tcgeo.application.views.adapter.AttributesAdapter;
import ru.tcgeo.application.views.callback.AttributesCallback;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GI_WktGeometry;

public class AAEditAttributesFragment extends Dialog {

    @Bind(R.id.rvAttributes)
    RecyclerView rvAttributes;

    AttributesAdapter mAdapter;

    public AAEditAttributesFragment(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @OnClick(R.id.tvSave)
    public void onSave() {
//        for (int i = 0; i < m_attributes_list.getCount(); i++) {
//            TextView name_text = (TextView) m_attributes_list.getChildAt(i).findViewById(R.id.field_name);
//            EditText value_text = (EditText) m_attributes_list.getChildAt(i).findViewById(R.id.field_value);
//            if (value_text != null && name_text != null) {
//                GIEditLayersKeeper.Instance().m_geometry.m_attributes.get(String.valueOf(name_text.getText())).m_value = String.valueOf(value_text.getText().toString());
//            }
//            if (GIEditLayersKeeper.Instance().m_layer.type_ == GILayer.GILayerType.XML) {
//                if (new_attribute_value.getText().length() > 0) {
//                    GIDBaseField new_field = new GIDBaseField();
//                    new_field.m_value = new_attribute_value.getText().toString();
//                    //TODO
//                    new_field.m_name = new_attribute_name.getText().toString();
//                    GIEditLayersKeeper.Instance().m_geometry.m_attributes.put(new_attribute_name.getText().toString(), new_field);
//                }
//            }
//        }
//        GIEditLayersKeeper.Instance().m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
//        GIEditLayersKeeper.Instance().m_layer.Save();
//        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
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
        setContentView(R.layout.aa_fragment_edit_attributes);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        GI_WktGeometry geometry = GIEditLayersKeeper.Instance().m_geometry;
        List<Attribute> data = new ArrayList<>();
//        data.add(new Attribute(getResources().getString(R.string.new_attribute_name), getResources().getString(R.string.new_attribute_value), 1));
        for (String name : geometry.m_attributes.keySet()) {
            GIDBaseField value = geometry.m_attributes.get(name);
            Attribute item = new Attribute(name, value.m_value.toString());
            data.add(item);
        }

        mAdapter = new AttributesAdapter.Builder(getContext())
                .callback(new AttributesCallback() {
                    @Override
                    public void onClick() {

                    }
                })
                .data(data)
                .build();

        rvAttributes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvAttributes.setAdapter(mAdapter);
    }

}
