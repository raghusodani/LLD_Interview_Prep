package Feed;

import Models.Post;
import Models.User;
import Services.PostService;
import CommonEnum.SortOrder;
import java.util.*;

/**
 * Pull Model: Query posts on-demand when user requests feed
 * - Pros: No storage overhead, always fresh data
 * - Cons: Slower for users with many followings
 */
public class PullFeedGenerator {
    private PostService postService;
    private FeedSortStrategy sortStrategy;

    public PullFeedGenerator(PostService postService) {
        this.postService = postService;
        this.sortStrategy = new TimestampSortStrategy(SortOrder.TIMESTAMP_DESC);
    }

    public void setSortStrategy(FeedSortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    /**
     * Generate feed by pulling posts from all followees
     * Pull Model: Query happens at read time
     */
    public List<Post> generateFeed(User user, int limit) {
        List<Post> feedPosts = new ArrayList<>();

        // Get user's own posts
        feedPosts.addAll(postService.getPostsByUser(user.getUserId()));

        // Get posts from all people user follows
        for (String followingUserId : user.getFollowing()) {
            feedPosts.addAll(postService.getPostsByUser(followingUserId));
        }

        // Sort posts using strategy
        sortStrategy.sort(feedPosts);

        // Limit results
        return feedPosts.subList(0, Math.min(limit, feedPosts.size()));
    }

    /**
     * Generate feed with pagination support
     */
    public List<Post> generateFeed(User user, int pageNumber, int pageSize) {
        List<Post> allPosts = generateFeed(user, Integer.MAX_VALUE);

        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allPosts.size());

        if (startIndex >= allPosts.size()) {
            return new ArrayList<>();
        }

        return allPosts.subList(startIndex, endIndex);
    }
}
