package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;
    private int size;
    private final Comparator<E> comparator;

    private class Node {
        E value;
        int N;
        Node left, right;
        boolean color;

        Node(E value, int N, boolean color) {
            this.value = value;
            this.N = N;
            this.color = color;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }

        if (root.left == null && root.right != null) {
            return root.right.value;
        } else if (root.left == null && root.right == null) {
            return root.value;
        }

        return curr.value;
    }

    @Override
    public E last() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }

        if (root.right == null && root.left != null) {
            return root.left.value;
        } else if (root.right == null && root.left == null) {
            return root.value;
        }

        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E value) {
        Node curr = root;

        if (value == null) {
            throw new NullPointerException("Null Value");
        }

        while (curr != null && compare(curr.value, value) != 0) {
            if (compare(curr.value, value) < 0) {
                curr = curr.right;
            } else {
                curr = curr.left;
            }
        }

        return curr != null;
    }

    private boolean isRed(Node x) {
        if (x == null)
            return false;
        return x.color == RED;
    }

    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        x.N = h.N;
        h.N = 1 + size(h.left) + size(h.right);
        return x;
    }

    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        x.N = h.N;
        h.N = 1 + size(h.left) + size(h.right);
        return x;
    }

    private int size(Node x) {
        if (x == null) {
            return 0;
        }
        return x.N;
    }

    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {
        flipColors(h);
        if (!isRed(h.left.left)) {
            h = rotateRight(h);
        }
        return h;
    }

    @Override
    public boolean add(E value) {
        try {
            root = put(root, value);
            root.color = BLACK;
            size++;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Node put(Node h, E value) {
        if (h == null) {
            return new Node(value, 1, RED);
        }

        if (compare(value, h.value) < 0) {
            h.left = put(h.left, value);
        } else if (compare(value, h.value) > 0) {
            h.right = put(h.right, value);
        } else {
            h.value = value;
        }

        if (isRed(h.right) && !isRed(h.left)) {
            h = rotateLeft(h);
        }

        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }

        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }

        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    @Override
    public boolean remove(E value) {
        try {
            if (!isRed(root.left) && !isRed(root.right)) {
                root.color = RED;
            }
            root = delete(root, value);
            if (!isEmpty()) {
                root.color = BLACK;
            }
            size--;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private Node delete(Node h, E value) {
        if (compare(value, h.value) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = delete(h.left, value);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }

            if (value == h.value && h.right == null) {
                return null;
            }

            if (!isRed(h.right) && !isRed(h.right.left)) {
                h = moveRedRight(h);
            }

            if (value == h.value) {
                h.value = get(h.right, min(h.right).value);
                h.right = deleteMin(h.right);
            } else {
                h.right = delete(h.right, value);
            }
        }
        return balance(h);
    }

    private Node min(Node x) {
        if (x.left == null) {
            return x;
        } else {
            return min(x.left);
        }
    }

    private Node deleteMin(Node h) {
        if (h.left == null) {
            return null;
        }

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);
        return balance(h);
    }

    private Node balance(Node h) {
        if (isRed(h.right)) {
            h = rotateLeft(h);
        }

        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }

        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }

        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    private E get(Node x, E value) {
        while (x != null) {
            if (compare(value, x.value) < 0) {
                x = x.left;
            } else if (compare(value, x.value) > 0) {
                x = x.right;
            } else {
                return x.value;
            }
        }

        return null;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "BST{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new RedBlackTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }
}