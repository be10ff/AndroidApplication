package ru.tcgeo.application.views.viewholder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class LayerHolder extends RecyclerView.ViewHolder {

    // common layer settings
    @BindView(R.id.etLayerName)
    public android.support.v7.widget.AppCompatEditText etLayerName;

    @BindView(R.id.cbLayerVisibility)
    public CheckBox cbLayerVisibility;

    @BindView(R.id.llLayerSettings)
    public LinearLayout llLayerSettings;

    @BindView(R.id.flMore)
    public View flMore;

    @BindView(R.id.tvFilePath)
    public TextView tvFilePath;

    @BindView(R.id.ivFileExsist)
    public ImageView ivFileExsist;

    @BindView(R.id.tvScaleRange)
    public TextView tvScaleRange;

    @BindView(R.id.rsbScaleRange)
    public RangeSeekBar rsbScaleRange;

    @BindView(R.id.flReOrder)
    public View flReOrder;


    @BindView(R.id.ivReOrder)
    public View ivReOrder;

    @BindView(R.id.flRemove)
    public View flRemove;

    @BindView(R.id.ivRemove)
    public View ivRemove;

    @BindView(R.id.flMarkers)
    public View flMarkers;

    @BindView(R.id.ivMarkersSource)
    public android.support.v7.widget.AppCompatImageView ivMarkersSource;

    protected LayerHolderCallback callback;

    ValueAnimator mAnimator;

    TextWatcher etLayerNameTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            callback.onLayerName(LayerHolder.this);
        }
    };

    private boolean expanded;

    public LayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;

        llLayerSettings.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        llLayerSettings.getViewTreeObserver().removeOnPreDrawListener(this);
                        llLayerSettings.setVisibility(View.GONE);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        llLayerSettings.measure(widthSpec, heightSpec);
                        mAnimator = slideAnimator(0, llLayerSettings.getMeasuredHeight());
                        mAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                llLayerSettings.setVisibility(View.VISIBLE);
                                expanded = true;
                                flMore.setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        flMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flMore.setEnabled(false);
                                expanded = !expanded;
                                if (!expanded) {
                                    collapse();
                                } else {
                                    expand();
                                }
                            }
                        });

                        return true;
                    }
                });
        initListeners();
    }

//    @OnClick(R.id.flRemove)
//    public void onRemove() {
//        callback.onRemove(this);
//    }

    public void removeListeners() {
        cbLayerVisibility.setOnCheckedChangeListener(null);
        etLayerName.removeTextChangedListener(etLayerNameTextChangedListener);
        rsbScaleRange.setOnRangeSeekBarChangeListener(null);
    }

    public void initListeners() {
        cbLayerVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback.onVisibilityCheckChanged(LayerHolder.this, isChecked);
            }
        });

        rsbScaleRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

                tvScaleRange.setText(tvScaleRange.getContext().getString(R.string.scale_range_format, MapUtils.z2scale((Integer) minValue), MapUtils.z2scale((Integer)maxValue)));
                callback.onScaleRange(LayerHolder.this);
            }
        });

        etLayerName.addTextChangedListener(etLayerNameTextChangedListener);
    }

    private void expand() {
        llLayerSettings.setVisibility(View.VISIBLE);
        mAnimator.start();
    }

    private void collapse() {
        int finalHeight = llLayerSettings.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                llLayerSettings.setVisibility(View.GONE);
                flMore.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = llLayerSettings.getLayoutParams();
                layoutParams.height = value;
                llLayerSettings.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
