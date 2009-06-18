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
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.metamodel;

/**
 *  Instances of the type ManagedType represent entity, mapped 
 *  superclass, and embeddable types.
 *
 *  @param <X> The represented type.
 */
public interface ManagedType<X> extends Type<X> {

    /**
     *  Return the attributes of the managed type.
     */
     java.util.Set<Attribute<? super X, ?>> getAttributes();

    /**
     *  Return the attributes declared by the managed type.
     */
     java.util.Set<Attribute<X, ?>> getDeclaredAttributes();

    /**
     *  Return the single-valued attribute of the managed 
     *  type that corresponds to the specified name and Java type 
     *  in the represented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return single-valued attribute with given name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type);

    /**
     *  Return the declared single-valued attribute of the 
     *  managed type that corresponds to the specified name and Java 
     *  type in the represented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type);
	
    /**
     *  Return the single-valued attributes of the managed type.
     *  @return single-valued attributes
     */
    java.util.Set<SingularAttribute<? super X, ?>> getSingularAttributes();

    /**
     *  Return the single-valued attributes declared by the managed
     *  type.
     *  @return declared single-valued attributes
     */
    java.util.Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes();
	
    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return CollectionAttribute of the given name and element
     *          type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */    
    <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType);

    /**
     *  Return the Set-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return SetAttribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType);

    /**
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return ListAttribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType);

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name and Java key and value
     *  types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return MapAttribute of the given name and key and value
     *  types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <K, V> MapAttribute<? super X, K, V> getMap(String name, 
                                                Class<K> keyType, 
                                                Class<V> valueType);

    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared CollectionAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType);

    /**
     *  Return the Set-valued attribute declared by the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared SetAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType);

    /**
     *  Return the List-valued attribute declared by the managed 
     *  type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared ListAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType);

    /**
     *  Return the Map-valued attribute declared by the managed 
     *  type that corresponds to the specified name and Java key 
     *  and value types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return declared MapAttribute of the given name and key 
     *          and value types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, 
                                                Class<K> keyType, 
                                                Class<V> valueType);
    
    /**
     *  Return all collection-valued attributes of the managed type.
     *  @return collection valued attributes
     */
    java.util.Set<PluralAttribute<? super X, ?, ?>> getCollections();

    /**
     *  Return all collection-valued attributes declared by the 
     *  managed type.
     *  @return declared collection valued attributes
     */
    java.util.Set<PluralAttribute<X, ?, ?>> getDeclaredCollections();

//String-based:

    /**
     *  Return the attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    Attribute<? super X, ?> getAttribute(String name); 

    /**
     *  Return the declared attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    Attribute<X, ?> getDeclaredAttribute(String name); 

    /**
     *  Return the single-valued attribute of the managed type that
     *  corresponds to the specified name in the represented type.
     *  @param name  the name of the represented attribute
     *  @return single-valued attribute with the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    SingularAttribute<? super X, ?> getSingularAttribute(String name);

    /**
     *  Return the declared single-valued attribute of the managed
     *  type that corresponds to the specified name in the
     *  represented type.
     *  @param name  the name of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    SingularAttribute<X, ?> getDeclaredSingularAttribute(String name);

    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */    
    CollectionAttribute<? super X, ?> getCollection(String name); 

    /**
     *  Return the Set-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return SetAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    SetAttribute<? super X, ?> getSet(String name);

    /**
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return ListAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    ListAttribute<? super X, ?> getList(String name);

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return MapAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    MapAttribute<? super X, ?, ?> getMap(String name); 

    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    CollectionAttribute<X, ?> getDeclaredCollection(String name); 

    /**
     *  Return the Set-valued attribute declared by the managed type 
     *  that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared SetAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    SetAttribute<X, ?> getDeclaredSet(String name);

    /**
     *  Return the List-valued attribute declared by the managed 
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared ListAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    ListAttribute<X, ?> getDeclaredList(String name);

    /**
     *  Return the Map-valued attribute declared by the managed 
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared MapAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    MapAttribute<X, ?, ?> getDeclaredMap(String name);
}