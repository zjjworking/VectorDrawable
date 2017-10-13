package com.zjj.vectordrawable.view;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zjj on 17/9/19.
 */

public interface BaseTableAdapter {
    public int getRowCount();

    public int getColmunCount();

    public View getView(int row, int comun, View converView, ViewGroup parent);

    public int getWidth(int columu);

    public int getHeight(int row);

    public int getItemViewType(int row,int column);

    public int getViewTypeCount();
}
