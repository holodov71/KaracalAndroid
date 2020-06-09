package app.karacal.retrofit.models.response;

public class UploadTrackResponse {
    private Object errorMessage;
    private boolean isSuccess;

    public Object getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
