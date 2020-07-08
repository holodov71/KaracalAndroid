package app.karacal.network.models.response;

public class GuideByEmailResponse extends BaseResponse {

    private boolean isGuide = false;

    public boolean isGuide() {
        return isGuide;
    }

    @Override
    public String toString() {
        return "GuideByEmailResponse{" +
                "isGuide=" + isGuide +
                '}';
    }
}
