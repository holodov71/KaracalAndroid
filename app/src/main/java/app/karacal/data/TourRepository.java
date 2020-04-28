package app.karacal.data;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.R;
import app.karacal.models.Tour;

@Singleton
public class TourRepository {

    private final ArrayList<Tour> tours = new ArrayList<>();

    @Inject
    public TourRepository() {
        tours.add(new Tour(1, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                9.99, 10, 14, 48.863352, 2.346973));
        tours.add(new Tour(2, R.mipmap.tour_image_example_02,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                null, 9, 14, 48.852007, 2.356188));
        tours.add(new Tour(3, R.mipmap.tour_image_example_03,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                9.99, 5, 14, 48.846273, 2.333738));
        tours.add(new Tour(4, R.mipmap.tour_image_example_02,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                9.99, 8, 14,  48.842149, 2.281903));
        tours.add(new Tour(5, R.mipmap.tour_image_example_05,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                null, 7, 14, 48.870894, 2.418592));
        tours.add(new Tour(6, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                null, 10, 14, 48.832326, 2.369280));
        tours.add(new Tour(7, R.mipmap.tour_image_example_03,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                null, 6, 14, 48.854111, 2.365951));
        tours.add(new Tour(8, R.mipmap.tour_image_example_05,
                "Graph techniques with steph",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                9.99, 3, 14, 48.867015, 2.317614));
        tours.add(new Tour(9, R.mipmap.tour_image_example_06,
                "The little secrets of the grand palace",
                "Go on a date with the city of light and explore the shores of its main artery in the sunset.",
                null, 9, 14, 48.832901, 2.388167));
    }

    public ArrayList<Tour> getToursByCategoryId(int categoryId) {
        switch (categoryId) {
            case 0:
                return getRecommendedTours();
            case 1:
                return getNearTours();
            case 2:
                return getOriginalTours();
            default:
                return new ArrayList<>();
        }
    }

    public ArrayList<Tour> getRecommendedTours() {
        ArrayList<Tour> result = new ArrayList<>();
        result.add(tours.get(0));
        result.add(tours.get(1));
        result.add(tours.get(2));
        return result;
    }

    public ArrayList<Tour> getNearTours() {
        ArrayList<Tour> result = new ArrayList<>();
        result.add(tours.get(3));
        result.add(tours.get(4));
        result.add(tours.get(5));
        return result;
    }

    public ArrayList<Tour> getOriginalTours() {
        ArrayList<Tour> result = new ArrayList<>();
        result.add(tours.get(6));
        result.add(tours.get(7));
        result.add(tours.get(8));
        return result;
    }

    public ArrayList<Tour> getAllTours(){
        return tours;
    }

    public Tour getTourById(int tourId){
        return tours.get(tourId - 1);
    }

}
