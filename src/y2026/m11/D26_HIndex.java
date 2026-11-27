package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.Arrays;

/**
 * [프로그래머스 - H-Index]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/42747
 * <p>
 * 키워드: 정렬, 투 포인터
 * <p>
 * 풀이 평가 (2026-11-27)
 * - 총평: 정렬 후 h 를 줄이며 포인터를 당기는 풀이. 반례 통과.
 * - 복잡도: 시간 O(N log N) / 공간 O(1)
 * - 개선: h 시작값 1_000 대신 n, 테스트 케이스 추가, 주석 보강
 * - 다음에 시도: 카운팅 배열 O(N), h 이분 탐색
 */
public class D26_HIndex {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] H-Index",
                input -> solution.solution((int[]) input[0]),
                new TestCase<>(new Object[]{new int[]{3, 0, 6, 1, 5}}, 3)
        );
    }

    static private class Solution {
        public int solution(int[] citations) {
            Arrays.sort(citations);
            int n = citations.length;

            int p = n;
            int h = 1_000;

            for (;h > 0; h--) {
                while (p > 0 && citations[p - 1] >= h) p --;
                if (n - p >= h) break;
            }

            return h;
        }
    }
}
