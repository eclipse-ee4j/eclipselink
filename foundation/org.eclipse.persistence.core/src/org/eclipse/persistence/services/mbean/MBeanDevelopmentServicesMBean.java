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
package org.eclipse.persistence.services.mbean;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This interface is meant to provide a framework for gaining access to configuration and
 * statistics of the EclipseLink Cache during runtime.  It provides JMX functionality
 * <ul>
 * <li>
 * </ul>
 */
public interface MBeanDevelopmentServicesMBean {

    /**
     *        This method is used to clear the contents of a particular identity map.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void initializeIdentityMap(String className) throws ClassNotFoundException;

    /**
     *        This method is used to clear the contents of all identity maps.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     */
    public void initializeAllIdentityMaps();

    /**
     *        This method will be used to set a new type of identity map for a particular class type.  If objects
     * of that class type are in use loss of object identity will result.  For prevention client may wish to
     * initialize all identity maps first.
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void setIdentityMapForClass(String className, String identityMapClassType, int maxSize) throws ClassNotFoundException;

    /**
     *        This method used to reset a project in a session. All connected clients will get errors.
     * project must be of the xml type.  This attribute will not be stored on a save.
     */
    public void refreshProject(String projectFilePath);

    /**
     *        This method is used to update the cache size of a particular Identity Map
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void updateCacheSize(String className, int newSize) throws ClassNotFoundException;

    /**
     *     This method is used to control if All parameters should be bound
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters);

    /**
     *     Method returns if all Parameters should be bound or not
     */
    public boolean getShouldBindAllParameters();

    /**
     *        Sets the size of strings after which they will be bound into the statement
     */
    public void setStringBindingSize(int size);

    /**
     *     Return the size of strings after which will be bound into the statement
     */
    public int getStringBindingSize();

    /**
     *        This method is used to turn on TopLink Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     * Also please note that TopLink Batch Writing may not work with all JDBC Drivers
     */
    public void setUsesBatchWriting(boolean usesBatchWriting);

    /**
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesBatchWriting();

    /**
     *        This method is used to turn on JDBC Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     *
     */
    public void setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting);

    /**
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesJDBCBatchWriting();

    /**
     *        This method allows control of whether byte arrays should be bound into the statement or not.
     * Toggling this attribute while an SQL statement is being built, or if you are caching statements, will
     * result in errors.
     */
    public void setUsesByteArrayBinding(boolean usesByteArrayBinding);

    /**
     *     Shows if Byte Array Binding is turned on or not
     */
    public boolean getUsesByteArrayBinding();

    /**
     *     This method allows the client to set if Native SQL should be used.
     */
    public void setUsesNativeSQL(boolean usesNativeSQL);

    /**
     *     Shows if native SQL is being used
     */
    public boolean getUsesNativeSQL();

    /**
     *     This method is used to set if streams should be used for binding.  Please note that toggling this
     * attribute while a statement is being built will result in errors.
     */
    public void setUsesStreamsForBinding(boolean usesStreamsForBinding);

    /**
     *     This method indicates if streams are being used for binding
     */
    public boolean getUsesStreamsForBinding();

    /**
     *     Allows the client to set if String should be bound into the statement or not.  Please note that
     * toggling this attribute while a statement is being built, or if statement caching is being used, will result
     * in errors.
     */
    public void setUsesStringBinding(boolean usesStringBinding);

    /**
     *     This method indicates if Strings are being bound
     */
    public boolean getUsesStringBinding();
}