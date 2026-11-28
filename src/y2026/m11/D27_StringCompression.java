package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

/**
 * [프로그래머스 - 문자열 압축]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/60057
 * <p>
 * 키워드: 문자열, 완전 탐색, 구현(시뮬레이션)
 * <p>
 * 풀이 평가 (2026-11-28)
 * - 총평: 예시 5/5 통과. 단위 범위(1~len/2)와 루프 전/중/끝 설계를
 *   먼저 정리하고 그대로 구현함. 소요 약 1시간(구현 구상에 시간 씀).
 * - 복잡도: 시간 O(N^2) / 공간 O(N) — N ≤ 1000이라 충분
 * - 개선: prev/current 버퍼를 직접 채우지 말고 substring+equals로 비교,
 *   결과 문자열 대신 길이만 누적, 반복되는 append 식은 메서드로 묶기,
 *   짧은 꼬리 분기는 일반 흐름으로 합칠 수 있음.
 * - 다음에 시도: 값 대신 인덱스만 들고 regionMatches로 앞 조각과 비교.
 *   설계 단계에서 "값을 들고 다닐지, 위치만 들고 다닐지" 먼저 정하기.
 */
public class D27_StringCompression {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 문자열 압축",
                input -> solution.solution((String) input[0]),
                new TestCase<>(new Object[]{"aabbaccc"}, 7),
                new TestCase<>(new Object[]{"ababcdcdababcdcd"}, 9),
                new TestCase<>(new Object[]{"abcabcdede"}, 8),
                new TestCase<>(new Object[]{"abcabcabcabcdededededede"}, 14),
                new TestCase<>(new Object[]{"xababcdcdababcdcd"}, 17)
        );
    }

    /**
     N 로 압축 (1 <= N <= Math.trunc(s.length / 2))

     - prev[N]
     - current[N]
     - count
     - sb


     * 루프 이전
     prev[] 에 값 채움
     count = 1

     * 루프 돔
     current 에 값 채움
     prev 와 비교 후 같으면 count ++, 다르면 prev 값을 sb 에 넣음 {count}{prev}, prev = current, count = 1
     만약 못채우면, 그 값 그대로 prev 에 넣음

     * 루프 끝
     prev 값 sb 에 넣음
     */
    static private class Solution {
        public int solution(String s) {
            int min = s.length();
            for (int i = 1; i <= s.length() / 2; i++) {
                int count = calcCompressedLength(s, i);
                min = Math.min(count, min);
            }
            return min;
        }

        // 압축한 길이 반환
        private int calcCompressedLength(String s, int n) {
            StringBuilder sb = new StringBuilder();
            StringBuilder prev = new StringBuilder();
            StringBuilder current = new StringBuilder();
            int count = 1;
            int p = 0;

            // 값 채우기
            for (; p < n; p++) {
                prev.append(s.charAt(p));
            }

            // 반복문
            while (p < s.length()) {
                // current 값 채움
                for (int i = 0; i < n && p + i < s.length(); i++) {
                    current.append(s.charAt(i + p));
                }
                if (current.length() < n) {
                    sb.append((count == 1 ? "" : count) + prev.toString());
                    count = 1;
                    prev = current;
                    break;
                }

                if (prev.compareTo(current) == 0) {
                    count ++;
                } else {
                    sb.append((count == 1 ? "" : count) + prev.toString());
                    count = 1;
                    prev = current;
                }
                current = new StringBuilder();
                p += n;
            }
            // prev 값을 마지막에 추가
            sb.append((count == 1 ? "" : count) + prev.toString());
            return sb.toString().length();
        }
    }
}
