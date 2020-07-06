package app.karacal.network.models.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.JsonHelper;

public class BaseResponse {
    private JsonObject errorMessage;
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
