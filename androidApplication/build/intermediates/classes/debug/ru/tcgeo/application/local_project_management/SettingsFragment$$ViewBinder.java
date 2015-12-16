// Generated code from Butter Knife. Do not modify!
package ru.tcgeo.application.local_project_management;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SettingsFragment$$ViewBinder<T extends ru.tcgeo.application.local_project_management.SettingsFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361825, "field 'mName'");
    target.mName = finder.castView(view, 2131361825, "field 'mName'");
    view = finder.findRequiredView(source, 2131361827, "field 'mType'");
    target.mType = finder.castView(view, 2131361827, "field 'mType'");
    view = finder.findRequiredView(source, 2131361832, "field 'mLocationType'");
    target.mLocationType = finder.castView(view, 2131361832, "field 'mLocationType'");
    view = finder.findRequiredView(source, 2131361833, "field 'mLocation'");
    target.mLocation = finder.castView(view, 2131361833, "field 'mLocation'");
    view = finder.findRequiredView(source, 2131361835, "field 'mZoomType'");
    target.mZoomType = finder.castView(view, 2131361835, "field 'mZoomType'");
    view = finder.findRequiredView(source, 2131361836, "field 'mZoomMin'");
    target.mZoomMin = finder.castView(view, 2131361836, "field 'mZoomMin'");
    view = finder.findRequiredView(source, 2131361837, "field 'mZoomMax'");
    target.mZoomMax = finder.castView(view, 2131361837, "field 'mZoomMax'");
    view = finder.findRequiredView(source, 2131361830, "method 'apply'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.apply();
        }
      });
    view = finder.findRequiredView(source, 2131361829, "method 'onDown'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onDown(p0);
        }
      });
    view = finder.findRequiredView(source, 2131361828, "method 'onUp'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onUp(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.mName = null;
    target.mType = null;
    target.mLocationType = null;
    target.mLocation = null;
    target.mZoomType = null;
    target.mZoomMin = null;
    target.mZoomMax = null;
  }
}
