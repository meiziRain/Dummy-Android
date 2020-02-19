package com.meizi.dummy.ui.contact;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.meizi.dummy.R;

import java.lang.reflect.Method;
import java.util.List;

public class ContactFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_contact, container, false);


        // 使用了这个方法以后关于menu的回调函数才会被调用
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        try {
            // https://blog.csdn.net/guolin_blog/article/details/18234477
            // https://www.cnblogs.com/caoyc/p/4581214.html
            // 利用反射设置展开后的列表图标(默认是不显示图标的)
            // 此处与文章不同的原因可能是导入包不同
            // 之前报错：
            // System.err: java.lang.IllegalArgumentException:
            // Expected receiver of type com.android.internal.view.menu.MenuBuilder,
            // but got androidx.appcompat.view.menu.MenuBuilder
            // 2020-02-08 13:42:11.863 28187-28187/com.meizi.dummy W/System.err:     at java.lang.reflect.Method.invoke(Native Method)
            Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            // MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}