package Feed;

import Models.Post;
import java.util.List;

public interface FeedSortStrategy {
    void sort(List<Post> posts);
}
