import Models.*;
import Services.*;
import CommonEnum.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   SOCIAL MEDIA FEED SYSTEM - DEMO             ║");
        System.out.println("║   Pull Model | Observer Pattern | Strategy    ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // Initialize services
        UserService userService = new UserService();
        PostService postService = new PostService();
        FeedService feedService = new FeedService(postService);

        System.out.println("━━━ STEP 1: Create Users ━━━\n");

        User alice = userService.createUser("Alice");
        User bob = userService.createUser("Bob");
        User charlie = userService.createUser("Charlie");
        User david = userService.createUser("David");

        System.out.println("✅ Created users: " + alice.getUsername() + ", " + bob.getUsername() +
                          ", " + charlie.getUsername() + ", " + david.getUsername());

        System.out.println("\n━━━ STEP 2: Users Follow Each Other ━━━\n");

        // Alice follows Bob and Charlie
        userService.follow(alice.getUserId(), bob.getUserId());
        userService.follow(alice.getUserId(), charlie.getUserId());

        // Bob follows Alice and David
        userService.follow(bob.getUserId(), alice.getUserId());
        userService.follow(bob.getUserId(), david.getUserId());

        // Charlie follows everyone
        userService.follow(charlie.getUserId(), alice.getUserId());
        userService.follow(charlie.getUserId(), bob.getUserId());
        userService.follow(charlie.getUserId(), david.getUserId());

        System.out.println("\n━━━ STEP 3: Users Create Posts ━━━\n");

        // Simulate time delay for different timestamps
        Post post1 = postService.createPost(alice.getUserId(), alice.getUsername(),
                                           "Hello everyone! This is my first post!", PostType.TEXT);

        try { Thread.sleep(100); } catch (InterruptedException e) {}

        Post post2 = postService.createPost(bob.getUserId(), bob.getUsername(),
                                           "Check out this amazing sunset! 🌅", PostType.IMAGE);

        try { Thread.sleep(100); } catch (InterruptedException e) {}

        Post post3 = postService.createPost(charlie.getUserId(), charlie.getUsername(),
                                           "New video tutorial on design patterns!", PostType.VIDEO);

        try { Thread.sleep(100); } catch (InterruptedException e) {}

        Post post4 = postService.createPost(alice.getUserId(), alice.getUsername(),
                                           "Great article on system design", PostType.LINK);

        try { Thread.sleep(100); } catch (InterruptedException e) {}

        Post post5 = postService.createPost(david.getUserId(), david.getUsername(),
                                           "Working on a new project! Excited! 🚀", PostType.TEXT);

        System.out.println("\n━━━ STEP 4: Users Interact - Likes & Comments ━━━\n");

        // Bob likes Alice's first post
        postService.likePost(post1.getPostId(), bob.getUserId());
        System.out.println("👍 Bob liked Alice's post");

        // Charlie likes Alice's and Bob's posts
        postService.likePost(post1.getPostId(), charlie.getUserId());
        postService.likePost(post2.getPostId(), charlie.getUserId());
        System.out.println("👍 Charlie liked Alice's and Bob's posts");

        // David likes Bob's post
        postService.likePost(post2.getPostId(), david.getUserId());
        System.out.println("👍 David liked Bob's post");

        // Add comments
        postService.addComment(post1.getPostId(), bob.getUserId(), bob.getUsername(),
                              "Welcome Alice! Great to have you here!");
        postService.addComment(post1.getPostId(), charlie.getUserId(), charlie.getUsername(),
                              "Nice first post!");
        postService.addComment(post2.getPostId(), alice.getUserId(), alice.getUsername(),
                              "Beautiful picture Bob!");

        System.out.println("\n━━━ STEP 5: Generate Feeds (Pull Model) ━━━\n");

        // Alice's feed: Should show her own posts + Bob's and Charlie's posts (people she follows)
        feedService.displayFeed(alice, 10);

        // Bob's feed: Should show his own posts + Alice's and David's posts
        feedService.displayFeed(bob, 10);

        // Charlie's feed: Should show his own posts + everyone's posts (he follows all)
        feedService.displayFeed(charlie, 10);

        System.out.println("\n━━━ STEP 6: Unfollow Scenario ━━━\n");

        // Alice unfollows Bob
        userService.unfollow(alice.getUserId(), bob.getUserId());

        System.out.println("\n📱 Alice's Updated Feed (after unfollowing Bob):");
        feedService.displayFeed(alice, 10);

        System.out.println("\n━━━ STEP 7: New Posts After Unfollow ━━━\n");

        try { Thread.sleep(100); } catch (InterruptedException e) {}

        Post post6 = postService.createPost(bob.getUserId(), bob.getUsername(),
                                           "Alice won't see this post!", PostType.TEXT);

        Post post7 = postService.createPost(charlie.getUserId(), charlie.getUsername(),
                                           "But Alice will see this one!", PostType.TEXT);

        System.out.println("\n📱 Alice's Feed (Bob's new post NOT shown):");
        feedService.displayFeed(alice, 10);

        System.out.println("\n━━━ DEMO COMPLETE ━━━\n");
        System.out.println("✅ Key Features Demonstrated:");
        System.out.println("   - User creation and management");
        System.out.println("   - Follow/Unfollow (Observer Pattern)");
        System.out.println("   - Post creation with different types (Factory Pattern)");
        System.out.println("   - Likes and Comments");
        System.out.println("   - Pull Model feed generation");
        System.out.println("   - Feed sorted by timestamp (Strategy Pattern)");
        System.out.println("   - Real-time feed updates based on follows");
    }
}
