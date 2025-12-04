/*
 * @lc app=leetcode id=207 lang=java
 *
 * [207] Course Schedule
 *
 * https://leetcode.com/problems/course-schedule/description/
 *
 * algorithms
 * Medium (50.19%)
 * Likes:    17613
 * Dislikes: 847
 * Total Accepted:    2.3M
 * Total Submissions: 4.7M
 * Testcase Example:  '2\n[[1,0]]'
 *
 * There are a total of numCourses courses you have to take, labeled from 0 to
 * numCourses - 1. You are given an array prerequisites where prerequisites[i]
 * = [ai, bi] indicates that you must take course bi first if you want to take
 * course ai.
 * 
 * 
 * For example, the pair [0, 1], indicates that to take course 0 you have to
 * first take course 1.
 * 
 * 
 * Return true if you can finish all courses. Otherwise, return false.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: numCourses = 2, prerequisites = [[1,0]]
 * Output: true
 * Explanation: There are a total of 2 courses to take. 
 * To take course 1 you should have finished course 0. So it is possible.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
 * Output: false
 * Explanation: There are a total of 2 courses to take. 
 * To take course 1 you should have finished course 0, and to take course 0 you
 * should also have finished course 1. So it is impossible.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= numCourses <= 2000
 * 0 <= prerequisites.length <= 5000
 * prerequisites[i].length == 2
 * 0 <= ai, bi < numCourses
 * All the pairs prerequisites[i] are unique.
 * 
 * 
 */

// @lc code=start

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

class Solution {
    /**
     * Course Schedule using Topological Sort (Kahn's Algorithm - BFS)
     *
     * Key Idea: If we can topologically sort all courses, there's no cycle
     *
     * Algorithm:
     * 1. Build graph and calculate in-degrees (number of prerequisites)
     * 2. Start with courses that have 0 prerequisites (in-degree = 0)
     * 3. Process courses one by one, reducing in-degree of dependent courses
     * 4. If we process all courses → possible, else → cycle exists
     *
     * Time: O(V + E) where V = courses, E = prerequisites
     * Space: O(V + E) for graph and in-degree array
     */
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Step 1: Build adjacency list and in-degree array
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        // Build graph: [a, b] means b → a (must take b before a)
        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course);
            inDegree[course]++;  // course has one more prerequisite
        }

        // Step 2: Add all courses with no prerequisites to queue
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        // Step 3: Process courses using BFS (Topological Sort)
        int processedCourses = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            processedCourses++;

            // Reduce in-degree for all dependent courses
            for (int nextCourse : graph.get(course)) {
                inDegree[nextCourse]--;
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }

        // If we processed all courses, no cycle exists
        return processedCourses == numCourses;
    }
}
// @lc code=end

