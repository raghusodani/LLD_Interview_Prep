/*
 * @lc app=leetcode id=433 lang=java
 *
 * [433] Minimum Genetic Mutation
 *
 * https://leetcode.com/problems/minimum-genetic-mutation/description/
 *
 * algorithms
 * Medium (56.02%)
 * Likes:    3251
 * Dislikes: 344
 * Total Accepted:    258.2K
 * Total Submissions: 460.4K
 * Testcase Example:  '"AACCGGTT"\n"AACCGGTA"\n["AACCGGTA"]'
 *
 * A gene string can be represented by an 8-character long string, with choices
 * from 'A', 'C', 'G', and 'T'.
 * 
 * Suppose we need to investigate a mutation from a gene string startGene to a
 * gene string endGene where one mutation is defined as one single character
 * changed in the gene string.
 * 
 * 
 * For example, "AACCGGTT" --> "AACCGGTA" is one mutation.
 * 
 * 
 * There is also a gene bank bank that records all the valid gene mutations. A
 * gene must be in bank to make it a valid gene string.
 * 
 * Given the two gene strings startGene and endGene and the gene bank bank,
 * return the minimum number of mutations needed to mutate from startGene to
 * endGene. If there is no such a mutation, return -1.
 * 
 * Note that the starting point is assumed to be valid, so it might not be
 * included in the bank.
 * 
 * 
 * Example 1:
 * 
 * 
 * Input: startGene = "AACCGGTT", endGene = "AACCGGTA", bank = ["AACCGGTA"]
 * Output: 1
 * 
 * 
 * Example 2:
 * 
 * 
 * Input: startGene = "AACCGGTT", endGene = "AAACGGTA", bank =
 * ["AACCGGTA","AACCGCTA","AAACGGTA"]
 * Output: 2
 * 
 * 
 * 
 * Constraints:
 * 
 * 
 * 0 <= bank.length <= 10
 * startGene.length == endGene.length == bank[i].length == 8
 * startGene, endGene, and bank[i] consist of only the characters ['A', 'C',
 * 'G', 'T'].
 * 
 * 
 */

// @lc code=start
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Set;
class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        if (startGene.equals(endGene)) {
            return 0;
        }

        char[] chars = { 'A', 'C', 'G', 'T' };
        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(startGene);
        visited.add(startGene);

        int mutations = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                if (current.equals(endGene)) {
                    return mutations;
                }

                char[] currentChars = current.toCharArray();
                for (int j = 0; j < currentChars.length; j++) {
                    char old = currentChars[j];
                    for (char c : chars) {
                        currentChars[j] = c;
                        String next = new String(currentChars);
                        if (!visited.contains(next) && bankSet.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                    currentChars[j] = old;
                }
            }
            mutations++;
        }

        return -1;
        
    }
}
// @lc code=end

