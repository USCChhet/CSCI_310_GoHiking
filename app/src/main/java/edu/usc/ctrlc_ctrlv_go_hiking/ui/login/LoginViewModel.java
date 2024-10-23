package edu.usc.ctrlc_ctrlv_go_hiking.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private final FirebaseAuth auth;  // FirebaseAuth instance
    private final MutableLiveData<FirebaseUser> userLiveData;  // Observes current user state
    private final MutableLiveData<String> errorMessage;  // Observes error messages

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
        userLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();

        // Set the current user if already logged in
        if (auth.getCurrentUser() != null) {
            userLiveData.setValue(auth.getCurrentUser());
        }
    }

    // Getter for Firebase user data
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    // Getter for error messages
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Method to sign out the user
    public void signOut() {
        auth.signOut();
        userLiveData.setValue(null);  // Set user to null on sign-out
    }

    // Method to update the user state after a login attempt
    public void updateUserState(FirebaseUser user) {
        if (user != null) {
            userLiveData.setValue(user);  // User is successfully logged in
        } else {
            errorMessage.setValue("Login failed. Please try again.");  // Set error message
        }
    }
}
