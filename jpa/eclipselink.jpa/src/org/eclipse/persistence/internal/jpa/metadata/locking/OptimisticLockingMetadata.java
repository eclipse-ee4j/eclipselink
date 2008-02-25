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
package org.eclipse.persistence.internal.jpa.metadata.locking;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;


import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;
import org.eclipse.persistence.descriptors.AllFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;



/**
 * Object to hold onto optimistic locking metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class OptimisticLockingMetadata  {
    private Boolean m_cascade;
    private List<ColumnMetadata> m_selectedColumns;
    private Enum m_type;
    
    /**
     * INTERNAL:
     */
    public OptimisticLockingMetadata() {}
    
    /**
     * INTERNAL:
     */
    public OptimisticLockingMetadata(Object optimisticLocking) {
        setType((Enum)invokeMethod("type", optimisticLocking, (Object[])null));
        setCascade((Boolean)invokeMethod("cascade", optimisticLocking, (Object[])null));
        setSelectedColumns((Object[])invokeMethod("selectedColumns", optimisticLocking, (Object[])null));   
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnMetadata> getSelectedColumns() {
        return m_selectedColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getType() {
        return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasSelectedColumns() {
        return ! m_selectedColumns.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascade() {
        return m_cascade;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor) {
        // Process the type. A null will default to VERSION_COLUMN.
        if (m_type == null || m_type.equals(OptimisticLockingType.VERSION_COLUMN)) {
            // A version annotation or element should be define and discovered
            // in later processing.
            descriptor.setUsesCascadedOptimisticLocking(m_cascade != null && m_cascade.booleanValue());
        } else if (m_type.equals(OptimisticLockingType.ALL_COLUMNS)) {
            descriptor.setOptimisticLockingPolicy(new AllFieldsLockingPolicy());
        } else if (m_type.equals(OptimisticLockingType.CHANGED_COLUMNS)) {
            descriptor.setOptimisticLockingPolicy(new ChangedFieldsLockingPolicy());
        } else if (m_type.equals(OptimisticLockingType.SELECTED_COLUMNS)) {
            if (m_selectedColumns.isEmpty()) {
                throw ValidationException.optimisticLockingSelectedColumnNamesNotSpecified(descriptor.getJavaClass());
            } else {
                SelectedFieldsLockingPolicy policy = new SelectedFieldsLockingPolicy();
                
                // Process the selectedColumns
                for (ColumnMetadata selectedColumn : m_selectedColumns) {
                    if (selectedColumn.getName().equals("")) {  
                        throw ValidationException.optimisticLockingSelectedColumnNamesNotSpecified(descriptor.getJavaClass());
                    } else {
                        policy.addLockFieldName(selectedColumn.getName());
                    }
                }
                
                descriptor.setOptimisticLockingPolicy(policy);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setCascade(Boolean cascade) {
    	m_cascade = cascade;
    }
    
    /**
     * INTERNAL:
     * Called from annotation population.
     */
    protected void setSelectedColumns(Object[] selectedColumns) {
        m_selectedColumns = new ArrayList<ColumnMetadata>();
    		
        for (Object selectedColumn : selectedColumns) {
            m_selectedColumns.add(new ColumnMetadata(selectedColumn));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSelectedColumns(List<ColumnMetadata> selectedColumns) {
        m_selectedColumns = selectedColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(Enum type) {
        m_type = type;
    }
    
    /** 
     * INTERNAL:
     * Invoke the specified named method on the object,
     * handling the necessary exceptions.
     */
    private Object invokeMethod(String methodName, Object target ,Object[] params) {
        ArrayList<Class<?>> parmClasses = new ArrayList<Class<?>>();
        if(params!=null){
            for(Object parm : params) {
                parmClasses.add(parm.getClass());
            }
        }
        Method method=null;
        try {
            method = Helper.getDeclaredMethod(target.getClass(), methodName,
                    parmClasses.size() == 0 ? (Class<?>[])null : (Class<?>[]) parmClasses.toArray());            
        } catch (NoSuchMethodException e) {
            EntityManagerSetupException.methodInvocationFailed(method, target,e);
        }
        if(method!=null){
             try {
                 if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                     try {
                         return AccessController.doPrivileged(new PrivilegedMethodInvoker(method, target, params));
                     } catch (PrivilegedActionException exception) {
                         Exception throwableException = exception.getException();
                         if (throwableException instanceof IllegalAccessException) {
                             throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
                         } else {
                             throw EntityManagerSetupException.methodInvocationFailed(method, target, throwableException);
                         }
                     }
                 } else {
                     return PrivilegedAccessHelper.invokeMethod(method, target, params);
                 }
             } catch (IllegalAccessException ex1) {
                 throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
             } catch (InvocationTargetException ex2) {
                 throw EntityManagerSetupException.methodInvocationFailed(method, target, ex2);
             }
        }else{
            return null;
        }
    }

}
