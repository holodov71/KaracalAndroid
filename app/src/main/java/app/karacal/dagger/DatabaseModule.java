package app.karacal.dagger;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.AppDatabase;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    public static final String KARACAL_DATABASE = "karacal.db";

    @Provides
    @Singleton
    public AppDatabase provideDatabase(App application)  {
        AppDatabase database = Room.databaseBuilder(
                application,
                AppDatabase.class,
                KARACAL_DATABASE)
            .build();

        populateInitialData(database);

        return database;
    }

    @Provides
    ToursDao provideToursDao(AppDatabase database) {
        return database.toursDao();
    }

    /**
     * Inserts the default data into the database if it is currently empty.
     */
    private void populateInitialData(AppDatabase database) {
//        if (database.toursDao().count() == 0) {
//            database.runInTransaction(new Runnable() {
//                @Override
//                public void run() {
//                    for (TourEntity tourEntity: getDefaultTours()) {
//                        database.toursDao().insert(tourEntity);
//                    }
//
//
//                }
//            });
//        }
    }

    private List<TourEntity> getDefaultTours(){
        List<TourEntity> tours = new ArrayList<>();

        tours.add(new TourEntity(1, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 10, 14, "Alexander McQueen", 48.863352, 2.346973, 1));

        tours.add(new TourEntity(2, R.mipmap.tour_image_example_02,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 9, 14, "Alexander McQueen", 48.852007, 2.356188, 2));

        tours.add(new TourEntity(3, R.mipmap.tour_image_example_03,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 5, 14, "Alexander McQueen", 48.846273, 2.333738,3));

        tours.add(new TourEntity(4, R.mipmap.tour_image_example_02,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 8, 14,  "Alexander McQueen", 48.842149, 2.281903, 4));

        tours.add(new TourEntity(5, R.mipmap.tour_image_example_05,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 7, 14, "Alexander McQueen", 48.870894, 2.418592, 5));

        tours.add(new TourEntity(6, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 10, 14, "Alexander McQueen", 48.832326, 2.369280, 6));

        tours.add(new TourEntity(7, R.mipmap.tour_image_example_03,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 6, 14, "Alexander McQueen", 48.854111, 2.365951, 7));

        tours.add(new TourEntity(8, R.mipmap.tour_image_example_05,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 3, 14, "Alexander McQueen", 48.867015, 2.317614, 8));

        tours.add(new TourEntity(9, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                0, 9, 14, "Alexander McQueen", 48.832901, 2.388167, 9));

        tours.add(new TourEntity(10, R.mipmap.tour_image_varda,
                "La rue Daguerre sous l'oeil d'Agnès Varda",
                "Plongez dans le Paris d'Agnès Varda avec Orianne, l'autre voix d'Ici Ta Voix. Elle vous fera découvrir la rue Daguerre et son histoire, dans les pas de la célèbre artiste française.",
                0, 10, 7, "Orianne MARTY", 48.836139, 2.323694, 10));

        tours.add(new TourEntity(11, R.mipmap.tour_image_procope,
                "Le Procope",
                "Découvrez le plus ancien restaurant de Paris encore en activité avec Lydia. De nombreux personnages historiques sont passés entre ses murs, à l'instar des philosophes des Lumières (Voltaire, Rousseau), des révolutionnaires (Danton, Marat) ou encore des hommes politiques (Napoléon Bonaparte, Gambetta)." +
                        "\n" +
                        "Cette visite commence devant l'entrée secondaire du Procope, rue du commerce Saint-André.",
                0, 10, 5, "Lydia ALVAREZ", 48.853222, 2.339056, 11));

        tours.add(new TourEntity(12, R.mipmap.tour_image_jardins_hopital,
                "Les jardins de l'hôpital Saint-Louis",
                "Suivez-moi dans les jardins calmes et paisibles de l'Hôpital Saint Louis et découvrez ses secrets !",
                0, 10, 5, "Fanny COHEN MOREAU", 48.873472, 2.369861, 12));

        tours.add(new TourEntity(13, R.mipmap.tour_image_bastille,
                "Métro Bastille",
                "Découvrez Paris par ses bouches de métro ! Un audio à écouter sur le quai ou sur la place, en fonction de votre préférence.",
                0, 10, 5, "Hanna FEYLER", 48.853306, 2.369111, 13));

        tours.add(new TourEntity(14, R.mipmap.tour_image_soulages,
                "Musée Soulages",
                "Avant votre visite ou si vous n'avez pas le temps de rentrer dans le musée, découvrez l'architecture et l'aménagement de la Place ! Depuis le Jardin Public du Foirail, flânez dans les allées pour découvrir ce beau monument.",
                0, 10, 8, "Hanna FEYLER", 44.351694, 2.568111, 14));

        tours.add(new TourEntity(15, R.mipmap.tour_image_colonne_de_juillet,
                "La colonne de Juillet",
                "Mais quelle est donc cette colonne, place de la Bastille ? Vous vous l'êtes souvent demandé mais vous n'avez jamais eu le temps de répondre à votre question ? Karacal vous le raconte !",
                0, 10, 3, "Hanna FEYLER", 48.853306, 2.369111, 15));

        tours.add(new TourEntity(16, R.mipmap.tour_image_orsay,
                "Musée d'Orsay - Episode 1 des \"Visites Guidées Privées\"",
                "Découvrez l’histoire du Musée D’Orsay, le contexte de création du bâtiment, ses singularités architecturales et son évolution ! -> de 1900 à aujourd’hui ! Nous évoquerons le contexte des expositions universelles, les différentes vies du bâtiments et la sélection des oeuvres disponibles dans cet édifice." +
                        "\n" +
                        "Point de départ : 1 Rue de la Légion d'Honneur, 75007 Paris, Parvis principal de l'entrée du Musée. Cet audio peut tout à fait être écouté dans une file d'attente.",
                0, 10, 8, "Marie PLAINFOSSE", 48.86075, 2.325306, 16));

        tours.add(new TourEntity(17, R.mipmap.tour_image_terreaux,
                "La place des Terreaux",
                "Anouck vous propose une courte visite de la Place des Terreaux, l'occasion de découvrir son histoire et son passé.",
                0, 10, 4, "Anouck RECH", 48.86075, 2.325306, 17));

        tours.add(new TourEntity(18, R.mipmap.tour_image_invaders,
                "Space Invaders",
                "Partez à la recherche de ces petites mosaïques du nom de \"Space Invaders\" avec Trendy. Premier d'une longue série, rendez-vous Place de la République !",
                0, 10, 7, "Moa KHOUAS (Trendy)", 48.867861, 2.363972, 18));

        tours.add(new TourEntity(19, R.mipmap.tour_image_monceau,
                "Le Parc Monceau",
                "Flânez dans le Parc Monceau et découvrez son histoire avec Hanna !",
                0, 10, 4, "Hanna FEYLER", 48.88025, 2.309056, 19));

        tours.add(new TourEntity(20, R.mipmap.tour_image_stgeor,
                "La place Saint Georges",
                "Bonjour je suis Clotilde. Je souhaite vous faire découvrir les dessous scandaleux des monuments de la place Saint Georges... Et oui, cette place n'est pas aussi classique qu'elle le paraît !",
                0, 10, 6, "Clotilde DE BROSSES", 48.879111, 2.337444, 20));

        tours.add(new TourEntity(21, R.mipmap.tour_image_stravinsky,
                "La Fontaine Stravinsky",
                "Découvrez la Fontaine Stravinsky, créée par les artistes Nouveaux Réalistes Nikki de Saint-Phalle et Jean Tinguely. Rendez-vous devant la Fontaine, à côté du Centre Pompidou sur la Place Igor Stravinsky.",
                0, 10, 4, "Jeanne RETACKER", 48.859444, 2.351389, 21));

        tours.add(new TourEntity(22, R.mipmap.tour_image_cuisine,
                "L'ingrédient secret de la toile \"Intérieur d'une cuisine\"",
                "Découvrez l'histoire rocambolesque d'un tableau qui cache un terrible secret sous son vernis de finition... L'expression \"mettre du cœur à l'ouvrage\", ça vous dit quelque chose ? Et bien vous allez voir que Martin Drölling est allé très loin dans l'illustration de ce vieil adage !\n" +
                        "\n" +
                        "Rendez-vous dans l'aîle Sully, au 2e étage dans la salle 938.",
                0, 10, 6, "Jennifer KERNER", 48.860639, 2.337417, 22));

        tours.add(new TourEntity(23, R.mipmap.tour_image_philippe_pot,
                "Le tombeau de Philippe Pot : un chef-d'oeuvre funéraire du XVe siècle",
                "Les visiteurs du Louvre étaient en deuil car le plus beau gisant du musée était parti sous les pinceaux des restaurateurs... Maintenant qu'il est de retour, profitons-en pour admirer ensemble un tombeau spectaculaire qui a révolutionné l'art funéraire de la fin du XVe siècle !" +
                        "\n" +
                        "Vous trouverez cette oeuvre dans l'Aile Richelieu, au ez-de-chaussée et dans la salle 210.",
                0, 10, 11, "Jennifer KERNER", 48.861583, 2.335861, 23));

        tours.add(new TourEntity(24, R.mipmap.tour_image_mort,
                "La redoutable allégorie de la Mort",
                "Comme des milliers de parisiens depuis 1530, venez frissonner au contact de la sublime \"Mort saint Innocent\" au Musée du Louvre.\n" +
                        "\n" +
                        "Vous trouverez cette oeuvre dans l'aîle Richelieu, \tau rez-de-chaussée, salle 212.",
                0, 10, 9, "Jennifer KERNER", 48.862194, 2.336056, 24));

        tours.add(new TourEntity(25, R.mipmap.tour_image_vaudou,
                "Un fascinant mannequin Vaudou",
                "Le vaudou... Religion intrigante et magie mystérieuse qui alimente tous les fantasmes ! Mais parfois, la réalité est encore plus fascinante... Découvrons ensemble le plus bel objet vaudou des collections du Musée du Quai Branly.",
                0, 10, 7, "Jennifer KERNER", 48.86125, 2.298139, 25));

        tours.add(new TourEntity(26, R.mipmap.tour_image_chteaugaillard,
                "Le Château Gaillard",
                "Découvrez avec moi ce château que j'affectionne tant !",
                0, 10, 5, "Rémi Hamot", 49.238028, 1.403444, 26));

        tours.add(new TourEntity(27, R.mipmap.tour_image_corps,
                "Le corps imputrescible de Ste Madeleine-Sophie Barat",
                "Un cadavre vieux de 150 ans toujours aussi frais qu'une rose, cela vous paraît impossible ? Les témoignages sur ce phénomène se sont pourtant multipliés depuis des siècles... Je vous propose de découvrir un cas jugé miraculeux par le Vatican, celui du corps imputrescible de Ste Madeleine-Sophie Barat.",
                0, 10, 6, "Jennifer KERNER", 48.851111, 2.313917, 27));

        tours.add(new TourEntity(28, R.mipmap.tour_image_saint_lambert,
                "Les blousons noirs du square Saint Lambert",
                "Les Blousons noirs, ces jeunes plus ou moins voyous, sont nés le 23 juillet 1959 dans ce petit square du 15e arrondissement. Laissez-moi vous raconter cette histoire le temps de faire le tour du lieu.",
                0, 10, 6, "Quentin BIDAULT", 48.842611, 2.296694, 28));

        tours.add(new TourEntity(29, R.mipmap.tour_image_example_03,
                "Les adresses gourmande du XIIIe arrondissement",
                "Culture gourmande au programme !",
                0, 10, 5, "Chloé NGUYEN", 48.851111, 2.313917, 29));

        tours.add(new TourEntity(30, R.mipmap.tour_image_arnes,
                "Arènes de Lutèce",
                "Voici l'un des plus vieux vestiges de Paris ! Une courte ballade à faire seul ou en famille, un voyage dans le temps du Paris antique. Rendez-vous dans les Arènes.",
                0, 10, 6, "Hanna FEYLER", 48.845278, 2.352889, 30));

        tours.add(new TourEntity(31, R.mipmap.tour_image_assemblee_nationale,
                "Assemblée Nationale",
                "Un grand bâtiment parisien dont vous avez forcément déjà entendu parler... mais en connaissez-vous l'histoire ? Venez la découvrir ! Rendez-vous devant le monument, devant les grilles ou sur le pont pour une meilleure vue.",
                0, 10, 4, "Hanna FEYLER", 48.861, 2.318361, 31));

        tours.add(new TourEntity(32, R.mipmap.tour_image_canal_saint_martin,
                "Canal Saint-Martin",
                "Le Canal Saint-Martin est un incontournable point de fraîcheur lors des chaudes journées d'été. Moins touristique en hiver, il offre de belles couleurs en automne et redevient le coin favori des parisiens dès les premières sorties printanières. Débutez votre balade où vous le souhaitez et apprenez-en plus grâce à Karacal sur ce canal parisien.",
                0, 10, 4, "Hanna FEYLER", 48.869306, 2.367083, 32));

        tours.add(new TourEntity(33, R.mipmap.tour_image_piaf,
                "Edith Piaf est-elle née ici ?",
                "Edith Piaf est sans conteste l'une des plus grandes voix de la chanson française... Mais ce fut aussi une femme exigeante et compliquée, dont la vie fut parfois tragique. \n" +
                        "Retrouvons-nous ici pour parler un peu d'elle...",
                0, 10, 4, "Charlotte DURAND", 48.8735, 2.38375, 33));

        tours.add(new TourEntity(34, R.mipmap.tour_image_devient_paris,
                "Comment Paris devient Paris",
                "Les villes n'apparaissent pas du jour au lendemain, tout le monde sait cela.\n" +
                        "Mais ce que les gens imaginent moins c'est que fut un jour où Paris n'était qu'un petit village gaulois. (Non je ne ferais pas de blagues impliquant Astérix) En avant pour la visite !",
                0, 10, 4, "Charlotte DURAND", 48.854917, 2.3475, 34));

        tours.add(new TourEntity(35, R.mipmap.tour_image_cernuschi,
                "Musée Cernuschi",
                "Avez-vous déjà eu l'idée de créer un musée dédié aux arts asiatiques ? \n" +
                        "Si oui contactez-moi immédiatement. Si non vous pouvez découvrir dans ce podcast comment Henri Cernuschi finit par créer le musée qui porte son nom, dans cet hôtel particulier à deux pas du parc Monceau !",
                0, 10, 5, "Hanna FEYLER", 48.879583, 2.312417, 35));

        tours.add(new TourEntity(36, R.mipmap.tour_image_grand_palais,
                "Grand Palais",
                "Voici mon premier podcast consacré aux œuvres de la République, sous forme d'une petite présentation du Grand Palais. J'espère qu'il vous plaira.\n" +
                        "Je vous conseille d'écouter ce podcast en remontant l'avenue Winston Churchill un jour de grand soleil, l'expérience sera parfaite. Bien sûr vous pouvez aussi l'écouter sous la pluie mais ça sera moins sympa.",
                0, 10, 3, "Valentin KERAVEL", 48.866111, 2.313472, 36));

        tours.add(new TourEntity(37, R.mipmap.tour_image_palais_galliera,
                "Palais Galliera",
                "Comment j'en suis venu à parler du Palais Galliera ? C'est une histoire marrante que je ne vais pas vous raconter aujourd'hui.\n" +
                        "Par contre je vais vous parler de la comtesse. La comtesse Galliera bien sûr. Il faut suivre un peu !",
                0, 10, 3, "Maxime PARI", 48.865972, 2.296611, 37));

        tours.add(new TourEntity(38, R.mipmap.tour_image_arc_de_triomphe,
                "Arc de Triomphe",
                "J'ai toujours aimé l'Arc de Triomphe, solide et avec la place de l'Etoile qui tourne autour de lui.\n" +
                        "Mais je ne m'étais jamais vraiment penché sur son histoire. C'est maintenant chose faite !",
                0, 10, 3, "Maxime PARI", 48.873778, 2.295028, 38));

        tours.add(new TourEntity(39, R.mipmap.tour_image_louvre,
                "Louvre",
                "On parle toujours de ce qu'il y a dans le Louvre. Des égyptiens et de la Joconde principalement. Comme je n'aime pas faire les choses comme les autres je vais plutôt vous parler de l'extérieur du musée, et de l'histoire de ses bâtiments. Un peu moins de 4 minutes pour tout ça, je suis un mec efficace.",
                0, 10, 4, "Maxime PARI", 48.860611, 2.337611, 39));

        tours.add(new TourEntity(40, R.mipmap.tour_image_barbier_criminels,
                "Le barbier et le patissier criminels",
                "Cela faisait longtemps que je n'avais pas fait de podcast. Cette fois je vais vous parler d'une histoire mi-glauque, mi-légendaire et re-mi-glauque. ça fait trois moitiés mais je m'en fiche c'est mon podcast je fais ce que je veux.",
                0, 10, 4, "Maxime PARI", 48.835944, 2.350194, 40));

        tours.add(new TourEntity(41, R.mipmap.tour_image_pont_mirebeau,
                "Le Pont Mirabeau ",
                "Dans la ligne 8 du métro, il y a depuis longtemps affiché quelques vers du poème \"Le Pont Mirabeau\" de Guillaume Apollinaire. Je crois que c'est ce qui m'a inspiré pour ce podcast !\n" +
                        "Comme le titre l'indique ça va parler du pont Mirabeau. J'espère que ça va vous plaire, n'hésitez pas à me faire des retours en commentaire !",
                0, 10, 2, "Valentin KERAVEL", 48.846667, 2.275583, 41));

        return tours;
    }
}
