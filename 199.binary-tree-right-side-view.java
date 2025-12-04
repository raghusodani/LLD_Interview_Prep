/*
 * @lc app=leetcode id=199 lang=java
 *
 * [199] Binary Tree Right Side View
 *
 * https://leetcode.com/problems/binary-tree-right-side-view/description/
 *
 * algorithms
 * Medium (68.58%)
 * Likes:    13127
 * Dislikes: 1075
 * Total Accepted:    2M
 * Total Submissions: 3M
 * Testcase Example:  '[1,2,3,null,5,null,4]'
 *
 * Given the root of a binary tree, imagine yourself standing on the right side
 * of it, return the values of the nodes you can see ordered from top to
 * bottom.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: root = [1,2,3,null,5,null,4]
 * 
 * Output: [1,3,4]
 * 
 * Explanation:
 * 
 * 
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: root = [1,2,3,4,null,null,null,5]
 * 
 * Output: [1,3,4,5]
 * 
 * Explanation:
 * 
 * 
 * 
 * 
 * Example 3:
 * 
 * 
 * Input: root = [1,null,3]
 * 
 * Output: [1,3]
 * 
 * 
 * Example 4:
 * 
 * 
 * Input: root = []
 * 
 * Output: []
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * The number of nodes in the tree is in the range [0, 100].
 * -100 <= Node.val <= 100
 * 
 * 
 */

// @lc code=start
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
import java.util.ArrayList;
import java.util.Queue;

import java.util.LinkedList;
import java.util.List;
class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if(root == null) return res;
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        while(!q.isEmpty()){
            int size = q.size();
            for(int i = 0; i < size; i++){
                TreeNode node = q.poll();
                if(i == size - 1) res.add(node.val);
                if(node.left != null) q.offer(node.left);
                if(node.right != null) q.offer(node.right);
            }
        }
        return res;
    }
}
// @lc code=end

