package Services;

import Models.Post;
import Models.Comment;
import CommonEnum.PostType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PostService {
    private Map<String, Post> posts;
    private Map<String, List<String>> userPosts;  // userId -> list of postIds
    private static int postCounter = 1;
    private static int commentCounter = 1;

    public PostService() {
        this.posts = new ConcurrentHashMap<>();
        this.userPosts = new ConcurrentHashMap<>();
    }

    /**
     * Factory Method: Create different types of posts
     */
    public Post createPost(String userId, String username, String content, PostType postType) {
        String postId = "P" + (postCounter++);
        Post post = new Post(postId, userId, username, content, postType);

        posts.put(postId, post);
        userPosts.computeIfAbsent(userId, k -> new ArrayList<>()).add(postId);

        System.out.println("📝 @" + username + " created a new " + postType + " post");
        return post;
    }

    public Post getPost(String postId) {
        return posts.get(postId);
    }

    public List<Post> getPostsByUser(String userId) {
        List<String> postIds = userPosts.getOrDefault(userId, new ArrayList<>());
        List<Post> userPostsList = new ArrayList<>();
        for (String postId : postIds) {
            Post post = posts.get(postId);
            if (post != null) {
                userPostsList.add(post);
            }
        }
        return userPostsList;
    }

    public List<Post> getAllPosts() {
        return new ArrayList<>(posts.values());
    }

    /**
     * Like functionality
     */
    public boolean likePost(String postId, String userId) {
        Post post = posts.get(postId);
        if (post == null) return false;

        post.addLike(userId);
        return true;
    }

    /**
     * Unlike functionality
     */
    public boolean unlikePost(String postId, String userId) {
        Post post = posts.get(postId);
        if (post == null) return false;

        post.removeLike(userId);
        return true;
    }

    /**
     * Comment on post
     */
    public Comment addComment(String postId, String userId, String username, String content) {
        Post post = posts.get(postId);
        if (post == null) return null;

        String commentId = "C" + (commentCounter++);
        Comment comment = new Comment(commentId, postId, userId, username, content);
        post.addComment(comment);

        System.out.println("💬 @" + username + " commented on post " + postId);
        return comment;
    }

    /**
     * Get comments for a post
     */
    public List<Comment> getComments(String postId) {
        Post post = posts.get(postId);
        if (post == null) return new ArrayList<>();
        return post.getComments();
    }
}
