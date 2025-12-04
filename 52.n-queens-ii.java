/*
 * @lc app=leetcode id=52 lang=java
 *
 * [52] N-Queens II
 *
 * https://leetcode.com/problems/n-queens-ii/description/
 *
 * algorithms
 * Hard (77.61%)
 * Likes:    4194
 * Dislikes: 281
 * Total Accepted:    546.1K
 * Total Submissions: 703.2K
 * Testcase Example:  '4'
 *
 * The n-queens puzzle is the problem of placing n queens on an n x n
 * chessboard such that no two queens attack each other.
 * 
 * Given an integer n, return the number of distinct solutions to the n-queens
 * puzzle.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: n = 4
 * Output: 2
 * Explanation: There are two distinct solutions to the 4-queens puzzle as
 * shown.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: n = 1
 * Output: 1
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= n <= 9
 * 
 * 
 */

// @lc code=start
class Solution {
    private int count = 0;

    /**
     * Finds the number of distinct solutions to the n-queens puzzle.
     *
     * Approach: Backtracking with boolean arrays to track attacked positions.
     * - cols[i]: column i is occupied
     * - diag1[i]: \ diagonal is occupied (row - col + n - 1)
     * - diag2[i]: / diagonal is occupied (row + col)
     *
     * Time: O(n!) - Try placing queens with pruning
     * Space: O(n) - Three boolean arrays + recursion stack
     */
    public int totalNQueens(int n) {
        boolean[] cols = new boolean[n];           // Track columns
        boolean[] diag1 = new boolean[2 * n - 1]; // Track \ diagonals
        boolean[] diag2 = new boolean[2 * n - 1]; // Track / diagonals

        backtrack(0, n, cols, diag1, diag2);
        return count;
    }

    /**
     * Backtracking to place queens row by row.
     *
     * @param row - current row to place queen
     * @param n - board size
     * @param cols - boolean array tracking occupied columns
     * @param diag1 - boolean array tracking occupied \ diagonals
     * @param diag2 - boolean array tracking occupied / diagonals
     */
    private void backtrack(int row, int n, boolean[] cols,
                          boolean[] diag1, boolean[] diag2) {
        // Base case: successfully placed all n queens
        if (row == n) {
            count++;
            return;
        }

        // Try placing queen in each column of current row
        for (int col = 0; col < n; col++) {
            // Calculate diagonal indices
            // \ diagonal: row - col is constant (shift by n-1 to avoid negative)
            int d1 = row - col + n - 1;
            // / diagonal: row + col is constant
            int d2 = row + col;

            // Check if position is under attack
            if (cols[col] || diag1[d1] || diag2[d2]) {
                continue; // Skip this position
            }

            // Place queen (mark as occupied)
            cols[col] = true;
            diag1[d1] = true;
            diag2[d2] = true;

            // Recurse to place queen in next row
            backtrack(row + 1, n, cols, diag1, diag2);

            // Backtrack (remove queen)
            cols[col] = false;
            diag1[d1] = false;
            diag2[d2] = false;
        }
    }
}
// @lc code=end

/*
 * ALTERNATIVE SOLUTION: Bitmasking (Fastest)
 *
 * Use bits to represent available positions - much faster constants!
 *
 * Time: O(n!) but with better constants
 * Space: O(n) - Only recursion stack
 *
 * Key Ideas:
 * - Use bitwise operations to track occupied positions
 * - available = ((1 << n) - 1) & ~(cols | diag1 | diag2)
 * - Get rightmost bit: position = available & -available
 * - Remove rightmost bit: available -= position
 * - Shift diagonals: diag1 << 1, diag2 >> 1
 *
 * This approach is ~2-3x faster than boolean arrays for large n.
 * See comments in code for full implementation.
 */

