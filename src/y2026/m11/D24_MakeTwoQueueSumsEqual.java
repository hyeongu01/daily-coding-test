package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * [프로그래머스 - 두 큐 합 같게 만들기]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/118667
 * <p>
 * 키워드: two-pointers, queue
 * <p>
 * 풀이 평가 (2026-11-25)
 * - 총평: 1차(누적합+해시맵) 실패 후 재도전 성공. 두 큐를 배열 하나로 보고
 *   [l, r) 구간 투포인터 + r % 2n 순환 처리. 무작위 6만 케이스 완전탐색과 일치.
 * - 복잡도: 시간 O(N) / 공간 O(N)
 * - 개선: 미사용 import(HashMap, Map) 제거, count 는 l + (r - n) 과 같아 생략 가능,
 *   sum1/sum2 루프는 arr 복사 루프와 합치거나 System.arraycopy 사용.
 * - 다음에 시도: 종료 조건(l < 2n, r < 3n)이 왜 충분한지 직접 설명해 보기,
 *   "남는 구간의 합"으로 뒤집어 보는 비슷한 문제(LeetCode 1658) 풀어 보기.
 */
public class D24_MakeTwoQueueSumsEqual {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 두 큐 합 같게 만들기",
                input -> solution.solution((int[]) input[0], (int[]) input[1]),
                new TestCase<>(new Object[]{new int[]{3, 2, 7, 2}, new int[]{4, 6, 5, 1}}, 2),
                new TestCase<>(new Object[]{new int[]{1, 2, 1, 2}, new int[]{1, 10, 1, 2}}, 7),
                new TestCase<>(new Object[]{new int[]{1, 1}, new int[]{1, 5}}, -1),
                new TestCase<>(new Object[]{new int[]{10, 1, 1, 2}, new int[]{1, 2, 1, 2}}, 5)
        );
    }

    static private class Solution {
        public int solution(int[] queue1, int[] queue2) {
            int[] arr = new int[queue1.length + queue2.length];
            for (int i = 0; i < queue1.length; i++) {
                arr[i] = queue1[i];
            }
            for (int i = 0; i < queue2.length; i++) {
                arr[i + queue1.length] = queue2[i];
            }

            long sum1 = 0L; long sum2 = 0L;
            for (int n: queue1) {
                sum1 += n;
            }
            for (int n: queue2) {
                sum2 += n;
            }
            long sum = sum1 + sum2;
            if (sum % 2 == 1) return -1;
            long target = sum / 2;

            int l = 0; int r = queue1.length;
            long currentSum = sum1;
            int count = 0;
            while (l < arr.length && r < queue1.length + arr.length) {
                if (currentSum > target) {
                    currentSum -= arr[l++];
                } else if (currentSum < target) {
                    currentSum += arr[r++ % arr.length];
                } else {
                    return count;
                }
                count ++;
            }
            return -1;
        }
    }
}
