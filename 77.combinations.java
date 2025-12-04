/*
 * @lc app=leetcode id=77 lang=java
 *
 * [77] Combinations
 *
 * https://leetcode.com/problems/combinations/description/
 *
 * algorithms
 * Medium (73.69%)
 * Likes:    8774
 * Dislikes: 246
 * Total Accepted:    1.2M
 * Total Submissions: 1.7M
 * Testcase Example:  '4\n2'
 *
 * Given two integers n and k, return all possible combinations of k numbers
 * chosen from the range [1, n].
 * 
 * You may return the answer in any order.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: n = 4, k = 2
 * Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
 * Explanation: There are 4 choose 2 = 6 total combinations.
 * Note that combinations are unordered, i.e., [1,2] and [2,1] are considered
 * to be the same combination.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: n = 1, k = 1
 * Output: [[1]]
 * Explanation: There is 1 choose 1 = 1 total combination.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= n <= 20
 * 1 <= k <= n
 * 
 * 
 */

// @lc code=start
import java.util.ArrayList;
import java.util.List;

class Solution {
    private void backtrack(List<List<Integer>> result, List<Integer> current,
                          int n, int k, int start) {
        // Base case: found a valid combination of size k
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Pruning: calculate how many more numbers we need
        int need = k - current.size();

        // Only iterate while there are enough remaining numbers
        for (int i = start; i <= n - need + 1; i++) {
            current.add(i);                           // Choose
            backtrack(result, current, n, k, i + 1); // Explore
            current.remove(current.size() - 1);       // Un-choose
        }
    }

    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), n, k, 1);
        return result;
    }
}
// @lc code=end
