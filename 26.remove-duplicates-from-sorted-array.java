/*
 * @lc app=leetcode id=26 lang=java
 *
 * [26] Remove Duplicates from Sorted Array
 */

// @lc code=start
class Solution {
    public int removeDuplicates(int[] nums) {
        int beforeDuplicate = 0, afterDuplicate = 0;
        while (afterDuplicate < nums.length) {
            if (nums[beforeDuplicate] == nums[afterDuplicate]) {
                afterDuplicate++;
            } else {
                nums[beforeDuplicate + 1] = nums[afterDuplicate];
                beforeDuplicate++;
            }
        }
        return beforeDuplicate + 1;
    }
}
// @lc code=end

