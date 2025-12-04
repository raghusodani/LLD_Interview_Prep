/*
 * @lc app=leetcode id=547 lang=java
 *
 * [547] Number of Provinces
 *
 * https://leetcode.com/problems/number-of-provinces/description/
 *
 * algorithms
 * Medium (69.52%)
 * Likes:    10877
 * Dislikes: 410
 * Total Accepted:    1.4M
 * Total Submissions: 2M
 * Testcase Example:  '[[1,1,0],[1,1,0],[0,0,1]]'
 *
 * There are n cities. Some of them are connected, while some are not. If city
 * a is connected directly with city b, and city b is connected directly with
 * city c, then city a is connected indirectly with city c.
 * 
 * A province is a group of directly or indirectly connected cities and no
 * other cities outside of the group.
 * 
 * You are given an n x n matrix isConnected where isConnected[i][j] = 1 if the
 * i^th city and the j^th city are directly connected, and isConnected[i][j] =
 * 0 otherwise.
 * 
 * Return the total number of provinces.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
 * Output: 2
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: isConnected = [[1,0,0],[0,1,0],[0,0,1]]
 * Output: 3
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= n <= 200
 * n == isConnected.length
 * n == isConnected[i].length
 * isConnected[i][j] is 1 or 0.
 * isConnected[i][i] == 1
 * isConnected[i][j] == isConnected[j][i]
 * 
 * 
 */

// @lc code=start
class Solution {
    public int findCircleNum(int[][] isConnected) {
        // create a boolean array to mark visited cities
        boolean[] visited = new boolean[isConnected.length];

        // initialize count of provinces
        int count = 0;

        // iterate through all cities
        for (int i = 0; i < isConnected.length; i++) {
            // if city is not visited, increment count and mark as visited
            if (!visited[i]) {
                count++;
                dfs(isConnected, visited, i);
            }
        }
        return count;
    }

    // depth-first search to mark all connected cities as visited
    private void dfs(int[][] isConnected, boolean[] visited, int i) {
        // mark city as visited
        visited[i] = true;

        // iterate through all other cities
        for (int j = 0; j < isConnected.length; j++) {
            // if city is connected and not visited, recursively call dfs
            if (isConnected[i][j] == 1 && !visited[j]) {
                dfs(isConnected, visited, j);
            }
        }
    }
}
// @lc code=end

