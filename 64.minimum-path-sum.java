/*
 * @lc app=leetcode id=64 lang=java
 *
 * [64] Minimum Path Sum
 *
 * https://leetcode.com/problems/minimum-path-sum/description/
 *
 * algorithms
 * Medium (67.31%)
 * Likes:    13405
 * Dislikes: 190
 * Total Accepted:    1.7M
 * Total Submissions: 2.5M
 * Testcase Example:  '[[1,3,1],[1,5,1],[4,2,1]]'
 *
 * Given a m x n grid filled with non-negative numbers, find a path from top
 * left to bottom right, which minimizes the sum of all numbers along its
 * path.
 * 
 * Note: You can only move either down or right at any point in time.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
 * Output: 7
 * Explanation: Because the path 1 → 3 → 1 → 1 → 1 minimizes the sum.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: grid = [[1,2,3],[4,5,6]]
 * Output: 12
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == grid.length
 * n == grid[i].length
 * 1 <= m, n <= 200
 * 0 <= grid[i][j] <= 200
 * 
 * 
 */

 /*
  * Inplement a recursive solution first, then implement a DP solution.
  */

// @lc code=start
class Solution {

    public int minPathSum(int[][] grid, int i, int j, int[][] dp) {
        if (i == grid.length - 1 && j == grid[0].length - 1) {
            return grid[i][j];
        }
        if (dp[i][j] != -1) {
            return dp[i][j];
        }
        int right = Integer.MAX_VALUE;
        int down = Integer.MAX_VALUE;
        if (j + 1 < grid[0].length) {
            right = minPathSum(grid, i, j + 1, dp);
        }
        if (i + 1 < grid.length) {
            down = minPathSum(grid, i + 1, j, dp);
        }
        dp[i][j] = grid[i][j] + Math.min(right, down);
        return dp[i][j];
    }

    public int minPathSum(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = -1;
            }
        }
        return minPathSum(grid, 0, 0, dp);
        
    }
}
// @lc code=end

