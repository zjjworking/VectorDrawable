package com.zjj.vectordrawable.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by zjj on 17/10/9.
 */

public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter{
    private ArrayList<View> mHeaderViewInfos = new ArrayList<>();
    private ArrayList<View> mFooterViewInfos = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    public HeaderViewRecyclerAdapter(ArrayList<View> headerViewInfos,ArrayList<View> footerViewInfos,
                                     RecyclerView.Adapter adapter){
        mAdapter = adapter;
        if(mHeaderViewInfos == null){
            mHeaderViewInfos = new ArrayList<>();
        }else {
            mHeaderViewInfos = headerViewInfos;
        }
        if(mFooterViewInfos == null){
            mFooterViewInfos = new ArrayList<>();
        }else {
            mFooterViewInfos = footerViewInfos;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == RecyclerView.INVALID_TYPE){
            return new HeaderViewHolder(mHeaderViewInfos.get(0));
        }else if (viewType == RecyclerView.INVALID_TYPE -1){
            return new FooterViewHolder(mFooterViewInfos.get(0));
        }
        return mAdapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == RecyclerView.INVALID_TYPE){
            return;
        }else if(viewType == RecyclerView.INVALID_TYPE -1){
            return;
        }else {
            mAdapter.onBindViewHolder(holder,position - mHeaderViewInfos.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = mHeaderViewInfos.size();
        if(numHeaders > position){
            return RecyclerView.INVALID_TYPE;
        }
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if(mAdapter != null){
            adapterCount = mAdapter.getItemCount();
            if(adapterCount > adjPosition){
                return mAdapter.getItemViewType(adjPosition);
            }
        }
        return RecyclerView.INVALID_TYPE - 1;
    }

    @Override
    public int getItemCount() {
        return mAdapter != null ? mAdapter.getItemCount() + mFooterViewInfos.size() + mHeaderViewInfos.size() :
                mFooterViewInfos.size() + mHeaderViewInfos.size() ;
    }
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    private static class FooterViewHolder extends RecyclerView.ViewHolder{

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
