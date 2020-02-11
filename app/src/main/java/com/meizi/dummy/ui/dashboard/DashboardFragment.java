package com.meizi.dummy.ui.dashboard;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuView;
import com.meizi.dummy.ChatActivity;
import com.meizi.dummy.R;
import com.meizi.dummy.ui.chat.def.DefaultMessagesActivity;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.lang.reflect.Method;
import java.util.List;

public class DashboardFragment extends Fragment {
    private CircleRefreshLayout mRefreshLayout;
    private DashboardViewModel dashboardViewModel;

    private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 使用了这个方法以后关于menu的回调函数才会被调用

        initSwipeMenuListView((SwipeMenuListView) root.findViewById(R.id.listView));

//        final CircleRefreshLayout mRefreshLayout = root.findViewById(R.id.refresh_layout);
//        initCircleRefreshLayout(mRefreshLayout);

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void initCircleRefreshLayout(CircleRefreshLayout mRefreshLayout) {
        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        // do something when refresh starts
                        Toast.makeText(getContext(), "Refresh started!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                        Toast.makeText(getContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initSwipeMenuListView(SwipeMenuListView listView) {
        mAppList = getContext().getPackageManager().getInstalledApplications(0);
        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("置顶");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        listView.setMenuCreator(creator);

        //  No adapter attached; skipping layout 报错，原因未知。
        listView.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {  //用swith的方法来实现item的每一项的点击
                    case 0://0代表第一行的点击！也可以点击Item实现跳转！只要用Intent来实现就好！
                        Intent intent = new Intent(getActivity(), DefaultMessagesActivity.class);
                        startActivity(intent);
                        break;
                    case 1://0代表第一行的点击
                        Toast.makeText(getContext(), "我终于找到你了......", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
//					delete(item);
                        mAppList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                Toast.makeText(getContext(), "swipe start!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                Toast.makeText(getContext(), "swipe end!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ApplicationInfo item = getItem(position);
            holder.iv_icon.setImageDrawable(item.loadIcon(getContext().getPackageManager()));
            holder.tv_name.setText(item.loadLabel(getContext().getPackageManager()));
            return convertView;
        }


        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }
    }
}