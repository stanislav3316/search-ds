package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<T extends Comparable<T>> implements ISortedSet<T> {
    enum Color {RED, BLACK}
    private Node root = null;
    private Node NIL = new Node(null, null, null, null, Color.BLACK);
    private int size = 0;
    private final Comparator<T> comparator;

    private class Node {
        Color color;
        Node left, right, parent;
        T data;

        Node(T data, Node left, Node right, Node parent, Color color) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.color = Color.BLACK;
        }

        @Override
        public boolean equals(Object o) {
            if (!Node.class.isInstance(o)) {
                return false;
            }

            Node temp = (Node) o;
            return data == temp.data && left == temp.left &&
                    right == temp.right && parent == temp.parent &&
                    color == temp.color;
        }
    }

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public T first() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }

        if (root.left == null && root.right != null) {
            return root.right.data;
        } else if (root.left == null && root.right == null) {
            return root.data;
        }

        return curr.data;
    }

    @Override
    public T last() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }

        if (root.right == null && root.left != null) {
            return root.left.data;
        } else if (root.right == null && root.left == null) {
            return root.data;
        }

        return curr.data;
    }

    @Override
    public List<T> inorderTraverse() {
        List<T> list = new ArrayList<T>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<T> list) {
        if (curr == null) {
            return;
        }

        if (curr.left != null && !curr.left.equals(NIL)) {
            inorderTraverse(curr.left, list);
        }

        list.add(curr.data);

        if (curr.right != null && !curr.right.equals(NIL)) {
            inorderTraverse(curr.right, list);
        }
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
    public boolean contains(T value) {
        Node curr = root;

        if (value == null) {
            throw new NullPointerException("Null Value");
        }

        while (curr != null && compare(curr.data, value) != 0) {
            if (compare(curr.data, value) < 0) {
                curr = curr.right;
            } else {
                curr = curr.left;
            }
        }

        return curr != null;
    }

    @Override
    public boolean add(T value) {
        if (value == null) {
            throw new NullPointerException();
        }

        Node put = new Node(value, NIL,NIL, null, Color.BLACK);
        if (root == null) {
            root = put;
        } else {
            // contains
            Node curr = root;
            while (curr != null && !curr.equals(NIL)) {
                int num = compare(curr.data, value);
                if (num == 0) {
                    break;
                }

                if (compare(curr.data, value) < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }

            if (curr != null && !curr.equals(NIL) && compare(curr.data, value) == 0) {
                return false;
            }

            RBInsert(put);
        }

        size++;
        return true;
    }

    private void RBInsert(Node z) {
        Node y = NIL;
        Node x = root;

        while (!x.equals(NIL)) {
            y = x;
            if (compare(z.data, x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y.equals(NIL)) {
            root = z;
        } else if (compare(z.data, y.data) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.left = NIL;
        z.right = NIL;
        z.color = Color.RED;
        RBInsertFix(z);
    }

    private void RBInsertFix(Node z) {
        Node y;
        while (z.parent != null && z.parent.color == Color.RED) {
            if (z.parent.equals(z.parent.parent.left)) {
                y = z.parent.parent.right;
                if (y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                } else {
                    if (z.equals(z.parent.right)) {
                        z = z.parent;
                        leftRotate(z);
                    }

                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                y = z.parent.parent.left;
                if(y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                } else {
                    if (z.equals(z.parent.left)) {
                        z = z.parent;
                        rightRotate(z);
                    }

                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;

        if (!y.left.equals(NIL)) {
            y.left.parent = x;
        }

        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x.equals(x.parent.left)) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;

        if (!x.right.equals(NIL)) {
            x.right.parent = y;
        }

        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y.equals(y.parent.left)) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        x.right = y;
        y.parent = x;
    }

    private Node getElement(Node node, T key) {
        if (node != null) {
            if (compare(node.data, key) < 0) {
                return getElement(node.right, key);
            } else if (compare(node.data, key) > 0) {
                return getElement(node.left, key);
            } else {
                return node;
            }
        }

        return null;
    }

    private void RBDelete(Node z) {
        Node x,y;
        if (z.left.equals(NIL) || z.right.equals(NIL)) {
            y = z;
        } else {
            y = TreeSuccessor(z);
        }

        if (!y.left.equals(NIL)) {
            x = y.left;
        } else {
            x = y.right;
        }

        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y.equals(y.parent.left)) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        if (!y.equals(z)) {
            z.data = y.data;
        }

        if (y.color == Color.BLACK) {
            RBDeleteFixUp(x);
        }

        //return y;
    }

    private void RBDeleteFixUp(Node x) {
        Node w;
        while(!x.equals(root) && x.color == Color.BLACK) {
            if (x.equals(x.parent.left)) {
                w = x.parent.right;
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }

                if (w.left.color == Color.BLACK && w.right.color == Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.right.color == Color.BLACK) {
                        w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                w = x.parent.left;
                if (w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }

                if (w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.left.color == Color.BLACK) {
                        w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }

        x.color = Color.BLACK;
    }

    private Node TreeSuccessor(Node x) {
        Node y;
        if (!x.right.equals(NIL)) {
            return minimumm(x.right);
        }

        y = x.parent;
        while(!y.equals(NIL) && x.equals(y.right)) {
            x = y;
            y = y.parent;
        }

        return y;
    }

    private Node minimumm(Node x) {
        while (!x.left.equals(NIL)) {
            x = x.left;
        }

        return x;
    }

    @Override
    public boolean remove(T value) {
        if(value == null) {
            throw new NullPointerException();
        }

        Node curr = root;
        while (curr != null && !curr.equals(NIL)) {
            int num = compare(curr.data, value);
            if (num == 0) {
                break;
            }

            if (compare(curr.data, value) < 0) {
                curr = curr.right;
            } else {
                curr = curr.left;
            }
        }

        if (curr == null || curr.equals(NIL)) {
            return false;
        }

        if(size == 1) {
            root = null;
        } else {
            RBDelete(curr);
        }

        size--;
        return true;
    }

    private int compare(T v1, T v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "BST{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        System.out.println(tree.contains(10));
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

        for (int i : tree.inorderTraverse()) {
            System.out.println(i);
        }

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