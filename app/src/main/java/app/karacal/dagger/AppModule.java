package app.karacal.dagger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Date;

import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.models.Track;
import app.karacal.retrofit.CommentsService;
import app.karacal.retrofit.DateTypeAdapter;
import app.karacal.retrofit.GuideService;
import app.karacal.retrofit.InitService;
import app.karacal.retrofit.ProfileService;
import app.karacal.retrofit.StripeService;
import app.karacal.retrofit.TourService;
import app.karacal.retrofit.TracksService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    @Provides
    @Singleton
    public Retrofit retrofitProvider(App context){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return new Retrofit.Builder()
                .baseUrl(App.getApiBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();
    }

    @Provides
    public InitService initService(Retrofit retrofit ){
        return retrofit.create(InitService.class);
    }

    @Provides
    public ProfileService profileService(Retrofit retrofit){
        return retrofit.create(ProfileService.class);
    }

    @Provides
    public GuideService guideService(Retrofit retrofit){
        return retrofit.create(GuideService.class);
    }

    @Provides
    public TourService tourService(Retrofit retrofit){
        return retrofit.create(TourService.class);
    }

    @Provides
    public TracksService stripeService(Retrofit retrofit){
        return retrofit.create(TracksService.class);
    }

    @Provides
    public StripeService tracksService(Retrofit retrofit){
        return retrofit.create(StripeService.class);
    }

    @Provides
    public CommentsService commentsService(Retrofit retrofit){
        return retrofit.create(CommentsService.class);
    }

}
