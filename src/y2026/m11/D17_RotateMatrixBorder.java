package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

/**
 * [프로그래머스 - 행렬 테두리 회전하기]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/77485
 * <p>
 * 키워드: 시뮬레이션, 행렬
 */
public class D17_RotateMatrixBorder {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 행렬 테두리 회전하기",
                input -> solution.solution((int) input[0], (int) input[1], (int[][]) input[2]),
                new TestCase<>(new Object[]{6, 6, new int[][]{{2, 2, 5, 4}, {3, 3, 6, 6}, {5, 1, 6, 3}}}, new int[]{8, 10, 25}),
                new TestCase<>(new Object[]{3, 3, new int[][]{{1, 1, 2, 2}, {1, 2, 2, 3}, {2, 1, 3, 2}, {2, 2, 3, 3}}}, new int[]{1, 1, 5, 3}),
                new TestCase<>(new Object[]{100, 97, new int[][]{{1, 1, 100, 97}}}, new int[]{1})
        );
    }

    static private class Solution {
        public int[] solution(int rows, int columns, int[][] queries) {
            int[][] matrix = new int[rows][columns];
            int[] answer = new int[queries.length];
            int point = 0;

            for (int i = 0; i < rows * columns; i++) {
                matrix[i / columns][i % columns] = i + 1;
            }
            for (int[] query : queries) {
                answer[point++] = rotate(matrix, query);
            }
            return answer;
        }

        // 회전시키고, 최소값 반환
        private int rotate(int[][] matrix, int[] query) {
            int y1 = query[0] - 1;
            int x1 = query[1] - 1;
            int y2 = query[2] - 1;
            int x2 = query[3] - 1;
            int n = (y2 + x2 - y1 - x1) * 2;

            int direction = 0; // 0: right, 1: bottom, 2: left, 3: top
            int currentY = y1;
            int currentX = x1;
            int temp = matrix[currentY][currentX];
            int min = temp;

            for (int i = 0; i < n; i++) {
                if (direction == 0) {
                    currentX += 1;
                    if (currentX == x2) direction = 1;
                } else if (direction == 1) {
                    currentY += 1;
                    if (currentY == y2) direction = 2;
                } else if (direction == 2) {
                    currentX -= 1;
                    if (currentX == x1) direction = 3;
                } else {
                    currentY -= 1;
                }

                int t = matrix[currentY][currentX];
                matrix[currentY][currentX] = temp;
                temp = t;
                min = Math.min(min, t);
            }
            return min;
        }
    }
}
