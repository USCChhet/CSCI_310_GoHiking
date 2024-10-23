package edu.usc.ctrlc_ctrlv_go_hiking.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import edu.usc.ctrlc_ctrlv_go_hiking.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Initialize the ViewModel
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observe user state changes
        loginViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Log.d("LoginFragment", "User logged in: " + user.getEmail());
                // Navigate to the main screen or update UI accordingly
            } else {
                Log.d("LoginFragment", "User is signed out.");
            }
        });

        // Observe error messages
        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e("LoginFragment", error);
                // Display error to the user (e.g., via Toast or Snackbar)
            }
        });

        // Set click listener for the login button
        binding.buttonLogin.setOnClickListener(view -> launchSignInFlow());

        // Set click listener for the Create Account button
        binding.buttonCreateAccount.setOnClickListener(view -> showCreateAccountForm());

        // Set click listener for the Submit Create Account button
        binding.buttonSubmitCreateAccount.setOnClickListener(view -> createUser());

        return root;
    }


    private void showCreateAccountForm() {
        // Hide the button view and show the create account form
        binding.buttonContainer.setVisibility(View.GONE);
        binding.createAccountForm.setVisibility(View.VISIBLE);
    }

    private void createUser() {
        // Retrieve text from input fields using the binding object
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d("LoginFragment", "User created: " + user.getEmail());
                        Toast.makeText(getContext(), "Account created successfully.", Toast.LENGTH_SHORT).show();
                        // Optionally, navigate to the main screen or perform other actions here
                    } else {
                        Log.e("LoginFragment", "Error: " + task.getException().getMessage());
                        Toast.makeText(getContext(), "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void launchSignInFlow() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Log.d("LoginFragment", "User logged in: " + user.getEmail());
                        // Navigate to the main screen or update UI accordingly
                    }
                } else {

                    IdpResponse response = IdpResponse.fromResultIntent(result.getData());

                    if (response == null) {
                        Log.e("LoginFragment", "Sign-in canceled by user.");
                    } else {
                        Log.e("LoginFragment", "Error: " + response.getError().getMessage());
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
