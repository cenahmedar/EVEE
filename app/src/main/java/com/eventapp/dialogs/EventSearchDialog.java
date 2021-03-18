package com.eventapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.eventapp.R;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.Functions;
import com.eventapp.models.BasicModel;
import com.eventapp.models.Event;
import com.eventapp.models.EventFilter;
import com.eventapp.pages.LocationPickActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.opensooq.supernova.gligar.GligarPicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.eventapp.helpers.mConstants.Types;

@SuppressLint("NonConstantResourceId")
public class EventSearchDialog extends DialogFragment implements EventService.IEventService {

    public static final String TAG = "search_dialog";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tx_type_name)
    TextView tx_type_name;

    @BindView(R.id.tx_city)
    TextView tx_city;

    @BindView(R.id.txPrice)
    EditText txPrice;


    private EventFilter eventFilter;
    private final IEventSearchDialog iEventSearchDialog;
    private EventService eventService;
    private ArrayList<BasicModel> cities;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    public interface IEventSearchDialog {
        void BsCallback(EventFilter eventFilter);
    }


    public EventSearchDialog(EventFilter eventFilter, IEventSearchDialog iEventSearchDialog) {
        this.eventFilter = eventFilter;
        this.iEventSearchDialog = iEventSearchDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.event_search_dialog, container, false);
        ButterKnife.bind(this, view);
        eventService = new EventService(this);
        getCities();

        tx_city.setText(eventFilter.getCityName());
        tx_type_name.setText(eventFilter.getTypeName());
        txPrice.setText(eventFilter.getPriceTo());
        return view;
    }

    private void getCities() {
        cities = new ArrayList<>();
        ArrayList<String> cityName = new ArrayList<>();
        eventService.getRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int i = 0;
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Event event = ds.getValue(Event.class);
                    if (!cityName.contains(event.getAddress().getCity())) {
                        cityName.add(event.getAddress().getCity());
                        cities.add(new BasicModel(i, event.getAddress().getCity()));
                        i++;
                    }

                }

            }


        });
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Event Filter");
        toolbar.inflateMenu(R.menu.search_dialog_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_reset:
                    iEventSearchDialog.BsCallback(new EventFilter());
                    dismiss();
                    break;
            }
            return true;
        });
    }

    @OnClick({R.id.sp_type, R.id.sp_city, R.id.btnSave})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.sp_type:
                spnTypeBuilder();
                break;
            case R.id.sp_city:
                spnCityBuilder();
                break;
            case R.id.btnSave:
                save();
                break;

        }
    }


    private void spnTypeBuilder() {
        ListBottomSheetDialogFragment typeDialog = new ListBottomSheetDialogFragment(Types, basicModel -> {
            tx_type_name.setText(basicModel.getName());
            eventFilter.setTypeID(basicModel.getID());
            eventFilter.setTypeName(basicModel.getName());
        });
        typeDialog.show(getChildFragmentManager(), "typeDialog");
    }


    private void spnCityBuilder() {

        ListBottomSheetDialogFragment typeDialog = new ListBottomSheetDialogFragment(cities, basicModel -> {
            tx_city.setText(basicModel.getName());
            eventFilter.setCityName(basicModel.getName());
        });
        typeDialog.show(getChildFragmentManager(), "cityDialog");

    }

    @Override
    public void EventResponse(EventService.EventResponse eventResponse, boolean success, Event event) {
    }


    private void save() {
        String price = txPrice.getText().toString().trim();
        if (Functions.isNullOrEmpty(price))
            eventFilter.setPriceTo(null);
        else
            eventFilter.setPriceTo(price);
        iEventSearchDialog.BsCallback(eventFilter);
        dismiss();
    }

}
