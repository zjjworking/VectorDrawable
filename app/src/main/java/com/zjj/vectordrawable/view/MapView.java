package com.zjj.vectordrawable.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.zjj.vectordrawable.R;
import com.zjj.vectordrawable.bean.ProviceItem;
import com.zjj.vectordrawable.tool.PathParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.animation.ValueAnimator.INFINITE;
import static android.animation.ValueAnimator.REVERSE;

/**
 * Created by zjj on 17/9/13.
 */

public class MapView extends View{
    private Context context;
    private List<ProviceItem> itemList = new ArrayList<>();
    private float scale= 1.3f;
    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1, 0xFFFFFFFF};
    private Paint paint;
    private ProviceItem selectItem;
    private GestureDetectorCompat gestureDetectorCompat;
    private int minWidth,minHeight;
    private ValueAnimator valueAnimator;
    private RectF totoalF;
    private int viewWidth;
        private float position;
    private boolean needChange;
    public MapView(Context context) {
        super(context,null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context){
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        //加载数据
        loadDataThread.start();
        gestureDetectorCompat = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                handlerTouch(e.getX(),e.getY());
                return true;
            }
        });
        minHeight = context.getResources().getDimensionPixelOffset(R.dimen.map_min_height);
        minWidth  =context.getResources().getDimensionPixelOffset(R.dimen.map_min_width);
    }
    private void handlerTouch(float x,float y){
        if(itemList == null){
            return;
        }
        ProviceItem tempItem = null;
        for(ProviceItem item : itemList){
            if(item.isTouch((int)(x/scale),(int)(y/scale))){
                tempItem = item;
                break;
            }
        }
        if(itemList != null){
            selectItem = tempItem;
            postInvalidate();
        }
    }

    Thread loadDataThread = new Thread(){
        @Override
        public void run() {
            List<ProviceItem> list = new ArrayList<>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.china);
            float left = -1;
            float right = -1;
            float top= -1;
            float bottom = -1;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//取得DocumentBuilderFactory实例
                DocumentBuilder builder = factory.newDocumentBuilder();//从factory获取DocumentBuilder实例
                Document doc = builder.parse(inputStream); //解析输入流 得到Document实例
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");

                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element)items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    Log.i("tuch","pathData"+ pathData);
                    Path path = PathParser.createPathFromPathData(pathData);
                    RectF rectF = new RectF();
                    path.computeBounds(rectF,true);
                    left = left == -1?rectF.left:(int)Math.min(rectF.left,left);
                    right = right == -1?rectF.right:(int)Math.min(rectF.right,right);
                    top = top == -1?rectF.top:(int)Math.min(rectF.top,top);
                    bottom = bottom == -1?rectF.bottom:(int)Math.min(rectF.bottom,bottom);
                    ProviceItem proviceItem = new ProviceItem(path);
                    list.add(proviceItem);
                 }

            }catch (Exception e){
                e.printStackTrace();
            }
            totoalF = new RectF(left,top,right,bottom);
            itemList = list;
            handler.sendEmptyMessage(1);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(itemList == null){
                return;
            }
            //屏幕适配缩放比例，大小
            scale = viewWidth/totoalF.width();
            int totalNumber = itemList.size();
            for (int i = 0; i < totalNumber; i++) {
                int color = Color.WHITE;
                int flag = i % 4;
                switch (flag){
                    case 1:
                        color = colorArray[0];
                        break;
                    case 2:
                        color = colorArray[1];
                        break;
                    case 3:
                        color = colorArray[2];
                        break;
                    default:
                        color = Color.WHITE;
                        break;
                }
                itemList.get(i).setDrawColor(color);
            }
            postInvalidate();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(itemList == null){
            return;
        }
        canvas.save();
        canvas.scale(scale,scale);
        for(ProviceItem item: itemList){
            if(item != null){
                item.drawItem(canvas,paint,false);
            }
        }
        if(selectItem != null){
            selectItem.drawItem(canvas,paint,true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gestureDetectorCompat.onTouchEvent(event);
    }
// 尺寸自适应
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width  = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        viewWidth = width;
        int viewHeight = height;

        switch (widthMode){
            case MeasureSpec.EXACTLY:
                viewWidth = width > minWidth ? width : minWidth;
                break;
            case MeasureSpec.AT_MOST://如果是mach_parent
            case MeasureSpec.UNSPECIFIED:
                viewWidth = minWidth;
                break;

        }
        int computHeight = (minHeight*viewWidth/minWidth);
        switch (heightMode){
            case MeasureSpec.EXACTLY:
                viewHeight  = height ;
                break;
            case MeasureSpec.AT_MOST://如果是mach_parent
            case MeasureSpec.UNSPECIFIED:
                viewHeight = minHeight > computHeight ? minHeight : computHeight;
                break;

        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(viewWidth,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight,MeasureSpec.EXACTLY));
    }
}
