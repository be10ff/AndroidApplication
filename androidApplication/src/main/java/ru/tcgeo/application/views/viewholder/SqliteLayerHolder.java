package ru.tcgeo.application.views.viewholder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class SqliteLayerHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvLayerName)
    public TextView tvLayerName;

    @Bind(R.id.cbLayerVisibility)
    public CheckBox cbLayerVisibility;

    @Bind(R.id.llLayer)
    public LinearLayout llLayer;

    @Bind(R.id.llLayerSettings)
    public LinearLayout llLayerSettings;

    @Bind(R.id.cbLayerDetails)
    public CheckBox cbLayerDetails;
    ValueAnimator mAnimator;
    private LayerHolderCallback callback;


    public SqliteLayerHolder(View itemView, LayerHolderCallback callback) {
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
                                cbLayerDetails.setChecked(true);
                                cbLayerDetails.setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        cbLayerDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                cbLayerDetails.setEnabled(false);
                                if (!isChecked) {
                                    collapse();
                                } else {
                                    expand();
                                }
                            }
                        });

                        return true;
                    }
                });
    }


    @OnCheckedChanged(R.id.cbLayerVisibility)
    public void onLayerVisibilityCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        callback.onVisibilityCheckChanged(this, isChecked);
    }

    @OnClick(R.id.tvLayerName)
    public void onSettings() {
        callback.onSettings(this);
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
                cbLayerDetails.setEnabled(true);
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
