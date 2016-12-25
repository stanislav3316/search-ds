package ru.mail.polis;

import com.sun.org.apache.regexp.internal.RE;
import sun.reflect.generics.tree.Tree;

/**
 * Created by iters on 12/25/16.
 */
public class REDBLACK<T extends Comparable<T>> {

    enum Color {RED, BLACK}
    private Node root = null;
    private Node NIL = new Node(null, null, null, null, Color.BLACK);

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

    private void RBInsert(Node z) {
        Node y = NIL;
        Node x = root;

        while (!x.equals(NIL)) {
            y = x;
            if (z.data.compareTo(x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.parent = y;
        if (y.equals(NIL)) {
            root = z;
        } else if (z.data.compareTo(y.data) < 0) {
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
        if (y.parent.equals(NIL)) {
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


    public void add(T value) {
        if(value==null){
            throw new NullPointerException();
        }

        if(root==null){
            root = new Node(value, NIL,NIL, null, Color.BLACK);
        }
        else {
            RBInsert(new Node(value, null, null, null, Color.BLACK));
        }
    }

    private Node findNode(Node node, T key){
        if(node==null || node.equals(NIL)){
            return null;
        }
        if(node.data.compareTo(key)==0){
            return node;
        }
        if(node.data.compareTo(key) > 0){ //node.key>key
            return findNode(node.left,key);
        }
        else{
            return findNode(node.right, key);
        }
    }

    public boolean remove(T value) {
        if(value==null){
            throw new NullPointerException();
        }
        Node r = findNode(root,value);
        if(r==null){
            return false;
        }
        else{
            if(r == root){
                root = null;
            }
            else {
                RBDelete(r);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        REDBLACK<Integer> tree = new REDBLACK<>();
        tree.add(10);
        tree.add(20);
        tree.add(30);
        tree.add(40);
        tree.add(50);

        tree.remove(30);
        tree.remove(20);
        tree.remove(10);

        System.out.println();
    }

}
