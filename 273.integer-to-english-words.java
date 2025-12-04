/*
 * @lc app=leetcode id=273 lang=java
 *
 * [273] Integer to English Words
 *
 * https://leetcode.com/problems/integer-to-english-words/description/
 *
 * algorithms
 * Hard (34.65%)
 * Likes:    3801
 * Dislikes: 6823
 * Total Accepted:    566.4K
 * Total Submissions: 1.6M
 * Testcase Example:  '123'
 *
 * Convert a non-negative integer num to its English words representation.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: num = 123
 * Output: "One Hundred Twenty Three"
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: num = 12345
 * Output: "Twelve Thousand Three Hundred Forty Five"
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: num = 1234567
 * Output: "One Million Two Hundred Thirty Four Thousand Five Hundred Sixty
 * Seven"
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 0 <= num <= 2^31 - 1
 * 
 * 
 */

// @lc code=start
class Solution {
    public String numberToWords(int num) {
        if (num == 0) {
            return "Zero";
        }

        String[] units = {"", "Thousand", "Million", "Billion"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};

        StringBuilder sb = new StringBuilder();
        int unitIndex = 0;
        while (num > 0) {
            if (num % 1000 != 0) {
                StringBuilder temp = new StringBuilder();
                int n = num % 1000;
                if (n >= 100) {
                    temp.append(ones[n / 100]).append(" Hundred ");
                    n %= 100;
                }
                if (n >= 20) {
                    temp.append(tens[n / 10]).append(" ");
                    n %= 10;
                }
                if (n >= 1) {
                    temp.append(ones[n]).append(" ");
                }
                temp.append(units[unitIndex]).append(" ");
                sb.insert(0, temp.toString());
            }
            num /= 1000;
            unitIndex++;
        }
        return sb.toString().trim();
        
    }
}
// @lc code=end

