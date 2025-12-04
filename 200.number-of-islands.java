/*
 * @lc app=leetcode id=200 lang=java
 *
 * [200] Number of Islands
 *
 * https://leetcode.com/problems/number-of-islands/description/
 *
 * algorithms
 * Medium (63.20%)
 * Likes:    24457
 * Dislikes: 598
 * Total Accepted:    3.8M
 * Total Submissions: 6M
 * Testcase Example:  '[["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]]'
 *
 * Given an m x n 2D binary grid grid which represents a map of '1's (land) and
 * '0's (water), return the number of islands.
 * 
 * An island is surrounded by water and is formed by connecting adjacent lands
 * horizontally or vertically. You may assume all four edges of the grid are
 * all surrounded by water.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: grid = [
 * ⁠ ["1","1","1","1","0"],
 * ⁠ ["1","1","0","1","0"],
 * ⁠ ["1","1","0","0","0"],
 * ⁠ ["0","0","0","0","0"]
 * ]
 * Output: 1
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: grid = [
 * ⁠ ["1","1","0","0","0"],
 * ⁠ ["1","1","0","0","0"],
 * ⁠ ["0","0","1","0","0"],
 * ⁠ ["0","0","0","1","1"]
 * ]
 * Output: 3
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == grid.length
 * n == grid[i].length
 * 1 <= m, n <= 300
 * grid[i][j] is '0' or '1'.
 * 
 * 
 */

// @lc code=start
class Solution {
    /**
     * Count number of islands using DFS (Depth-First Search)
     *
     * Algorithm:
     * 1. Scan grid for '1' (unvisited land)
     * 2. When found, increment island count
     * 3. Use DFS to mark entire island as visited (change '1' to '0')
     * 4. Continue scanning
     *
     * Time: O(m × n) - Visit each cell once
     * Space: O(m × n) - Worst case recursion depth (entire grid is one island)
     */
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int islands = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        // Scan every cell in the grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Found unvisited land
                if (grid[i][j] == '1') {
                    islands++;           // Found a new island!
                    dfs(grid, i, j);     // Mark entire island as visited
                }
            }
        }

        return islands;
    }

    /**
     * DFS to mark all connected land cells as visited
     * "Flood fill" the island by changing '1' to '0'
     *
     * @param grid - the grid
     * @param i - current row
     * @param j - current column
     */
    private void dfs(char[][] grid, int i, int j) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Base cases: out of bounds or water/visited
        if (i < 0 || i >= rows || j < 0 || j >= cols || grid[i][j] == '0') {
            return;
        }

        // Mark current cell as visited (change to water)
        grid[i][j] = '0';

        // Visit all 4 adjacent cells (up, down, left, right)
        dfs(grid, i - 1, j);  // Up
        dfs(grid, i + 1, j);  // Down
        dfs(grid, i, j - 1);  // Left
        dfs(grid, i, j + 1);  // Right
    }
}
// @lc code=end

