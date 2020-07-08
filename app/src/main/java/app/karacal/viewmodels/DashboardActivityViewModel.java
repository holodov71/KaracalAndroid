package app.karacal.viewmodels;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.Profile;

public class DashboardActivityViewModel extends ViewModel {

    @Inject
    ProfileHolder profileHolder;

    public DashboardActivityViewModel() {
        App.getAppComponent().inject(this);
    }

    public Profile getProfile(){
        return profileHolder.getProfile();
    }




}
