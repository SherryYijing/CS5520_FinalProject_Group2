package com.example.cs5520_finalproject_group2.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs5520_finalproject_group2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPageFragment extends Fragment {
    private EditText loginEmail, loginPassword;
    private TextView goToRegisterTextView;
    private Button buttonLogin;
    private FirebaseAuth firebaseAuth;
    private fromLoginPageFragment mListener;
    public LoginPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginPageFragment newInstance() {
        LoginPageFragment fragment = new LoginPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);

        getActivity().setTitle("Login");

        loginEmail = view.findViewById(R.id.loginEmailAddress);
        loginPassword = view.findViewById(R.id.loginPassword);
        goToRegisterTextView = view.findViewById(R.id.goToRegisterTextView);
        buttonLogin = view.findViewById(R.id.loginButton);
        firebaseAuth = FirebaseAuth.getInstance();

        goToRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fromLoginPageToRegisterPage();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getContext(), "The input email and password cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getContext(), "Login Successfully!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), ""+e.getMessage().toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mListener.clickLoginButton(firebaseAuth.getCurrentUser());
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof fromLoginPageFragment)
        {
            mListener = (fromLoginPageFragment) context;
        }
        else{
            throw new RuntimeException(context + " must implement interface!");
        }
    }

    public interface fromLoginPageFragment {
        void fromLoginPageToRegisterPage();
        void clickLoginButton(FirebaseUser firebaseUser);
    }
}