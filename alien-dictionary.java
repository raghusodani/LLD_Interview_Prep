// Alien Dictionary - Graph + Topological Sort

import java.util.*;

class Solution {
    public String alienOrder(String[] words) {
        // Step 1: Build adjacency list and in-degree map
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();

        // Initialize: add all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }

        // Step 2: Build graph by comparing adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // Check invalid case: longer word as prefix
            if (word1.startsWith(word2) && word1.length() > word2.length()) {
                return "";  // Invalid ordering
            }

            // Find first different character
            int minLen = Math.min(word1.length(), word2.length());
            for (int j = 0; j < minLen; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);

                if (c1 != c2) {
                    // c1 comes before c2 in alien language
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    break;  // Only first difference matters
                }
            }
        }

        // Step 3: Topological sort using BFS (Kahn's algorithm)
        Queue<Character> queue = new LinkedList<>();

        // Add all characters with in-degree 0
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.offer(c);
            }
        }

        StringBuilder result = new StringBuilder();

        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);

            // Reduce in-degree for neighbors
            for (char neighbor : graph.get(c)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // Step 4: Check if valid (all characters included)
        if (result.length() != inDegree.size()) {
            return "";  // Cycle detected or invalid
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1
        String[] words1 = {"wrt", "wrf", "er", "ett", "rftt"};
        System.out.println(sol.alienOrder(words1));  // "wertf"

        // Test 2
        String[] words2 = {"z", "x"};
        System.out.println(sol.alienOrder(words2));  // "zx"

        // Test 3
        String[] words3 = {"z", "x", "z"};
        System.out.println(sol.alienOrder(words3));  // "" (invalid)

        // Test 4
        String[] words4 = {"abc", "ab"};
        System.out.println(sol.alienOrder(words4));  // "" (invalid: longer prefix)
    }
}
