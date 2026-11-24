package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * [LeetCode 560 - Subarray Sum Equals K]
 * <p>
 * https://leetcode.com/problems/subarray-sum-equals-k
 * <p>
 * 키워드: prefix-sum, hash-map
 * <p>
 * 풀이 평가 (2026-11-24)
 * - 총평: 예시 통과. 누적합 + 해시맵 카운팅의 정석 풀이, map.put(0, 1) 초기화까지
 *   정확함. 1차 35분 실패 → 다음 날 6분 성공.
 * - 복잡도: 시간 O(N) / 공간 O(N)
 * - 개선: map → prefixCount 등 의미 있는 이름,
 *   카운트 증가는 merge(sum, 1, Integer::sum) 관용구로 간결하게.
 * - 다음에 시도: "조회 후 저장" 순서를 바꾸면 왜 틀리는지 k=0 케이스로 확인,
 *   같은 패턴 변형(나머지를 키로 쓰는 문제)에 적용해 보기.
 */
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
        int sum = 0; int result = 0;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);

        for (int n: nums) {
            sum += n;
            // sum - key = k 인 key 개수 찾아서 result 에 넣기
            result += map.getOrDefault(sum - k, 0);
            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return result;
    }
}
