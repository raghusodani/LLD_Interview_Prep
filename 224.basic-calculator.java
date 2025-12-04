/*
 * @lc app=leetcode id=224 lang=java
 *
 * [224] Basic Calculator
 *
 * https://leetcode.com/problems/basic-calculator/description/
 *
 * algorithms
 * Hard (46.25%)
 * Likes:    6823
 * Dislikes: 549
 * Total Accepted:    652.4K
 * Total Submissions: 1.4M
 * Testcase Example:  '"1 + 1"'
 *
 * Given a string s representing a valid expression, implement a basic
 * calculator to evaluate it, and return the result of the evaluation.
 * 
 * Note: You are not allowed to use any built-in function which evaluates
 * strings as mathematical expressions, such as eval().
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: s = "1 + 1"
 * Output: 2
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: s = " 2-1 + 2 "
 * Output: 3
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: s = "(1+(4+5+2)-3)+(6+8)"
 * Output: 23
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= s.length <= 3 * 10^5
 * s consists of digits, '+', '-', '(', ')', and ' '.
 * s represents a valid expression.
 * '+' is not used as a unary operation (i.e., "+1" and "+(2 + 3)" is
 * invalid).
 * '-' could be used as a unary operation (i.e., "-1" and "-(2 + 3)" is
 * valid).
 * There will be no two consecutive operators in the input.
 * Every number and running calculation will fit in a signed 32-bit integer.
 * 
 * 
 */

// @lc code=start
import java.util.Stack;
class Solution {
    public int calculate(String s) {
        int len = s.length();
        int sign = 1;
        int result = 0;
        Stack<Integer> stack = new Stack<>();
        for(int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if(Character.isDigit(c)) {
                int sum = c - '0';
                while(i + 1 < len && Character.isDigit(s.charAt(i + 1))) {
                    sum = sum * 10 + s.charAt(i + 1) - '0';
                    i++;
                }
                result += sum * sign;
            } else if(c == '+') {
                sign = 1;
            } else if(c == '-') {
                sign = -1;
            } else if(c == '(') {
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            } else if(c == ')') {
                result = result * stack.pop() + stack.pop();
            }
        }
        return result;
        
    }
}
// @lc code=end

