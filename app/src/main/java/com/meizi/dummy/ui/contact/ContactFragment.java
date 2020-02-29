package com.meizi.dummy.ui.contact;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.meizi.dummy.R;
import com.meizi.dummy.ui.add.SearchActivity;
import com.meizi.dummy.utils.ToastUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {

    public SearchFragment searchFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_contact, container, false);


        searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener(keyword -> {
            //这里处理逻辑
            System.out.println("OnSearchClick");
        });


        // 好友/群组 TAB
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getParentFragmentManager(), FragmentPagerItems.with(getContext())
                .add("好友", FriendFragment.class)
                .add("群聊", GroupFragment.class)
                .create());
        ViewPager viewPager = root.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = root.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);



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
        switch (item.getItemId()) {
            case R.id.action_search://点击搜索
                searchFragment.showFragment(getParentFragmentManager(), SearchFragment.TAG);
                break;
            case R.id.add_relationship:
                Intent intend = new Intent(getContext(), SearchActivity.class);
                startActivity(intend);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}