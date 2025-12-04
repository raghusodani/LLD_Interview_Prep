// Word Ladder - Bidirectional BFS (FASTEST Optimization)

import java.util.*;

class Solution {
    /**
     * Word Ladder using Bidirectional BFS
     *
     * Key Optimization: Search from BOTH ends simultaneously!
     * - Start BFS from beginWord
     * - Start BFS from endWord
     * - When they meet, we found the shortest path
     *
     * Why faster?
     * - Single BFS: explores up to 2^d nodes (d = depth)
     * - Bidirectional: explores up to 2 × 2^(d/2) nodes
     * - Example: depth 10
     *   - Single: 2^10 = 1024 nodes
     *   - Bidirectional: 2 × 2^5 = 64 nodes
     *
     * Time: O(M² × N) but with much better constants
     * Space: O(N)
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);

        if (!wordSet.contains(endWord)) return 0;

        // Two sets for bidirectional search
        Set<String> beginSet = new HashSet<>();
        Set<String> endSet = new HashSet<>();

        beginSet.add(beginWord);
        endSet.add(endWord);

        int steps = 1;

        while (!beginSet.isEmpty() && !endSet.isEmpty()) {
            // Always expand the smaller set (optimization)
            if (beginSet.size() > endSet.size()) {
                Set<String> temp = beginSet;
                beginSet = endSet;
                endSet = temp;
            }

            // Expand beginSet by one level
            Set<String> nextLevel = new HashSet<>();

            for (String word : beginSet) {
                char[] wordArray = word.toCharArray();

                for (int j = 0; j < wordArray.length; j++) {
                    char original = wordArray[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;

                        wordArray[j] = c;
                        String newWord = new String(wordArray);

                        // Found intersection! Two BFS met!
                        if (endSet.contains(newWord)) {
                            return steps + 1;
                        }

                        // Add to next level if in wordSet
                        if (wordSet.contains(newWord)) {
                            nextLevel.add(newWord);
                            wordSet.remove(newWord);
                        }
                    }

                    wordArray[j] = original;
                }
            }

            beginSet = nextLevel;
            steps++;
        }

        return 0;
    }
}
