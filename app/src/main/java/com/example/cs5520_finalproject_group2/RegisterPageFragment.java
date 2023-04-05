package com.example.cs5520_finalproject_group2;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cs5520_finalproject_group2.TakePhoto.PhotoMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGER = "imageUri";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private fromRegisterPageFragment mListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private String imageUri = null;
    private EditText registerFullName, registerEmail, registerPassword;
    private TextView goToLoginTextView;
    private ImageButton registerPhoto;
    private Button registerButton;

    public RegisterPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterPageFragment newInstance(String imageUri) {
        RegisterPageFragment fragment = new RegisterPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGER, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getString(ARG_IMAGER);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_page, container, false);

        goToLoginTextView = view.findViewById(R.id.goToLoginTextView);
        registerFullName = view.findViewById(R.id.registerPersonName);
        registerEmail = view.findViewById(R.id.registerEmailAddress);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerButton = view.findViewById(R.id.registerButton);
        registerPhoto = view.findViewById(R.id.userPhoto);

        registerPhoto.setImageResource(R.drawable.select_avatar);
        if (imageUri != null){
            storage.getReference().child("images/" + Uri.parse(imageUri)
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
                                    .into(registerPhoto);
                        }
                    });
        }
        goToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fromRegisterPageToLoginPage();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = registerFullName.getText().toString();
                String password = registerPassword.getText().toString();
                String email = registerEmail.getText().toString();
                
                if(fullName.isEmpty()){
                    Toast.makeText(getContext(), "The register full name cannot be empty!", 
                            Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getContext(), "The register password cannot be empty!", 
                            Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getContext(), "The register email cannot be empty!", 
                            Toast.LENGTH_SHORT).show();
                } else if (imageUri == null) {
                    Toast.makeText(getContext(), "You need to pick a photo to register!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser = task.getResult().getUser();
                                        setUpUserProfile(fullName);
                                        mListener.clickRegisterButton(firebaseUser);
                                    }
                                }
                            });
                }
            }
        });

        registerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pickProfilePhoto();
            }
        });

        return view;
    }

    public void setUpUserProfile(String fullName) {
        UserProfileChangeRequest userProfileChangeRequest
                = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .setPhotoUri(Uri.parse(imageUri))
                .build();

        firebaseUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mListener.clickRegisterButton(firebaseUser);
                        }
                    }
                });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromRegisterPageFragment)
        {
            mListener = (fromRegisterPageFragment) context;
        }
        else{
            throw new RuntimeException(context + " must implement interface!");
        }
    }

    public interface fromRegisterPageFragment {
        void pickProfilePhoto();
        void fromRegisterPageToLoginPage();
        void clickRegisterButton(FirebaseUser firebaseUser);
    }
}