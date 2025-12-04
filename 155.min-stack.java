/*
 * @lc app=leetcode id=155 lang=java
 *
 * [155] Min Stack
 *
 * https://leetcode.com/problems/min-stack/description/
 *
 * algorithms
 * Medium (57.18%)
 * Likes:    15596
 * Dislikes: 978
 * Total Accepted:    2.5M
 * Total Submissions: 4.3M
 * Testcase Example:  '["MinStack","push","push","push","getMin","pop","top","getMin"]\n' +
import java.util.Stack;
  '[[],[-2],[0],[-3],[],[],[],[]]'
 *
 * Design a stack that supports push, pop, top, and retrieving the minimum
 * element in constant time.
 * 
 * Implement the MinStack class:
 * 
 * 
 * MinStack() initializes the stack object.
 * void push(int val) pushes the element val onto the stack.
 * void pop() removes the element on the top of the stack.
 * int top() gets the top element of the stack.
 * int getMin() retrieves the minimum element in the stack.
 * 
 * 
 * You must implement a solution with O(1) time complexity for each
 * function.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input
 * ["MinStack","push","push","push","getMin","pop","top","getMin"]
 * [[],[-2],[0],[-3],[],[],[],[]]
 * 
 * Output
 * [null,null,null,null,-3,null,0,-2]
 * 
 * Explanation
 * MinStack minStack = new MinStack();
 * minStack.push(-2);
 * minStack.push(0);
 * minStack.push(-3);
 * minStack.getMin(); // return -3
 * minStack.pop();
 * minStack.top();    // return 0
 * minStack.getMin(); // return -2
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * -2^31 <= val <= 2^31 - 1
 * Methods pop, top and getMin operations will always be called on non-empty
 * stacks.
 * At most 3 * 10^4 calls will be made to push, pop, top, and getMin.
 * 
 * 
 */

// @lc code=start

import java.util.*;
import java.util.Collections;

class MinStack {
    Stack<Integer> stack = new Stack<>();
    int minStack;

    public MinStack() {
        minStack = Integer.MAX_VALUE;
    }
    
    public void push(int val) {
        stack.push(val);
        minStack = Math.min(minStack, val);
    }
    
    public void pop() {
        stack.pop();
        if(stack.isEmpty()) {
            minStack = Integer.MAX_VALUE;
        } else {
            minStack = Collections.min(stack);
        }
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return minStack;
    }
}

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(val);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.getMin();
 */
// @lc code=end

