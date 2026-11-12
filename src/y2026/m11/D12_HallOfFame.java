package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.Queue;
import java.util.PriorityQueue;

/**
 * [프로그래머스 - 명예의 전당 (1)]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/138477
 * <p>
 * 키워드: 우선순위 큐, 최대/최소 힙
 */
public class D12_HallOfFame {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 명예의 전당 (1)",
                input -> solution.solution((int) input[0], (int[]) input[1]),
                new TestCase<>(new Object[]{3, new int[]{10, 100, 20, 150, 1, 100, 200}}, new int[]{10, 10, 10, 20, 20, 100, 100}),
                new TestCase<>(new Object[]{4, new int[]{0, 300, 40, 300, 20, 70, 150, 50, 500, 1000}}, new int[]{0, 0, 0, 0, 20, 40, 70, 70, 150, 300})
        );
    }

    static private class Solution {
        public int[] solution(int k, int[] score) {
            int[] result = new int[score.length];
            Queue<Integer> queue = new PriorityQueue<>((t1, t2) -> {
                return t1 - t2; // 오름차순
            });

            for (int i = 0; i < score.length; i++) {
                queue.add(score[i]);
                if (i >= k) queue.poll();
                result[i] = queue.peek();
            }

            return result;
        }
    }
}
