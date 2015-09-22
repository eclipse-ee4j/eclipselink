package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

/**
 * @author Roman Grigoriadi
 */
public class MyCyclicGenericClass<T extends MyCyclicGenericClass<? extends T>> {
    public T field1;

    public MyCyclicGenericClass() {
    }
}
