package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.ArrayDeque;
import java.util.Deque;

public class D11_HideCactus {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TestRunner.run("[프로그래머스] 선인장 숨기기",
                inputs -> solution.solution(
                        (int) inputs[0],
                        (int) inputs[1],
                        (int) inputs[2],
                        (int) inputs[3],
                        (int[][]) inputs[4]
                ),
                new TestCase<>(new Object[]{4, 5, 2, 2, new int[][]{{0, 0}, {3, 1}, {1, 3}, {2, 4}, {1, 1}, {2, 2}, {2, 3}, {0, 4}}}, new int[]{2, 2}),
                new TestCase<>(new Object[]{3, 3, 1, 1, new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}}}, new int[]{1, 1}),
                new TestCase<>(new Object[]{4, 6, 3, 4, new int[][]{{1, 2}}}, new int[]{0, 0}),
                new TestCase<>(new Object[]{4, 6, 1, 2, new int[][]{{0, 1}, {0, 3}, {0, 5}, {1, 1}, {1, 3}, {1, 5}, {2, 1}, {2, 3}, {2, 5}, {3, 1}, {3, 3}, {3, 5}}}, new int[]{3, 4}),
                new TestCase<>(new Object[]{2, 2, 2, 2, new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 0}}}, new int[]{0, 0}),
                new TestCase<>(new Object[]{4, 4, 3, 1, new int[][]{{2, 0}, {1, 3}, {3, 2}, {0, 1}}}, new int[]{0, 2})
        );
    }

    private static class Solution {
        public int[] solution(int m, int n, int h, int w, int[][] drops) {
            long[][] map = new long[m][n];
            fillMaxValue(map);
            for (int i = 0; i < drops.length; i++) {
                map[drops[i][0]][drops[i][1]] = i + 1;
            }
            System.out.println("");
            print2DimArr(map);

            // 가로 방향으로 슬라이딩 윈도우
            long[][] rowMaxMap = new long[m - h + 1][n - w + 1];
            fillMaxValue(rowMaxMap);
            Deque<Integer> deque = new ArrayDeque<>();

            int i = 0;
            for (int j = 0; j < w; j++) {
                while (!deque.isEmpty() && map[i][deque.peekLast()] > map[i][j]) {
                    deque.pollLast();
                }
                deque.offerLast(j);
                System.out.println("deque: " + deque);
            }
            rowMaxMap[i][0] = map[i][deque.peekFirst()];

            for (int j = 1; j < n - w + 1; j++) {
                if (!deque.isEmpty() && deque.peekFirst() == j - 1) {
                    deque.pollFirst();
                }

                int newIndex = j + w - 1;
                while (
                        !deque.isEmpty() &&
                                map[i][deque.peekLast()] > map[i][newIndex]
                ) {
                    deque.pollLast();
                }
                deque.offerLast(newIndex);

                System.out.println("deque: " + deque);

                rowMaxMap[i][j] = map[i][deque.peekFirst()];
            }

            System.out.println(" - rowMaxMap");
            print2DimArr(rowMaxMap);


            return new int[]{1, 1};
        }

        private void print2DimArr(long[][] arr) {
            for (long[] row : arr) {
                for (long i : row) {
                    System.out.print((i > 1000000 ? "INF" : i) + ", ");
                }
                System.out.println();
            }
        }

        private void fillMaxValue(long[][] arr) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = Long.MAX_VALUE;
                }
            }
        }
    }


}
