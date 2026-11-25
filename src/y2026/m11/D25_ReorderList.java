package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * https://leetcode.com/problems/reorder-list
 * <p>
 * 키워드: Linked List, Two Pointers, Deque
 * <p>
 * 풀이 평가 (2026-11-25, 풀이 25분)
 * - 총평: 예시 2/2 통과, 길이 1·2·3 경계도 정상. 노드 자체를 Deque 에
 *   담아 앞/뒤를 번갈아 꺼낸 발상이 간결하고, 마지막 next = null 로
 *   사이클을 끊은 처리가 정확함.
 * - 복잡도: 시간 O(N) / 공간 O(N) — 노드 N-1개를 Deque 에 보관.
 * - 개선: void 메서드 끝의 return; 은 불필요. n = deque.size() + 1 후
 *   1부터 도는 대신 while (!deque.isEmpty()) + boolean 토글이면 더 명확.
 * - 다음에 시도: 추가 공간 O(1) 풀이 — 가운데 찾기 → 뒤쪽 절반 뒤집기
 *   → 두 리스트 번갈아 합치기. 비슷한 패턴: Palindrome Linked List(234).
 */
class D25_ReorderList {

    public static void main(String[] args) {
        D25_ReorderList solution = new D25_ReorderList();

        TestRunner.run("Reorder List",
                (int[] arr) -> {
                    ListNode head = ListNode.from(arr);
                    solution.reorderList(head);
                    return ListNode.toArray(head);
                },
                new TestCase<>(new int[]{1, 2, 3, 4}, new int[]{1, 4, 2, 3}),
                new TestCase<>(new int[]{1, 2, 3, 4, 5}, new int[]{1, 5, 2, 4, 3})
        );
    }

    public void reorderList(ListNode head) {
        Deque<ListNode> deque = new ArrayDeque<>();

        ListNode currentNode = head.next;
        while (currentNode != null) {
            deque.addLast(currentNode);
            currentNode = currentNode.next;
        }

        // 첫 값은 이미 잘 채워짐
        currentNode = head;
        int n = deque.size() + 1;
        for (int i = 1; i < n; i++) {
            currentNode.next = i % 2 == 0 ? deque.pollFirst() : deque.pollLast();
            currentNode = currentNode.next;
        }
        currentNode.next = null;
        return;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        static ListNode from(int[] arr) {
            if (arr.length == 0) return null;
            ListNode head = new ListNode(arr[0]);
            ListNode current = head;
            for (int i = 1; i < arr.length; i++) {
                current.next = new ListNode(arr[i]);
                current = current.next;
            }
            return head;
        }

        static int[] toArray(ListNode head) {
            int size = 0;
            for (ListNode c = head; c != null; c = c.next) size++;
            int[] result = new int[size];
            int i = 0;
            for (ListNode c = head; c != null; c = c.next) result[i++] = c.val;
            return result;
        }
    }
}
