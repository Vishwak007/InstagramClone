package com.example.instagramclone.Model;

public class Comment {
    String commentId;
    String comment;
    String publisher;

    public Comment() {
    }

    public Comment(String commentId, String comment, String publisher) {
        this.commentId = commentId;
        this.comment = comment;
        this.publisher = publisher;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
