package com.zjj.vectordrawable.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;

/**
 * Created by zjj on 17/10/8.
 * 解决嵌套滑动冲突 修改RecyclerView内部onInterceptTouchEvent方法ACTION_MOVE时滑动条件
 * 子RecyclerView在滑动中 不会被父RecyclerView打断  父RecyclerView修改requestDisallowInterceptTouchEvent方法
 * 添加头部 模仿ListView添加foot和header的机制 包装一层
 */

public class BetterRecyclerView extends RecyclerView {
    //添加头部尾部
    private ArrayList<View> mHeaderViewInfos = new ArrayList<>();
    private ArrayList<View> mFooterViewInfos = new ArrayList<>();
    private Adapter mAdapter;

    private int touchSlop;
    private Context mContext;
    private int INVALID_POINTER = -1;
    private int scrollPointerId = INVALID_POINTER;
    private int initialTouchX;
    private int initialTouchY;
    private final static String TAG = "BetterRecyclerView";

    public BetterRecyclerView(Context context) {
        //super(context);
        this(context,null);
    }

    public BetterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
    }

    public BetterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledEdgeSlop();
        mContext = context;
    }

    @Override
    public void setScrollingTouchSlop(int slopConstant) {
        super.setScrollingTouchSlop(slopConstant);
        ViewConfiguration vc = ViewConfiguration.get(mContext);
        switch (slopConstant){
            case TOUCH_SLOP_DEFAULT:
                touchSlop = vc.getScaledEdgeSlop();
                break;
            case TOUCH_SLOP_PAGING:
                touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(vc);
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e == null) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(e);
        int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN :
                scrollPointerId = MotionEventCompat.getPointerId(e, 0);
                initialTouchX = Math.round(e.getX() + 0.5f);
                initialTouchY = Math.round(e.getY() + 0.5f);
                return super.onInterceptTouchEvent(e);
            case MotionEvent.ACTION_POINTER_DOWN:
                scrollPointerId = MotionEventCompat.getPointerId(e, actionIndex);
                initialTouchX = Math.round(MotionEventCompat.getX(e, actionIndex) + 0.5f);
                initialTouchY = Math.round(MotionEventCompat.getY(e, actionIndex) + 0.5f);
                return super.onInterceptTouchEvent(e);
            case MotionEvent.ACTION_MOVE:
                int index = MotionEventCompat.findPointerIndex(e, scrollPointerId);
                if (index < 0) {
                    return false;
                }
                int x = Math.round(MotionEventCompat.getX(e, index) + 0.5f);
                int y = Math.round(MotionEventCompat.getY(e, index) + 0.5f);
                if (getScrollState() != SCROLL_STATE_DRAGGING ) {
                    int dx = x - initialTouchX;
                    int dy = y - initialTouchY;
                    boolean startScroll = false;
                    //将斜率添加进来，这样可以减少 startScroll 为true 的机会。这个机会就会给需要这个返回值
                    if (getLayoutManager().canScrollHorizontally() && Math.abs(dx) > touchSlop &&
                            (getLayoutManager().canScrollVertically() || Math.abs(dx) > Math.abs(dy))) {
                        startScroll = true;
                    }
                    if(getLayoutManager().canScrollVertically() && Math.abs(dy) > touchSlop &&
                            (getLayoutManager().canScrollHorizontally() || Math.abs(dy) > Math.abs(dx))) {
                        startScroll = true;
                    }
                    Log.d(TAG, "startScroll: " + startScroll);
                    return startScroll && super.onInterceptTouchEvent(e);
                }
                return super.onInterceptTouchEvent(e);
            default:
                return super.onInterceptTouchEvent(e);
        }
    }

    /**
     * 这个方法的作用是允许父类打断这个onTouche 事件
     * 设置一个空函数 达到相反的效果  达到不允许父类打断
     * @param disallowIntercept
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }
    //添加头部
    public void addHeaderView(View v) {
        mHeaderViewInfos.add(v);

        if (mAdapter != null) {
            if (mAdapter instanceof HeaderViewRecyclerAdapter) {
                mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, mAdapter);
            }
        }
    }

    public void addFooterView(View v) {
        mFooterViewInfos.add(v);

        if (mAdapter != null) {
            if (mAdapter instanceof HeaderViewRecyclerAdapter) {
                mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, mAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
            mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
        } else {
            mAdapter = adapter;
        }
        //这里需要使用mAapter，不然头部和尾部就添加不进去了
        super.setAdapter(mAdapter);
    }
}
