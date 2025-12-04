/*
 * @lc app=leetcode id=743 lang=java
 *
 * [743] Network Delay Time
 *
 * https://leetcode.com/problems/network-delay-time/description/
 *
 * algorithms
 * Medium (58.88%)
 * Likes:    8212
 * Dislikes: 389
 * Total Accepted:    782.3K
 * Total Submissions: 1.3M
 * Testcase Example:  '[[2,1,1],[2,3,1],[3,4,1]]\n4\n2'
 *
 * You are given a network of n nodes, labeled from 1 to n. You are also given
 * times, a list of travel times as directed edges times[i] = (ui, vi, wi),
 * where ui is the source node, vi is the target node, and wi is the time it
 * takes for a signal to travel from source to target.
 * 
 * We will send a signal from a given node k. Return the minimum time it takes
 * for all the n nodes to receive the signal. If it is impossible for all the n
 * nodes to receive the signal, return -1.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
 * Output: 2
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: times = [[1,2,1]], n = 2, k = 1
 * Output: 1
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: times = [[1,2,1]], n = 2, k = 2
 * Output: -1
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= k <= n <= 100
 * 1 <= times.length <= 6000
 * times[i].length == 3
 * 1 <= ui, vi <= n
 * ui != vi
 * 0 <= wi <= 100
 * All the pairs (ui, vi) are unique. (i.e., no multiple edges.)
 * 
 * 
 */

// @lc code=start
import java.util.Arrays;
class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        // create a graph using the times grid
        int[][] graph = new int[n][n];
        for (int[] row : graph) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        for (int[] time : times) {
            graph[time[0] - 1][time[1] - 1] = time[2];
        }

        // create a distance array
        int[] distance = new int[n];

        // create a visited array
        boolean[] visited = new boolean[n];

        // initialize the distance array
        Arrays.fill(distance, Integer.MAX_VALUE);

        // initialize the distance of the source node to 0
        distance[k - 1] = 0;

        // run the dijkstra algorithm
        for (int i = 0; i < n; i++) {
            // find the node with the minimum distance
            int minDistance = Integer.MAX_VALUE;
            int minNode = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && distance[j] < minDistance) {
                    minDistance = distance[j];
                    minNode = j;
                }
            }

            // if no node is found, break
            if (minNode == -1) {
                break;
            }

            // mark the node as visited
            visited[minNode] = true;

            // update the distance of the adjacent nodes
            for (int j = 0; j < n; j++) {
                if (!visited[j] && graph[minNode][j] != Integer.MAX_VALUE && distance[minNode] != Integer.MAX_VALUE && distance[minNode] + graph[minNode][j] < distance[j]) {
                    distance[j] = distance[minNode] + graph[minNode][j];
                }
            }
        }

        // find the maximum distance
        int maxDistance = 0;
        for (int i = 0; i < n; i++) {
            if (distance[i] == Integer.MAX_VALUE) {
                return -1;
            }
            maxDistance = Math.max(maxDistance, distance[i]);
        }
        return maxDistance;
    }
}
// @lc code=end

