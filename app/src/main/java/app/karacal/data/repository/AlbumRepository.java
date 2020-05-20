package app.karacal.data.repository;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.R;
import app.karacal.models.Album;
import app.karacal.models.Track;

@Singleton
public class AlbumRepository {

    @Inject
    public AlbumRepository(){
    }

    public Album getAlbumByTourId(int tourId){
        ArrayList<Track> tracks = new ArrayList<>();

        tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));

        switch (tourId){
            case 1:
                tracks.add(new Track("Larue Daguerresousloeild Agns Varda", R.raw.track_10_1));
                break;
            case 2:
                tracks.add(new Track("Le Procope", R.raw.track_11_1));
                break;
            case 3:
                tracks.add(new Track("Lhopital Saint-Louis", R.raw.track_12_1));
                break;
            case 4:
                tracks.add(new Track("Mtro Bastille", R.raw.track_13_1));
                break;
            case 5:
                tracks.add(new Track("Muse Soulages", R.raw.track_14_1));
                break;
            case 6:
                tracks.add(new Track("Colonnede Juillet", R.raw.track_15_1));
                break;
//            case 7:
//                tracks.add(new Track("Musée d'Orsay", R.raw.track_16_1));
//                break;
            case 8:
                tracks.add(new Track("La place des Terreaux", R.raw.track_17_1));
                break;
            case 9:
                tracks.add(new Track("Space Invaders", R.raw.track_18_1));
                break;
            case 10:
                tracks.add(new Track("Le Parc Monceau Intgral", R.raw.track_19_1));
                break;
            case 11:
                tracks.add(new Track("Place Saint Georges", R.raw.track_20_1));
                break;
            case 12:
                tracks.add(new Track("La Fontaine Stravinsky", R.raw.track_21_1));
                break;
            case 13:
                tracks.add(new Track("1.Introduction", R.raw.track_22_1));
                tracks.add(new Track("2.Historique", R.raw.track_22_2));
                tracks.add(new Track("3.L'ingrédient secret", R.raw.track_22_3));
                tracks.add(new Track("4.Conseils de visite et conclusion", R.raw.track_22_4));
                break;
//            case 14:
//                tracks.add(new Track("Tombeau de Philippe Pot chef doeuvre funraire du XVe", R.raw.track_23_1));
//                break;
            case 15:
                tracks.add(new Track("La redoutable allgorie de la Mort", R.raw.track_24_1));
                break;
            case 16:
                tracks.add(new Track("Un Fascinant Mannequin Vaudou", R.raw.track_25_1));
                break;
            case 17:
                tracks.add(new Track("Le Château Gaillard", R.raw.track_26_1));
                break;
            case 18:
                tracks.add(new Track("Le corps imputrescible de Ste Madeleine-Sophie Barat", R.raw.track_27_1));
                break;
            case 19:
                tracks.add(new Track("Les Blousons noirs du square Saint-Lambert", R.raw.track_28_1));
                break;
            case 20:
                tracks.add(new Track("Les adresses gourmandes du XIIIe", R.raw.track_29_1));
                break;
            case 21:
                tracks.add(new Track("Les arnes de Lutce", R.raw.track_30_1));
                tracks.add(new Track("Le plan de Lutce", R.raw.track_30_2));
                break;
            case 22:
                tracks.add(new Track("Le Palais Bourbon", R.raw.track_31_1));
                break;
            case 23:
                tracks.add(new Track("Le Canal Saint Martin", R.raw.track_32_1));
                break;
            case 24:
                tracks.add(new Track("Ici naquit Édith Piaf", R.raw.track_33_1));
                break;
            case 25:
                tracks.add(new Track("Comment Paris de vient Paris", R.raw.track_34_1));
                break;
            case 26:
                tracks.add(new Track("Le muse Cernuschi", R.raw.track_35_1));
                break;
            case 27:
                tracks.add(new Track("Grand Palais intrieur", R.raw.track_36_1));
                break;
            case 28:
                tracks.add(new Track("Palais Galliera", R.raw.track_37_1));
                break;
            case 29:
                tracks.add(new Track("Arc de Triomphe", R.raw.track_38_1));
                break;
            case 30:
                tracks.add(new Track("Louvre", R.raw.track_39_1));
                break;
            case 31:
                tracks.add(new Track("Barbier et patissier", R.raw.track_40_1));
                break;
            case 32:
                tracks.add(new Track("Pont Mirabeau intrieur", R.raw.track_41_1));
                break;
        }

        tracks.add(new Track("Abientt", R.raw.track_abientt));

        return new Album("Title of the Audio Album", tracks);
    }


}
