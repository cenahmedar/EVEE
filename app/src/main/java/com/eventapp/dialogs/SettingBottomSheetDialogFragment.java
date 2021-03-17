package com.eventapp.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.R;
import com.eventapp.helpers.Apm;
import com.eventapp.pages.auth.AuthActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import retrofit2.Response;

public class SettingBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public SettingBottomSheetDialogFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.setting_bottom_dialog, container, false);
        ButterKnife.bind(this, root);
        Apm apm = new Apm(getActivity());
        root.findViewById(R.id.btn_log_out_this).setOnClickListener(view -> {
            apm.postUser(null);
            Intent i = new Intent(getActivity(), AuthActivity.class);
            i.putExtra("start_with_login_fragment", true);
            startActivity(i);
            getActivity().finish();
            FirebaseAuth.getInstance().signOut();
        });
        return root;
    }
}
