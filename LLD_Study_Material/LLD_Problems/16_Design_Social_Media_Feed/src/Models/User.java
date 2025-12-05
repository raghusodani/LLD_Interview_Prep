package Models;

import java.util.*;

public class User {
    private String userId;
    private String username;
    private Set<String> followers;      // Users who follow this user
    private Set<String> following;      // Users this user follows

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getFollowers() {
        return new HashSet<>(followers);
    }

    public Set<String> getFollowing() {
        return new HashSet<>(following);
    }

    public void addFollower(String userId) {
        followers.add(userId);
    }

    public void removeFollower(String userId) {
        followers.remove(userId);
    }

    public void addFollowing(String userId) {
        following.add(userId);
    }

    public void removeFollowing(String userId) {
        following.remove(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", followers=" + followers.size() +
                ", following=" + following.size() +
                '}';
    }
}
