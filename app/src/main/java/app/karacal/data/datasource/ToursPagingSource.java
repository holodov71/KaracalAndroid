package app.karacal.data.datasource;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Tour;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ToursPagingSource
//        extends PositionalDataSource<Tour>
{

//    @Inject
//    private ApiHelper apiHelper;
//
//    @Inject
//    private TourRepository tourRepository;



//    public ToursPagingSource(List<Tour> tours) {
//    }
//
//    @SuppressLint("CheckResult")
//    @Override
//    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Tour> callback) {
//        loadTours().subscribe(tours -> {
//                    Log.v("loadTours", "loadInitial = "+tours.size());
//                    tours.get(tours.size() - 1).setPrice(999);
//                    callback.onResult(tours.subList(0, 20), 0);
//
//                }, throwable -> {
//                    Log.v("loadTours", "throwable "+throwable.getMessage());
//                    // TODO: load list from DB
//                });
//    }
//
//    @SuppressLint("CheckResult")
//    @Override
//    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Tour> callback) {
//        loadTours().subscribe(tours -> {
//            Log.v("loadTours", "loadInitial = "+tours.size());
//            tours.get(tours.size() - 1).setPrice(999);
//            callback.onResult(tours.subList(params.startPosition, params.startPosition + params.loadSize));
//
//        }, throwable -> {
//            Log.v("loadTours", "throwable "+throwable.getMessage());
//            // TODO: load list from DB
//        });
//    }
//
//    private Single<List<Tour>> loadTours(){
//        String token = PreferenceHelper.loadToken(App.getInstance());
//
//        return apiHelper.loadPagedContents("Bearer " + token)
//                .flatMapIterable(list -> list)
//                .map(Tour::new)
//                .toList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

//
//    @NotNull
//    @Override
//    public Single<LoadResult<Integer, Tour>> loadSingle(@NotNull LoadParams<Integer> params) {
//        // Start refresh at page 1 if undefined.
//        Integer nextPageNumber = params.getKey();
//        if (nextPageNumber == null) {
//            nextPageNumber = 1;
//        }
//
//        String token = PreferenceHelper.loadToken(App.getInstance());
//
//        return mBackend.getContentsPage("Bearer " + token)
//                .subscribeOn(Schedulers.io())
//                .map(this::toLoadResult)
//                .onErrorReturn(LoadResult.Error::new);
//    }
//
//    private LoadResult<Integer, Tour> toLoadResult(
//            @NonNull List<ContentResponse> response) {
//
//        int nextPageNumber = 2;
//        List<Tour> tours = new ArrayList<>();
//        for(ContentResponse contentResponse: response){
//            tours.add(new Tour(contentResponse));
//        }
//
//        return new LoadResult.Page<>(
//                tours,
//                null, // Only paging forward.
//                nextPageNumber,
//                LoadResult.Page.COUNT_UNDEFINED,
//                LoadResult.Page.COUNT_UNDEFINED);
//    }
}
