/*
 * @lc app=leetcode id=117 lang=java
 *
 * [117] Populating Next Right Pointers in Each Node II
 *
 * https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/description/
 *
 * algorithms
 * Medium (56.49%)
 * Likes:    6137
 * Dislikes: 339
 * Total Accepted:    800.1K
 * Total Submissions: 1.4M
 * Testcase Example:  '[1,2,3,4,5,null,7]'
 *
 * Given a binary tree
 * 
 * 
 * struct Node {
 * ⁠ int val;
 * ⁠ Node *left;
 * ⁠ Node *right;
 * ⁠ Node *next;
 * }
 * 
 * 
 * Populate each next pointer to point to its next right node. If there is no
 * next right node, the next pointer should be set to NULL.
 * 
 * Initially, all next pointers are set to NULL.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: root = [1,2,3,4,5,null,7]
 * Output: [1,#,2,3,#,4,5,7,#]
 * Explanation: Given the above binary tree (Figure A), your function should
 * populate each next pointer to point to its next right node, just like in
 * Figure B. The serialized output is in level order as connected by the next
 * pointers, with '#' signifying the end of each level.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: root = []
 * Output: []
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * The number of nodes in the tree is in the range [0, 6000].
 * -100 <= Node.val <= 100
 * 
 * 
 * 
 * Follow-up:
 * 
 * 
 * You may only use constant extra space.
 * The recursive approach is fine. You may assume implicit stack space does not
 * count as extra space for this problem.
 * 
 * 
 */

// @lc code=start
/*
// Definition for a Node.
import java.util.LinkedList;
import java.util.Queue;
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {}
    
    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
};
*/

import java.util.Queue;

class Solution {
    public Node connect(Node root) {
        if (root == null) {
            return root;
        }

        // the curNode as the linkedlist of each level
        Node curNode = root;
        while (curNode != null) {
            // a dummyNode to travesal current Level
            Node dummyNode = new Node(0);

            // the prev Node of next level
            Node prevNode = dummyNode;
            while (curNode != null) {
                if (curNode.left != null) {
                    // linked the left child
                    prevNode.next = curNode.left;
                    // update prev as LinkedList
                    prevNode = curNode.left;
                }

                if (curNode.right != null) {
                    prevNode.next = curNode.right;
                    prevNode = curNode.right;
                }

                // the next node of current level
                curNode = curNode.next;
            }

            // after process the next level, process 
            curNode = dummyNode.next;
        }

        return root;

    }
}
// @lc code=end

