package Models;

import CommonEnum.PostType;
import java.time.LocalDateTime;
import java.util.*;

public class Post {
    private String postId;
    private String userId;
    private String username;
    private String content;
    private PostType postType;
    private LocalDateTime timestamp;
    private Set<String> likes;           // User IDs who liked
    private List<Comment> comments;

    public Post(String postId, String userId, String username, String content, PostType postType) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.postType = postType;
        this.timestamp = LocalDateTime.now();
        this.likes = new HashSet<>();
        this.comments = new ArrayList<>();
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

    public PostType getPostType() {
        return postType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Set<String> getLikes() {
        return new HashSet<>(likes);
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public void addLike(String userId) {
        likes.add(userId);
    }

    public void removeLike(String userId) {
        likes.remove(userId);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public int getLikeCount() {
        return likes.size();
    }

    public int getCommentCount() {
        return comments.size();
    }

    @Override
    public String toString() {
        return "\n┌─────────────────────────────────────────────────────┐" +
               "\n│ Post by: @" + username + " (" + postType + ")" +
               "\n│ Time: " + timestamp +
               "\n├─────────────────────────────────────────────────────┤" +
               "\n│ " + content +
               "\n├─────────────────────────────────────────────────────┤" +
               "\n│ 👍 " + likes.size() + " likes  💬 " + comments.size() + " comments" +
               "\n└─────────────────────────────────────────────────────┘";
    }
}
