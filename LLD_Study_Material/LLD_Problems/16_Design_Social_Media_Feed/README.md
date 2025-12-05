# Social Media Feed System 📱

## **Quick Start**

```bash
cd src
javac Main.java
java Main
```

## **What This Demonstrates**

✅ **Observer Pattern** - Follow/Unfollow mechanism
✅ **Strategy Pattern** - Feed sorting algorithms
✅ **Factory Pattern** - Post creation
✅ **Pull Model** - On-demand feed generation
✅ **Service Layer** - Clean separation of concerns

## **Key Features**

- User creation and management
- Follow/Unfollow functionality (bidirectional)
- Post creation (TEXT, IMAGE, VIDEO, LINK)
- Like/Unlike posts
- Comment on posts
- Personalized feed generation
- Feed sorted by timestamp (most recent first)
- Unfollow updates feed in real-time

## **Architecture**

```
Services Layer:
- UserService    → User & relationship management
- PostService    → Post CRUD & interactions
- FeedService    → Feed generation & display

Models:
- User           → User data & follow lists
- Post           → Post content & engagement
- Comment        → Comment data

Feed Generation:
- PullFeedGenerator      → Pull Model implementation
- FeedSortStrategy       → Strategy interface
- TimestampSortStrategy  → Concrete strategy
```

## **Design Patterns**

### **1. Observer Pattern**
Users "observe" activities of people they follow
- Followers are observers
- Followees are subjects
- Feed shows observed activities

### **2. Strategy Pattern**
Pluggable sorting algorithms for feed
- TimestampSortStrategy (implemented)
- RelevanceSortStrategy (future)
- TrendingSortStrategy (future)

### **3. Factory Pattern**
Centralized post creation with ID generation

## **Pull vs Push Model**

**Our Implementation: Pull Model**
- ✅ Query posts when user views feed
- ✅ Always fresh data
- ✅ No storage overhead
- ✅ Simple to implement

**Alternative: Push Model**
- Fanout posts to all followers on creation
- Pre-computed feeds
- Fast reads, expensive writes
- Used for celebrities in production

**Read SOLUTION.md** for detailed comparison and hybrid approach!

## **Sample Output**

```
📱 FEED FOR @ALICE
┌─────────────────────────────────────────────────────┐
│ Post by: @Alice (LINK)
│ Time: 2025-12-05T09:40:08.074116
├─────────────────────────────────────────────────────┤
│ Great article on system design
├─────────────────────────────────────────────────────┤
│ 👍 0 likes  💬 0 comments
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ Post by: @Bob (IMAGE)
│ Time: 2025-12-05T09:40:07.863710
├─────────────────────────────────────────────────────┤
│ Check out this amazing sunset! 🌅
├─────────────────────────────────────────────────────┤
│ 👍 2 likes  💬 1 comments
└─────────────────────────────────────────────────────┘
  Comments:
   └─ @Alice: Beautiful picture Bob!
```

## **Interview Focus**

**Must Know:**
- Pull vs Push model trade-offs
- Observer Pattern for social graph
- Strategy Pattern for feed customization
- Scalability for millions of users
- Thread safety considerations

**Common Questions:**
1. How to handle celebrities with millions of followers?
2. How to implement trending posts?
3. How to add hashtags?
4. How to prevent spam?
5. How to implement stories (24-hour posts)?

**All answers in SOLUTION.md!** 📖

## **Extensions**

Easy to add:
- Relevance-based sorting
- Hashtag search
- Retweet/Share
- Privacy settings
- Notifications
- Stories
- Trending feed

**Difficulty:** ⭐⭐⭐ (Medium-High)

**Interview Frequency:** ⭐⭐⭐ (Very Common at social media companies!)
