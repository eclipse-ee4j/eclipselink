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
package org.eclipse.persistence.exceptions;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *    <p><b>Purpose</b>: IntegrityChecker is used for catching all the descriptor exceptions,
 *    and checking database tables. It gives the user options if he/she wants to
 *    catch descriptor exceptions, check database, and check InstantiationPolicy or not.
 */
public class IntegrityChecker implements Serializable {

    /** To add all the Descriptor exceptions */
    protected Vector caughtExceptions = null;

    /** To load the tables from database    */
    protected Vector tables = null;

    /** To know that should we catch all the descriptors exceptions or not */
    protected boolean shouldCatchExceptions;

    /** To know that should we check database tables or not */
    protected boolean shouldCheckDatabase;

    /** To know that should we check InstantiationPolicy or not */
    protected boolean shouldCheckInstantiationPolicy;

    /**
     * PUBLIC:
     * IntegrityChecker is used for catching all the Descriptor Exceptions,
     * and check database tables. IntegrityChecker gives the option to the user that does he wants to
     * catch all the descriptor exceptions,check database, and check InstantiationPolicy or not.
     */
    public IntegrityChecker() {
        super();
        this.shouldCatchExceptions = true;
        this.shouldCheckDatabase = false;
        this.shouldCheckInstantiationPolicy = true;
    }

    /**
     * PUBLIC:
     * This method is used for catching all the Descriptor Exceptions
     */
    public void catchExceptions() {
        setShouldCatchExceptions(true);
    }

    /**
     * PUBLIC:
     * This method is used to check the database tables.
     */
    public void checkDatabase() {
        setShouldCheckDatabase(true);
    }

    /**
     * PUBLIC:
     * This method is used to check the InstantiationPolicy.
     */
    public void checkInstantiationPolicy() {
        setShouldCheckInstantiationPolicy(true);
    }

    /**
     * INTERNAL:
     * This method checks that tables are present in the database.
     */
    public boolean checkTable(DatabaseTable table, AbstractSession session) {
        if (getTables().size() == 0) {
            // load the tables from the session
            initializeTables(session);
        }
        boolean tableExists =  getTables().contains(table.getName());
        //Some DBs (e.g. some versions of MySQL) convert all the table names to lower case.
        if (!tableExists && session.getPlatform().isMySQL()) {
            return getTables().contains(table.getName().toLowerCase());
        }
        return tableExists;
    }

    /**
     * PUBLIC:
     * This method is used for don't catching all the Descriptor Exceptions
     */
    public void dontCatchExceptions() {
        setShouldCatchExceptions(false);
    }

    /**
     * PUBLIC:
     * This method is used for don't checking the database tables and fields.
     */
    public void dontCheckDatabase() {
        setShouldCheckDatabase(false);
    }

    /**
     * PUBLIC:
     * This method is used for don't checking the InstantiationPolicy.
     */
    public void dontCheckInstantiationPolicy() {
        setShouldCheckInstantiationPolicy(false);
    }

    /**
     * PUBLIC:
     * This method returns the vector which adds all the Descriptors Exceptions.
     */
    public Vector getCaughtExceptions() {
        if (caughtExceptions == null) {
            caughtExceptions = new Vector();
        }
        return caughtExceptions;
    }

    /**
     * INTERNAL:
     * This method returns a vector which holds all the tables of database
     */
    public Vector getTables() {
        if (tables == null) {
            tables = new Vector();
        }
        return tables;
    }

    /**
     * INTERNAL:
     * This method handles all the Descriptor Exceptions.
     * This method will throw the exception or add the exceptions into a vector depending on the value of shouldCatchExceptions.
     */
    public void handleError(RuntimeException runtimeException) {
        if (!shouldCatchExceptions()) {
            throw runtimeException;
        }
        getCaughtExceptions().addElement(runtimeException);
    }

    /**
     * INTERNAL:
     * Return if any errors occurred.
     */
    public boolean hasErrors() {
        if ((caughtExceptions != null) && (caughtExceptions.size() > 0)) {
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return if any runtime errors occurred.
     */
    public boolean hasRuntimeExceptions() {
        if (hasErrors()) {
            for (Enumeration exceptionsEnum = getCaughtExceptions().elements();
                     exceptionsEnum.hasMoreElements();) {
                if (exceptionsEnum.nextElement() instanceof RuntimeException) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * This method is used to get all the database tables and add them into a vector.
     */
    public void initializeTables(AbstractSession session) {
        List result = session.getAccessor().getTableInfo(null, null, null, null, session);
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            AbstractRecord row = (AbstractRecord)iterator.next();
            if (session.getPlatform().shouldForceFieldNamesToUpperCase()) {
                this.tables.add(((String)row.get("TABLE_NAME")).toUpperCase());
            } else {
                this.tables.add(row.get("TABLE_NAME"));                
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void setCaughtExceptions(Vector exceptions) {
        this.caughtExceptions = exceptions;
    }

    /**
     * PUBLIC:
     * This method assigns the value to the variable (shouldCatchExceptions)
     * that defines whether we should catch all Descriptor Exceptions or not.
     */
    public void setShouldCatchExceptions(boolean answer) {
        shouldCatchExceptions = answer;
    }

    /**
     * PUBLIC:
     * This method assigns the value to the variable (shouldCheckDatabase)
     * that defines whether we should check the database or not.
     */
    public void setShouldCheckDatabase(boolean answer) {
        shouldCheckDatabase = answer;
    }

    /**
     * PUBLIC:
     * This method assigns the value to the variable (shouldCheckInstantiationPolicy)
     * that defines whether we should check InstantiationPolicy or not.
     */
    public void setShouldCheckInstantiationPolicy(boolean answer) {
        shouldCheckInstantiationPolicy = answer;
    }

    /**
     * PUBLIC:
     * This method signifies whether all Descriptor Exceptions should be thrown individually or queued.
     */
    public boolean shouldCatchExceptions() {
        return shouldCatchExceptions;
    }

    /**
     * PUBLIC:
     * This method signifies whether database tables and fields should be checked or not.
     */
    public boolean shouldCheckDatabase() {
        return shouldCheckDatabase;
    }

    /**
     * PUBLIC:
     * This method tells us whether we should check InstantiationPolicy or not.
     */
    public boolean shouldCheckInstantiationPolicy() {
        return shouldCheckInstantiationPolicy;
    }
}
