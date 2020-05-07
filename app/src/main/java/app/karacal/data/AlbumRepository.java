package app.karacal.data;

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
        switch (tourId){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                tracks.add(new Track("Introduction", R.raw.test_1));
                tracks.add(new Track("Historical meaning", R.raw.test_2));
                tracks.add(new Track("First appearance", R.raw.test_3));
                tracks.add(new Track("Clarity of perception", R.raw.test_4));
                tracks.add(new Track("International relationships", R.raw.test_5));
                tracks.add(new Track("International relationships", R.raw.test_6));
                tracks.add(new Track("International relationships", R.raw.test_7));
                tracks.add(new Track("International relationships", R.raw.test_8));
                tracks.add(new Track("International relationships", R.raw.test_9));
                tracks.add(new Track("International relationships", R.raw.test_10));
                break;
            case 10:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Larue Daguerresousloeild Agns Varda", R.raw.track_10_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 11:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le Procope", R.raw.track_11_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 12:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Lhopital Saint-Louis", R.raw.track_12_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 13:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Mtro Bastille", R.raw.track_13_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 14:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Muse Soulages", R.raw.track_14_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 15:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Colonnede Juillet", R.raw.track_15_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 16:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Musée d'Orsay", R.raw.track_16_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 17:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("La place des Terreaux", R.raw.track_17_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 18:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Space Invaders", R.raw.track_18_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 19:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le Parc Monceau Intgral", R.raw.track_19_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 20:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Place Saint Georges", R.raw.track_20_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 21:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("La Fontaine Stravinsky", R.raw.track_21_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 22:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("1.Introduction", R.raw.track_22_1));
                tracks.add(new Track("2.Historique", R.raw.track_22_2));
                tracks.add(new Track("3.L'ingrédient secret", R.raw.track_22_3));
                tracks.add(new Track("4.Conseils de visite et conclusion", R.raw.track_22_4));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 23:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Tombeau de Philippe Pot chef doeuvre funraire du XVe", R.raw.track_23_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 24:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("La redoutable allgorie de la Mort", R.raw.track_24_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 25:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Un Fascinant Mannequin Vaudou", R.raw.track_25_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 26:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le Château Gaillard", R.raw.track_26_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 27:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le corps imputrescible de Ste Madeleine-Sophie Barat", R.raw.track_27_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 28:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Les Blousons noirs du square Saint-Lambert", R.raw.track_28_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 29:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Les adresses gourmandes du XIIIe", R.raw.track_29_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 30:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Les arnes de Lutce", R.raw.track_30_1));
                tracks.add(new Track("Le plan de Lutce", R.raw.track_30_2));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 31:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le Palais Bourbon", R.raw.track_31_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 32:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le Canal Saint Martin", R.raw.track_32_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 33:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Ici naquit Édith Piaf", R.raw.track_33_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 34:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Comment Paris de vient Paris", R.raw.track_34_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 35:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Le muse Cernuschi", R.raw.track_35_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 36:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Grand Palais intrieur", R.raw.track_36_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 37:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Palais Galliera", R.raw.track_37_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 38:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Arc de Triomphe", R.raw.track_38_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 39:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Louvre", R.raw.track_39_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 40:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Barbier et patissier", R.raw.track_40_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
            case 41:
                tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
                tracks.add(new Track("Pont Mirabeau intrieur", R.raw.track_41_1));
                tracks.add(new Track("Abientt", R.raw.track_abientt));
                break;
        }
        return new Album("Title of the Audio Album", tracks);
    }


}
