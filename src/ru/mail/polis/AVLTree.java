package ru.mail.polis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    private Node root;
    private int size = 0;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.leftChild != null) {
            curr = curr.leftChild;
        }

        if (root.leftChild == null && root.rightChild != null) {
            return root.rightChild.data;
        } else if (root.leftChild == null && root.rightChild == null) {
            return root.data;
        }

        return curr.data;
    }

    @Override
    public E last() {
        if (root == null) {
            return null;
        }

        Node curr = root;
        while (curr.rightChild != null) {
            curr = curr.rightChild;
        }

        if (root.rightChild == null && root.leftChild != null) {
            return root.leftChild.data;
        } else if (root.rightChild == null && root.leftChild == null) {
            return root.data;
        }

        return curr.data;
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
        inorderTraverse(curr.leftChild, list);
        list.add(curr.data);
        inorderTraverse(curr.rightChild, list);
    }

    @Override
    public int size() {
        return size();
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

        while (curr != null && compare(curr.data, value) != 0) {
            if (compare(curr.data, value) < 0) {
                curr = curr.rightChild;
            } else {
                curr = curr.leftChild;
            }
        }

        return curr != null;
    }

    @Override
    public boolean add(E data) {
        try {
            add(data, root);
            size++;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void add(E data, Node currentNode) {
        if (root == null) {
            root = new Node(data, null, null, null);
            return;
        }

        if (compare(data, currentNode.data) < 0) {
            if (currentNode.leftChild != null) {
                add(data, currentNode.leftChild);
            } else {
                currentNode.leftChild = new Node(data, null, null, currentNode);
                updateBalance(currentNode.leftChild);
            }


        } else {

            if (currentNode.rightChild != null) {
                add(data, currentNode.rightChild);
            } else {
                currentNode.rightChild = new Node(data, null, null, currentNode);
                updateBalance(currentNode.rightChild);
            }

        }
    }

    private void updateBalance(Node node) {
        if (node.balanceFactor > 1 || node.balanceFactor < -1) {
            rebalance(node);
            return;
        }

        if (node.parent != null) {
            if (node.parent.leftChild == node) {
                node.parent.balanceFactor += 1;
            } else if (node.parent.rightChild == node) {
                node.parent.balanceFactor -= 1;
            }

            if (node.parent.balanceFactor != 0) {
                updateBalance(node.parent);
            }
        }
    }

    private void rotateLeft(Node rotRoot) {
        Node newRoot = rotRoot.rightChild;
        rotRoot.rightChild = newRoot.leftChild;

        if (newRoot.leftChild != null) {
            newRoot.leftChild.parent = rotRoot;
        }
        newRoot.parent = rotRoot.parent;

        if (rotRoot == root) {
            root = newRoot;
        } else {
            if (rotRoot.parent.leftChild == rotRoot) {
                rotRoot.parent.leftChild = newRoot;
            } else {
                rotRoot.parent.rightChild = newRoot;
            }
        }

        newRoot.leftChild = rotRoot;
        rotRoot.parent = newRoot;
        rotRoot.balanceFactor = rotRoot.balanceFactor + 1 - Math.min(newRoot.balanceFactor, 0);
        newRoot.balanceFactor = newRoot.balanceFactor + 1 + Math.max(rotRoot.balanceFactor, 0);
    }

    private void rotateRight(Node rotRoot) {
        Node newRoot = rotRoot.leftChild;
        rotRoot.leftChild = newRoot.rightChild;

        if (newRoot.rightChild != null) {
            newRoot.rightChild.parent = rotRoot;
        }
        newRoot.parent = rotRoot.parent;

        if (rotRoot == root) {
            root = newRoot;
        } else {
            if (rotRoot.parent.rightChild == rotRoot) {
                rotRoot.parent.rightChild = newRoot;
            } else {
                rotRoot.parent.leftChild = newRoot;
            }
        }

        newRoot.rightChild = rotRoot;
        rotRoot.parent = newRoot;
        rotRoot.balanceFactor = rotRoot.balanceFactor - 1 - Math.max(newRoot.balanceFactor, 0);
        newRoot.balanceFactor = newRoot.balanceFactor - 1 + Math.min(rotRoot.balanceFactor, 0);
    }

    private void rebalance(Node node) {
        if (node.balanceFactor < 0) {

            if (node.rightChild.balanceFactor > 0) {
                rotateRight(node.rightChild);
                rotateLeft(node);
            } else {
                rotateLeft(node);
            }

        } else if (node.balanceFactor > 0) {

            if (node.leftChild.balanceFactor < 0) {
                rotateLeft(node.leftChild);
                rotateRight(node);
            } else {
                rotateRight(node);
            }
        }
    }

    public Node getElement(E key) {
        return getElement(root, key);
    }

    private Node getElement(Node node, E key) {
        if (node != null) {
            if (compare(node.data, key) < 0) {
                return getElement(node.rightChild, key);
            } else if (compare(node.data, key) > 0) {
                return getElement(node.leftChild, key);
            } else {
                return node;
            }
        }

        return null;
    }

    private Node findMostAvailableNode(Node curr) {
        if (curr.rightChild != null) {
            curr = curr.rightChild;
            while (curr.leftChild != null) {
                curr = curr.leftChild;
            }
            return curr;
        } else if (curr.leftChild != null) {
            return curr.leftChild;
        }

        return curr;
    }

    private void swapNodesAndDelete(Node a, Node b) {
        if (b == root) {
            root = null;
            return;
        }

        a.data = b.data;

        if (b.leftChild != null) {
            b.leftChild.parent = b.parent;
            if (b.parent.leftChild == b) {
                b.parent.leftChild = b.leftChild;
                b.parent.balanceFactor -= 1;
            } else if (b.parent.rightChild == b) {
                b.parent.rightChild = b.leftChild;
                b.parent.balanceFactor += 1;
            }
            updateBalance(b.leftChild);

        } else if (b.rightChild != null) {
            b.rightChild.parent = b.parent;
            if (b.parent.leftChild == b) {
                b.parent.leftChild = b.rightChild;
                b.parent.balanceFactor -= 1;
            } else if (b.parent.rightChild == b) {
                b.parent.rightChild = b.rightChild;
                b.parent.balanceFactor += 1;
            }
            updateBalance(b.rightChild);

        } else {
            if (b.parent.leftChild == b) {
                b.parent.balanceFactor -= 1;
                b.parent.leftChild = null;
            } else if (b.parent.rightChild == b) {
                b.parent.balanceFactor += 1;
                b.parent.rightChild = null;
            }
            updateBalance(b.parent);
        }
    }

    @Override
    public boolean remove(E value) {
        try {
            Node removeable = getElement(value);
            Node chager = findMostAvailableNode(removeable);
            swapNodesAndDelete(removeable, chager);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    class Node {
        E data;
        Node leftChild, rightChild, parent;
        int balanceFactor;

        Node(E data, Node leftChild, Node rightChild, Node parent) {
            this.data = data;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.parent = parent;
            balanceFactor = 0;
        }
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);
        tree.add(20);
        tree.add(30);
        tree.add(40);
        tree.add(50);

        System.out.println(tree.first());
        System.out.println(tree.last());

        ArrayList<Integer> list = (ArrayList<Integer>) tree.inorderTraverse();
        for (int i : list) {
            System.out.println(i);
        }

        System.out.println(tree.contains(30));
        System.out.println(tree.contains(10));
        System.out.println(tree.contains(50));
        System.out.println(tree.contains(70));
        System.out.println(tree.contains(0));

        tree.remove(50);
        tree.remove(10);
        System.out.println();

        list = (ArrayList<Integer>) tree.inorderTraverse();
        for (int i : list) {
            System.out.println(i);
        }

        tree.add(1000);
    }
}