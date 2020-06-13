package app.karacal.retrofit.models.response;

public class BaseResponse {
    private String errorMessage;
    private boolean isSuccess;

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

}
