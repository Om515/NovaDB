package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Routing node in the B+ Tree that maps keys to subsequent child nodes.
 * @param <K> The type of the keys, which must be Comparable.
 * @param <V> The type of the values.
 */
public class InternalNode<K extends Comparable<K>, V> extends Node<K, V> {
    
    protected List<Node<K, V>> children;

    public InternalNode(int maxKeys) {
        super(maxKeys);
        this.children = new ArrayList<>(maxKeys + 2);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public void insertKey(int index, K key) {
        keys.add(index, key);
    }

    public void insertChild(int index, Node<K, V> child) {
        children.add(index, child);
        child.setParent(this);
    }

    public void removeKey(int index) {
        keys.remove(index);
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public Node<K, V> getChild(int index) {
        return children.get(index);
    }

    public int childCount() {
        return children.size();
    }

    /**
     * Splits this internal node in half, safely dislodging the median key permanently for parental promotion.
     */
    public InternalNode<K, V> split() {
        InternalNode<K, V> sibling = new InternalNode<>(maxKeys);
        int mid = keys.size() / 2;
        int keysSize = keys.size();
        
        for (int i = mid + 1; i < keysSize; i++) {
            sibling.keys.add(keys.get(i));
        }
        
        for (int i = mid + 1; i <= keysSize; i++) {
            Node<K, V> child = children.get(i);
            sibling.children.add(child);
            child.setParent(sibling);
        }
        
        keys.subList(mid, keysSize).clear();
        children.subList(mid + 1, keysSize + 1).clear();
        
        return sibling;
    }
}
