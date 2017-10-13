package com.zjj.vectordrawable.bean;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.animation.LinearInterpolator;

/**
 * Created by zjj on 17/9/13.
 */

public class ProviceItem {
    /**
     * 绘制路径
     */
    protected Path path;

    protected PathMeasure pathMeasure;
    /**
     * 绘制颜色
     */
    private int drawColor;



    public ProviceItem(Path path){
        this.path = path;
        pathMeasure = new PathMeasure(path,false);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    public void drawItem(Canvas canvas, Paint paint, boolean isSelect){
        if(isSelect){
            //绘制背景
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8,0,0,0xffffff);
            canvas.drawPath(path,paint);

            //绘制省份
            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path,paint);


        }else {
            //没有选中  背景
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(path,paint);
            //绘制边界线
            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = 0xFFD0E8F4;
            paint.setColor(strokeColor);
            canvas.drawPath(path,paint);
        }
    }

    /**
     * 判断点击坐标 是否在范围之内
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x,int y){
        //构造一个区域对象
        RectF rectF = new RectF();
        //不规则区域放到rectf中了
        path.computeBounds(rectF,true);
        Region region = new Region();
        region.setPath(path,new Region((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom));
        return  region.contains(x,y);
    }

}
