package com.zjj.vectordrawable.view.ItemTouchHelpExtend;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

import com.zjj.vectordrawable.adapter.RecyclerAdapter;
import com.zjj.vectordrawable.view.Recycler;

/**
 * Created by zjj on 17/10/12.
 */

public class ItemTouchHelpCallback extends ItemTouchHelper.Callback{
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int sipeFlag = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlag,sipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        RecyclerAdapter adapter = (RecyclerAdapter)recyclerView.getAdapter();
        adapter.move(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(dY != 0 && dX ==0){
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        RecyclerAdapter.ItemBaseViewHolder holder = (RecyclerAdapter.ItemBaseViewHolder) viewHolder;
        if(viewHolder instanceof RecyclerAdapter.ItemSwipeWithActionWidthNoSpringViewHolder){
            if(dX < -holder.mActionContainer.getWidth()){
                dX =- holder.mActionContainer.getWidth();
            }
            holder.mViewContent.setTranslationX(dX);
            return;
        }
        if(viewHolder instanceof RecyclerAdapter.ItemBaseViewHolder){
            holder.mViewContent.setTranslationX(dX);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
