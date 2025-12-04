/*
 * @lc app=leetcode id=1262 lang=java
 *
 * [1262] Greatest Sum Divisible by Three
 *
 * https://leetcode.com/problems/greatest-sum-divisible-by-three/description/
 *
 * algorithms
 * Medium (51.10%)
 * Likes:    1937
 * Dislikes: 47
 * Total Accepted:    73.4K
 * Total Submissions: 141.6K
 * Testcase Example:  '[3,6,5,1,8]'
 *
 * Given an integer array nums, return the maximum possible sum of elements of
 * the array such that it is divisible by three.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: nums = [3,6,5,1,8]
 * Output: 18
 * Explanation: Pick numbers 3, 6, 1 and 8 their sum is 18 (maximum sum
 * divisible by 3).
 * 
 * Example 2:
 * 
 * 
 * Input: nums = [4]
 * Output: 0
 * Explanation: Since 4 is not divisible by 3, do not pick any number.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: nums = [1,2,3,4,4]
 * Output: 12
 * Explanation: Pick numbers 1, 3, 4 and 4 their sum is 12 (maximum sum
 * divisible by 3).
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= nums.length <= 4 * 10^4
 * 1 <= nums[i] <= 10^4
 * 
 * 
 */

// @lc code=start
class Solution {
    /**
     * Greatest Sum Divisible by Three - Dynamic Programming
     *
     * Key Insight: Track maximum sum for each remainder (0, 1, 2)
     *
     * dp[i] = maximum sum with remainder i when divided by 3
     *
     * For each number, we have 2 choices:
     * 1. Don't take it: keep current dp[i]
     * 2. Take it: update dp[(i + num) % 3] = dp[i] + num
     *
     * Time: O(n) - one pass through array
     * Space: O(1) - only 3 states
     */
    public int maxSumDivThree(int[] nums) {
        // dp[0] = max sum with remainder 0 (divisible by 3)
        // dp[1] = max sum with remainder 1
        // dp[2] = max sum with remainder 2

        // Use negative values to indicate "not possible"
        int[] dp = {0, Integer.MIN_VALUE, Integer.MIN_VALUE};

        for (int num : nums) {
            // Create temp array to avoid overwriting states we need
            int[] temp = dp.clone();

            for (int i = 0; i < 3; i++) {
                if (dp[i] == Integer.MIN_VALUE) continue;  // Skip invalid states

                // New remainder after adding num
                int newRemainder = (i + num) % 3;

                // Update: either keep old or take new
                temp[newRemainder] = Math.max(temp[newRemainder], dp[i] + num);
            }

            dp = temp;
        }

        return dp[0];
    }
}
// @lc code=end

/*
 * ALTERNATIVE SOLUTION: Greedy with removing the smallest
 *
 * public int maxSumDivThree(int[] nums) {
        int sum = 0;
        int oneRemainder = 20000;
        int twoRemainder = 20000;
        
        for(int num : nums)
        {
            sum += num;
            
            if(num % 3 == 1)
            {
                twoRemainder = Math.min(twoRemainder, oneRemainder + num);
                oneRemainder = Math.min(oneRemainder, num); 
            }
            
            if(num % 3 == 2)
            {
                oneRemainder = Math.min(oneRemainder, twoRemainder + num);
                twoRemainder = Math.min(twoRemainder, num);
                 
            }
        }
        
        if(sum % 3 == 0)
            return sum;
        
        if(sum % 3 == 1)
            return sum - oneRemainder;
        
        if(sum % 3 == 2)
            return sum - twoRemainder;
        
        return 0;
            
    }
 */

