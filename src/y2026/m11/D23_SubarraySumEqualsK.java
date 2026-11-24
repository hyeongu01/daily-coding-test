package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

/**
 * [LeetCode 560 - Subarray Sum Equals K]
 * <p>
 * https://leetcode.com/problems/subarray-sum-equals-k
 * <p>
 * 키워드: prefix-sum, hash-map
 */
// TODO(daily-problem): 풀이 완료 후 이 줄을 삭제하세요
class D23_SubarraySumEqualsK {

    public static void main(String[] args) {
        D23_SubarraySumEqualsK solution = new D23_SubarraySumEqualsK();

        TestRunner.run("Subarray Sum Equals K",
                input -> solution.subarraySum((int[]) input[0], (int) input[1]),
                new TestCase<>(new Object[]{new int[]{1, 1, 1}, 2}, 2),
                new TestCase<>(new Object[]{new int[]{1, 2, 3}, 3}, 2)
        );
    }

    public int subarraySum(int[] nums, int k) {
        return 0;
    }
}
