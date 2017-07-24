package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.IFolderItemListener;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIGroupLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesStyle;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.gilib.parser.GISource;
import ru.tcgeo.application.home_screen.adapter.LayersAdapter;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.views.adapter.ReLayersAdapter;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReSettingsDialog extends Dialog implements IFolderItemListener {

    ListView mLayersList;
    FrameLayout mProperties;
    LayersAdapter layersAdapter;
    @Bind(R.id.rvLayers)
    RecyclerView rvLayers;
    @Bind(R.id.fabAdd)
    ImageView fabAdd;
    ReLayersAdapter adapter;
    private GIMap mMap;
    private LayerHolderCallback callback;
    private List<GITuple> data;
    private Context context;

    public ReSettingsDialog(Builder builder) {
        super(builder.context);
        this.callback = builder.callback;
        this.context = builder.context;
        this.data = builder.data;
    }


//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        App.getInstance().getEventBus().register(this);
//        getDialog().setCanceledOnTouchOutside(true);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mMap = ((Geoinfo)getActivity()).getMap();
//        View v = inflater.inflate(R.layout.dialog_settings, container, false);
//        mLayersList = (ListView)  v.findViewById(R.id.layers_list);
//        mProperties = (FrameLayout)v.findViewById(R.id.content);
//
//        layersAdapter = new LayersAdapter((Geoinfo)getActivity(),
//                R.layout.layers_list_item, R.id.layers_list_item_text);
//
//        View header = inflater.inflate(
//                R.layout.add_layer_header_layout, null);
//        header.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                OpenFileDialog dlg = new OpenFileDialog();
//                dlg.setIFolderItemListener(ReSettingsDialog.this);
//                dlg.show(getChildFragmentManager(), "tag");
//            }
//        });
//        mLayersList.addHeaderView(header);
//
//        addLayers((GIGroupLayer) mMap.m_layers, layersAdapter);
//        mLayersList.setAdapter(layersAdapter);
//        mLayersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                final LayersAdapterItem item = layersAdapter.getItem(position - mLayersList.getHeaderViewsCount());
//
//                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//                ft.replace(R.id.content, new AllSettingsFragment(mMap, item)).commit();
//            }
//        });
//        return v;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_settings);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mMap = ((Geoinfo) context).getMap();
        adapter = new ReLayersAdapter.Builder(context)
                .callback(callback)
                .data(getLayers(mMap.m_layers))
                .build();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        rvLayers.setLayoutManager(layoutManager);
        rvLayers.addItemDecoration(dividerItemDecoration);
        rvLayers.setAdapter(adapter);
    }

    //
    @Override
    public void onStart() {
        super.onStart();

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        int dialogWidth = ScreenUtils.getScreenWidth(context);
        int dialogHeight = (int) (ScreenUtils.getScreenHeight(context) * 0.9f);
        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    public List<GITuple> getLayers(GIGroupLayer layer) {
        List<GITuple> result = new ArrayList<>();
        for (GITuple tuple : layer.m_list) {
            if (GILayer.GILayerType.LAYER_GROUP == tuple.layer.type_)
                result.addAll(getLayers((GIGroupLayer) tuple.layer));
            else {
                result.add(tuple);
            }
        }
        return result;
    }

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(context, R.string.file_error,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnFileClicked(File file) {
        addLayer(file);

        layersAdapter.clear();
//        addLayers((GIGroupLayer) mMap.m_layers, layersAdapter);
//        mMap.UpdateMap();
    }

    public void addLayer(File file) {

        String filenameArray[] = file.getName().split("\\.");
        String extention = filenameArray[filenameArray.length - 1];
        if (extention.equalsIgnoreCase("sqlitedb")) {
            addSQLLayer(file);
        } else if (extention.equalsIgnoreCase("xml")) {
            addXMLLayer(file);
        } else if (extention.equalsIgnoreCase("yandex") || extention.equalsIgnoreCase("traffic")) {
            addYandexTraffic(file);
        }
        mMap.UpdateMap();

    }

    public void addSQLLayer(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("absolute", file.getAbsolutePath()); //getName()
        properties_layer.m_type = GILayer.GILayerType.SQL_YANDEX_LAYER;
        properties_layer.m_strType = "SQL_YANDEX_LAYER";
        GILayer layer;
        //TODO
        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.SQL_YANDEX_LAYER);
        properties_layer.m_sqldb = new GISQLDB();//"auto";
        properties_layer.m_sqldb.m_zoom_type = "auto";

        properties_layer.m_sqldb.m_min_z = 1;
        properties_layer.m_sqldb.m_max_z = 19;

        int min = 1;
        int max = 19;

        properties_layer.m_range = new GIRange();
        double con = 0.0254 * 0.0066 * 256 / (0.5 * 40000000);
        properties_layer.m_range.m_from = (int) (1 / (Math.pow(2, min) * con));
        properties_layer.m_range.m_to = (int) (1 / (Math.pow(2, max) * con));

        mMap.ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;
        mMap.InsertLayerAt(layer, 0);
    }

    public void addYandexTraffic(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("text", "yandex"); //getName()
        properties_layer.m_type = GILayer.GILayerType.ON_LINE;
        properties_layer.m_strType = "ON_LINE";
        GILayer layer;
        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.ON_LINE);
        mMap.ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;
        mMap.AddLayer(layer);
    }

    public void addXMLLayer(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("absolute", file.getAbsolutePath());
        properties_layer.m_type = GILayer.GILayerType.XML;
        properties_layer.m_strType = "XML";
        GILayer layer;
        //
        Paint fill = new Paint();
        Paint line = new Paint();

        GIColor color_fill = new GIColor.Builder().description("fill").name("gray").build();
        GIColor color_line = new GIColor.Builder().description("line").name("gray").build();

        line.setColor(color_line.Get());
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(2);

        fill.setColor(color_fill.Get());
        fill.setStrokeWidth(2);
        fill.setStyle(Paint.Style.FILL);

        GIVectorStyle vstyle = new GIVectorStyle(line, fill, 1);

        properties_layer.m_style = new GIPropertiesStyle.Builder()
                .type("vector")
                .lineWidth(2)
                .opacity(1)
                .color(color_line)
                .color(color_fill)
                .build();

        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.XML, vstyle);
        mMap.ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;

        mMap.AddLayer(layer);
    }

//    @Subscribe  public void refresh(ProjectChangedEvent e){
//        layersAdapter.clear();
//        addLayers((GIGroupLayer) mMap.m_layers, layersAdapter);
//        mMap.UpdateMap();
//    }

    public static class Builder {

        private LayerHolderCallback callback;
        private List<GITuple> data;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(LayerHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GITuple> data) {
            this.data = data;
            return this;
        }

        public ReSettingsDialog build() {
            return new ReSettingsDialog(this);
        }
    }
}
