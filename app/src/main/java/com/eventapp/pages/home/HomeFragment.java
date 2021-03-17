package com.eventapp.pages.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.dialogs.SettingBottomSheetDialogFragment;
import com.eventapp.pages.home.event.EventCrudFragment;
import com.eventapp.pages.home.event.EventDetailFragment;
import com.eventapp.pages.home.event.MyEventsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

@SuppressLint("NonConstantResourceId")
public class HomeFragment extends BaseFragment {

    private View rootView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void setToolBar() {
        toolbar.inflateMenu(R.menu.home_menu);
        toolbar.setNavigationIcon(null);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                logOutMenu();
                break;

            case R.id.action_my_list:
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new MyEventsFragment(), true, false, bundleManager.getBundle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutMenu() {
        SettingBottomSheetDialogFragment addPhotoBottomDialogFragment = new SettingBottomSheetDialogFragment();
        addPhotoBottomDialogFragment.show(getChildFragmentManager(), "SettingBottomSheetDialogFragment");
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, parent, false);
        ButterKnife.bind(this, rootView);
        setToolBar();


        init();
        return rootView;
    }

    private void init() {
    }


    @OnClick({R.id.eventCardLayout})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.eventCardLayout:
            /*    bundleManager = new BundleManager();
                bundleManager.addSerializable(BundleManager.SELECTED_CHAT_ID, chat.getID());
                bundleManager.addSerializable(BundleManager.SELECTED_CHAT, chat);*/
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventDetailFragment(), true, false, bundleManager.getBundle()));
                break;
        }
    }


}