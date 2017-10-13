package com.zjj.vectordrawable;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjj.vectordrawable.view.MyAdapter;
import com.zjj.vectordrawable.view.TableView;
import com.zjj.vectordrawable.view.TestViewGroup;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    TableView tableView;
    Handler handler=new Handler();

    // Used to load the 'native-lib' library on application startup.
    private TestViewGroup testViewGroup;
    static {
        //开启TextView使用矢量图权限
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableView= (TableView) findViewById(R.id.table);
        final MyAdapter adapter = new MyAdapter(MainActivity.this);
        tableView.setAdapter(adapter);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tableView.setAdapter(adapter);
            }
        },1500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setRowCount(10000);
                Log.i("tuch","加载10000");
                tableView.setAdapter(adapter);
            }
        },8000);
//        testViewGroup  =(TestViewGroup)findViewById(R.id.testViewGroup);
//        testViewGroup.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.i(TAG,"MainActivity +++ onTouch");
//                return false;
//            }
//        });
//        testViewGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG,"MainActivity +++ onClick");
//            }
//        });
    }

    public native String stringFromJNI();

//    public void anim(View view) {
//        ImageView imageView = (ImageView)view;
//        Drawable drawable = imageView.getDrawable();
//        ((Animatable) drawable).start();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG,"MainActivity +++ onTouchEvent");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG,"MainActivity +++ dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

}
