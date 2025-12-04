/*
 * @lc app=leetcode id=213 lang=java
 *
 * [213] House Robber II
 *
 * https://leetcode.com/problems/house-robber-ii/description/
 *
 * algorithms
 * Medium (44.20%)
 * Likes:    10827
 * Dislikes: 182
 * Total Accepted:    1.1M
 * Total Submissions: 2.6M
 * Testcase Example:  '[2,3,2]'
 *
 * You are a professional robber planning to rob houses along a street. Each
 * house has a certain amount of money stashed. All houses at this place are
 * arranged in a circle. That means the first house is the neighbor of the last
 * one. Meanwhile, adjacent houses have a security system connected, and it
 * will automatically contact the police if two adjacent houses were broken
 * into on the same night.
 * 
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the
 * police.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: nums = [2,3,2]
 * Output: 3
 * Explanation: You cannot rob house 1 (money = 2) and then rob house 3 (money
 * = 2), because they are adjacent houses.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: nums = [1,2,3,1]
 * Output: 4
 * Explanation: Rob house 1 (money = 1) and then rob house 3 (money = 3).
 * Total amount you can rob = 1 + 3 = 4.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: nums = [1,2,3]
 * Output: 3
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= nums.length <= 100
 * 0 <= nums[i] <= 1000
 * 
 * 
 */

// @lc code=start
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if(n == 1){
            return nums[0];
        }
        // we will need 2 DP arrays to make choice about the first house, 
        // either pick that or not
        int[] dp = new int[n];
        dp[0] =  nums[0];
        dp[1] = Math.max(dp[0], nums[1]);
        for(int i = 2; i < n;i++){
            dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
        }
        int[] dp2 = new int[nums.length];
        dp2[0] = 0;
        dp2[1] = nums[1];
        for (int i = 2; i < nums.length; i++) {
            dp2[i] = Math.max(dp2[i - 1], dp2[i - 2] + nums[i]);
        }
        return Math.max(dp[n-2], dp2[n-1]);

 
        
    }
}
// @lc code=end

