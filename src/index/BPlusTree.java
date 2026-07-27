package index;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * B+ Tree implementation managing tree structure operations natively across arbitrary logic layers.
 * @param <K> The type of the keys, which must be Comparable.
 * @param <V> The type of the values.
 */
public class BPlusTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private int order;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new LeafNode<>(order - 1);
    }

    public BPlusTree() {
        this(4); 
    }

    public void insert(K key, V value) {
        LeafNode<K, V> leaf = findLeaf(key);
        insertIntoLeaf(leaf, key, value);
    }

    private LeafNode<K, V> findLeaf(K key) {
        Node<K, V> curr = root;
        while (!curr.isLeaf()) {
            InternalNode<K, V> internal = (InternalNode<K, V>) curr;
            int index = Collections.binarySearch(internal.keys, key);
            if (index < 0) {
                index = -(index + 1);
            } else {
                index = index + 1;
            }
            curr = internal.getChild(index);
        }
        return (LeafNode<K, V>) curr;
    }

    private void insertIntoLeaf(LeafNode<K, V> leaf, K key, V value) {
        int index = Collections.binarySearch(leaf.keys, key);
        if (index >= 0) return; // duplicate

        int insertPos = -(index + 1);
        leaf.keys.add(insertPos, key);
        leaf.values.add(insertPos, value);

        if (leaf.keyCount() > leaf.maxKeys) {
            handleOverflow(leaf);
        }
    }

    private void handleOverflow(Node<K, V> node) {
        if (node.isLeaf()) {
            splitLeaf((LeafNode<K, V>) node);
        } else {
            splitInternal((InternalNode<K, V>) node);
        }
    }

    private void splitLeaf(LeafNode<K, V> leaf) {
        LeafNode<K, V> rightSibling = leaf.split();
        K promotedKey = rightSibling.keys.get(0);
        
        InternalNode<K, V> parent = leaf.getParent();
        if (parent == null) {
            createNewRoot(leaf, promotedKey, rightSibling);
        } else {
            int insertPos = Collections.binarySearch(parent.keys, promotedKey);
            if (insertPos < 0) insertPos = -(insertPos + 1);
            parent.insertKey(insertPos, promotedKey);
            parent.insertChild(insertPos + 1, rightSibling);
            
            if (parent.keyCount() > parent.maxKeys) {
                handleOverflow(parent);
            }
        }
    }

    private void splitInternal(InternalNode<K, V> internal) {
        K promotedKey = internal.keys.get(internal.keys.size() / 2);
        InternalNode<K, V> rightSibling = internal.split();
        
        InternalNode<K, V> parent = internal.getParent();
        if (parent == null) {
            createNewRoot(internal, promotedKey, rightSibling);
        } else {
            int insertPos = Collections.binarySearch(parent.keys, promotedKey);
            if (insertPos < 0) insertPos = -(insertPos + 1);
            parent.insertKey(insertPos, promotedKey);
            parent.insertChild(insertPos + 1, rightSibling);
            
            if (parent.keyCount() > parent.maxKeys) {
                handleOverflow(parent);
            }
        }
    }

    private void createNewRoot(Node<K, V> left, K promotedKey, Node<K, V> right) {
        InternalNode<K, V> newRoot = new InternalNode<>(order - 1);
        newRoot.insertKey(0, promotedKey);
        newRoot.insertChild(0, left);
        newRoot.insertChild(1, right);
        root = newRoot;
    }

    public V search(K key) {
        LeafNode<K, V> leaf = findLeaf(key);
        int index = Collections.binarySearch(leaf.keys, key);
        if (index >= 0) {
            return leaf.values.get(index);
        }
        return null;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public void delete(K key) {
        // Placeholder stub for Phase 6E
    }

    public int size() {
        int count = 0;
        Node<K, V> curr = root;
        while (!curr.isLeaf()) {
            curr = ((InternalNode<K, V>) curr).getChild(0);
        }
        LeafNode<K, V> leaf = (LeafNode<K, V>) curr;
        while (leaf != null) {
            count += leaf.keyCount();
            leaf = leaf.next;
        }
        return count;
    }

    public int height() {
        int h = 1;
        Node<K, V> curr = root;
        while (!curr.isLeaf()) {
            curr = ((InternalNode<K, V>) curr).getChild(0);
            h++;
        }
        return h;
    }

    public void printTree() {
        Queue<Node<K, V>> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            System.out.print("Level " + level + ": ");
            for (int i = 0; i < size; i++) {
                Node<K, V> curr = queue.poll();
                System.out.print(curr.keys + " ");
                if (!curr.isLeaf()) {
                    InternalNode<K, V> internal = (InternalNode<K, V>) curr;
                    for (int j = 0; j < internal.childCount(); j++) {
                        queue.add(internal.getChild(j));
                    }
                }
            }
            System.out.println();
            level++;
        }
    }
}
