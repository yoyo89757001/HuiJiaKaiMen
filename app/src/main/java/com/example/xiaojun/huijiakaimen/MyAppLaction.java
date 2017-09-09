package com.example.xiaojun.huijiakaimen;

import android.app.Application;
import android.util.Log;
import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.gson.reflect.TypeToken;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;


/**
 * Created by Administrator on 2017/7/5.
 */

public class MyAppLaction extends Application {
    private File mCascadeFile;
    public static FaceDet mFaceDet;
    public static String sip=null;



    @Override
    public void onCreate() {
        super.onCreate();

        try {

           mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());

            Reservoir.init(this, 900*1024); //in bytes 1M

        } catch (IOException e) {
            Log.d("gggg", e.getMessage());

        }



        Type resultType = new TypeToken<String>() {
        }.getType();
        Reservoir.getAsync("ipipip", resultType, new ReservoirGetCallback<String>() {
            @Override
            public void onSuccess(final String i) {
                sip=i;

            }

            @Override
            public void onFailure(Exception e) {
                Log.d("MyAppLaction", e.getMessage()+"获取摄像头异常");

            }

        });


//        Type resultType3 = new TypeToken<JiuDianBean>() {
//        }.getType();
//        Reservoir.getAsync("jiudian", resultType3, new ReservoirGetCallback<JiuDianBean>() {
//            @Override
//            public void onSuccess(final JiuDianBean i) {
//                jiuDianBean=i;
//              //  Log.d("MyAppLaction", "jiuDianBean:" + jiuDianBean);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.d("MyAppLaction", e.getMessage()+"ddd");
//
//            }
//
//        });




    }



}
