/*
 * @lc app=leetcode id=61 lang=java
 *
 * [61] Rotate List
 *
 * https://leetcode.com/problems/rotate-list/description/
 *
 * algorithms
 * Medium (40.71%)
 * Likes:    10942
 * Dislikes: 1529
 * Total Accepted:    1.5M
 * Total Submissions: 3.6M
 * Testcase Example:  '[1,2,3,4,5]\n2'
 *
 * Given the head of a linked list, rotate the list to the right by k
 * places.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: head = [1,2,3,4,5], k = 2
 * Output: [4,5,1,2,3]
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: head = [0,1,2], k = 4
 * Output: [2,0,1]
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * The number of nodes in the list is in the range [0, 500].
 * -100 <= Node.val <= 100
 * 0 <= k <= 2 * 10^9
 * 
 * 
 */

// @lc code=start
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        // if head is null, return null
        if(head == null) return null;
        // if head.next is null, return head
        if(head.next == null) return head;
        // if k is 0, return head
        if(k == 0) return head;

        // get the length of the list
        ListNode dummy = new ListNode(0, head);
        ListNode slow = dummy, fast = dummy;
        int length = 0;
        while(fast.next != null) {
            fast = fast.next;
            length++;
        }
        // get the k
        k = k % length;
        while(length - k > 0) {
            slow = slow.next;
            k++;
        }
        fast.next = dummy.next;
        dummy.next = slow.next;
        slow.next = null;
        return dummy.next;

    }
}
// @lc code=end

