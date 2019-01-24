package ru.tcgeo.application.views.control;


import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;

import io.reactivex.subjects.PublishSubject;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.data.GIMap;
import ru.tcgeo.application.data.gilib.models.GIBounds;
import ru.tcgeo.application.data.gilib.models.GILonLat;
import ru.tcgeo.application.data.gilib.requestor.GIDataRequestorImp;


public class GITouchControl extends View implements GIControl, OnLongClickListener/*, ITouchControl */ {
    private static final int INVALID_ID = -1;

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

    Geoinfo activity;
    private ScaleGestureDetector m_ScaleDetector;
    private GIMap m_map;
    private int active_id = INVALID_ID;
    private float previousX;
    private float previousY;
    private Point m_focus;
    private float m_ScaleFactor;
    private boolean m_scaled;

    private PublishSubject<Integer> followSubject = PublishSubject.create();


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
        ViewConfiguration vc = ViewConfiguration.get(activity);
        mTouchSlop = 2 * vc.getScaledTouchSlop();


    }


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
            m_map.RequestDataInPoint(new Point((int) x, (int) y), 2 * mTouchSlop, requestor);
            requestor.ShowDialog(this.getContext(), new Point(point.x, point.y), m_map);
        }
        return false;
    }

    public boolean IsRunning() {
        return activity.IsRunning();
    }

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
