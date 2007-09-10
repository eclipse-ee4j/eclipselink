/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.services;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.sessions.remote.CacheSynchronizationManager;
import org.eclipse.persistence.sessions.remote.AbstractClusteringService;
import org.eclipse.persistence.exceptions.SynchronizationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the TopLink Identity Map Manager.
 * <p>
 * <b>Description</b>: This class is ment to provide a framework for gaining access to configuration and
 * statistics of the TopLink Cache during runtime.  It will provide the basis for developement
 * of a JMX service and possibly other frameworks.
 *
 * @deprecated Will be replaced by a server-specific equivalent for deprecated.services.oc4j.Oc4jRuntimeServices
 * @see deprecated.services.oc4j.Oc4jRuntimeServices
 */
public class DevelopmentServices {

    /** stores access to the session object that we are controlling */
    protected AbstractSession session;

    /**
     * PUBLIC:
         * Default constructor
     */
    public DevelopmentServices() {
    }

    /**
     * PUBLIC:
     * Constructor
     * @param session the session for these services
     */
    public DevelopmentServices(AbstractSession session) {
        this.session = session;
    }

    /**
     * PUBLIC:
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
     * PUBLIC:
     *        This method is used to clear the contents of all identity maps.  Users should be
     * aware that if any of these objects are in use when this action is completed they will lose object identity.
     */
    public void initializeAllIdentityMaps() {
        getSession().getIdentityMapAccessorInstance().initializeIdentityMaps();
    }

    /**
     * PUBLIC:
     *        This method will be used to set a new type of identity map for a particular class type.  If objects
     * of that class type are in use loss of object identity will result.  For prevention client may wish to
     * initialize all identity maps first.
     * @param className the fully qualified className to set the identity map for.
     * @param identityMapClassType the fully qualified class name of the new identity map type.
         * @param maxSize the maximum size to be specified for the new idenity map.
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
        org.eclipse.persistence.descriptors.ClassDescriptor descriptor = getSession().getDescriptor(classToChange);
        descriptor.setIdentityMapClass(identityMapClass);
        descriptor.setIdentityMapSize(maxSize);
        getSession().getIdentityMapAccessorInstance().initializeIdentityMap(classToChange);
    }

    /**
     * PUBLIC:
     *        This method used to reset a project in a session. All connected clients will get errors.
     * project must be of the xml type.  This attribute will not be stored on a save.
     * @param projectFilePath the filename of the project xml file to build the new project from.
     */
    public void refreshProject(String projectFilePath) {
        ((DatabaseSessionImpl)getSession()).logout();
        getSession().setProject(org.eclipse.persistence.sessions.factories.XMLProjectReader.read(projectFilePath));
        ((DatabaseSessionImpl)getSession()).login();
    }

    /**
     * PUBLIC:
     *        This method is used to update the cache size of a particular Identity Map
         * @param className the name of the class for which to update the identity map size.
     * @excpetion ClassNotFoundException thrown then the IdenityMap for that class name could not be found
     */
    public void updateCacheSize(String className, int newSize) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange).updateMaxSize(newSize);
    }

    /**
     * PUBLIC:
     *        This method is used to modify the cache synchronization feature.  It allows the user to update the method
     * of sending changes.  Asynchronous means that the method may return to the client before that changes have been
     * sent to all servers.
     * @param isAsynchronous should be set to true for Asynchronous updates and false otherwise.
     */
    public void setChangeSetPropagationShouldBeAsynchronous(boolean isAsynchronous) {
        if (getSession().getCacheSynchronizationManager() != null) {
            getSession().getCacheSynchronizationManager().setIsAsynchronous(isAsynchronous);
        }
    }

    /**
     * PUBLIC:
     *        This method indicates if Propigation of Cache Synch messages should be Asynchronous or not
     */
    public boolean getChangeSetPropagationShouldBeAsynchronous() {
        return (getSession().getCacheSynchronizationManager() != null) && getSession().getCacheSynchronizationManager().isAsynchronous();
    }

    /**
     * PUBLIC:
     *        This method is used to modify the cache synchronization feature.  It allows the user to update whether
     * remote connections should be dropped on error.
     * @param shouldRemoveConnection should be set to true if the connection should be removed on an error. Should be set to false if the connection
         * should be maintained
     */
    public void setShouldRemoveConnectionOnError(boolean shouldRemoveConnection) {
        if (getSession().getCacheSynchronizationManager() != null) {
            getSession().getCacheSynchronizationManager().setShouldRemoveConnectionOnError(shouldRemoveConnection);
        }
    }

    /**
     * PUBLIC:
     * This method indicates if remote connections should be dropped when there is an error.
     * @return boolean true if connections should be dropped on an error. Returns false otherwise.
     */
    public boolean getShouldRemoveConnectionOnError() {
        return (getSession().getCacheSynchronizationManager() != null) && getSession().getCacheSynchronizationManager().shouldRemoveConnectionOnError();
    }

    /**
     * PUBLIC:
     *        This method will be used to update the multicast group of the Synchronization Service.  This will control
     * what servers will be connected to each other.
     * @param multicastIP the IP address of the Multicast server
     * @param multicastPort the port to connect to on the Multicast server
     */
    public void updateSynchronizationDiscoveryMulticastGroup(String multicastIP, int multicastPort) {
        getSession().getCacheSynchronizationManager().getClusteringService().setMulticastGroupAddress(multicastIP);
        getSession().getCacheSynchronizationManager().getClusteringService().setMulticastPort(multicastPort);
    }

    /**
     * PUBLIC:
     *        This method will be used by clients to restart their synchronization service for a particular session.
     * This will cause the session to drop all remote connections used for synchronization
     */
    public void initializeCacheSynchronizationServices() throws org.eclipse.persistence.exceptions.SynchronizationException {
        CacheSynchronizationManager synchManager = getSession().getCacheSynchronizationManager();
        AbstractClusteringService clusteringService = null;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                clusteringService = (AbstractClusteringService)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(synchManager.getClusteringService().getClass()));
            }else{
                clusteringService = (AbstractClusteringService)PrivilegedAccessHelper.newInstanceFromClass(synchManager.getClusteringService().getClass());
            }
        } catch (Exception exception) {
            throw SynchronizationException.failToResetCacheSynch();
        }
        synchManager.getClusteringService().stopListening();
        clusteringService.setMulticastGroupAddress(synchManager.getClusteringService().getMulticastGroupAddress());
        clusteringService.setMulticastPort(synchManager.getClusteringService().getMulticastPort());
        clusteringService.setSession(getSession());
        synchManager.removeAllRemoteConnections();
        synchManager.initialize();

    }

    /**
     * PUBLIC:
     *     This method is used to control if All parameters should be bound
     */
    public void setShouldBindAllParameters(boolean shouldBindAllParameters) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setShouldBindAllParameters(shouldBindAllParameters);
    }

    /**
     * PUBLIC:
     *     Method returns if all Parameters should be bound or not
     */
    public boolean getShouldBindAllParameters() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return false;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).shouldBindAllParameters();
    }

    /**
     * PUBLIC:
     *        Sets the size of strings after which they will be bound into the statement
     */
    public void setStringBindingSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setStringBindingSize(size);
    }

    /**
     * PUBLIC:
     *     Return the size of strings after which will be bound into the statement
     */
    public int getStringBindingSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getStringBindingSize();
    }

    /**
     * PUBLIC:
     *        This method is used to turn on TopLink Batch Writing.  Please note that toggling this
     * setting while a transaction is open may result in mis-ordered sql statements or loss of statements.
     * Also please note that TopLink Batch Writing may not work with all JDBC Drivers
     */
    public void setUsesBatchWriting(boolean usesBatchWriting) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesBatchWriting(usesBatchWriting);
    }

    /**
     * PUBLIC:
     *        This method will return if batchWriting is in use or not.
     */
    public boolean getUsesBatchWriting() {
        return getSession().getDatasourceLogin().getPlatform().usesBatchWriting();
    }

    /**
     * PUBLIC:
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
     * PUBLIC:
     *        This method will return if batchWriting is in use or not.
      */
    public boolean getUsesJDBCBatchWriting() {
        return getSession().getDatasourceLogin().getPlatform().usesJDBCBatchWriting();
    }

    /**
     * PUBLIC:
     *        This method allows control of weither byte arrays should be bound into the statement or not.
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
     * PUBLIC:
     *     Shows if Byte Array Binding is turned on or not
     */
    public boolean getUsesByteArrayBinding() {
        return getSession().getDatasourceLogin().getPlatform().usesByteArrayBinding();
    }

    /**
     * PUBLIC:
     *     This method allows the client to set if Native SQL should be used.
     */
    public void setUsesNativeSQL(boolean usesNativeSQL) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesNativeSQL(usesNativeSQL);
    }

    /**
     * PUBLIC:
     *     Shows if native SQL is being used
     */
    public boolean getUsesNativeSQL() {
        return getSession().getDatasourceLogin().getPlatform().usesNativeSQL();
    }

    /**
     * PUBLIC:
     *     This method is used to set if streams should be used for binding.  Please note that toggeling this
     * attribute while a statement is being built will result in errors.
     */
    public void setUsesStreamsForBinding(boolean usesStreamsForBinding) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setUsesStreamsForBinding(usesStreamsForBinding);
    }

    /**
     * PUBLIC:
     *     This method indicates if streams are being used for binding
     */
    public boolean getUsesStreamsForBinding() {
        return getSession().getDatasourceLogin().getPlatform().usesStreamsForBinding();
    }

    /**
     * PUBLIC:
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
     * PUBLIC:
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
        return this.session;
    }
}
