package Models;

import java.time.LocalDateTime;

public class Comment {
    private String commentId;
    private String postId;
    private String userId;
    private String username;
    private String content;
    private LocalDateTime timestamp;

    public Comment(String commentId, String postId, String userId, String username, String content) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getCommentId() {
        return commentId;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "   └─ @" + username + ": " + content + " (" + timestamp + ")";
    }
}
