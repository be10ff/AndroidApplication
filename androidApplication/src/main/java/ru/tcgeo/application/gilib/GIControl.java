package ru.tcgeo.application.gilib;

import android.graphics.Rect;

import ru.tcgeo.application.gilib.models.GIBounds;


public interface GIControl
{
	GIMap Map();
	/**
	 *  устанавливает связь с картой
	 * @param map
	 */
	void setMap(GIMap map);
	/**
	 * onMapMove - вызывается при изменении последней зафиксированной области карты
	 */
	void onMapMove();
	/**
	 * вызывается при изменении отображаемой области карты
	 */
	void onViewMove();
	/**
	 * вызывается по окончании полного рендеринга карты
	 * @param bounds
	 * @param view_rect
	 */
	void afterMapFullRedraw(GIBounds bounds, Rect view_rect);
	/**
	 * вызывается по окончании рендеринга графических элементов карты, но перед рендерингом текста.
	 * @param bounds
	 * @param view_rect
	 */
	void afterMapImageRedraw(GIBounds bounds, Rect view_rect);
	/**
	 * вызывается всякий раз при отрисовке карты на экран
	 * @param view_rect
	 */
	void onMarkerLayerRedraw(Rect view_rect);
	/**
	 * вызывается всякий раз при отрисовке карты на экран
	 */
	void afterViewRedraw();
}
