package Services;

import Models.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private Map<String, User> users;
    private static int userCounter = 1;

    public UserService() {
        this.users = new ConcurrentHashMap<>();
    }

    public User createUser(String username) {
        String userId = "U" + (userCounter++);
        User user = new User(userId, username);
        users.put(userId, user);
        return user;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * User A follows User B
     * Observer Pattern: A observes B's activities
     */
    public boolean follow(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower == null || followee == null || followerId.equals(followeeId)) {
            return false;
        }

        // Add to follower's following list
        follower.addFollowing(followeeId);

        // Add to followee's followers list
        followee.addFollower(followerId);

        System.out.println("✅ @" + follower.getUsername() + " is now following @" + followee.getUsername());
        return true;
    }

    /**
     * User A unfollows User B
     */
    public boolean unfollow(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower == null || followee == null) {
            return false;
        }

        // Remove from follower's following list
        follower.removeFollowing(followeeId);

        // Remove from followee's followers list
        followee.removeFollower(followerId);

        System.out.println("❌ @" + follower.getUsername() + " unfollowed @" + followee.getUsername());
        return true;
    }

    public List<User> getFollowers(String userId) {
        User user = users.get(userId);
        if (user == null) return new ArrayList<>();

        List<User> followerList = new ArrayList<>();
        for (String followerId : user.getFollowers()) {
            followerList.add(users.get(followerId));
        }
        return followerList;
    }

    public List<User> getFollowing(String userId) {
        User user = users.get(userId);
        if (user == null) return new ArrayList<>();

        List<User> followingList = new ArrayList<>();
        for (String followingId : user.getFollowing()) {
            followingList.add(users.get(followingId));
        }
        return followingList;
    }
}
