/*
 * @lc app=leetcode id=146 lang=java
 *
 * [146] LRU Cache
 *
 * https://leetcode.com/problems/lru-cache/description/
 *
 * algorithms
 * Medium (46.18%)
 * Likes:    22572
 * Dislikes: 1192
 * Total Accepted:    2.3M
 * Total Submissions: 5.1M
 * Testcase Example:  '["LRUCache","put","put","get","put","get","put","get","get","get"]\n' +
import java.util.Map;
  '[[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]'
 *
 * Design a data structure that follows the constraints of a Least Recently
 * Used (LRU) cache.
 * 
 * Implement the LRUCache class:
 * 
 * 
 * LRUCache(int capacity) Initialize the LRU cache with positive size
 * capacity.
 * int get(int key) Return the value of the key if the key exists, otherwise
 * return -1.
 * void put(int key, int value) Update the value of the key if the key exists.
 * Otherwise, add the key-value pair to the cache. If the number of keys
 * exceeds the capacity from this operation, evict the least recently used
 * key.
 * 
 * 
 * The functions get and put must each run in O(1) average time complexity.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input
 * ["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
 * [[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
 * Output
 * [null, null, null, 1, null, -1, null, -1, 3, 4]
 * 
 * Explanation
 * LRUCache lRUCache = new LRUCache(2);
 * lRUCache.put(1, 1); // cache is {1=1}
 * lRUCache.put(2, 2); // cache is {1=1, 2=2}
 * lRUCache.get(1);    // return 1
 * lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
 * lRUCache.get(2);    // returns -1 (not found)
 * lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
 * lRUCache.get(1);    // return -1 (not found)
 * lRUCache.get(3);    // return 3
 * lRUCache.get(4);    // return 4
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= capacity <= 3000
 * 0 <= key <= 10^4
 * 0 <= value <= 10^5
 * At most 2 * 10^5 calls will be made to get and put.
 * 
 * 
 */

// @lc code=start

class LRUCache {
    class DoublyLinkedNode{
        int key;
        int value;

        DoublyLinkedNode prev;
        DoublyLinkedNode next;

        DoublyLinkedNode(int key, int value){
            this.key= key;
            this.value= value;
        }
    }
    public DoublyLinkedNode[] map;
    public int capacity;
    public DoublyLinkedNode head, tail;

    public LRUCache(int capacity) {
        this.capacity= capacity;
        map= new DoublyLinkedNode[10001];

    }
    
    public int get(int key) {
        if(map[key]==null){
            return -1;
        }
        else{
            DoublyLinkedNode node= map[key];
            removeNode(node);
            addNode(node);
            return node.value;
        }
    }
    
    public void put(int key, int value) {
        if(map[key]!=null){
            DoublyLinkedNode node= map[key];
            node.value= value;
            removeNode(node);
            addNode(node);
        }
        else{
            if(capacity==0){
                map[tail.key]= null;
                removeNode(tail);
            }
            DoublyLinkedNode node= new DoublyLinkedNode(key, value);
            addNode(node);
            map[key]= node;
        }
    }
    public void addNode(DoublyLinkedNode node){
        node.next= head;
        node.prev= null;
        if(head!=null){
            head.prev= node;
        }
        head= node;
        if(tail==null){
            tail= head;
        }
        capacity--;
    }
    public void removeNode(DoublyLinkedNode node){
        DoublyLinkedNode prev= node.prev;
        DoublyLinkedNode next= node.next;
        if(prev!=null){
            prev.next= next;
        }
        else{
            head= next;
        }
        if(next!=null){
            next.prev= prev;
        }
        else{
            tail= prev;
        }
        capacity++;
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
// @lc code=end

