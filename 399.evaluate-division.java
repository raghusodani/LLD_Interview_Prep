/*
 * @lc app=leetcode id=399 lang=java
 *
 * [399] Evaluate Division
 *
 * https://leetcode.com/problems/evaluate-division/description/
 *
 * algorithms
 * Medium (63.65%)
 * Likes:    10039
 * Dislikes: 1071
 * Total Accepted:    659.2K
 * Total Submissions: 1M
 * Testcase Example:  '[["a","b"],["b","c"]]\n' +
  '[2.0,3.0]\n' +
  '[["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]'
 *
 * You are given an array of variable pairs equations and an array of real
 * numbers values, where equations[i] = [Ai, Bi] and values[i] represent the
 * equation Ai / Bi = values[i]. Each Ai or Bi is a string that represents a
 * single variable.
 * 
 * You are also given some queries, where queries[j] = [Cj, Dj] represents the
 * j^th query where you must find the answer for Cj / Dj = ?.
 * 
 * Return the answers to all queries. If a single answer cannot be determined,
 * return -1.0.
 * 
 * Note: The input is always valid. You may assume that evaluating the queries
 * will not result in division by zero and that there is no contradiction.
 * 
 * Note: The variables that do not occur in the list of equations are
 * undefined, so the answer cannot be determined for them.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: equations = [["a","b"],["b","c"]], values = [2.0,3.0], queries =
 * [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]
 * Output: [6.00000,0.50000,-1.00000,1.00000,-1.00000]
 * Explanation: 
 * Given: a / b = 2.0, b / c = 3.0
 * queries are: a / c = ?, b / a = ?, a / e = ?, a / a = ?, x / x = ? 
 * return: [6.0, 0.5, -1.0, 1.0, -1.0 ]
 * note: x is undefined => -1.0
 * 
 * Example 2:
 * 
 * 
 * Input: equations = [["a","b"],["b","c"],["bc","cd"]], values =
 * [1.5,2.5,5.0], queries = [["a","c"],["c","b"],["bc","cd"],["cd","bc"]]
 * Output: [3.75000,0.40000,5.00000,0.20000]
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: equations = [["a","b"]], values = [0.5], queries =
 * [["a","b"],["b","a"],["a","c"],["x","y"]]
 * Output: [0.50000,2.00000,-1.00000,-1.00000]
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= equations.length <= 20
 * equations[i].length == 2
 * 1 <= Ai.length, Bi.length <= 5
 * values.length == equations.length
 * 0.0 < values[i] <= 20.0
 * 1 <= queries.length <= 20
 * queries[i].length == 2
 * 1 <= Cj.length, Dj.length <= 5
 * Ai, Bi, Cj, Dj consist of lower case English letters and digits.
 * 
 * 
 */

// @lc code=start
import java.util.*;

class Solution {
    /**
     * Evaluate division using Graph + DFS
     *
     * Approach:
     * 1. Build a weighted directed graph
     *    - Nodes: variables
     *    - Edges: divisions with weights
     * 2. For each query, use DFS to find path and multiply weights
     *
     * Time: O(equations.length + queries.length × (V + E))
     * Space: O(equations.length) for graph
     */
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        // Build graph: adjacency list
        Map<String, Map<String, Double>> graph = new HashMap<>();

        // Add edges (both directions)
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double value = values[i];

            // a / b = value  →  a → b with weight 'value'
            graph.putIfAbsent(a, new HashMap<>());
            graph.get(a).put(b, value);

            // b / a = 1/value  →  b → a with weight '1/value'
            graph.putIfAbsent(b, new HashMap<>());
            graph.get(b).put(a, 1.0 / value);
        }

        // Process queries
        double[] results = new double[queries.size()];

        for (int i = 0; i < queries.size(); i++) {
            String start = queries.get(i).get(0);
            String end = queries.get(i).get(1);

            // Check if variables exist
            if (!graph.containsKey(start) || !graph.containsKey(end)) {
                results[i] = -1.0;
            } else if (start.equals(end)) {
                // Same variable: a / a = 1.0
                results[i] = 1.0;
            } else {
                // Use DFS to find path
                Set<String> visited = new HashSet<>();
                results[i] = dfs(graph, start, end, 1.0, visited);
            }
        }

        return results;
    }

    /**
     * DFS to find path from start to end
     *
     * @param graph - adjacency list
     * @param current - current node
     * @param target - target node
     * @param product - accumulated product of edge weights
     * @param visited - set of visited nodes
     * @return result of division, or -1.0 if no path found
     */
    private double dfs(Map<String, Map<String, Double>> graph,
                      String current, String target,
                      double product, Set<String> visited) {
        // Mark as visited
        visited.add(current);

        // Check neighbors
        Map<String, Double> neighbors = graph.get(current);

        // Found target
        if (neighbors.containsKey(target)) {
            return product * neighbors.get(target);
        }

        // Explore neighbors
        for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
            if (!visited.contains(neighbor.getKey())) {
                double result = dfs(graph, neighbor.getKey(), target,
                                   product * neighbor.getValue(), visited);
                if (result != -1.0) {
                    return result;
                }
            }
        }
        
        return -1.0;  // No path found
    }
}
// @lc code=end

