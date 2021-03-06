package app.karacal.dagger;

import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.activities.AddEditTourActivity;
import app.karacal.activities.ApplyPrivacyPolicyActivity;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CommentsActivity;
import app.karacal.activities.GuideDashboardActivity;
import app.karacal.activities.DonateActivity;
import app.karacal.activities.EditAudioActivity;
import app.karacal.activities.FollowMyListeningsActivity;
import app.karacal.activities.LoginActivity;
import app.karacal.activities.PaymentMethodActivity;
import app.karacal.activities.PermissionActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.activities.ReferFriendActivity;
import app.karacal.activities.SettingsActivity;
import app.karacal.activities.SubscriptionActivity;
import app.karacal.fragments.DashboardTourItemFragment;
import app.karacal.fragments.EmailLoginFragment;
import app.karacal.fragments.LoginTypeSelectFragment;
import app.karacal.fragments.MainHomeFragment;
import app.karacal.fragments.MainLocationFragment;
import app.karacal.fragments.MainMenuFragment;
import app.karacal.fragments.MainSearchFragment;
import app.karacal.fragments.PaymentMethodAddFragment;
import app.karacal.fragments.PaymentMethodListFragment;
import app.karacal.fragments.RegistrationContactsFragment;
import app.karacal.fragments.RegistrationInterestsFragment;
import app.karacal.service.NotificationJobService;
import app.karacal.viewmodels.AudioActivityViewModel;
import app.karacal.viewmodels.CategoryActivityViewModel;
import app.karacal.viewmodels.CommentsActivityViewModel;
import app.karacal.viewmodels.DashboardActivityViewModel;
import app.karacal.viewmodels.AddEditTourActivityViewModel;
import app.karacal.viewmodels.EditAudioViewModel;
import app.karacal.viewmodels.MainActivityViewModel;
import app.karacal.viewmodels.PaymentMethodViewModel;
import app.karacal.viewmodels.ProfileActivityViewModel;
import app.karacal.viewmodels.SubscriptionActivityViewModel;
import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DatabaseModule.class})
public interface AppComponent {

    void inject(PermissionActivity permissionActivity);

    void inject(ApplyPrivacyPolicyActivity activity);

    void inject(LoginActivity loginActivity);

    void inject(ProfileActivity activity);

    void inject(ReferFriendActivity activity);

    void inject(SettingsActivity activity);

    void inject(FollowMyListeningsActivity activity);

    void inject(AddEditTourActivity activity);

    void inject(GuideDashboardActivity activity);

    void inject(EditAudioActivity activity);

    void inject(AudioActivity activity);

    void inject(CommentsActivity activity);

    void inject(PaymentMethodActivity activity);

    void inject(DonateActivity activity);

    void inject(SubscriptionActivity activity);

    void inject(LoginTypeSelectFragment fragment);

    void inject(EmailLoginFragment fragment);

    void inject(RegistrationContactsFragment fragment);

    void inject(RegistrationInterestsFragment fragment);

    void inject(MainHomeFragment fragment);

    void inject(MainMenuFragment fragment);

    void inject(MainLocationFragment fragment);

    void inject(MainSearchFragment fragment);

    void inject(DashboardTourItemFragment fragment);

    void inject(PaymentMethodListFragment fragment);

    void inject(PaymentMethodAddFragment fragment);

    void inject (MainActivityViewModel viewModel);

    void inject(CategoryActivityViewModel viewModel);

    void inject (AudioActivityViewModel viewModel);

    void inject (AddEditTourActivityViewModel viewModel);

    void inject (DashboardActivityViewModel viewModel);

    void inject (CommentsActivityViewModel viewModel);

    void inject (ProfileActivityViewModel viewModel);

    void inject (EditAudioViewModel viewModel);

    void inject (PaymentMethodViewModel viewModel);

    void inject (SubscriptionActivityViewModel viewModel);

    void inject (NotificationJobService notificationJobService);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(App application);

        AppComponent build();
    }
}
