/*
 * @lc app=leetcode id=1437 lang=java
 *
 * [1437] Check If All 1's Are at Least Length K Places Away
 *
 * https://leetcode.com/problems/check-if-all-1s-are-at-least-length-k-places-away/description/
 *
 * algorithms
 * Easy (58.17%)
 * Likes:    782
 * Dislikes: 234
 * Total Accepted:    133.4K
 * Total Submissions: 215.4K
 * Testcase Example:  '[1,0,0,0,1,0,0,1]\n2'
 *
 * Given an binary array nums and an integer k, return true if all 1's are at
 * least k places away from each other, otherwise return false.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: nums = [1,0,0,0,1,0,0,1], k = 2
 * Output: true
 * Explanation: Each of the 1s are at least 2 places away from each other.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: nums = [1,0,0,1,0,1], k = 2
 * Output: false
 * Explanation: The second 1 and third 1 are only one apart from each
 * other.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= nums.length <= 10^5
 * 0 <= k <= nums.length
 * nums[i] is 0 or 1
 * 
 * 
 */

// @lc code=start
class Solution {
    public boolean kLengthApart(int[] nums, int k) {
        int lastOne = -1;
        for(int i = 0; i < nums.length; i++) {
            if(nums[i] == 1) {
                if(lastOne == -1) {
                    lastOne = i;
                } else {
                    if(i - lastOne - 1 < k) {
                        return false;
                    }
                    lastOne = i;
                }
            }
        }
        return true;
    }
}
// @lc code=end

