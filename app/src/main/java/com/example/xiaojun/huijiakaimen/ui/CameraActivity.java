package com.example.xiaojun.huijiakaimen.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.xiaojun.huijiakaimen.R;
import com.example.xiaojun.huijiakaimen.view.CameraBack;
import com.example.xiaojun.huijiakaimen.view.CameraSurfaceView;
import com.example.xiaojun.huijiakaimen.view.CanvesView;

public class CameraActivity extends Activity implements CameraBack{
    private CameraSurfaceView cameraSurfaceView;
    private CanvesView canvesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraSurfaceView=findViewById(R.id.camera);
        cameraSurfaceView.setCallBack(this);
        canvesView=findViewById(R.id.ffff);

    }

    @Override
    public void Back(String path) {
        Log.d("ffffff", path+"dddddddddddddddddd");
        cameraSurfaceView.setStartPZ();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cameraSurfaceView.stopView();
        finish();
    }
}
