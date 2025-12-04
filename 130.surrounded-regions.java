/*
 * @lc app=leetcode id=130 lang=java
 *
 * [130] Surrounded Regions
 *
 * https://leetcode.com/problems/surrounded-regions/description/
 *
 * algorithms
 * Medium (44.03%)
 * Likes:    9646
 * Dislikes: 2188
 * Total Accepted:    1.1M
 * Total Submissions: 2.5M
 * Testcase Example:  '[["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]'
 *
 * You are given an m x n matrix board containing letters 'X' and 'O', capture
 * regions that are surrounded:
 * 
 * 
 * Connect: A cell is connected to adjacent cells horizontally or
 * vertically.
 * Region: To form a region connect every 'O' cell.
 * Surround: The region is surrounded with 'X' cells if you can connect the
 * region with 'X' cells and none of the region cells are on the edge of the
 * board.
 * 
 * 
 * To capture a surrounded region, replace all 'O's with 'X's in-place within
 * the original board. You do not need to return anything.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: board =
 * [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]
 * 
 * Output:
 * [["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]
 * 
 * Explanation:
 * 
 * In the above diagram, the bottom region is not captured because it is on the
 * edge of the board and cannot be surrounded.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: board = [["X"]]
 * 
 * Output: [["X"]]
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == board.length
 * n == board[i].length
 * 1 <= m, n <= 200
 * board[i][j] is 'X' or 'O'.
 * 
 * 
 */

// @lc code=start
class Solution {
    public void dfs(char[][] board, int i, int j) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] != 'O')
            return;

        board[i][j] = 'B';
        dfs(board, i + 1, j);
        dfs(board, i - 1, j);
        dfs(board, i, j + 1);
        dfs(board, i, j - 1);
    }

    public void solve(char[][] board) {
        if (board == null || board[0].length == 0)
            return;

        // Mark borders as waters
        int rows = board.length;
        int cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            dfs(board, i, 0); // Left border
            dfs(board, i, cols - 1); // Right border
        }

        for (int j = 0; j < cols; j++) {
            dfs(board, 0, j); // Top border
            dfs(board, rows - 1, j); // Bottom border
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                } else if (board[i][j] == 'B')
                    board[i][j] = 'O';
            }
        }
    }
}
// @lc code=end
