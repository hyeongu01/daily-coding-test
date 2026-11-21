package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

/**
 * https://leetcode.com/problems/next-greater-element-ii
 * <p>
 * 키워드: Monotonic Stack, Array
 * <p>
 * 풀이 평가 (2026-11-21, 풀이 40분)
 * - 총평: 예시 2/2, 무작위 2만 건(중복·음수 포함) 완전탐색과 일치.
 *   최댓값에서 시작해 원형을 한 바퀴로 끊은 아이디어가 좋음.
 * - 복잡도: 시간 O(N) 분할 상환 / 공간 O(N). result 를 "다음 큰 값의
 *   인덱스" 체인으로 써서 점프하는 방식으로, 체인이 곧 단조 스택 역할.
 * - 개선: 최댓값 찾기는 if (nums[i] >= max) 한 블록으로 쓰면 더 명확.
 *   i 를 maxIndex-1+n 부터 거꾸로 도는 대신 k = 1..n-1 오프셋으로 돌면
 *   범위가 한눈에 보임. result 가 인덱스→값으로 의미가 바뀌는 점 주의.
 * - 다음에 시도: 최댓값을 찾지 않고 i = 2n-1 → 0 을 i % n 으로 도는
 *   명시적 단조 스택 풀이와 비교. 비슷한 패턴: Online Stock Span(901).
 */
class D21_NextGreaterElementII {

    public static void main(String[] args) {
        D21_NextGreaterElementII solution = new D21_NextGreaterElementII();

        TestRunner.run("Next Greater Element II",
                solution::nextGreaterElements,
                new TestCase<>(new int[]{1, 2, 1}, new int[]{2, -1, 2}),
                new TestCase<>(new int[]{1, 2, 3, 4, 3}, new int[]{2, 3, 4, -1, 4})
        );
    }

    /**
     * 10 ^ 4 이니까 O(n^2) 이상이면 시간초과 날듯
     * 시간복잡도: O(n)
     * 공간복잡도: O(n)
     * <p>
     * 최대값을 찾아서 해당 값부터 시작
     */
    public int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // 최대값, 인덱스 찾음
        int max = nums[0];
        int maxIndex = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, nums[i]);
            maxIndex = nums[i] == max ? i : maxIndex;
        }
        result[maxIndex] = -1;
        // maxIndex - 1 ~ maxIndex + 1 % n 까지 진행
        for (int i = maxIndex - 1 + n; i >= maxIndex + 1; i--) {
            int currentIndex = i % n;
            int targetIndex = (currentIndex + 1) % n;
            while (targetIndex != -1 && nums[targetIndex] <= nums[currentIndex]) {
                targetIndex = result[targetIndex];
            }
            result[currentIndex] = targetIndex;
        }
        // result 인덱스 -> 값 변경
        for (int i = 0; i < n; i++) {
            result[i] = result[i] == -1 ? -1 : nums[result[i]];
        }

        return result;
    }
}
