/*
 * @lc app=leetcode id=354 lang=java
 *
 * [354] Russian Doll Envelopes
 *
 * https://leetcode.com/problems/russian-doll-envelopes/description/
 *
 * algorithms
 * Hard (37.62%)
 * Likes:    6396
 * Dislikes: 166
 * Total Accepted:    277.5K
 * Total Submissions: 737.8K
 * Testcase Example:  '[[5,4],[6,4],[6,7],[2,3]]'
 *
 * You are given a 2D array of integers envelopes where envelopes[i] = [wi, hi]
 * represents the width and the height of an envelope.
 * 
 * One envelope can fit into another if and only if both the width and height
 * of one envelope are greater than the other envelope's width and height.
 * 
 * Return the maximum number of envelopes you can Russian doll (i.e., put one
 * inside the other).
 * 
 * Note: You cannot rotate an envelope.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: envelopes = [[5,4],[6,4],[6,7],[2,3]]
 * Output: 3
 * Explanation: The maximum number of envelopes you can Russian doll is 3
 * ([2,3] => [5,4] => [6,7]).
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: envelopes = [[1,1],[1,1],[1,1]]
 * Output: 1
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= envelopes.length <= 10^5
 * envelopes[i].length == 2
 * 1 <= wi, hi <= 10^5
 * 
 * 
 */

// @lc code=start
import java.util.Arrays;
class Solution {
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);
        int[] dp = new int[envelopes.length];
        int len = 0;
        for (int[] envelope : envelopes) {
            int index = Arrays.binarySearch(dp, 0, len, envelope[1]);
            if (index < 0) {
                index = -(index + 1);
            }
            dp[index] = envelope[1];
            if (index == len) {
                len++;
            }
        }
        return len;
        
    }
}
// @lc code=end

