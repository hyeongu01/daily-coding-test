package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.List;
import java.util.ArrayList;

/**
 * [프로그래머스 - 기능개발]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/42586
 * <p>
 * 키워드: 큐, 배열
 */
public class D17_FeatureDevelopment {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 기능개발",
                input -> solution.solution((int[]) input[0], (int[]) input[1]),
                new TestCase<>(new Object[]{new int[]{93, 30, 55}, new int[]{1, 30, 5}}, new int[]{2, 1}),
                new TestCase<>(new Object[]{new int[]{95, 90, 99, 99, 80, 99}, new int[]{1, 1, 1, 1, 1, 1}}, new int[]{1, 3, 2})
        );
    }

    static private class Solution {
        public int[] solution(int[] progresses, int[] speeds) {
            int currentPoint = 0;
            List<Integer> list = new ArrayList<>();

            for (int i = 1; i < 100; i++) {
                int count = 0;
                while (currentPoint < progresses.length && progresses[currentPoint] + speeds[currentPoint] * i >= 100) {
                    count ++;
                    currentPoint ++;
                }
                if (count > 0) list.add(count);
            }
            return list.stream().mapToInt(Integer::intValue).toArray();
        }
    }
}
