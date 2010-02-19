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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
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
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.XMLChoiceFieldToClassAssociation;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLEntry;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.internal.queries.ContainerPolicy;

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

public class XMLChoiceCollectionMapping extends DatabaseMapping implements XMLMapping {
    private Map<XMLField, Class> fieldToClassMappings;
    private Map<Class, XMLField> classToFieldMappings;
    private Map<XMLField, XMLMapping> choiceElementMappings;
    private Map<XMLField, String> fieldToClassNameMappings;
    private Map<XMLField, Converter> fieldsToConverters;
    private ContainerPolicy containerPolicy;
    private boolean isWriteOnly;
    private static final AttributeAccessor temporaryAccessor = new InstanceVariableAttributeAccessor();;
    private boolean reuseContainer;
    private Converter converter;

    public XMLChoiceCollectionMapping() {
        fieldToClassMappings = new HashMap<XMLField, Class>();
        fieldToClassNameMappings = new HashMap<XMLField, String>();
        classToFieldMappings = new HashMap<Class, XMLField>();
        choiceElementMappings = new HashMap<XMLField, XMLMapping>();
        fieldsToConverters = new HashMap<XMLField, Converter>();
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
    public void buildClone(Object original, Object clone, UnitOfWorkImpl unitOfWork) {
        throw DescriptorException.invalidMappingOperation(this, "buildClone");
    }

    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
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
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
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
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        throw DescriptorException.invalidMappingOperation(this, "mergeChangesIntoObject");
    }

    /**
    * INTERNAL:
    * Merge changes from the source to the target object.
    */
    public void mergeIntoObject(Object target, boolean isTargetUninitialized, Object source, MergeManager mergeManager) {
        throw DescriptorException.invalidMappingOperation(this, "mergeIntoObject");
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
       List<XMLEntry> values = ((DOMRecord)row).getValuesIndicatingNoEntry(this.getFields());
       Object container = getContainerPolicy().containerInstance(values.size());
       for(XMLEntry next:values) {
           XMLField valueField = next.getXMLField();
           DatabaseMapping nextMapping = (DatabaseMapping)this.choiceElementMappings.get(valueField);
           Converter converter = getConverter();
           if(nextMapping.isAbstractCompositeCollectionMapping()) {
               XMLCompositeCollectionMapping xmlMapping = (XMLCompositeCollectionMapping)nextMapping;
               Object value = xmlMapping.buildObjectFromNestedRow((AbstractRecord)next.getValue(), joinManager, sourceQuery, executionSession);
               if(converter != null) {
                   if (converter instanceof XMLConverter) {
                       value = ((XMLConverter) converter).convertDataValueToObjectValue(value, executionSession, ((XMLRecord) row).getUnmarshaller());
                   } else {
                       value = converter.convertDataValueToObjectValue(value, executionSession);
                   }
               }
               getContainerPolicy().addInto(value, container, executionSession);
           } else {
               XMLCompositeDirectCollectionMapping xmlMapping = (XMLCompositeDirectCollectionMapping)nextMapping;
               Object value = next.getValue();
               if(converter != null) {
                   if (converter instanceof XMLConverter) {
                       value = ((XMLConverter) converter).convertDataValueToObjectValue(value, executionSession, ((XMLRecord) row).getUnmarshaller());
                   } else {
                       value = converter.convertDataValueToObjectValue(value, executionSession);
                   }
               }
               getContainerPolicy().addInto(value, container, executionSession);
           }
       }
       return container;
    }

    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
        Object attributeValue = getAttributeValueFromObject(object);
        List<XMLEntry> nestedRows = new ArrayList<XMLEntry>();
        XMLRecord record = (XMLRecord)row;
        //First determine which Field is associated with each value:
        ContainerPolicy cp = getContainerPolicy();
        Object iterator = cp.iteratorFor(attributeValue);
        while(cp.hasNext(iterator)) {
            Object value = cp.next(iterator, session);
            if (null != converter) {
                if (converter instanceof XMLConverter) {
                    value = ((XMLConverter)converter).convertObjectValueToDataValue(value, session, record.getMarshaller());
                } else {
                    value = converter.convertObjectValueToDataValue(value, session);
                }
            }
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
            DatabaseMapping xmlMapping = (DatabaseMapping)this.choiceElementMappings.get(associatedField);
            if(xmlMapping.isAbstractCompositeCollectionMapping()) {
                fieldValue = ((XMLCompositeCollectionMapping)xmlMapping).buildCompositeRow(fieldValue, session, row, writeType);
            }
            XMLEntry entry = new XMLEntry();
            entry.setValue(fieldValue);
            entry.setXMLField(associatedField);
            nestedRows.add(entry);
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
    
    public void addChoiceElement(XMLField field, String elementTypeName) {
        this.fieldToClassNameMappings.put(field, elementTypeName);
        addChoiceElementMapping(field, elementTypeName);
    }
    
    
    public Map<XMLField, Class> getFieldToClassMappings() {
        return fieldToClassMappings;
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.fieldToClassMappings.size() == 0) {
            this.convertClassNamesToClasses(((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getLoader());
        }
        
        Iterator<XMLMapping> mappings = getChoiceElementMappings().values().iterator();
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
            }else{
                if(converter != null){
                    ((AbstractCompositeCollectionMapping)nextMapping).setConverter(converter);
                }
                ((AbstractCompositeCollectionMapping)nextMapping).setContainerPolicy(getContainerPolicy());
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
            if (classToFieldMappings.get(elementType) == null) {
                classToFieldMappings.put(elementType, entry.getKey());
            }
            if(fieldToClassMappings.get(entry.getKey()) == null) {
                fieldToClassMappings.put(entry.getKey(), elementType);
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
            for(XMLField xmlField:this.fieldToClassNameMappings.keySet()) {
                String className = this.fieldToClassNameMappings.get(xmlField);
                XMLChoiceFieldToClassAssociation association = new XMLChoiceFieldToClassAssociation(xmlField, className);
                associations.add(association);
            }
        }
        return associations;
    }
    

    public void setChoiceFieldToClassAssociations(ArrayList associations) {
        if(associations.size() > 0) {
            for(Object next:associations) {
                XMLChoiceFieldToClassAssociation association = (XMLChoiceFieldToClassAssociation)next;
                this.addChoiceElement(association.getXmlField(), association.getClassName());
                if(association.getConverter() != null) {
                    this.addConverter(association.getXmlField(), association.getConverter());
                }
            }
        }
    }    
    
    private void addChoiceElementMapping(XMLField xmlField, String className){              
         if (xmlField.getLastXPathFragment().nameIsText()) {
             XMLCompositeDirectCollectionMapping xmlMapping = new XMLCompositeDirectCollectionMapping();             
             Class theClass = XMLConversionManager.getDefaultXMLManager().convertClassNameToClass(className);
             xmlMapping.setAttributeElementClass(theClass);             
             xmlMapping.setField(xmlField);             
             xmlMapping.setAttributeAccessor(temporaryAccessor);
             this.choiceElementMappings.put(xmlField, xmlMapping);                
         } else {
             XMLCompositeCollectionMapping xmlMapping = new XMLCompositeCollectionMapping();  
             if(!className.equals("java.lang.Object")){
                xmlMapping.setReferenceClassName(className);
             }      
             xmlMapping.setField(xmlField);
             xmlMapping.setAttributeAccessor(temporaryAccessor);
             this.choiceElementMappings.put(xmlField, xmlMapping);                
         }        
    }    
    
    private void addChoiceElementMapping(XMLField xmlField, Class theClass){
    
        if (xmlField.getLastXPathFragment().nameIsText()) {
            XMLCompositeDirectCollectionMapping xmlMapping = new XMLCompositeDirectCollectionMapping();            
            xmlMapping.setAttributeElementClass(theClass);
            xmlMapping.setField(xmlField);
            xmlMapping.setAttributeAccessor(temporaryAccessor);
            this.choiceElementMappings.put(xmlField, xmlMapping);                
        } else {
            XMLCompositeCollectionMapping xmlMapping = new XMLCompositeCollectionMapping();            
            if(!theClass.equals(ClassConstants.OBJECT)){
                xmlMapping.setReferenceClass(theClass);
            }
            xmlMapping.setField(xmlField);
            xmlMapping.setAttributeAccessor(temporaryAccessor);
            this.choiceElementMappings.put(xmlField, xmlMapping);                
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
        Iterator<XMLMapping> mappings = getChoiceElementMappings().values().iterator();
        while(mappings.hasNext()){
            DatabaseMapping nextMapping = (DatabaseMapping)mappings.next();
            nextMapping.setAttributeName(this.getAttributeName());         
            if(nextMapping.getAttributeAccessor() == temporaryAccessor){
            	nextMapping.setAttributeAccessor(getAttributeAccessor());
            }
            
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

}
