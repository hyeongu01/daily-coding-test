package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * [프로그래머스 - 뒤에 있는 큰 수 찾기]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/154539
 * <p>
 * 키워드: 스택, 단조 스택
 * <p>
 * 풀이 평가 (2026-11-20)
 * - 총평: 예시 2/2 통과. 뒤에서부터 값을 쌓는 단조 스택 풀이로, 이
 *   문제의 정석 접근이며 중복 값(<=) 처리도 정확함.
 * - 복잡도: 시간 O(N) / 공간 O(N). 각 원소가 덱에 한 번 들어가고
 *   한 번 나오므로 while 을 합쳐도 N 번 이하(분할 상환).
 * - 개선: Deque 를 스택으로만 쓰므로 push/pop/peek 으로 바꾸면 의도가
 *   더 드러남. 주석의 "오름차순"은 top 기준이라는 설명을 덧붙이면 좋음.
 * - 다음에 시도: (1) 앞에서부터 인덱스를 쌓는 방식과 비교
 *   (2) Integer 박싱 없이 int[] 로 스택 직접 구현
 *   (3) 스택 없이 answer 배열만으로 풀기
 */
public class D20_BiggerNumberBehind {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 뒤에 있는 큰 수 찾기",
                solution::solution,
                new TestCase<>(new int[]{2, 3, 3, 5}, new int[]{3, 5, 5, -1}),
                new TestCase<>(new int[]{9, 1, 5, 3, 6, 2}, new int[]{-1, 5, 6, 6, -1, -1})
        );
    }

    static private class Solution {
        public int[] solution(int[] numbers) {
            Deque<Integer> deque = new ArrayDeque<>(); // 항상 오름차순을 유지하는 덱
            int[] answer = new int[numbers.length];

            for (int i = numbers.length - 1; i >= 0; i--) {
                int n = numbers[i];
                // 자신보다 작은 값 제거
                while (!deque.isEmpty() && deque.peekFirst() <= n) {
                    deque.pollFirst();
                }
                answer[i] = deque.isEmpty() ? -1 : deque.peekFirst();
                deque.offerFirst(n);
            }
            return answer;
        }
    }
}
