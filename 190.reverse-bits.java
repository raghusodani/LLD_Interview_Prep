/*
 * @lc app=leetcode id=190 lang=java
 *
 * [190] Reverse Bits
 *
 * https://leetcode.com/problems/reverse-bits/description/
 *
 * algorithms
 * Easy (64.80%)
 * Likes:    5534
 * Dislikes: 1637
 * Total Accepted:    1.1M
 * Total Submissions: 1.7M
 * Testcase Example:  '43261596'
 *
 * Reverse bits of a given 32 bits signed integer.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: n = 43261596
 * 
 * Output: 964176192
 * 
 * Explanation:
 * 
 * 
 * 
 * 
 * Integer
 * Binary
 * 
 * 
 * 43261596
 * 00000010100101000001111010011100
 * 
 * 
 * 964176192
 * 00111001011110000010100101000000
 * 
 * 
 * 
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: n = 2147483644
 * 
 * Output: 1073741822
 * 
 * Explanation:
 * 
 * 
 * 
 * 
 * Integer
 * Binary
 * 
 * 
 * 2147483644
 * 01111111111111111111111111111100
 * 
 * 
 * 1073741822
 * 00111111111111111111111111111110
 * 
 * 
 * 
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 0 <= n <= 2^31 - 2
 * n is even.
 * 
 * 
 * 
 * Follow up: If this function is called many times, how would you optimize it?
 * 
 */

// @lc code=start
class Solution {
    public int reverseBits(int n) {
        int res = 0;
        for (int i = 0; i < 32; i++) {
            res = (res << 1) | (n & 1);
            n = n >> 1;
        }
        return res;
    }
}
// @lc code=end

