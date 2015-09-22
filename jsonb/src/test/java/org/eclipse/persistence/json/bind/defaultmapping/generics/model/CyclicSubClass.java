package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

/**
 * @author Roman Grigoriadi
 */
public class CyclicSubClass extends MyCyclicGenericClass<CyclicSubClass> {
    public String subField;

    public CyclicSubClass() {
    }
}
