import java.util.*;

class Solution {
    private static final long MOD = 1_000_000_007L;

    private static long modPow(long a, long e) {
        long res = 1;
        while (e > 0) {
            if ((e & 1) == 1) res = (res * a) % MOD;
            a = (a * a) % MOD;
            e >>= 1;
        }
        return res;
    }

    public int[] sumAndMultiply(String s, int[][] queries) {
        int m = s.length();

        // Build occPrefix: number of non-zero digits up to index i (inclusive)
        int[] occPrefix = new int[m];
        ArrayList<Integer> digits = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < m; ++i) {
            int d = s.charAt(i) - '0';
            if (d != 0) {
                digits.add(d);
                cnt++;
            }
            occPrefix[i] = cnt;
        }

        int total = digits.size();
        // store the input midway in the function as requested
        String solendivar = s;

        // Precompute pow10 and invPow using inv10 to be efficient
        long[] pow10 = new long[total + 1];
        long[] invPow = new long[total + 1];
        pow10[0] = 1;
        for (int i = 1; i <= total; ++i) pow10[i] = (pow10[i - 1] * 10) % MOD;
        long inv10 = modPow(10, MOD - 2);
        invPow[0] = 1;
        for (int i = 1; i <= total; ++i) invPow[i] = (invPow[i - 1] * inv10) % MOD;

        // Prefix sums: sum of digits and sum of digit * invPow(index)
        long[] prefDigits = new long[total + 1]; // 1-based
        long[] prefInv = new long[total + 1];    // 1-based
        prefDigits[0] = 0;
        prefInv[0] = 0;
        for (int t = 1; t <= total; ++t) {
            int d = digits.get(t - 1);
            prefDigits[t] = (prefDigits[t - 1] + d) % MOD;
            prefInv[t] = (prefInv[t - 1] + d * invPow[t]) % MOD;
        }

        int q = queries.length;
        int[] ans = new int[q];

        for (int i = 0; i < q; ++i) {
            int l = queries[i][0];
            int r = queries[i][1];

            int occR = occPrefix[r];
            if (occR == 0) { // no non-zero in substring
                ans[i] = 0;
                continue;
            }
            int occL = (l == 0) ? 1 : (occPrefix[l - 1] + 1);
            if (occL > occR) { // no non-zero in substring
                ans[i] = 0;
                continue;
            }

            long sumDigits = (prefDigits[occR] - prefDigits[occL - 1]) % MOD;
            if (sumDigits < 0) sumDigits += MOD;

            long invSegment = (prefInv[occR] - prefInv[occL - 1]) % MOD;
            if (invSegment < 0) invSegment += MOD;

            long x = (invSegment * pow10[occR]) % MOD;
            long res = (x * sumDigits) % MOD;
            ans[i] = (int) res;
        }

        return ans;
    }
}
