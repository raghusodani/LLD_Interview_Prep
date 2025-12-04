/*
 * @lc app=leetcode id=79 lang=java
 *
 * [79] Word Search
 *
 * https://leetcode.com/problems/word-search/description/
 *
 * algorithms
 * Medium (46.20%)
 * Likes:    17272
 * Dislikes: 741
 * Total Accepted:    2.3M
 * Total Submissions: 5M
 * Testcase Example:  '[["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]\n"ABCCED"'
 *
 * Given an m x n grid of characters board and a string word, return true if
 * word exists in the grid.
 * 
 * The word can be constructed from letters of sequentially adjacent cells,
 * where adjacent cells are horizontally or vertically neighboring. The same
 * letter cell may not be used more than once.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
 * = "ABCCED"
 * Output: true
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
 * = "SEE"
 * Output: true
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word
 * = "ABCB"
 * Output: false
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == board.length
 * n = board[i].length
 * 1 <= m, n <= 6
 * 1 <= word.length <= 15
 * board and word consists of only lowercase and uppercase English letters.
 * 
 * 
 * 
 * Follow up: Could you use search pruning to make your solution faster with a
 * larger board?
 * 
 */

// @lc code=start
class Solution {

    private boolean backtrack(int row, int col, char[][] board, String word, int index, boolean[][] visited) {
        if (index == word.length())
            return true;
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || visited[row][col]
                || board[row][col] != word.charAt(index))
            return false;

        visited[row][col] = true;
        boolean result = backtrack(row + 1, col, board, word, index + 1, visited) ||
                backtrack(row - 1, col, board, word, index + 1, visited) ||
                backtrack(row, col + 1, board, word, index + 1, visited) ||
                backtrack(row, col - 1, board, word, index + 1, visited);
        visited[row][col] = false;

        return result;
    }

    public boolean exist(char[][] board, String word) {
        int m = board.length;
        int n = board[0].length;
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == word.charAt(0) && backtrack(i, j, board, word, 0, visited)) {
                    return true;
                }
            }
        }
        return false;

    }
}
// @lc code=end
