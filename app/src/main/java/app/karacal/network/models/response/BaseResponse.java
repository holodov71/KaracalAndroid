package app.karacal.network.models.response;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.JsonHelper;

public class BaseResponse {
    private Object errorMessage;
    private boolean isSuccess;

    public String getErrorMessage() {
        String errors = JsonHelper.extractStrings(errorMessage);
        if (errors != null){
            return errors;
        }
        return App.getResString(R.string.common_error);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

}
