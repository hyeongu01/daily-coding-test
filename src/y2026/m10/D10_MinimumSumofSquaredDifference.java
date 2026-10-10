package y2026.m10;

import utils.TestCase;
import utils.TestRunner;

import java.util.Map;
import java.util.HashMap;

public class D10_MinimumSumofSquaredDifference {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("Minimum Sum of Squared Difference",
                input -> solution.minSumSquareDiff((int[]) input[0], (int[]) input[1], (int) input[2], (int) input[3]),
                new TestCase<>(new Object[]{new int[]{1, 2, 3, 4}, new int[]{2, 10, 20, 19}, 0, 0}, 579L),
                new TestCase<>(new Object[]{new int[]{1,4,10,12}, new int[]{5,8,6,9}, 1, 1}, 43L)
        );
    }


    // 목표: O(n^2) 넘지 않기 - O(nlogn) 으로 끝내기
    // 결과: O(nlogn) (우선순위 큐 사용)
    // 1차 피드백: k1 + k2 가 커서 문제가 발생함 (10^9)
    // 시간복잡도가 O(knlogn) - k: k1 + k2 이었음
    // 결론: 우선순위 큐 -> 해시맵
    static class Solution {
        public long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2) {
            Map<Integer, Integer> diffMap = new HashMap<>();
            int max = 0;
            for (int i = 0; i < nums1.length; i++) {
                int key = Math.abs(nums1[i] - nums2[i]);
                int val = diffMap.getOrDefault(key, 0);
                diffMap.put(key, val + 1);
                max = Math.max(max, key);
            }

            long k = (long) k1 + (long) k2;
            while (k > 0) {
                if (max == 0) break;
                int val = diffMap.get(max);
                int prevVal = diffMap.getOrDefault(max - 1, 0);
                if (val > k) { // k 개 이동
                    diffMap.put(max, val - (int) k);
                    diffMap.put(max - 1, prevVal + (int) k);
                    k = 0;
                } else { // val 개 이동
                    diffMap.remove(max);
                    diffMap.put(max - 1, prevVal + val);
                    k -= val;
                }
                max--;
            }

            long result = 0;
            for (int key : diffMap.keySet()) {
                int val = diffMap.get(key);
                result += (long) key * key * val;
            }
            return result;
        }
    }

}
