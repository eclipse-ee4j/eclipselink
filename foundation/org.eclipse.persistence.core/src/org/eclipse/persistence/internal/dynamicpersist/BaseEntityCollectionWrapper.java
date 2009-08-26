package org.eclipse.persistence.internal.dynamicpersist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BaseEntityCollectionWrapper extends BaseEntity implements Collection<Object> {

    public static int NUM_ATTRIBUTES = 1;
    public static void setNumAttributes(Integer numAttributes) {
        // number of attributes can't be changed, ignore
    }
    public static int getNumAttributes() {
        return NUM_ATTRIBUTES;
    }
    
    public BaseEntityCollectionWrapper() {
        super();
        fields = new Object[getNumAttributes()];
        // use ArrayList as the Collection type
        // TODO - support some sort of CollectionPolicy for different Collection types  
        fields[0] = new ArrayList<Object>(); 
    }

    public boolean add(Object e) {
        return ((Collection)fields[0]).add(e);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return ((Collection)fields[0]).addAll(c);
    }

    public void clear() {
        ((Collection)fields[0]).clear();
    }

    public boolean contains(Object o) {
        return ((Collection)fields[0]).contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return ((Collection)fields[0]).containsAll(c);
    }

    public boolean isEmpty() {
        return ((Collection)fields[0]).isEmpty();
    }

    public Iterator<Object> iterator() {
        return ((Collection)fields[0]).iterator();
    }

    public boolean remove(Object o) {
        return ((Collection)fields[0]).remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return ((Collection)fields[0]).removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return ((Collection)fields[0]).retainAll(c);
    }

    public int size() {
        return ((Collection)fields[0]).size();
    }

    public Object[] toArray() {
        return ((Collection)fields[0]).toArray();
    }

    public <T> T[] toArray(T[] a) {
        return (T[])((Collection)fields[0]).toArray(a);
    }
}