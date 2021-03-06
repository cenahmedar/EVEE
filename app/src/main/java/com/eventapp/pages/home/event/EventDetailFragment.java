package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.dialogs.EventRatingDialog;
import com.eventapp.dialogs.EventSearchDialog;
import com.eventapp.dialogs.ParticipantsDialog;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.firbaseServices.RatingService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.models.Event;
import com.eventapp.models.Rating;
import com.eventapp.models.User;
import com.eventapp.pages.home.HomePageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@SuppressLint("NonConstantResourceId")
public class EventDetailFragment extends BaseFragment implements EventService.IEventService,
        AuthService.IAuthService, RatingDialogListener, RatingService.IRatingService {


    private View rootView;
    private String key;
    private EventService eventService;
    private RatingService ratingService;
    private AuthService authService;
    private Event event;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.btn_edit)
    ImageView btn_edit;

    @BindView(R.id.tx_name)
    TextView tx_name;

    @BindView(R.id.tx_location)
    TextView tx_location;

    @BindView(R.id.tx_date)
    TextView tx_date;

    @BindView(R.id.tx_time)
    TextView tx_time;

    @BindView(R.id.tx_desc)
    TextView tx_desc;

    @BindView(R.id.tx_user_name)
    TextView tx_user_name;

    @BindView(R.id.tx_count)
    TextView tx_count;

    @BindView(R.id.tx_type)
    TextView tx_type;

    @BindView(R.id.tx_price)
    TextView tx_price;

    @BindView(R.id.btn_join)
    Button btn_join;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_detail, parent, false);
        ButterKnife.bind(this, rootView);

        eventService = new EventService(this);
        ratingService = new RatingService(this);
        authService = new AuthService(activity, apm, this);
        firebaseAuth = FirebaseAuth.getInstance();
        key = (String) bundleManager.getSerializable(this, BundleManager.SELECTED_EVENT_KEY);

        init();
        return rootView;
    }

    private void init() {
        progressBarBuilder.show();
        eventService.getEvent(key);
        ratingService.getEventRate(key);
    }

    @SuppressLint("SetTextI18n")
    private void setEvent() {

        if (authService.getMyKey().equals(event.getUserKey()))
            btn_edit.setVisibility(View.VISIBLE);

        tx_name.setText(event.getName());
        tx_location.setText(event.getAddress().getAddress());
        tx_date.setText(event.getDate());
        tx_time.setText(event.getTime());
        tx_desc.setText(event.getDescription());
        tx_type.setText(event.getTypeName());
        tx_count.setText(event.getUsersCount() + "");
        if (event.getPrice() == null || event.isFree())
            tx_price.setText("FREE");
        else
            tx_price.setText(event.getPrice().toString());
        tx_user_name.setText(event.getUser().getName().toUpperCase() + " " + event.getUser().getSurName().toUpperCase());
        Picasso.get().load(event.getImage()).into(imageView);

        if (event.isJoined(authService.getMyKey())) {
            btn_join.setBackgroundColor(activity.getResources().getColor(R.color.pink));
            btn_join.setText("LEAVE");
        } else {
            btn_join.setBackgroundColor(activity.getResources().getColor(R.color.greenJoinBtnColor));
            btn_join.setText("JOIN");
        }
    }

    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        if (response.equals(AuthService.AuthResponse.GetUser) && success) {
            progressBarBuilder.hide();
            if (event == null) {
                activity.onBackPressed();
            } else {
                // authService.getUser(event.getUserKey());
                event.setUser(user);
                setEvent();
            }
        }
    }

    @Override
    public void EventResponse(EventService.EventResponse response, boolean success, Event event) {
        if (response.equals(EventService.EventResponse.GetEvent) && success) {
            progressBarBuilder.hide();
            this.event = event;
            if (event == null) {
                activity.onBackPressed();
            } else
                authService.getUser(event.getUserKey());

        } else if (response.equals(EventService.EventResponse.JoinUnJoinEvent)) {
            eventService.getEvent(key);
        }
    }


    @OnClick({R.id.btn_close, R.id.btn_join, R.id.btn_edit, R.id.tx_count, R.id.btn_rate, R.id.ln_ratingBar})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                activity.onBackPressed();
                break;

            case R.id.btn_join:
                eventService.joinUnJoinEvent(key, event);
                break;


            case R.id.btn_edit:
                bundleManager = new BundleManager();
                bundleManager.addSerializable(BundleManager.SELECTED_EVENT, event);
                bundleManager.addSerializable(BundleManager.SELECTED_EVENT_KEY, key);
                ((HomePageActivity) activity).loadFragment(new EventCrudFragment(), true, false, bundleManager.getBundle());
                break;

            case R.id.tx_count:
                ParticipantsDialog exampleDialog = new ParticipantsDialog(key);
                exampleDialog.show(getChildFragmentManager(), "ParticipantsDialog");
                break;

            case R.id.btn_rate:
                showRatingDialog();
                break;

            case R.id.ln_ratingBar:
                EventRatingDialog eventRatingDialog = new EventRatingDialog(key);
                eventRatingDialog.show(getChildFragmentManager(), "EventRatingDialog");
                break;

        }
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("SAVE")
                .setNegativeButtonText("CANCEL")
                .setNoteDescriptions(Arrays.asList("very bad", "bad", "not bad", "good", "very good"))
                .setDefaultRating(1)
                .setTitle("Stars")
                .setDescription("Please share your thoughts")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Comment..")
                .setHintTextColor(R.color.whitecustom)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(getActivity()).setTargetFragment(this, 0)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comment) {
        final Rating rating = new Rating(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(),
                key,
                String.valueOf(value),
                comment);

        ratingService.insertRate(rating);
    }

    @Override
    public void RatingResponse(RatingService.RatingResponse response, boolean success, float rate) {
        if (response.equals(RatingService.RatingResponse.InsertRate)) {
            ratingService.getEventRate(key);
        } else if (response.equals(RatingService.RatingResponse.EventRate)) {
            ratingBar.setRating(rate);
        }
    }

    @Override
    public void ListResponse(ArrayList<Rating> ratings) {

    }
}
