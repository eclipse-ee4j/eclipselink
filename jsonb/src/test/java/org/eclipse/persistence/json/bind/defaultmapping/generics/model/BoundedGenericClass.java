package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

import java.util.List;
import java.util.Set;

/**
 * @author Roman Grigoriadi
 */
public class BoundedGenericClass<T extends Set<? extends Number>, U> {
    public List<? extends U> upperBoundedList;
    public List<? super U> lowerBoundedList;
    public T boundedSet;

    public BoundedGenericClass() {
    }
}
