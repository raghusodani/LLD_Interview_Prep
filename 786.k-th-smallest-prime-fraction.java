/*
 * @lc app=leetcode id=786 lang=java
 *
 * [786] K-th Smallest Prime Fraction
 *
 * https://leetcode.com/problems/k-th-smallest-prime-fraction/description/
 *
 * algorithms
 * Medium (68.79%)
 * Likes:    2116
 * Dislikes: 121
 * Total Accepted:    166.9K
 * Total Submissions: 242.6K
 * Testcase Example:  '[1,2,3,5]\n3'
 *
 * You are given a sorted integer array arr containing 1 and prime numbers,
 * where all the integers of arr are unique. You are also given an integer k.
 * 
 * For every i and j where 0 <= i < j < arr.length, we consider the fraction
 * arr[i] / arr[j].
 * 
 * Return the k^th smallest fraction considered. Return your answer as an array
 * of integers of size 2, where answer[0] == arr[i] and answer[1] == arr[j].
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: arr = [1,2,3,5], k = 3
 * Output: [2,5]
 * Explanation: The fractions to be considered in sorted order are:
 * 1/5, 1/3, 2/5, 1/2, 3/5, and 2/3.
 * The third fraction is 2/5.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: arr = [1,7], k = 1
 * Output: [1,7]
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 2 <= arr.length <= 1000
 * 1 <= arr[i] <= 3 * 10^4
 * arr[0] == 1
 * arr[i] is a prime number for i > 0.
 * All the numbers of arr are unique and sorted in strictly increasing
 * order.
 * 1 <= k <= arr.length * (arr.length - 1) / 2
 * 
 * 
 * 
 * Follow up: Can you solve the problem with better than O(n^2) complexity?
 */

// @lc code=start
class Solution {
    public int[] kthSmallestPrimeFraction(int[] arr, int k) {
        int n = arr.length;
        double left = 0, right = 1, mid;
        int[] res = new int[2];
        
        while(left <= right){
            mid = left + (right-left)/2;
            double maxFrac = 0;
            int j = 1, total = 0, p = 0, q = 0;
            for(int i = 0; i < n - 1; i++){
                while(j < n && arr[i] > mid * arr[j]){
                    j++;
                }
                total += (n - j);
                if(j == n){
                    break;
                }
                double frac = (double) arr[i] / arr[j];
                if(frac > maxFrac){
                    p = i;
                    q = j;
                    maxFrac = frac;
                }
            }
            if(total == k){
                res[0] = arr[p];
                res[1] = arr[q];
                return res;
            }
            else if(total > k){
                right = mid;
            }
            else{
                left = mid;
            }
        }
        return res;
    }
}
// @lc code=end

