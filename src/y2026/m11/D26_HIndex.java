package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

/**
 * [프로그래머스 - H-Index]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/42747
 */
public class D26_HIndex {

    // TODO(daily-problem): 풀이 완료 후 이 줄을 삭제하세요

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] H-Index",
                input -> solution.solution((int[]) input[0]),
                new TestCase<>(new Object[]{new int[]{3, 0, 6, 1, 5}}, 3)
        );
    }

    static private class Solution {
        public int solution(int[] citations) {
            int answer = 0;
            return answer;
        }
    }
}
