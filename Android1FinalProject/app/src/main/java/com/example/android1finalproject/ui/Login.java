package com.example.android1finalproject.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.example.android1finalproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Fragment {

    private FirebaseAuth auth;
    private EditText emailT;
    private EditText passwordT;

    public Login() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        auth = FirebaseAuth.getInstance();
        emailT = v.findViewById(R.id.email_login);
        passwordT = v.findViewById(R.id.password_login);

        v.findViewById(R.id.register_btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(v).navigate(R.id.action_login_to_register);
            }
        });

        v.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailT.getText().toString();
                String password = passwordT.getText().toString();

                // Call the authenticate method
                authenticate(email, password);
            }
        });

        return v;
    }

    // Move the authentication logic here
    private void authenticate(String email, String password) {
        if (isValidLoginData(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            handleAuthenticationSuccess();
                        } else {
                            handleAuthenticationFailure();
                        }
                    });
        }
    }

    private boolean isValidLoginData(String email, String password) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }

    private void handleAuthenticationSuccess() {
        Toast.makeText(requireContext(), getString(R.string.Login_successful), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_login_to_category);
    }

    private void handleAuthenticationFailure() {
        Toast.makeText(requireContext(), getString(R.string.Login_unsuccessful), Toast.LENGTH_SHORT).show();
    }

}