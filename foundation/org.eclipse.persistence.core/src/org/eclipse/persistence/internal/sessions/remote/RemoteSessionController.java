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
package org.eclipse.persistence.internal.sessions.remote;

import java.util.*;
import java.rmi.server.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.sessions.coordination.CommandManager;

/**
 * RemoteSessionController sits between the remote session (on the client)
 * and whatever session is running on the server. Any interaction between these
 * two classes takes place through this object.
 */
public class RemoteSessionController {

    /** Used to resolve transaction treading for client-side nested transaction where server uses many worker threads. */
    protected boolean isInTransaction;

    /** This is a either a database session or a client session depending upon the setup. */
    protected AbstractSession session;

    /** The original remote value holders, before they were serialized; keyed by ID */
    protected Hashtable remoteValueHolders;

    /** The original cursor (either CursoredStream or ScrollableCursor, before they were serialized; keyed by ID */
    protected Hashtable remoteCursors;

    /** This is the Synchronization policy used to synchronize remote caches */
    protected CommandManager commandManager;

    public RemoteSessionController(AbstractSession session) {
        super();
        this.commandManager = session.getCommandManager();
        this.initialize(session);
    }

    /**
     * INTERNAL:
     * This method is intended to be used by by sessions that wish to execute a command on a
     * remote session
     * @param remoteCommand RemoteCommand The command to be executed on the remote session
     */
    public Transporter processCommand(Transporter remoteCommand) {
        Transporter transporter = new Transporter();
        try {
            ((RemoteCommand)remoteCommand.getObject()).execute(getSession(), this);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Begin a transaction on the database.
     */
    public Transporter beginTransaction() {
        // Must force concurrency mgrs active thread if in nested transaction.
        if (isInTransaction()) {
            getSession().getTransactionMutex().setActiveThread(Thread.currentThread());
        }

        Transporter transporter = new Transporter();

        try {
            getSession().beginTransaction();
            // Must force concurrency mgrs active thread if in nested transaction.
            if (isInTransaction()) {
                getSession().getTransactionMutex().setActiveThread(Thread.currentThread());
            }
            if (getSession().isInTransaction()) {
                setIsInTransaction(true);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * build and return an object descriptor to be sent to the client
     */
    protected ObjectDescriptor buildObjectDescriptor(Object object) {
        ObjectDescriptor objectDescriptor = new ObjectDescriptor();
        ClassDescriptor descriptor = getSession().getDescriptor(object);
        Vector key = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, getSession());
        objectDescriptor.setKey(key);
        objectDescriptor.setWriteLockValue(getSession().getIdentityMapAccessorInstance().getWriteLockValue(key, object.getClass(), descriptor));
        objectDescriptor.setObject(object);
        CacheKey cacheKey = getSession().getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(key, object.getClass(), descriptor);

        // Check for null because when there is NoIdentityMap, CacheKey will be null
        if (cacheKey != null) {
            objectDescriptor.setReadTime(cacheKey.getReadTime());
        }
        return objectDescriptor;
    }

    /**
     * Used for closing scrolable cursor across RMI.
     */
    public Transporter closeScrollableCursor(ObjID id) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(id);
            if (stream != null) {
                stream.close();
            }
            getRemoteCursors().remove(id);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Remote unit of work after serialization is commited locally.
     */
    public Transporter commitRootUnitOfWork(Transporter remoteTransporter) {
        // Must force concurrency mgrs active thread if in nested transaction.
        if (isInTransaction()) {
            getSession().getTransactionMutex().setActiveThread(Thread.currentThread());
        }

        RemoteUnitOfWork remoteUnitOfWork = (RemoteUnitOfWork)remoteTransporter.getObject();

        // The parent is changed to the session on the server side.
        remoteUnitOfWork.reinitializeForSession(getSession(), this);

        Transporter transporter = new Transporter();
        ;
        try {
            // Committing locally.
            remoteUnitOfWork.commitRootUnitOfWork();
            transporter.setObject(remoteUnitOfWork);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Commit a transaction on the database.
     */
    public Transporter commitTransaction() {
        Transporter transporter = new Transporter();

        try {
            getSession().commitTransaction();
            if (!getSession().isInTransaction()) {
                setIsInTransaction(false);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Used for closing cursored streams across RMI.
     */
    public Transporter cursoredStreamClose(Transporter remoteCursoredStreamOid) {
        Transporter transporter = new Transporter();
        try {
            CursoredStream stream = (CursoredStream)getRemoteCursors().get(remoteCursoredStreamOid.getObject());
            if (stream != null) {
                stream.close();
            }
            getRemoteCursors().remove(remoteCursoredStreamOid);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Retrieve next page of objects from the remote cursored stream once, avoid page size round trips from the server to the client
     */
    public Transporter cursoredStreamNextpage(Transporter remoteCursoredId, int pageSize) {
        Transporter transporter = new Transporter();
        Vector nextPagesObjects = null;
        Map objectDescriptors = new IdentityHashMap(pageSize);
        try {
            CursoredStream stream = (CursoredStream)getRemoteCursors().get(remoteCursoredId.getObject());
            if (stream != null) {
                //retrieve page size of objects from the cursored stream
                nextPagesObjects = stream.nextElements(pageSize);
                for (Enumeration enumtr = nextPagesObjects.elements(); enumtr.hasMoreElements();) {
                    Object objectNext = enumtr.nextElement();
                    if (objectNext == null) {
                        break;
                    } else {
                        if (stream.getQuery().isReadAllQuery() && (!stream.getQuery().isReportQuery())) {
                            replaceValueHoldersIn(objectNext, objectDescriptors);
                        }
                    }
                }
            } else {
                nextPagesObjects = new Vector(0);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        transporter.setObjectDescriptors(objectDescriptors);
        transporter.setObject(nextPagesObjects);
        return transporter;
    }

    /**
     * Return the wrapped cursored stream from the server
     */
    public Transporter cursoredStreamSize(Transporter remoteCursoredStreamOid) {
        Transporter transporter = new Transporter();
        try {
            //unwrap the remote cursored stream
            CursoredStream stream = (CursoredStream)getRemoteCursors().get(remoteCursoredStreamOid.getObject());
            if (stream != null) {
                transporter.setObject(new Integer(stream.size()));
            } else {
                transporter.setObject(new Integer(0));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Returns a remote cursor stub in a transporter
     */
    public Transporter cursorSelectObjects(Transporter remoteTransporter) {
        Transporter transporter = new Transporter();

        try {
            CursorPolicy policy = (CursorPolicy)remoteTransporter.getObject();
            if (isInTransaction()) {
                policy.getQuery().setAccessor(getSession().getAccessor());
            }
            if (policy.isCursoredStreamPolicy()) {
                //wrap the cursored stream into a RemoteCursoredStream object and send the object to the client
                CursoredStream stream = (CursoredStream)getSession().executeQuery(policy.getQuery());
                RemoteCursoredStream remoteStream = new RemoteCursoredStream(stream);

                // For bug 3452418 prevents reading the initial objects twice.
                remoteStream.setObjectCollection(stream.nextElements(stream.getObjectCollection().size()));
                getRemoteCursors().put(remoteStream.getID(), stream);
                if (stream.getQuery().isReadAllQuery() && (!stream.getQuery().isReportQuery())) {
                    transporter.setObjectDescriptors(replaceValueHoldersInAll(remoteStream.getObjectCollection(), new CollectionContainerPolicy(ClassConstants.Vector_class)));
                }
                transporter.setObject(remoteStream);
            } else if (policy.isScrollableCursorPolicy()) {
                //wrap the scrollable cursor into a RemoteScrollableCursor object and send the object to the client
                ScrollableCursor stream = (ScrollableCursor)getSession().executeQuery(policy.getQuery());
                RemoteScrollableCursor remoteStream = new RemoteScrollableCursor(stream);
                getRemoteCursors().put(remoteStream.getID(), stream);
                transporter.setObject(remoteStream);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * A named query after serialization is executed locally.
     */
    public Transporter executeNamedQuery(Transporter nameTransporter, Transporter classTransporter, Transporter argumentsTransporter) {
        Transporter transporter = new Transporter();

        try {
            Object result;
            DatabaseQuery query;
            if (classTransporter.getObject() == null) {
                result = getSession().executeQuery((String)nameTransporter.getObject(), (Vector)argumentsTransporter.getObject());
                query = getSession().getQuery((String)nameTransporter.getObject());
            } else {
                result = getSession().executeQuery((String)nameTransporter.getObject(), (Class)classTransporter.getObject(), (Vector)argumentsTransporter.getObject());
                query = getSession().getDescriptor((Class)classTransporter.getObject()).getQueryManager().getQuery((String)nameTransporter.getObject());
            }
            transporter.setQuery(query);
            transporter.setObjectDescriptors(query.replaceValueHoldersIn(result, this));
            transporter.setObject(result);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * A remote query after serialization is executed locally.
     */
    public Transporter executeQuery(Transporter remoteTransporter) {
        DatabaseQuery query = (DatabaseQuery)remoteTransporter.getObject();
        Transporter transporter = new Transporter();

        try {
        	AbstractRecord argumentRow = query.getTranslationRow();
            query.setTranslationRow(null);
            Object result;
            if (isInTransaction()) {
                query.setAccessor(getSession().getAccessor());
            }
            if (argumentRow == null) {
                result = getSession().executeQuery(query);
            } else {
                result = getSession().executeQuery(query, argumentRow);
            }
            transporter.setObjectDescriptors(query.replaceValueHoldersIn(result, this));
            transporter.setObject(result);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * INTERNAL:
     * This method returns the command manager policy for this remote connection
     */
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * return the read-only classes
     **/
    public Transporter getDefaultReadOnlyClasses() {
        Transporter transporter = new Transporter();

        try {
            transporter.setObject(getSession().getDefaultReadOnlyClasses());
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Extract descriptor from the session
     */
    public Transporter getDescriptor(Transporter remoteTransporter) {
        Class theClass = (Class)remoteTransporter.getObject();
        Transporter transporter = new Transporter();

        try {
            ClassDescriptor descriptor = getSession().getDescriptor(theClass);
            transporter.setObject(descriptor);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Get the associated session login.
     */
    public Transporter getLogin() {
        Transporter transporter = new Transporter();

        try {
            transporter.setObject(getSession().getDatasourceLogin());
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * return the pre-remoted cursors
     */
    protected Hashtable getRemoteCursors() {
        return remoteCursors;
    }

    /**
     *INTERNAL:
     * return the pre-serialized remote value holders
     */
    public Hashtable getRemoteValueHolders() {
        return remoteValueHolders;
    }

    /**
     * INTERNAL:
     * Get the value returned by remote function call
     */
    public Transporter getSequenceNumberNamed(Transporter remoteFunctionCall) {
        Transporter transporter = new Transporter();
        Object returnValue = null;

        try {
            RemoteFunctionCall functionCall = (RemoteFunctionCall)remoteFunctionCall.getObject();
            returnValue = functionCall.execute(session, this);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        transporter.setObject(returnValue);

        return transporter;
    }

    /**
     * Get the associated server side session.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     *  Initialize this RemoteSessionController with a session.  Clear the cached
     *  valueholders and cursors.
     */
    protected void initialize(AbstractSession session) {
        setSession(session);
        setRemoteValueHolders(new Hashtable(3));
        setRemoteCursors(new Hashtable(3));
        setIsInTransaction(false);
    }

    /**
     *  Initalize all the server side identity maps.
     */
    public Transporter initializeIdentityMapsOnServerSession() {
        Transporter transporter = new Transporter();

        try {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * The corresponding original value holder is instantiated.
     */
    public Transporter instantiateRemoteValueHolderOnServer(Transporter remoteTransporter) {
        RemoteValueHolder clientValueHolder = (RemoteValueHolder)remoteTransporter.getObject();
        RemoteValueHolder serverValueHolder = (RemoteValueHolder)getRemoteValueHolders().get(clientValueHolder.getID());
        Transporter transporter = new Transporter();

        try {
            Object value = serverValueHolder.getMapping().getValueFromRemoteValueHolder(serverValueHolder);// force instantiation
            transporter.setObjectDescriptors(serverValueHolder.getMapping().replaceValueHoldersIn(value, this));

            // The following is a hack.  Many appologies.
            // The hashCode is called for the case where an Indirect Collection is being returned.  The hashCode function
            // causes the delegate for the collection to be build.  The delegate needs to be returned to the client
            // in order to properly deal with the list.
            // We call hashCode after setObjectDescriptors() because the setObjectDescriptors() method will get rid of the
            // delegate.
            // We will have to revisit this because it causes the IndirectList to be instantiated even if it was not in the
            // past - TGW
            if (value != null) {
                value.hashCode();
            }
            transporter.setObject(value);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    protected boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Traverse the specified object, replacing the standard
     * value holders with remote value holders.
     * And build up a collection of object descriptors.
     */
    public Map replaceValueHoldersIn(Object object) {
        // 2612538 - the default size of Map (32) is appropriate
        Map result = new IdentityHashMap();
        replaceValueHoldersIn(object, result);
        return result;
    }

    /**
     * Traverse the specified object, replacing the standard
     * value holders with remote value holders.
     * Add the resulting object descriptors to the
     * "collecting parm".
     */
    public void replaceValueHoldersIn(Object object, Map objectDescriptors) {
        if (object == null) {
            return;
        }
        DescriptorIterator iterator = new ReplaceValueHoldersIterator(this);
        iterator.setResult(objectDescriptors);
        iterator.setSession(getSession());
        iterator.setShouldIterateOnIndirectionObjects(true);// process the value holders themselves
        iterator.setShouldIterateOverIndirectionObjects(false);// but don't go beyond them
        iterator.startIterationOn(object);
    }

    /**
     * Traverse the specified objects, replacing the standard
     * value holders with remote value holders.
     * And build up a collection of object descriptors.
     */
    public Map replaceValueHoldersInAll(Object container, ContainerPolicy policy) {
        // 2612538 - the default size of Map (32) is appropriate
        Map result = new IdentityHashMap();
        for (Object iter = policy.iteratorFor(container); policy.hasNext(iter);) {
            replaceValueHoldersIn(policy.next(iter, getSession()), result);
        }
        return result;
    }

    /**
     * Rollback a transaction on the database.
     */
    public Transporter rollbackTransaction() {
        Transporter transporter = new Transporter();

        try {
            getSession().rollbackTransaction();
            if (!getSession().isInTransaction()) {
                setIsInTransaction(false);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    /**
     * Save the pre-serialized version of the remote value holder
     * so that it can be used when the serialized version (on the client)
     * is instantiated and needs to come back to the server to get
     * its "value".
     */
    public void saveRemoteValueHolder(RemoteValueHolder remoteValueHolder) {
        getRemoteValueHolders().put(remoteValueHolder.getID(), remoteValueHolder);
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public Transporter scrollableCursorAbsolute(Transporter remoteScrollableCursorOid, int rows) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.absolute(rows)));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    public Transporter scrollableCursorAfterLast(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                stream.afterLast();
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    public Transporter scrollableCursorBeforeFirst(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                stream.beforeFirst();
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Used for closing cursored streams across RMI.
     */
    public Transporter scrollableCursorClose(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                stream.close();
            }
            getRemoteCursors().remove(remoteScrollableCursorOid.getObject());
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Retrieves the current row index number
     */
    public Transporter scrollableCursorCurrentIndex(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Integer(stream.currentIndex()));
            } else {
                transporter.setObject(new Integer(0));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Moves the cursor to the first row in the result set
     */
    public Transporter scrollableCursorFirst(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.first()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    public Transporter scrollableCursorIsAfterLast(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.isAfterLast()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    public Transporter scrollableCursorIsBeforeFirst(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.isBeforeFirst()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    public Transporter scrollableCursorIsFirst(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.isFirst()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    public Transporter scrollableCursorIsLast(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.isLast()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Moves the cursor to the last row in the result set
     */
    public Transporter scrollableCursorLast(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.last()));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Retrieve next object from the scrollable cursor
     */
    public Transporter scrollableCursorNextObject(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                Object objectNext = null;

                // For bug 2797683 read beyond end of stream exception should not be thrown here: called first by RemoteScrollableCursor.hasNext().
                if (stream.hasNext()) {
                    objectNext = stream.next();
                }
                if (objectNext == null) {
                    // 2612538 - the default size of Map (32) is appropriate
                    transporter.setObjectDescriptors(new IdentityHashMap());
                } else {
                    if (stream.getQuery().isReadAllQuery() && (!stream.getQuery().isReportQuery())) {
                        transporter.setObjectDescriptors(replaceValueHoldersIn(objectNext));
                    }
                }
                transporter.setObject(objectNext);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Retrieve previous object from the scrollable cursor
     */
    public Transporter scrollableCursorPreviousObject(Transporter remoteScrollableCursorOid) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                Object objectPrevious = null;

                // For bug 2797683 read beyond end of stream exception should not be thrown here: called first by RemoteScrollableCursor.hasNext().
                if (stream.hasPrevious()) {
                    objectPrevious = stream.previous();
                }
                if (objectPrevious == null) {
                    // 2612538 - the default size of Map (32) is appropriate
                    transporter.setObjectDescriptors(new IdentityHashMap());
                } else {
                    if (stream.getQuery().isReadAllQuery() && (!stream.getQuery().isReportQuery())) {
                        transporter.setObjectDescriptors(replaceValueHoldersIn(objectPrevious));
                    }
                }
                transporter.setObject(objectPrevious);
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public Transporter scrollableCursorRelative(Transporter remoteScrollableCursorOid, int rows) {
        Transporter transporter = new Transporter();
        try {
            ScrollableCursor stream = (ScrollableCursor)getRemoteCursors().get(remoteScrollableCursorOid.getObject());
            if (stream != null) {
                transporter.setObject(new Boolean(stream.relative(rows)));
            } else {
                transporter.setObject(new Boolean(false));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        return transporter;
    }

    /**
     * Return the scrollable cursor size from the server
     */
    public Transporter scrollableCursorSize(Transporter remoteCursorOid) {
        Transporter transporter = new Transporter();
        try {
            //unwrap the remote cursored stream
            ScrollableCursor cursor = (ScrollableCursor)getRemoteCursors().get(remoteCursorOid.getObject());
            if (cursor != null) {
                transporter.setObject(new Integer(cursor.size()));
            } else {
                transporter.setObject(new Integer(0));
            }
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }

        return transporter;
    }

    protected void setIsInTransaction(boolean isInTransaction) {
        this.isInTransaction = isInTransaction;
    }

    /**
     * set the pre-remoted cursors
     */
    protected void setRemoteCursors(Hashtable remoteCursors) {
        this.remoteCursors = remoteCursors;
    }

    /**
     * set the pre-serialized remote value holders
     */
    protected void setRemoteValueHolders(Hashtable remoteValueHolders) {
        this.remoteValueHolders = remoteValueHolders;
    }

    /**
     * Set the associated server side session.
     */
    protected void setSession(AbstractSession session) {
        this.session = session;
    }
}
