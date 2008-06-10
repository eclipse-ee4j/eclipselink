/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.converters.XMLRootConverter;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.oxm.XMLField;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 * PUBLIC:
 * <p><b>Purpose:</b>Provide a mapping that can map a single attribute to a number of
 * different elements in an XML Document. This will be used to map to Choices or Substitution
 * Groups in an XML Schema
 * <p><b>Responsibilities:</b><ul>
 * <li>Allow the user to specify XPath -> Type mappings</li>
 * <li>Handle reading and writing of XML Documents containing a single choice or substitution
 * group element</li>
 * </ul>
 * <p>The XMLChoiceMapping allows the user to specify a number of different xpaths, and types associated with those xpaths. 
 * When any of these elements are encountered in the XML Document, they are read in as the correct
 * type and set in the object.
 * <p><b>Setting up XPath mappings:</b>Unlike other OXM Mappings, instead of setting a single xpath,
 * the addChoiceElement method is used to specify an xpath and the type associated with this xpath.
 * <br>
 * xmlChoiceCollectionMapping.addChoiceElement("mystring/text()", String.class);
 * <br>
 * xmlChoiceCollectionMapping.addChoiceElement("myaddress", Address.class);
 * 
 */

public class XMLChoiceObjectMapping extends DatabaseMapping implements XMLMapping {
    private Map<XMLField, Class> fieldToClassMappings;
    private Map<Class, XMLField> classToFieldMappings;
    private Map<XMLField, String> fieldToClassNameMappings;
    private Map<XMLField, XMLMapping> choiceElementMappings;
    private Map<XMLField, XMLConverter> fieldsToConverters;

    public XMLChoiceObjectMapping() {
        fieldToClassMappings = new HashMap<XMLField, Class>();
        fieldToClassNameMappings = new HashMap<XMLField, String>();
        classToFieldMappings = new HashMap<Class, XMLField>();
        choiceElementMappings = new HashMap<XMLField, XMLMapping>();
        fieldsToConverters = new HashMap<XMLField, XMLConverter>();

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
        return null;
    }

    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session) throws DescriptorException {

    }

    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {

    }

    public boolean isXMLMapping() {
        return true;
    }

    public Vector<DatabaseField> getFields() {
        return this.collectFields();
    }

    protected Vector<DatabaseField> collectFields() {
        Vector<DatabaseField> fields = new Vector<DatabaseField>(getFieldToClassMappings().keySet());
        return fields;
    }

    public void addChoiceElement(String xpath, Class elementType) {
        XMLField field = new XMLField(xpath);
        getFieldToClassMappings().put(field, elementType);
        if (classToFieldMappings.get(elementType) == null) {
            classToFieldMappings.put(elementType, field);
        }
    }

    public void addChoiceElement(String xpath, String elementTypeName, boolean xmlRoot) {
    	XMLField field = new XMLField(xpath);
        this.fieldToClassNameMappings.put(field, elementTypeName);
        if(xmlRoot) {
        	this.fieldsToConverters.put(field, new XMLRootConverter(field));
        }
    }
    
    public void addChoiceElement(String xpath, String elementTypeName) {
    	addChoiceElement(xpath, elementTypeName, false);
    }

    public void addChoiceElement(XMLField xmlField, Class elementType) {
    	getFieldToClassMappings().put(xmlField, elementType);
        if (classToFieldMappings.get(elementType) == null) {
            classToFieldMappings.put(elementType, xmlField);
        }
    }
    
    public void addChoiceElement(XMLField field, String elementTypeName) {
    	this.fieldToClassNameMappings.put(field, elementTypeName);
    }
    
    public Map<XMLField, Class> getFieldToClassMappings() {
        return fieldToClassMappings;
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.fieldToClassMappings.size() == 0) {
            this.convertClassNamesToClasses(((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getLoader());
        }

        //create mappings for each field.
        Iterator<XMLField> fields = getFieldToClassMappings().keySet().iterator();
        while (fields.hasNext()) {
            XMLField next = fields.next();
            XMLConverter converter = null;
            if(fieldsToConverters != null) {
            	converter = fieldsToConverters.get(next);
            }
            if (next.getLastXPathFragment().nameIsText()) {
                //if it's a simple value, create a Direct Mapping
                XMLDirectMapping xmlMapping = new XMLDirectMapping();
                xmlMapping.setAttributeName(this.getAttributeName());
                xmlMapping.setAttributeAccessor(this.getAttributeAccessor());
                xmlMapping.setAttributeClassification(getFieldToClassMappings().get(next));
                XMLConversionManager xmlConversionManager = (XMLConversionManager) session.getDatasourcePlatform().getConversionManager();
                QName schemaType = (QName)xmlConversionManager.getDefaultJavaTypes().get(xmlMapping.getAttributeClassification());
                if(schemaType != null) {
                	next.setSchemaType(schemaType);
                }
                xmlMapping.setField(next);
                xmlMapping.setDescriptor(this.getDescriptor());
                if(converter != null) {
                	xmlMapping.setConverter(converter);
                }
                this.choiceElementMappings.put(next, xmlMapping);
                xmlMapping.initialize(session);
            } else {
                XMLCompositeObjectMapping xmlMapping = new XMLCompositeObjectMapping();
                xmlMapping.setAttributeName(this.getAttributeName());
                xmlMapping.setAttributeAccessor(this.getAttributeAccessor());
                xmlMapping.setReferenceClass(getFieldToClassMappings().get(next));
                xmlMapping.setField(next);
                xmlMapping.setDescriptor(this.getDescriptor());
                if(converter != null) {
                	xmlMapping.setConverter(converter);
                }
                this.choiceElementMappings.put(next, xmlMapping);
                xmlMapping.initialize(session);
            }
        }
    }

    public Map<Class, XMLField> getClassToFieldMappings() {
        return classToFieldMappings;
    }

    public Map<XMLField, XMLMapping> getChoiceElementMappings() {
        return choiceElementMappings;
    }

    public void convertClassNamesToClasses(ClassLoader classLoader) {
        Iterator<XMLField> fields = fieldToClassNameMappings.keySet().iterator();
        while (fields.hasNext()) {
            XMLField next = fields.next();
            String className = fieldToClassNameMappings.get(next);
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
            addChoiceElement(next, elementType);
        }
    }
    
    public void addConverter(XMLField field, XMLConverter converter) {
    	if(this.fieldsToConverters == null) {
    		fieldsToConverters = new HashMap<XMLField, XMLConverter>();
    	}
    	fieldsToConverters.put(field, converter);
    }    	
}
