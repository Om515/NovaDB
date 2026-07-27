package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Leaf node in the B+ Tree that stores the actual data keys and records.
 * @param <K> The type of the keys, which must be Comparable.
 * @param <V> The type of the values.
 */
public class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
    protected List<V> values;
    protected LeafNode<K, V> next;

    public LeafNode(int maxKeys) {
        super(maxKeys);
        this.values = new ArrayList<>(maxKeys + 1);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
    
    /**
     * Splits this leaf node in half across keys and values, returning the newly created right sibling leaf.
     */
    public LeafNode<K, V> split() {
        LeafNode<K, V> sibling = new LeafNode<>(maxKeys);
        
        int mid = keys.size() / 2;
        int size = keys.size();
        
        for (int i = mid; i < size; i++) {
            sibling.keys.add(this.keys.get(i));
            sibling.values.add(this.values.get(i));
        }
        
        this.keys.subList(mid, size).clear();
        this.values.subList(mid, size).clear();
        
        sibling.next = this.next;
        this.next = sibling;
        
        return sibling;
    }
}
