package ru.tcgeo.application.control;


import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.requestor.GIDataRequestorImp;


public class GITouchControl extends View implements GIControl, OnLongClickListener/*, ITouchControl */ {
    private static final int INVALID_ID = -1;
    //    public AppCompatCheckBox btnEditCreate;
//    public AppCompatCheckBox btnEditGeometry;
//    public AppCompatCheckBox btnEditAttributes;
//    public AppCompatCheckBox btnEditDelete;
//    public SubActionButton fbEditCreate;
//    public SubActionButton fbEditGeometry;
//    public SubActionButton fbEditAttributes;
//    public SubActionButton fbEditDelete;
//    public FloatingActionButton fbEditButton;
//    GIGPSButtonView fbGPS;
//    ImageButton fbEdit;
//    FloatingActionMenu editActionMenu;
    int mTouchSlop;
    float x;
    float y;
    float m_OriginPointX;
    float m_OriginPointY;
    boolean m_IsMoveClick;
    boolean m_IsClick;
    boolean m_IsMultyClick;
    boolean m_IsLongClick;
    boolean m_IsRule;
    boolean m_IsSquare;
    boolean m_GotPosition;
    //    Context m_context;
//    GIGPSLocationListener m_location_listener;
    Geoinfo activity;
    private ScaleGestureDetector m_ScaleDetector;
    private GIMap m_map;
    private int active_id = INVALID_ID;
    private float previousX;
    private float previousY;
    private Point m_focus;
    private float m_ScaleFactor;
    private boolean m_scaled;
//    private GIEditingStatus m_Status = GIEditingStatus.STOPPED;
//    private GITrackingStatus m_TrackingStatus = GITrackingStatus.STOP;


//    private LocationManager locationManager;

    public GITouchControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context);
    }

    public GITouchControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
    }

    public GITouchControl(Context context) {
        super(context);
        this.initialize(context);
    }

    private void initialize(Context context) {
        activity = (Geoinfo) context;
        m_ScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        m_scaled = false;
        m_ScaleFactor = 1.0f;
        m_focus = new Point();
        m_IsMoveClick = false;
        m_IsMultyClick = false;
        m_IsLongClick = false;
        m_GotPosition = false;
        this.setOnLongClickListener(this);
        //// TODO: 19.07.17
        ViewConfiguration vc = ViewConfiguration.get(activity);
        mTouchSlop = vc.getScaledTouchSlop();

//        // TODO 13/04/2018
//        m_Status = GIEditingStatus.STOPPED;
//        m_TrackingStatus = GITrackingStatus.STOP;

//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


    }

//    public void setupButtons() {
//
//        final FloatingActionButtonsCallback callback = activity;
//        // floating buttons
//        //--------------------------------------------------------------------
//
//        //--------------------------------------------------------------------
//        // GPS buttons
//        //--------------------------------------------------------------------
//        fbGPS = new GIGPSButtonView(activity);
//        FloatingActionButton.LayoutParams gps_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
//        gps_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
//
//        FloatingActionButton gps_action_button = new FloatingActionButton.Builder(activity)
//                .setContentView(fbGPS)
//                .setBackgroundDrawable(null)
//                .setPosition(FloatingActionButton.POSITION_TOP_LEFT)
//                .setLayoutParams(gps_menu_params)
//                .build();
//
//        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(activity);
//        FloatingActionButton.LayoutParams action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(84), ScreenUtils.dpToPx(84));
//        action_params.gravity = Gravity.CENTER_HORIZONTAL;
//        itemBuilder.setLayoutParams(action_params);
//
//
////        fbGPS.SetGPSEnabledStatus(m_location_listener.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
//
//        //-------------------------------------------------------------------
//        // GPS AUTO_FOLL0W
//        //--------------------------------------------------------------------
//        final CheckBox m_btnAutoFollow = new CheckBox(activity);
//        m_btnAutoFollow.setButtonDrawable(R.drawable.auto_follow_status_);
//        SubActionButton fbAutoFollow = itemBuilder.setContentView(m_btnAutoFollow).build();
//        m_btnAutoFollow.setChecked(GIEditLayersKeeper.Instance().toAutoFollow);
//        m_btnAutoFollow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GIEditLayersKeeper.Instance().toAutoFollow = m_btnAutoFollow.isChecked();
//                if (m_btnAutoFollow.isChecked()) {
//                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    if (location != null) {
//                        GILonLat go_to = GILonLat.fromLocation(location);
//                        GILonLat go_to_map = GIProjection.ReprojectLonLat(go_to, GIProjection.WGS84(), GIProjection.WorldMercator());
//                        m_map.SetCenter(go_to_map);
//                    }
//                }
////                GIEditLayersKeeper.Instance().GetPositionControl();
//            }
//        });
//
//        //--------------------------------------------------------------------
//        // GPS TRACK_CONTROL
//        //--------------------------------------------------------------------
//        final CheckBox m_btnTrackControl = new CheckBox(activity);
//        m_btnTrackControl.setTextSize(0);
//        m_btnTrackControl.setButtonDrawable(R.drawable.stop_start_track_button);
//        SubActionButton fbTrackControl = itemBuilder.setContentView(m_btnTrackControl).build();
//        m_btnTrackControl.setChecked(m_TrackingStatus == GITrackingStatus.WRITE);
//        m_btnTrackControl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (m_TrackingStatus == GITrackingStatus.STOP) {
//                    if (!GIEditLayersKeeper.Instance().CreateTrack()) {
//                        m_TrackingStatus = GITrackingStatus.STOP;
//                        m_btnTrackControl.setChecked(false);
//                    }
//                } else {
//                    m_TrackingStatus = GITrackingStatus.STOP;
//                    GIEditLayersKeeper.Instance().StopTrack();
//                }
//            }
//        });
//
//        //--------------------------------------------------------------------
//        // GPS SHOW TRACK
//        //--------------------------------------------------------------------
//        final CheckBox m_btnShowTrack = new CheckBox(activity);
//        m_btnShowTrack.setTextSize(0);
//        m_btnShowTrack.setButtonDrawable(R.drawable.show_track);
//        SubActionButton fbShowTrack = itemBuilder.setContentView(m_btnShowTrack).build();
//        m_btnShowTrack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (GIEditLayersKeeper.Instance().currentTrackControl != null) {
//                    GIEditLayersKeeper.Instance().currentTrackControl.Show(m_btnShowTrack.isChecked());
//                    App.Instance().getMap().UpdateMap();
//                }
//            }
//        });
//
//        //--------------------------------------------------------------------
//        // GPS POI CONTROL
//        //--------------------------------------------------------------------
//        final ImageButton m_btnPoiControl = new ImageButton(activity);
//        m_btnPoiControl.setImageResource(R.drawable.poi_status);
//        m_btnPoiControl.setBackgroundDrawable(null);
//        SubActionButton fbPoiControl = itemBuilder.setContentView(m_btnPoiControl).build();
//        m_btnPoiControl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getState() != GIEditingStatus.EDITING_POI && getState() != GIEditingStatus.EDITING_GEOMETRY) {
//                    GIEditLayersKeeper.Instance().CreatePOI();
//                } else {
//                    GIEditLayersKeeper.Instance().StopEditing();
//                }
//            }
//        });
//
//        //--------------------------------------------------------------------
//        // GPS buttons
//        //--------------------------------------------------------------------
//        FloatingActionMenu gpsActionMenu = new FloatingActionMenu.Builder(activity)
//
//                .addSubActionView(fbAutoFollow)
//                .addSubActionView(fbTrackControl)
//                .addSubActionView(fbShowTrack)
//                .addSubActionView(fbPoiControl)
//
//                .attachTo(gps_action_button)
//                .setRadius(ScreenUtils.dpToPx(144))
//                .setStartAngle(0)
//                .setEndAngle(90)
//                .build();
//        //--------------------------------------------------------------------
//        // GPS buttons
//        //--------------------------------------------------------------------
//
//        //--------------------------------------------------------------------
//        // Compass buttons
//        //--------------------------------------------------------------------
//        GICompassView fbCompass = new GICompassView(activity);
//        FloatingActionButton.LayoutParams compass_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
//        compass_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
//
//        FloatingActionButton compass_action_button = new FloatingActionButton.Builder(activity)
//                .setContentView(fbCompass)
//                .setBackgroundDrawable(null)
//                .setPosition(FloatingActionButton.POSITION_TOP_RIGHT)
//                .setLayoutParams(compass_menu_params)
//                .build();
//        //--------------------------------------------------------------------
//        // COMPASS_OPEN_BUTTON
//        //--------------------------------------------------------------------
//        final ImageButton btnProjectSelectorButton = new ImageButton(activity);
//        btnProjectSelectorButton.setImageResource(R.drawable.open);
//        btnProjectSelectorButton.setBackgroundDrawable(null);
//        SubActionButton fbOpen = itemBuilder.setContentView(btnProjectSelectorButton).build();
//        btnProjectSelectorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.ProjectSelectorDialogClicked();
//            }
//        });
//        //--------------------------------------------------------------------
//        // COMPASS_OPEN_Layers
//        //--------------------------------------------------------------------
//        final ImageButton btnLayers = new ImageButton(activity);
//        btnLayers.setImageResource(R.drawable.gear);
//        btnLayers.setBackgroundDrawable(null);
//        SubActionButton fbLayers = itemBuilder.setContentView(btnLayers).build();
//        btnLayers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.SettingsDialogClicked();
//
//            }
//        });
//        //--------------------------------------------------------------------
//        // COMPASS_EDIT_Layers
//        //--------------------------------------------------------------------
//        final ImageButton btnEditLayers = new ImageButton(activity);
//        btnEditLayers.setImageResource(R.drawable.edit);
//        btnEditLayers.setBackgroundDrawable(null);
//        SubActionButton fbEditLayers = itemBuilder.setContentView(btnEditLayers).build();
//        btnEditLayers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.EditableLayersDialogClicked();
//            }
//        });
//        //--------------------------------------------------------------------
//        // COMPASS_MARKERS
//        //--------------------------------------------------------------------
//        final ImageButton btnMarkers = new ImageButton(activity);
//        btnMarkers.setImageResource(R.drawable.poi);
//        btnMarkers.setBackgroundDrawable(null);
//        SubActionButton fbMarkers = itemBuilder.setContentView(btnMarkers).build();
//        btnMarkers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.MarkersDialogClicked();
//            }
//        });
//        //--------------------------------------------------------------------
//        // Compass buttons
//        //--------------------------------------------------------------------
//        FloatingActionMenu compassActionMenu = new FloatingActionMenu.Builder(activity)
//
//                .addSubActionView(fbOpen)
//                .addSubActionView(fbLayers)
//                .addSubActionView(fbEditLayers)
//                .addSubActionView(fbMarkers)
//
//                .attachTo(compass_action_button)
//                .setRadius(ScreenUtils.dpToPx(144))
//                .setStartAngle(90)
//                .setEndAngle(180)
//                .build();
//        //--------------------------------------------------------------------
//        // Compass buttons
//        //--------------------------------------------------------------------
//
//        //--------------------------------------------------------------------
//        // Edit buttons
//        //--------------------------------------------------------------------
//        btnEditCreate = new AppCompatCheckBox(activity);
//        btnEditCreate.setTextSize(0);
////		Drawable d = AppCompatDrawableManager.get().getDrawable(this, R.drawable.edit_create_bg);
//        btnEditCreate.setButtonDrawable(R.drawable.edit_create_bg);
//        btnEditCreate.setBackgroundDrawable(null);
//        fbEditCreate = itemBuilder.setContentView(btnEditCreate).build();
//
//
//        btnEditGeometry = new AppCompatCheckBox(activity);
//        btnEditGeometry.setTextSize(0);
//        btnEditGeometry.setButtonDrawable(R.drawable.edit_geometry_bg);
//        btnEditGeometry.setBackgroundDrawable(null);
//        fbEditGeometry = itemBuilder.setContentView(btnEditGeometry).build();
//
//
//        btnEditAttributes = new AppCompatCheckBox(activity);
//        btnEditAttributes.setTextSize(0);
//        btnEditAttributes.setButtonDrawable(R.drawable.edit_attributes_bg);
//        btnEditAttributes.setBackgroundDrawable(null);
//        fbEditAttributes = itemBuilder.setContentView(btnEditAttributes).build();
//
//        btnEditDelete = new AppCompatCheckBox(activity);
//        btnEditDelete.setTextSize(0);
//        btnEditDelete.setButtonDrawable(R.drawable.edit_delete_bg);
//        btnEditDelete.setBackgroundDrawable(null);
//        fbEditDelete = itemBuilder.setContentView(btnEditDelete).build();
//
//
//        btnEditCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((getState() != GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION) && (btnEditCreate.isChecked())) {
//                    if (!GIEditLayersKeeper.Instance().CreateNewObject()) {
//                        return;
//                    }
//                    setState(GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION);
//                    fbEditAttributes.setEnabled(false);
//                    fbEditGeometry.setEnabled(false);
//                    fbEditDelete.setEnabled(false);
//                    btnEditAttributes.setChecked(false);
//                    btnEditGeometry.setChecked(false);
//                    btnEditDelete.setChecked(false);
//                    m_map.UpdateMap();
//                } else {
//                    setState(GIEditingStatus.RUNNING);
//                    GIEditLayersKeeper.Instance().FillAttributes();
//                    fbEditCreate.setEnabled(false);
//                }
//            }
//        });
//        btnEditGeometry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (GIEditLayersKeeper.Instance().currentLayer == GIEditLayersKeeper.Instance().trackLayer) {
//                    return;
//                }
//
//                if ((getState() == GIEditingStatus.EDITING_GEOMETRY) || (getState() == GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING) || (getState() == GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION)) {
//                    setState(GIEditingStatus.RUNNING);
//                    GIEditLayersKeeper.Instance().StopEditingGeometry();
//                    fbEditCreate.setEnabled(true);
//                    fbEditAttributes.setEnabled(true);
//                    fbEditDelete.setEnabled(true);
//                    btnEditCreate.setChecked(false);
//                    btnEditAttributes.setChecked(false);
//                    btnEditDelete.setChecked(false);
//                } else {
//                    setState(GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING);
//                    fbEditCreate.setEnabled(false);
//                    fbEditAttributes.setEnabled(false);
//                    fbEditDelete.setEnabled(false);
//                }
//            }
//        });
//        btnEditAttributes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getState() != GIEditingStatus.WAITIN_FOR_SELECT_OBJECT) {
//                    setState(GIEditingStatus.WAITIN_FOR_SELECT_OBJECT);
//                    fbEditCreate.setEnabled(false);
//                    fbEditGeometry.setEnabled(false);
//                    fbEditDelete.setEnabled(false);
//                    btnEditCreate.setChecked(false);
//                    btnEditGeometry.setChecked(false);
//                    btnEditDelete.setChecked(false);
//                } else {
//                    setState(GIEditingStatus.RUNNING);
//                    fbEditCreate.setEnabled(true);
//                    fbEditGeometry.setEnabled(true);
//                    fbEditDelete.setEnabled(true);
//                }
//            }
//        });
//
//        btnEditDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setState(GIEditingStatus.WAITING_FOR_TO_DELETE);
//            }
//        });
//
//        fbEdit = new ImageButton(activity);
//        FloatingActionButton.LayoutParams edit_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
//        edit_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
//        fbEdit.setImageResource(R.drawable.edit);
//        fbEdit.setBackgroundDrawable(null);
//        fbEdit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (GIEditLayersKeeper.Instance().currentLayer == GIEditLayersKeeper.Instance().trackLayer) {
//                    fbEditGeometry.setVisibility(View.GONE);
//                    fbEditCreate.setVisibility(View.GONE);
//                } else {
//                    fbEditGeometry.setVisibility(View.VISIBLE);
//                    fbEditCreate.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        fbEditButton = new FloatingActionButton.Builder(activity)
//                .setContentView(fbEdit)
//                .setBackgroundDrawable(null)
//                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
//                .setLayoutParams(edit_menu_params)
//                .build();
//
//        fbEditButton.setVisibility(View.GONE);
//        fbEditButton.setActivated(false);
//
//        FloatingActionButton.LayoutParams edit_action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(84), ScreenUtils.dpToPx(84));
//        edit_action_params.gravity = Gravity.CENTER_HORIZONTAL;
//        itemBuilder.setLayoutParams(edit_action_params);
//
//        editActionMenu = new FloatingActionMenu.Builder(activity)
//
//                .addSubActionView(fbEditCreate)
//                .addSubActionView(fbEditGeometry)
//                .addSubActionView(fbEditAttributes)
//                .addSubActionView(fbEditDelete)
//                .attachTo(fbEditButton)
//                .setRadius(ScreenUtils.dpToPx(144))
//                .setStartAngle(180)
//                .setEndAngle(270)
//                .build();
//
//
//    }

//    public void showEditAttributesFragment(GI_WktGeometry geometry) {
//        EditAttributesDialog dialog = new EditAttributesDialog(activity, true, new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                btnEditCreate.setEnabled(true);
//                btnEditAttributes.setEnabled(true);
//                btnEditGeometry.setEnabled(true);
//                btnEditDelete.setEnabled(true);
//                m_map.UpdateMap();
//            }
//        }, geometry.attributes);
//        dialog.show();
//    }

    public void SetMeasureState(boolean rule, boolean square) {
        if (!IsRunning()) {
            m_IsRule = rule;
            m_IsSquare = square;
        } else {
            m_IsRule = false;
            m_IsSquare = false;
        }
    }

    public void setMap(GIMap map) {
        m_map = map;
    }

    public void onMapMove() {
    }

    public void onViewMove() {
    }

    public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {
    }

    public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {
    }

    public void onMarkerLayerRedraw(Rect view_rect) {
    }

    public void afterViewRedraw() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        x = event.getX();
        y = event.getY();
        float Distance;

        m_ScaleDetector.onTouchEvent(event);


        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                m_IsMultyClick = true;
                m_IsMoveClick = true;
                m_map.setToDraft(false);
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                m_OriginPointX = x;
                m_OriginPointY = y;
                m_IsMoveClick = false;
                m_IsMultyClick = false;
                m_IsLongClick = false;
                m_IsClick = true;
                m_map.setToDraft(false);

                previousX = event.getX();
                previousY = event.getY();
                active_id = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Distance = (float) Math.hypot(m_OriginPointX - x, m_OriginPointY - y);
                //Distance = (float)Math.cbrt(Math.pow(m_OriginPointX - x, 2) + Math.pow(m_OriginPointY - y, 2));
                //ToDo do NOT invalidate if m_Radius > Distance !
                if (mTouchSlop < Distance) {
                    m_IsMoveClick = true;
                } else // remembering
                {
                    return false;
                }

                final int pointerIndex = event.findPointerIndex(active_id);

                // Move view
                if (!m_scaled) {
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);
                    float scale = ((float) m_map.m_view_rect.width()) / ((float) m_map.m_view.width());

                    m_map.MoveViewBy((int) ((previousX - x) * scale), (int) ((previousY - y) * scale));
                    previousX = x;
                    previousY = y;
                } else // Scale view
                {
                    if (event.getPointerCount() == 2)
                        m_map.ScaleViewBy(m_focus, m_ScaleFactor);
                }

                m_map.invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (!m_IsMoveClick) {
                    if (m_IsRule) {
                        Point point = new Point((int) event.getX(), (int) event.getY());
                        GILonLat mark = m_map.ScreenToMap(point);
                        GIRuleToolControl RC = GIRuleToolControl.Instance(getContext(), m_map);
                        RC.AddPoint(mark);
                    }
                    if (m_IsSquare) {
                        Point point = new Point((int) event.getX(), (int) event.getY());
                        GILonLat mark = m_map.ScreenToMap(point);
                        GISquareToolControl SC = GISquareToolControl.Instance(getContext(), m_map);
                        SC.AddPoint(mark);
                    }
                    if (IsRunning()) {
                        Point point = new Point((int) event.getX(), (int) event.getY());
                        GILonLat mark = m_map.ScreenToMap(point);
                        GIBounds area = m_map.getrequestArea(point);
                        m_GotPosition = activity.getMap().ClickAt(mark, area);
                        return false;
                    }
                    return true;
                }

                if (m_scaled)
                    m_scaled = false;

                m_ScaleFactor = 1.0f;
                m_map.setToDraft(true);
                m_map.UpdateMap();


                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                active_id = INVALID_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // TODO: One finger up error?
                final int pointerIndex = (event.getAction() &
                        MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == active_id) {
                    final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                    previousX = event.getX(newPointerIndex);
                    previousY = event.getY(newPointerIndex);
                    active_id = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    public boolean onLongClick(View arg0) {
        if (!m_IsMultyClick && !m_IsMoveClick && !m_IsRule && !m_IsSquare && !IsRunning()) {
            m_IsClick = false;
            m_IsLongClick = true;
            GILonLat lonlat = m_map.ScreenToMap(new Point((int) x, (int) y));
            Point point = m_map.MapToScreen(lonlat);
            GIDataRequestorImp requestor = new GIDataRequestorImp(this.getContext(), new Point((int) x, (int) y), m_map.ps);
            m_map.RequestDataInPoint(new Point((int) x, (int) y), requestor);
            requestor.ShowDialog(this.getContext(), new Point(point.x, point.y), m_map);
        }
        return false;
    }

    //    public GIEditingStatus getState() {
//        return m_Status;
//    }
//
//    public void setState(GIEditingStatus status) {
//        m_Status = status;
//        if (IsRunning()) {
//            SetMeasureState(false, false);
//        }
//
//    }
//
    public boolean IsRunning() {
        return activity.IsRunning();
//        return !(m_Status == GIEditingStatus.STOPPED);
    }
//
//    public GITrackingStatus getTrackingStatus() {
//        return m_TrackingStatus;
//    }
//
//    public void setTrackingStatus(GITrackingStatus status) {
//        m_TrackingStatus = status;
//    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            m_ScaleFactor = detector.getScaleFactor();
            if (!m_scaled) {
                m_focus.x = (int) detector.getFocusX();
                m_focus.y = (int) detector.getFocusY();
                m_scaled = true;
            }
            return true;
        }
    }

}
