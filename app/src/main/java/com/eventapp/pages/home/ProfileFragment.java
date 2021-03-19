package com.eventapp.pages.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.models.AddressModel;
import com.eventapp.models.Event;
import com.eventapp.models.EventFilter;
import com.eventapp.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opensooq.supernova.gligar.GligarPicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class ProfileFragment extends BaseFragment implements AuthService.IAuthService {

    private View rootView;
    private String pickedImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_profile)
    CircleImageView iv_profile;

    @BindView(R.id.txName)
    EditText txName;

    @BindView(R.id.txSurname)
    EditText txSurname;

    @BindView(R.id.txEmail)
    EditText txEmail;

    private AuthService authService;
    private User user;
    private static final int PICKER_REQUEST_CODE = 999;
    private StorageReference storageReference;

    private void setToolBar() {
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_save:
                    if (pickedImage != null)
                        uploadImage(pickedImage);
                    else {
                        save();
                    }
                    break;
            }
            return true;
        });
    }

    private void uploadImage(String pickedImage) {
        progressBarBuilder.show();
        String imageName = UUID.randomUUID().toString();
        final StorageReference imageFolder = storageReference.child("images/" + imageName);
        imageFolder.putFile(Uri.fromFile(new File(pickedImage))).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(activity, "Image Uploaded", Toast.LENGTH_SHORT).show();
            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                progressBarBuilder.hide();
                user.setImage(uri.toString());
                save();

            });
        }).addOnFailureListener(e -> {
            progressBarBuilder.hide();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, parent, false);
        ButterKnife.bind(this, rootView);
        setToolBar();

        authService = new AuthService(activity, apm, this);
        storageReference = FirebaseStorage.getInstance().getReference();

        init();
        return rootView;
    }

    private void init() {
        progressBarBuilder.show();
        authService.getUser(authService.getMyKey());
    }

    @OnClick({R.id.iv_profile})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_profile:
                new GligarPicker().requestCode(PICKER_REQUEST_CODE).withFragment(this).show();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == PICKER_REQUEST_CODE) {

            String[] pathsList = data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT);
            if (pathsList.length != 0) {
                pickedImage = pathsList[0];
            }

            //iv_profile.setImageBitmap(BitmapFactory.decodeFile(pickedImage));
            Picasso.get().load(new File(pickedImage)).fit().centerCrop()
                    .placeholder(R.drawable.avatar).error(R.drawable.avatar).into(iv_profile);

        }
    }


    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        progressBarBuilder.hide();
        if (response.equals(AuthService.AuthResponse.GetUser) && success) {
            this.user = user;
            initView();
        }
    }

    private void initView() {
        txEmail.setText(user.getEmail());
        txName.setText(user.getName());
        txSurname.setText(user.getSurName());
        Picasso.get().load(user.getImage()).fit().centerCrop()
                .placeholder(R.drawable.avatar).error(R.drawable.avatar).into(iv_profile);

    }


    private void save() {
        pickedImage = null;

        final String name = txName.getText().toString().trim();
        final String surname = txSurname.getText().toString().trim();
        if (name.isEmpty() || surname.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        user.setName(name);
        user.setSurName(surname);

        authService.updateUser(authService.getMyKey(), user);
    }

}