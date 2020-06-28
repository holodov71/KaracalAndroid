package app.karacal.models;

import java.util.Date;

import app.karacal.retrofit.models.response.CommentsResponse;

public class Comment {

    private String author;
    private Date date;
    private String comment;

    public Comment(String author, Date date, String comment) {
        this.author = author;
        this.date = date;
        this.comment = comment;
    }

    public Comment(CommentsResponse.CommentResponse commentResponse) {
        this.author = commentResponse.getClientName() + commentResponse.getClientSecondName();
        this.date = new Date();
        this.comment = commentResponse.getText();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
