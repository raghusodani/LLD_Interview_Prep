/*
 * @lc app=leetcode id=209 lang=java
 *
 * [209] Minimum Size Subarray Sum
 *
 * https://leetcode.com/problems/minimum-size-subarray-sum/description/
 *
 * algorithms
 * Medium (50.25%)
 * Likes:    13891
 * Dislikes: 520
 * Total Accepted:    1.6M
 * Total Submissions: 3.3M
 * Testcase Example:  '7\n[2,3,1,2,4,3]'
 *
 * Given an array of positive integers nums and a positive integer target,
 * return the minimal length of a subarray whose sum is greater than or equal
 * to target. If there is no such subarray, return 0 instead.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: target = 7, nums = [2,3,1,2,4,3]
 * Output: 2
 * Explanation: The subarray [4,3] has the minimal length under the problem
 * constraint.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: target = 4, nums = [1,4,4]
 * Output: 1
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: target = 11, nums = [1,1,1,1,1,1,1,1]
 * Output: 0
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= target <= 10^9
 * 1 <= nums.length <= 10^5
 * 1 <= nums[i] <= 10^4
 * 
 * 
 * 
 * Follow up: If you have figured out the O(n) solution, try coding another
 * solution of which the time complexity is O(n log(n)).
 */

// @lc code=start
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        return minSubArrayLen2(target, nums);
        // int n = nums.length;
        // int ans = Integer.MAX_VALUE;
        // int left = 0;
        // int sum = 0;
        // for (int i = 0; i < n; i++) {
        //     sum += nums[i];
        //     while (sum >= target) {
        //         ans = Math.min(ans, i + 1 - left);
        //         sum -= nums[left++];
        //     }
        // }
        // return (ans != Integer.MAX_VALUE) ? ans : 0;
        
    }

    // now a new solution that uses O(n log(n)), explain in comments as well
    // 
    public int minSubArrayLen2(int target, int[] nums) {
        int n = nums.length;
        int[] prefix = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prefix[i] = prefix[i - 1] + nums[i - 1];
        }
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int left = i + 1, right = n;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (prefix[mid] - prefix[i] >= target) {
                    ans = Math.min(ans, mid - i);
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
        }
        return (ans != Integer.MAX_VALUE) ? ans : 0;
    }
}
// @lc code=end

