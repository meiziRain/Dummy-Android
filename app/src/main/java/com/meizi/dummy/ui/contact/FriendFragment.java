package com.meizi.dummy.ui.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meizi.dummy.R;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactLayout;

import java.util.ArrayList;


public class FriendFragment extends Fragment {
    private ContactLayout mContactLayout;
    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        mContactLayout = root.findViewById(R.id.contact_layout);
        mContactLayout.getTitleBar().setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mContactLayout.initDefault();
    }
}
