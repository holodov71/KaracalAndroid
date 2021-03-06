package app.karacal.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Profile;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.LogActivity;
import apps.in.android_logger.Logger;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends LogActivity {

    @Inject
    ApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_login);
        ImageView splashView = findViewById(R.id.gifView);
        Glide.with(this).load(R.raw.login_gif).into(splashView);
        tryToAuthorize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("CheckResult")
    private void tryToAuthorize(){
        String token = PreferenceHelper.loadToken(this);
        Logger.log(this, "token = "+token);
        if (token != null){
            Log.v("this", "tryToAuthorize token = "+token);

            Profile mProfile = ProfileCache.getInstance(this).getProfile();
            if (mProfile != null){
                NavigationHelper.startMainActivity(this);
                finish();
            } else {

                apiHelper.getProfile(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(profile -> {
                            ProfileCache.getInstance(this).setProfile(this, profile);
                            NavigationHelper.startMainActivity(this);
                            finish();
                        }, throwable -> {
                            PreferenceHelper.saveToken(null);
                            proceedToLogin();
                        });
            }

        } else {
            proceedToLogin();
        }
    }

    private void proceedToLogin(){
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentHostView);
        fragmentContainerView.setVisibility(View.VISIBLE);
    }
}
