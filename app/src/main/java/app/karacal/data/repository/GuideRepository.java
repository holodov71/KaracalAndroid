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

@Singleton
public class GuideRepository {

    @Inject
    ApiHelper apiHelper;

    private Context context;

    public MutableLiveData<List<Guide>> guidesLiveData = new MutableLiveData<>();

    @Inject
    public GuideRepository(App context){
        this.context = context;

//        guides.add(new Guide(1, "Zacharie ", "BOUBLI", "Paris", "Historien et guide, Je suis passionné d'histoire. Avec moi, vous découvrirez les annecdotes sombres et croustillantes de Paris, du cimetière Montparnasse au Halles en passant par le Marais. Suivez-moi !", R.mipmap.avatar_example));
//        guides.add(new Guide(2, "Eugénie", "DUMONT-PAILLETTE", "Paris","Comédienne et passionné d'art, je vous entraîne dans mon sillage pour découvrir des oeuvres que j'adore, du Louvre au cinéma américain, vous aurez toujours une bonne raison de découvrir et sourire en ma compagnie ! En route !", R.mipmap.guide_avatar_2));
//        guides.add(new Guide(3, "Anouck", "RECH", "Paris et Lyon","Ma passion : trouver les meilleures manières d'accompagner les gens pour découvrir l'art et la culture ! Etudiante en médiation culturelle je suis sur Karacal pour expérimenter de nouveaux formats. Préparez-vous ça va décoiffer !", -1));
//        guides.add(new Guide(4, "Jeanne", "RETACKER", "Paris", "Je m'appelle Jeanne, je vis maintenant à Lyon mais je suis née à Paris et j'adore cette ville ! Je fais donc en sorte de vous la faire découvrir à distance :) Mes passions sont l'architecture et l'art contemporain.", R.mipmap.guide_avatar_4));
//        guides.add(new Guide(5, "Clotilde", "DE BROSSES", "Paris", "Passionné d'art et d'histoire, je suis professeure. J'enseigne mais surtout, je partage ! C'est ma vocation. Vous emmenez dans mon univers joyeux pour apprendre à mes côté, en voilà une belle aventure. Ca vous tente ? Alors, c'est parti !", R.mipmap.guide_avatar_5));
//        guides.add(new Guide(6, "Fanny", "COHEN MOREAU", "Paris", "Diplomée en histoire et en journalisme, je produis aujourd'hui le podcast Passion Médiévistes, où j'interviewe de jeunes chercheurs en histoire médiévale dans un but pédagogique et de partage du savoir. C'est avec la même logique que j'ai décidé de créer des visites Karacal ! Suivez mes pas et venez découvrir des lieux insolites et secrets.", R.mipmap.guide_avatar_6));
//        guides.add(new Guide(7, "Marie", "PLAINFOSSE", "", "Bonjour, je suis Marie, comédienne de profession. J'organise aussi des visites guidées \"en vrai\" si vous voulez entendre mes commentaires les moins politiquement correct ! C'est un plaisir de vous guider sur Karacal. ", -1));
//        guides.add(new Guide(8, "Moa", "KHOUAS", "Paris et région centre", "Hello, je suis Trendy. Comédien, j'aime utiliser la puissance et l'éventail sonore de l'audio, passionné d'histoire, j'aime partager et échanger. Partons à la recherche des Spaces Invaders ou découvrir la Grande Mosquée de Paris, je vous accompagne, sur Karacal.", R.mipmap.guide_avatar_8));
//        guides.add(new Guide(9, "Coralie", "BOUMELLALA", "Paris", "Je suis la co-fondatrice du podcast Ici ta Voix et journaliste radio expérimentée. J'aime beaucoup travailler sur les efforts sonores, ajouter des voix... bref, bienvenue dans mon monde !", -1));
//        guides.add(new Guide(10, "Oriane", "MARTY", "Paris", "Je m'appelle Orianne, j'ai co-fondé le podcast Ici ta voix avec Coralie. J'aime raconter ce qui anime les gens, faire parler les artistes de leur travail et essayer modestement d'expliquer la création. Je serais ravie d'avoir des retours de votre part en commentaire !", R.mipmap.guide_avatar_10));
//        guides.add(new Guide(11, "Lydia", "ALVAREZ", "", "Bonjour, je suis Lydia ! Je possède une licence en histoire de l'art et je suis, actuellement, en pleine préparation d'un doctorat en histoire ancienne. Je suis passionnée par l'Histoire, notamment par l'antiquité gréco-romaine et égyptienne. Je m'intéresse, également, au Moyen-âge, à l'art de la Renaissance, à la période révolutionnaire ainsi qu'aux différents mystères et affaires criminelles qui émaillent notre histoire.", R.mipmap.guide_avatar_11));
//        guides.add(new Guide(12, "Charlotte", "DURAND", "", "Coucou, je suis Charlotte,  actuellement plus ou moins étudiante en histoire des religions à la Sorbonne. Mon sujet de prédilection est l'antiquité mésopotamienne mais je m'intéresse à beaucoup d'autres sujets plus récents. Accessoirement je suis aussi la co-fondatrice de Karacal.", R.mipmap.guide_avatar_12));
//        guides.add(new Guide(13, "Hanna", "FEYLER", "", "Je m'appelle Hanna et je suis spécialiste de l'expertise d'objet d'art. J'ai plein de périodes préférées et de petites obsessions, mais pour les découvrir il faudra écouter mes audios :) Quand même, un indice : une de mes obsessions est Karacal... et oui, j'ai co-fondé l'entreprise !", R.mipmap.guide_avatar_13));
//        guides.add(new Guide(14, "Jennifer", "KERNER", "", "", -1));
//        guides.add(new Guide(15, "Rémi", "Hamot", "", "", -1));
//        guides.add(new Guide(16, "Quentin", "BIDAULT", "", "", -1));
//        guides.add(new Guide(17, "Chloé", "NGUYEN", "", "", -1));
//        guides.add(new Guide(18, "Maxime", "PARI", "", "", -1));
//        guides.add(new Guide(19, "Valentin", "KERAVEL", "", "", -1));
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

    @SuppressLint("CheckResult")
    public void loadGuides() {
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
