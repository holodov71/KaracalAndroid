package app.karacal.network.models.response;

import java.util.List;

public class CommentsResponse extends BaseResponse {
    private List<CommentResponse> comments;

    public List<CommentResponse> getComments() {
        return comments;
    }

    public class CommentResponse{
        private int id;
        private int clientId;
        private String clientName;
        private String clientSecondName;
        private int guideId;
        private int tourId;
        private String createdAt;
        private String text;
        private int status;

        public int getId() {
            return id;
        }

        public int getClientId() {
            return clientId;
        }

        public String getClientName() {
            return clientName;
        }

        public String getClientSecondName() {
            return clientSecondName;
        }

        public int getGuideId() {
            return guideId;
        }

        public int getTourId() {
            return tourId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getText() {
            return text;
        }

        public int getStatus() {
            return status;
        }
    }
}
