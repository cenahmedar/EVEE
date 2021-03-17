package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.dialogs.ListBottomSheetDialogFragment;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.models.AddressModel;
import com.eventapp.models.Event;
import com.eventapp.pages.LocationPickActivity;
import com.eventapp.pages.home.HomePageActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opensooq.supernova.gligar.GligarPicker;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import static com.eventapp.helpers.Functions.isNullOrEmpty;
import static com.eventapp.helpers.mConstants.Types;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

@SuppressLint("NonConstantResourceId")
public class EventCrudFragment extends BaseFragment implements EventService.IEventService {


    private static final int PICKER_REQUEST_CODE = 999;
    private static final int LOCATION_PICK = 997;
    private View rootView;
    private String pickedImage;
    private WifiManager wifiManager;
    private Event event;
    private String key;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.tx_phone)
    EditText tx_phone;

    @BindView(R.id.tx_date)
    TextView tx_date;

    @BindView(R.id.tx_type_name)
    TextView tx_type_name;

    @BindView(R.id.txName)
    EditText txName;

    @BindView(R.id.txPrice)
    EditText txPrice;

    @BindView(R.id.txDesc)
    EditText txDesc;

    @BindView(R.id.tx_location)
    TextView tx_location;


    @BindView(R.id.lin_price)
    LinearLayout lin_price;


    @BindView(R.id.image_1)
    ImageView image_1;

    @BindView(R.id.btnPickImage)
    ImageView btnPickImage;

    @BindView(R.id.freeSwitch)
    SwitchMaterial freeSwitch;

    private StorageReference storageReference;
    private EventService eventService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();
        eventService = new EventService(this);
        event = new Event();

        key = (String) bundleManager.getSerializable(this, BundleManager.SELECTED_EVENT_KEY);
        event = (Event) bundleManager.getSerializable(this, BundleManager.SELECTED_EVENT);

    }

    private void setToolBar() {
        toolbar.setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
    }


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_crud, parent, false);
        ButterKnife.bind(this, rootView);


        setToolBar();

        init();
        return rootView;
    }

    private void init() {
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        image_1.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                pickedImage = null;
                image_1.setImageResource(0);
                image_1.setVisibility(View.GONE);
                btnPickImage.setVisibility(View.VISIBLE);

            }
        }));
        freeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lin_price.setVisibility(View.GONE);
            } else {
                lin_price.setVisibility(View.VISIBLE);
            }
        });

        if (key != null)
            initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        image_1.setVisibility(View.VISIBLE);
        btnPickImage.setVisibility(View.GONE);
        Picasso.get().load(event.getImage()).into(image_1);
        txName.setText(event.getName());
        tx_type_name.setText(event.getTypeName());
        tx_date.setText(event.getDate() + " " + event.getTime());
        tx_phone.setText(event.getPhone());
        freeSwitch.setChecked(event.isFree());
        txPrice.setText(event.getPrice() == null ? null : event.getPrice().toString());
        txDesc.setText(event.getDescription());
        tx_location.setText(event.getAddress().getAddress());
    }


    @OnClick({R.id.sp_type, R.id.btn_date, R.id.btnPickImage, R.id.btn_location, R.id.btnSave})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.sp_type:
                spnTypeBuilder();
                break;
            case R.id.btn_date:
                dateTimePickerBuilder();
                break;
            case R.id.btnPickImage:
                new GligarPicker().requestCode(PICKER_REQUEST_CODE).withFragment(this).show();
                break;
            case R.id.btn_location:
                startActivityForResult(new Intent(activity, LocationPickActivity.class), LOCATION_PICK);
                break;
            case R.id.btnSave:
                if (pickedImage != null)
                    uploadImage(pickedImage);
                else {
                    if (key == null)
                        save();
                    else
                        update();
                }
                break;

        }
    }


    private void spnTypeBuilder() {
        ListBottomSheetDialogFragment typeDialog = new ListBottomSheetDialogFragment(Types, basicModel -> {
            tx_type_name.setText(basicModel.getName());
            event.setTypeID(basicModel.getID());
            event.setTypeName(basicModel.getName());
        });
        typeDialog.show(getChildFragmentManager(), "typeDialog");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void dateTimePickerBuilder() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (view1, hourOfDay, minute) -> {

                String date = String.format("%02d", dayOfMonth) + "." + String.format("%02d", month + 1) + "." + year;
                String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                tx_date.setText(date + " " + time);

                event.setDate(date);
                event.setTime(time);

            }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(activity));
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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

            image_1.setImageBitmap(BitmapFactory.decodeFile(pickedImage));
            btnPickImage.setVisibility(View.GONE);
            image_1.setVisibility(View.VISIBLE);

        } else if (requestCode == LOCATION_PICK) {
            AddressModel addressModel = (AddressModel) data.getExtras().getSerializable("ADDRESS_MODEL");
            tx_location.setText(addressModel.getAddress());
            event.setAddress(addressModel);
        }
    }


    private void uploadImage(String pickedImage) {
        progressBarBuilder.show();
        String imageName = UUID.randomUUID().toString();
        final StorageReference imageFolder = storageReference.child("images/" + imageName);
        imageFolder.putFile(Uri.fromFile(new File(pickedImage))).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(activity, "Image Uploaded", Toast.LENGTH_SHORT).show();
            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                progressBarBuilder.hide();
                event.setImage(uri.toString());
                if (key == null)
                    save();
                else
                    update();
            });
        }).addOnFailureListener(e -> {
            progressBarBuilder.hide();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void save() {
        event.setName(txName.getText().toString().trim());
        event.setPhone(tx_phone.getText().toString().trim());
        event.setFree(freeSwitch.isChecked());
        if (!freeSwitch.isChecked())
            event.setPrice(Double.parseDouble(txPrice.getText().toString()));
        else
            event.setPrice(null);

        event.setDescription(txDesc.getText().toString());

        if (event.getAddress() == null || isNullOrEmpty(event.getName()) || isNullOrEmpty(event.getTypeName()) || event.getDate() == null || event.getTime() == null ||
                isNullOrEmpty(event.getPhone()) || (event.getPrice() == null && !event.isFree()) || isNullOrEmpty(event.getDescription()) || event.getImage() == null) {
            Toast.makeText(activity, "please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarBuilder.show();
        eventService.insertEvent(event);

    }


    private void update() {
        event.setName(txName.getText().toString().trim());
        event.setPhone(tx_phone.getText().toString().trim());
        event.setFree(freeSwitch.isChecked());
        if (!freeSwitch.isChecked())
            event.setPrice(Double.parseDouble(txPrice.getText().toString()));
        else
            event.setPrice(null);

        event.setDescription(txDesc.getText().toString());

        if (event.getAddress() == null || isNullOrEmpty(event.getName()) || isNullOrEmpty(event.getTypeName()) || event.getDate() == null || event.getTime() == null ||
                isNullOrEmpty(event.getPhone()) || (event.getPrice() == null && !event.isFree()) || isNullOrEmpty(event.getDescription()) || event.getImage() == null) {
            Toast.makeText(activity, "please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarBuilder.show();
        eventService.updateEvent(key, event);
    }


    @Override
    public void EventResponse(EventService.EventResponse response, boolean success, Event event) {
        if (response.equals(EventService.EventResponse.InsertEvent) || response.equals(EventService.EventResponse.UpdateEvent)) {
            progressBarBuilder.hide();
            if (success)
                activity.onBackPressed();
        }
    }
}