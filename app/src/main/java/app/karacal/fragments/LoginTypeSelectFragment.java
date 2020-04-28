package app.karacal.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import app.karacal.R;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.LogFragment;
import apps.in.android_logger.Logger;

public class LoginTypeSelectFragment extends LogFragment {

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1703;

    private GoogleSignInClient googleSignInClient;
    private LoginManager facebookLoginManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_type_select, container, false);
        setupFacebookButton(view);
        setupGoogleButton(view);
        setupEmailButton(view);
        setupTextViews(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFacebookLogin();
        setupGoogleLogin();
    }

    private void setupGoogleLogin() {
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void setupFacebookLogin() {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        facebookLoginManager = LoginManager.getInstance();
        facebookLoginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        processFacebookLoginResult(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Logger.log(LoginTypeSelectFragment.this, "Facebook login canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Logger.log(LoginTypeSelectFragment.this, "Facebook login error", exception);
                    }
                });
    }

    private void setupFacebookButton(View view) {
        Button button = view.findViewById(R.id.buttonConnectFacebook);
        button.setOnClickListener(v -> {
            facebookLoginManager.logIn(this, Arrays.asList("public_profile", "email"));
        });
    }

    private void setupGoogleButton(View view) {
        Button button = view.findViewById(R.id.buttonConnectGoogle);
        button.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        });
    }

    private void setupEmailButton(View view) {
        Button button = view.findViewById(R.id.buttonConnectEmail);
        button.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.emailLoginFragment);
        });
    }

    private void setupTextViews(View view) {
        View.OnClickListener linkClickListener = v -> {
            NavigationHelper.startPrivacyPolicyActivity(getActivity());
        };
        TextView textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        setupLink(textViewPrivacyPolicy, linkClickListener);
        TextView textViewTermsOfService = view.findViewById(R.id.textViewTermsOfService);
        setupLink(textViewTermsOfService, linkClickListener);
    }

    private void setupLink(TextView textView, View.OnClickListener listener) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setOnClickListener(listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            processGoogleLoginResult(task);
        }
    }

    private void processGoogleLoginResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //TODO implement
            Logger.log(LoginTypeSelectFragment.this, "Google login success");
            NavigationHelper.startMainActivity(getActivity());
        } catch (ApiException e) {
            Logger.log(LoginTypeSelectFragment.this, "Google login failed, status code - " + e.getStatusCode(), e);
        }
    }


    private void processFacebookLoginResult(LoginResult loginResult) {
        if (loginResult.getAccessToken() != null) {
            //TODO implement
            Logger.log(LoginTypeSelectFragment.this, "Facebook login success");
            NavigationHelper.startMainActivity(getActivity());
        } else {
            Logger.log(LoginTypeSelectFragment.this, "Facebook login failed");
        }
    }
}
