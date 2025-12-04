/*
 * @lc app=leetcode id=2071 lang=java
 *
 * [2071] Maximum Number of Tasks You Can Assign
 *
 * https://leetcode.com/problems/maximum-number-of-tasks-you-can-assign/description/
 *
 * algorithms
 * Hard (50.30%)
 * Likes:    1073
 * Dislikes: 57
 * Total Accepted:    84.4K
 * Total Submissions: 167.9K
 * Testcase Example:  '[3,2,1]\n[0,3,3]\n1\n1'
 *
 * You have n tasks and m workers. Each task has a strength requirement stored
 * in a 0-indexed integer array tasks, with the i^th task requiring tasks[i]
 * strength to complete. The strength of each worker is stored in a 0-indexed
 * integer array workers, with the j^th worker having workers[j] strength. Each
 * worker can only be assigned to a single task and must have a strength
 * greater than or equal to the task's strength requirement (i.e., workers[j]
 * >= tasks[i]).
 * 
 * Additionally, you have pills magical pills that will increase a worker's
 * strength by strength. You can decide which workers receive the magical
 * pills, however, you may only give each worker at most one magical pill.
 * 
 * Given the 0-indexed integer arrays tasks and workers and the integers pills
 * and strength, return the maximum number of tasks that can be completed.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: tasks = [3,2,1], workers = [0,3,3], pills = 1, strength = 1
 * Output: 3
 * Explanation:
 * We can assign the magical pill and tasks as follows:
 * - Give the magical pill to worker 0.
 * - Assign worker 0 to task 2 (0 + 1 >= 1)
 * - Assign worker 1 to task 1 (3 >= 2)
 * - Assign worker 2 to task 0 (3 >= 3)
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: tasks = [5,4], workers = [0,0,0], pills = 1, strength = 5
 * Output: 1
 * Explanation:
 * We can assign the magical pill and tasks as follows:
 * - Give the magical pill to worker 0.
 * - Assign worker 0 to task 0 (0 + 5 >= 5)
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: tasks = [10,15,30], workers = [0,10,10,10,10], pills = 3, strength =
 * 10
 * Output: 2
 * Explanation:
 * We can assign the magical pills and tasks as follows:
 * - Give the magical pill to worker 0 and worker 1.
 * - Assign worker 0 to task 0 (0 + 10 >= 10)
 * - Assign worker 1 to task 1 (10 + 10 >= 15)
 * The last pill is not given because it will not make any worker strong enough
 * for the last task.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * n == tasks.length
 * m == workers.length
 * 1 <= n, m <= 5 * 10^4
 * 0 <= pills <= m
 * 0 <= tasks[i], workers[j], strength <= 10^9
 * 
 * 
 */

// @lc code=start

import java.util.Arrays;
import java.util.TreeMap;

class Solution {
    public int maxTaskAssign(int[] tasks, int[] workers, int pills, int strength) {
        int left = 0, right = Math.min(tasks.length, workers.length);
        Arrays.sort(tasks);
        Arrays.sort(workers);

        while (left < right) {
            int mid = (left + right + 1) / 2;
            int usedPills = 0;
            TreeMap<Integer, Integer> avail = new TreeMap<>();
            for (int i = workers.length - mid; i < workers.length; ++i)
                avail.put(workers[i], avail.getOrDefault(workers[i], 0) + 1);

            boolean canAssign = true;
            for (int i = mid - 1; i >= 0; --i) {
                int t = tasks[i];
                int w = avail.lastKey();
                if (w >= t) {
                    decrement(avail, w);
                } else {
                    Integer key = avail.ceilingKey(t - strength);
                    if (key == null || ++usedPills > pills) {
                        canAssign = false;
                        break;
                    }
                    decrement(avail, key);
                }
            }

            if (canAssign)
                left = mid;
            else
                right = mid - 1;
        }

        return left;
    }

    private void decrement(TreeMap<Integer, Integer> m, int k) {
        int c = m.get(k);
        if (c == 1) m.remove(k);
        else m.put(k, c - 1);
    }
}
// @lc code=end

