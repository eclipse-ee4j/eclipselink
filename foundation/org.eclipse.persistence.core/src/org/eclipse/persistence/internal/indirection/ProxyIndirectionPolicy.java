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

import java.lang.reflect.Proxy;
import java.security.AccessController;

import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.identitymaps.CacheKey;

import java.util.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.internal.sessions.remote.*;

/**
 * <H2>ProxyIndirectionPolicy</H2>
 *
 * Define the behavior for Proxy Indirection.<P>
 *
 * Proxy Indirection uses the <CODE>Proxy</CODE> and <CODE>InvocationHandler</CODE> features
 * of JDK 1.3 to provide "transparent indirection" for 1:1 relationships.  In order to use Proxy
 * Indirection:<P>
 *
 * <UL>
 *        <LI>The target class must implement at least one public interface
 *        <LI>The attribute on the source class must be typed as that public interface
 * </UL>
 *
 * In this policy, proxy objects are returned during object creation.  When a message other than
 * <CODE>toString</CODE> is called on the proxy the real object data is retrieved from the database.
 *
 * @see            org.eclipse.persistence.internal.indirection.ProxyIndirectionHandler
 * @author        Rick Barkhouse
 * @since        TopLink 3.0
 */
public class ProxyIndirectionPolicy extends BasicIndirectionPolicy {
    private Class[] targetInterfaces;

    public ProxyIndirectionPolicy(Class[] targetInterfaces) {
        this.targetInterfaces = targetInterfaces;
    }

    public ProxyIndirectionPolicy() {
        this.targetInterfaces = new Class[] {  };
    }

    /**
     * INTERNAL:
     * Nothing required.
     */
    public void initialize() {
        // Nothing required
    }
    
    /**
     * Reset the wrapper used to store the value.
     */
    public void reset(Object target) {
        // Nothing required.
    }

    /**
     * INTERNAL:
     * Return if targetInterfaces is not empty.
     */
    public boolean hasTargetInterfaces() {
        return (targetInterfaces != null) && (targetInterfaces.length != 0);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This will be a proxy object.
     */
    public Object valueFromRow(Object object) {
        ValueHolderInterface valueHolder = new ValueHolder(object);

        return ProxyIndirectionHandler.newProxyInstance(object.getClass(), targetInterfaces, valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This will be a proxy object.
     */
    public Object valueFromQuery(ReadQuery query, AbstractRecord row, AbstractSession session) {
        ClassDescriptor descriptor = null;
        try {
            // Need an instance of the implementing class
            //CR#3838
            descriptor = session.getDescriptor(query.getReferenceClass());
            if (descriptor.isDescriptorForInterface()) {
                descriptor = (ClassDescriptor)descriptor.getInterfacePolicy().getChildDescriptors().firstElement();
            }
        } catch (Exception e) {
            return null;
        }
        ValueHolderInterface valueHolder = new QueryBasedValueHolder(query, row, session);

        return ProxyIndirectionHandler.newProxyInstance(descriptor.getJavaClass(), targetInterfaces, valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by invoking the appropriate method on the object and passing it the
     * row and session.
     */
    public Object valueFromMethod(Object object, AbstractRecord row, AbstractSession session) {
        ValueHolderInterface valueHolder = new TransformerBasedValueHolder(this.getTransformationMapping().getAttributeTransformer(), object, row, session);

        return ProxyIndirectionHandler.newProxyInstance(object.getClass(), targetInterfaces, valueHolder);
    }

    /**
     * INTERNAL:
     * Return the value to be stored in the object's attribute.
     * This value is determined by the batch query.     *
     * NOTE: Currently not supported anyway.
     */
    public Object valueFromBatchQuery(ReadQuery batchQuery, AbstractRecord row, ObjectLevelReadQuery originalQuery, CacheKey parentCacheKey) {
        Object object;

        try {
            // Need an instance of the implementing class
            ClassDescriptor d = originalQuery.getDescriptor();
            if (d.isDescriptorForInterface()) {
                d = (ClassDescriptor)originalQuery.getDescriptor().getInterfacePolicy().getChildDescriptors().firstElement();
            }
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                object = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(d.getJavaClass()));
            }else{
                object = PrivilegedAccessHelper.newInstanceFromClass(d.getJavaClass());
            }
        } catch (Exception e) {
            //org.eclipse.persistence.internal.helper.Helper.toDo("*** Should probably throw some sort of TopLink exception here. ***");
            e.printStackTrace();
            return null;
        }
        ValueHolderInterface valueHolder = new BatchValueHolder(batchQuery, row, this.getForeignReferenceMapping(), originalQuery, parentCacheKey);

        return ProxyIndirectionHandler.newProxyInstance(object.getClass(), targetInterfaces, valueHolder);
    }

    /**
     * INTERNAL:
     * Return whether the specified object is instantiated.
     */
    public boolean objectIsInstantiated(Object object) {
        if (object instanceof Proxy) {
            ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(object);
            ValueHolderInterface valueHolder = handler.getValueHolder();
            return valueHolder.isInstantiated();
        } else {
            return true;
        }
    }
    
    /**
     * INTERNAL:
     * Return whether the specified object can be instantiated without database access.
     */
    public boolean objectIsEasilyInstantiated(Object object) {
        if (object instanceof Proxy) {
            ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(object);
            ValueHolderInterface valueHolder = handler.getValueHolder();
            if (valueHolder instanceof DatabaseValueHolder) {
                return ((DatabaseValueHolder)valueHolder).isEasilyInstantiated();
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Return the null value of the appropriate attribute. That is, the field from the database is NULL,
     * return what should be placed in the object's attribute as a result.
     */
    public Object nullValueFromRow() {
        return null;
    }

    /**
     * INTERNAL:
     * Replace the client value holder with the server value holder after copying some of the settings from
     * the client value holder.
     */
    public void mergeRemoteValueHolder(Object clientSideDomainObject, Object serverSideDomainObject, MergeManager mergeManager) {
        getMapping().setAttributeValueInObject(clientSideDomainObject, serverSideDomainObject);
    }

    /**
     * INTERNAL:
     * Return the "real" attribute value, as opposed to any wrapper.  This will trigger the wrapper to
     * instantiate the value.
     */
    public Object getRealAttributeValueFromObject(Object obj, Object object) {
        if (object instanceof Proxy) {
            ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(object);
            ValueHolderInterface valueHolder = handler.getValueHolder();
            return valueHolder.getValue();
        } else {
            return object;
        }
    }

    /**
     * INTERNAL:
     * Given a proxy object, trigger the indirection and return the actual object represented by the proxy.
     */
    public static Object getValueFromProxy(Object value) {
        if (Proxy.isProxyClass(value.getClass())) {
            return ((ProxyIndirectionHandler)Proxy.getInvocationHandler(value)).getValueHolder().getValue();
        }
        return value;
    }

    /**
     * INTERNAL:
     * Set the "real" value of the attribute to attributeValue.
     */
    public void setRealAttributeValueInObject(Object target, Object attributeValue) {
        this.getMapping().setAttributeValueInObject(target, attributeValue);
    }

    /**
     * INTERNAL:
     * Return the original indirection object for a unit of work indirection object.
     */
    public Object getOriginalIndirectionObject(Object unitOfWorkIndirectionObject, AbstractSession session) {
        if (unitOfWorkIndirectionObject instanceof UnitOfWorkValueHolder) {
            ValueHolderInterface valueHolder = ((UnitOfWorkValueHolder)unitOfWorkIndirectionObject).getWrappedValueHolder();
            if ((valueHolder == null) && session.isRemoteUnitOfWork()) {
                RemoteSessionController controller = ((RemoteUnitOfWork)session).getParentSessionController();
                valueHolder = controller.getRemoteValueHolders().get(((UnitOfWorkValueHolder)unitOfWorkIndirectionObject).getWrappedValueHolderRemoteID());
            }
            return valueHolder;
        } else {
            return unitOfWorkIndirectionObject;
        }
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the client.  Replace the transient attributes of the
     * remote value holders with client-side objects.
     */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        //org.eclipse.persistence.internal.helper.Helper.toDo("*** Something tells me this isn't going to work. *** [X]");
    }

    /**
     * INTERNAL:
     * Return the reference row for the reference object.  This allows the new row to be built without
     * instantiating the reference object.  Return null if the object has already been instantiated.
     */
    public AbstractRecord extractReferenceRow(Object referenceObject) {
        if ((referenceObject == null) || !Proxy.isProxyClass(referenceObject.getClass())) {
            return null;
        }

        ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(referenceObject);
        ValueHolderInterface valueHolder = handler.getValueHolder();

        if (valueHolder.isInstantiated()) {
            return null;
        } else {
            return ((DatabaseValueHolder)valueHolder).getRow();
        }
    }

    /**
     * INTERNAL:
     *    Return a clone of the attribute.
     *  @param buildDirectlyFromRow indicates that we are building the clone
     *  directly from a row as opposed to building the original from the
     *  row, putting it in the shared cache, and then cloning the original.
     */
    public Object cloneAttribute(Object attributeValue, Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession, boolean buildDirectlyFromRow) {
        if (!(attributeValue instanceof Proxy)) {
            boolean isExisting = !cloningSession.isUnitOfWork() || (((UnitOfWorkImpl)cloningSession).isObjectRegistered(clone) && (!((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original)));
            return this.getMapping().buildCloneForPartObject(attributeValue, original, null, clone, cloningSession, refreshCascade, isExisting, !buildDirectlyFromRow);
        }

        ValueHolderInterface newValueHolder;
        ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(attributeValue);
        ValueHolderInterface oldValueHolder = handler.getValueHolder();
        
        if (!buildDirectlyFromRow && cloningSession.isUnitOfWork() && ((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original)) {
            // CR#3156435 Throw a meaningful exception if a serialized/dead value holder is detected.
            // This can occur if an existing serialized object is attempt to be registered as new.
            if ((oldValueHolder instanceof DatabaseValueHolder)
                    && (! ((DatabaseValueHolder) oldValueHolder).isInstantiated())
                    && (((DatabaseValueHolder) oldValueHolder).getSession() == null)
                    && (! ((DatabaseValueHolder) oldValueHolder).isSerializedRemoteUnitOfWorkValueHolder())) {
                throw DescriptorException.attemptToRegisterDeadIndirection(original, getMapping());
            }
            newValueHolder = new ValueHolder();
            newValueHolder.setValue(this.getMapping().buildCloneForPartObject(oldValueHolder.getValue(), original, null, clone, cloningSession, refreshCascade, false, false));
        } else {
        	AbstractRecord row = null;
            if (oldValueHolder instanceof DatabaseValueHolder) {
                row = ((DatabaseValueHolder)oldValueHolder).getRow();
            }
            newValueHolder = this.getMapping().createCloneValueHolder(oldValueHolder, original, clone, row, cloningSession, buildDirectlyFromRow);
        }

        return ProxyIndirectionHandler.newProxyInstance(attributeValue.getClass(), targetInterfaces, newValueHolder);
    }

    /**
     * INTERNAL:
     * Return a backup clone of the attribute.
     */
    public Object backupCloneAttribute(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        if (!(attributeValue instanceof Proxy)) {
            return this.getMapping().buildBackupCloneForPartObject(attributeValue, clone, backup, unitOfWork);
        }
        ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(attributeValue);
        ValueHolderInterface unitOfWorkValueHolder = handler.getValueHolder();
        ValueHolderInterface backupValueHolder = null;
        
        if ((!(unitOfWorkValueHolder instanceof UnitOfWorkValueHolder)) || unitOfWorkValueHolder.isInstantiated()) {
            backupValueHolder = (ValueHolderInterface) super.backupCloneAttribute(unitOfWorkValueHolder, clone, backup, unitOfWork);
        } else {
            // CR#2852176 Use a BackupValueHolder to handle replacing of the original.
            backupValueHolder = new BackupValueHolder(unitOfWorkValueHolder);
            ((UnitOfWorkValueHolder)unitOfWorkValueHolder).setBackupValueHolder((BackupValueHolder) backupValueHolder);
        }

        return ProxyIndirectionHandler.newProxyInstance(attributeValue.getClass(), targetInterfaces, backupValueHolder);
    }

    /**
     * INTERNAL:
     * Iterate over the specified attribute value.
     */
    public void iterateOnAttributeValue(DescriptorIterator iterator, Object attributeValue) {
        if (attributeValue instanceof Proxy) {
            ProxyIndirectionHandler handler = (ProxyIndirectionHandler)Proxy.getInvocationHandler(attributeValue);
            ValueHolderInterface valueHolder = handler.getValueHolder();

            iterator.iterateValueHolderForMapping(valueHolder, this.getMapping());
        } else {
            if (attributeValue != null) {
                this.getMapping().iterateOnRealAttributeValue(iterator, attributeValue);
            }
        }
    }

    /**
     * INTERNAL:
     * Verify that the value of the attribute within an instantiated object is of the appropriate type for
     * the indirection policy.  In this case, the attribute must non-null and implement some public interface.
     */
    public Object validateAttributeOfInstantiatedObject(Object attributeValue) {
        if ((attributeValue != null) && (attributeValue.getClass().getInterfaces().length == 0) && attributeValue instanceof Proxy) {
            //org.eclipse.persistence.internal.helper.Helper.toDo("*** Need a new DescriptorException here. ***");
            //			throw DescriptorException.valueHolderInstantiationMismatch(attributeValue, this.getMapping());
            System.err.println("** ProxyIndirection attribute validation failed.");
        }
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Verify that attribute type is correct for the indirection policy. If it is incorrect, add an exception to the
     * integrity checker.  In this case, the attribute type must be contained in targetInterfaces.
     */
    public void validateDeclaredAttributeType(Class attributeType, IntegrityChecker checker) throws DescriptorException {
        if (!isValidType(attributeType)) {
            checker.handleError(DescriptorException.invalidAttributeTypeForProxyIndirection(attributeType, targetInterfaces, getMapping()));
        }
    }

    /**
     * INTERNAL:
     * Verify that the return type of the attribute's get method is correct for the indirection policy. If it is
     * incorrect, add an exception to the integrity checker.  In this case, the return type must be a
     * public interface.
     */
    public void validateGetMethodReturnType(Class returnType, IntegrityChecker checker) throws DescriptorException {
        if (!isValidType(returnType)) {
            checker.handleError(DescriptorException.invalidGetMethodReturnTypeForProxyIndirection(returnType, targetInterfaces, getMapping()));
        }
    }

    /**
     * INTERNAL:
     * Verify that the parameter type of the attribute's set method is correct for the indirection policy. If it is
     * incorrect, add an exception to the integrity checker.  In this case, the parameter type must be a
     * public interface.
     */
    public void validateSetMethodParameterType(Class parameterType, IntegrityChecker checker) throws DescriptorException {
        if (!isValidType(parameterType)) {
            checker.handleError(DescriptorException.invalidSetMethodParameterTypeForProxyIndirection(parameterType, targetInterfaces, getMapping()));
        }
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
     * Verify that a class type is valid to use for the proxy.  The class must be one of the
     * interfaces in <CODE>targetInterfaces</CODE>.
     */
    public boolean isValidType(Class attributeType) {
        if (!attributeType.isInterface()) {
            return false;
        }
        for (int i = 0; i < targetInterfaces.length; i++) {
            if (attributeType == targetInterfaces[i]) {
                return true;
            }
        }
        return false;
    }
}
