package com.zjj.vectordrawable.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjj.vectordrawable.R;

/**
 * Created by zjj on 17/9/19.
 */

public class MyAdapter implements  BaseTableAdapter{
    LayoutInflater layoutInflater;
    private int width;
    private int height;
    private int count = 10;
    public MyAdapter(Context context){
        Resources resources =  context.getResources();
        width = resources.getDimensionPixelOffset(R.dimen.table_width);
        height = resources.getDimensionPixelOffset(R.dimen.table_height);
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getRowCount() {
        return count;
    }
    public void setRowCount(int count){
        this.count = count;
    }

    @Override
    public int getColmunCount() {
        return 20;
    }

    private int getLayout(int row,int column){
        final int layoutResource;
        switch (getItemViewType(row,column)){
            case 0:
                layoutResource = R.layout.item_table1_header;
                break;
            case 1:
                layoutResource = R.layout.item_table1;
                break;
            default:
                throw new RuntimeException("mdzz");
        }
        return layoutResource;
    }
    @Override
    public View getView(int row, int comun, View converView, ViewGroup parent) {
        if(converView == null){
            converView = layoutInflater.inflate(getLayout(row,comun),parent,false);
        }
        TextView textView = (TextView)converView.findViewById(R.id.text1);
        textView.setText("第"+row +"行" +"第"+ comun +"列");
        return converView;
    }

    @Override
    public int getWidth(int columu) {
        return width;
    }

    @Override
    public int getHeight(int row) {
        return height;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if(row < 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
