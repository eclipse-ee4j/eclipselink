/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Marcel Valovy - major speed up, major refurbishing.
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class is leveraged by reference mappings. It plays 3 roles:
 * <ul>
 * <li>Stores objects with an ID</li>
 * <li>Stores key based relationships</li>
 * <li>Resolves key based relationships based on the objects stored by ID</li>
 * </ul>
 */
public final class ReferenceResolver {

    /**
     * This field is in fact used locally only. We're just saving resources by reusing one instance of the object.
     */
    private final ReferenceKey refKey;
    /**
     * Stores References.
     */
    private LinkedHashMap<ReferenceKey, Reference> referencesMap;
    /**
     * Stores References that have been kicked out of the {@link #referencesMap} because a Reference with same key
     * was added to it.
     */
    private List<Reference> unluckyReferences;
    /**
     * Stores positions of References that have been kicked out of the {@link #referencesMap}. The position is
     * the same this Reference would have if this code would run on a list instead of map.
     */
    private LinkedList<Integer> unluckyRefPositions;
    /**
     * Speed-up cache that was introduced in 2.5 instead of the previous speed-up mechanisms using session cache.
     */
    private Map<Class, Map<Object, Object>> cache;

    /**
     * The default constructor initializes the list of References.
     */
    public ReferenceResolver() {
        referencesMap = new LinkedHashMap<ReferenceKey, Reference>();
        unluckyReferences = new ArrayList<Reference>();
        unluckyRefPositions = new LinkedList<Integer>();
        cache = new HashMap<Class, Map<Object, Object>>();
        refKey = new ReferenceKey();
    }

    /**
     * Add a Reference object to the list - these References will
     * be resolved after unmarshalling is complete.
     */
    public final void addReference(final Reference ref) {
        final ReferenceKey key = new ReferenceKey(ref);
        /* If an entry with equal key is already present in map, we preserve the original entry and
         * note the position of the new entry into the list of positions and put the new misfit value
         * into the list of unluckyReferences. */
        final Reference previous = referencesMap.get(key);
        if (previous != null || ref.getSourceObject() instanceof Collection) {
            // The input integer represents the position (starting from 0) of the new element that didn't fit into the map.
            unluckyRefPositions.add(referencesMap.size() + unluckyReferences.size());
            unluckyReferences.add(ref);
        } else {
            referencesMap.put(key, ref);
        }

    }

    /**
     * Retrieve the reference for a given mapping instance.
     */
    public final Reference getReference(final ObjectReferenceMapping mapping, final Object sourceObject) {
        refKey.setMapping(mapping);
        refKey.setSourceObject(sourceObject);
        final Reference reference = referencesMap.get(refKey);

        if (reference != null) {
            return reference;
        }

        // Search for unlucky references that were kicked out of hashMap by entries with equal key.
        for (Reference reference1 : unluckyReferences) {
            if (reference1.getMapping() == mapping && reference1.getSourceObject() == sourceObject) {
                return reference1;
            }
        }
        return null;
    }

    /**
     * Return a reference for the given mapping and source object, that doesn't already
     * contain an entry for the provided field.
     */
    public final Reference getReference(final ObjectReferenceMapping mapping, final Object sourceObject,
                                        final Field xmlField) {
        final Field targetField = (Field) mapping.getSourceToTargetKeyFieldAssociations().get(xmlField);
        String tgtXpath = null;
        if (!(mapping.getReferenceClass() == null || mapping.getReferenceClass() == Object.class)) {
            if (targetField != null) {
                tgtXpath = targetField.getXPath();
            }
        }
        final ReferenceKey key = new ReferenceKey(sourceObject, mapping);
        Reference reference = referencesMap.get(key);
        if (reference != null && reference.getPrimaryKeyMap().get(tgtXpath) == null) {
            return reference;
        }

        // Search for unlucky references that were kicked out of hashMap by entries with equal key.
        for (Reference reference1 : unluckyReferences) {
            if (reference1.getMapping() == mapping && reference1.getSourceObject() == sourceObject) {
                if (reference1.getPrimaryKeyMap().get(tgtXpath) == null) {
                    return reference1;
                }
            }
        }
        return null;
    }

    /**
     * Store an instance by key based on a mapped class.  These values will be
     * used when it comes time to resolve the references.
     *
     * @since EclipseLink 2.5.0
     */
    public final void putValue(final Class clazz, final Object key, final Object object) {
        Map<Object, Object> keyToObject = cache.get(clazz);
        if (null == keyToObject) {
            keyToObject = new HashMap<Object, Object>();
            cache.put(clazz, keyToObject);
        }
        keyToObject.put(key, object);
    }

    /**
     * INTERNAL:
     *
     * @param session               typically will be a unit of work
     * @param userSpecifiedResolver a user-provided subclass of IDResolver, may be null
     */
    public final void resolveReferences(final CoreAbstractSession session, final IDResolver userSpecifiedResolver,
                                        final ErrorHandler handler) {
        final Collection<Reference> luckyReferences = referencesMap.values();
        final Iterator<Reference> itLucky = luckyReferences.iterator();
        final Iterator<Reference> itUnlucky = unluckyReferences.iterator();
        /**
         * Represents rational index of last Reference that was kicked out from original hashMap.
         */
        Integer lastValue = unluckyRefPositions.peekLast();
        /**
         * Speed up array which lowers time complexity by a factor of n.
         */
        boolean[] a = null;
        if (lastValue == null) {
            lastValue = -1; // for the condition "i <= lastValue"
        } else {
            a = new boolean[lastValue + 1];
            //noinspection ForLoopReplaceableByForEach
            for (final Integer integer : unluckyRefPositions) {
                a[integer] = true;
            }
        }
        for (int i = 0, totalLength = luckyReferences.size() + unluckyReferences.size(); i < totalLength; i++) {
            final Reference reference;
            // A quick check to see if position [i] originally
            // contained a Reference that was later kicked out.
            assert a != null;
            if (i <= lastValue && a[i]) {
                reference = itUnlucky.next();
            } else {
                reference = itLucky.next();
            }
            perform(session, userSpecifiedResolver, handler, reference);
        }
        resetContainers();
    }

    /**
     * Resets the references containers.
     * <p/>
     * PERF:
     * Allocating a new object may be faster than clearing old objects, especially in this case.
     * <p/>
     * As 'Stephen C' says: ,,There are locality and cross-generational issues that could affect performance. When you
     * repeatedly recycle an ArrayList, the object and its backing array are likely to be tenured. That means that:
     * <pre>
     *   -  The list objects and the objects representing list elements are likely to be in different areas of the
     *      heap, potentially increasing TLB misses and page traffic, especially at GC time.
     *   -  Assignment of (young generation) references into the (tenured) list's backing array are likely to incur
     *     write barrier overheads ... depending on the GC implementation."
     * </pre>
     * from <a href="http://stackoverflow.com/questions/18370780/empty-an-arraylist-or-just-create-a-new-one-and-let-the-old-one-be-garbage-colle">Stack Overflow.</a>
     * <p/>
     * Taking last size can give approximate prediction of the next size.
     * Halving provides convergence and increases efficiency. Since the list will be empty after, it's efficient.
     */
    private void resetContainers() {
        referencesMap = new LinkedHashMap<ReferenceKey, Reference>(referencesMap.size() / 2 + 1);
        unluckyReferences = new ArrayList<Reference>(unluckyReferences.size() / 2 + 1);
        unluckyRefPositions = new LinkedList<Integer>();
        cache = new HashMap<Class, Map<Object, Object>>(cache.size() / 2 + 1);
    }

    /**
     * Add java doc if you understand this code.
     */
    private void perform(final CoreAbstractSession session, final IDResolver userSpecifiedResolver,
                         final ErrorHandler handler, final Reference reference) {
        final Object referenceSourceObject = reference.getSourceObject();
        if (reference.getMapping() instanceof CollectionReferenceMapping) {
            final CollectionReferenceMapping mapping = (CollectionReferenceMapping) reference.getMapping();
            final CoreContainerPolicy cPolicy = mapping.getContainerPolicy();
            //container should never be null
            final Object container = reference.getContainer();

            // create vectors of primary key values - one vector per reference instance
            createPKVectorsFromMap(reference, mapping);
            // if the we could not generate the primary key for the reference, it will not resolve - skip it
            if (reference.getPrimaryKey() == null) {
                return;
            }
            // loop over each pk vector and get object from cache - then add to collection and set on object
            Object value = null;
            if (!mapping.isWriteOnly()) {
                for (Object o : ((Vector) reference.getPrimaryKey())) {
                    final CacheId primaryKey = (CacheId) o;

                    if (userSpecifiedResolver != null) {
                        final Callable c;
                        try {
                            if (primaryKey.getPrimaryKey().length > 1) {
                                final Map<String, Object> idWrapper = new HashMap<String, Object>();
                                for (int y = 0; y < primaryKey.getPrimaryKey().length; y++) {
                                    final ObjectReferenceMapping refMapping =
                                            (ObjectReferenceMapping) reference.getMapping();
                                    final String idName = (String) refMapping.getReferenceDescriptor().
                                            getPrimaryKeyFieldNames().get(y);
                                    final Object idValue = primaryKey.getPrimaryKey()[y];
                                    idWrapper.put(idName, idValue);
                                }
                                c = userSpecifiedResolver.resolve(idWrapper, reference.getTargetClass());
                            } else {
                                c = userSpecifiedResolver.resolve(primaryKey.getPrimaryKey()[0],
                                        reference.getTargetClass());
                            }
                            if (c != null) {
                                value = c.call();
                            }
                        } catch (Exception e) {
                            throw XMLMarshalException.unmarshalException(e);
                        }
                    } else {
                        value = getValue(session, reference, primaryKey, handler);
                    }

                    if (value != null) {
                        cPolicy.addInto(value, container, session);
                    }
                }
            }
            // for each reference, get the source object and add it to the container policy
            // when finished, set the policy on the mapping
            mapping.setAttributeValueInObject(referenceSourceObject, container);
            final InverseReferenceMapping inverseReferenceMapping = mapping.getInverseReferenceMapping();
            if (inverseReferenceMapping != null && value != null) {
                final CoreAttributeAccessor backpointerAccessor = inverseReferenceMapping.getAttributeAccessor();
                final CoreContainerPolicy backpointerContainerPolicy = inverseReferenceMapping.getContainerPolicy();
                if (backpointerContainerPolicy == null) {
                    backpointerAccessor.setAttributeValueInObject(value, referenceSourceObject);
                } else {
                    Object backpointerContainer = backpointerAccessor.getAttributeValueFromObject(value);
                    if (backpointerContainer == null) {
                        backpointerContainer = backpointerContainerPolicy.containerInstance();
                        backpointerAccessor.setAttributeValueInObject(value, backpointerContainer);
                    }
                    backpointerContainerPolicy.addInto(referenceSourceObject, backpointerContainer, session);
                }
            }
        } else if (reference.getMapping() instanceof ObjectReferenceMapping) {
            final CacheId primaryKey = (CacheId) reference.getPrimaryKey();
            Object value = null;
            if (userSpecifiedResolver != null) {
                final Callable c;
                try {
                    if (primaryKey.getPrimaryKey().length > 1) {
                        final Map<String, Object> idWrapper = new HashMap<String, Object>();
                        for (int y = 0; y < primaryKey.getPrimaryKey().length; y++) {
                            final ObjectReferenceMapping refMapping = (ObjectReferenceMapping) reference.getMapping();
                            final String idName = (String) refMapping.getReferenceDescriptor()
                                    .getPrimaryKeyFieldNames().get(y);
                            final Object idValue = primaryKey.getPrimaryKey()[y];
                            idWrapper.put(idName, idValue);
                        }
                        c = userSpecifiedResolver.resolve(idWrapper, reference.getTargetClass());
                    } else {
                        c = userSpecifiedResolver.resolve(primaryKey.getPrimaryKey()[0], reference.getTargetClass());
                    }
                    if (c != null) {
                        value = c.call();
                    }
                } catch (Exception e) {
                    throw XMLMarshalException.unmarshalException(e);
                }
            } else {
                value = getValue(session, reference, primaryKey, handler);
            }

            ObjectReferenceMapping mapping = (ObjectReferenceMapping) reference.getMapping();
            if (value != null) {
                mapping.setAttributeValueInObject(reference.getSourceObject(), value);
            }
            if (null != reference.getSetting()) {
                reference.getSetting().setValue(value);
            }

            InverseReferenceMapping inverseReferenceMapping = mapping.getInverseReferenceMapping();
            if (inverseReferenceMapping != null) {
                CoreAttributeAccessor backpointerAccessor = inverseReferenceMapping.getAttributeAccessor();
                CoreContainerPolicy backpointerContainerPolicy = inverseReferenceMapping.getContainerPolicy();
                if (backpointerContainerPolicy == null) {
                    backpointerAccessor.setAttributeValueInObject(value, referenceSourceObject);
                } else {
                    Object backpointerContainer = backpointerAccessor.getAttributeValueFromObject(value);
                    if (backpointerContainer == null) {
                        backpointerContainer = backpointerContainerPolicy.containerInstance();
                        backpointerAccessor.setAttributeValueInObject(value, backpointerContainer);
                    }
                    backpointerContainerPolicy.addInto(reference.getSourceObject(), backpointerContainer, session);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Create primary key values to be used for cache lookup.  The map
     * of primary keys on the reference is keyed on the reference descriptors primary
     * key field names.  Each of these primary keys contains all of the values for a
     * particular key - in the order that they we read in from the document.  For
     * example, if the key field names are A, B, and C, and there are three reference
     * object instances, then the hashmap would have the following:
     * (A=[1,2,3], B=[X,Y,Z], C=[Jim, Joe, Jane]).  If the primary key field names on
     * the reference descriptor contained [B, C, A], then the result of this method call
     * would be reference.primaryKeys=([X, Jim, 1], [Y, Joe, 2], [Z, Jane, 3]).
     */
    private void createPKVectorsFromMap(final Reference reference, final CollectionReferenceMapping mapping) {
        final CoreDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
        final Vector<CacheId> pks = new Vector<CacheId>();
        if (null == referenceDescriptor) {
            final CacheId pkVals = (CacheId) reference.getPrimaryKeyMap().get(null);
            if (null == pkVals) {
                return;
            }
            for (int x = 0; x < pkVals.getPrimaryKey().length; x++) {
                final Object[] values = new Object[1];
                values[0] = pkVals.getPrimaryKey()[x];
                pks.add(new CacheId(values));
            }
        } else {
            final List pkFields = referenceDescriptor.getPrimaryKeyFieldNames();
            if (pkFields.isEmpty()) {
                return;
            }

            boolean init = true;

            // for each primary key field name
            for (Object pkField : pkFields) {
                final CacheId pkVals = (CacheId) reference.getPrimaryKeyMap().get(pkField);

                if (pkVals == null) {
                    return;
                }
                // initialize the list of pk vectors once and only once
                if (init) {
                    for (int i = 0; i < pkVals.getPrimaryKey().length; i++) {
                        pks.add(new CacheId(new Object[0]));
                    }
                    init = false;
                }

                // now add each value for the current target key to it's own vector
                for (int i = 0; i < pkVals.getPrimaryKey().length; i++) {
                    final Object val = pkVals.getPrimaryKey()[i];
                    (pks.get(i)).add(val);
                }
            }
        }
        reference.setPrimaryKey(pks);
    }

    /**
     * Add java doc if you understand this code.
     */
    private Object getValue(final CoreAbstractSession session, final Reference reference, final CacheId primaryKey,
                            final ErrorHandler handler) {
        final Class referenceTargetClass = reference.getTargetClass();
        if (null == referenceTargetClass || referenceTargetClass == CoreClassConstants.OBJECT) {
            for (Object entry : session.getDescriptors().values()) {
                Object value = null;
                final Descriptor targetDescriptor = (Descriptor) entry;
                final List pkFields = targetDescriptor.getPrimaryKeyFields();
                if (null != pkFields && 1 == pkFields.size()) {
                    Field pkField = (Field) pkFields.get(0);
                    pkField = (Field) targetDescriptor.getTypedField(pkField);
                    final Class targetType = pkField.getType();
                    if (targetType == CoreClassConstants.STRING || targetType == CoreClassConstants.OBJECT) {
                        value = getValue(targetDescriptor.getJavaClass(), primaryKey);
                    } else {
                        try {
                            final Object[] pkValues = primaryKey.getPrimaryKey();
                            final Object[] convertedPkValues = new Object[pkValues.length];
                            for (int x = 0; x < pkValues.length; x++) {
                                convertedPkValues[x] = session.getDatasourcePlatform().getConversionManager()
                                        .convertObject(pkValues[x], targetType);
                            }
                            value = getValue(targetDescriptor.getJavaClass(), new CacheId(convertedPkValues));
                        } catch (ConversionException e) {
                        }
                    }
                    if (null != value) {
                        return value;
                    }
                }
            }
            if (primaryKey.getPrimaryKey()[0] != null) {
                final XMLMarshalException e = XMLMarshalException.missingIDForIDRef(
                        Object.class.getName(), primaryKey.getPrimaryKey());
                if (handler != null) {
                    final SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), null, e);
                    try {
                        handler.warning(saxParseException);
                    } catch (SAXException saxException) {
                        throw e;
                    }
                }
            }
            return null;
        } else {
            Object value = getValue(referenceTargetClass, primaryKey);
            if (null == value) {
                final CoreMapping mapping = (CoreMapping) reference.getMapping();
                final CoreDescriptor targetDescriptor = mapping.getReferenceDescriptor();
                if (targetDescriptor.hasInheritance()) {
                    final CoreInheritancePolicy inheritancePolicy = targetDescriptor.getInheritancePolicy();
                    final List<CoreDescriptor> childDescriptors = inheritancePolicy.getAllChildDescriptors();
                    for (CoreDescriptor childDescriptor : childDescriptors) {
                        value = getValue(childDescriptor.getJavaClass(), primaryKey);
                        if (null != value) {
                            return value;
                        }
                    }
                }
            }
            if (value == null && (primaryKey.getPrimaryKey()[0] != null)) {
                final XMLMarshalException e = XMLMarshalException.missingIDForIDRef(
                        referenceTargetClass.getName(), primaryKey.getPrimaryKey());
                if (handler != null) {
                    SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), null, e);
                    try {
                        handler.warning(saxParseException);
                    } catch (SAXException saxException) {
                        throw e;
                    }
                }
            }
            return value;
        }
    }

    /**
     * Retrieves value from {@link #cache}.
     */
    private Object getValue(Class clazz, CacheId primaryKey) {
        Map<Object, Object> keyToObject = cache.get(clazz);
        if (null != keyToObject) {
            return keyToObject.get(primaryKey);
        }
        return null;
    }

    /**
     * This class serves to represent a key for {@link org.eclipse.persistence.internal.oxm.Reference} when used
     * in maps.
     */
    private static final class ReferenceKey {
        private Object sourceObject;
        private Mapping mapping;

        public ReferenceKey() {
        }

        public ReferenceKey(final Object sourceObject, final Mapping mapping) {
            this.sourceObject = sourceObject;
            this.mapping = mapping;
        }

        public ReferenceKey(final Reference ref) {
            this.sourceObject = ref.getSourceObject();
            this.mapping = ref.getMapping();
        }

        public void setMapping(final ObjectReferenceMapping mapping) {
            this.mapping = mapping;
        }

        public void setSourceObject(final Object sourceObject) {
            this.sourceObject = sourceObject;
        }

        @Override
        public final int hashCode() {
            int result = System.identityHashCode(sourceObject);
            result = 31 * result + System.identityHashCode(mapping);
            return result;
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof ReferenceKey)) return false;

            ReferenceKey that = (ReferenceKey) o;

            return sourceObject == that.sourceObject && mapping == that.mapping;
        }
    }

}