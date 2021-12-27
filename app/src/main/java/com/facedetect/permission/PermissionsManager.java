package com.facedetect.permission;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Consumer;

/**
 * @author fan.jiang
 */
public class PermissionsManager {

    /**
     * 请求权限
     * @param act
     */
    @SuppressLint("CheckResult")
    public static void requestPermission(AppCompatActivity act, String args,
                                         final IManager.IPermission iPermission) {

        RxPermissions rx = new RxPermissions(act);
        rx.request(args).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    if (null != iPermission) {
                        iPermission.accede();
                    }
                } else {
                    if (null != iPermission) {
                        iPermission.reject();
                    }
                }
            }
        });
    }

    /**
     * 请求多权限
     * @param act
     */
    @SuppressLint("CheckResult")
    public static void requestPermissions(AppCompatActivity act, String[] args,
                                          final IManager.IPermission iPermission) {
        RxPermissions rx = new RxPermissions(act);
        rx.request(args).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    if (null != iPermission) {
                        iPermission.accede();
                    }
                } else {
                    if (null != iPermission) {
                        iPermission.reject();
                    }
                }
            }
        });
    }
}