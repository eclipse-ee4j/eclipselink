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
package org.eclipse.persistence.mappings.foundation;
 
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.transformers.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
import org.eclipse.persistence.sessions.Project;
 
/**
 * <p><b>Purpose</b>: A transformation mapping is used for a specialized translation between how
 * a value is represented in Java and its representation on the databae. Transformation mappings
 * should only be used when other mappings are inadequate.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public abstract class AbstractTransformationMapping extends DatabaseMapping {
 
    /** Name of the class which implements AttributeTransformer to be used to retrieve the attribute value */
    protected String attributeTransformerClassName;
 
    /** attributeTransformerClassName is converter to an instance of AttributeTransformer */
    protected AttributeTransformer attributeTransformer;
 
    /** Stores field name and the class name of a FieldTransformer in a vector to preserve order */
    protected Vector fieldTransformations;
 
    /** The TransformerClassNames are converted into instances of FieldTransformer */
    protected Vector fieldToTransformers;
 
    /** PERF: Indicates if this mapping's attribute is a simple value which cannot be modified only replaced. */
    protected boolean isMutable;
 
    /** Implements indirection behavior */
    protected IndirectionPolicy indirectionPolicy;
 
    /**
     * PUBLIC:
     * Default constructor.
     */
    public AbstractTransformationMapping() {
        fieldTransformations = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        fieldToTransformers = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        setIsMutable(true);
        dontUseIndirection();
    }
 
    /**
     * PUBLIC:
     * Add the field and the name of the method
     * that returns the value to be placed in said field
     * when the object is written to the database.
     * The method may take zero arguments, or it may
     * take a single argument of type
     * <code>org.eclipse.persistence.sessions.Session</code>.
     */
    public void addFieldTransformation(DatabaseField field, String methodName) {
        MethodBasedFieldTransformation transformation = new MethodBasedFieldTransformation();
        transformation.setField(field);
        transformation.setMethodName(methodName);
        getFieldTransformations().addElement(transformation);
    }
 
    /**
     * PUBLIC:
     * Add the name of field and the name of the method
     * that returns the value to be placed in said field
     * when the object is written to the database.
     * The method may take zero arguments, or it may
     * take a single argument of type
     * <code>org.eclipse.persistence.sessions.Session</code>.
     */
    public void addFieldTransformation(String fieldName, String methodName) {
        this.addFieldTransformation(new DatabaseField(fieldName), methodName);
    }
 
    /**
     * INTERNAL:
     * Add the name of a field and the name of a class which implements
     * the FieldTransformer interface. When the object is written, the transform
     * method will be called on the FieldTransformer to acquire the value to put
     * in the field.
     */
    public void addFieldTransformerClassName(String fieldName, String className) {
        this.addFieldTransformerClassName(new DatabaseField(fieldName), className);
    }
 
    /**
     * INTERNAL:
     * Add the name of a field and the name of a class which implements
     * the FieldTransformer interface. When the object is written, the transform
     * method will be called on the FieldTransformer to acquire the value to put
     * in the field.
     */
    public void addFieldTransformerClassName(DatabaseField field, String className) {
        TransformerBasedFieldTransformation transformation = new TransformerBasedFieldTransformation();
        transformation.setField(field);
        transformation.setTransformerClassName(className);
 
        getFieldTransformations().addElement(transformation);
    }
 
    /**
     * PUBLIC:
     * Add the name of field and the transformer
     * that returns the value to be placed in the field
     * when the object is written to the database.
     */
    public void addFieldTransformer(String fieldName, FieldTransformer transformer) {
        this.addFieldTransformer(new DatabaseField(fieldName), transformer);
    }
 
    /**
     * PUBLIC:
     * Add the field and the transformer
     * that returns the value to be placed in the field
     * when the object is written to the database.
     */
    public void addFieldTransformer(DatabaseField field, FieldTransformer transformer) {
        TransformerBasedFieldTransformation transformation = new TransformerBasedFieldTransformation(transformer);
        transformation.setField(field);
 
        getFieldTransformations().addElement(transformation);
    }
 
    /**
     * INTERNAL:
     * The referenced object is checked if it is instantiated or not
     */
    protected boolean areObjectsToBeProcessedInstantiated(Object object) {
        return getIndirectionPolicy().objectIsInstantiated(getAttributeValueFromObject(object));
    }
 
    /**
     * INTERNAL:
     * Clone the attribute from the clone and assign it to the backup.
     */
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        // If mapping is a no-attribute transformation mapping, do nothing
        if (isWriteOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(clone);
        if (!isMutable()) {
            setAttributeValueInObject(backup, attributeValue);
            return;
        }
        Object clonedAttributeValue;
 
        // If the mapping is read-only, a direct pass through of the value will be performed.
        // This is done because the method invocation is not possible as the row will be
        // empty and we have no way to clone the value.
        // Since the value cannot change anyway we just pass it through.
        if (isReadOnly()) {
            clonedAttributeValue = attributeValue;
        } else {
            clonedAttributeValue = getIndirectionPolicy().backupCloneAttribute(attributeValue, clone, backup, unitOfWork);
        }
        setAttributeValueInObject(backup, clonedAttributeValue);
    }
 
    /**
     * INTERNAL
     * Build a phantom row that contains only the fields
     * for the mapping, populated with the values generated by
     * invoking the field methods on the specified object.
     */
    protected AbstractRecord buildPhantomRowFrom(Object domainObject, AbstractSession session) {
    	AbstractRecord row = new DatabaseRecord(this.getFieldToTransformers().size());
        for (Enumeration stream = this.getFieldToTransformers().elements();
                 stream.hasMoreElements();) {
            Object[] pair = (Object[])stream.nextElement();
            DatabaseField field = (DatabaseField)pair[0];
            FieldTransformer transformer = (FieldTransformer)pair[1];
            Object fieldValue = this.invokeFieldTransformer(field, transformer, domainObject, session);
            row.put(field, fieldValue);
        }
        return row;
    }
 
    /**
     * INTERNAL:
     * Builds a shallow original object.  Only direct attributes and primary
     * keys are populated.  In this way the minimum original required for
     * instantiating a working copy clone can be built without placing it in
     * the shared cache (no concern over cycles).
     * @parameter original later the input to buildCloneFromRow
     */
    public void buildShallowOriginalFromRow(AbstractRecord record, Object original, JoinedAttributeManager joinManager, ObjectBuildingQuery query, AbstractSession executionSession) {
        // In this case we know it is a primary key mapping, so hope that it
        // is essentially a direct mapping.  If it is a 1-1 with a
        // no-indirection pointer back to original, then will get a stack
        // overflow.
        // Only solution to this is to trigger the transformation using the root
        // session.
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)query.getSession();
        query.setSession(unitOfWork.getParent());
        try {
            readFromRowIntoObject(record, joinManager, original, query, executionSession);
        } finally {
            query.setSession(unitOfWork);
        }
    }
 
    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy the vector without re-registering the target objects.
     * For 1-1 or ref the reference is from the clone so it is already registered.
     */
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        if (!isMutable()) {
            return attributeValue;
        }
        AbstractRecord row = this.buildPhantomRowFrom(clone, unitOfWork);
        return this.invokeAttributeTransformer(row, backup, unitOfWork);
    }
 
    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    public void buildClone(Object original, Object clone, UnitOfWorkImpl unitOfWork) {
        // If mapping is a no-attribute transformation mapping, do nothing
        if (isWriteOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(original);
        Object clonedAttributeValue;
 
        // If the mapping is read-only, a direct pass through of the value will be performed.
        // This is done because the method invocation is not possible as the row will be
        // empty and we have no way to clone the value.
        // Since the value cannot change anyway we just pass it through.
        if (isReadOnly() || !isMutable()) {
            clonedAttributeValue = attributeValue;
        } else {
            clonedAttributeValue = getIndirectionPolicy().cloneAttribute(attributeValue, original, clone, unitOfWork, false);// building clone from an original not a row.
        }
 
        setAttributeValueInObject(clone, clonedAttributeValue);
    }
 
    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to this value in the
     * working copy clone.
     * In order to bypass the shared cache when in transaction a UnitOfWork must
     * be able to populate working copies directly from the row.
     */
    public void buildCloneFromRow(AbstractRecord record, JoinedAttributeManager joinManager, Object clone, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // If mapping is a no-attribute transformation mapping, do nothing
        if (isWriteOnly()) {
            return;
        }
 
        // This will set the value in the clone automatically.
        Object attributeValue = readFromRowIntoObject(record, joinManager, clone, sourceQuery, executionSession);
 
        Object clonedAttributeValue;
 
        // If the mapping is read-only, a direct pass through of the value will be performed.
        // This is done because the method invocation is not possible as the row will be
        // empty and we have no way to clone the value.
        // Since the value cannot change anyway we just pass it through.
        if (isReadOnly() || !isMutable()) {
            clonedAttributeValue = attributeValue;
        } else {
            clonedAttributeValue = getIndirectionPolicy().cloneAttribute(attributeValue, null,// no original
                                                                         clone, unitOfWork, true);// build clone directly from row.
        }
        setAttributeValueInObject(clone, clonedAttributeValue);
    }
 
    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the attribute value, go right to the object itself.
     */
    public Object buildCloneForPartObject(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        return buildBackupCloneForPartObject(attributeValue, original, clone, unitOfWork);
    }
 
    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    public void buildCopy(Object copy, Object original, ObjectCopyingPolicy policy) {
        // If mapping is a no-attribute transformation mapping, do nothing
        if (isWriteOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(original);
 
        Object clonedAttributeValue;
 
        // If the mapping is read-only, a direct pass through of the value will be performed.
        // This is done because the method invocation is not possible as the row will be
        // empty and we have no way to clone the value.
        // Since the value cannot change anyway we just pass it through.
        if (isReadOnly() || !isMutable()) {
            clonedAttributeValue = attributeValue;
        } else {
        	AbstractRecord row = this.buildPhantomRowFrom(original, policy.getSession());
            clonedAttributeValue = invokeAttributeTransformer(row, copy, policy.getSession());
        }
 
        setAttributeValueInObject(copy, clonedAttributeValue);
    }
 
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }
 
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        //Objects referenced through transformation mappings are not registered as
        // they have no identity, this is a no-op.
    }
 
    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        AbstractTransformationMapping clone = (AbstractTransformationMapping)super.clone();
        clone.setFieldToTransformers(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getFieldToTransformers().size() + 1));
 
        for (Enumeration enumtr = getFieldToTransformers().elements(); enumtr.hasMoreElements();) {
            Object[] transformation = new Object[2];
            Object[] pair = (Object[])enumtr.nextElement();
            transformation[0] = pair[0];
            transformation[1] = pair[1];
            clone.getFieldToTransformers().addElement(transformation);
        }
 
        clone.setIndirectionPolicy((IndirectionPolicy)indirectionPolicy.clone());
 
        return clone;
    }
 
    /**
     * INTERNAL:
     * Return all the fields with this mapping.
     */
    @Override
    protected Vector collectFields() {
        Vector databaseFields = new Vector(getFieldToTransformers().size());
        for (Enumeration stream = getFieldToTransformers().elements(); stream.hasMoreElements();) {
            databaseFields.addElement(((Object[])stream.nextElement())[0]);
        }
        return databaseFields;
    }
 
    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        if (isReadOnly() || isWriteOnly()) {
            return null;
        }
        Object cloneAttribute = getAttributeValueFromObject(clone);
        Object backUpAttribute = null;
        if ((cloneAttribute != null) && (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return null;
        }
        boolean difference = false;
        if (owner.isNew()) {
            difference = true;
        } else {
            if (backUp != null) {
                backUpAttribute = getAttributeValueFromObject(backUp);
            }
            boolean backUpIsInstantiated = ((backUpAttribute == null) || (getIndirectionPolicy().objectIsInstantiated(backUpAttribute)));
 
            for (Enumeration stream = getFieldToTransformers().elements();
                     stream.hasMoreElements();) {
                Object[] pair = (Object[])stream.nextElement();
                DatabaseField field = (DatabaseField)pair[0];
                FieldTransformer transformer = (FieldTransformer)pair[1];
                Object cloneFieldValue = null;
                Object backUpFieldValue = null;
                if (clone != null) {
                    cloneFieldValue = invokeFieldTransformer(field, transformer, clone, session);
                }
                if ((backUpIsInstantiated) && (backUp != null)) {
                    backUpFieldValue = invokeFieldTransformer(field, transformer, backUp, session);
                }
 
                if ((cloneFieldValue == null) && (backUpFieldValue == null)) {
                    continue;// skip this iteration, go to the next one
                }
                if ((cloneFieldValue != null) && cloneFieldValue.equals(backUpFieldValue)) {
                    continue;// skip this iteration, go to the next one
                }
                difference = true;
                break;// There is a difference.
            }
        }
        if (difference) {
            return buildChangeRecord(clone, owner, session);
        }
        return null;
    }
 
    /**
     * INTERNAL:
     * Directly build a change record without comparison
     */
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        TransformationMappingChangeRecord changeRecord = new TransformationMappingChangeRecord(owner);
        changeRecord.setRow(this.buildPhantomRowFrom(clone, session));
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        return changeRecord;
    }
 
    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession databaseSession) {
        for (Enumeration stream = getFieldToTransformers().elements(); stream.hasMoreElements();) {
            Object[] objects = (Object[])stream.nextElement();
            DatabaseField field = (DatabaseField)objects[0];
            FieldTransformer transformer = (FieldTransformer)objects[1];
            Object firstFieldValue = invokeFieldTransformer(field, transformer, firstObject, databaseSession);
            Object secondFieldValue = invokeFieldTransformer(field, transformer, secondObject, databaseSession);
 
            if ((firstFieldValue == null) && (secondFieldValue == null)) {
                continue;// skip this iteration, go to the next one
            }
            if ((firstFieldValue == null) || (secondFieldValue == null)) {
                return false;
            }
            if (!firstFieldValue.equals(secondFieldValue)) {
                return false;
            }
        }
 
        return true;
    }
 
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);

        if (this.attributeTransformerClassName == null) {
            return;
        }
        Class attributeTransformerClass = null;
        try{
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    attributeTransformerClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(attributeTransformerClassName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(attributeTransformerClassName, exception.getException());
                }
            } else {
                attributeTransformerClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(attributeTransformerClassName, true, classLoader);
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(attributeTransformerClassName, exc);
        }
        this.setAttributeTransformerClass(attributeTransformerClass);
        
        for (Enumeration stream = getFieldTransformations().elements(); stream.hasMoreElements();) {
            FieldTransformation transformation = (FieldTransformation)stream.nextElement();
            if (transformation instanceof TransformerBasedFieldTransformation) {
                TransformerBasedFieldTransformation transformer = (TransformerBasedFieldTransformation)transformation;
                String transformerClassName = transformer.getTransformerClassName(); 
                if (transformerClassName == null) {
                    return;
                }
                Class transformerClass = null;
                try{
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            transformerClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(transformerClassName, true, classLoader));
                        } catch (PrivilegedActionException exception) {
                            throw ValidationException.classNotFoundWhileConvertingClassNames(transformerClassName, exception.getException());
                        }
                    } else {
                        transformerClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(transformerClassName, true, classLoader);
                    }
                } catch (ClassNotFoundException exc){
                    throw ValidationException.classNotFoundWhileConvertingClassNames(transformerClassName, exc);
                }
                transformer.setTransformerClass(transformerClass);
            }
        }
    }

    /**
     * INTERNAL:
     * Builder the unit of work value holder.
     * Ignore the original object.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public UnitOfWorkValueHolder createUnitOfWorkValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractRecord row, UnitOfWorkImpl unitOfWork, boolean buildDirectlyFromRow) {
        return new UnitOfWorkTransformerValueHolder(attributeValue, original, clone, this, unitOfWork);
    }
 
    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This defaults to false and only required for transformations that perform database access.
     */
    public void dontUseIndirection() {
        setIndirectionPolicy(new NoIndirectionPolicy());
    }
 
    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.
     * Replace the transient attributes of the remote value holders
     * with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        getIndirectionPolicy().fixObjectReferences(object, objectDescriptors, processedObjects, query, session);
    }
 
    /**
     * INTERNAL:
     * The attributeTransformer stores an instance of the class which implements
     * AttributeTransformer.
     */
    public AttributeTransformer getAttributeTransformer() {
        return attributeTransformer;
    }
 
    /**
     * PUBLIC:
     * Return the attribute transformation method name.
     */
    public String getAttributeMethodName() {
        if (getAttributeTransformer() instanceof MethodBasedAttributeTransformer) {
            return ((MethodBasedAttributeTransformer)getAttributeTransformer()).getMethodName();
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the attribute transformer's class.
     * This is used to map to XML.
     */
    public Class getAttributeTransformerClass() {
        if ((getAttributeTransformer() == null) || (getAttributeTransformer() instanceof MethodBasedAttributeTransformer)) {
            return null;
        }
        return getAttributeTransformer().getClass();
    }
    
    /**
     * INTERNAL:
     * Set the attribute transformer's class.
     * This is used to map from XML.
     */
    public void setAttributeTransformerClass(Class attributeTransformerClass) {
        if (attributeTransformerClass == null) {
            return;
        }
        try {
            Object instance = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    instance = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(attributeTransformerClass));
                }catch (PrivilegedActionException ex){
                    throw (Exception)ex.getCause();
                }
            }else{
                instance = PrivilegedAccessHelper.newInstanceFromClass(attributeTransformerClass);
            }
            setAttributeTransformer((AttributeTransformer)instance);
        } catch (Exception ex) {
            throw DescriptorException.attributeTransformerClassInvalid(getAttributeTransformerClassName(), this, ex);
        }
    }
    
    /**
     * INTERNAL:
     * Return the attribute transformer class name
     */
    public String getAttributeTransformerClassName() {
        return attributeTransformerClassName;
    }
 
    /**
     * INTERNAL:
     * Check for write-only, one-way transformation.
     */
    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        if (isWriteOnly()) {
            return null;
        }
        Object attributeValue = super.getAttributeValueFromObject(object);
        return getIndirectionPolicy().validateAttributeOfInstantiatedObject(attributeValue);
    }
 
    /**
     * INTERNAL:
     * Returns a Vector which stores fieldnames and the respective method/transformer names.
     */
    public Vector getFieldTransformations() {
        return fieldTransformations;
    }
 
    /**
     * INTERNAL:
     * @return a vector which stores fields and their respective transformers.
     */
    public Vector getFieldToTransformers() {
        return fieldToTransformers;
    }
 
    /**
     * INTERNAL:
     * Return the mapping's indirection policy.
     */
    public IndirectionPolicy getIndirectionPolicy() {
        return indirectionPolicy;
    }
 
    /**
     * INTERNAL:
     * Returns the real attribute value from the reference object's attribute value.
     * If the attribute is using indirection the value of the value-holder is returned.
     * If the value holder is not instantiated then it is instantiated.
     */
    public Object getRealAttributeValueFromAttribute(Object attributeValue, Object object, AbstractSession session) {
        return this.indirectionPolicy.getRealAttributeValueFromObject(object, attributeValue);
    }
    
    /**
     * INTERNAL:
     * Trigger the instantiation of the attribute if lazy.
     */
    public void instantiateAttribute(Object object, AbstractSession session) {
        getIndirectionPolicy().instantiateObject(object, getAttributeValueFromObject(object));
    }
    
    /**
     * INTERNAL:
     * Extract and return the appropriate value from the
     * specified remote value holder.
     */
    public Object getValueFromRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        return getIndirectionPolicy().getValueFromRemoteValueHolder(remoteValueHolder);
    }
 
    /**
     * INTERNAL:
     * The mapping is initialized with the given session.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        initializeAttributeTransformer(session);
        initializeFieldToTransformers(session);
        setFields(collectFields());
        getIndirectionPolicy().initialize();
    }
 
    /**
     * INTERNAL:
     * Convert the attribute transformer class name into an AttributeTransformer
    * If the old-style method name in set, then use a MethodBasedAttributeTRansformer
     */
    protected void initializeAttributeTransformer(AbstractSession databaseSession) throws DescriptorException {
        if (isWriteOnly()) {
            return;
        }
        getAttributeTransformer().initialize(this);
    }
 
    /**
     * INTERNAL:
     * Required for reverse compatibility and test cases:
     * @return a hash table containing the fieldName and their respective method names
     */
    public Hashtable getFieldNameToMethodNames() {
        Hashtable table = new Hashtable(getFieldTransformations().size());
        Iterator transformations = getFieldTransformations().iterator();
        while (transformations.hasNext()) {
            FieldTransformation transformation = (FieldTransformation)transformations.next();
            if (transformation instanceof MethodBasedFieldTransformation) {
                table.put(transformation.getField().getQualifiedName(), ((MethodBasedFieldTransformation)transformation).getMethodName());
            }
        }
        return table;
    }
 
    /**
     * INTERNAL:
     * Convert the field names and their corresponding method names to
     * DatabaseFields and Methods.
     */
    protected void initializeFieldToTransformers(AbstractSession session) throws DescriptorException {
        for (Enumeration stream = getFieldToTransformers().elements(); stream.hasMoreElements();) {
            Object[] pair = (Object[])stream.nextElement();
            pair[0] = getDescriptor().buildField(((DatabaseField)pair[0]));
            ((FieldTransformer)pair[1]).initialize(this);
        }
        for (Enumeration stream = getFieldTransformations().elements(); stream.hasMoreElements();) {
            FieldTransformation transformation = (FieldTransformation)stream.nextElement();
            DatabaseField field = getDescriptor().buildField(transformation.getField());
            String transformerClassName = "MethodBasedFieldTransformer";
            FieldTransformer transformer = null;
            try {
                transformer = transformation.buildTransformer();
            } catch (ConversionException ex) {
                if (transformation instanceof TransformerBasedFieldTransformation) {
                    transformerClassName = ((TransformerBasedFieldTransformation)transformation).getTransformerClassName();
                }
 
                throw DescriptorException.fieldTransformerClassNotFound(transformerClassName, this, ex);
            } catch (Exception ex) {
                if (transformation instanceof TransformerBasedFieldTransformation) {
                    transformerClassName = ((TransformerBasedFieldTransformation)transformation).getTransformerClassName();
                }
 
                throw DescriptorException.fieldTransformerClassInvalid(transformerClassName, this, ex);
            }
 
            transformer.initialize(this);
            // Attempt to ensure a type is set on the field.
            if (field.getType() == null) {
                if (transformer instanceof MethodBasedFieldTransformer) {
                    field.setType(((MethodBasedFieldTransformer)transformer).getFieldType());
                } else if (field.getColumnDefinition() != null) {
                    // Search for the type for this field definition.
                    if (session.getDatasourcePlatform() instanceof DatabasePlatform) {
                        Iterator iterator = session.getPlatform().getFieldTypes().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry)iterator.next();
                            if (((FieldTypeDefinition)entry.getValue()).getName().equals(field.getColumnDefinition())) {
                                field.setType((Class)entry.getKey());
                                break;
                            }
                        }
                    }
                }
            }
            Object[] fieldToTransformer = new Object[2];
            fieldToTransformer[0] = field;
            fieldToTransformer[1] = transformer;
 
            getFieldToTransformers().addElement(fieldToTransformer);
        }
    }
 
    /**
     * INTERNAL:
     * Invoke the buildAttributeValue method on the AttributeTransformer
     */
    public Object invokeAttributeTransformer(AbstractRecord record, Object domainObject, AbstractSession session) throws DescriptorException {
        return getAttributeTransformer().buildAttributeValue(record, domainObject, session);
    }
 
    /**
     * INTERNAL:
    * Invoke the buildFieldValue on the appropriate FieldTransformer
    */
    protected Object invokeFieldTransformer(DatabaseField field, FieldTransformer transformer, Object domainObject, AbstractSession session) throws DescriptorException {
        return transformer.buildFieldValue(domainObject, field.getName(), session);
    }
 
    protected Object invokeFieldTransformer(DatabaseField field, Object domainObject, AbstractSession session) {
        Enumeration transformers = getFieldToTransformers().elements();
        while (transformers.hasMoreElements()) {
            Object[] next = (Object[])transformers.nextElement();
            if (field.equals(next[0])) {
                return invokeFieldTransformer(field, (FieldTransformer)next[1], domainObject, session);
            }
        }
        return null;
    }
 
    /**
     * PUBLIC:
     * Return true if the attribute for this mapping is not a simple atomic value that cannot be modified,
     * only replaced.
     * This is true by default for non-primitives, but can be set to false to avoid cloning
     * and change comparison in the unit of work.
     */
    public boolean isMutable() {
        return isMutable;
    }
 
    /**
     * INTERNAL:
     * Return true if read-only is explicitly set to true;
     * otherwise return whether the transformation has no fields
     * (no fields = read-only)
     */
    public boolean isReadOnly() {
        if (super.isReadOnly()) {
            return true;
        } else {
            return getFieldTransformations().isEmpty() && getFieldToTransformers().isEmpty();
        }
    }
 
    /**
     * INTERNAL:
     */
    public boolean isTransformationMapping() {
        return true;
    }
 
    /**
     * INTERNAL:
     * Return if the transformation has no attribute, is write only.
     */
    public boolean isWriteOnly() {
        return (getAttributeName() == null) && ((getAttributeTransformer() == null) && (getAttributeTransformerClassName() == null));
    }
 
    /**
     * INTERNAL:
     * Perform the iteration opperation on the iterators current objects attributes.
     * Only require if primitives are desired.
     */
    public void iterate(DescriptorIterator iterator) {
        Object attributeValue = getAttributeValueFromObject(iterator.getVisitedParent());
        getIndirectionPolicy().iterateOnAttributeValue(iterator, attributeValue);
    }
 
    /**
     * INTERNAL:
     * Iterate on the attribute value.
     * The value holder has already been processed.
     */
    public void iterateOnRealAttributeValue(DescriptorIterator iterator, Object realAttributeValue) {
        iterator.iteratePrimitiveForMapping(realAttributeValue, this);
    }
 
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Which is the original from the parent UnitOfWork
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        if (isWriteOnly()) {
            return;
        }
        if (!isMutable()) {
            setRealAttributeValueInObject(target, getRealAttributeValueFromObject(source, mergeManager.getSession()));
            return;
        }
        AbstractRecord record = (AbstractRecord)((TransformationMappingChangeRecord)changeRecord).getRecord();
        Object attributeValue = invokeAttributeTransformer(record, target, mergeManager.getSession());
        setRealAttributeValueInObject(target, attributeValue);
    }
 
    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (isWriteOnly()) {
            return;
        }

        // do refresh check first as I may need to reset remote value holder
        if (mergeManager.shouldRefreshRemoteObject() && usesIndirection()) {
            getIndirectionPolicy().mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }
        
        if (mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            if (!areObjectsToBeProcessedInstantiated(target)) {
                // This will occur when the clone's value has not been instantiated yet and we do not need
                // the refresh that attribute
                return;
            }
        } else if (!areObjectsToBeProcessedInstantiated(source)) {
            // I am merging from a clone into an original.  No need to do merge if the attribute was never
            // modified
            return;
        }

        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!areObjectsToBeProcessedInstantiated(source))) {
                setAttributeValueInObject(target, getIndirectionPolicy().getOriginalIndirectionObject(getAttributeValueFromObject(source), mergeManager.getSession()));
                return;
            }
        }
        if (isReadOnly()) {
            // if it is read only then we do not have any fields specified for the
            // transformer, without fields we can not build the row, so just copy
            // over the value  alternatively we could build the entire row for the object.
            setRealAttributeValueInObject(target, getRealAttributeValueFromObject(source, mergeManager.getSession()));
            return;
        }
        if (!isMutable()) {
            Object attribute = getRealAttributeValueFromObject(source, mergeManager.getSession());
            if (this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) {
                // Object level or attribute level so lets see if we need to raise the event?
                Object targetAttribute = getRealAttributeValueFromObject(target, mergeManager.getSession());
                if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())
                        && (((targetAttribute == null) && (attribute != null)) || ((targetAttribute != null) && (!targetAttribute.equals(attribute))))) {
                    this.descriptor.getObjectChangePolicy().raiseInternalPropertyChangeEvent(target, getAttributeName(), targetAttribute, attribute);
                }
            }
            setRealAttributeValueInObject(target, attribute);
            return;
        }
 
        // This dumps the attribute into the row and back.
        AbstractRecord row = buildPhantomRowFrom(source, mergeManager.getSession());
        Object attributeValue = invokeAttributeTransformer(row, source, mergeManager.getSession());
        AbstractRecord targetRow = buildPhantomRowFrom(target, mergeManager.getSession());
        setRealAttributeValueInObject(target, attributeValue);
        //set the change after the set on the object as this mapping uses the object to build the change record.
        if (this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) {
            for (Enumeration keys = targetRow.keys(); keys.hasMoreElements(); ){
                Object field = keys.nextElement();
                if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())
                        && (!row.get(field).equals(targetRow.get(field)))) {
                    this.descriptor.getObjectChangePolicy().raiseInternalPropertyChangeEvent(target, getAttributeName(), invokeAttributeTransformer(targetRow, source, mergeManager.getSession()), attributeValue);
                    break;
                }
            }
        }
    }
 
    /**
     * INTERNAL:
     * Allow for initialization of properties and validation.
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        if (isWriteOnly()) {
            return;// Allow for one-way transformations.
        }
 
        super.preInitialize(session);
 
        // PERF: Also auto-set mutable to false is the attribute type is a primitive.
        // This means it is not necessary to clone the value (through double transformation).
        if ((getAttributeClassification() != null) && (getAttributeClassification().isPrimitive() || Helper.isPrimitiveWrapper(getAttributeClassification()) || getAttributeClassification().equals(ClassConstants.STRING) || getAttributeClassification().equals(ClassConstants.BIGDECIMAL) || getAttributeClassification().equals(ClassConstants.NUMBER))) {
            setIsMutable(false);
        }
    }
 
    /**
     * INTERNAL:
     * Extracts value from return row and set the attribute to the value in the object.
     * Return row is merged into object after execution of insert or update call
     * accordiing to ReturningPolicy.
     */
    public Object readFromReturnRowIntoObject(AbstractRecord row, Object object, ReadObjectQuery query, Collection handledMappings) throws DatabaseException {
        AbstractRecord transformationRow = new DatabaseRecord(getFields().size());
        for (int i = 0; i < getFields().size(); i++) {
            DatabaseField field = getFields().elementAt(i);
            Object value;
            if (row.containsKey(field)) {
                value = row.get(field);
            } else {
                value = valueFromObject(object, field, query.getSession());
            }
            transformationRow.add(field, value);
        }
        Object attributeValue = readFromRowIntoObject(transformationRow, null, object, query, query.getSession());
        if (handledMappings != null) {
            handledMappings.add(this);
        }
 
        return attributeValue;
    }
 
    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to the value in the object.
     */
    public Object readFromRowIntoObject(AbstractRecord row, JoinedAttributeManager joinManager, Object object, ObjectBuildingQuery query, AbstractSession executionSession) throws DatabaseException {
        if (isWriteOnly()) {
            return null;
        }
 
        Object attributeValue = getIndirectionPolicy().valueFromMethod(object, row, query.getSession());
        try {
            getAttributeAccessor().setAttributeValueInObject(object, attributeValue);
        } catch (DescriptorException exception) {
            exception.setMapping(this);
            throw exception;
        }
        return attributeValue;
    }
 
    /**
     * INTERNAL:
     * Needed for backwards compatibility
     */
    public Vector getFieldNameToMethodNameAssociations() {
        Vector associations = new Vector();
        for (Iterator source = getFieldTransformations().iterator(); source.hasNext();) {
            FieldTransformation tf = (FieldTransformation)source.next();
            if (tf instanceof MethodBasedFieldTransformation) {
                Association ass = new Association();
                ass.setKey(tf.getField().getQualifiedName());
                ass.setValue(((MethodBasedFieldTransformation)tf).getMethodName());
                associations.addElement(ass);
            }
        }
        return associations;
    }
 
    /**
     * INTERNAL:
     * needed for backwards compatibility
     */
    public void setFieldNameToMethodNameAssociations(Vector associations) {
        setFieldTransformations(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(associations.size()));
        for (Iterator source = associations.iterator(); source.hasNext();) {
            Association ass = (Association)source.next();
            MethodBasedFieldTransformation tf = new MethodBasedFieldTransformation();
            tf.setField(new DatabaseField((String)ass.getKey()));
            tf.setMethodName((String)ass.getValue());
            getFieldTransformations().addElement(tf);
        }
    }
 
    /**
     * INTERNAL:
     * Once descriptors are serialized to the remote session. All its mappings and reference descriptors are traversed. Usually
     * mappings are initilaized and serialized reference descriptors are replaced with local descriptors if they already exist on the
     * remote session.
     */
    public void remoteInitialization(DistributedSession session) {
        setFieldToTransformers(new Vector());
 
        // Remote mappings is initilaized here again because while serializing only the uninitialized data is passed
        // as the initialized data is not serializable.
        if (!isWriteOnly()) {
            super.remoteInitialization(session);
            initializeAttributeTransformer(session);
        }
        initializeFieldToTransformers(session);
    }
 
    /**
      * PUBLIC:
      * Set the AttributeTransformer, this transformer will be used to extract the value for the
      * object's attribute from the database row.
      */
    public void setAttributeTransformer(AttributeTransformer transformer) {
        attributeTransformer = transformer;
        if ((transformer != null) && !(transformer instanceof MethodBasedAttributeTransformer)) {
            attributeTransformerClassName = transformer.getClass().getName();
        }
    }
 
    /**
     * INTERNAL:
     * Set the Attribute Transformer Class Name
     * @param className
     */
    public void setAttributeTransformerClassName(String className) {
        attributeTransformerClassName = className;
    }
 
 
    /**
     * PUBLIC:
     * To set the attribute method name. The method is invoked internally by TopLink
     * to retreive the value to store in the domain object. The method receives Record
     * as its parameter and optionally Session, and should extract the value from the
     * record to set into the object, but should not set the value on the object, only return it.
     */
    public void setAttributeTransformation(String methodName) {
        if ((methodName != null) && (methodName != "")) {
            setAttributeTransformer(new MethodBasedAttributeTransformer(methodName));
        } else {
            setAttributeTransformer(null);
        }
    }
 
    /**
     * INTERNAL:
     * Check for write-only, one-way transformations.
     */
    public void setAttributeValueInObject(Object object, Object value) {
        if (isWriteOnly()) {
            return;
        }
 
        super.setAttributeValueInObject(object, value);
    }
 
    /**
     * PUBLIC:
     * Set if the value of the attribute is atomic or a complex mutable object and can be modified without replacing the entire object.
     * This defaults to true for non-primitives, but can be set to false to optimize object cloning and change comparison.
     */
    public void setIsMutable(boolean mutable) {
        isMutable = mutable;
    }
 
    /**
     * INTERNAL:
     * Set the value of the attribute mapped by this mapping,
     * placing it inside a value holder if necessary.
     * If the value holder is not instantiated then it is instantiated.
     * Check for write-only, one-way transformations.
     */
    public void setRealAttributeValueInObject(Object object, Object value) throws DescriptorException {
        if (this.isWriteOnly()) {
            return;
        }
        this.getIndirectionPolicy().setRealAttributeValueInObject(object, value);
    }
 
    /**
     * INTERNAL:
     * Set the field to method name associations.
     */
    public void setFieldTransformations(Vector fieldTransformations) {
        this.fieldTransformations = fieldTransformations;
    }
 
    protected void setFieldToTransformers(Vector aVector) {
        fieldToTransformers = aVector;
    }
 
    /**
     * ADVANCED:
     * Set the indirection policy.
     */
    public void setIndirectionPolicy(IndirectionPolicy indirectionPolicy) {
        this.indirectionPolicy = indirectionPolicy;
        indirectionPolicy.setMapping(this);
    }
 
    /**
     * INTERNAL:
     * Will be used by Gromit. For details see usesIndirection().
     * @see #useBasicIndirection()
     * @see #dontUseIndirection()
     */
    public void setUsesIndirection(boolean usesIndirection) {
        if (usesIndirection) {
            useBasicIndirection();
        } else {
            dontUseIndirection();
        }
    }
 
    /**
     * INTERNAL:
     * Returns true if the merge should cascade to the mappings reference's parts.
     */
    public boolean shouldMergeCascadeParts(MergeManager mergeManager) {
        return (mergeManager.shouldCascadeAllParts() || (mergeManager.shouldCascadePrivateParts() && isPrivateOwned()));
    }
 
    /**
     * INTERNAL:
     * Returns true if the merge should cascade to the mappings reference.
     */
    protected boolean shouldMergeCascadeReference(MergeManager mergeManager) {
        if (mergeManager.shouldCascadeReferences()) {
            return true;
        }
 
        // P2.0.1.3: Was merging references on non-privately owned parts
        // Same logic in:
        return shouldMergeCascadeParts(mergeManager);
    }
 
    /**
     * INTERNAL:
     * Either create a new change record or update the change record with the new value.
     * This is used by attribute change tracking.
     */
    public void updateChangeRecord(Object clone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        TransformationMappingChangeRecord changeRecord = (TransformationMappingChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (!isWriteOnly()) {
            if (changeRecord == null) {
                objectChangeSet.addChange(buildChangeRecord(clone, objectChangeSet, uow));
            } else {
                changeRecord.setRow(this.buildPhantomRowFrom(clone, uow));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        return ! isMutable();
    }
 
    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This defaults to false and only required for transformations that perform database access.
     */
    public void useBasicIndirection() {
        setIndirectionPolicy(new BasicIndirectionPolicy());
    }
 
    /**
     * PUBLIC:
     * Indirection means that a IndirectContainer (wrapping a ValueHolder) will be put in-between
     * the attribute and the real object.
     * This allows for the reading of the target from the database to be delayed until accessed.
     * This defaults to true and is strongly suggested as it give a huge performance gain.
     */
    public void useContainerIndirection(Class containerClass) {
        ContainerIndirectionPolicy policy = new ContainerIndirectionPolicy();
        policy.setContainerClass(containerClass);
        setIndirectionPolicy(policy);
    }
 
    /**
     * PUBLIC:
     * Indirection means that a ValueHolder will be put in-between the attribute and the real object.
     * This defaults to false and only required for transformations that perform database access.
     * @see #useBasicIndirection()
     */
    public void useIndirection() {
        useBasicIndirection();
    }
 
    /**
     * PUBLIC:
     * Indirection meansthat a ValueHolder will be put in-between the attribute and the real object.
     * This defaults to false and only required for transformations that perform database access.
     * @see org.eclipse.persistence.mappings.IndirectionPolicy
     */
    public boolean usesIndirection() {
        return getIndirectionPolicy().usesIndirection();
    }
 
    /**
     * INTERNAL:
     * Validate mapping declaration
     */
    public void validateBeforeInitialization(AbstractSession session) throws DescriptorException {
        super.validateBeforeInitialization(session);
 
        if (isWriteOnly()) {
            return;
        }
 
        if ((getAttributeTransformer() == null) && (getAttributeTransformerClassName() == null)) {
            session.getIntegrityChecker().handleError(DescriptorException.noAttributeTransformationMethod(this));
        }
 
        if (getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
            Class attributeType = ((InstanceVariableAttributeAccessor)getAttributeAccessor()).getAttributeType();
            getIndirectionPolicy().validateDeclaredAttributeType(attributeType, session.getIntegrityChecker());
        } else if (getAttributeAccessor().isMethodAttributeAccessor()) {
            Class returnType = ((MethodAttributeAccessor)getAttributeAccessor()).getGetMethodReturnType();
            getIndirectionPolicy().validateGetMethodReturnType(returnType, session.getIntegrityChecker());
 
            Class parameterType = ((MethodAttributeAccessor)getAttributeAccessor()).getSetMethodParameterType();
            getIndirectionPolicy().validateSetMethodParameterType(parameterType, session.getIntegrityChecker());
        }
    }
 
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) {
        return invokeFieldTransformer(field, object, session);
    }
 
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }
 
        for (Enumeration stream = getFieldToTransformers().elements(); stream.hasMoreElements();) {
            Object[] next = (Object[])stream.nextElement();
            DatabaseField field = (DatabaseField)next[0];
            FieldTransformer transformer = (FieldTransformer)next[1];
            Object fieldValue = invokeFieldTransformer(field, transformer, object, session);
            row.put(field, fieldValue);
        }
    }
 
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }
 
        for (Enumeration stream = getFieldToTransformers().elements(); stream.hasMoreElements();) {
            Object[] next = (Object[])stream.nextElement();
            DatabaseField field = (DatabaseField)next[0];
            Object fieldValue = ((TransformationMappingChangeRecord)changeRecord).getRecord().get(field);
            row.put(field, fieldValue);
        }
    }
 
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * But before that check if the reference object is instantiated or not.
     */
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery query, AbstractRecord record) {
        if (!areObjectsToBeProcessedInstantiated(query.getObject())) {
            return;
        }
 
        if (query.getSession().isUnitOfWork()) {
            if (compareObjects(query.getBackupClone(), query.getObject(), query.getSession())) {
                return;
            }
        }
 
        writeFromObjectIntoRow(query.getObject(), record, query.getSession(), WriteType.UPDATE);
    }
 
    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }
 
        for (Enumeration entry = getFieldToTransformers().elements(); entry.hasMoreElements();) {
            DatabaseField field = (DatabaseField)((Object[])entry.nextElement())[0];
            record.put(field, null);
        }
    }
}
