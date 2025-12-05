package Services;

import Models.User;
import Models.Post;
import Feed.PullFeedGenerator;

import java.util.List;

public class FeedService {
    private PullFeedGenerator feedGenerator;

    public FeedService(PostService postService) {
        this.feedGenerator = new PullFeedGenerator(postService);
    }

    /**
     * Get feed for a user using Pull Model
     */
    public List<Post> getFeed(User user, int limit) {
        return feedGenerator.generateFeed(user, limit);
    }

    /**
     * Get paginated feed
     */
    public List<Post> getFeed(User user, int pageNumber, int pageSize) {
        return feedGenerator.generateFeed(user, pageNumber, pageSize);
    }

    /**
     * Display feed in a nice format
     */
    public void displayFeed(User user, int limit) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📱 FEED FOR @" + user.getUsername().toUpperCase());
        System.out.println("=".repeat(60));

        List<Post> feed = getFeed(user, limit);

        if (feed.isEmpty()) {
            System.out.println("No posts to display. Follow more users to see their posts!");
        } else {
            for (Post post : feed) {
                System.out.println(post);

                // Show comments if any
                if (post.getCommentCount() > 0) {
                    System.out.println("  Comments:");
                    for (var comment : post.getComments()) {
                        System.out.println(comment);
                    }
                }
            }
        }

        System.out.println("=".repeat(60) + "\n");
    }
}
