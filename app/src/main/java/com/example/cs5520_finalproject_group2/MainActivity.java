package com.example.cs5520_finalproject_group2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.cs5520_finalproject_group2.Fragments.AddEventFragment;
import com.example.cs5520_finalproject_group2.Fragments.EditEventFragment;
import com.example.cs5520_finalproject_group2.Fragments.LoginPageFragment;
import com.example.cs5520_finalproject_group2.Fragments.RegisterPageFragment;
import com.example.cs5520_finalproject_group2.Fragments.TimeTableFragment;
import com.example.cs5520_finalproject_group2.Fragments.UserProfileFragment;
import com.example.cs5520_finalproject_group2.Models.Event;
import com.example.cs5520_finalproject_group2.Models.EventAdapter;
import com.example.cs5520_finalproject_group2.TakePhoto.PhotoMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        RegisterPageFragment.fromRegisterPageFragment, LoginPageFragment.fromLoginPageFragment,
        UserProfileFragment.fromUserProfileFragment, AddEventFragment.IAddEventActivity,
        TimeTableFragment.ITimeTableActivity, EditEventFragment.IEditEventActivity,
        EventAdapter.IEventAdapterAction {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String registering = null;
    private String imageUri = null;
    private final String TAG = "demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        populateTheScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageUri != null) {
            if(Objects.equals(registering, "true")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, RegisterPageFragment.newInstance(imageUri),
                                "registerFragment")
                        .commit();
                registering = null;
            }
            imageUri = null;
        }
    }

    public void populateTheScreen() {
        if (currentUser != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, UserProfileFragment.newInstance(),
                            "mainFragment").addToBackStack(null)
                    .commit();

        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, LoginPageFragment.newInstance(),
                            "loginFragment")
                    .commit();
        }
    }


    ActivityResultLauncher<Intent> startActivity
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        //imageUri
                        Intent data = result.getData();
                        assert data != null;
                        setImageUri(data.getStringExtra("imageUri"));
                    }
                }
            });

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setRegistering(String registering) {
        this.registering = registering;
    }

    /**
     * For registerPageFragment interface
     */
    @Override
    public void pickProfilePhoto() {
        setRegistering("true");
        Intent toInClass09 = new Intent(MainActivity.this, PhotoMainActivity.class);
        startActivity.launch(toInClass09);
    }

    /**
     * For registerPageFragment interface
     */
    @Override
    public void fromRegisterPageToLoginPage() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, LoginPageFragment.newInstance(),
                        "loginFragment")
                .commit();
    }

    /**
     * For registerPageFragment interface
     */
    @Override
    public void clickRegisterButton(FirebaseUser firebaseUser) {
        setRegistering(null);
        currentUser = firebaseUser;
        populateTheScreen();
    }

    /**
     * For loginPageFragment interface
     */
    @Override
    public void fromLoginPageToRegisterPage() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, RegisterPageFragment.newInstance(null),
                        "registerFragment")
                .commit();
    }

    /**
     * For loginPageFragment interface
     */
    @Override
    public void clickLoginButton(FirebaseUser firebaseUser) {
        this.currentUser = firebaseUser;
        populateTheScreen();
    }

    /**
     * For userProfileFragment interface
     */
    @Override
    public void logout() {
        firebaseAuth.signOut();
        currentUser = null;
        setImageUri(null);
        setRegistering(null);
        populateTheScreen();
    }


    @Override
    public void addEvent(String day) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, TimeTableFragment.newInstance(day),
                        "TimeTableFragment")
                .commit();
    }

    @Override
    public void toAddEvent(String day) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, AddEventFragment.newInstance(day),
                        "AddEventFragment")
                .commit();
    }

    @Override
    public void toNavigation(String day) {
        Intent map = new Intent(MainActivity.this, MapActivity.class);
        map.putExtra("data", day);
        startActivity(map);
    }

    @Override
    public void toUserProfile() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, UserProfileFragment.newInstance(),
                        "UserProfileFragment")
                .commit();
    }

    @Override
    public void editEvent(String day) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, TimeTableFragment.newInstance(day),
                        "TimeTableFragment")
                .commit();
    }

    @Override
    public void deleteEvent(String day) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, TimeTableFragment.newInstance(day),
                        "TimeTableFragment")
                .commit();
    }

    @Override
    public void timetable() {
        getSupportFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.fragmentContainer, TimeTableFragment.newInstance(null),
                        "TimeTableFragment")
                .commit();
    }

    @Override
    public void toEdit(Event event) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, EditEventFragment.newInstance(event),
                        "EditEventFragment")
                .commit();
    }
}