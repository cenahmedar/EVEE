package com.eventapp.dialogs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eventapp.R;
import com.eventapp.helpers.Apm;
import com.eventapp.models.BasicModel;
import com.eventapp.pages.auth.AuthActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ListBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private final List<BasicModel> list;
    private final IListBottomSheetDialogFragment iListBottomSheetDialogFragment;

    @BindView(R.id.listView)
    ListView listView;

    public interface IListBottomSheetDialogFragment {
        void BsCallback(BasicModel basicModel);
    }


    public ListBottomSheetDialogFragment(List<BasicModel> list, IListBottomSheetDialogFragment iListBottomSheetDialogFragment) {
        this.list = list;
        this.iListBottomSheetDialogFragment = iListBottomSheetDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_modal, container, false);
        ButterKnife.bind(this, root);
        ArrayAdapter<BasicModel> adapter = new ArrayAdapter<BasicModel>(getActivity(), android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            dismiss();
            iListBottomSheetDialogFragment.BsCallback((BasicModel) parent.getItemAtPosition(position));
        });


        return root;
    }


}
