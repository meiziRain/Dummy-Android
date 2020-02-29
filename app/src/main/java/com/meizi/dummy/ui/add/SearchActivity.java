package com.meizi.dummy.ui.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.meizi.dummy.R;
import com.meizi.dummy.utils.ToastUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.friendship.TIMFriendRequest;
import com.tencent.imsdk.friendship.TIMFriendResult;

public class SearchActivity extends AppCompatActivity{


    @BindView(R.id.floating_search_view)
    FloatingSearchView floatingSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);


        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String query) {
                TIMFriendRequest timFriendRequest = new TIMFriendRequest(query);
                timFriendRequest.setAddWording("it's me!");
                timFriendRequest.setAddSource("android");
                TIMFriendshipManager.getInstance().addFriend(timFriendRequest, new TIMValueCallBack<TIMFriendResult>() {
                    @Override
                    public void onError(int i, String s) {
                        ToastUtil.longMessage("添加失败！");
                    }

                    @Override
                    public void onSuccess(TIMFriendResult timFriendResult) {
                        ToastUtil.longMessage("添加成功！");
                    }
                });
            }
        });



    }
}
