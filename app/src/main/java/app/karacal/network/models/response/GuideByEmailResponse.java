package app.karacal.network.models.response;

public class GuideByEmailResponse extends BaseResponse {

    private boolean isGuide = false;
    private Integer id;

    public boolean isGuide() {
        return isGuide;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GuideByEmailResponse{" +
                "isGuide=" + isGuide +
                ", id=" + id +
                '}';
    }
}
