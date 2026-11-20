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
