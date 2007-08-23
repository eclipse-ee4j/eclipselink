/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.services.mbean;


/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the TopLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This interface is ment to provide a framework for gaining access to configuration and
 * statistics of the TopLink Cache during runtime.  It provides JMX functionality
 * <ul>
 * <li>
 * </ul>
 *
 * @deprecated Will be replaced by a server-specific equivalent for deprecated.services.oc4j.Oc4jRuntimeServices
 * @see deprecated.services.oc4j.Oc4jRuntimeServices
 */
public interface MBeanDevelopmentServicesMBean {

    /**
     * PUBLIC:
     *        This method is used to clear the contents of a particular identity map.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void initializeIdentityMap(String className) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method is used to clear the contents of all identity maps.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     */
    public void initializeAllIdentityMaps();

    /**
     * PUBLIC:
     *        This method will be used to set a new type of identity map for a particular class type.  If objects
     * of that class type are in use loss of object identity will result.  For prevention client may wish to
     * initialize all identity maps first.
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void setIdentityMapForClass(String className, String identityMapClassType, int maxSize) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method used to reset a project in a session. All connected clients will get errors.
     * project must be of the xml type.  This attribute will not be stored on a save.
     */
    public void refreshProject(String projectFilePath);

    /**
     * PUBLIC:
     *        This method is used to update the cache size of a particular Identity Map
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public void updateCacheSize(String className, int newSize) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method is ued to modify the cache synchronization feature.  It allows the user to update the method
     * of sending changes.  Asynchronous means that the method may return to the client before that changes have been
     * sent to all servers.
     */
    public void setChangeSetPropagationShouldBeAsynchronous(boolean isAsynchronous);

    /**
     * PUBLIC:
     *        This method indicates if Propigation of Cache Synch messages should be Asynchronous or not
     */
    public boolean getChangeSetPropagationShouldBeAsynchronous();

    /**
     * PUBLIC:
     *        This method is ued to modify the cache synchronization feature.  It allows the user to update weither
     * remote connections should be dropped on error.
     */
    public void setShouldRemoveConnectionOnError(boolean shouldRemoveConnection);

    public boolean getShouldRemoveConnectionOnError();

    /**
     * PUBLIC:
     *        This method will be used to update the multicast group of the Synchronization Service.  This will control
     * what servers will be connected to each other.
     */
    public void updateSynchronizationDiscoveryMulticastGroup(String multicastIP, int multicastPort);

    /**
     * PUBLIC:
     *        This method will be used by clients to restart their synchronization service for a particular session.
     * This will cause the session to drop all remote connections used for synchronization
     */
    public void initializeCacheSynchronizationServices() throws org.eclipse.persistence.exceptions.SynchronizationException;

    /**
     * PUBLIC:
     *     This method is used to controll if All parameters should be bound
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters);

    /**
     * PUBLIC:
     *     Method returns if all Parameters should be bound or not
     */
    public boolean getShouldBindAllParameters();

    /**
     * PUBLIC:
     *        Sets the size of strings after which they will be bound into the statement
     */
    public void setStringBindingSize(int size);

    /**
     * PUBLIC:
     *     Return the size of strings after which will be bound into the statement
     */
    public int getStringBindingSize();

    /**
     * PUBLIC:
     *        This method is used to turn on TopLink Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     * Also please note that TopLink Batch Writing may not work with all JDBC Drivers
     */
    public void setUsesBatchWriting(boolean usesBatchWriting);

    /**
     * PUBLIC:
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesBatchWriting();

    /**
     * PUBLIC:
     *        This method is used to turn on JDBC Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     *
     */
    public void setUsesJDBCBatchWriting(boolean usesJDBCBatchWriting);

    /**
     * PUBLIC:
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesJDBCBatchWriting();

    /**
     * PUBLIC:
     *        This method allows control of weither byte arrays should be bound into the statement or not.
     * Toggling this attribute while an SQL statement is being built, or if you are caching statements, will
     * result in errors.
     */
    public void setUsesByteArrayBinding(boolean usesByteArrayBinding);

    /**
     * PUBLIC:
     *     Shows if Byte Array Binding is turned on or not
     */
    public boolean getUsesByteArrayBinding();

    /**
     * PUBLIC:
     *     This method allows the client to set if Native SQL should be used.
     */
    public void setUsesNativeSQL(boolean usesNativeSQL);

    /**
     * PUBLIC:
     *     Shows if native SQL is being used
     */
    public boolean getUsesNativeSQL();

    /**
     * PUBLIC:
     *     This method is used to set if streams should be used for binding.  Please note that toggeling this
     * attribute while a statement is being built will result in errors.
     */
    public void setUsesStreamsForBinding(boolean usesStreamsForBinding);

    /**
     * PUBLIC:
     *     This method indicates if streams are being used for binding
     */
    public boolean getUsesStreamsForBinding();

    /**
     * PUBLIC:
     *     Allows the client to set if String should be bound into the statement or not.  Please note that
     * toggling this attribute while a statement is being built, or if statement caching is being used, will result
     * in errors.
     */
    public void setUsesStringBinding(boolean usesStringBinding);

    /**
     * PUBLIC:
     *     This method indicates if Strings are being bound
     */
    public boolean getUsesStringBinding();
}