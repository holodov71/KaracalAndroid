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
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.NavigationHelper;
import app.karacal.network.models.request.SocialLoginRequest;
import apps.in.android_logger.LogFragment;
import apps.in.android_logger.Logger;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginTypeSelectFragment extends LogFragment {

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1703;

    private GoogleSignInClient googleSignInClient;

    private LoginManager facebookLoginManager;
    private CallbackManager callbackManager;

    private Disposable loginDisposable;

    @Inject
    ApiHelper apiHelper;

    private View progressLoading;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_type_select, container, false);
        setupFacebookButton(view);
        setupGoogleButton(view);
        setupEmailButton(view);
        setupTextViews(view);
        setupProgress(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
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
        callbackManager = CallbackManager.Factory.create();
        facebookLoginManager = LoginManager.getInstance();
        facebookLoginManager
                .setLoginBehavior(LoginBehavior.WEB_ONLY)
                .registerCallback(callbackManager,
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
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if(accessToken != null) {
                parseFacebookProfile(accessToken);
            } else {
                callFacebookAuth();
            }
        });
    }

    private void callFacebookAuth(){
        facebookLoginManager.logIn(this, Arrays.asList("public_profile", "email"));
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
        TextView textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        setupLink(textViewPrivacyPolicy, v -> {
            NavigationHelper.startPolitiqueProtectionActivity(getActivity());
        });
        TextView textViewTermsOfService = view.findViewById(R.id.textViewTermsOfService);
        setupLink(textViewTermsOfService, v -> {
            NavigationHelper.startPrivacyPolicyActivity(getActivity());
        });
    }

    private void setupLink(TextView textView, View.OnClickListener listener) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setOnClickListener(listener);
    }

    private void setupProgress(View view) {
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            processGoogleLoginResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processGoogleLoginResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                String firstName = account.getGivenName();
                String secondName = account.getFamilyName();
                String email = account.getEmail();
                String personId = account.getId();
                makeServerLogin(personId, firstName, secondName, email);
            } else {
                ToastHelper.showToast(getContext(), getString(R.string.google_sign_in_error));
            }
        } catch (ApiException e) {
            ToastHelper.showToast(getContext(), getString(R.string.google_sign_in_error));
            Logger.log(LoginTypeSelectFragment.this, "Google login failed, status code - " + e.getStatusCode(), e);
        }
    }


    private void processFacebookLoginResult(LoginResult loginResult) {
        if (loginResult.getAccessToken() != null) {
            parseFacebookProfile(loginResult.getAccessToken());
        } else {
            ToastHelper.showToast(getContext(), getString(R.string.facebook_sign_in_error));
            Logger.log(LoginTypeSelectFragment.this, "Facebook login failed");
        }
    }

    private void parseFacebookProfile(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                (object, response) -> {
                    try {
                        String id = object.getString("id");
                        String firstName = object.getString("first_name");
                        String lastName = object.getString("last_name");
                        String email = object.getString("email");

                        makeServerLogin(id, firstName, lastName, email);

                    } catch (Exception e) {
                        ToastHelper.showToast(getContext(), getString(R.string.facebook_sign_in_error));
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void makeServerLogin(String userId, String firstName, String secondName, String email) {
        if (loginDisposable == null) {
            progressLoading.setVisibility(View.VISIBLE);
            SocialLoginRequest loginRequest = new SocialLoginRequest(userId, firstName, secondName, email);

            loginDisposable = apiHelper.socialLogin(loginRequest).flatMap(apiHelper::getProfile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(profile -> {
                        FragmentActivity activity = getActivity();
                        if (activity != null) {
                            ProfileCache.getInstance(activity).setProfile(activity, profile);
                            NavigationHelper.startMainActivity(getActivity());
                            activity.finish();
                        }
                        loginDisposable = null;
                    }, throwable -> {
                        progressLoading.setVisibility(View.GONE);
                        ToastHelper.showToast(getContext(), throwable.getMessage());
                        loginDisposable = null;
                    });
        }
    }
}
