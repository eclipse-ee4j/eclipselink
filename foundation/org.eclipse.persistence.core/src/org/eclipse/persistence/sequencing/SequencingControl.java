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
package org.eclipse.persistence.sequencing;

import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.server.ConnectionPool;

/**
 * <p>
 * <b>Purpose</b>: Define an interface to control sequencing functionality.
 * <p>
 * <b>Description</b>:  This interface is accessed through DatabaseSession.getSequencingControl().
 * It allows to create, re-create, customize Sequencing object
 * which is available through DatabaseSession.getSequencing()
 * and provides sequencing values for all descriptors that use sequencing.
 *
 * Here's the lifecycle of Sequencing object used by DatabaseSession:
 * 1. DatabaseSession created - sequencing object doesn't yet exist;
 * 2. DatabaseSession.login() causes creation of Sequencing object;
 * 3. DatabaseSession.logout() causes destruction of Sequencing object.
 *
 * In case sequencing object doesn't yet exist all the set parameters' values will be used
 * during its creation.
 *
 * In case sequencing object already exists:
 * 1. The following methods don't alter sequencing object - the corresponding parameters will only
 *    be used in case a new sequencing object is created:
 *      setShouldUseSeparateConnection;
 *      setLogin;
 *      setMinPoolSize;
 *      setMaxPoolSize.
 * 2. The following methods cause immediate destruction of the sequencing object and creation of a new one:
 *      setValueGenerationPolicy;
 *      setShouldUseNativeSequencing;
 *      setShouldUseTableSequencing;
 *      resetSequencing;
 * 3. The following methods cause change immediately:
 *      setPreallocationSize (next sequencing preallocation will use the set parameter's value).
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the APIs for controlling sequencing.
 * </ul>
 * @see Sequence
 * @see org.eclipse.persistence.sessions.DatabaseSession
 */
public interface SequencingControl {

    /**
     * ADVANCED:
     * Immediately re-create sequencing object.
     * The only reason to use this method is to pick up all parameters'
     * values that were changed after the original sequencing object has been created.
     */
    public void resetSequencing();

    /**
     * PUBLIC:
     * Indicate whether separate connection(s) for sequencing could be used
     * (by default it couldn't).
     * If this flag is set to true then separate connection(s) for sequencing
     * will be used in case getSequence().shouldUseSeparateConnection()
     * returns true.
     * @see Sequence
     */
    public boolean shouldUseSeparateConnection();

    /**
     * PUBLIC:
     * Set whether separate connection(s) for sequencing could be used
     * (by default it couldn't).
     * If this flag is set to true then separate connection(s) for sequencing
     * will be used in case getSequence().shouldUseSeparateConnection()
     * returns true.
     * @see Sequence
     */
    public void setShouldUseSeparateConnection(boolean shouldUseSeparateConnection);

    /**
     * PUBLIC:
     * Indicates whether sequencing actually uses separate connection(s).
     * Returns true if sequencing is connected and uses separate connection(s).
     * Returns false if sequencing is not connected (getSequencing()==null).
     * Note that if shouldUseSeparateConnection() returns false this method also returns false.
     * However if shouldUseSeparateConnection() returns true this method
     * returns false in the following two cases:
     * <br>sequencing is not connected;
     * <br>getSequence().shouldUseSeparateConnection() == false.
     * @see Sequence
     */
    boolean isConnectedUsingSeparateConnection();

    /**
     * ADVANCED:
     * Return a DatabaseLogin to be used by separate sequencing connection(s).
     * @see org.eclipse.persistence.sessions.DatabaseLogin
     */
    Login getLogin();

    /**
     * ADVANCED:
     * Returns a DatabaseLogin to be used by separate sequencing connection(s)
     * The set value is ignored if shouldUseSeparateConnection() returns false.
     * The DatabaseLogin *MUST*:
     * 1. specify *NON-JTS* connections (such as NON_JTS driver or read-only datasource);
     * 2. sequenceLogin.shouldUseExternalTransactionController()==false
     * In case this method is not called, but separate connection should be used,
     * sequencing will use a clone of login owned by the DatabaseSession,
     * or a clone of read login owned by ServerSession.
     * @see org.eclipse.persistence.sessions.DatabaseLogin
     */
    void setLogin(Login login);

    /**
     * ADVANCED:
     * Return the connection pool to use for sequencing.
     */
    ConnectionPool getConnectionPool();
    
    /**
     * ADVANCED:
     * Set the connection pool to use for sequencing.
     */
    void setConnectionPool(ConnectionPool pool);

    /**
     * PUBLIC:
     * Returns a minimum number of connections in sequencing connection pool.
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     * @see org.eclipse.persistence.sessions.server.ServerSession
     */
    int getMinPoolSize();

    /**
     * PUBLIC:
     * Sets a minimum number of connections in sequencing connection pool
     * The set value is ignored if shouldUseSeparateConnection() returns false.
     * The set value is ignored if SequencingControl has been obtained not from ServerSession.
     * By default is 2.
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     * @see org.eclipse.persistence.sessions.server.ServerSession
     */
    void setMinPoolSize(int size);

    /**
     * PUBLIC:
     * Returns a maximum number of connections in sequencing connection pool
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     * @see org.eclipse.persistence.sessions.server.ServerSession
     */
    int getMaxPoolSize();

    /**
     * PUBLIC:
     * Sets a maximum number of connections in sequencing connection pool
     * The set value is ignored if shouldUseSeparateConnection() returns false.
     * The set value is ignored if SequencingControl has been obtained not from ServerSession.
     * By default is 2.
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     * @see org.eclipse.persistence.sessions.server.ServerSession
     */
    void setMaxPoolSize(int size);
    
    /**
     * PUBLIC:
     * Sets a initial number of connections in sequencing connection pool
     * The set value is ignored if shouldUseSeparateConnection() returns false.
     * The set value is ignored if SequencingControl has been obtained not from ServerSession.
     * By default is 1.
     * @see org.eclipse.persistence.sessions.server.ConnectionPool
     * @see org.eclipse.persistence.sessions.server.ServerSession
     */
    void setInitialPoolSize(int size);

    /**
     * ADVANCED:
     * Removes all preallocated sequencing objects.
     * Ignored if getSequencingValueGenarationPolicy().shouldUsePreallocation() returns false.
     * This method is called internally after Sequencing object is destructed.
     * @see Sequence
     */
    void initializePreallocated();

    /**
     * ADVANCED:
     * Removes all preallocated sequencing objects for the given sequence name.
     * Ignored if getSequencingValueGenarationPolicy().shouldUsePreallocation() returns false.
     * @see Sequence
     */
    void initializePreallocated(String seqName);
}
