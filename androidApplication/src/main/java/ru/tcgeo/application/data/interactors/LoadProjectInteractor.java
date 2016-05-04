package ru.tcgeo.application.data.interactors;

import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.view.MapView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abelov on 28.04.16.
 */
public class LoadProjectInteractor {
    private MapView view;

    public void setView(MapView view) {
        this.view = view;
    }

    public void loadProject(final String path){

        Observable.create(new Observable.OnSubscribe<GIProjectProperties>() {
            @Override
            public void call(Subscriber<? super GIProjectProperties> subscriber) {
                subscriber.onNext(new GIProjectProperties(path));
                subscriber.onCompleted();
                }
            })
//                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GIProjectProperties>() {
            @Override
            public void onNext(GIProjectProperties s) { view.onMapLoaded(s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                view.onError();
            }
        });

    }
}
