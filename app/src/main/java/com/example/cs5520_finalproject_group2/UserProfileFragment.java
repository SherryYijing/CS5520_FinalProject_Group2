package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView userName;
    private Button myTimeTableButton, logoutButton;
    private fromUserProfileFragment mListener;
    private ImageButton userProfilePhoto;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        userName = view.findViewById(R.id.userNameTextView);
        logoutButton = view.findViewById(R.id.logoutButton);
        userProfilePhoto = view.findViewById(R.id.userProfilePhoto);
        myTimeTableButton = view.findViewById(R.id.timeTableButton);

        userName.setText(firebaseAuth.getCurrentUser().getDisplayName());

        if(firebaseAuth.getCurrentUser().getPhotoUrl() != null){
            storage.getReference().child("images/" + firebaseAuth.getCurrentUser().getPhotoUrl()
                            .getLastPathSegment())
                    .getDownloadUrl()
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Glide.with(view)
                                    .load(task.getResult())
                                    .apply(new RequestOptions().override(200, 200))
                                    .centerCrop()
                                    .error(R.drawable.select_avatar)
                                    .into(userProfilePhoto);
                        }
                    });
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        myTimeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.timetable();
//                Intent map = new Intent(getActivity(), MapActivity.class);
//                startActivity(map);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof fromUserProfileFragment) {
            mListener = (fromUserProfileFragment) context;
        } else {
            throw new RuntimeException(context + " must implement interface!");
        }
    }

    public interface fromUserProfileFragment{
        void logout();
        void timetable();
    }
}