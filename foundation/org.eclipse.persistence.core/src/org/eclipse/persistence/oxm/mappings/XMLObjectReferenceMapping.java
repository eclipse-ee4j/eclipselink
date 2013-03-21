/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.internal.oxm.ReferenceResolver;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * TopLink OXM version of a 1-1 mapping.  A list of source-target key field
 * associations is used to link the source xpaths to their related target xpaths, 
 * and hence their primary key (unique identifier) values used when (un)marshalling.
 * This mapping has a Vector of XMLFields as opposed to a single XMLField.
 * 
 * It is important to note that each target xpath is assumed to be set as a primary
 * key field on the target (reference) class descriptor - this is necessary in order
 * to locate the correct target object instance in the session cache when resolving
 * mapping references.
 */
public class XMLObjectReferenceMapping extends AggregateMapping implements ObjectReferenceMapping<AbstractSession, AttributeAccessor, ContainerPolicy, ClassDescriptor, DatabaseField, UnmarshalRecord, XMLField, XMLRecord>, XMLMapping {
    protected HashMap sourceToTargetKeyFieldAssociations;
    protected Vector sourceToTargetKeys; // maintain the order of the keys
    private boolean isWriteOnly;
    private XMLInverseReferenceMapping inverseReferenceMapping;

    /**
     * PUBLIC:
     * The default constructor initializes the sourceToTargetKeyFieldAssociations
     * and sourceToTargetKeys data structures.
     */
    public XMLObjectReferenceMapping() {
        sourceToTargetKeyFieldAssociations = new HashMap();
        sourceToTargetKeys = new Vector();
    }

    /**
     * PUBLIC:
     * Add a source-target xpath pair to the map.
     * 
     * @param srcXPath
     * @param tgtXPath
     */
    public void addSourceToTargetKeyFieldAssociation(String srcXPath, String tgtXPath) {
        XMLField srcFld = new XMLField(srcXPath);
        sourceToTargetKeys.add(srcFld);
        if(null == tgtXPath) {
            sourceToTargetKeyFieldAssociations.put(srcFld, null);
        } else {
            sourceToTargetKeyFieldAssociations.put(srcFld, new XMLField(tgtXPath));
        }
    }
    
    public void addSourceToTargetKeyFieldAssociation(XMLField srcField, XMLField tgtField) {
        sourceToTargetKeys.add(srcField);
        sourceToTargetKeyFieldAssociations.put(srcField, tgtField);
    }

    /**    
     * INTERNAL:
     * Retrieve the target object's primary key value that is mapped to a given
     * source xpath (in the source-target key field association list).
     * 
     * @param sourceObject
     * @param xmlFld
     * @param session
     * @return null if the target object is null, the reference class is null, or
     * a primary key field name does not exist on the reference descriptor that
     * matches the target field name - otherwise, return the associated primary 
     * key value   
     */
    public Object buildFieldValue(Object targetObject, XMLField xmlFld, AbstractSession session) {
        if (targetObject == null || getReferenceClass() == null) {
            return null;
        }
        ClassDescriptor descriptor = referenceDescriptor;
        if(null == descriptor) {
            descriptor = session.getClassDescriptor(targetObject);
        }
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        Object primaryKey = objectBuilder.extractPrimaryKeyFromObject(targetObject, session);
        int idx = 0;
        if(!(null == referenceClass || ClassConstants.OBJECT == getReferenceClass())) {
            idx = descriptor.getPrimaryKeyFields().indexOf(getSourceToTargetKeyFieldAssociations().get(xmlFld));
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
        ReferenceResolver resolver = record.getReferenceResolver();
        if (resolver == null) {
            return;
        }

        Object srcObject = record.getCurrentObject();
        // the order in which the primary keys are added to the vector is
        // relevant for cache lookup - it must match the ordering of the 
        // reference descriptor's primary key entries

        Reference reference = resolver.getReference(this, srcObject);
        CacheId primaryKeys;
        if(null == referenceClass || ClassConstants.OBJECT == referenceClass) {
            if (reference == null) {
                // if reference is null, create a new instance and set it on the resolver
                primaryKeys = new CacheId(new Object[1]);
                reference = new Reference(this, srcObject, referenceClass, primaryKeys);
                resolver.addReference(reference);
                record.reference(reference);
            }  else {
                primaryKeys = (CacheId) reference.getPrimaryKey();
            }
            primaryKeys.set(0, object);
        } else {
            Vector pkFieldNames = referenceDescriptor.getPrimaryKeyFieldNames();
            // if reference is null, create a new instance and set it on the resolver
            if (reference == null) {
                primaryKeys = new CacheId(new Object[pkFieldNames.size()]);
                reference = new Reference(this, srcObject, referenceClass, primaryKeys);
                resolver.addReference(reference);
                record.reference(reference);
            } else {
                primaryKeys = (CacheId) reference.getPrimaryKey();
            }
            XMLField tgtFld = (XMLField) getSourceToTargetKeyFieldAssociations().get(xmlField);
            int idx = pkFieldNames.indexOf(tgtFld.getQualifiedName());
            // fix for bug# 5687430
            // need to get the actual type of the target (i.e. int, String, etc.) 
            // and use the converted value when checking the cache.
            Object value = session.getDatasourcePlatform().getConversionManager().convertObject(object, referenceDescriptor.getTypedField(tgtFld).getType());
            if (value != null) {
                primaryKeys.set(idx, value);
            }
        }
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // objects referenced by this mapping are not registered as they have
        // no identity, however mappings from the referenced object may need cascading.
        Object objectReferenced = getRealAttributeValueFromObject(object, uow);
        if (objectReferenced == null) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadePerformRemove(objectReferenced, uow, visitedObjects);
        }
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // Aggregate objects are not registered but their mappings should be.
        Object objectReferenced = getRealAttributeValueFromObject(object, uow);
        if (objectReferenced == null) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadeRegisterNewForCreate(objectReferenced, uow, visitedObjects);
        }
    }

    /**
     * INTERNAL:
     * Return a list of XMLFields based on the source XPath values
     * in the source-target key field associations list.
     */
    public Vector getFields() {
        return sourceToTargetKeys;
    }

    /**
     * Return a QName representation the schema type for a given XMLField, if
     * applicable.
     * 
     * Note:  This method performs the same functionality as 'getSchemaType' in 
     * org.eclipse.persistence.internal.oxm.XMLSimpleMappingNodeValue.
     * 
     * @param xmlField
     * @param value
     * @return
     */
    protected QName getSchemaType(XMLField xmlField, Object value, AbstractSession session) {
        QName schemaType = null;
        if (xmlField.isTypedTextField()) {
            schemaType = xmlField.getXMLType(value.getClass());
        } else if (xmlField.isUnionField()) {
            return getSingleValueToWriteForUnion((XMLUnionField) xmlField, value, session);
        } else if (xmlField.getSchemaType() != null) {
            schemaType = xmlField.getSchemaType();
        }
        return schemaType;
    }

    /**
     * Return a single QName representation for a given XMLUnionField, if applicable.
     * 
     * Note:  This method performs the same functionality as 'getSingleValueToWriteForUnion'
     * in org.eclipse.persistence.internal.oxm.XMLSimpleMappingNodeValue.
     *  
     * @param xmlField
     * @param value
     * @return
     */
    protected QName getSingleValueToWriteForUnion(XMLUnionField xmlField, Object value, AbstractSession session) {
        ArrayList schemaTypes = xmlField.getSchemaTypes();
        QName schemaType = null;
        QName nextQName;
        Class javaClass;
        for (int i = 0; i < schemaTypes.size(); i++) {
            nextQName = (QName) (xmlField).getSchemaTypes().get(i);
            try {
                if (nextQName != null) {
                    javaClass = xmlField.getJavaClass(nextQName);
                    value = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, javaClass, nextQName);
                    schemaType = nextQName;
                    break;
                }
            } catch (ConversionException ce) {
                if (i == (schemaTypes.size() - 1)) {
                    schemaType = nextQName;
                }
            }
        }
        return schemaType;
    }

    /**
     * INTERNAL:
     * Return a list of source-target xmlfield pairs.
     * 
     * @return
     */
    public HashMap getSourceToTargetKeyFieldAssociations() {
        return sourceToTargetKeyFieldAssociations;
    }

    /**
     * Return a string representation of a given value, based on a given schema type. 
     * 
     * Note:  This method performs the same functionality as 'getValueToWrite'
     * in org.eclipse.persistence.internal.oxm.XMLSimpleMappingNodeValue.
     * 
     * @param schemaType
     * @param value
     * @return
     */
    protected String getValueToWrite(QName schemaType, Object value, AbstractSession session) {
        return (String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType);
    }

    /**
     * INTERNAL:
     * Register a ReferenceResolver as an event listener on the session, 
     * if one doesn't already exist.  Each source/target field will have
     * a namespace resolver set as well. 
     * 
     * @see org.eclipse.persistence.internal.oxm.ReferenceResolver
     * @see org.eclipse.persistence.oxm.NamespaceResolver
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        if (null == referenceClass) {
            if(referenceClassName != null){
                setReferenceClass(session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(referenceClassName));
            }
        }
        if(!(null == referenceClass || referenceClass == ClassConstants.OBJECT)) {
            super.initialize(session);
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
                if(null == targetDescriptor) {
                    throw DescriptorException.referenceClassNotSpecified(this);
                }               
                //primary key field from ref desc
                List<DatabaseField > pkFields  = targetDescriptor.getPrimaryKeyFields();
                for(int i=0; i<pkFields.size(); i++){
                	XMLField nextPKField = (XMLField)pkFields.get(i);
                	if(targetField.equals(nextPKField)){
                		targetField = (XMLField) targetDescriptor.buildField(nextPKField);
                		sourceField.setSchemaType(targetField.getSchemaType());
                		break;
                	}
                } 

            }
            sourceToTargetKeyFieldAssociations.put(sourceField, targetField);
        }
        
        if (this.inverseReferenceMapping != null) {
            if (null != this.inverseReferenceMapping.getAttributeAccessor()) {
                this.inverseReferenceMapping.getAttributeAccessor().initializeAttributes(this.referenceClass);
            }
        }
    }
    
    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
    }
    

    /**
     * INTERNAL:
     * Indicates that this is an XML mapping.
     */
    public boolean isXMLMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Extract the primary key values from the row, then create an 
     * org.eclipse.persistence.internal.oxm.Reference instance and store it 
     * on the session's org.eclipse.persistence.internal.oxm.ReferenceResolver.
     */
    public Object readFromRowIntoObject(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object targetObject, CacheKey parentCacheKey, ObjectBuildingQuery sourceQuery, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        // the order in which the primary keys are added to the vector is
        // relevant for cache lookup - it must match the ordering of the 
        // reference descriptor's primary key entries
        CacheId primaryKeys;
        ClassDescriptor descriptor = sourceQuery.getSession().getClassDescriptor(referenceClass);
        Vector pkFieldNames = null;
        if(null == descriptor) {
            primaryKeys = new CacheId(new Object[1]);
        } else {
            pkFieldNames = descriptor.getPrimaryKeyFieldNames();
            primaryKeys = new CacheId(new Object[pkFieldNames.size()]);
        }
        Iterator keyIt = sourceToTargetKeys.iterator();
        while (keyIt.hasNext()) {
            XMLField keyFld = (XMLField) keyIt.next();
            XMLField tgtFld = (XMLField) getSourceToTargetKeyFieldAssociations().get(keyFld);
            Object value;
            int idx = 0;
            if(null == tgtFld) {
               value = databaseRow.get(keyFld);
            } else {
                idx = pkFieldNames.indexOf(tgtFld.getXPath());
                if (idx == -1) {
                    continue;
                }
                // fix for bug# 5687430
                // need to get the actual type of the target (i.e. int, String, etc.) 
                // and use the converted value when checking the cache.
                value = executionSession.getDatasourcePlatform().getConversionManager().convertObject(databaseRow.get(keyFld), descriptor.getTypedField(tgtFld).getType());
            }
            if (value != null) {
                primaryKeys.set(idx, value);
            }
        }
        // store the Reference instance on the resolver for use during mapping
        // resolution phase
        ReferenceResolver resolver = ((DOMRecord) databaseRow).getReferenceResolver();
        if (resolver != null) {
            resolver.addReference(new Reference(this, targetObject, referenceClass, primaryKeys));
        }
        return null;
    }

    /**
     * @Override 
     * @param field
     */
    public void setField(DatabaseField field) {
        // do nothing.
    }

    /**
     * INTERNAL:
     * Set the list of source-target xmlfield pairs.
     * 
     * @return
     */
    public void setSourceToTargetKeyFieldAssociations(HashMap sourceToTargetKeyFieldAssociations) {
        this.sourceToTargetKeyFieldAssociations = sourceToTargetKeyFieldAssociations;
    }

    /**
     * INTERNAL:
     * Write the attribute value from the object to the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        // for each xmlField on this mapping
        Object targetObject = getAttributeValueFromObject(object);
        writeSingleValue(targetObject, object, (XMLRecord) row, session);
    }

    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {
        for (Iterator fieldIt = getFields().iterator(); fieldIt.hasNext();) {
            XMLField xmlField = (XMLField) fieldIt.next();
            Object fieldValue = buildFieldValue(value, xmlField, session);
            if (fieldValue != null) {
                QName schemaType = getSchemaType(xmlField, fieldValue, session);
                String stringValue = getValueToWrite(schemaType, fieldValue, session);
                row.put(xmlField, stringValue);
            }
        }

    }
    
    public void setIsWriteOnly(boolean b) {
        this.isWriteOnly = b;
    }
    
    public boolean isWriteOnly() {
        return this.isWriteOnly;
    }
    
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        if(isWriteOnly()) {
            return;
        }
        super.setAttributeValueInObject(object, value);
    } 
    
    public XMLInverseReferenceMapping getInverseReferenceMapping() {
        return inverseReferenceMapping;
    }

    void setInverseReferenceMapping(XMLInverseReferenceMapping inverseReferenceMapping) {
        this.inverseReferenceMapping = inverseReferenceMapping;
    }
    @Override
    public boolean isObjectReferenceMapping() {
        return true;
    }
    
}