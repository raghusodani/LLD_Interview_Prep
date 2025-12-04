/*
 * @lc app=leetcode id=909 lang=java
 *
 * [909] Snakes and Ladders
 *
 * https://leetcode.com/problems/snakes-and-ladders/description/
 *
 * algorithms
 * Medium (47.96%)
 * Likes:    3593
 * Dislikes: 1310
 * Total Accepted:    344.4K
 * Total Submissions: 718K
 * Testcase Example:  '[[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,35,-1,-1,13,-1],[-1,-1,-1,-1,-1,-1],[-1,15,-1,-1,-1,-1]]'
 *
 * You are given an n x n integer matrix board where the cells are labeled from
 * 1 to n^2 in a Boustrophedon style starting from the bottom left of the board
 * (i.e. board[n - 1][0]) and alternating direction each row.
 * 
 * You start on square 1 of the board. In each move, starting from square curr,
 * do the following:
 * 
 * 
 * Choose a destination square next with a label in the range [curr + 1,
 * min(curr + 6, n^2)].
 * 
 * 
 * This choice simulates the result of a standard 6-sided die roll: i.e., there
 * are always at most 6 destinations, regardless of the size of the
 * board.
 * 
 * 
 * If next has a snake or ladder, you must move to the destination of that
 * snake or ladder. Otherwise, you move to next.
 * The game ends when you reach the square n^2.
 * 
 * 
 * A board square on row r and column c has a snake or ladder if board[r][c] !=
 * -1. The destination of that snake or ladder is board[r][c]. Squares 1 and
 * n^2 are not the starting points of any snake or ladder.
 * 
 * Note that you only take a snake or ladder at most once per dice roll. If the
 * destination to a snake or ladder is the start of another snake or ladder,
 * you do not follow the subsequent snake or ladder.
 * 
 * 
 * For example, suppose the board is [[-1,4],[-1,3]], and on the first move,
 * your destination square is 2. You follow the ladder to square 3, but do not
 * follow the subsequent ladder to 4.
 * 
 * 
 * Return the least number of dice rolls required to reach the square n^2. If
 * it is not possible to reach the square, return -1.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: board =
 * [[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,35,-1,-1,13,-1],[-1,-1,-1,-1,-1,-1],[-1,15,-1,-1,-1,-1]]
 * Output: 4
 * Explanation: 
 * In the beginning, you start at square 1 (at row 5, column 0).
 * You decide to move to square 2 and must take the ladder to square 15.
 * You then decide to move to square 17 and must take the snake to square 13.
 * You then decide to move to square 14 and must take the ladder to square 35.
 * You then decide to move to square 36, ending the game.
 * This is the lowest possible number of moves to reach the last square, so
 * return 4.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: board = [[-1,-1],[-1,3]]
 * Output: 1
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * n == board.length == board[i].length
 * 2 <= n <= 20
 * board[i][j] is either -1 or in the range [1, n^2].
 * The squares labeled 1 and n^2 are not the starting points of any snake or
 * ladder.
 * 
 * 
 */

// @lc code=start
import java.util.Queue;
import java.util.LinkedList;

class Solution {
    /**
     * Snakes and Ladders using BFS (Shortest Path)
     *
     * Algorithm:
     * 1. Flatten 2D board into 1D array (handle Boustrophedon pattern)
     * 2. Use BFS to find shortest path from square 1 to n²
     * 3. For each square, try all dice rolls (1-6)
     * 4. Handle snakes/ladders by jumping to destination
     *
     * Time: O(n²) - Visit each square at most once
     * Space: O(n²) - Queue and visited array
     */
    public int snakesAndLadders(int[][] board) {
        int n = board.length;

        // Step 1: Flatten board into 1D array
        int[] flatBoard = new int[n * n + 1];  // 1-indexed
        int i = n - 1, j = 0, index = 0, inc = 1;

        while (index < n * n) {
            flatBoard[index + 1] = board[i][j];  // 1-indexed
            index++;

            // Handle Boustrophedon (zigzag) pattern
            if (inc == 1 && j == n - 1) {
                inc = -1;
                i--;
            } else if (inc == -1 && j == 0) {
                inc = 1;
                i--;
            } else {
                j += inc;
            }
        }

        // Step 2: BFS to find shortest path
        boolean[] visited = new boolean[n * n];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{1, 0});  // {square, moves}
        visited[0] = true;  // Mark square 1 as visited (0-indexed in visited array)
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int square = current[0];
            int moves = current[1];

            // Try all dice rolls (1 to 6)
            for (int dice = 1; dice <= 6; dice++) {
                int next = square + dice;

                // Check bounds
                if (next > n * n) break;

                // Handle snake or ladder
                if (flatBoard[next] != -1) {
                    next = flatBoard[next];
                }

                // Reached the end!
                if (next == n * n) {
                    return moves + 1;
                }

                // Visit if not visited
                if (!visited[next - 1]) {
                    visited[next - 1] = true;
                    queue.offer(new int[]{next, moves + 1});
                }
            }
        }

        return -1;  // Cannot reach end
    }
}
// @lc code=end

