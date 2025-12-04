/*
 * @lc app=leetcode id=115 lang=java
 *
 * [115] Distinct Subsequences
 *
 * https://leetcode.com/problems/distinct-subsequences/description/
 *
 * algorithms
 * Hard (51.11%)
 * Likes:    7261
 * Dislikes: 318
 * Total Accepted:    631K
 * Total Submissions: 1.2M
 * Testcase Example:  '"rabbbit"\n"rabbit"'
 *
 * Given two strings s and t, return the number of distinct subsequences of s
 * which equals t.
 * 
 * The test cases are generated so that the answer fits on a 32-bit signed
 * integer.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "rabbbit", t = "rabbit"
 * Output: 3
 * Explanation:
 * As shown below, there are 3 ways you can generate "rabbit" from s.
 * rabbbit
 * rabbbit
 * rabbbit
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = "babgbag", t = "bag"
 * Output: 5
 * Explanation:
 * As shown below, there are 5 ways you can generate "bag" from s.
 * babgbag
 * babgbag
 * babgbag
 * babgbag
 * babgbag
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length, t.length <= 1000
 * s and t consist of English letters.
 * 
 * 
 */

// @lc code=start
class Solution {
    public int numDistinct(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[m][n];
        
    }
}
// @lc code=end

