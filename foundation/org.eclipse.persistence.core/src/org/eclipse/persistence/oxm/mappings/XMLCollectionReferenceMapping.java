/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * TopLink OXM version of a 1-M mapping.  A list of source-target key field
 * associations is used to link the source xpaths to their related target 
 * xpaths, and hence their primary key (unique identifier) values used when 
 * (un)marshalling.
 * 
 * It is important to note that each target xpath is assumed to be set as a primary
 * key field on the target (reference) class descriptor - this is necessary in order
 * to locate the correct target object instance in the session cache when resolving
 * mapping references.
 * 
 * The usesSingleNode flag should be set to true if the keys are to be written out in space-separated
 * lists.
 * 
 * @see XMLObjectReferenceMapping
 * @see ContainerMapping
 */
public class XMLCollectionReferenceMapping extends XMLObjectReferenceMapping implements ContainerMapping {
    protected ContainerPolicy containerPolicy; // type of container used to hold the aggregate objects
    private static final String SPACE = " ";
    private DatabaseField field;
    private boolean usesSingleNode;
    private boolean reuseContainer;

    /**
     * PUBLIC:
     * The default constructor initializes the sourceToTargetKeyFieldAssociations
     * and sourceToTargetKeys data structures.
     */
    public XMLCollectionReferenceMapping() {
        sourceToTargetKeyFieldAssociations = new HashMap();
        sourceToTargetKeys = new NonSynchronizedVector();
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
        this.usesSingleNode = false;
    }

    public DatabaseField getField() {
        return field;
    }

    public void setField(DatabaseField field) {
        this.field = field;
    }

    
    /**
     * Get the XPath String
     * @return String the XPath String associated with this Mapping
     */
    public String getXPath() {
        return getField().getName();
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString) {
        this.setField(new XMLField(xpathString));
    }

    /**    
     * INTERNAL:
     * Retrieve the target object's primary key value that is mapped to a given
     * source xpath (in the source-target key field association list).
     * 
     * @param targetObject - the reference class instance that holds the required pk value
     * @param xmlFld
     * @param session
     * @return null if the target object is null, the reference class is null, or
     * a primary key field name does not exist on the reference descriptor that
     * matches the target field name - otherwise, return the associated primary 
     * key value   
     */
    public Object buildFieldValue(Object targetObject, XMLField xmlFld, AbstractSession session) {
        if (targetObject == null) {
            return null;
        }
        ClassDescriptor descriptor = referenceDescriptor;
        if(null == descriptor) {
            descriptor = session.getClassDescriptor(targetObject);
        }
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        Object primaryKey = objectBuilder.extractPrimaryKeyFromObject(targetObject, session);
        XMLField tgtXMLField = (XMLField) getSourceToTargetKeyFieldAssociations().get(xmlFld);
        int idx = 0;
        if(!(null == referenceClass || ClassConstants.OBJECT == referenceClass)) {
            idx = descriptor.getPrimaryKeyFields().indexOf(tgtXMLField);
            if (idx == -1) {
                return null;
            }
        }
        if (primaryKey instanceof CacheId) {
            return ((CacheId)primaryKey).getPrimaryKey()[idx];
        } else {
            return primaryKey;
        }
    }

    /**
     * INTERNAL:
     * Create (if necessary) and populate a reference object that will be used
     * during the mapping reference resolution phase after unmarshalling is
     * complete.
     * 
     * @param record
     * @param xmlField
     * @param object
     * @param session
     * @return
     */
    public void buildReference(UnmarshalRecord record, XMLField xmlField, Object object, AbstractSession session) {
        ReferenceResolver resolver = ReferenceResolver.getInstance(session);
        if (resolver == null) {
            return;
        }

        Object srcObject = record.getCurrentObject();
        Reference reference = resolver.getReference(this, srcObject);
        if (reference == null) {
            // if reference is null, create a new instance and set it on the resolver
            reference = new Reference(this, srcObject, referenceClass, new HashMap());
            resolver.addReference(reference);
        }
        CacheId primaryKeys;
        if(null == referenceClass || ClassConstants.OBJECT == referenceClass) {
            HashMap primaryKeyMap = reference.getPrimaryKeyMap();
            CacheId pks = (CacheId) primaryKeyMap.get(null);
            if (pks == null){
                Object[] pkValues = new Object[1];
                pks = new CacheId(pkValues);
                primaryKeyMap.put(null, pks);
            }
            if(usesSingleNode) {
                for (StringTokenizer stok = new StringTokenizer((String) object); stok.hasMoreTokens();) {
                    pks.add(stok.nextToken());
                }
            } else {
                pks.add(object);
            }
        } else {
            XMLField tgtFld = (XMLField) getSourceToTargetKeyFieldAssociations().get(xmlField);
            String tgtXPath = tgtFld.getXPath();
            HashMap primaryKeyMap = reference.getPrimaryKeyMap();
            CacheId pks = (CacheId) primaryKeyMap.get(tgtXPath);
            ClassDescriptor descriptor = session.getClassDescriptor(referenceClass);
            if (pks == null){
                pks = new CacheId(new Object[0]);
                primaryKeyMap.put(tgtXPath, pks);
            }

            Class type = descriptor.getTypedField(tgtFld).getType();
            XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
            if(usesSingleNode) {
                for (StringTokenizer stok = new StringTokenizer((String) object); stok.hasMoreTokens();) {
                    Object value = xmlConversionManager.convertObject(stok.nextToken(), type);
                    if (value != null) {
                        pks.add(value);
                    }
                }
            } else {
                Object value = xmlConversionManager.convertObject(object, type);
                if (value != null) {
                    pks.add(value);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Return the mapping's containerPolicy.
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    /**
     * INTERNAL:
     * The mapping is initialized with the given session. This mapping is fully initialized
     * after this.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if(null != getField()) {
            setField(getDescriptor().buildField(getField()));
        }
        ContainerPolicy cp = getContainerPolicy();
        if (cp != null) {
            if (cp.getContainerClass() == null) {
                Class cls = session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(cp.getContainerClassName());
                cp.setContainerClass(cls);
            }
        }

        // iterate over each source & target XMLField and set the 
        // appropriate namespace resolver
        XMLDescriptor descriptor = (XMLDescriptor) this.getDescriptor();
        XMLDescriptor targetDescriptor = (XMLDescriptor) getReferenceDescriptor();
        for (int index = 0; index < sourceToTargetKeys.size(); index++) {
            XMLField sourceField = (XMLField) sourceToTargetKeys.get(index);
            XMLField targetField = (XMLField) sourceToTargetKeyFieldAssociations.remove(sourceField);
            sourceField = (XMLField) descriptor.buildField(sourceField);
            sourceToTargetKeys.set(index, sourceField);
            if(null != targetField) {
                targetField = (XMLField) targetDescriptor.buildField(targetField);
            }
            sourceToTargetKeyFieldAssociations.put(sourceField, targetField);
        }
    }

    /**
     * INTERNAL:
     * Extract the primary key values from the row, then create an 
     * org.eclipse.persistence.internal.oxm.Reference instance and stored it 
     * on the session's org.eclipse.persistence.internal.oxm.ReferenceResolver.
     */
    public Object readFromRowIntoObject(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object targetObject, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        ClassDescriptor descriptor = sourceQuery.getSession().getClassDescriptor(getReferenceClass());
        ContainerPolicy cp = getContainerPolicy();
        HashMap primaryKeyMap = new HashMap();
        // for each source xmlField, get the value from the row and store
        for (Iterator fieldIt = getFields().iterator(); fieldIt.hasNext();) {
            XMLField fld = (XMLField) fieldIt.next();
            //
            Object fieldValue = databaseRow.getValues(fld);
            if ((fieldValue == null) || (fieldValue instanceof String) || !(fieldValue instanceof Vector)) {
                return cp.containerInstance();
            }
            // fix for bug# 5687430
            // need to get the actual type of the target (i.e. int, String, etc.) 
            // and use the converted value when checking the cache.
            XMLConversionManager xmlConversionManager = (XMLConversionManager) executionSession.getDatasourcePlatform().getConversionManager();
            XMLField tgtFld = (XMLField) getSourceToTargetKeyFieldAssociations().get(fld);
            CacheId newValues = new CacheId(new Object[0]);
            for (Iterator valIt = ((Vector) fieldValue).iterator(); valIt.hasNext();) {
                for (StringTokenizer stok = new StringTokenizer((String) valIt.next()); stok.hasMoreTokens();) {
                    Object value;
                    if(null == tgtFld) {
                        value = stok.nextToken();
                    } else {
                        value = xmlConversionManager.convertObject(stok.nextToken(), descriptor.getTypedField(tgtFld).getType());
                    }
                    if (value != null) {
                        newValues.add(value);
                    }
                }
            }
            if(null == tgtFld) {
                primaryKeyMap.put(null, newValues);
            } else {
                primaryKeyMap.put(tgtFld.getXPath(), newValues);
            }
        }
        // store the Reference instance on the resolver for use during mapping resolution phase
        ReferenceResolver resolver = ReferenceResolver.getInstance(sourceQuery.getSession());
        if (resolver != null) {
            resolver.addReference(new Reference(this, targetObject, referenceClass, primaryKeyMap));
        }
        return null;
    }

    /**
     * ADVANCED:
     * Set the mapping's containerPolicy.
     */
    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        // set reference class here if necessary
        this.containerPolicy = containerPolicy;
        if (this.containerPolicy instanceof MapContainerPolicy) {
            ((MapContainerPolicy) this.containerPolicy).setElementClass(getReferenceClass());
        }
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Collection interface.
     * <p>jdk1.1.x: The container class must be a subclass of Vector.
     */
    public void useCollectionClass(Class concreteContainerClass) {
        this.setContainerPolicy(ContainerPolicy.buildPolicyFor(concreteContainerClass));
    }

    public void useCollectionClassName(String concreteContainerClassName) {
        this.setContainerPolicy(new CollectionContainerPolicy(concreteContainerClassName));
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects. The key used to index the value in the Map
     * is the value returned by a call to the specified zero-argument method.
     * The method must be implemented by the class (or a superclass) of the
     * value to be inserted into the Map.
     * <p>jdk1.2.x: The container class must implement (directly or indirectly) the Map interface.
     * <p>jdk1.1.x: The container class must be a subclass of Hashtable.
     * <p>The referenceClass must be set before calling this method.
     */
    public void useMapClass(Class concreteContainerClass, String methodName) {
        // the reference class has to be specified before coming here
        if (this.getReferenceClass() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(concreteContainerClass);
        policy.setKeyName(methodName, getReferenceClass().getName());
        this.setContainerPolicy(policy);
    }

    /**
     * INTERNAL:
     * For the purpose of XMLCollectionReferenceMappings, 'usesSingleNode' 
     * refers to the fact that the source key xpath fields should all be written as
     * space-separated lists. Would be used for mapping to an IDREFS field in a schema
     */
    public boolean usesSingleNode() {
        return this.usesSingleNode;
    }

    public void setUsesSingleNode(boolean useSingleNode) {
        this.usesSingleNode = useSingleNode;
    }

    /**
     * INTERNAL:
     * Write the attribute value from the object to the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        // for each xmlField on this mapping
        for (Iterator fieldIt = getFields().iterator(); fieldIt.hasNext();) {
            XMLField xmlField = (XMLField) fieldIt.next();
            ContainerPolicy cp = getContainerPolicy();
            Object collection = getAttributeAccessor().getAttributeValueFromObject(object);
            if (collection == null) {
                return;
            }

            Object fieldValue;
            Object objectValue;
            StringBuilder stringValueBuilder = new StringBuilder();
            QName schemaType;
            Object iterator = cp.iteratorFor(collection);
            if (usesSingleNode()) {
                while (cp.hasNext(iterator)) {
                    objectValue = cp.next(iterator, session);
                    fieldValue = buildFieldValue(objectValue, xmlField, session);
                    if (fieldValue != null) {
                        schemaType = getSchemaType(xmlField, fieldValue, session);
                        String newValue = getValueToWrite(schemaType, fieldValue, session);
                        if (newValue != null) {
                            stringValueBuilder.append(newValue);
                            if (cp.hasNext(iterator)) {
                                stringValueBuilder.append(SPACE);
                            }
                        }
                    }
                }
                if (stringValueBuilder.length() > 0) {
                    row.put(xmlField, stringValueBuilder.toString());
                }
            } else {
                ArrayList keyValues = new ArrayList();
                while (cp.hasNext(iterator)) {
                    objectValue = cp.next(iterator, session);
                    fieldValue = buildFieldValue(objectValue, xmlField, session);
                    if (fieldValue != null) {
                        schemaType = getSchemaType(xmlField, fieldValue, session);
                        String stringValue = getValueToWrite(schemaType, fieldValue, session);
                        //row.add(xmlField, stringValue);
                        keyValues.add(stringValue);
                    }
                }
                row.put(xmlField, keyValues);
            }
        }
    }

    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {
        for (Iterator fieldIt = getFields().iterator(); fieldIt.hasNext();) {
            XMLField xmlField = (XMLField) fieldIt.next();
            Object fieldValue = buildFieldValue(value, xmlField, session);
            if (fieldValue != null) {
                QName schemaType = getSchemaType(xmlField, fieldValue, session);
                String stringValue = getValueToWrite(schemaType, fieldValue, session);
                row.add(xmlField, stringValue);
            }
        }

    }

    public boolean isCollectionMapping() {
        return true;
    }

    /**
     * Return true if the original container on the object should be used if 
     * present.  If it is not present then the container policy will be used to
     * create the container. 
     */
    public boolean getReuseContainer() {
        return reuseContainer;
    }

    /**
     * Specify whether the original container on the object should be used if 
     * present.  If it is not present then the container policy will be used to
     * create the container. 
     */
    public void setReuseContainer(boolean reuseContainer) {
        this.reuseContainer = reuseContainer;
    }

}