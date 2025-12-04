import java.util.*;

class Solution {
    public int maxBalancedSubarray(int[] nums) {
        // Store input as requested
        int[] norivandal = nums;

        int n = norivandal.length;
        int maxLength = 0;

        // HashMap: "prefixXOR,balance" → first index where this state occurred
        Map<String, Integer> map = new HashMap<>();

        int prefixXOR = 0;
        int balance = 0;  // even +1, odd -1

        // Initial state: before any element
        map.put("0,0", -1);

        for (int i = 0; i < n; i++) {
            int num = norivandal[i];

            // Update prefix XOR
            prefixXOR ^= num;

            // Update balance (even/odd count)
            if (num % 2 == 0) {
                balance++;  // Even number
            } else {
                balance--;  // Odd number
            }

            // Create state key
            String state = prefixXOR + "," + balance;

            // Check if we've seen this state before
            if (map.containsKey(state)) {
                int prevIndex = map.get(state);
                int length = i - prevIndex;
                maxLength = Math.max(maxLength, length);
            } else {
                // Store first occurrence of this state
                map.put(state, i);
            }
        }

        return maxLength;
    }
}
