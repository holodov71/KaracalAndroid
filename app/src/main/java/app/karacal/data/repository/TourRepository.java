package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Guide;
import app.karacal.models.Tour;
import app.karacal.retrofit.models.request.NearToursRequest;
import app.karacal.retrofit.models.request.SaveTourRequest;
import app.karacal.retrofit.models.response.BaseResponse;
import app.karacal.retrofit.models.response.SaveTourResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class TourRepository {

    @Inject
    ApiHelper apiHelper;

    private ToursDao toursDao;
    private Context context;

//    private final ArrayList<Tour> tours = new ArrayList<>();
    private final MutableLiveData<ArrayList<Tour>> toursList = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> toursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> recommendedToursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> originalToursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> nearToursLiveData = new MutableLiveData<>();
    private final ArrayList<Tour> nearTours = new ArrayList<>();

    @Inject
    public TourRepository(ToursDao toursDao, App context) {

        this.toursDao = toursDao;
        this.context = context;

//        tours.add(new Tour(1, R.mipmap.tour_image_varda,
//                "La rue Daguerre sous l'oeil d'Agnès Varda",
//                "Plongez dans le Paris d'Agnès Varda avec Orianne, l'autre voix d'Ici Ta Voix. Elle vous fera découvrir la rue Daguerre et son histoire, dans les pas de la célèbre artiste française.",
//                null, 10, 7, 10, 48.836139, 2.323694));
//
//        tours.add(new Tour(2, R.mipmap.tour_image_procope,
//                "Le Procope",
//                "Découvrez le plus ancien restaurant de Paris encore en activité avec Lydia. De nombreux personnages historiques sont passés entre ses murs, à l'instar des philosophes des Lumières (Voltaire, Rousseau), des révolutionnaires (Danton, Marat) ou encore des hommes politiques (Napoléon Bonaparte, Gambetta)." +
//                        "\n" +
//                        "Cette visite commence devant l'entrée secondaire du Procope, rue du commerce Saint-André.",
//                null, 10, 5, 11, 48.853222, 2.339056));
//
//        tours.add(new Tour(3, R.mipmap.tour_image_jardins_hopital,
//                "Les jardins de l'hôpital Saint-Louis",
//                "Suivez-moi dans les jardins calmes et paisibles de l'Hôpital Saint Louis et découvrez ses secrets !",
//                null, 10, 5, 6, 48.873472, 2.369861));
//
//        tours.add(new Tour(4, R.mipmap.tour_image_bastille,
//                "Métro Bastille",
//                "Découvrez Paris par ses bouches de métro ! Un audio à écouter sur le quai ou sur la place, en fonction de votre préférence.",
//                null, 10, 5, 13, 48.853306, 2.369111));
//
//        tours.add(new Tour(5, R.mipmap.tour_image_soulages,
//                "Musée Soulages",
//                "Avant votre visite ou si vous n'avez pas le temps de rentrer dans le musée, découvrez l'architecture et l'aménagement de la Place ! Depuis le Jardin Public du Foirail, flânez dans les allées pour découvrir ce beau monument.",
//                null, 10, 8, 13, 44.351694, 2.568111));
//
//        tours.add(new Tour(6, R.mipmap.tour_image_colonne_de_juillet,
//                "La colonne de Juillet",
//                "Mais quelle est donc cette colonne, place de la Bastille ? Vous vous l'êtes souvent demandé mais vous n'avez jamais eu le temps de répondre à votre question ? Karacal vous le raconte !",
//                null, 10, 3, 13, 48.853306, 2.369111));

//        tours.add(new Tour(7, R.mipmap.tour_image_orsay,
//                "Musée d'Orsay - Episode 1 des \"Visites Guidées Privées\"",
//                "Découvrez l’histoire du Musée D’Orsay, le contexte de création du bâtiment, ses singularités architecturales et son évolution ! -> de 1900 à aujourd’hui ! Nous évoquerons le contexte des expositions universelles, les différentes vies du bâtiments et la sélection des oeuvres disponibles dans cet édifice." +
//                        "\n" +
//                        "Point de départ : 1 Rue de la Légion d'Honneur, 75007 Paris, Parvis principal de l'entrée du Musée. Cet audio peut tout à fait être écouté dans une file d'attente.",
//                null, 10, 8, 7, 48.86075, 2.325306));

//        tours.add(new Tour(8, R.mipmap.tour_image_terreaux,
//                "La place des Terreaux",
//                "Anouck vous propose une courte visite de la Place des Terreaux, l'occasion de découvrir son histoire et son passé.",
//                null, 10, 4, 3, 48.86075, 2.325306));
//
//        tours.add(new Tour(9, R.mipmap.tour_image_invaders,
//                "Space Invaders",
//                "Partez à la recherche de ces petites mosaïques du nom de \"Space Invaders\" avec Trendy. Premier d'une longue série, rendez-vous Place de la République !",
//                null, 10, 7, 8, 48.867861, 2.363972));
//
//        tours.add(new Tour(10, R.mipmap.tour_image_monceau,
//                "Le Parc Monceau",
//                "Flânez dans le Parc Monceau et découvrez son histoire avec Hanna !",
//                null, 10, 4, 13, 48.88025, 2.309056));
//
//        tours.add(new Tour(11, R.mipmap.tour_image_stgeor,
//                "La place Saint Georges",
//                "Bonjour je suis Clotilde. Je souhaite vous faire découvrir les dessous scandaleux des monuments de la place Saint Georges... Et oui, cette place n'est pas aussi classique qu'elle le paraît !",
//                null, 10, 6, 5, 48.879111, 2.337444));
//
//        tours.add(new Tour(12, R.mipmap.tour_image_stravinsky,
//                "La Fontaine Stravinsky",
//                "Découvrez la Fontaine Stravinsky, créée par les artistes Nouveaux Réalistes Nikki de Saint-Phalle et Jean Tinguely. Rendez-vous devant la Fontaine, à côté du Centre Pompidou sur la Place Igor Stravinsky.",
//                null, 10, 4, 4, 48.859444, 2.351389));
//
//        tours.add(new Tour(13, R.mipmap.tour_image_cuisine,
//                "L'ingrédient secret de la toile \"Intérieur d'une cuisine\"",
//                "Découvrez l'histoire rocambolesque d'un tableau qui cache un terrible secret sous son vernis de finition... L'expression \"mettre du cœur à l'ouvrage\", ça vous dit quelque chose ? Et bien vous allez voir que Martin Drölling est allé très loin dans l'illustration de ce vieil adage !\n" +
//                        "\n" +
//                        "Rendez-vous dans l'aîle Sully, au 2e étage dans la salle 938.",
//                null, 10, 6, 14, 48.860639, 2.337417));

//        tours.add(new Tour(14, R.mipmap.tour_image_philippe_pot,
//                "Le tombeau de Philippe Pot : un chef-d'oeuvre funéraire du XVe siècle",
//                "Les visiteurs du Louvre étaient en deuil car le plus beau gisant du musée était parti sous les pinceaux des restaurateurs... Maintenant qu'il est de retour, profitons-en pour admirer ensemble un tombeau spectaculaire qui a révolutionné l'art funéraire de la fin du XVe siècle !" +
//                        "\n" +
//                        "Vous trouverez cette oeuvre dans l'Aile Richelieu, au ez-de-chaussée et dans la salle 210.",
//                null, 10, 11, 14, 48.861583, 2.335861));

//        tours.add(new Tour(15, R.mipmap.tour_image_mort,
//                "La redoutable allégorie de la Mort",
//                "Comme des milliers de parisiens depuis 1530, venez frissonner au contact de la sublime \"Mort saint Innocent\" au Musée du Louvre.\n" +
//                        "\n" +
//                        "Vous trouverez cette oeuvre dans l'aîle Richelieu, \tau rez-de-chaussée, salle 212.",
//                null, 10, 9, 14, 48.862194, 2.336056));
//
//        tours.add(new Tour(16, R.mipmap.tour_image_vaudou,
//                "Un fascinant mannequin Vaudou",
//                "Le vaudou... Religion intrigante et magie mystérieuse qui alimente tous les fantasmes ! Mais parfois, la réalité est encore plus fascinante... Découvrons ensemble le plus bel objet vaudou des collections du Musée du Quai Branly.",
//                null, 10, 7, 14, 48.86125, 2.298139));
//
//        tours.add(new Tour(17, R.mipmap.tour_image_chteaugaillard,
//                "Le Château Gaillard",
//                "Découvrez avec moi ce château que j'affectionne tant !",
//                null, 10, 5, 14, 49.238028, 1.403444));
//
//        tours.add(new Tour(18, R.mipmap.tour_image_corps,
//                "Le corps imputrescible de Ste Madeleine-Sophie Barat",
//                "Un cadavre vieux de 150 ans toujours aussi frais qu'une rose, cela vous paraît impossible ? Les témoignages sur ce phénomène se sont pourtant multipliés depuis des siècles... Je vous propose de découvrir un cas jugé miraculeux par le Vatican, celui du corps imputrescible de Ste Madeleine-Sophie Barat.",
//                null, 10, 6, 14, 48.851111, 2.313917));
//
//        tours.add(new Tour(19, R.mipmap.tour_image_saint_lambert,
//                "Les blousons noirs du square Saint Lambert",
//                "Les Blousons noirs, ces jeunes plus ou moins voyous, sont nés le 23 juillet 1959 dans ce petit square du 15e arrondissement. Laissez-moi vous raconter cette histoire le temps de faire le tour du lieu.",
//                null, 10, 6, 16, 48.842611, 2.296694));
//
//        tours.add(new Tour(20, R.mipmap.tour_image_example_03,
//                "Les adresses gourmande du XIIIe arrondissement",
//                "Culture gourmande au programme !",
//                null, 10, 5, 17, 48.851111, 2.313917));

//        tours.add(new Tour(21, R.mipmap.tour_image_arnes,
//                "Arènes de Lutèce",
//                "Voici l'un des plus vieux vestiges de Paris ! Une courte ballade à faire seul ou en famille, un voyage dans le temps du Paris antique. Rendez-vous dans les Arènes.",
//                null, 10, 6, 13, 48.845278, 2.352889));
//
//        tours.add(new Tour(22, R.mipmap.tour_image_assemblee_nationale,
//                "Assemblée Nationale",
//                "Un grand bâtiment parisien dont vous avez forcément déjà entendu parler... mais en connaissez-vous l'histoire ? Venez la découvrir ! Rendez-vous devant le monument, devant les grilles ou sur le pont pour une meilleure vue.",
//                null, 10, 4, 13, 48.861, 2.318361));
//
//        tours.add(new Tour(23, R.mipmap.tour_image_canal_saint_martin,
//                "Canal Saint-Martin",
//                "Le Canal Saint-Martin est un incontournable point de fraîcheur lors des chaudes journées d'été. Moins touristique en hiver, il offre de belles couleurs en automne et redevient le coin favori des parisiens dès les premières sorties printanières. Débutez votre balade où vous le souhaitez et apprenez-en plus grâce à Karacal sur ce canal parisien.",
//                null, 10, 4, 13, 48.869306, 2.367083));
//
//        tours.add(new Tour(24, R.mipmap.tour_image_piaf,
//                "Edith Piaf est-elle née ici ?",
//                "Edith Piaf est sans conteste l'une des plus grandes voix de la chanson française... Mais ce fut aussi une femme exigeante et compliquée, dont la vie fut parfois tragique. \n" +
//                        "Retrouvons-nous ici pour parler un peu d'elle...",
//                null, 10, 4, 12, 48.8735, 2.38375));
//
//        tours.add(new Tour(25, R.mipmap.tour_image_devient_paris,
//                "Comment Paris devient Paris",
//                "Les villes n'apparaissent pas du jour au lendemain, tout le monde sait cela.\n" +
//                        "Mais ce que les gens imaginent moins c'est que fut un jour où Paris n'était qu'un petit village gaulois. (Non je ne ferais pas de blagues impliquant Astérix) En avant pour la visite !",
//                null, 10, 4, 12, 48.854917, 2.3475));
//
//        tours.add(new Tour(26, R.mipmap.tour_image_cernuschi,
//                "Musée Cernuschi",
//                "Avez-vous déjà eu l'idée de créer un musée dédié aux arts asiatiques ? \n" +
//                        "Si oui contactez-moi immédiatement. Si non vous pouvez découvrir dans ce podcast comment Henri Cernuschi finit par créer le musée qui porte son nom, dans cet hôtel particulier à deux pas du parc Monceau !",
//                null, 10, 5, 13, 48.879583, 2.312417));
//
//        tours.add(new Tour(27, R.mipmap.tour_image_grand_palais,
//                "Grand Palais",
//                "Voici mon premier podcast consacré aux œuvres de la République, sous forme d'une petite présentation du Grand Palais. J'espère qu'il vous plaira.\n" +
//                        "Je vous conseille d'écouter ce podcast en remontant l'avenue Winston Churchill un jour de grand soleil, l'expérience sera parfaite. Bien sûr vous pouvez aussi l'écouter sous la pluie mais ça sera moins sympa.",
//                null, 10, 3, 18, 48.866111, 2.313472));
//
//        tours.add(new Tour(28, R.mipmap.tour_image_palais_galliera,
//                "Palais Galliera",
//                "Comment j'en suis venu à parler du Palais Galliera ? C'est une histoire marrante que je ne vais pas vous raconter aujourd'hui.\n" +
//                        "Par contre je vais vous parler de la comtesse. La comtesse Galliera bien sûr. Il faut suivre un peu !",
//                null, 10, 3, 18, 48.865972, 2.296611));
//
//        tours.add(new Tour(29, R.mipmap.tour_image_arc_de_triomphe,
//                "Arc de Triomphe",
//                "J'ai toujours aimé l'Arc de Triomphe, solide et avec la place de l'Etoile qui tourne autour de lui.\n" +
//                        "Mais je ne m'étais jamais vraiment penché sur son histoire. C'est maintenant chose faite !",
//                null, 10, 3, 18, 48.873778, 2.295028));
//
//        tours.add(new Tour(30, R.mipmap.tour_image_louvre,
//                "Louvre",
//                "On parle toujours de ce qu'il y a dans le Louvre. Des égyptiens et de la Joconde principalement. Comme je n'aime pas faire les choses comme les autres je vais plutôt vous parler de l'extérieur du musée, et de l'histoire de ses bâtiments. Un peu moins de 4 minutes pour tout ça, je suis un mec efficace.",
//                null, 10, 4, 18, 48.860611, 2.337611));
//
//        tours.add(new Tour(31, R.mipmap.tour_image_barbier_criminels,
//                "Le barbier et le patissier criminels",
//                "Cela faisait longtemps que je n'avais pas fait de podcast. Cette fois je vais vous parler d'une histoire mi-glauque, mi-légendaire et re-mi-glauque. ça fait trois moitiés mais je m'en fiche c'est mon podcast je fais ce que je veux.",
//                null, 10, 4, 18, 48.835944, 2.350194));
//
//        tours.add(new Tour(32, R.mipmap.tour_image_pont_mirebeau,
//                "Le Pont Mirabeau",
//                "Dans la ligne 8 du métro, il y a depuis longtemps affiché quelques vers du poème \"Le Pont Mirabeau\" de Guillaume Apollinaire. Je crois que c'est ce qui m'a inspiré pour ce podcast !\n" +
//                        "Comme le titre l'indique ça va parler du pont Mirabeau. J'espère que ça va vous plaire, n'hésitez pas à me faire des retours en commentaire !",
//                null, 10, 2, 19, 48.846667, 2.275583));

    }

//    public ArrayList<Tour> getToursByCategoryId(int categoryId) {
//        switch (categoryId) {
//            case 0:
//                return getRecommendedTours();
//            case 1:
//                return getNearTours();
//            case 2:
//                return getOriginalTours();
//            default:
//                return new ArrayList<>();
//        }
//    }

//    public ArrayList<Tour> getRecommendedTours() {
//        ArrayList<Tour> result = new ArrayList<>();
//        result.add(tours.get(0));
//        result.add(tours.get(1));
//        result.add(tours.get(2));
//        return result;
//    }
//
//    public ArrayList<Tour> getNearTours() {
//        return nearTours;
//    }
//
//    public ArrayList<Tour> getNearTours(Location location) {
//        nearTours.clear();
//        for (Tour tour : tours) {
//            if (location.distanceTo(tour.getTourLocation()) <= 500){
//                nearTours.add(tour);
//            }
//        }
//        return nearTours;
//    }

//    public ArrayList<Tour> getOriginalTours() {
//        ArrayList<Tour> result = new ArrayList<>();
////        Temporary realization
//        for (int i = 0; i < tours.size(); i++){
//            if (i>2){
//                result.add(tours.get(i));
//            }
//        }
//        return result;
//    }

    public void getOriginalToursList() {
        toursDao.getAllTours().observeForever(new Observer<List<TourEntity>>() {
            @Override
            public void onChanged(List<TourEntity> tourEntities) {
                ArrayList<Tour> tours = new ArrayList<>();
                for (TourEntity tour: tourEntities){
                    tours.add(new Tour(tour));
                }
                toursList.setValue(tours);
            }
        });
    }

//    public ArrayList<Tour> getAllTours(){
//        return tours;
//    }

    public Tour getTourById(int tourId) {
        if (originalToursLiveData.getValue() != null){
            for (Tour tour : originalToursLiveData.getValue()) {
                if (tour.getId() == tourId) {
                    return tour;
                }
            }
        }
        return null;
    }

    public ArrayList<Tour> searchToursByText(String query) {
        ArrayList<Tour> filteredTours = new ArrayList<>();
        if(originalToursLiveData.getValue() != null) {
            for (Tour tour : originalToursLiveData.getValue()) {
                String loverQuery = query.toLowerCase();
                String loverTitle = tour.getTitle().toLowerCase();
                if (loverTitle.contains(loverQuery)) {
                    filteredTours.add(tour);
                }
            }
        }
        return filteredTours;
    }

    public ArrayList<Tour> getToursByAuthor(int guideId) {
        ArrayList<Tour> filteredTours = new ArrayList<>();
        if(originalToursLiveData.getValue() != null) {
            for (Tour tour : originalToursLiveData.getValue()) {
                if (tour.getAuthorId() == guideId) {
                    filteredTours.add(tour);
                }
            }
        }
        return filteredTours;
    }

    @SuppressLint("CheckResult")
    public void loadTours() {
        apiHelper.loadTours(PreferenceHelper.loadToken(context))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    Log.v("loadTours", "toursLiveData.setValue(tours) size = "+tours.size());
                    toursLiveData.setValue(tours);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
                });
    }

    @SuppressLint("CheckResult")
    public void loadContents() {
        apiHelper.loadContents(PreferenceHelper.loadToken(context))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    Log.v("loadTours", "contentsLiveData.setValue(tours) size = "+tours.size());
                    tours.get(tours.size() - 1).setPrice(350);
                    originalToursLiveData.setValue(tours);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
                });
    }

    @SuppressLint("CheckResult")
    public void loadNearTours(double latitude, double longitude) {
        apiHelper.loadNearTours(PreferenceHelper.loadToken(context), new NearToursRequest(latitude, longitude, 0.5))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    Log.v("loadTours", "contentsLiveData.setValue(tours) size = "+tours.size());
                    nearToursLiveData.setValue(tours);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
                });
    }

    public Observable<SaveTourResponse> saveTour(SaveTourRequest tour) {
        return apiHelper.createTour(PreferenceHelper.loadToken(context), tour);
    }
}
