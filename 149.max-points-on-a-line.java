/*
 * @lc app=leetcode id=149 lang=java
 *
 * [149] Max Points on a Line
 *
 * https://leetcode.com/problems/max-points-on-a-line/description/
 *
 * algorithms
 * Hard (29.68%)
 * Likes:    4449
 * Dislikes: 573
 * Total Accepted:    496.1K
 * Total Submissions: 1.7M
 * Testcase Example:  '[[1,1],[2,2],[3,3]]'
 *
 * Given an array of points where points[i] = [xi, yi] represents a point on
 * the X-Y plane, return the maximum number of points that lie on the same
 * straight line.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: points = [[1,1],[2,2],[3,3]]
 * Output: 3
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]
 * Output: 4
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= points.length <= 300
 * points[i].length == 2
 * -10^4 <= xi, yi <= 10^4
 * All the points are unique.
 * 
 * 
 */

// @lc code=start
class Solution {
    public int maxPoints(int[][] points) {
        if (points.length <= 2) {
            return points.length;
        }

        int max = 0;
        for (int i = 0; i < points.length; i++) {
            int[] p1 = points[i];
            for (int j = i + 1; j < points.length; j++) {
                int[] p2 = points[j];
                int count = 2;
                for (int k = j + 1; k < points.length; k++) {
                    int[] p3 = points[k];
                    int x = (p2[1] - p1[1]) * (p3[0] - p1[0]);
                    int y = (p2[0] - p1[0]) * (p3[1] - p1[1]);
                    if (x == y) {
                        count++;
                    }
                }
                max = Math.max(max, count);
            }
        }
        return max;
        
    }
}
// @lc code=end

