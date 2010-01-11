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
package org.eclipse.persistence.testing.tests.remote.rmi;

import java.rmi.*;

import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.sessions.remote.rmi.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.remote.TransporterGenerator;

/**
 * RemoteSessionController sits between the remote session and the session. Any interaction between these
 * two classes takes place through this object. As the object extends unicast remote object it listens to 
 * only single remote session during runtime.
 */
 
public class RMIRemoteSessionControllerDispatcherForTestingExceptions extends RMIRemoteSessionControllerDispatcher {
    protected TransporterGenerator generator;

public RMIRemoteSessionControllerDispatcherForTestingExceptions(Session session) throws RemoteException 
{
	// This call to the super is required in RMI.
    this(session, (TransporterGenerator)session.getProperty("TransporterGenerator"));
}
public RMIRemoteSessionControllerDispatcherForTestingExceptions(Session session, TransporterGenerator generator) throws RemoteException 
{
	// This call to the super is required in RMI.
	super(session);
	this.controller = null;
    this.generator = generator;
}

protected Transporter handleByMode() {
    return generator.generate();
}

/**
 * INTERNAL:
 * This method is intended to be used by by sessions that wish to execute a command on a 
 * remote session
 * @param remoteCommand RemoteCommand The command to be executed on the remote session
 */

public Transporter processCommand(Transporter remoteTransporter)
{
    return handleByMode();
}
/**
 * Begin a transaction on the database.
 */
 
public Transporter beginTransaction()
{
    return handleByMode();
}
/**
 * Remote unit of work after serialization is commited locally.
 */
 
public Transporter commitRootUnitOfWork(Transporter remoteUnitOfWork)
{
    return handleByMode();
}
/**
 * Commit a transaction on the database.
 */
 
public Transporter commitTransaction()
{
    return handleByMode();
}
/**
 * TESTING:
 * Return if the two object match completely.
 * This checks the objects attributes and their private parts.
 */
 
public Transporter compareObjects(Transporter firstObject, Transporter secondObject)
{
    return handleByMode();
}
/**
 * TESTING:
 * Return true if the object do not match.
 * This checks the objects attributes and their private parts.
 */

public Transporter compareObjectsDontMatch(Transporter firstObject, Transporter secondObject)
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Return if their is an object for the primary key.
 */

public Transporter containsObjectInIdentityMap(Transporter domainObject)
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Return if their is an object for the primary key.
 */

public Transporter containsObjectInIdentityMap(Transporter primaryKey, Transporter theClass)
{
    return handleByMode();
}
/**
 * Used for closing cursored streams across RMI.
 */
 
public Transporter cursoredStreamClose(Transporter remoteCursoredStreamID) {
    return handleByMode();
}
/** 
 * Retrieve next page size of objects from the remote cursored stream 
 */
 
public Transporter cursoredStreamNextPage(Transporter remoteCursoredStream, int pageSize) {
    return handleByMode();
}
/** 
 * Return the cursored stream size
 */
 
public Transporter cursoredStreamSize(Transporter remoteCursoredStreamOid) {
    return handleByMode();
}
/**
 * Returns a remote cursor stub in a transporter
 */

public Transporter cursorSelectObjects(Transporter remoteTransporter)
{
    return handleByMode();
}
/**
 * A remote query after serialization is executed locally. 
 */
 
public Transporter executeNamedQuery(Transporter nameTransporter, Transporter classTransporter, Transporter argumentsTransporter)
{
    return handleByMode();
}
/**
 * A remote query after serialization is executed locally. 
 */
 
public Transporter executeQuery(Transporter query)
{
    return handleByMode();
}

/**
 * Get the default read-only classes
 **/

public Transporter getDefaultReadOnlyClasses()
{
    return handleByMode();
}

/** 
 * Extract descriptor from the session
 */
 
public Transporter getDescriptor(Transporter theClass)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Return the ExceptionHandler.Exception handler can catch errors that occur on queries or during database access.
 */

public Transporter getExceptionHandler()
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Return the object from the identity with primary and class of the given object.
 */

public Transporter getFromIdentityMap(Transporter domainObject)
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Return the object from the identity with the primary and class.
 */

public Transporter getFromIdentityMap(Transporter primaryKey, Transporter theClass)
{
    return handleByMode();
}
/**
 * Get the associated session login.
 */
 
public Transporter getLogin()
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Return the profiler.
 * The profiler is a tool that can be used to determine performance bottlenecks.
 * The profiler can be queries to print summaries and configure for logging purposes.
 */

public Transporter getProfiler()
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Return the writer to which an accessor writes logged messages and SQL.
 * If not set, this reference defaults to a writer on System.out.
 * To enable logging, logMessages must be turned on.
 *
 * @see #logMessages()
 */

public Transporter getRemoteLog()
{
    return handleByMode();
}
/**
 * INTERNAL:
 * Get the value returned by remote function call
 */

public Transporter getSequenceNumberNamed(Transporter remoteFunctionCall) {
    return handleByMode();
}
/**
 * PUBLIC:
 * Return the session log to which an accessor logs messages and SQL.
 * If not set, this will default to a session log on a writer on System.out.
 * To enable logging, logMessages must be turned on.
 *
 * @see #logMessages()
 */

public Transporter getSessionLog()
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Reset the identity map for only the instances of the class.
 * For inheritance the user must make sure that they only use the root class.
 * Caution must be used in doing this to ensure that the objects within the identity map
 * are not referenced from other objects of other classes or from the application.
 */

public Transporter initializeIdentityMap(Transporter theClass)
{
    return handleByMode();
}
public Transporter initializeIdentityMapsOnServerSession()
{
    return handleByMode();
}
/**
 * The corresponding original value holder is instantiated.
 */
 
public Transporter instantiateRemoteValueHolderOnServer(Transporter remoteValueHolder)
{
    return handleByMode();
}
/**
 * Return if this session is connected.
 */

public Transporter isConnected()
{
    return handleByMode();
}
/**
 * Log the log entry.
 */

public Transporter log(Transporter entry)
{
    return handleByMode();
}
/**
 * INTERNAL:
 * This method is used to merge the changes into the session.
 */

public Transporter mergeChangesIntoRemoteSession(Transporter remoteTransporter){
    return handleByMode();
}
/**
 * PUBLIC:
 * Used to print all the objects in the identity map of the passed in class.
 * The output of this method will go the the Session's log.
 */

public Transporter printIdentityMap(Transporter theClass)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Used to print all the objects in every identity map in this session.
 * The output of this method will go to the Session's log.
 */

public Transporter printIdentityMaps()
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Remove the object from the object cache.
 * Caution should be used when calling to avoid violating object identity.
 * The application should only call this is it knows that no references to the object exist.
 */
 
public Transporter removeFromIdentityMap(Transporter domainObject)
{
    return handleByMode();
}
/**
 * ADVANCED:
 * Remove the object from the object cache.
 * Caution should be used when calling to avoid violating object identity.
 * The application should only call this is it knows that no references to the object exist.
 */
 
public Transporter removeFromIdentityMap(Transporter key, Transporter theClass)
{
    return handleByMode();
}
/**
 * Rollback a transaction on the database.
 */
 
public Transporter rollbackTransaction()
{
    return handleByMode();
}
/**
 * Moves the cursor to the given row number in the result set
 */
 
public Transporter scrollableCursorAbsolute(Transporter remoteScrollableCursorOid, int rows) {
    return handleByMode();
}
/**
 * Moves the cursor to the end of the result set, just after the last row.
 */
 
public Transporter scrollableCursorAfterLast(Transporter remoteScrollableCursorOid){
    return handleByMode();
}
/**
 * Moves the cursor to the front of the result set, just before the first row
 */
 
public Transporter scrollableCursorBeforeFirst(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Used for closing scrollable cursor across RMI.
 */
 
public Transporter scrollableCursorClose(Transporter remoteScrollableCursorOid)  {
    return handleByMode();
}
/**
 * Retrieves the current row index number
 */
 
public Transporter scrollableCursorCurrentIndex(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Moves the cursor to the first row in the result set
 */
 
public Transporter scrollableCursorFirst(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Indicates whether the cursor is after the last row in the result set.
 */
 
public Transporter scrollableCursorIsAfterLast(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Indicates whether the cursor is before the first row in the result set.
 */
 
public Transporter scrollableCursorIsBeforeFirst(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Indicates whether the cursor is on the first row of the result set.
 */
  
public Transporter scrollableCursorIsFirst(Transporter remoteScrollableCursor) {
    return handleByMode();
}
/**
 * Indicates whether the cursor is on the last row of the result set.
 */
 
public Transporter scrollableCursorIsLast(Transporter remoteScrollableCursor)  {
    return handleByMode();
}
/**
 * Moves the cursor to the last row in the result set
 */
 
public Transporter scrollableCursorLast(Transporter remoteScrollableCursor)  {
    return handleByMode();
}
/** 
 * Retrieve next object from the scrollable cursor 
 */
 
public Transporter scrollableCursorNextObject(Transporter scrollableCursorOid) {
    return handleByMode();
}
/** 
 * Retrieve previous object from the scrollable cursor 
 */
 
public Transporter scrollableCursorPreviousObject(Transporter scrollableCursorOid) {
    return handleByMode();
}

/**
 * Moves the cursor to the given row number in the result set
 */
 
public Transporter scrollableCursorRelative(Transporter remoteScrollableCursor, int rows)  {
    return handleByMode();
}
/** 
 * Return the cursor size
 */
 
public Transporter scrollableCursorSize(Transporter remoteCursorOid) {
    return handleByMode();
}
/**
 * PUBLIC:
 * Set the exceptionHandler.
 * Exception handler can catch errors that occur on queries or during database access.
 */

public Transporter setExceptionHandler(Transporter exceptionHandler)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Set the writer to which an accessor writes logged messages and SQL.
 * If not set, this reference defaults to a writer on System.out. 
 * To enable logging logMessages() is used.
 *
 * @see #logMessages()
 */

public Transporter setLog(Transporter log)
{
    return handleByMode();
}
/**
 * INTERNAL:
 * Set the login.
 */

public Transporter setLogin(Transporter login)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Set the profiler for the session.
 * This allows for performance operations to be profiled.
 */

public Transporter setProfiler(Transporter profiler)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Set the session log to which an accessor logs messages and SQL.
 * If not set, this will default to a session log on a writer on System.out.
 * To enable logging, logMessages must be turned on.
 *
 * @see #logMessages()
 */

public Transporter setSessionLog(Transporter sessionLog)
{
    return handleByMode();
}
/**
 * PUBLIC:
 * Set messages logging.
 * Message logging will dump all SQL executed through TopLink to the session's
 * log. By default this is System.out, but can be set to any Writer.
 *
 * @see #setLog(Writer)
 */

public Transporter setShouldLogMessages(Transporter shouldLogMessages)
{
    return handleByMode();
}
/**
 * Return if all messages such as executed SQL commands should be logged.
 */

public Transporter shouldLogMessages()
{
    return handleByMode();
}
/**
 * ADVANCED:
 * This can be used to help debugging an object identity problem.
 * An object identity problem is when an object in the cache references an object not in the cache.
 * This method will validate that all cached objects are in a correct state.
 */

public Transporter validateCache()
{
    return handleByMode();
}
/**
 * INTERNAL:
 * TESTING:
 * This is used by testing code to ensure that a deletion was successful.
 */

public Transporter verifyDelete(Transporter domainObject)
{
    return handleByMode();
}
}
