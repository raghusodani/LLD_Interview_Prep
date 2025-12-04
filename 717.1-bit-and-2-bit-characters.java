/*
 * @lc app=leetcode id=717 lang=java
 *
 * [717] 1-bit and 2-bit Characters
 *
 * https://leetcode.com/problems/1-bit-and-2-bit-characters/description/
 *
 * algorithms
 * Easy (45.27%)
 * Likes:    1188
 * Dislikes: 2344
 * Total Accepted:    246.5K
 * Total Submissions: 499.5K
 * Testcase Example:  '[1,0,0]'
 *
 * We have two special characters:
 * 
 * 
 * The first character can be represented by one bit 0.
 * The second character can be represented by two bits (10 or 11).
 * 
 * 
 * Given a binary array bits that ends with 0, return true if the last
 * character must be a one-bit character.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: bits = [1,0,0]
 * Output: true
 * Explanation: The only way to decode it is two-bit character and one-bit
 * character.
 * So the last character is one-bit character.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: bits = [1,1,1,0]
 * Output: false
 * Explanation: The only way to decode it is two-bit character and two-bit
 * character.
 * So the last character is not one-bit character.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= bits.length <= 1000
 * bits[i] is either 0 or 1.
 * 
 * 
 */

// @lc code=start
class Solution {
    public boolean isOneBitCharacter(int[] bits) {
        int i = 0;
        while (i < bits.length - 1) {
            if (bits[i] == 1) {
                i += 2;
            } else {
                i++;
            }
        }
        return i == bits.length - 1;
        
    }
}
// @lc code=end

