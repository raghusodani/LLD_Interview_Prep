/*
 * @lc app=leetcode id=140 lang=java
 *
 * [140] Word Break II
 *
 * https://leetcode.com/problems/word-break-ii/description/
 *
 * algorithms
 * Hard (54.65%)
 * Likes:    7499
 * Dislikes: 545
 * Total Accepted:    787.2K
 * Total Submissions: 1.4M
 * Testcase Example:  '"catsanddog"\n["cat","cats","and","sand","dog"]'
 *
 * Given a string s and a dictionary of strings wordDict, add spaces in s to
 * construct a sentence where each word is a valid dictionary word. Return all
 * such possible sentences in any order.
 * 
 * Note that the same word in the dictionary may be reused multiple times in
 * the segmentation.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "catsanddog", wordDict = ["cat","cats","and","sand","dog"]
 * Output: ["cats and dog","cat sand dog"]
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = "pineapplepenapple", wordDict =
 * ["apple","pen","applepen","pine","pineapple"]
 * Output: ["pine apple pen apple","pineapple pen apple","pine applepen apple"]
 * Explanation: Note that you are allowed to reuse a dictionary word.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
 * Output: []
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length <= 20
 * 1 <= wordDict.length <= 1000
 * 1 <= wordDict[i].length <= 10
 * s and wordDict[i] consist of only lowercase English letters.
 * All the strings of wordDict are unique.
 * Input is generated in a way that the length of the answer doesn't exceed
 * 10^5.
 * 
 * 
 */

// @lc code=start
import java.util.*;
class Solution {
    private void dfs(String s, List<String> wordDict, String path, List<String> res, int index) {
        if (index == s.length()) {
            res.add(path.substring(1));
            return;
        }
        for (int i = index; i < s.length(); i++) {
            if (wordDict.contains(s.substring(index, i + 1))) {
                dfs(s, wordDict, path + " " + s.substring(index, i + 1), res, i + 1);
            }
        }
    }

    public List<String> wordBreak(String s, List<String> wordDict) {
        List<String> res = new ArrayList<>();
        if (s == null || s.length() == 0) {
            return res;
        }
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 0; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordDict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        if (dp[s.length()]) {
            dfs(s, wordDict, "", res, 0);
        }
        return res;

        
    }
}
// @lc code=end

