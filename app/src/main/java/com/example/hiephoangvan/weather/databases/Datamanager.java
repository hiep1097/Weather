package com.example.hiephoangvan.weather.databases;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class Datamanager {
    private PlaceDatabase db;
    private static Datamanager instance;
    public Datamanager() {
        db = PlaceDatabase.getInstance();
    }

    public static Datamanager getInstance() {
        if (instance == null) {
            instance = new Datamanager();
        }
        return instance;
    }

    public Flowable<List<Places>> getAllPlaces(){
        return db
                .placeDAO()
                .getAllPlaces();
    }

    public void addPlace(Places place) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.placeDAO().addPlace(place);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void deletePlace(Places place) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.placeDAO().deletePlace(place);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void updatePlace(Places place) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.placeDAO().updatePlace(place);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
