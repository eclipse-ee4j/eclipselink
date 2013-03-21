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
 *     @author  mobrien
 *     @since   EclipseLink 1.0 enh# 235168
 ******************************************************************************/  
package org.eclipse.persistence.services;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This class is meant to provide a framework for gaining access to configuration and
 * statistics of the EclipseLink Cache during runtime.  It will provide the basis for development
 * of a JMX service and possibly other frameworks.
 *
 */
public class DevelopmentServices {

    /** stores access to the session object that we are controlling */
    protected Session session;

    /**
         * Default constructor
     */
    public DevelopmentServices() {
    }

    /**
     * Constructor
     * @param session the session for these services
     */
    public DevelopmentServices(Session session) {
        this.session = session;
    }

    /**
     *        This method is used to clear the contents of a particular identity map.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     * @param className the fully qualified name of the class for which the identity map should be cleared.
     * @exception ClassNotFoundException thrown then the IdenityMap for that class name could not be found
     */
    public void initializeIdentityMap(String className) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        getSession().getIdentityMapAccessorInstance().initializeIdentityMap(classToChange);
    }

    /**
     *        This method is used to clear the contents of all identity maps.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     */
    public void initializeAllIdentityMaps() {
        getSession().getIdentityMapAccessorInstance().initializeIdentityMaps();
    }

    /**
     *        This method will be used to set a new type of identity map for a particular class type.  If objects
     * of that class type are in use loss of object identity will result.  For prevention client may wish to
     * initialize all identity maps first.
     * @param className the fully qualified className to set the identity map for.
     * @param identityMapClassType the fully qualified class name of the new identity map type.
     * @param maxSize the maximum size to be specified for the new identity map.
     * @exception ClassNotFoundException thrown then the IdenityMap for that class name could not be found
     */
    public void setIdentityMapForClass(String className, String identityMapClassType, int maxSize) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        Class identityMapClass = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                identityMapClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(identityMapClassType));
            }catch (PrivilegedActionException ex){
                throw (RuntimeException)ex.getCause();
            }
        }else{
            identityMapClass = PrivilegedAccessHelper.getClassForName(identityMapClassType);
        }
        ClassDescriptor descriptor = getSession().getDescriptor(classToChange);
        descriptor.setIdentityMapClass(identityMapClass);
        descriptor.setIdentityMapSize(maxSize);
        getSession().getIdentityMapAccessorInstance().initializeIdentityMap(classToChange);
    }

    /**
     *        This method used to reset a project in a session. All connected clients will get errors.
     * project must be of the xml type.  This attribute will not be stored on a save.
     * @param projectFilePath the filename of the project xml file to build the new project from.
     */
    public void refreshProject(String projectFilePath) {
        ((DatabaseSessionImpl)getSession()).logout();
        getSession().setProject(XMLProjectReader.read(projectFilePath));
        ((DatabaseSessionImpl)getSession()).login();
    }

    /**
     *        This method is used to update the cache size of a particular Identity Map
     * @param className the name of the class for which to update the identity map size.
     * @exception ClassNotFoundException thrown then the IdenityMap for that class name could not be found
     */
    public void updateCacheSize(String className, int newSize) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange).updateMaxSize(newSize);
    }

    /**
     *     This method is used to control if All parameters should be bound
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setShouldBindAllParameters(shouldBindAllParameters);
    }

    /**
     *     Method returns if all Parameters should be bound or not
     */
    public boolean getShouldBindAllParameters() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return false;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).shouldBindAllParameters();
    }

    /**
     *        Sets the size of strings after which they will be bound into the statement
     */
    public void setStringBindingSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setStringBindingSize(size);
    }

    /**
     *     Return the size of strings after which will be bound into the statement
     */
    public int getStringBindingSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getStringBindingSize();
    }

    /**
     *        This method is used to turn on EclipseLink Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in unordered sql statements or loss of statements.
     * Also please note that EclipseLink Batch Writing may not work with all JDBC Drivers
     */
    public void setUsesBatchWriting(boolean usesBatchWriting) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesBatchWriting(usesBatchWriting);
    }

    /**
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesBatchWriting() {
        return getSession().getDatasourceLogin().getPlatform().usesBatchWriting();
    }

    /**
     *        This method is used to turn on JDBC Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     *
     */
    public void setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesJDBCBatchWriting(usesJDBCBatchWriting);
    }

    /**
     *        This method will return if batchWriting is in use or not.
      */
    public boolean getUsesJDBCBatchWriting() {
        return getSession().getDatasourceLogin().getPlatform().usesJDBCBatchWriting();
    }

    /**
     *        This method allows control of whether byte arrays should be bound into the statement or not.
     * Toggling this attribute while an SQL statement is being built, or if you are caching statements, will
     * result in errors.
     */
    public void setUsesByteArrayBinding(boolean usesByteArrayBinding) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesByteArrayBinding(usesByteArrayBinding);
    }

    /**
     *     Shows if Byte Array Binding is turned on or not
     */
    public boolean getUsesByteArrayBinding() {
        return getSession().getDatasourceLogin().getPlatform().usesByteArrayBinding();
    }

    /**
     *     This method allows the client to set if Native SQL should be used.
     */
    public void setUsesNativeSQL(boolean usesNativeSQL) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesNativeSQL(usesNativeSQL);
    }

    /**
     *     Shows if native SQL is being used
     */
    public boolean getUsesNativeSQL() {
        return getSession().getDatasourceLogin().getPlatform().usesNativeSQL();
    }

    /**
     *     This method is used to set if streams should be used for binding.  Please note that toggling this
     * attribute while a statement is being built will result in errors.
     */
    public void setUsesStreamsForBinding(boolean usesStreamsForBinding) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesStreamsForBinding(usesStreamsForBinding);
    }

    /**
     *     This method indicates if streams are being used for binding
     */
    public boolean getUsesStreamsForBinding() {
        return getSession().getDatasourceLogin().getPlatform().usesStreamsForBinding();
    }

    /**
     *     Allows the client to set if String should be bound into the statement or not.  Please note that
     * toggling this attribute while a statement is being built, or if statement caching is being used, will result
     * in errors.
     */
    public void setUsesStringBinding(boolean usesStringBinding) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesStringBinding(usesStringBinding);
    }

    /**
     *     This method indicates if Strings are being bound
     */
    public boolean getUsesStringBinding() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return false;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getPlatform().usesStringBinding();
    }

    /**
     * INTERNAL:
     */
    protected AbstractSession getSession() {
        return (AbstractSession)this.session;
    }
}
