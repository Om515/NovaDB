package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract generic node for the B+ Tree hierarchy.
 * @param <K> The type of the keys, which must be Comparable.
 * @param <V> The type of the values.
 */
public abstract class Node<K extends Comparable<K>, V> {
    protected List<K> keys;
    protected int maxKeys;
    protected InternalNode<K, V> parent;

    public Node(int maxKeys) {
        this.maxKeys = maxKeys;
        this.keys = new ArrayList<>(maxKeys + 1);
    }

    public abstract boolean isLeaf();

    public int keyCount() {
        return keys.size();
    }

    public boolean isFull() {
        return keys.size() >= maxKeys;
    }

    public InternalNode<K, V> getParent() {
        return parent;
    }

    public void setParent(InternalNode<K, V> parent) {
        this.parent = parent;
    }
}
