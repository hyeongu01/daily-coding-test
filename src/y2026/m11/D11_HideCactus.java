package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * [프로그래머스 - 선인장 숨기기]
 * <p>
 * https://school.programmers.co.kr/learn/courses/30/lessons/468379
 * <p>
 * 키워드: 슬라이딩 윈도우, 모노토틱 덱 (최대/최소값 슬라이딩 윈도우)
 */
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
            // 1. rainMap 작성
            long[][] rainMap = new long[m][n];
            fill(rainMap, Long.MAX_VALUE);

            for (int i = 0; i < drops.length; i++) {
                rainMap[drops[i][0]][drops[i][1]] = i + 1;
            }

            // 2. 왼쪽으로 슬라이딩 윈도우
            long[][] lineMinMap = new long[m][n - w + 1];
            fill(lineMinMap, Long.MAX_VALUE);

            for (int i = 0; i < m; i++) {
                Deque<Integer> deque = new ArrayDeque<>(); // 모노로틱 큐 (내림차순)
                for (int j = 0; j < w; j++) { // 최초 슬라이딩 윈도우 채우기
                    while (!deque.isEmpty() && rainMap[i][deque.peekLast()] > rainMap[i][j]) {
                        deque.pollLast();
                    }
                    deque.offerLast(j);
                }
                lineMinMap[i][0] = rainMap[i][deque.peekFirst()];

                for (int j = 1; j < n - w + 1; j++) {
                    if (!deque.isEmpty() && deque.peekFirst() == j - 1) {
                        deque.pollFirst();
                    }
                    while (!deque.isEmpty() && rainMap[i][deque.peekLast()] > rainMap[i][j + w - 1]) {
                        deque.pollLast();
                    }
                    deque.offerLast(j + w - 1);

                    lineMinMap[i][j] = rainMap[i][deque.peekFirst()];
                }
            }
            printArr(lineMinMap, "lineMinMap");

            // 3. 세로로 슬라이딩 윈도우
            long[][] minMap = new long[m - h + 1][n - w + 1];
            for (int j = 0; j < n - w + 1; j++) {
                Deque<Integer> deque = new ArrayDeque<>();
                for (int i = 0; i < h; i++) {
                    while (!deque.isEmpty() && lineMinMap[deque.peekLast()][j] > lineMinMap[i][j]) {
                        deque.pollLast();
                    }
                    deque.offerLast(i);
                }
                minMap[0][j] = lineMinMap[deque.peekFirst()][j];

                for (int i = 1; i < m - h + 1; i++) {
                    if (!deque.isEmpty() && deque.peekFirst() == i - 1) {
                        deque.pollFirst();
                    }
                    while (!deque.isEmpty() && lineMinMap[deque.peekLast()][j] > lineMinMap[i + h - 1][j]) {
                        deque.pollLast();
                    }
                    deque.offerLast(i + h - 1);

                    minMap[i][j] = lineMinMap[deque.peekFirst()][j];
                }

            }

            printArr(minMap, "minMap");

            // 4. 결과 계산
            int[] result = {0, 0};
            long max = minMap[0][0];
            for (int i = 0; i < minMap.length; i++) {
                for (int j = 0; j < minMap[0].length; j++) {
                    if (max < minMap[i][j]) {
                        result[0] = i;
                        result[1] = j;
                        max = minMap[i][j];
                    }
                }
            }

            return result;
        }

        private void printArr(long[][] arr, String name) {
            System.out.println("-- " + name + " --");
            for (long[] ar : arr) {
                for (long a : ar) {
                    System.out.print((a == Long.MAX_VALUE ? "-" : a) + ", ");
                }
                System.out.println();
            }
        }

        private void fill(long[][] arr, long value) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    arr[i][j] = value;
                }
            }
        }
    }


}
