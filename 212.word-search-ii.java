/*
 * @lc app=leetcode id=212 lang=java
 *
 * [212] Word Search II
 *
 * https://leetcode.com/problems/word-search-ii/description/
 *
 * algorithms
 * Hard (37.76%)
 * Likes:    10024
 * Dislikes: 504
 * Total Accepted:    853.8K
 * Total Submissions: 2.3M
 * Testcase Example:  '[["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]]\n' +
  '["oath","pea","eat","rain"]'
 *
 * Given an m x n board of characters and a list of strings words, return all
 * words on the board.
 * 
 * Each word must be constructed from letters of sequentially adjacent cells,
 * where adjacent cells are horizontally or vertically neighboring. The same
 * letter cell may not be used more than once in a word.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: board =
 * [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]],
 * words = ["oath","pea","eat","rain"]
 * Output: ["eat","oath"]
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: board = [["a","b"],["c","d"]], words = ["abcb"]
 * Output: []
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * m == board.length
 * n == board[i].length
 * 1 <= m, n <= 12
 * board[i][j] is a lowercase English letter.
 * 1 <= words.length <= 3 * 10^4
 * 1 <= words[i].length <= 10
 * words[i] consists of lowercase English letters.
 * All the strings of words are unique.
 * 
 * 
 */

// @lc code=start

import java.util.ArrayList;
import java.util.List;

class Solution {
    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    private TrieNode root = new TrieNode();
    private List<String> result = new ArrayList<>();

    public List<String> findWords(char[][] board, String[] words) {
        // Step 1: Build Trie from words
        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = word;
        }

        // Step 2: DFS from each cell
        int m = board.length;
        int n = board[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(board, i, j, root);
            }
        }
        
        return result;
    }

    private void dfs(char[][] board, int i, int j, TrieNode node) {
        // Boundary check
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
            return;
        }

        char c = board[i][j];

        // Already visited or no matching child in Trie
        if (c == '#' || node.children[c - 'a'] == null) {
            return;
        }

        // Move to next Trie node
        node = node.children[c - 'a'];

        // Found a word
        if (node.word != null) {
            result.add(node.word);
            node.word = null;  // Avoid duplicates
        }

        // Mark as visited
        board[i][j] = '#';

        // Explore 4 directions
        dfs(board, i + 1, j, node);
        dfs(board, i - 1, j, node);
        dfs(board, i, j + 1, node);
        dfs(board, i, j - 1, node);

        // Backtrack
        board[i][j] = c;

        // Optimization: Prune Trie (remove leaf nodes)
        if (allChildrenNull(node)) {
            // This path leads to no more words, can prune
            // But we don't have parent reference, so skip for simplicity
        }
    }

    private boolean allChildrenNull(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return false;
        }
        return true;
    }
}
// @lc code=end

