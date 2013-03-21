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
package org.eclipse.persistence.internal.sessions.remote;

import java.util.*;
import java.rmi.server.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
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
    
    /** Used to isolate queries to a unit of work in an early transaction. */
    protected boolean isInEarlyTransaction;

    /** This is a either a database session or a client session depending upon the setup. */
    protected AbstractSession session;
    
    /** Use the temporary unit of work to isolate queries after an early transaction. */
    protected UnitOfWorkImpl unitOfWork;

    /** The original remote value holders, before they were serialized; keyed by ID */
    protected Map<ObjID, ValueHolderInterface> remoteValueHolders;

    /** The original cursor (either CursoredStream or ScrollableCursor, before they were serialized; keyed by ID */
    protected Map<ObjID, Cursor> remoteCursors;

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
     * Begin an early unit of work transaction.
     */
    public Transporter beginEarlyTransaction() {
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
                this.isInTransaction = true;
                this.isInEarlyTransaction = true;
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
        Object key = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, getSession());
        objectDescriptor.setKey(key);
        objectDescriptor.setWriteLockValue(getExecutionSession().getIdentityMapAccessorInstance().getWriteLockValue(key, object.getClass(), descriptor));
        objectDescriptor.setObject(object);
        CacheKey cacheKey = getExecutionSession().getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(key, object.getClass(), descriptor);

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
        remoteTransporter.expand(this.session);
        // Must force concurrency mgrs active thread if in nested transaction.
        if (isInTransaction()) {
            getSession().getTransactionMutex().setActiveThread(Thread.currentThread());
        }

        RemoteUnitOfWork remoteUnitOfWork = (RemoteUnitOfWork)remoteTransporter.getObject();

        // The parent is changed to the session on the server side.
        remoteUnitOfWork.reinitializeForSession(getSession(), this);

        Transporter transporter = new Transporter();
        try {
            // Committing locally.
            if (remoteUnitOfWork.isFlush()) {
                remoteUnitOfWork.writeChanges();
            } else {
                remoteUnitOfWork.commitRootUnitOfWork();
                this.isInTransaction = false;
                this.isInEarlyTransaction = false;
            }
            transporter.setObject(remoteUnitOfWork);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        transporter.prepare(remoteUnitOfWork);
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
                this.isInTransaction = false;
                this.isInEarlyTransaction = false;
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
                transporter.setObject(Integer.valueOf(stream.size()));
            } else {
                transporter.setObject(Integer.valueOf(0));
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
            // Clear the unit of work, as the client unit of work may have been cleared.
            this.unitOfWork = null;
            AbstractSession executionSession = getExecutionSession();
            if (policy.isCursoredStreamPolicy()) {
                //wrap the cursored stream into a RemoteCursoredStream object and send the object to the client
                CursoredStream stream = (CursoredStream)executionSession.executeQuery(policy.getQuery());
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
                ScrollableCursor stream = (ScrollableCursor)executionSession.executeQuery(policy.getQuery());
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
            // Clear the unit of work, as the client unit of work may have been cleared.
            this.unitOfWork = null;
            AbstractSession executionSession = getExecutionSession();
            if (classTransporter.getObject() == null) {
                result = executionSession.executeQuery((String)nameTransporter.getObject(), (Vector)argumentsTransporter.getObject());
                query = executionSession.getQuery((String)nameTransporter.getObject());
            } else {
                result = executionSession.executeQuery((String)nameTransporter.getObject(), (Class)classTransporter.getObject(), (Vector)argumentsTransporter.getObject());
                query = executionSession.getDescriptor((Class)classTransporter.getObject()).getQueryManager().getQuery((String)nameTransporter.getObject());
            }
            transporter.setQuery(query);
            transporter.setObjectDescriptors(query.replaceValueHoldersIn(result, this));
            transporter.setObject(result);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        transporter.prepare(this.session);
        return transporter;
    }

    /**
     * A remote query after serialization is executed locally.
     */
    public Transporter executeQuery(Transporter remoteTransporter) {
        remoteTransporter.expand(this.session);
        DatabaseQuery query = (DatabaseQuery)remoteTransporter.getObject();
        Transporter transporter = new Transporter();

        try {
            AbstractRecord argumentRow = query.getTranslationRow();
            query.setTranslationRow(null);
            Object result;
            // Clear the unit of work, as the client unit of work may have been cleared.
            this.unitOfWork = null;
            AbstractSession executionSession = getExecutionSession();
            if (argumentRow == null) {
                result = executionSession.executeQuery(query);
            } else {
                result = executionSession.executeQuery(query, argumentRow);
            }
            transporter.setObjectDescriptors(query.replaceValueHoldersIn(result, this));
            transporter.setObject(result);
        } catch (RuntimeException exception) {
            transporter.setException(exception);
        }
        transporter.prepare(this.session);
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
     * Extract descriptor from the session
     */
    public Transporter getDescriptorForAlias(Transporter remoteTransporter) {
        String alias = (String)remoteTransporter.getObject();
        Transporter transporter = new Transporter();

        try {
            ClassDescriptor descriptor = getSession().getDescriptorForAlias(alias);
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
    protected Map<ObjID, Cursor> getRemoteCursors() {
        return remoteCursors;
    }

    /**
     * INTERNAL:
     * return the pre-serialized remote value holders
     */
    public Map<ObjID, ValueHolderInterface> getRemoteValueHolders() {
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
        remoteTransporter.expand(this.session);
        RemoteValueHolder clientValueHolder = (RemoteValueHolder)remoteTransporter.getObject();
        RemoteValueHolder serverValueHolder = (RemoteValueHolder)getRemoteValueHolders().get(clientValueHolder.getID());
        Transporter transporter = new Transporter();

        try {
            Object value = serverValueHolder.getMapping().getValueFromRemoteValueHolder(serverValueHolder);// force instantiation
            transporter.setObjectDescriptors(serverValueHolder.getMapping().replaceValueHoldersIn(value, this));

            // The following is a hack.  Many apologies.
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
        transporter.prepare(this.session);
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
        iterator.setSession(getExecutionSession());
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
                this.isInTransaction = false;
                this.isInEarlyTransaction = false;
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
                transporter.setObject(Boolean.valueOf(stream.absolute(rows)));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Integer.valueOf(stream.currentIndex()));
            } else {
                transporter.setObject(Integer.valueOf(0));
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
                transporter.setObject(Boolean.valueOf(stream.first()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.isAfterLast()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.isBeforeFirst()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.isFirst()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.isLast()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.last()));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Boolean.valueOf(stream.relative(rows)));
            } else {
                transporter.setObject(Boolean.FALSE);
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
                transporter.setObject(Integer.valueOf(cursor.size()));
            } else {
                transporter.setObject(Integer.valueOf(0));
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
    protected void setRemoteCursors(Map<ObjID, Cursor> remoteCursors) {
        this.remoteCursors = remoteCursors;
    }

    /**
     * set the pre-serialized remote value holders
     */
    protected void setRemoteValueHolders(Map<ObjID, ValueHolderInterface> remoteValueHolders) {
        this.remoteValueHolders = remoteValueHolders;
    }

    /**
     * Set the associated server side session.
     */
    protected void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * Return the correct session for the transaction context.
     * If in an active transaction, a unit of work must be used to avoid putting uncommitted data into the cache,
     * and to use the correct accessor for the queries.
     */
    protected AbstractSession getExecutionSession() {
        AbstractSession executionSession = this.session;
        if (this.isInEarlyTransaction) {
            if (this.unitOfWork == null) {
                this.unitOfWork = this.session.acquireUnitOfWork();
                this.unitOfWork.setWasTransactionBegunPrematurely(true);
            }
            executionSession = this.unitOfWork;
        }
        return executionSession;
    }
}
