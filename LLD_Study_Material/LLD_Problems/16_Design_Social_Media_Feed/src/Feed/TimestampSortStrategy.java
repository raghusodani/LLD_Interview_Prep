package Feed;

import Models.Post;
import CommonEnum.SortOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimestampSortStrategy implements FeedSortStrategy {
    private SortOrder sortOrder;

    public TimestampSortStrategy(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void sort(List<Post> posts) {
        if (sortOrder == SortOrder.TIMESTAMP_DESC) {
            // Most recent first
            posts.sort(Comparator.comparing(Post::getTimestamp).reversed());
        } else {
            // Oldest first
            posts.sort(Comparator.comparing(Post::getTimestamp));
        }
    }
}
