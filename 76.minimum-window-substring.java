/*
 * @lc app=leetcode id=76 lang=java
 *
 * [76] Minimum Window Substring
 *
 * https://leetcode.com/problems/minimum-window-substring/description/
 *
 * algorithms
 * Hard (46.30%)
 * Likes:    19563
 * Dislikes: 826
 * Total Accepted:    1.9M
 * Total Submissions: 4.2M
 * Testcase Example:  '"ADOBECODEBANC"\n"ABC"'
 *
 * Given two strings s and t of lengths m and n respectively, return the
 * minimum window substring of s such that every character in t (including
 * duplicates) is included in the window. If there is no such substring, return
 * the empty string "".
 * 
 * The testcases will be generated such that the answer is unique.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "ADOBECODEBANC", t = "ABC"
 * Output: "BANC"
 * Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C'
 * from string t.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = "a", t = "a"
 * Output: "a"
 * Explanation: The entire string s is the minimum window.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: s = "a", t = "aa"
 * Output: ""
 * Explanation: Both 'a's from t must be included in the window.
 * Since the largest window of s only has one 'a', return empty string.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == s.length
 * n == t.length
 * 1 <= m, n <= 10^5
 * s and t consist of uppercase and lowercase English letters.
 * 
 * 
 * 
 * Follow up: Could you find an algorithm that runs in O(m + n) time?
 * 
 */

// @lc code=start

import java.util.Map;
import java.util.HashMap;

class Solution {
    public String minWindow(String s, String t) {
        Map<Character, Integer> charCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        int left = 0, right = 0, count = t.length();
        int minLeft = 0, minLen = Integer.MAX_VALUE;
        String resString = "";
        // keep adding comments at each step what we are doing.
        // looping 's' for whole length
        while (right < s.length()) {
            char c = s.charAt(right);
            if (charCount.containsKey(c)) {
                if (charCount.get(c) > 0) {
                    count--;
                }
                charCount.put(c, charCount.get(c) - 1);
            }
            right++;

            // if count is 0, we found a valid substring
            while (count == 0) {
                // if the current substring is smaller than the previous one, update it
                if (right - left < minLen) {
                    minLen = right - left;
                    minLeft = left;
                }
                char c2 = s.charAt(left);
                if (charCount.containsKey(c2)) {
                    charCount.put(c2, charCount.get(c2) + 1);
                    if (charCount.get(c2) > 0) {
                        count++;
                    }
                }
                left++;
            }
        }
        resString = minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLen);
        return resString;
    }
}
// @lc code=end

