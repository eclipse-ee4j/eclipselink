package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class WildcardMultipleBoundsClass<T extends Number & Serializable & Comparable<? extends T>> {

    public T wildcardField;

    public GenericTestClass<String, T> genericTestClassPropagatedWildCard;

    public List<? extends T> propagatedWildcardList;
}
