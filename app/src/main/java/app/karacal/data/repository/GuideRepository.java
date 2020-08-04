package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Guide;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static app.karacal.App.TAG;

@Singleton
public class GuideRepository {

    @Inject
    ApiHelper apiHelper;

    private Context context;

    public MutableLiveData<List<Guide>> guidesLiveData = new MutableLiveData<>();

    @Inject
    public GuideRepository(App context){
        this.context = context;
    }

    public Guide getGuide(int id){
        if (guidesLiveData.getValue() != null){
            for(Guide guide: guidesLiveData.getValue()){
                if (guide.getId() == id){
                    return guide;
                }
            }
        }
        return null;
    }

    public Guide searchGuide(String query){
        if (guidesLiveData.getValue() != null){
            for(Guide guide: guidesLiveData.getValue()){
                String name = guide.getName().toLowerCase();
                if (name.contains(query.toLowerCase())){
                    return guide;
                }
            }
        }
        return null;
    }

    @SuppressLint("CheckResult")
    public void loadGuides() {
        if (guidesLiveData.getValue() == null || guidesLiveData.getValue().isEmpty()) {
            apiHelper.loadGuides(PreferenceHelper.loadToken(context))
                    .flatMapIterable(list -> list)
                    .map(Guide::new)
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(guides -> {
                        guidesLiveData.setValue(guides);
                        // TODO: save to DB

                    }, throwable -> {
                        // TODO: load list from DB
                    });
        }
    }




}
