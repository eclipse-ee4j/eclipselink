/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.metamodel;

/**
 * Instances of the type ManagedType represent entity, mapped superclass, and
 * embeddable types.
 * 
 * @param <X>
 *            The represented type.
 */
public interface ManagedType<X> extends Type<X>, Bindable<X> {
    /**
     * Return the non-collection-valued attribute of the managed type that
     * corresponds to the specified name and Java type in the represented type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param type
     *            the type of the represented attribute
     * @return non-collection attribute with given name and type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <Y> Attribute<? super X, Y> getAttribute(String name, Class<Y> type);

    /**
     * Return the declared non-collection-valued attribute of the managed type
     * that corresponds to the specified name and Java type in the represented
     * type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param type
     *            the type of the represented attribute
     * @return declared non-collection attribute of the given name and type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <Y> Attribute<X, Y> getDeclaredAttribute(String name, Class<Y> type);

    /**
     * Return the non-collection-valued attributes of the managed type.
     * 
     * @return non-collection attributes
     */
    java.util.Set<Attribute<? super X, ?>> getAttributes();

    /**
     * Return the non-collection-valued attributes declared by the managed type.
     * 
     * @return declared non-collection attributes
     */
    java.util.Set<Attribute<X, ?>> getDeclaredAttributes();

    /**
     * Return the Collection-valued attribute of the managed type that
     * corresponds to the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return Collection attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> Collection<? super X, E> getCollection(String name, Class<E> elementType);

    /**
     * Return the Set-valued attribute of the managed type that corresponds to
     * the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return Set attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> Set<? super X, E> getSet(String name, Class<E> elementType);

    /**
     * Return the List-valued attribute of the managed type that corresponds to
     * the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return List attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> List<? super X, E> getList(String name, Class<E> elementType);

    /**
     * Return the Map-valued attribute of the managed type that corresponds to
     * the specified name and Java key and value types.
     * 
     * @param name
     *            the name of the represented attribute
     * @param keyType
     *            the key type of the represented attribute
     * @param valueType
     *            the value type of the represented attribute
     * @return Map attribute of the given name and key and value types
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <K, V> Map<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType);

    /**
     * Return the Collection-valued attribute declared by the managed type that
     * corresponds to the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return declared Collection attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> Collection<X, E> getDeclaredCollection(String name, Class<E> elementType);

    /**
     * Return the Set-valued attribute declared by the managed type that
     * corresponds to the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return declared Set attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> Set<X, E> getDeclaredSet(String name, Class<E> elementType);

    /**
     * Return the List-valued attribute declared by the managed type that
     * corresponds to the specified name and Java element type.
     * 
     * @param name
     *            the name of the represented attribute
     * @param elementType
     *            the element type of the represented attribute
     * @return declared List attribute of the given name and element type
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <E> List<X, E> getDeclaredList(String name, Class<E> elementType);

    /**
     * Return the Map-valued attribute declared by the managed type that
     * corresponds to the specified name and Java key and value types.
     * 
     * @param name
     *            the name of the represented attribute
     * @param keyType
     *            the key type of the represented attribute
     * @param valueType
     *            the value type of the represented attribute
     * @return declared Map attribute of the given name and key and value types
     * @throws IllegalArgumentException
     *             if attribute of the given name and type is not present in the
     *             managed type
     */
    <K, V> Map<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType);

    /**
     * Return all collection-valued attributes of the managed type.
     * 
     * @return collection valued attributes
     */
    java.util.Set<AbstractCollection<? super X, ?, ?>> getCollections();

    /**
     * Return all collection-valued attributes declared by the managed type.
     * 
     * @return declared collection valued attributes
     */
    java.util.Set<AbstractCollection<X, ?, ?>> getDeclaredCollections();

    // String-based:
    /**
     * Return the non-collection-valued attribute of the managed type that
     * corresponds to the specified name in the represented type.
     * 
     * @param name
     *            the name of the represented attribute
     * @return non-collection attribute with the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Attribute<? super X, ?> getAttribute(String name);

    /**
     * Return the declared non-collection-valued attribute of the managed type
     * that corresponds to the specified name in the represented type.
     * 
     * @param name
     *            the name of the represented attribute
     * @return declared non-collection attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Attribute<X, ?> getDeclaredAttribute(String name);

    /**
     * Return the Collection-valued attribute of the managed type that
     * corresponds to the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return Collection attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Collection<? super X, ?> getCollection(String name);

    /**
     * Return the Set-valued attribute of the managed type that corresponds to
     * the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return Set attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Set<? super X, ?> getSet(String name);

    /**
     * Return the List-valued attribute of the managed type that corresponds to
     * the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return List attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    List<? super X, ?> getList(String name);

    /**
     * Return the Map-valued attribute of the managed type that corresponds to
     * the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return Map attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Map<? super X, ?, ?> getMap(String name);

    /**
     * Return the Collection-valued attribute declared by the managed type that
     * corresponds to the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return declared Collection attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Collection<X, ?> getDeclaredCollection(String name);

    /**
     * Return the Set-valued attribute declared by the managed type that
     * corresponds to the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return declared Set attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Set<X, ?> getDeclaredSet(String name);

    /**
     * Return the List-valued attribute declared by the managed type that
     * corresponds to the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return declared List attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    List<X, ?> getDeclaredList(String name);

    /**
     * Return the Map-valued attribute declared by the managed type that
     * corresponds to the specified name.
     * 
     * @param name
     *            the name of the represented attribute
     * @return declared Map attribute of the given name
     * @throws IllegalArgumentException
     *             if attribute of the given name is not present in the managed
     *             type
     */
    Map<X, ?, ?> getDeclaredMap(String name);
}