/*
 * @lc app=leetcode id=205 lang=java
 *
 * [205] Isomorphic Strings
 *
 * https://leetcode.com/problems/isomorphic-strings/description/
 *
 * algorithms
 * Easy (47.58%)
 * Likes:    10190
 * Dislikes: 2258
 * Total Accepted:    2M
 * Total Submissions: 4.2M
 * Testcase Example:  '"egg"\n"add"'
 *
 * Given two strings s and t, determine if they are isomorphic.
 * 
 * Two strings s and t are isomorphic if the characters in s can be replaced to
 * get t.
 * 
 * All occurrences of a character must be replaced with another character while
 * preserving the order of characters. No two characters may map to the same
 * character, but a character may map to itself.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "egg", t = "add"
 * 
 * Output: true
 * 
 * Explanation:
 * 
 * The strings s and t can be made identical by:
 * 
 * 
 * Mapping 'e' to 'a'.
 * Mapping 'g' to 'd'.
 * 
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = "foo", t = "bar"
 * 
 * Output: false
 * 
 * Explanation:
 * 
 * The strings s and t can not be made identical as 'o' needs to be mapped to
 * both 'a' and 'r'.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: s = "paper", t = "title"
 * 
 * Output: true
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length <= 5 * 10^4
 * t.length == s.length
 * s and t consist of any valid ascii character.
 * 
 * 
 */

// @lc code=start
import java.util.HashMap;
class Solution {
    public boolean isIsomorphic(String s, String t) {
        HashMap<Character, Character> map = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);
            if (map.containsKey(c1)) {
                if (map.get(c1) != c2) {
                    return false;
                }
            } else {
                if (map.containsValue(c2)) {
                    return false;
                }
                map.put(c1, c2);
            }
        }
        return true;
        
    }
}
// @lc code=end

