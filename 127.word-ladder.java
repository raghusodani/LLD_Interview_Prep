/*
 * @lc app=leetcode id=127 lang=java
 *
 * [127] Word Ladder
 *
 * https://leetcode.com/problems/word-ladder/description/
 *
 * algorithms
 * Hard (44.09%)
 * Likes:    13206
 * Dislikes: 1956
 * Total Accepted:    1.5M
 * Total Submissions: 3.4M
 * Testcase Example:  '"hit"\n"cog"\n["hot","dot","dog","lot","log","cog"]'
 *
 * A transformation sequence from word beginWord to word endWord using a
 * dictionary wordList is a sequence of words beginWord -> s1 -> s2 -> ... ->
 * sk such that:
 * 
 * 
 * Every adjacent pair of words differs by a single letter.
 * Every si for 1 <= i <= k is in wordList. Note that beginWord does not need
 * to be in wordList.
 * sk == endWord
 * 
 * 
 * Given two words, beginWord and endWord, and a dictionary wordList, return
 * the number of words in the shortest transformation sequence from beginWord
 * to endWord, or 0 if no such sequence exists.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: beginWord = "hit", endWord = "cog", wordList =
 * ["hot","dot","dog","lot","log","cog"]
 * Output: 5
 * Explanation: One shortest transformation sequence is "hit" -> "hot" -> "dot"
 * -> "dog" -> cog", which is 5 words long.
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: beginWord = "hit", endWord = "cog", wordList =
 * ["hot","dot","dog","lot","log"]
 * Output: 0
 * Explanation: The endWord "cog" is not in wordList, therefore there is no
 * valid transformation sequence.
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 1 <= beginWord.length <= 10
 * endWord.length == beginWord.length
 * 1 <= wordList.length <= 5000
 * wordList[i].length == beginWord.length
 * beginWord, endWord, and wordList[i] consist of lowercase English
 * letters.
 * beginWord != endWord
 * All the words in wordList are unique.
 * 
 * 
 */

// @lc code=start
import java.util.*;
class Solution {
    /**
     * Word Ladder using BFS (Optimized)
     *
     * Optimizations:
     * 1. Remove from wordSet instead of using separate visited set
     * 2. Early termination when endWord is found
     * 3. Reuse char array
     *
     * Time: O(M² × N) where M = word length, N = word list size
     * Space: O(N) for queue and set
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // Convert to set for O(1) lookup
        Set<String> wordSet = new HashSet<>(wordList);

        // Early exit if endWord not in list
        if (!wordSet.contains(endWord)) return 0;

        // BFS
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);

        int steps = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();

                // Try changing each character
                char[] wordArray = word.toCharArray();
                for (int j = 0; j < wordArray.length; j++) {
                    char original = wordArray[j];  // Save original

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;  // Skip same character

                        wordArray[j] = c;
                        String newWord = new String(wordArray);

                        // Check if in wordSet (not visited)
                        if (wordSet.contains(newWord)) {
                            if (newWord.equals(endWord)) return steps + 1;  // Found!

                            queue.add(newWord);
                            wordSet.remove(newWord);  // Mark as visited
                        }
                    }

                    wordArray[j] = original;  // Restore
                }
            }
            steps++;
        }
        return 0;
        
    }
}
// @lc code=end

