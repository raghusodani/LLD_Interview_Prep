# Design Social Media Feed System - Comprehensive Solution 📱

## **Problem Statement**

Design a social media platform where users can:
- Create posts (text, image, video, link)
- Follow/unfollow other users
- View a personalized feed of posts from people they follow
- Like and comment on posts
- See feed sorted by timestamp (most recent first)

**Real-World Examples:** Twitter, Instagram, Facebook

---

## **🎯 Our Approach**

### **Core Requirements**

**Functional Requirements:**
- ✅ User management (create, get users)
- ✅ Follow/Unfollow functionality
- ✅ Post creation with different types
- ✅ Like/Unlike posts
- ✅ Comment on posts
- ✅ Generate personalized feed
- ✅ Feed sorted by timestamp

**Non-Functional Requirements:**
- ✅ Pull Model for feed generation (query on-demand)
- ✅ Extensible for new post types
- ✅ Support different feed sorting strategies
- ✅ Performance: O(F + P) where F = followings, P = posts per user
- ✅ Thread-safe operations (ConcurrentHashMap)

---

## **🏗️ Architecture & Design Patterns**

### **Pattern 1: Observer Pattern**

**Where:** Follow/Unfollow mechanism

**Why:**
- Followers "observe" activities of people they follow
- Decouples users from each other
- Easy to notify on new posts (future enhancement)

**Implementation:**
```java
public class User {
    private Set<String> followers;   // Users observing this user
    private Set<String> following;   // Users this user observes
}

// UserService acts as the mediator
userService.follow(followerId, followeeId);
// Now follower observes followee's activities
```

**Benefits:**
- ✅ Loose coupling between users
- ✅ Easy to add push notifications later
- ✅ Supports one-to-many relationship

---

### **Pattern 2: Strategy Pattern**

**Where:** Feed sorting algorithms

**Why:**
- Different sorting strategies (timestamp, relevance, trending)
- Runtime selection of sorting algorithm
- Easy to add new sorting strategies

**Implementation:**
```java
public interface FeedSortStrategy {
    void sort(List<Post> posts);
}

public class TimestampSortStrategy implements FeedSortStrategy {
    @Override
    public void sort(List<Post> posts) {
        posts.sort(Comparator.comparing(Post::getTimestamp).reversed());
    }
}

// Can easily add:
public class RelevanceSortStrategy implements FeedSortStrategy {
    @Override
    public void sort(List<Post> posts) {
        // Sort by likes + comments + recency
        posts.sort((p1, p2) -> {
            int score1 = p1.getLikeCount() * 2 + p1.getCommentCount();
            int score2 = p2.getLikeCount() * 2 + p2.getCommentCount();
            return Integer.compare(score2, score1);
        });
    }
}
```

**Benefits:**
- ✅ Open/Closed Principle
- ✅ Runtime flexibility
- ✅ Testable in isolation

---

### **Pattern 3: Factory Pattern (Implicit)**

**Where:** Post creation in PostService

**Why:**
- Centralize post ID generation
- Handle different post types uniformly
- Can add validation per post type

**Implementation:**
```java
public Post createPost(String userId, String username, String content, PostType type) {
    String postId = "P" + (postCounter++);
    Post post = new Post(postId, userId, username, content, type);

    // Centralized storage and indexing
    posts.put(postId, post);
    userPosts.computeIfAbsent(userId, k -> new ArrayList<>()).add(postId);

    return post;
}
```

**Benefits:**
- ✅ Single Responsibility
- ✅ Consistent ID generation
- ✅ Centralized post management

---

### **Pattern 4: Pull Model Architecture**

**Key Concept:** Fetch posts when user requests feed, not when posts are created

```
Traditional (Push Model):
  User posts → Fanout to all followers' feeds (write-heavy)
  User views feed → Read from pre-computed feed (fast read)

Our Approach (Pull Model):
  User posts → Store in user's timeline only (fast write)
  User views feed → Query all followings' posts (read-heavy)
```

**Why Pull Model:**
- ✅ No storage overhead (no pre-computed feeds)
- ✅ Always fresh data
- ✅ Simpler implementation
- ✅ Good for users with few followings

**Trade-off:**
- ❌ Slower for users following 1000+ people
- ❌ Query happens on every feed request
- **Mitigation:** Can add caching layer for hot users

---

## **📐 Class Diagram**

```
┌─────────────┐
│    User     │
│─────────────│
│ -userId     │
│ -username   │
│ -followers  │◄─────────┐ Observer Pattern
│ -following  │          │ (Followers observe user's activities)
└─────────────┘          │
      ▲                  │
      │                  │
      │           ┌──────┴──────┐
      │           │ UserService │
      │           │─────────────│
      │           │ +follow()   │
      │           │ +unfollow() │
      │           └─────────────┘
      │
      │
┌─────┴─────────────┐         ┌──────────────────┐
│       Post        │         │ PostService      │
│───────────────────│         │──────────────────│
│ -postId           │         │ +createPost()    │ (Factory)
│ -userId           │         │ +likePost()      │
│ -content          │         │ +addComment()    │
│ -postType         │◄────────┤ +getPostsByUser()│
│ -timestamp        │         └──────────────────┘
│ -likes            │
│ -comments         │
└───────────────────┘
      │
      │ has-many
      ▼
┌─────────────┐
│   Comment   │
│─────────────│
│ -commentId  │
│ -postId     │
│ -userId     │
│ -content    │
│ -timestamp  │
└─────────────┘


┌──────────────────────┐         ┌─────────────────────┐
│  FeedSortStrategy    │◄────────│  PullFeedGenerator  │
│      (Interface)     │         │─────────────────────│
└──────────┬───────────┘         │ -sortStrategy       │
           │                     │ +generateFeed()     │
           │                     └─────────────────────┘
           │                               ▲
    ┌──────┴──────────┐                   │
    │                 │                   │
┌───▼──────────────┐  │         ┌─────────┴─────┐
│TimestampSort     │  │         │  FeedService  │
│Strategy          │  │         │───────────────│
└──────────────────┘  │         │ +getFeed()    │
                      │         │ +displayFeed()│
┌─────────────────┐   │         └───────────────┘
│RelevanceSort    │◄──┘
│Strategy (future)│
└─────────────────┘
```

---

## **🔑 Key Design Decisions**

### **Decision 1: Pull Model vs Push Model**

**What:** Use Pull Model (query on-demand) instead of Push Model (fanout on write)

**Pull Model:**
```
Write: Post → Store in author's timeline only
Read: User requests feed → Query all followings → Merge → Sort
```

**Push Model:**
```
Write: Post → Fanout to ALL followers' feeds (expensive!)
Read: User requests feed → Read pre-computed feed (fast!)
```

**Why Pull?**
- Simpler implementation (no fanout logic)
- No storage overhead for feeds
- Always fresh data
- Good for coding interviews (realistic for machine coding rounds)

**Interview Question:**
> "What if a celebrity with 10M followers posts? Pull Model becomes slow!"

**Answer:**
> "For celebrities (users with >100K followers), we'd use a **Hybrid approach**:
> - Regular users: Pull Model
> - Celebrities: Push Model with fanout + Redis caching
> - Threshold-based switching (e.g., if followers > 10K, use Push)
>
> In production (Twitter):
> - Pull for most users (query home timeline)
> - Push for power users (pre-compute and cache)
> - Use Redis for feed caching
> - Timeline service handles fanout asynchronously"

---

### **Decision 2: Observer Pattern for Follow/Unfollow**

**What:** Bidirectional relationship tracking

```java
class User {
    Set<String> followers;   // Who follows me
    Set<String> following;   // Who I follow
}

follow(A, B):
    A.following.add(B)
    B.followers.add(A)
```

**Why:**
- Efficient feed generation (iterate over following)
- Can query "who follows me" instantly
- Supports future notification system

**Interview Question:**
> "Why store both followers and following? Isn't one enough?"

**Answer:**
> "Storing both provides O(1) lookup for both directions:
> - `following`: Needed for feed generation (query: who's posts should I see?)
> - `followers`: Needed for analytics, notifications (query: who will see my post?)
>
> Trade-off: 2x storage for instant lookups. In production:
> - For casual users: Store both (small data)
> - For celebrities: Store followers in database, following in cache
> - Could use graph database (Neo4j) for complex relationships"

---

### **Decision 3: Strategy Pattern for Sorting**

**What:** Pluggable sorting algorithms

```java
feedGenerator.setSortStrategy(new TimestampSortStrategy(TIMESTAMP_DESC));
// or
feedGenerator.setSortStrategy(new RelevanceSortStrategy());
```

**Why:**
- Different users might prefer different sorting
- Easy to A/B test sorting algorithms
- Open/Closed Principle

**Interview Question:**
> "How would you implement 'relevance' sorting?"

**Answer:**
```java
public class RelevanceSortStrategy implements FeedSortStrategy {
    @Override
    public void sort(List<Post> posts) {
        posts.sort((p1, p2) -> {
            // Score = likes * 2 + comments * 3 + recency_bonus
            long score1 = calculateRelevanceScore(p1);
            long score2 = calculateRelevanceScore(p2);
            return Long.compare(score2, score1);
        });
    }

    private long calculateRelevanceScore(Post post) {
        long likeScore = post.getLikeCount() * 2;
        long commentScore = post.getCommentCount() * 3;

        // Recency bonus (newer posts get boost)
        long hoursSincePost = ChronoUnit.HOURS.between(post.getTimestamp(), LocalDateTime.now());
        long recencyBonus = Math.max(0, 100 - hoursSincePost);

        return likeScore + commentScore + recencyBonus;
    }
}
```

---

### **Decision 4: Service Layer Separation**

**What:** Separate UserService, PostService, FeedService

**Why:**
- **Single Responsibility:** Each service has one concern
- **UserService:** User and relationship management
- **PostService:** Post CRUD and interactions
- **FeedService:** Feed generation logic
- Easy to test independently
- Can scale services separately in microservices architecture

---

## **💡 SOLID Principles Applied**

### **S - Single Responsibility**
- `UserService` - Only manages users and relationships
- `PostService` - Only manages posts and interactions
- `FeedService` - Only generates and displays feeds
- `PullFeedGenerator` - Only feed generation logic

### **O - Open/Closed**
- Adding new sort strategy: Create new class, no modification
- Adding new post type: Just use enum, PostService unchanged
- Adding new interaction type (share, bookmark): Extend Post class

### **L - Liskov Substitution**
- Any `FeedSortStrategy` implementation can replace base interface
- Polymorphism works correctly

### **I - Interface Segregation**
- `FeedSortStrategy` - Only sorting method
- Services have focused public interfaces
- Clients depend on minimal interfaces

### **D - Dependency Inversion**
- `PullFeedGenerator` depends on `FeedSortStrategy` interface
- `FeedService` depends on abstraction, not concrete classes
- High-level modules don't depend on low-level implementations

---

## **🎭 Scenario Walkthrough**

### **Scenario: Alice Views Her Feed**

```
Initial State:
- Alice follows: [Bob, Charlie]
- Bob has posted: "Sunset photo"
- Charlie has posted: "Design patterns video"
- Alice has posted: "Hello world", "System design article"

Step 1: User requests feed
   feedService.getFeed(alice, 10)
   │
Step 2: PullFeedGenerator.generateFeed()
   │
Step 3: Collect posts from followings
   │
   ├─ Get Alice's own posts: ["Hello world", "System design"]
   ├─ Get Bob's posts: ["Sunset photo"] (Alice follows Bob)
   ├─ Get Charlie's posts: ["Design patterns video"] (Alice follows Charlie)
   │
Step 4: Merge all posts
   feedPosts = [post1, post2, post3, post4]
   │
Step 5: Sort using TimestampSortStrategy
   Sorted (most recent first):
   1. "System design article" (Alice, 10:30 AM)
   2. "Design patterns video" (Charlie, 10:25 AM)
   3. "Sunset photo" (Bob, 10:20 AM)
   4. "Hello world" (Alice, 10:15 AM)
   │
Step 6: Limit results to 10
   Return top 10 posts
   │
Step 7: Display feed
   Show posts with likes/comments
```

**Time Complexity:** O(F * P * log(F * P))
- F = number of followings
- P = average posts per user
- log factor from sorting

---

## **🚀 Extensions & Enhancements**

### **1. Push Model Implementation**

**When:** For power users with many followers

```java
public class PushFeedGenerator {
    private Map<String, List<Post>> precomputedFeeds; // userId -> feed

    public void onPostCreated(Post post, String authorId) {
        // Fanout to all followers' feeds
        Set<String> followers = userService.getFollowers(authorId);
        for (String followerId : followers) {
            precomputedFeeds.get(followerId).add(0, post);
            // Trim to last 1000 posts
            if (precomputedFeeds.get(followerId).size() > 1000) {
                precomputedFeeds.get(followerId).remove(1000);
            }
        }
    }

    public List<Post> getFeed(String userId) {
        // Fast read from pre-computed feed
        return precomputedFeeds.get(userId);
    }
}
```

---

### **2. Hybrid Model (Best of Both)**

```java
public class HybridFeedGenerator {
    private PushFeedGenerator pushGen;
    private PullFeedGenerator pullGen;

    public List<Post> generateFeed(User user) {
        List<Post> feed = new ArrayList<>();

        // Pull from celebrities (few, but many posts)
        Set<String> celebrities = getCelebrities(user.getFollowing());
        for (String celeb : celebrities) {
            feed.addAll(postService.getRecentPosts(celeb, 10));
        }

        // Push from regular users (many, but few posts each)
        Set<String> regularUsers = getRegularUsers(user.getFollowing());
        feed.addAll(pushGen.getPrecomputedFeed(regularUsers));

        // Merge and sort
        sortStrategy.sort(feed);
        return feed;
    }
}
```

---

### **3. Relevance-Based Feed (ML-powered)**

```java
public class MLRelevanceSortStrategy implements FeedSortStrategy {
    private MLModel model;

    @Override
    public void sort(List<Post> posts) {
        posts.sort((p1, p2) -> {
            double score1 = model.predict(createFeatures(p1));
            double score2 = model.predict(createFeatures(p2));
            return Double.compare(score2, score1);
        });
    }

    private Map<String, Double> createFeatures(Post post) {
        return Map.of(
            "likes", (double) post.getLikeCount(),
            "comments", (double) post.getCommentCount(),
            "author_followers", (double) getAuthorFollowerCount(post.getUserId()),
            "recency_hours", (double) getHoursSincePost(post),
            "user_engagement_history", getUserEngagementScore(post.getUserId())
        );
    }
}
```

---

### **4. Real-Time Notifications (Observer Pattern Extension)**

```java
public interface PostObserver {
    void onNewPost(Post post, User author);
}

public class NotificationObserver implements PostObserver {
    @Override
    public void onNewPost(Post post, User author) {
        // Send push notifications to all followers
        for (String followerId : author.getFollowers()) {
            sendPushNotification(followerId,
                "@" + author.getUsername() + " posted: " + post.getContent());
        }
    }
}

// In PostService:
public Post createPost(...) {
    Post post = new Post(...);

    // Notify observers
    for (PostObserver observer : observers) {
        observer.onNewPost(post, author);
    }

    return post;
}
```

---

## **🎯 Interview Questions & Answers**

### **Q1: How would you scale to millions of users?**

**Answer:**
```
Architecture for scale:

1. Database Sharding:
   - Shard users by userId hash
   - Shard posts by authorId hash
   - Follow graph in separate graph database (Neo4j)

2. Caching Strategy:
   - Redis for hot user feeds (celebrity feeds)
   - TTL-based invalidation (5 minutes)
   - Cache-aside pattern

3. Service Separation:
   - User Service (handles follows)
   - Post Service (handles posts)
   - Feed Service (generates feeds)
   - Timeline Service (stores timelines)
   - Each can scale independently

4. Database Strategy:
   - Write: MySQL (user data, relationships)
   - Read: Cassandra (timeline storage)
   - Graph: Neo4j (follow relationships)

5. Message Queue:
   - Kafka for async fanout (push model for celebrities)
   - SQS for notifications

6. CDN:
   - CloudFront for images/videos
   - S3 for media storage

7. Load Balancing:
   - ALB for service endpoints
   - Consistent hashing for cache distribution
```

---

### **Q2: Pull vs Push Model - When to use which?**

**Answer:**
```
Pull Model (Query on read):
Use when:
  ✅ Most users have few followings (<100)
  ✅ Want always fresh data
  ✅ Storage is expensive
  ✅ Write-heavy workload acceptable

Example: LinkedIn (professional network, fewer connections)

Push Model (Fanout on write):
Use when:
  ✅ Users have many followers
  ✅ Read latency is critical
  ✅ Can afford storage for pre-computed feeds
  ✅ Read-heavy workload

Example: Twitter (news feed must be instant)

Hybrid Model:
Use when:
  ✅ Mix of regular users and celebrities
  ✅ Need to optimize for both cases

Example: Instagram (pull for regular, push for influencers)

Comparison:
┌─────────────┬──────────────┬──────────────┐
│             │ Pull Model   │ Push Model   │
├─────────────┼──────────────┼──────────────┤
│ Write Time  │ O(1)         │ O(F) fanout  │
│ Read Time   │ O(F * P)     │ O(1)         │
│ Storage     │ O(P)         │ O(U * P)     │
│ Freshness   │ Always fresh │ Cached       │
│ Best For    │ Few follows  │ Many follows │
└─────────────┴──────────────┴──────────────┘

F = followers, P = posts, U = users
```

---

### **Q3: How to handle "trending" or "viral" posts?**

**Answer:**
```
Trending Algorithm:

1. Calculate trending score:
   score = (likes + comments * 2) / (hours_since_post + 2)^1.5

   - Likes and comments increase score
   - Recency factor (power of 1.5) decays over time
   - +2 to avoid division by zero

2. Track engagement velocity:
   - Likes in last hour vs total likes
   - If velocity > threshold, mark as trending

3. Dedicated Trending Feed:
   - Separate from personalized feed
   - Query top N posts globally
   - Cache aggressively (5-minute TTL)

4. Implementation:
   ```java
   public class TrendingSortStrategy implements FeedSortStrategy {
       @Override
       public void sort(List<Post> posts) {
           posts.sort((p1, p2) -> {
               double score1 = calculateTrendingScore(p1);
               double score2 = calculateTrendingScore(p2);
               return Double.compare(score2, score1);
           });
       }

       private double calculateTrendingScore(Post post) {
           long engagement = post.getLikeCount() + post.getCommentCount() * 2;
           double hoursSince = getHoursSince(post.getTimestamp());
           return engagement / Math.pow(hoursSince + 2, 1.5);
       }
   }
   ```

5. Use Redis Sorted Set:
   - ZADD trending:posts <score> <postId>
   - ZRANGE trending:posts 0 9 WITHSCORES
   - Update scores every 5 minutes (background job)
```

---

### **Q4: How to implement "Stories" (24-hour temporary posts)?**

**Answer:**
```
Story Implementation:

1. New Model:
   ```java
   public class Story {
       private String storyId;
       private String userId;
       private String content;
       private LocalDateTime expiresAt;  // timestamp + 24 hours

       public boolean isExpired() {
           return LocalDateTime.now().isAfter(expiresAt);
       }
   }
   ```

2. TTL-based Storage:
   - Redis with TTL=24 hours
   - SETEX story:<storyId> 86400 <story_data>
   - Automatic expiration

3. Background Cleanup:
   - Scheduled job every hour
   - Delete expired stories from database
   - Update user story_count

4. Display Logic:
   - Filter out expired stories on read
   - Show stories before feed posts
   - Sort by recency within stories

5. Analytics:
   - Track story views (separate counter)
   - Story completion rate
   - Time spent viewing
```

---

### **Q5: How to detect and prevent spam?**

**Answer:**
```
Multi-layer Spam Prevention:

1. Rate Limiting (per user):
   - Max 10 posts per hour
   - Max 50 likes per minute
   - Max 20 comments per minute
   - Use Token Bucket algorithm

2. Content Filtering:
   ```java
   public class SpamDetector {
       public boolean isSpam(String content) {
           // Check blacklisted words
           if (containsBlacklistedWords(content)) return true;

           // Check repeated characters (e.g., "aaaaaaa")
           if (hasExcessiveRepetition(content)) return true;

           // Check links to known spam domains
           if (containsSuspiciousLinks(content)) return true;

           // ML-based detection
           return mlModel.predictSpam(content) > 0.8;
       }
   }
   ```

3. User Reputation System:
   - New users: Limited posting
   - Verified users: Higher limits
   - Flagged users: Manual review required

4. Report System:
   - Users can report spam
   - Auto-hide after 5 reports
   - Manual moderator review

5. Shadow Banning:
   - User doesn't know they're banned
   - Their posts only visible to them
   - Prevents creating new accounts
```

---

### **Q6: How to implement "Retweet" or "Share" functionality?**

**Answer:**
```
Two Approaches:

Approach 1: Reference-based (Twitter style):
```java
public class Post {
    private String postId;
    private String originalPostId;  // null if not a retweet
    private String retweetedBy;     // userId who retweeted

    public boolean isRetweet() {
        return originalPostId != null;
    }
}

// In PostService:
public Post retweetPost(String postId, String userId) {
    Post original = posts.get(postId);
    Post retweet = new Post(...);
    retweet.setOriginalPostId(postId);
    retweet.setRetweetedBy(userId);
    return retweet;
}
```

Approach 2: Copy-based:
```java
public Post sharePost(String postId, String userId, String additionalComment) {
    Post original = posts.get(postId);
    Post shared = new Post(
        generateId(),
        userId,
        username,
        additionalComment + "\n\nShared: " + original.getContent(),
        PostType.SHARED
    );
    shared.setOriginalPost(original);
    return shared;
}
```

**Better Approach:** Reference-based
- Less storage (no duplication)
- Updates to original reflect in retweets
- Can count retweet numbers easily

---

### **Q7: How to handle feed for users who follow 10,000+ people?**

**Answer:**
```
Problem: Querying 10K users' posts is slow

Solutions:

1. Pagination + Limit:
   - Don't fetch all posts at once
   - Fetch top 10 recent posts per user
   - Total: 10K * 10 = 100K posts max
   - Sort and return top 50

2. Time-based Filtering:
   - Only fetch posts from last 7 days
   - Older posts unlikely to be viewed
   - Significantly reduces data

3. Caching:
   - Cache each user's last 100 posts
   - Redis: user:<userId>:recent_posts
   - TTL: 1 hour
   - Dramatically faster queries

4. Database Optimization:
   - Index on (userId, timestamp DESC)
   - Composite index for fast range queries
   - SELECT * FROM posts
     WHERE userId IN (...)
     AND timestamp > NOW() - INTERVAL 7 DAY
     ORDER BY timestamp DESC
     LIMIT 50

5. Parallel Queries:
   - Fetch from multiple users in parallel
   - CompletableFuture for async queries
   - Merge results after all complete

6. Smart Pagination:
   - Show top 50 posts immediately
   - Lazy-load more on scroll
   - Infinite scroll pattern
```

---

### **Q8: How to implement "Mentions" (@username)?**

**Answer:**
```
Implementation:

1. Parse mentions from content:
   ```java
   public class MentionParser {
       public List<String> extractMentions(String content) {
           List<String> mentions = new ArrayList<>();
           Pattern pattern = Pattern.compile("@(\\w+)");
           Matcher matcher = pattern.matcher(content);

           while (matcher.find()) {
               mentions.add(matcher.group(1));
           }
           return mentions;
       }
   }
   ```

2. Store in Post model:
   ```java
   public class Post {
       private List<String> mentionedUserIds;
   }
   ```

3. Notify mentioned users:
   ```java
   public Post createPost(...) {
       Post post = new Post(...);
       List<String> mentions = mentionParser.extractMentions(content);

       // Notify each mentioned user
       for (String username : mentions) {
           User mentioned = userService.getUserByUsername(username);
           if (mentioned != null) {
               notificationService.notify(mentioned.getUserId(),
                   "@" + author.getUsername() + " mentioned you");
           }
       }

       return post;
   }
   ```

4. Mention Feed:
   - Separate from home feed
   - Query: SELECT * FROM posts WHERE mentionedUserIds CONTAINS userId
   - Shows posts where user was mentioned
```

---

### **Q9: How to test this system?**

**Answer:**
```
Unit Tests:

1. UserService Tests:
   - testFollowUser()
   - testUnfollowUser()
   - testCannotFollowSelf()
   - testFollowNonexistentUser()

2. PostService Tests:
   - testCreatePost()
   - testLikePost()
   - testUnlikePost()
   - testAddComment()
   - testGetPostsByUser()

3. FeedService Tests:
   - testPullModelFeed()
   - testFeedSortedByTimestamp()
   - testFeedExcludesUnfollowedUsers()
   - testEmptyFeedForNewUser()

4. Strategy Tests:
   - testTimestampSort()
   - testRelevanceSort()

Integration Tests:
   - testCompleteUserJourney()
     1. Create user
     2. Follow others
     3. Create posts
     4. Like/comment
     5. View feed
     6. Verify correct posts shown

Performance Tests:
   - testFeedGeneration1000Followings()
   - testConcurrent100UsersPostingSimultaneously()
   - testFeedPagination()

Edge Cases:
   - User with no followers
   - User with no followings
   - Deleted posts in feed
   - Deactivated users
   - Circular follows
```

---

### **Q10: How to handle privacy settings?**

**Answer:**
```
Privacy Levels:

1. Extend Post model:
   ```java
   public enum PrivacyLevel {
       PUBLIC,        // Anyone can see
       FOLLOWERS,     // Only followers
       FRIENDS,       // Mutual followers
       PRIVATE        // Only me
   }

   public class Post {
       private PrivacyLevel privacy;
   }
   ```

2. Filter in feed generation:
   ```java
   public List<Post> generateFeed(User viewer, User author) {
       List<Post> posts = postService.getPostsByUser(author.getUserId());

       return posts.stream()
           .filter(post -> canView(viewer, author, post))
           .collect(Collectors.toList());
   }

   private boolean canView(User viewer, User author, Post post) {
       switch (post.getPrivacy()) {
           case PUBLIC:
               return true;
           case FOLLOWERS:
               return author.getFollowers().contains(viewer.getUserId());
           case FRIENDS:
               return author.getFollowers().contains(viewer.getUserId()) &&
                      author.getFollowing().contains(viewer.getUserId());
           case PRIVATE:
               return viewer.getUserId().equals(author.getUserId());
           default:
               return false;
       }
   }
   ```

3. Performance:
   - Cache privacy checks (rarely change)
   - Filter at query level (database)
   - Use bitmap for privacy flags
```

---

### **Q11: How to implement "Hashtags"?**

**Answer:**
```
Hashtag System:

1. Parse and store:
   ```java
   public class Post {
       private Set<String> hashtags;
   }

   public class HashtagParser {
       public Set<String> extractHashtags(String content) {
           Set<String> tags = new HashSet<>();
           Pattern pattern = Pattern.compile("#(\\w+)");
           Matcher matcher = pattern.matcher(content);

           while (matcher.find()) {
               tags.add(matcher.group(1).toLowerCase());
           }
           return tags;
       }
   }
   ```

2. Inverted Index:
   ```java
   // hashtag -> list of postIds
   Map<String, List<String>> hashtagIndex;

   public void indexPost(Post post) {
       for (String tag : post.getHashtags()) {
           hashtagIndex
               .computeIfAbsent(tag, k -> new ArrayList<>())
               .add(post.getPostId());
       }
   }
   ```

3. Search by hashtag:
   ```java
   public List<Post> searchByHashtag(String hashtag) {
       List<String> postIds = hashtagIndex.get(hashtag.toLowerCase());
       return postIds.stream()
           .map(id -> posts.get(id))
           .sorted(Comparator.comparing(Post::getTimestamp).reversed())
           .collect(Collectors.toList());
   }
   ```

4. Trending Hashtags:
   - Count posts per hashtag in last 24 hours
   - Sort by count
   - Cache in Redis Sorted Set
   - Update every 15 minutes
```

---

### **Q12: How to ensure thread safety for concurrent access?**

**Answer:**
```
Thread Safety Strategies:

1. Current Implementation:
   ✅ ConcurrentHashMap for users and posts
   ✅ Thread-safe collections
   ✅ Immutable responses (defensive copies)

2. Concurrency Issues to Handle:

   a) Like Race Condition:
      ```java
      // ❌ Not thread-safe
      public void likePost(String postId, String userId) {
          Set<String> likes = post.getLikes();
          likes.add(userId);  // Race condition!
      }

      // ✅ Thread-safe
      public synchronized void likePost(String postId, String userId) {
          post.getLikes().add(userId);
      }
      ```

   b) Follow Race Condition:
      ```java
      // ✅ Atomic follow operation
      public synchronized boolean follow(String follower, String followee) {
          // Both updates in single synchronized block
          users.get(follower).addFollowing(followee);
          users.get(followee).addFollower(follower);
          return true;
      }
      ```

   c) Feed Generation:
      - Read-only operation (no locking needed)
      - Snapshot consistency (reads might be slightly stale)
      - Acceptable for social media (eventual consistency)

3. Database-level:
   - Use transactions for multi-table updates
   - Optimistic locking with version numbers
   - Row-level locks for critical operations

4. For High Scale:
   - Message queue for async updates
   - Eventually consistent reads acceptable
   - Strong consistency only for critical operations (payments)
```

---

## **⚠️ Known Limitations & Trade-offs**

### **1. No Persistence**
- **Current:** In-memory only
- **Fix:** Add Repository layer with database
- **Trade-off:** Simplicity vs durability

### **2. Pull Model Performance**
- **Current:** Slow for users with 1000+ followings
- **Fix:** Hybrid model or caching
- **Trade-off:** Read speed vs write complexity

### **3. No Media Storage**
- **Current:** Only text content
- **Fix:** Integrate S3/CDN for images/videos
- **Trade-off:** Complexity vs features

### **4. Simple Relevance Scoring**
- **Current:** Only timestamp-based sorting
- **Fix:** ML model for personalized ranking
- **Trade-off:** Simple vs personalized

### **5. No Eventual Consistency Handling**
- **Current:** Assumes instant consistency
- **Fix:** Add version vectors, conflict resolution
- **Trade-off:** Works for demo, not production

---

## **📚 Key Takeaways**

**Design Patterns Used:**
- ✅ Observer Pattern (Follow/Unfollow)
- ✅ Strategy Pattern (Feed sorting)
- ✅ Factory Pattern (Post creation)
- ✅ Service Layer Pattern (Separation of concerns)

**SOLID Principles:**
- ✅ All 5 principles demonstrated
- ✅ Especially strong on Open/Closed (Strategy pattern)

**Extensibility:**
- ✅ Easy to add new post types
- ✅ Easy to add new sorting strategies
- ✅ Easy to add notification system (Observer)

**Interview Focus Points:**
- Pull vs Push Model trade-offs
- Observer Pattern for follows
- Strategy Pattern for sorting
- Scalability considerations
- Thread safety for concurrent access

---

## **🎓 What You Should Master**

Before interview, ensure you can:

1. ✅ **Explain Pull vs Push models** with examples
2. ✅ **Draw architecture diagram** from memory
3. ✅ **Code feed generation** in 10 minutes
4. ✅ **Discuss scalability** for millions of users
5. ✅ **Explain Observer Pattern** usage here
6. ✅ **Add trending feature** in 5 minutes
7. ✅ **Discuss thread safety** challenges
8. ✅ **Compare with real systems** (Twitter, Instagram)
9. ✅ **Propose hybrid model** for optimization
10. ✅ **Answer all Q&A sections** confidently

---

## **🎯 Practice Exercises**

1. **Add relevance-based sorting** strategy
2. **Implement hashtag** search functionality
3. **Add notification system** using Observer pattern
4. **Implement retweet/share** feature
5. **Add privacy settings** to posts
6. **Create trending feed** (separate from personalized)
7. **Implement stories** with 24-hour expiration
8. **Add spam detection** logic

---

## **📊 Complexity Analysis**

**Time Complexity:**
- Create post: O(1)
- Like post: O(1)
- Comment: O(1)
- Follow: O(1)
- Generate feed (Pull): O(F * P + F * P * log(F * P))
  - F = followings count
  - P = posts per user
  - Dominated by sorting
- Generate feed (Push): O(1) read, O(F) write

**Space Complexity:**
- Users: O(U)
- Posts: O(P)
- Comments: O(C)
- Follow graph: O(U * F_avg)
- Pull Model: O(P) (only posts)
- Push Model: O(U * P) (pre-computed feeds)

---

## **🎬 Pro Tips for Interviews**

### **During Discussion:**
1. **Start with Pull Model** (simpler to code)
2. **Mention Push Model** as optimization
3. **Discuss Hybrid** for production
4. **Show Observer Pattern** usage
5. **Mention Strategy** for sorting

### **Common Follow-ups:**
- "How to optimize for celebrities?" → Hybrid model
- "How to show trending posts?" → Separate trending algorithm
- "How to handle spam?" → Rate limiting + ML
- "Thread safety?" → ConcurrentHashMap + synchronized methods

### **Red Flags to Avoid:**
- ❌ Only implementing Push without discussing Pull
- ❌ Not mentioning trade-offs
- ❌ Ignoring scalability
- ❌ No design patterns

### **Green Flags (Impress Interviewer):**
- ✅ Discuss Pull vs Push vs Hybrid
- ✅ Mention real systems (Twitter's architecture)
- ✅ Show Observer Pattern usage
- ✅ Discuss caching strategies
- ✅ Mention Redis for feed storage

---

**Time to master:** 3-4 hours

**Difficulty:** ⭐⭐⭐ (Medium-High)

**Interview Frequency:** ⭐⭐⭐ (Very Common - Asked at Facebook, Twitter, Instagram-type companies)
