/*
 * @lc app=leetcode id=757 lang=java
 *
 * [757] Set Intersection Size At Least Two
 *
 * https://leetcode.com/problems/set-intersection-size-at-least-two/description/
 *
 * algorithms
 * Hard (45.77%)
 * Likes:    801
 * Dislikes: 93
 * Total Accepted:    35.6K
 * Total Submissions: 72.7K
 * Testcase Example:  '[[1,3],[3,7],[8,9]]'
 *
 * You are given a 2D integer array intervals where intervals[i] = [starti,
 * endi] represents all the integers from starti to endi inclusively.
 * 
 * A containing set is an array nums where each interval from intervals has at
 * least two integers in nums.
 * 
 * 
 * For example, if intervals = [[1,3], [3,7], [8,9]], then [1,2,4,7,8,9] and
 * [2,3,4,8,9] are containing sets.
 * 
 * 
 * Return the minimum possible size of a containing set.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: intervals = [[1,3],[3,7],[8,9]]
 * Output: 5
 * Explanation: let nums = [2, 3, 4, 8, 9].
 * It can be shown that there cannot be any containing array of size 4.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: intervals = [[1,3],[1,4],[2,5],[3,5]]
 * Output: 3
 * Explanation: let nums = [2, 3, 4].
 * It can be shown that there cannot be any containing array of size 2.
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: intervals = [[1,2],[2,3],[2,4],[4,5]]
 * Output: 5
 * Explanation: let nums = [1, 2, 3, 4, 5].
 * It can be shown that there cannot be any containing array of size 4.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= intervals.length <= 3000
 * intervals[i].length == 2
 * 0 <= starti < endi <= 10^8
 * 
 * 
 */

// @lc code=start

import java.util.Arrays;

class Solution {
    /**
     * Set Intersection Size At Least Two - Greedy Algorithm
     *
     * Key Insight: Sort intervals by end point, then by start point (descending)
     * For each interval, ensure it has at least 2 elements from our set
     * Always prefer the largest 2 elements in the interval (rightmost)
     *
     * Algorithm:
     * 1. Sort intervals by end (ascending), then by start (descending)
     * 2. Track the last 2 elements added to the set
     * 3. For each interval:
     *    - If both last elements are in this interval → skip
     *    - If one last element is in this interval → add one more
     *    - If no last elements are in this interval → add two
     *
     * Time: O(n log n) - sorting
     * Space: O(log n) - sorting
     */
    public int intersectionSizeTwo(int[][] intervals) {
        // Sort by end point (ascending), then by start point (descending)
        // Why descending start? Prefer longer intervals first when same end
        Arrays.sort(intervals, (a, b) -> {
            if (a[1] != b[1]) {
                return a[1] - b[1];  // Sort by end point
            }
            return b[0] - a[0];      // If same end, sort by start (descending)
        });

        int size = 0;
        int largest = -1;      // Largest element added to set
        int secondLargest = -1; // Second largest element added to set
        
        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            // Case 1: Both largest elements are already in this interval
            if (start <= secondLargest) {
                // This interval already has 2 elements, skip
                continue;
            }

            // Case 2: Only the largest element is in this interval
            if (start <= largest) {
                // Need to add one more element
                secondLargest = largest;
                largest = end;
                size++;
            }
            // Case 3: Neither of the last elements are in this interval
            else {
                // Need to add two elements (choose the rightmost two)
                secondLargest = end - 1;
                largest = end;
                size += 2;
            }
        }
        
        return size;
    }
}

/*
 * EXPLANATION OF GREEDY CHOICE:
 *
 * Why choose rightmost elements (end and end-1)?
 * - Intervals are sorted by end point
 * - Choosing rightmost elements maximizes chance they'll be in future intervals
 * - Future intervals have same or larger end points
 * - Elements close to end point are more likely to be shared
 */
// @lc code=end

