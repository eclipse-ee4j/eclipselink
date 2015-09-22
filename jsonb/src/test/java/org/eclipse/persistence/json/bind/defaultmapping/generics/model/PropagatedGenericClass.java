package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

import org.eclipse.persistence.json.bind.defaultmapping.generics.model.GenericTestClass;

import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class PropagatedGenericClass<P, X> {
    public List<GenericTestClass<List<P>, X>> genericList;
    public GenericTestClass<P, X> genericTestClass;
}
