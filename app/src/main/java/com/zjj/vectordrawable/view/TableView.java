package com.zjj.vectordrawable.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;

import com.zjj.vectordrawable.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjj on 17/9/19.
 */

public class TableView extends ViewGroup {
    private static final String TAG = "TableView" ;
    private BaseTableAdapter adapter;

    private int currentX;
    private int currentY;

    private int scrollX;
    private int scrollY;

    //第一行
    private int firstRow;
    //第一列
    private int firstColumn;
    private int[] widths;
    private int[] heights;

    @SuppressWarnings("unused")
    private View headView;
    private List<View> rowViewList;
    private List<View> columnViewList;
    private List<List<View>> bodyViewTable;
    private int rowCount;
    private int columnCount;
    private int width;
    private int height;

    private  int minimumVelocity;

    private  int maximumVelocity;
    //需要重绘标志位
    private boolean needRelayout;
    private VelocityTracker velocityTracker;
    //滑动最小距离
    private int touchSlop;

    private Recycler recycler;
    private BaseTableAdapterDataSetObserver BaseTableAdapterDataSetObserver;

    private final Flinger flinger;

    public TableView(Context context) {
        this(context,null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.touchSlop = configuration.getScaledTouchSlop();
        needRelayout = true;
        this.headView = null;
        this.flinger = new Flinger(context);
        this.rowViewList = new ArrayList<>();
        this.columnViewList = new ArrayList<>();
        this.bodyViewTable = new ArrayList<>();
        this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.setWillNotDraw(false);
    }


    public BaseTableAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseTableAdapter baseTableAdapter){
        this.adapter = baseTableAdapter;
        BaseTableAdapterDataSetObserver = new BaseTableAdapterDataSetObserver();
        this.recycler = new Recycler(baseTableAdapter.getViewTypeCount());
        scrollX =0;
        scrollY = 0;
        firstColumn = 0;
        firstRow = 0;
        needRelayout = true;
        requestLayout();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                currentX = (int)ev.getRawX();
                currentY = (int)ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x2 = Math.abs(currentX - (int)ev.getRawX());
                int y2 = Math.abs(currentY - (int)ev.getRawY());
                if((x2 > touchSlop && x2 > y2 ) || y2 > touchSlop && y2 > x2){
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if(!flinger.isFinished()){
                    flinger.forceFinished();
                }
                currentX = (int)event.getRawX();
                currentY = (int)event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                int x2 = (int)event.getRawX();
                int y2 = (int)event.getRawY();

                int diffX = currentX -x2;
                int diffY = currentY - y2;
                scrollBy(diffX,diffY);
                break;
            }
            case MotionEvent.ACTION_UP:{
                final VelocityTracker velocityTracker = this.velocityTracker;
                velocityTracker.computeCurrentVelocity(1000,maximumVelocity);
                int velocityX = (int)velocityTracker.getXVelocity();
                int velocityY = (int)velocityTracker.getYVelocity();

                if(Math.abs(velocityX) > minimumVelocity || Math.abs(velocityY)>
                        minimumVelocity){
                    flinger.start(getActualScrollX(),getActualScrollY(),velocityX,velocityY,
                            getMaxScrollX(),getMaxScrollY());
                }else {
                    if(this.velocityTracker != null){
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                break;
            }
        }
        return true;
    }
    //事件  move
    @Override
    public void scrollBy(@Px int x, @Px int y) {
        scrollX += x;
        scrollY += y;
        //修整ScrollX  ScrollY
        scrollBounds();
        //    int diffX=currentX-x2;
        if(scrollX==0){
            //如果 等于 什么都不做
        } else if (scrollX  > 0) {
            //手指 往左滑
            Log.i(TAG,"<---------------");
            //scrollX>列宽    整列 item滑出去
            Log.i(TAG,"  scrollX  "+scrollX+"   firstColumn  "+firstColumn+"  widths[firstColumn+1]   "
                    +widths[firstColumn+1]+"  size  "+rowViewList.size());
            while (scrollX >widths[firstColumn+1]) {
                if (!rowViewList.isEmpty()) {
                    removeLeft();
                }
                //复位ScrollX
                scrollX -= widths[firstColumn + 1];
                //现在的第一列 被更新
                firstColumn++;
            }

            //找到右边添加一列   的临界条件
            while (getFilledWidth() < width) {
                addRight();
            }
        }else {
            //scrillX<0
            //手指往右滑
            //移除右边的Item的临界值

            while (!rowViewList.isEmpty()&&getFilledWidth() - widths[firstColumn + rowViewList.size()] >= width) {
                removeRight();
            }
            //添加左边的临界值
            while (scrollX < 0) {

                addLeft();
                //更新firstColumn
                firstColumn--;
                scrollX+=widths[firstColumn+1];
                Log.i(TAG,"减一-------- scrollX  "+scrollX+"  firstColumn  "+firstColumn);
            }
        }

        if (scrollY == 0) {
            // no op
        } else if (scrollY > 0) {
            while (heights[firstRow + 1] < scrollY) {
                if (!columnViewList.isEmpty()) {
                    removeTop();
                }
                scrollY -= heights[firstRow + 1];
                firstRow++;
            }
            while (getFilledHeight() < height){
                addBottom();
            }
        }else {
            while (!columnViewList.isEmpty() && getFilledHeight() - heights[firstRow + columnViewList.size()] >= height){
                removeBottom();
            }
            while (0> scrollY){
                addTop();
                firstRow--;
                scrollY += heights[firstRow + 1];
            }
        }
        repositionViews();

    }
    private int getFilledHeight() {
        return heights[0] + sumArray(heights, firstRow + 1, columnViewList.size()) - scrollY;
    }
    private void addTop() {
        addTopAndBottom(firstRow - 1, 0);
    }

    private void addBottom() {
        final int size = columnViewList.size();
        addTopAndBottom(firstRow + size, size);
    }
    //移除最后一列
    private void removeRight() {
        removeLeftOrRight(rowViewList.size()-1);
    }

    private void addLeft() {
        addLeftOrRight(firstColumn-1,0);
    }

    private void addRight() {
        int size=rowViewList.size();
        addLeftOrRight(firstColumn+size,size);
    }

    private void addLeftOrRight(int column, int index) {
        //添加首行  右边的View
        View view=obtainView(-1,column,widths[column+1],heights[0]);
        //更新 rowViewList
        rowViewList.add(index,view);
        int i=firstRow;

        for (List<View> list : bodyViewTable) {
            view = obtainView(i, column, widths[column + 1], heights[i + 1]);
            list.add(index, view);
            i++;
        }
        Log.d(TAG, " bodyViewTable    "+ bodyViewTable.size());
    }

    private int getFilledWidth() {
        //往右滑时scrollX《0    -scrollX 为正
        return widths[0]+sumArray(widths,firstColumn+1,rowViewList.size())-scrollX;
    }

    private void removeLeft() {
        removeLeftOrRight(0);
    }

    private void removeLeftOrRight(int i) {
        //移除 View
        removeView(rowViewList.remove(i));

        //移除
        for (List<View> list : bodyViewTable) {
            removeView(list.remove(i));
        }
        Log.d(TAG, " bodyViewTable    "+ bodyViewTable.size());
    }

    /*
         * The expected value is: percentageOfViewScrolled * computeHorizontalScrollRange()
         */
    @Override
    protected int computeHorizontalScrollExtent() {
        final float tableSize = width - widths[0];
        final float contentSize = sumArray(widths) - widths[0];
        final float percentageOfVisibleView = tableSize / contentSize;

        return Math.round(percentageOfVisibleView * tableSize);
    }

    /*
     * The expected value is between 0 and computeHorizontalScrollRange() - computeHorizontalScrollExtent()
     */
    @Override
    protected int computeHorizontalScrollOffset() {
        final float maxScrollX = sumArray(widths) - width;
        final float percentageOfViewScrolled = getActualScrollX() / maxScrollX;
        final int maxHorizontalScrollOffset = width - widths[0] - computeHorizontalScrollExtent();

        return widths[0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
    }

    /*
     * The base measure
     */
    @Override
    protected int computeHorizontalScrollRange() {
        return width;
    }

    /*
     * The expected value is: percentageOfViewScrolled * computeVerticalScrollRange()
     */
    @Override
    protected int computeVerticalScrollExtent() {
        final float tableSize = height - heights[0];
        final float contentSize = sumArray(heights) - heights[0];
        final float percentageOfVisibleView = tableSize / contentSize;

        return Math.round(percentageOfVisibleView * tableSize);
    }

    /*
     * The expected value is between 0 and computeVerticalScrollRange() - computeVerticalScrollExtent()
     */
    @Override
    protected int computeVerticalScrollOffset() {
        final float maxScrollY = sumArray(heights) - height;
        final float percentageOfViewScrolled = getActualScrollY() / maxScrollY;
        final int maxHorizontalScrollOffset = height - heights[0] - computeVerticalScrollExtent();

        return heights[0] + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
    }

    /*
     * The base measure
     */
    @Override
    protected int computeVerticalScrollRange() {
        return height;
    }

    public int getActualScrollX() {
        return scrollX + sumArray(widths, 1, firstColumn);
    }

    public int getActualScrollY() {
        return scrollY + sumArray(heights, 1, firstRow);
    }

    private int getMaxScrollX() {
        return Math.max(0, sumArray(widths) - width);
    }

    private int getMaxScrollY() {
        return Math.max(0, sumArray(heights) - height);
    }



    private void addTopAndBottom(int row, int index) {
        View view = obtainView(row, -1, widths[0], heights[row + 1]);
        columnViewList.add(index, view);

        List<View> list = new ArrayList<View>();
        final int size = rowViewList.size() + firstColumn;
        for (int i = firstColumn; i < size; i++) {
            view = obtainView(row, i, widths[i + 1], heights[row + 1]);
            list.add(view);
        }
        bodyViewTable.add(index, list);
    }

    private void removeTop() {
        removeTopOrBottom(0);
    }


    private void removeBottom() {
        removeTopOrBottom(columnViewList.size() - 1);
    }



    private void removeTopOrBottom(int position) {
        removeView(columnViewList.remove(position));
        List<View> remove = bodyViewTable.remove(position);
        for (View view : remove) {
            removeView(view);
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        final int typeView = (Integer) view.getTag(R.id.tag_type_view);
        //添加到回收池
        recycler.addRecycledView(view, typeView);
    }
    private void repositionViews(){
        int left,top,right,bottom,i;
        left = widths[0] - scrollX;
        i = firstColumn;
        for (View view: rowViewList){
            right = left + widths[++i];
            view.layout(left,0,right,heights[0]);
            left = right;
        }
        top = heights[0] - scrollY;
        i = firstRow;
        for (View view : columnViewList) {
            bottom = top + heights[++i];
            view.layout(0, top, widths[0], bottom);
            top = bottom;
        }

        top = heights[0] - scrollY;
        i = firstRow;
        for (List<View> list : bodyViewTable) {
            bottom = top + heights[++i];
            left = widths[0] - scrollX;
            int j = firstColumn;
            for (View view : list) {
                right = left + widths[++j];
                view.layout(left, top, right, bottom);
                left = right;
            }
            top = bottom;
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // int 32为  最高的2位代表模式  后30位代表距离
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int w;
        final int h;
        if(adapter != null){
            this.rowCount = adapter.getRowCount();
            this.columnCount = adapter.getColmunCount();
            widths = new int[columnCount+1];
            for (int i = -1 ; i < columnCount; i++) {
                //数组每个元素 存放着  控件宽度
                widths[i+1] += adapter.getWidth(i);
            }
            heights = new int[rowCount+1];
            for (int i = -1; i < rowCount; i++) {
                //数组每个元素 存放着  控件宽度
                heights[i+1] += adapter.getHeight(i);
            }
            if(widthMode == MeasureSpec.AT_MOST){
                w = Math.min(widthSize,sumArray(widths));
            }else if(widthMode == MeasureSpec.UNSPECIFIED){
                w = sumArray(widths);
            }else {
                w = widthSize;
                int sumArray = sumArray(widths);
                if(sumArray < widthSize){
                    final float factor = widthSize / (float)sumArray;
                    for (int i = 1; i < widths.length; i++) {
                        widths[i] = Math.round(widths[i] * factor);
                    }
                    widths[0] = widthSize - sumArray(widths,1,widths.length -1);
                }
            }
            if(heightMode == MeasureSpec.AT_MOST){
                h = Math.min(heightSize,sumArray(heights));
            }else if (heightMode == MeasureSpec.UNSPECIFIED) {
                h = sumArray(heights);
            } else {
                h = heightSize;
            }
        }else {
            if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
                w = 0;
                h = 0;
            } else {
                w = widthSize;
                h = heightSize;
            }
        }

        if (firstRow >= rowCount || getMaxScrollY() - getActualScrollY() < 0) {
            firstRow = 0;
            scrollY = Integer.MAX_VALUE;
        }
        if (firstColumn >= columnCount || getMaxScrollX() - getActualScrollX() < 0) {
            firstColumn = 0;
            scrollX = Integer.MAX_VALUE;
        }
        setMeasuredDimension(w, h);
     //   super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
    // 计算数组的综合
    private int sumArray(int array[]){
        return sumArray(array,0,array.length);
    }
    private int sumArray(int array[],int start,int end){
        int sum = 0;
        end += start;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }
    //摆放子控件
    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(needRelayout || changed){
            needRelayout = false;
            resetTable();
            if(adapter == null){
                return;
            }
            width = r -l;
            height = b-t;
            int left,top,right,bottom;
            right = Math.min(width,sumArray(widths));
            bottom = Math.min(height,sumArray(heights));
            //已经绘制第一个
            headView = makeAndStep(-1,-1,0,0,widths[0],heights[0]);

            left = widths[0] - scrollX;
            //填充第一列
            for (int i = firstColumn; i < columnCount&&left < width; i++) {
                right = left + widths[i+1];
                View view = makeAndStep(-1,i,left,0,right,heights[0]);
                rowViewList.add(view);
                //循环赋值
                left = right;
            }
            top = heights[0] - scrollY;
            //填充第一行
            for (int i = firstRow; i < rowCount&&top < height; i++) {
                bottom = top + heights[i+1];
                final View view = makeAndStep(i,-1,0,top,widths[0],bottom);
                columnViewList.add(view);
                //循环赋值
                top = bottom;
            }
            top = heights[0] - scrollY;
            for (int i = firstRow; i < rowCount && top < height ; i++) {
                bottom = top + heights[i+1];
                left = widths[0] - scrollX;
                List<View> list =new ArrayList<>();
                for (int j = firstColumn; j < columnCount && left < width; j++) {
                    right = left + widths[j+1];
                    View view = makeAndStep(i,j,left,top,right,bottom);
                    list.add(view);
                    left =right;

                }
                bodyViewTable.add(list);
                //上一个子控件的下边界 赋值给下一个上边界
                top = bottom;
            }


        }
    }
    private void scrollBounds() {
        scrollX = scrollBounds(scrollX, firstColumn, widths, width);
        scrollY = scrollBounds(scrollY, firstRow, heights, height);
    }

    private int scrollBounds(int desiredScroll, int firstCell, int sizes[], int viewSize) {
        if (desiredScroll == 0) {
            // no op
        } else if (desiredScroll < 0) {
            //修整左滑的临界值
            desiredScroll = Math.max(desiredScroll, -sumArray(sizes, 1, firstCell));
        } else {
            desiredScroll = Math.min(desiredScroll, Math.max(0, sumArray(sizes, firstCell + 1, sizes.length - 1 - firstCell) + sizes[0] - viewSize));
        }
        return desiredScroll;
    }

    private void adjustFirstCellsAndScroll() {
        int values[];

        values = adjustFirstCellsAndScroll(scrollX, firstColumn, widths);
        scrollX = values[0];
        firstColumn = values[1];

        values = adjustFirstCellsAndScroll(scrollY, firstRow, heights);
        scrollY = values[0];
        firstRow = values[1];
    }

    private int[] adjustFirstCellsAndScroll(int scroll, int firstCell, int sizes[]) {
        if (scroll == 0) {
            // no op
        } else if (scroll > 0) {
            while (sizes[firstCell + 1] < scroll) {
                firstCell++;
                scroll -= sizes[firstCell];
            }
        } else {
            while (scroll < 0) {
                scroll += sizes[firstCell];
                firstCell--;
            }
        }
        return new int[] { scroll, firstCell };
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private void setAlpha(ImageView imageView, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            imageView.setAlpha(alpha);
        } else {
            imageView.setAlpha(Math.round(alpha * 255));
        }
    }

    private void resetTable() {
        headView = null;
        rowViewList.clear();
        columnViewList.clear();
        bodyViewTable.clear();

        removeAllViews();
    }


    private View makeAndStep(int row,int colmun,int left,int top
    ,int right,int bottom){
        View view = obtainView(row,colmun,right-left,bottom-top);
        //给子控件边界
        view.layout(left,top,right,bottom);
        return view;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean ret;
        final Integer row = (Integer) child.getTag(R.id.tag_row);
        final Integer column = (Integer) child.getTag(R.id.tag_column);
        if(row == null || (row == -1 && column == -1)){
            ret = super.drawChild(canvas,child,drawingTime);
        }else {
            canvas.save();
            if(row == -1){
                canvas.clipRect(widths[0],0,canvas.getWidth(),canvas.getHeight());
            } else if (column == -1) {
                canvas.clipRect(0, heights[0], canvas.getWidth(), canvas.getHeight());
            } else {
                canvas.clipRect(widths[0], heights[0], canvas.getWidth(), canvas.getHeight());
            }

            ret = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
        }
        return ret;
    }

    //真正获取View
    private View obtainView(int row,int colmun,int width,int height){
        //获取当前子控件的类型
        int itemType = adapter.getItemViewType(row,colmun);
        View recycledView = recycler.getRecycledView(itemType);
        //recycledView可能为空
        View view = adapter.getView(row,colmun,recycledView,this);

        if(view == null){
            throw new RuntimeException("view 不能为空");
        }

        view.setTag(R.id.tag_type_view,itemType);
        view.setTag(R.id.tag_column,colmun);
        view.setTag(R.id.tag_row,row);

        view.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        addTableView(view,row,colmun);
        return view;
    }

    private void addTableView(View view, int row, int column) {
        if (row == -1 && column == -1) {
            addView(view, getChildCount() -1);
        } else if (row == -1 || column == -1) {
            addView(view, getChildCount() - 2);
        } else {
            addView(view, 0);
        }
    }

    private class BaseTableAdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            needRelayout = true;
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            // Do nothing
        }
    }

    private class Flinger implements Runnable{
        private final Scroller scroller;
        private int lastX = 0;
        private int lastY = 0;

        Flinger(Context context){
            scroller = new Scroller(context);
        }
        void start(int initX,int initY,int initialVelocityX,int initialVelocityY,int maxX,int maxY){
            scroller.fling(initX,initY,initialVelocityX,initialVelocityY,0,maxX,0,maxY);
            lastX = initX;
            lastY = initY;
            post(this);
        }
        @Override
        public void run() {
            if (scroller.isFinished()) {
                return;
            }

            boolean more = scroller.computeScrollOffset();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            int diffX = lastX - x;
            int diffY = lastY - y;
            if (diffX != 0 || diffY != 0) {
                scrollBy(diffX, diffY);
                lastX = x;
                lastY = y;
            }

            if (more) {
                post(this);
            }
        }

        boolean isFinished() {
            return scroller.isFinished();
        }

        void forceFinished() {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
        }
    }
}