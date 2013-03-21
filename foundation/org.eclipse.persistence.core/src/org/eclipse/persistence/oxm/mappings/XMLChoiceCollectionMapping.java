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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XMLChoiceFieldToClassAssociation;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.XMLContainerMapping;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLEntry;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * PUBLIC:
 * <p><b>Purpose:</b>Provide a mapping that can map a single attribute to a number of
 * different elements in an XML Document. This will be used to map to Choices or Substitution
 * Groups in an XML Schema
 * <p><b>Responsibilities:</b><ul>
 * <li>Allow the user to specify XPath -> Type mappings</li>
 * <li>Handle reading and writing of XML Documents containing a collection of choice or substitution
 * group elements</li>
 * </ul>
 * <p>The XMLChoiceCollectionMapping is the collection version of the XMLChoiceMapping. This mapping
 * allows the user to specify a number of different xpaths, and types associated with those xpaths. 
 * When any of these elements are encountered in the XML Document, they are read in as the correct
 * type and added to the collection.
 * <p><b>Setting up XPath mappings:</b>Unlike other OXM Mappings, instead of setting a single xpath,
 * the addChoiceElement method is used to specify an xpath and the type associated with this xpath.
 * <br>
 * xmlChoiceCollectionMapping.addChoiceElement("mystring/text()", String.class);
 * <br>
 * xmlChoiceCollectionMapping.addChoiceElement("myaddress", Address.class);
 * 
 */

public class XMLChoiceCollectionMapping extends DatabaseMapping implements ChoiceCollectionMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, Session, XMLUnmarshaller, XMLField, XMLMapping, XMLRecord>, XMLMapping {
    private Map<XMLField, Class> fieldToClassMappings;
    private Map<Class, XMLField> classToFieldMappings;
    private Map<Class, List<XMLField>> classToSourceFieldsMappings;
    private Map<String, List<XMLField>> classNameToSourceFieldsMappings;
    private Map<XMLField, XMLMapping> choiceElementMappings;
    private Map<String, XMLMapping> choiceElementMappingsByClassName;
    private Map<Class, XMLMapping> choiceElementMappingsByClass;
    private Map<XMLField, String> fieldToClassNameMappings;
    private Map<String, XMLField> classNameToFieldMappings;
    private Map<XMLField, Converter> fieldsToConverters;
    private ContainerPolicy containerPolicy;
    private boolean isDefaultEmptyContainer = XMLContainerMapping.EMPTY_CONTAINER_DEFAULT;
    private boolean isMixedContent;
    private AbstractNullPolicy wrapperNullPolicy;

    private boolean isWriteOnly;
    private static final AttributeAccessor temporaryAccessor = new InstanceVariableAttributeAccessor();;
    private boolean reuseContainer;
    private Converter converter;
    private XMLCompositeDirectCollectionMapping mixedContentMapping;
    
    private static final String DATA_HANDLER = "javax.activation.DataHandler";
    private static final String MIME_MULTIPART = "javax.mail.MimeMultipart";
    private static final String IMAGE = "java.awt.Image";

    public XMLChoiceCollectionMapping() {
        fieldToClassMappings = new HashMap<XMLField, Class>();
        fieldToClassNameMappings = new HashMap<XMLField, String>();
        classToFieldMappings = new HashMap<Class, XMLField>();
        classNameToFieldMappings = new HashMap<String, XMLField>();
        choiceElementMappings = new LinkedHashMap<XMLField, XMLMapping>();
        fieldsToConverters = new HashMap<XMLField, Converter>();
        choiceElementMappingsByClassName = new LinkedHashMap<String, XMLMapping>();
        choiceElementMappingsByClass = new LinkedHashMap<Class, XMLMapping>();
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
    }

    /**
     * Return the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        throw DescriptorException.invalidMappingOperation(this, "buildBackupClone");
    }

    /**
    * INTERNAL:
    * Clone the attribute from the original and assign it to the clone.
    */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        throw DescriptorException.invalidMappingOperation(this, "buildClone");
    }

    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        throw DescriptorException.invalidMappingOperation(this, "buildCloneFromRow");
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //Our current XML support does not make use of the UNitOfWork.
    }

    /**
     * INTERNAL:
     * This method was created in VisualAge.
     * @return prototype.changeset.ChangeRecord
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        throw DescriptorException.invalidMappingOperation(this, "compareForChange");
    }

    /**
    * INTERNAL:
    * Compare the attributes belonging to this mapping for the objects.
    */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        throw DescriptorException.invalidMappingOperation(this, "compareObjects");
    }

    /**
    * INTERNAL:
    * An object has been serialized from the server to the client.
    * Replace the transient attributes of the remote value holders
    * with client-side objects.
    */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        throw DescriptorException.invalidMappingOperation(this, "fixObjectReferences");
    }

    /**
     * INTERNAL:
     * Iterate on the appropriate attribute value.
     */
    public void iterate(DescriptorIterator iterator) {
        throw DescriptorException.invalidMappingOperation(this, "iterate");
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    @Override
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        throw DescriptorException.invalidMappingOperation(this, "mergeChangesIntoObject");
    }

    /**
    * INTERNAL:
    * Merge changes from the source to the target object.
    */
    @Override
    public void mergeIntoObject(Object target, boolean isTargetUninitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        throw DescriptorException.invalidMappingOperation(this, "mergeIntoObject");
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
       List<XMLEntry> values = ((DOMRecord)row).getValuesIndicatingNoEntry(this.getFields());
       Object container = getContainerPolicy().containerInstance(values.size());
       for(XMLEntry next:values) {
           Field valueField = next.getXMLField();
           DatabaseMapping nextMapping = (DatabaseMapping)this.choiceElementMappings.get(valueField);
           if(nextMapping.isAbstractCompositeCollectionMapping()) {
               XMLCompositeCollectionMapping xmlMapping = (XMLCompositeCollectionMapping)nextMapping;
               Object value = xmlMapping.buildObjectFromNestedRow((AbstractRecord)next.getValue(), joinManager, sourceQuery, executionSession, isTargetProtected);
               value = convertDataValueToObjectValue(value, executionSession, ((XMLRecord) row).getUnmarshaller());
               getContainerPolicy().addInto(value, container, executionSession);
           } else if(nextMapping instanceof XMLCompositeDirectCollectionMapping){
               XMLCompositeDirectCollectionMapping xmlMapping = (XMLCompositeDirectCollectionMapping)nextMapping;
               Object value = next.getValue();
               value = convertDataValueToObjectValue(value, executionSession, ((XMLRecord) row).getUnmarshaller());
               getContainerPolicy().addInto(value, container, executionSession);
           }
       }
       ArrayList<XMLMapping> processedMappings = new ArrayList<XMLMapping>();
       for(XMLMapping mapping:choiceElementMappings.values()) {
           if(((DatabaseMapping)mapping).isObjectReferenceMapping() && ((DatabaseMapping)mapping).isCollectionMapping() && !(processedMappings.contains(mapping))) {
        	   ((XMLCollectionReferenceMapping)mapping).readFromRowIntoObject(row, joinManager, ((XMLRecord)row).getCurrentObject(), cacheKey, sourceQuery, executionSession, isTargetProtected, container);
               processedMappings.add(mapping);
           }
       }
       return container;
    }

    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
        if(this.isReadOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(object);
        List<XMLEntry> nestedRows = new ArrayList<XMLEntry>();
        XMLRecord record = (XMLRecord)row;
        //First determine which Field is associated with each value:
        if(null != attributeValue) {
            ContainerPolicy cp = getContainerPolicy();
            Object iterator = cp.iteratorFor(attributeValue);
            if(null != iterator) {
                while(cp.hasNext(iterator)) {
                    Object value = cp.next(iterator, session);
                    value = convertObjectValueToDataValue(value, session, record.getMarshaller());
                    NodeValue associatedNodeValue = null;
                    XMLField associatedField = null;
                    Object fieldValue = value;
                    if(value instanceof XMLRoot) {
                        XMLRoot rootValue = (XMLRoot)value;
                        String localName = rootValue.getLocalName();
                        String namespaceUri = rootValue.getNamespaceURI();
                        fieldValue = rootValue.getObject();
                        associatedField = getFieldForName(localName, namespaceUri);
                        if(associatedField == null) {
                            associatedField = getClassToFieldMappings().get(fieldValue.getClass());
                        }
                    } else {
                        associatedField = getClassToFieldMappings().get(value.getClass());
                    }
                    if(associatedField == null) {
                        //this may be a reference mapping
                        List<XMLField> sourceFields = classToSourceFieldsMappings.get(value.getClass());
                        if(sourceFields != null && sourceFields.size() > 0) {
                            DatabaseMapping xmlMapping = (DatabaseMapping)this.choiceElementMappings.get(sourceFields.get(0));
                            for(XMLField next:sourceFields) {
                                fieldValue = ((XMLCollectionReferenceMapping)xmlMapping).buildFieldValue(value, next, session);
                                XMLEntry entry = new XMLEntry();
                                entry.setValue(fieldValue);
                                entry.setXMLField(next);
                                nestedRows.add(entry);
                            }
                        }
                    } else {
                        DatabaseMapping xmlMapping = (DatabaseMapping)this.choiceElementMappings.get(associatedField);
                        if(xmlMapping.isAbstractCompositeCollectionMapping()) {
                            fieldValue = ((XMLCompositeCollectionMapping)xmlMapping).buildCompositeRow(fieldValue, session, row, writeType);
                        }
                    
                        XMLEntry entry = new XMLEntry();
                        entry.setValue(fieldValue);
                        entry.setXMLField(associatedField);
                        nestedRows.add(entry);
                    }
                }
            }
        }
        ((DOMRecord)row).put(getFields(), nestedRows);
    }

    private XMLField getFieldForName(String localName, String namespaceUri) {
        Iterator fields = getFields().iterator(); 
        while(fields.hasNext()) {
            XMLField nextField = (XMLField)fields.next();
            XPathFragment fragment = nextField.getXPathFragment();
            while(fragment != null && (!fragment.nameIsText())) {
                if(fragment.getNextFragment() == null || fragment.getHasText()) {
                    if(fragment.getLocalName().equals(localName)) {
                        String fragUri = fragment.getNamespaceURI();
                        if((namespaceUri == null && fragUri == null) || (namespaceUri != null && fragUri != null && namespaceUri.equals(fragUri))) {
                            return nextField;
                        }
                    }
                }
                fragment = fragment.getNextFragment();
            }
        }
        return null;
    }    
    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {

    }

    public boolean isXMLMapping() {
        return true;
    }

    public Vector<DatabaseField> getFields() {
        if(fields == null || fields.size() == 0) {
            fields = this.collectFields();
        }
        return this.fields;
    }

    protected Vector<DatabaseField> collectFields() {
        return new Vector<DatabaseField>(fieldToClassMappings.keySet());
    }

    public void addChoiceElement(String xpath, Class elementType) {
        XMLField field = new XMLField(xpath);
        addChoiceElement(field, elementType);
    }
    
    public void addChoiceElement(String xpath, String elementTypeName) {
        XMLField field = new XMLField(xpath);
        addChoiceElement(field, elementTypeName);
    }

    
    public void addChoiceElement(XMLField xmlField, Class elementType) {
        getFieldToClassMappings().put(xmlField, elementType);
        if(!(this.fieldToClassNameMappings.containsKey(xmlField))) {
            this.fieldToClassNameMappings.put(xmlField, elementType.getName());
        }
        if (classToFieldMappings.get(elementType) == null) {
            classToFieldMappings.put(elementType, xmlField);
        }
        addChoiceElementMapping(xmlField, elementType);
    }
    
    public void addChoiceElement(List<XMLField> srcFields, Class elementType, List<XMLField> tgtFields) {
        for(XMLField sourceField:srcFields) {
            getFieldToClassMappings().put(sourceField, elementType);
            this.fieldToClassNameMappings.put(sourceField, elementType.getName());
        }
        if (getClassToSourceFieldsMappings().get(elementType) == null) {
            getClassToSourceFieldsMappings().put(elementType, srcFields);
        }
        addChoiceElementMapping(srcFields, elementType, tgtFields);
    }
    
    public void addChoiceElement(List<XMLField> srcFields, String elementTypeName, List<XMLField> tgtFields) {
        for(XMLField sourceField:srcFields) {
            this.fieldToClassNameMappings.put(sourceField, elementTypeName);
        }
        if (getClassNameToSourceFieldsMappings().get(elementTypeName) == null) {
            getClassNameToSourceFieldsMappings().put(elementTypeName, srcFields);
        }
        addChoiceElementMapping(srcFields, elementTypeName, tgtFields);
    }
    
    public void addChoiceElement(String srcXPath, Class elementType, String tgtXPath) {
        XMLField srcField = new XMLField(srcXPath);
        XMLField tgtField = new XMLField(tgtXPath);
        addChoiceElement(srcField, elementType, tgtField);
    }

    public void addChoiceElement(String srcXpath, String elementTypeName, String tgtXpath) {
        XMLField field = new XMLField(srcXpath);
        XMLField tgtField = new XMLField(tgtXpath);
        this.fieldToClassNameMappings.put(field, elementTypeName);
        if(this.classNameToFieldMappings.get(elementTypeName) == null) {
            this.classNameToFieldMappings.put(elementTypeName, field);
        }
        addChoiceElementMapping(field, elementTypeName, tgtField);        
    }
    
    public void addChoiceElement(XMLField sourceField, Class elementType, XMLField targetField) {
        getFieldToClassMappings().put(sourceField, elementType);
        this.fieldToClassNameMappings.put(sourceField, elementType.getName());
        if (classToFieldMappings.get(elementType) == null) {
            classToFieldMappings.put(elementType, sourceField);
        }
        addChoiceElementMapping(sourceField, elementType, targetField);
    }
    
    private void addChoiceElementMapping(List<XMLField> sourceFields, Class theClass, List<XMLField> targetFields) {
        XMLCollectionReferenceMapping xmlMapping = new XMLCollectionReferenceMapping();
        xmlMapping.setReferenceClass(theClass);
        xmlMapping.setAttributeAccessor(temporaryAccessor);
        for(int i = 0; i < sourceFields.size(); i++) {
            XMLField sourceField = sourceFields.get(i);
            xmlMapping.addSourceToTargetKeyFieldAssociation(sourceField, targetFields.get(i));
            this.choiceElementMappings.put(sourceField, xmlMapping);
        }
        this.choiceElementMappingsByClass.put(theClass, xmlMapping);
    }

    private void addChoiceElementMapping(List<XMLField> sourceFields, String theClass, List<XMLField> targetFields) {
        XMLCollectionReferenceMapping xmlMapping = new XMLCollectionReferenceMapping();
        xmlMapping.setReferenceClassName(theClass);
        xmlMapping.setAttributeAccessor(temporaryAccessor);
        for(int i = 0; i < sourceFields.size(); i++) {
            XMLField sourceField = sourceFields.get(i);
            xmlMapping.addSourceToTargetKeyFieldAssociation(sourceField, targetFields.get(i));
            this.choiceElementMappings.put(sourceField, xmlMapping);
        }
        this.choiceElementMappingsByClassName.put(theClass, xmlMapping);
    } 
    
    private void addChoiceElementMapping(XMLField sourceField, Class theClass, XMLField targetField) {
        XMLCollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        mapping.setReferenceClass(theClass);
        mapping.setAttributeAccessor(temporaryAccessor);
        mapping.addSourceToTargetKeyFieldAssociation(sourceField, targetField);
        this.choiceElementMappings.put(sourceField, mapping);
        this.choiceElementMappingsByClass.put(theClass, mapping);
    }
    
    private void addChoiceElementMapping(XMLField sourceField, String className, XMLField targetField) {
        XMLCollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        mapping.setReferenceClassName(className);
        mapping.setAttributeAccessor(temporaryAccessor);
        mapping.addSourceToTargetKeyFieldAssociation(sourceField, targetField);
        this.choiceElementMappings.put(sourceField, mapping);
        this.choiceElementMappingsByClassName.put(className, mapping);
    }
    
    public void addChoiceElement(XMLField field, String elementTypeName) {
        this.fieldToClassNameMappings.put(field, elementTypeName);
        if (classNameToFieldMappings.get(elementTypeName) == null) {
            classNameToFieldMappings.put(elementTypeName, field);
        }        
        addChoiceElementMapping(field, elementTypeName);
    }
    
    
    public Map<XMLField, Class> getFieldToClassMappings() {
        return fieldToClassMappings;
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        if (this.converter != null) {
            this.converter.initialize(this, session);
        }
        ArrayList<XMLMapping> mappingsList = new ArrayList<XMLMapping>();
        mappingsList.addAll(getChoiceElementMappings().values());
        for(XMLMapping next:getChoiceElementMappingsByClass().values()) {
            if(!(mappingsList.contains(next))) {
                mappingsList.add(next);
            }
        }        
        Iterator<XMLMapping> mappings = mappingsList.iterator();
        while(mappings.hasNext()){
            DatabaseMapping nextMapping = (DatabaseMapping)mappings.next();
            Converter converter = null;
            if(fieldsToConverters != null) {
                converter = fieldsToConverters.get(nextMapping.getField());
            }
            if(nextMapping.isAbstractCompositeDirectCollectionMapping()){
                XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();                   
                QName schemaType = (QName)xmlConversionManager.getDefaultJavaTypes().get(((AbstractCompositeDirectCollectionMapping)nextMapping).getAttributeElementClass());
                if(schemaType != null) {
                 ((XMLField)nextMapping.getField()).setSchemaType(schemaType);
                }   
                
                if(converter != null){
                    ((AbstractCompositeDirectCollectionMapping)nextMapping).setValueConverter(converter);
                }
                ((AbstractCompositeDirectCollectionMapping)nextMapping).setContainerPolicy(getContainerPolicy());                                               
            }else if(nextMapping.isAbstractCompositeCollectionMapping()){
                if(converter != null){
                    ((AbstractCompositeCollectionMapping)nextMapping).setConverter(converter);
                }
                ((AbstractCompositeCollectionMapping)nextMapping).setContainerPolicy(getContainerPolicy());
            } else if(nextMapping instanceof XMLBinaryDataCollectionMapping) {
                ((XMLBinaryDataCollectionMapping)nextMapping).setContainerPolicy(getContainerPolicy());
                if(converter != null) {
                    ((XMLBinaryDataCollectionMapping)nextMapping).setValueConverter(converter);
                }
            } else {
                ((XMLCollectionReferenceMapping)nextMapping).setContainerPolicy(getContainerPolicy());
                ((XMLCollectionReferenceMapping)nextMapping).setReuseContainer(true);
            }
            
            nextMapping.initialize(session);
        }        
    }

    public Map<Class, XMLField> getClassToFieldMappings() {
        return classToFieldMappings;
    }

    public Map<XMLField, XMLMapping> getChoiceElementMappings() {
        return choiceElementMappings;
    }

    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    public void setContainerPolicy(ContainerPolicy cp) {
        this.containerPolicy = cp;
    }

    public void useCollectionClass(Class concreteContainerClass) {
        this.setContainerPolicy(ContainerPolicy.buildPolicyFor(concreteContainerClass));
    }

    public void useCollectionClassName(String concreteContainerClassName) {
        this.setContainerPolicy(new CollectionContainerPolicy(concreteContainerClassName));
    }

    public void convertClassNamesToClasses(ClassLoader classLoader) {
    	Iterator<Entry<XMLField, String>> entries = fieldToClassNameMappings.entrySet().iterator();
    	while (entries.hasNext()) {
            Map.Entry<XMLField, String> entry = entries.next();
            String className = entry.getValue();
            Class elementType = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        elementType = (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(className, exception.getException());
                    }
                } else {
                    elementType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, classLoader);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(className, exc);
            }
            XMLMapping mapping = this.choiceElementMappings.get(entry.getKey());
            mapping.convertClassNamesToClasses(classLoader);

            if(fieldToClassMappings.get(entry.getKey()) == null) {
                fieldToClassMappings.put(entry.getKey(), elementType);
            }            
        }
        for(Entry<String, XMLField> next: this.classNameToFieldMappings.entrySet()) {
            String className = next.getKey();
            Class elementType = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        elementType = (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(className, exception.getException());
                    }
                } else {
                    elementType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, classLoader);
                }
            } catch (ClassNotFoundException exc) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(className, exc);
            }
            classToFieldMappings.put(elementType, next.getValue());
        }    	
        if(classNameToSourceFieldsMappings != null) {
            Iterator<Entry<String, List<XMLField>>> sourceFieldEntries = classNameToSourceFieldsMappings.entrySet().iterator();
            while(sourceFieldEntries.hasNext()) {
                Entry<String, List<XMLField>> nextEntry = sourceFieldEntries.next();
                String className = nextEntry.getKey();
                List<XMLField> fields = nextEntry.getValue();
                Class elementType = null;
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        try {
                            elementType = (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                        } catch (PrivilegedActionException exception) {
                            throw ValidationException.classNotFoundWhileConvertingClassNames(className, exception.getException());
                        }
                    } else {
                        elementType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, classLoader);
                    }
                } catch (ClassNotFoundException exc) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(className, exc);
                }
                this.getClassToSourceFieldsMappings().put(elementType,fields);
            }
        }
        if(!choiceElementMappingsByClassName.isEmpty()) {
            for(Entry<String, XMLMapping> next:choiceElementMappingsByClassName.entrySet()) {
                Class elementType = null;
                String className = next.getKey();
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        try {
                            elementType = (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                        } catch (PrivilegedActionException exception) {
                            throw ValidationException.classNotFoundWhileConvertingClassNames(className, exception.getException());
                        }
                    } else {
                        elementType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, classLoader);
                    }
                } catch (ClassNotFoundException exc) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(className, exc);
                }
                if(this.choiceElementMappingsByClass.get(elementType) == null) {
                    this.choiceElementMappingsByClass.put(elementType, next.getValue());
                }
                next.getValue().convertClassNamesToClasses(classLoader);
            }
        }
    }
    
    public void addConverter(XMLField field, Converter converter) {
        if(this.fieldsToConverters == null) {
            fieldsToConverters = new HashMap<XMLField, Converter>();
        }
        fieldsToConverters.put(field, converter);
    }

    public Converter getConverter(XMLField field) {
        if(null != this.fieldsToConverters) {
            Converter converter = fieldsToConverters.get(field);
            if(null != converter) {
                return converter;
            }
            if(null != this.choiceElementMappings) {
                DatabaseMapping mapping = (DatabaseMapping) this.choiceElementMappings.get(field);
                if(null == mapping) {
                    return null;
                }
                if(mapping.isAbstractCompositeDirectCollectionMapping()) {
                    return ((XMLCompositeDirectCollectionMapping)mapping).getValueConverter();
                } else if(mapping.isAbstractDirectMapping()) {
                    return ((XMLDirectMapping)mapping).getConverter();
                }
            }
        }
        return null;
    }

    public ArrayList getChoiceFieldToClassAssociations() {
        ArrayList associations = new ArrayList();
        if(this.fieldToClassNameMappings.size() > 0) {
        	
        	Set<Entry<XMLField, String>> entries = fieldToClassNameMappings.entrySet();
        	Iterator<Entry<XMLField, String>> iter = entries.iterator();
        	while(iter.hasNext()){
        		Entry<XMLField, String> nextEntry = iter.next();
        		XMLField xmlField = nextEntry.getKey();        	            
                String className = nextEntry.getValue();        	            
                XMLChoiceFieldToClassAssociation association = new XMLChoiceFieldToClassAssociation(xmlField, className);
                associations.add(association);
            }
        }
        return associations;
    }
    

    public void setChoiceFieldToClassAssociations(ArrayList associations) {
        if(associations.size() > 0) {
            for(Object next:associations) {
                XMLChoiceFieldToClassAssociation<XMLField> association = (XMLChoiceFieldToClassAssociation)next;
                this.addChoiceElement(association.getXmlField(), association.getClassName());
                if(association.getConverter() != null) {
                    this.addConverter(association.getXmlField(), association.getConverter());
                }
            }
        }
    }    
    
    private void addChoiceElementMapping(XMLField xmlField, String className){              
         if (xmlField.getLastXPathFragment().nameIsText() || xmlField.getLastXPathFragment().isAttribute()) {
             XMLCompositeDirectCollectionMapping xmlMapping = new XMLCompositeDirectCollectionMapping();             
             xmlMapping.setAttributeElementClassName(className);             
             xmlMapping.setField(xmlField);             
             xmlMapping.setAttributeAccessor(temporaryAccessor);
             this.choiceElementMappings.put(xmlField, xmlMapping);
             this.choiceElementMappingsByClassName.put(className, xmlMapping);
         } else {
             if(isBinaryType(className)) {
                 XMLBinaryDataCollectionMapping xmlMapping = new XMLBinaryDataCollectionMapping();
                 xmlMapping.setField(xmlField);
                 xmlMapping.setAttributeAccessor(temporaryAccessor);
                 Class theClass = XMLConversionManager.getDefaultXMLManager().convertClassNameToClass(className);
                 xmlMapping.setAttributeElementClass(theClass);
                 this.fieldsToConverters.put(xmlField, xmlMapping.getValueConverter());
                 this.choiceElementMappings.put(xmlField, xmlMapping);
                 this.choiceElementMappingsByClassName.put(className, xmlMapping);
             } else {
                 XMLCompositeCollectionMapping xmlMapping = new XMLCompositeCollectionMapping();  
                 if(!className.equals("java.lang.Object")){
                     xmlMapping.setReferenceClassName(className);
                 }      
                 xmlMapping.setField(xmlField);
                 xmlMapping.setAttributeAccessor(temporaryAccessor);
                 this.choiceElementMappings.put(xmlField, xmlMapping);
                 this.choiceElementMappingsByClassName.put(className, xmlMapping);
             }
         }        
    }    
    
    private void addChoiceElementMapping(XMLField xmlField, Class theClass){
    
        if (xmlField.getLastXPathFragment().nameIsText() || xmlField.getLastXPathFragment().isAttribute()) {
            XMLCompositeDirectCollectionMapping xmlMapping = new XMLCompositeDirectCollectionMapping();            
            xmlMapping.setAttributeElementClass(theClass);
            xmlMapping.setField(xmlField);
            xmlMapping.setAttributeAccessor(temporaryAccessor);
            this.choiceElementMappings.put(xmlField, xmlMapping);
            this.choiceElementMappingsByClass.put(theClass, xmlMapping);
        } else {
            if(isBinaryType(theClass)) {
                XMLBinaryDataCollectionMapping xmlMapping = new XMLBinaryDataCollectionMapping();
                xmlMapping.setField(xmlField);
                xmlMapping.setAttributeElementClass(theClass);
                xmlMapping.setAttributeAccessor(temporaryAccessor);
                this.fieldsToConverters.put(xmlField, xmlMapping.getValueConverter());
                this.choiceElementMappings.put(xmlField, xmlMapping);
                this.choiceElementMappingsByClass.put(theClass, xmlMapping);
            } else {
                XMLCompositeCollectionMapping xmlMapping = new XMLCompositeCollectionMapping();            
                if(!theClass.equals(ClassConstants.OBJECT)){
                    xmlMapping.setReferenceClass(theClass);
                }
                xmlMapping.setField(xmlField);
                xmlMapping.setAttributeAccessor(temporaryAccessor);
                this.choiceElementMappings.put(xmlField, xmlMapping);
                this.choiceElementMappingsByClass.put(theClass, xmlMapping);
            }
        }        
    }
    
    public boolean isWriteOnly() {
        return this.isWriteOnly;
    }
    
    public void setIsWriteOnly(boolean b) {
        this.isWriteOnly = b;
    }
    
    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
        //Collection<XMLMapping> allMappings = new ArrayList<XMLMapping>();
        ArrayList<XMLMapping> mappingsList = new ArrayList<XMLMapping>();
        mappingsList.addAll(getChoiceElementMappings().values());
        for(XMLMapping next:getChoiceElementMappingsByClass().values()) {
            if(!(mappingsList.contains(next))) {
                mappingsList.add(next);
            }
        } 
        for(XMLMapping next:getChoiceElementMappingsByClass().values()) {
            if(!(mappingsList.contains(next))) {
                mappingsList.add(next);
            }
        }
        Iterator<XMLMapping> mappings = mappingsList.iterator();
        while(mappings.hasNext()){
            DatabaseMapping nextMapping = (DatabaseMapping)mappings.next();
            nextMapping.setAttributeName(this.getAttributeName());         
            if(nextMapping.getAttributeAccessor() == temporaryAccessor){
            	nextMapping.setAttributeAccessor(getAttributeAccessor());
            }
            nextMapping.setIsReadOnly(this.isReadOnly());
            ((XMLMapping)nextMapping).setIsWriteOnly(this.isWriteOnly());
            
            nextMapping.setDescriptor(getDescriptor());
            
            nextMapping.preInitialize(session);
        }
    }
    
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        if(isWriteOnly()) {
            return;
        }
        super.setAttributeValueInObject(object, value);
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
    
    public Map<Class, List<XMLField>> getClassToSourceFieldsMappings() {
        if(this.classToSourceFieldsMappings == null) {
            this.classToSourceFieldsMappings = new HashMap<Class, List<XMLField>>();
        }
        return this.classToSourceFieldsMappings;
    }
    
    private Map<String, List<XMLField>> getClassNameToSourceFieldsMappings() {
        if(this.classNameToSourceFieldsMappings == null) {
            this.classNameToSourceFieldsMappings = new HashMap<String, List<XMLField>>();
        }
        return this.classNameToSourceFieldsMappings;
    } 
    
    private boolean isBinaryType(String className) {
        if(className.equals(byte[].class.getName()) || className.equals(Byte[].class.getName()) || className.equals(DATA_HANDLER)
                || className.equals(IMAGE) || className.equals(MIME_MULTIPART)) {
            return true;
        }
        return false;
    }
    
    private boolean isBinaryType(Class theClass) {
        String className = theClass.getName();
        if(className.equals(byte[].class.getName()) || className.equals(Byte[].class.getName()) || className.equals(DATA_HANDLER)
                || className.equals(IMAGE) || className.equals(MIME_MULTIPART)) {
            return true;
        }
        return false;
    }

    public Map<String, XMLField> getClassNameToFieldMappings() {
        return classNameToFieldMappings;
    } 
    
    public boolean isMixedContent() {
        return this.mixedContentMapping != null;
    }
    
    /**
     * PUBLIC:
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to 
     * any of the elements in the choice. The grouping element parameter is used in the case that there is
     * a common grouping element to all the other elements in this choice. If so, that grouping element can
     * be specified here to allow the mixed content to be written/detected inside the wrapper element.
     * @since EclipseLink 2.3.1
     */
    public void setMixedContent(String groupingElement) {
        String xpath = groupingElement;
        if(groupingElement.length() == 0) {
            xpath = XMLConstants.TEXT;
        } else {
            xpath += "/" + XMLConstants.TEXT;
        }
        XMLField field = new XMLField(xpath);
        XMLCompositeDirectCollectionMapping xmlMapping = new XMLCompositeDirectCollectionMapping();             
        Class theClass = ClassConstants.STRING;
        xmlMapping.setAttributeElementClass(theClass);             
        xmlMapping.setField(field);             
        xmlMapping.setAttributeAccessor(temporaryAccessor);
        this.mixedContentMapping = xmlMapping;
        this.choiceElementMappings.put(field, xmlMapping);
    }
    
    /**
     * PUBLIC:
     * Allows the user to indicate that this mapping should also allow for mixed content in addition to 
     * any of the elements in the choice. 
     * @since EclipseLink 2.3.1
     */
    public void setMixedContent(boolean mixed) {
        if(!mixed) {
            this.mixedContentMapping = null;
        } else {
            setMixedContent("");
        }
    }
    
    public XMLCompositeDirectCollectionMapping getMixedContentMapping() {
        return this.mixedContentMapping;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return isDefaultEmptyContainer;
    }

    /**
     * INTERNAL
     * Indicate whether by default an empty container should be set on the 
     * field/property if the collection is not present in the XML document.
     * @since EclipseLink 2.3.3
     */
    public void setDefaultEmptyContainer(boolean defaultEmptyContainer) {
        this.isDefaultEmptyContainer = defaultEmptyContainer;
    }

    public AbstractNullPolicy getWrapperNullPolicy() {
        return this.wrapperNullPolicy;
    }

    public void setWrapperNullPolicy(AbstractNullPolicy policy) {
        this.wrapperNullPolicy = policy;
    }
    
    public Map<Class, XMLMapping> getChoiceElementMappingsByClass() {
        return choiceElementMappingsByClass;
    }

    public void setChoiceElementMappingsByClass(Map<Class, XMLMapping> choiceElementMappingsByClass) {
        this.choiceElementMappingsByClass = choiceElementMappingsByClass;
    }      

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertObjectValueToDataValue(Object value, Session session, XMLMarshaller marshaller) {
        if (null != converter) {
            if (converter instanceof XMLConverter) {
                return ((XMLConverter)converter).convertObjectValueToDataValue(value, session, marshaller);
            } else {
                return converter.convertObjectValueToDataValue(value, session);
            }
        }
        return value;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session, XMLUnmarshaller unmarshaller) {
        if (null != converter) {
            if (converter instanceof XMLConverter) {
                return ((XMLConverter)converter).convertDataValueToObjectValue(fieldValue, session, unmarshaller);
            } else {
                return converter.convertDataValueToObjectValue(fieldValue, session);
            }
        }
        return fieldValue;
    }

}
