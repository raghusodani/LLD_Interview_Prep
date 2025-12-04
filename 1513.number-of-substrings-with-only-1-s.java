/*
 * @lc app=leetcode id=1513 lang=java
 *
 * [1513] Number of Substrings With Only 1s
 *
 * https://leetcode.com/problems/number-of-substrings-with-only-1s/description/
 *
 * algorithms
 * Medium (48.29%)
 * Likes:    1124
 * Dislikes: 39
 * Total Accepted:    115.6K
 * Total Submissions: 207.8K
 * Testcase Example:  '"0110111"'
 *
 * Given a binary string s, return the number of substrings with all characters
 * 1's. Since the answer may be too large, return it modulo 10^9 + 7.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "0110111"
 * Output: 9
 * Explanation: There are 9 substring in total with only 1's characters.
 * "1" -> 5 times.
 * "11" -> 3 times.
 * "111" -> 1 time.
 * 
 * Example 2:
 * 
 * 
 * Input: s = "101"
 * Output: 2
 * Explanation: Substring "1" is shown 2 times in s.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: s = "111111"
 * Output: 21
 * Explanation: Each substring contains only 1's characters.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length <= 10^5
 * s[i] is either '0' or '1'.
 * 
 * 
 */

// @lc code=start
class Solution {
    public int numSub(String s) {
        int count = 0;
        int res = 0;
        int mod = (int)1e9 + 7;
        for (int i = 0; i < s.length(); i++) {
            count = s.charAt(i) == '1' ? count + 1 : 0;
            res = (res + count) % mod;
        }
        return res;

        
    }
}
// @lc code=end

