package app.karacal.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import app.karacal.App;
import app.karacal.models.Album;
import app.karacal.models.Player;

public class EditAudioViewModel extends ViewModel {

    private final MutableLiveData<Album> album = new MutableLiveData<>();

    private final Player player;

    public EditAudioViewModel() {
        App.getAppComponent().inject(this);
        player = new Player(album);
    }

}
