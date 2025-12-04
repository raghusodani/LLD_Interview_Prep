/*
 * @lc app=leetcode id=3318 lang=java
 *
 * [3318] Find X-Sum of All K-Long Subarrays I
 */

// @lc code=start
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class Solution {
    public int[] findXSum(int[] nums, int k, int x) {
        int n = nums.length;
        int[] res = new int[n - k + 1];
        for(int i = 0; i <= n - k; i++){
            Map<Integer, Integer> cnt = new HashMap<>();
            for(int j = i; j < i + k; j++){
                cnt.put(nums[j], cnt.getOrDefault(nums[j], 0) + 1);
            }
            List<int[]>frequency =  new ArrayList<>();
            for(Map.Entry<Integer, Integer> entry : cnt.entrySet()){
                frequency.add(new int[]{entry.getValue(), entry.getKey()});
            }
            frequency.sort((a, b) -> b[0] != a[0] ? b[0] - a[0] : b[1] - a[1]);
            int xsumCurrent = 0;
            for (int j = 0; j < x && j < frequency.size(); ++j) {
                xsumCurrent += frequency.get(j)[0] * frequency.get(j)[1];
            }
            res[i] = xsumCurrent;

        }
        return res;
    }
}
// @lc code=end

