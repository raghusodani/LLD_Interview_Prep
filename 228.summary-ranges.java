/*
 * @lc app=leetcode id=228 lang=java
 *
 * [228] Summary Ranges
 *
 * https://leetcode.com/problems/summary-ranges/description/
 *
 * algorithms
 * Easy (53.52%)
 * Likes:    4436
 * Dislikes: 2359
 * Total Accepted:    872.3K
 * Total Submissions: 1.6M
 * Testcase Example:  '[0,1,2,4,5,7]'
 *
 * You are given a sorted unique integer array nums.
 * 
 * A range [a,b] is the set of all integers from a to b (inclusive).
 * 
 * Return the smallest sorted list of ranges that cover all the numbers in the
 * array exactly. That is, each element of nums is covered by exactly one of
 * the ranges, and there is no integer x such that x is in one of the ranges
 * but not in nums.
 * 
 * Each range [a,b] in the list should be output as:
 * 
 * 
 * "a->b" if a != b
 * "a" if a == b
 * 
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: nums = [0,1,2,4,5,7]
 * Output: ["0->2","4->5","7"]
 * Explanation: The ranges are:
 * [0,2] --> "0->2"
 * [4,5] --> "4->5"
 * [7,7] --> "7"
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: nums = [0,2,3,4,6,8,9]
 * Output: ["0","2->4","6","8->9"]
 * Explanation: The ranges are:
 * [0,0] --> "0"
 * [2,4] --> "2->4"
 * [6,6] --> "6"
 * [8,9] --> "8->9"
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 0 <= nums.length <= 20
 * -2^31 <= nums[i] <= 2^31 - 1
 * All the values of nums are unique.
 * nums is sorted in ascending order.
 * 
 * 
 */

// @lc code=start

import java.util.List;
import java.util.ArrayList;

class Solution {
    public List<String> summaryRanges(int[] nums) {
        List<String> ans = new ArrayList<>();
        int start = 0, end = 0;
        for(int i = 0 ;i < nums.length; i++){
            if(i == nums.length - 1 || nums[i] + 1 != nums[i + 1]){
                end = i;
                if(start == end){
                    ans.add(nums[start] + "");
                }else{
                    ans.add(nums[start] + "->" + nums[end]);
                }
                start = i + 1;
            }
        }
        return ans;
    }
}
// @lc code=end

