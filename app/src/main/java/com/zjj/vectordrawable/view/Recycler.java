package com.zjj.vectordrawable.view;

import android.view.View;

import java.util.Stack;

/**
 * Created by zjj on 17/9/19.
 */

public class Recycler {
    private Stack<View> [] views;//集合数组
    //打造一个回收池
    public Recycler(int type){
        views = new Stack[type];
        for (int i = 0; i < type; i++) {
            views[i] = new Stack<>();
        }
    }
    public void addRecycledView(View view,int type){
         views[type].push(view);
    }

    public View getRecycledView(int type){
        try{
            return  views[type].pop();
        }catch (Exception e){
            return null;
        }
    }
}
