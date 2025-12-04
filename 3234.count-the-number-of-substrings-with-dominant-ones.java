/*
 * @lc app=leetcode id=3234 lang=java
 *
 * [3234] Count the Number of Substrings With Dominant Ones
 *
 * https://leetcode.com/problems/count-the-number-of-substrings-with-dominant-ones/description/
 *
 * algorithms
 * Medium (17.68%)
 * Likes:    488
 * Dislikes: 112
 * Total Accepted:    38.4K
 * Total Submissions: 107.1K
 * Testcase Example:  '"00011"'
 *
 * You are given a binary string s.
 * 
 * Return the number of substrings with dominant ones.
 * 
 * A string has dominant ones if the number of ones in the string is greater
 * than or equal to the square of the number of zeros in the string.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "00011"
 * 
 * Output: 5
 * 
 * Explanation:
 * 
 * The substrings with dominant ones are shown in the table
 * below.
 * 
 * 
 * 
 * 
 * 
 * i
 * j
 * s[i..j]
 * Number of Zeros
 * Number of Ones
 * 
 * 
 * 
 * 
 * 3
 * 3
 * 1
 * 0
 * 1
 * 
 * 
 * 4
 * 4
 * 1
 * 0
 * 1
 * 
 * 
 * 2
 * 3
 * 01
 * 1
 * 1
 * 
 * 
 * 3
 * 4
 * 11
 * 0
 * 2
 * 
 * 
 * 2
 * 4
 * 011
 * 1
 * 2
 * 
 * 
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = "101101"
 * 
 * Output: 16
 * 
 * Explanation:
 * 
 * The substrings with non-dominant ones are shown in the table below.
 * 
 * Since there are 21 substrings total and 5 of them have non-dominant ones, it
 * follows that there are 16 substrings with dominant
 * ones.
 * 
 * 
 * 
 * 
 * 
 * i
 * j
 * s[i..j]
 * Number of Zeros
 * Number of Ones
 * 
 * 
 * 
 * 
 * 1
 * 1
 * 0
 * 1
 * 0
 * 
 * 
 * 4
 * 4
 * 0
 * 1
 * 0
 * 
 * 
 * 1
 * 4
 * 0110
 * 2
 * 2
 * 
 * 
 * 0
 * 4
 * 10110
 * 2
 * 3
 * 
 * 
 * 1
 * 5
 * 01101
 * 2
 * 3
 * 
 * 
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length <= 4 * 10^4
 * s consists only of characters '0' and '1'.
 * 
 * 
 */

// @lc code=start
class Solution {
    public int numberOfSubstrings(String s) {
        // we will use a sliding window to see dominant strings
        // first we will create prefix sum
        int size = s.length();
        int[] prefix = new int[size + 1];
        for(int i = 0; i < size; i++) {
            if (i == 0 || (i > 0 && s.charAt(i - 1) == '0')) {
                prefix[i + 1] = i;
            } else {
                prefix[i + 1] = prefix[i];
            }
        }
        int ans = 0;
        for (int i = 1; i <= size; i++) {
            int cnt0 = s.charAt(i - 1) == '0' ? 1 : 0;
            int j = i;
            while (j > 0 && cnt0 * cnt0 <= size) {
                int cnt1 = (i - prefix[j]) - cnt0;
                if (cnt0 * cnt0 <= cnt1) {
                    ans += Math.min(j - prefix[j], cnt1 - cnt0 * cnt0 + 1);
                }
                j = prefix[j];
                cnt0++;
            }
        }
        return ans;

    }
}
// @lc code=end
