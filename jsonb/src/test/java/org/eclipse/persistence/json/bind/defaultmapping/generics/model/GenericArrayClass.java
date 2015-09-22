package org.eclipse.persistence.json.bind.defaultmapping.generics.model;

/**
 * @author Roman Grigoriadi
 */
public class GenericArrayClass<T, U extends T> {
    public T[] genericArray;
    public U[] anotherGenericArray;
    public GenericTestClass<T[], U[]> propagatedGenericArray;
}
