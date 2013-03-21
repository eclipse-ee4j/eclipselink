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
package org.eclipse.persistence.internal.indirection;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <b>Purpose</b>: Provide ability for developers to wrap ValueHolders (Basic Indirection)<p>
 * <b>Responsibilities</b>:<ul>
 * <li>Wrap & un-wrap a ValueHolder within an IndirectContainer
 * <li>Reflectivly instantiate the containers as required
 * </ul>
 * @see org.eclipse.persistence.indirection.IndirectContainer
 * @author Doug Clarke (TOP)
 * @since 2.5.0.5
 */
public class ContainerIndirectionPolicy extends BasicIndirectionPolicy {
    private Class containerClass;
    private String containerClassName;
    private transient Constructor containerConstructor;

    /**
     * INTERNAL:
     * Construct a new indirection policy.
     */
    public ContainerIndirectionPolicy() {
        super();
    }

    /**
     * INTERNAL:
     *    Return a backup clone of the attribute.
     */
    public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        IndirectContainer container = (IndirectContainer)attributeValue;
        ValueHolderInterface valueHolder = container.getValueHolder();
        ValueHolderInterface newValueHolder = (ValueHolderInterface)super.backupCloneAttribute(valueHolder, clone, backup, unitOfWork);
        return buildContainer(newValueHolder);
    }

    /**
     * Build a conatiner with the initialized constructor.
     * @see initializeContainer
     */
    protected IndirectContainer buildContainer(ValueHolderInterface valueHolder) {
        try {
            IndirectContainer container = null;
            if (getContainerConstructor().getParameterTypes().length == 0) {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    container = (IndirectContainer)AccessController.doPrivileged(new PrivilegedInvokeConstructor(getContainerConstructor(), new Object[0]));
                }else{
                    container = (IndirectContainer)PrivilegedAccessHelper.invokeConstructor(getContainerConstructor(), new Object[0]);
                }
                container.setValueHolder(valueHolder);
            } else {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    container = (IndirectContainer)AccessController.doPrivileged(new PrivilegedInvokeConstructor(getContainerConstructor(), new Object[] { valueHolder }));
                }else{
                    container = (IndirectContainer)PrivilegedAccessHelper.invokeConstructor(getContainerConstructor(), new Object[] { valueHolder });
                }
            }
            return container;
        } catch (Exception exception) {
            throw DescriptorException.invalidIndirectionPolicyOperation(this, "buildContainer constructor (" + getContainerConstructor() + ") Failed: " + exception);
        }
    }

    /**
     * INTERNAL: This method can be used when an Indirection Object is required
     * to be built from a provided ValueHolderInterface object. This may be used
     * for custom value holder types. Certain policies like the
     * TransparentIndirectionPolicy may wrap the valueholder in another object.
     */
    
    public Object buildIndirectObject(ValueHolderInterface valueHolder){
        return buildContainer(valueHolder);
    }

    /**
     * INTERNAL:
     *    Return a clone of the attribute.
     *  @param buildDirectlyFromRow indicates that we are building the clone directly
     *  from a row as opposed to building the original from the row, putting it in
     *  the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        IndirectContainer container = (IndirectContainer)attributeValue;
        ValueHolderInterface valueHolder = container.getValueHolder();
        ValueHolderInterface newValueHolder = (ValueHolderInterface)super.cloneAttribute(valueHolder, original, cacheKey, clone, refreshCascade, cloningSession, buildDirectlyFromRow);

        return buildContainer(newValueHolder);
    }

    /**
     * INTERNAL:
     *    Return the reference row for the reference object.
     * This allows the new row to be built without instantiating
     * the reference object.
     * Return null if the object has already been instantiated.
     */
    public AbstractRecord extractReferenceRow(Object referenceObject) {
        if (this.objectIsInstantiated(referenceObject)) {
            return null;
        } else {
            return ((DatabaseValueHolder)((IndirectContainer)referenceObject).getValueHolder()).getRow();
        }
    }

    /**
     * Returns the Container class which implements IndirectContainer.
     */
    public Class getContainerClass() {
        return containerClass;
    }

    /**
     * INTERNAL:
     * Used by MW.
     */
    public String getContainerClassName() {
        if ((containerClassName == null) && (containerClass != null)) {
            containerClassName = containerClass.getName();
        }
        return containerClassName;
    }

    /**
     *
     * @return java.lang.reflect.Constructor
     */
    protected Constructor getContainerConstructor() {
        return containerConstructor;
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        IndirectContainer container = (IndirectContainer)unitOfWorkIndirectionObject;
        if (container.getValueHolder() instanceof UnitOfWorkValueHolder) {
            ValueHolderInterface valueHolder = ((UnitOfWorkValueHolder)container.getValueHolder()).getWrappedValueHolder();
            if ((valueHolder == null) && session.isRemoteUnitOfWork()) {
                RemoteSessionController controller = ((RemoteUnitOfWork)session).getParentSessionController();
                valueHolder = controller.getRemoteValueHolders().get(((UnitOfWorkValueHolder)container.getValueHolder()).getWrappedValueHolderRemoteID());
            }
            return buildContainer(valueHolder);
        } else {
            return container;
        }
    }

    /**
     * INTERNAL:
     *    Return the original indirection object for a unit of work indirection object.
     */
    public Object getOriginalIndirectionObjectForMerge(Object unitOfWorkIndirectionObject, AbstractSession session) {
        IndirectContainer container = (IndirectContainer) getOriginalIndirectionObject(unitOfWorkIndirectionObject, session);
        DatabaseValueHolder holder = (DatabaseValueHolder)container.getValueHolder();
        if (holder != null && holder.getSession()!= null){
            holder.setSession(session);
        }
        return container;
    }


    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.
     * This will trigger the wrapper to instantiate the value.
     */
    public Object getRealAttributeValueFromObject(Object object, Object attribute) {
        return ((IndirectContainer)attribute).getValueHolder().getValue();
    }

    /**
     * INTERNAL:
     * Ensure the container class implements IndirectContainer and that it
     * has a constructor which can be used.
     */
    public void initialize() {
        // Verify that the provided class implements IndirectContainer
        if (!ClassConstants.IndirectContainer_Class.isAssignableFrom(containerClass)) {
            throw DescriptorException.invalidIndirectionContainerClass(this, containerClass);
        }

        // Try to find constructor which takes a ValueHolderInterface
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    this.containerConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(getContainerClass(), new Class[] { ClassConstants.ValueHolderInterface_Class }, false));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof NoSuchMethodException){
                        throw (NoSuchMethodException) ex.getCause();
                    }
                    throw (RuntimeException)ex.getCause();
                }
            }else{
                this.containerConstructor = PrivilegedAccessHelper.getConstructorFor(getContainerClass(), new Class[] { ClassConstants.ValueHolderInterface_Class }, false);
            }
            return;
        } catch (NoSuchMethodException nsme) {// DO NOTHING, exception thrown at end
        }

        // Try to find the default constructor
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    this.containerConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(getContainerClass(), new Class[0], false));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof NoSuchMethodException){
                        throw (NoSuchMethodException) ex.getCause();
                    }
                    throw (RuntimeException)ex.getCause();
                }
            }else{
                this.containerConstructor = PrivilegedAccessHelper.getConstructorFor(getContainerClass(), new Class[0], false);
            }
            return;
        } catch (NoSuchMethodException nsme) {// DO NOTHING, exception thrown at end
        }

        // If no constructor is found then we throw an initialization exception
        throw DescriptorException.noConstructorIndirectionContainerClass(this, containerClass);
    }

    /**
     * INTERNAL:
     * The method validateAttributeOfInstantiatedObject(Object attributeValue) fixes the value of the attributeValue 
     * in cases where it is null and indirection requires that it contain some specific data structure.  Return whether this will happen.
     * This method is used to help determine if indirection has been triggered
     * @param attributeValue
     * @return
     * @see validateAttributeOfInstantiatedObject(Object attributeValue)
     */
    @Override
    public boolean isAttributeValueFullyBuilt(Object attributeValue){
        return true;
    }
    
    
    /**
     * INTERNAL:
     *    Iterate over the specified attribute value,
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        super.iterateOnAttributeValue(iterator, ((IndirectContainer)attributeValue).getValueHolder());
    }

    /**
     * INTERNAL:
     *    Return the null value of the appropriate attribute. That is, the
     * field from the database is NULL, return what should be
     * placed in the object's attribute as a result.
     * In this case, return an empty ValueHolder.
     */
    public Object nullValueFromRow() {
        return buildContainer(new ValueHolder());
    }

    /**
     * Reset the wrapper used to store the value.
     */
    public void reset(Object target) {
        getMapping().setAttributeValueInObject(target, buildContainer(new ValueHolder()));
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public boolean objectIsInstantiated(Object object) {
        return ((IndirectContainer)object).getValueHolder().isInstantiated();
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public boolean objectIsEasilyInstantiated(Object object) {
        ValueHolderInterface valueHolder = ((IndirectContainer)object).getValueHolder();
        if (valueHolder instanceof DatabaseValueHolder) {
            return ((DatabaseValueHolder)valueHolder).isEasilyInstantiated();
        } else {
            return true;
        }
    }

    /**
     * Sets the Container class which implements IndirectContainer
     */
    public void setContainerClass(Class containerClass) {
        this.containerClass = containerClass;
    }

    /**
     * Set the container classname for the MW
     */
    public void setContainterClassName(String containerClassName) {
        this.containerClassName = containerClassName;
    }

    /**
     * INTERNAL:
     *    Set the value of the appropriate attribute of target to attributeValue.
     * In this case, place the value inside the target's ValueHolder.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {
        IndirectContainer container = (IndirectContainer)this.getMapping().getAttributeValueFromObject(target);
        container.getValueHolder().setValue(attributeValue);
        this.getMapping().setAttributeValueInObject(target, container);
    }

    /**
     * INTERNAL:
     * Return whether the type is appropriate for the indirection policy.
     * In this case, the type must either be assignable from IndirectContainer or
     * allow the conatinerClass to be assigned to it.
     */
    protected boolean typeIsValid(Class attributeType) {
        return ClassConstants.IndirectContainer_Class.isAssignableFrom(attributeType) || attributeType.isAssignableFrom(getContainerClass());
    }

    /**
     * INTERNAL:
     *    Verify that the value of the attribute within an instantiated object
     * is of the appropriate type for the indirection policy.
     * In this case, the attribute must be non-null and it must be a
     * ValueHolderInterface.
     */
    public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
        if (!(getContainerClass().isInstance(attributeValue))) {
            throw DescriptorException.valueHolderInstantiationMismatch(attributeValue, this.getMapping());
        }
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the batchQuery.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        ValueHolderInterface valueHolder = (ValueHolderInterface)super.valueFromBatchQuery(batchQuery, row, originalQuery, parentCacheKey);
        return buildContainer(valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the appropriate
     * method on the object and passing it the row and session.
     * In this case, wrap the row in a ValueHolder for later use.
     */
    public Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session) {
        ValueHolderInterface valueHolder = (ValueHolderInterface)super.valueFromMethod(object, row, session);
        return buildContainer(valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the query.
     * In this case, wrap the query in a ValueHolder for later invocation.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
        ValueHolderInterface valueHolder = (ValueHolderInterface)super.valueFromQuery(query, row, session);
        return buildContainer(valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     *    This value is determined by the row.
     * In this case, simply wrap the object in a ValueHolder.
     */
    public Object valueFromRow(Object object) {
        return buildContainer(new ValueHolder(object));
    }
}
