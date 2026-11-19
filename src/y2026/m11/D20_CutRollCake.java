package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.Map;
import java.util.HashMap;

/**
 * [프로그래머스 - 롤케이크 자르기]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/132265
 * <p>
 * 키워드: 해시셋, 해시맵
 */
public class D20_CutRollCake {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 롤케이크 자르기",
                input -> solution.solution((int[]) input[0]),
                new TestCase<>(new Object[]{new int[]{1, 2, 1, 3, 1, 4, 1, 2}}, 2),
                new TestCase<>(new Object[]{new int[]{1, 2, 3, 1, 4}}, 0)
        );
    }

    static private class Solution {
        public int solution(int[] topping) {
            Map<Integer, Integer> firstPiece = new HashMap<>();
            Map<Integer, Integer> secondPiece = new HashMap<>();
            int result = 0;

            for (int t: topping) {
                secondPiece.put(t, secondPiece.getOrDefault(t, 0) + 1);
            }

            for (int t: topping) {
                // secondPiece 에서 꺼냄
                int secondCount = secondPiece.getOrDefault(t, 0) - 1; // 무조건 있어야함
                if (secondCount <= 0) {
                    secondPiece.remove(t);
                } else {
                    secondPiece.put(t, secondCount);
                }

                // firstPiece 에 추가
                firstPiece.put(t, firstPiece.getOrDefault(t, 0) + 1);

                // 종류 개수 비교
                if (firstPiece.size() == secondPiece.size()) result++;
            }
            return result;
        }
    }
}
