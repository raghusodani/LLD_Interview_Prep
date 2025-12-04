/*
 * @lc app=leetcode id=191 lang=java
 *
 * [191] Number of 1 Bits
 *
 * https://leetcode.com/problems/number-of-1-bits/description/
 *
 * algorithms
 * Easy (75.59%)
 * Likes:    7028
 * Dislikes: 1360
 * Total Accepted:    2M
 * Total Submissions: 2.6M
 * Testcase Example:  '11'
 *
 * Given a positive integer n, write a function that returns the number of set
 * bits in its binary representation (also known as the Hamming weight).
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: n = 11
 * 
 * Output: 3
 * 
 * Explanation:
 * 
 * The input binary string 1011 has a total of three set bits.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: n = 128
 * 
 * Output: 1
 * 
 * Explanation:
 * 
 * The input binary string 10000000 has a total of one set bit.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: n = 2147483645
 * 
 * Output: 30
 * 
 * Explanation:
 * 
 * The input binary string 1111111111111111111111111111101 has a total of
 * thirty set bits.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= n <= 2^31 - 1
 * 
 * 
 * 
 * Follow up: If this function is called many times, how would you optimize it?
 */

// @lc code=start
class Solution {
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n = n & (n - 1);
            count++;
        }
        return count;
        
    }
}
// @lc code=end

