package y2026.m11;

import utils.TestCase;
import utils.TestRunner;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * https://leetcode.com/problems/count-good-nodes-in-binary-tree
 * 키워드: tree, recursion
 */
public class D18_CountGoodNodesInBinaryTree {

    public static void main(String[] args) {
        Solution solution = new Solution();

        TreeNode testInput1 = TreeNode.fromArray(new Integer[]{3, 1, 4, 3, null, 1, 5});
        TreeNode testInput2 = TreeNode.fromArray(new Integer[]{3, 3, null, 4, 2});
        TreeNode testInput3 = TreeNode.fromArray(new Integer[]{1});

        TestRunner.run("Count Good Nodes in Binary Tree",
                solution::goodNodes,
                new TestCase<>(testInput1, 4),
                new TestCase<>(testInput2, 3),
                new TestCase<>(testInput3, 1)
        );
    }

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            append(sb, "", "", this);
            return sb.toString().stripTrailing();
        }

        private static void append(StringBuilder sb, String prefix, String childPrefix, TreeNode node) {
            sb.append(prefix).append(node == null ? "null" : String.valueOf(node.val)).append('\n');
            if (node == null || (node.left == null && node.right == null)) {
                return;
            }
            append(sb, childPrefix + "├── ", childPrefix + "│   ", node.left);
            append(sb, childPrefix + "└── ", childPrefix + "    ", node.right);
        }

        static TreeNode fromArray(Integer[] arr) {
            return fromArray(arr, 0);
        }

        static TreeNode fromArray(Integer[] arr, int index) {
            TreeNode root = new TreeNode(arr[index]);
            int leftIndex = index * 2 + 1;
            int rightIndex = index * 2 + 2;
            if (leftIndex < arr.length && arr[leftIndex] != null) {
                root.left = fromArray(arr, leftIndex);
            }
            if (rightIndex < arr.length && arr[rightIndex] != null) {
                root.right = fromArray(arr, rightIndex);
            }
            return root;
        }
    }

    static class Solution {
        public int goodNodes(TreeNode root) {
            int result = 1; // root 는 항상 good
            Deque<TreeNode> stack = new ArrayDeque<>();
            stack.addLast(root);

            while (!stack.isEmpty()) {
                TreeNode current = stack.pollLast();

                if (current.left != null) {
                    result += current.val <= current.left.val ? 1 : 0;
                    current.left.val = Math.max(current.val, current.left.val);
                    stack.addLast(current.left);
                }
                if (current.right != null) {
                    result += current.val <= current.right.val ? 1 : 0;
                    current.right.val = Math.max(current.val, current.right.val);
                    stack.addLast(current.right);
                }
            }
            return result;
        }
    }

    static class SolutionByRecursiveFunc {
        public int goodNodes(TreeNode root) {
            return dfs(root, Integer.MIN_VALUE);
        }

        public int dfs(TreeNode root, int maxValue) {
            if (root == null) return 0;
            int newMax = Math.max(root.val, maxValue);
            return (root.val >= maxValue ? 1 : 0) + dfs(root.left, newMax) + dfs(root.right, newMax);
        }
    }
}
