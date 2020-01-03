package com.example.learnjapanese.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnjapanese.ActivityLearningOptions;
import com.example.learnjapanese.MainActivity;
import com.example.learnjapanese.R;
import com.example.learnjapanese.RegisterActivity;
import com.example.learnjapanese.ThemeSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {

    private Button buttonLogin;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private TextView textViewRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        super.onViewCreated(view, savedInstanceState);
        buttonLogin = view.findViewById(R.id.buttonAutorize);
        editTextLogin = view.findViewById(R.id.editTextLogin);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        textViewRegister = view.findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in,R.menu.right_slide_out);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eMail = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                progressDialog.setMessage("Регистрация пользователя");
                progressDialog.setCancelable(false);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(eMail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                        }
                    }
                });
            }
        });

    }
}
