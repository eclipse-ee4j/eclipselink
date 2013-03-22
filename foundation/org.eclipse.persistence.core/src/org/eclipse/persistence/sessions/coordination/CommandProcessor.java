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
package org.eclipse.persistence.sessions.coordination;


/**
 * <p>
 * <b>Purpose</b>: Defines a pluggable interface for EclipseLink sessions and EclipseLink
 * applications to be able to be on the receiving end of EclipseLink command objects.
 * <p>
 * <b>Description</b>: This interface represents the entity that both initiates
 * (on the sending end) and processes (on the receiving end) remote commands.
 * The implementation classes of this interface should be set on the CommandManager
 * so that it can be invoked to process command messages as they get received
 * from other remote command managers in the TopLink cluster. When the implementing
 * class wants to send a remote command to other CommandProcessors then it invokes
 * the CommandManager to do so.
 * When running this remote command service in a EclipseLink application then the
 * the Session class should be used as the implementation class for this interface.
 * When running in a non-TopLink application then the application should provide
 * the implementation class.
 *
 * @see Command
 * @see CommandManager
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public interface CommandProcessor {
    // Log levels to be used by the CommandProcessor
    public static final int LOG_DEBUG = 4;
    public static final int LOG_INFO = 3;
    public static final int LOG_WARNING = 2;
    public static final int LOG_ERROR = 1;

    /**
     * PUBLIC:
     * Invoked by the CommandManager after it has received a Command object and
     * has converted it to the application command format.
     *
     * @param command Application formatted command to be processed
     */
    public void processCommand(Object command);

    /**
     * PUBLIC:
     * Return the CommandManager that will invoke this CommandProcessor to process
     * a command, and can be used to send remote commands out to other
     * CommandProcessors in the cluster.
     *
     * @return The remote command manager responsible for this CommandProcessor
     */
    public CommandManager getCommandManager();

    /**
     * PUBLIC:
     * Set the CommandManager that will invoke this CommandProcessor to process
     * a command, and can be used to send remote commands out to other
     * CommandProcessors in the cluster.
     *
     * @param commandManager The remote command manager responsible for this CommandProcessor
     */
    public void setCommandManager(CommandManager commandManager);

    /**
     * PUBLIC:
     * Determine whether messages at the specified log level should be logged.
     * This will be invoked by the CommandManager as a short-circuiting mechanism
     * to calling logMessage().
     *
     * @param logLevel A log constant that is one of LOG_ERROR, LOG_WARNING, LOG_INFO, LOG_DEBUG
     * @return True if a message at the specified level should be logged and false if it should not
     */
    public boolean shouldLogMessages(int logLevel);

    /**
     * PUBLIC:
     * Log a message to the application log output stream. If the specified log level
     * is lower than the level configured in the CommandProcessor then the message
     * should not be logged. The implementation class must map its logging system to
     * the four supported levels: Error, Warning, Info and Debug.
     *
     * @param logLevel A log constant that is one of LOG_ERROR, LOG_WARNING, LOG_INFO, LOG_DEBUG
     * @param message The message String that is to be logged
     */
    public void logMessage(int logLevel, String message);

    /**
     * PUBLIC:
     * Allow the implementation class to handle an exception thrown in in the remote
     * command service. The implementation may choose to simply rethrow the
     * exception if it does not want to handle it.
     *
     * NOTE: Handlers that simply ignore exceptions may cause unexpected behavior
     * that can be detrimental to the application and difficult to debug.
     *
     * @param exception The exception being thrown
     * @return An object that is not currently used
     */
    public Object handleException(RuntimeException exception);
}
