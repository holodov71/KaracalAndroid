package app.karacal.data.repository;

import android.content.Context;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Track;
import app.karacal.network.models.response.UploadTrackResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class AlbumRepository {

    @Inject
    ApiHelper apiHelper;

    private Context context;

    @Inject
    public AlbumRepository(App context){
        this.context = context;
    }

    public Single<List<UploadTrackResponse>> uploadAudioToServer(String guideId, String tourId, List<Track> tracks){
        return Observable.fromIterable(tracks)
                .flatMap(track -> apiHelper.uploadAudioToServer(PreferenceHelper.loadToken(context), guideId, tourId, track))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
