package com.zjj.vectordrawable;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class VectorCompatActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector_compat);
    }

    public void anim(View view) {
        ImageView imageView = (ImageView) view;
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void switchToLPlus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(VectorCompatActivity.this, LPlusActivity.class));
        } else {
            Toast.makeText(VectorCompatActivity.this, "系统版本不支持L plus", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            switchToLPlus();
            return true;
        }else if(id == R.id.action_main){
            startActivity(new Intent(VectorCompatActivity.this, MainActivity.class));
            return true;
        }else if(id == R.id.Recycler){
            startActivity(new Intent(VectorCompatActivity.this, RecyclerViewActivity.class));
            return true;
        }else if(id == R.id.RollDebug){
        startActivity(new Intent(VectorCompatActivity.this, RollDebugAct.class));
        return true;
        }else if(id == R.id.DemoAct){
        startActivity(new Intent(VectorCompatActivity.this, DemoAct.class));
        return true;
         }else if(id == R.id.itemTouchHelper){
            startActivity(new Intent(VectorCompatActivity.this, ItemTouchHelperActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
