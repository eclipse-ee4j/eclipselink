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
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <b>Purpose</b>: This class is reponsible for creating the tables defined in the project.
 * A specific subclass of this class is created for each project.  The specific table information
 * is defined in the subclass.
 *
 * @since TopLink 2.0
 * @author Peter Krogh
 */
public class TableCreator {
    protected Vector tableDefinitions;
    protected String name;
    protected boolean ignoreDatabaseException; //if true, DDL generation will continue even if exceptions occur

    public TableCreator() {
        this(new Vector());
    }

    public TableCreator(Vector tableDefinitions) {
        super();
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Add the table.
     */
    public void addTableDefinition(TableDefinition tableDefinition) {
        tableDefinitions.addElement(tableDefinition);
    }
    
    /**
     * Add a set of tables.
     */
    public void addTableDefinitions(Collection tableDefs) {
        tableDefinitions.addAll(tableDefs);
    }


    /**
     * Create constraints.
     */
    public void createConstraints(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        createConstraints(session, new SchemaManager(session));
    }

    /**
     * Create constraints.
     */
    public void createConstraints(org.eclipse.persistence.sessions.DatabaseSession session, SchemaManager schemaManager) {
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            schemaManager.buildFieldTypes((TableDefinition)enumtr.nextElement());
        }

        // Unique constraints should be generated before foreign key constraints,
        // because foreign key constraints can reference unique constraints
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createUniqueConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
        
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createForeignConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     */
    public void createTables(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        createTables(session, new SchemaManager(session));
    }

    /**
     * This creates the tables on the database.
     * If the table already exists this will fail.
     */
    public void createTables(org.eclipse.persistence.sessions.DatabaseSession session, SchemaManager schemaManager) {
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            schemaManager.buildFieldTypes((TableDefinition)enumtr.nextElement());
        }

        String sequenceTableName = getSequenceTableName(session);
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            // Must not create sequence table as done in createSequences.
            TableDefinition table = (TableDefinition)enumtr.nextElement();
            if (!table.getName().equals(sequenceTableName)) {
                try {
                    schemaManager.createObject(table);
                    session.getSessionLog().log(SessionLog.FINEST, "default_tables_created", table.getFullName());
                } catch (DatabaseException ex) {
                    session.getSessionLog().log(SessionLog.FINEST, "default_tables_already_existed", table.getFullName());
                    if (!shouldIgnoreDatabaseException()) {
                        throw ex;
                    }
                }
            }
        }

        // Unique constraints should be generated before foreign key constraints,
        // because foreign key constraints can reference unique constraints
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createUniqueConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
        
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createForeignConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }

        schemaManager.createSequences();
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        dropConstraints(session, new SchemaManager(session));
    }

    /**
     * Drop the table constraints from the database.
     */
    public void dropConstraints(org.eclipse.persistence.sessions.DatabaseSession session, SchemaManager schemaManager) {
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            schemaManager.buildFieldTypes((TableDefinition)enumtr.nextElement());
        }

        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.dropConstraints((TableDefinition)enumtr.nextElement());
            } catch (org.eclipse.persistence.exceptions.DatabaseException dbE) {
                //ignore
            }
        }
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(org.eclipse.persistence.sessions.DatabaseSession session) {
        //CR2612669
        dropTables(session, new SchemaManager(session));
    }

    /**
     * Drop the tables from the database.
     */
    public void dropTables(org.eclipse.persistence.sessions.DatabaseSession session, SchemaManager schemaManager) {
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            schemaManager.buildFieldTypes((TableDefinition)enumtr.nextElement());
        }

        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.dropConstraints((TableDefinition)enumtr.nextElement());
            } catch (org.eclipse.persistence.exceptions.DatabaseException dbE) {
                //ignore
            }
        }

        String sequenceTableName = getSequenceTableName(session);
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            // Must not create sequence table as done in createSequences.
            TableDefinition table = (TableDefinition)enumtr.nextElement();
            if (!table.getName().equals(sequenceTableName)) {
                try {
                    schemaManager.dropObject(table);
                } catch (DatabaseException ex) {
                    if (!shouldIgnoreDatabaseException()) {
                        throw ex;
                    }
                }
            }
        }
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the tables.
     */
    public Vector getTableDefinitions() {
        return tableDefinitions;
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(org.eclipse.persistence.sessions.DatabaseSession session) {
        replaceTables(session, new SchemaManager(session));
    }

    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(org.eclipse.persistence.sessions.DatabaseSession session, SchemaManager schemaManager) {
        replaceTablesAndConstraints(schemaManager, session);

        schemaManager.createSequences();

    }
    
    /**
     * Recreate the tables on the database.
     * This will drop the tables if they exist and recreate them.
     */
    public void replaceTables(org.eclipse.persistence.sessions.DatabaseSession session, 
            SchemaManager schemaManager, boolean keepSequenceTable) {
        replaceTablesAndConstraints(schemaManager, session);

        schemaManager.createOrReplaceSequences(keepSequenceTable, false);
    }    
    

    private void replaceTablesAndConstraints(final SchemaManager schemaManager, 
            final org.eclipse.persistence.sessions.DatabaseSession session) {
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            schemaManager.buildFieldTypes((TableDefinition)enumtr.nextElement());
        }
        
        // CR 3870467, do not log stack
        boolean shouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();
        if (shouldLogExceptionStackTrace) {
            session.getSessionLog().setShouldLogExceptionStackTrace(false);
        }
        try {
            for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
                try {
                    schemaManager.dropConstraints((TableDefinition)enumtr.nextElement());
                } catch (org.eclipse.persistence.exceptions.DatabaseException dbE) {
                    //ignore
                }
            }
        } finally {
            if (shouldLogExceptionStackTrace) {
                session.getSessionLog().setShouldLogExceptionStackTrace(true);
            }
        }

        String sequenceTableName = getSequenceTableName(session);
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            // Must not create sequence table as done in createSequences.
            TableDefinition table = (TableDefinition)enumtr.nextElement();
            if (!table.getName().equals(sequenceTableName)) {
                try {
                    schemaManager.replaceObject(table);
                } catch (DatabaseException ex) {
                    if (!shouldIgnoreDatabaseException()) {
                        throw ex;
                    }
                }
            }
        }

        // Unique constraints should be generated before foreign key constraints,
        // because foreign key constraints can reference unique constraints
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createUniqueConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
        
        for (Enumeration enumtr = getTableDefinitions().elements(); enumtr.hasMoreElements();) {
            try {
                schemaManager.createForeignConstraints((TableDefinition)enumtr.nextElement());
            } catch (DatabaseException ex) {
                if (!shouldIgnoreDatabaseException()) {
                    throw ex;
                }
            }
        }
    }

    /**
     * Set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the tables.
     */
    public void setTableDefinitions(Vector tableDefinitions) {
        this.tableDefinitions = tableDefinitions;
    }

    /**
     * Return true if DatabaseException is to be ignored.
     */
    boolean shouldIgnoreDatabaseException() {
        return ignoreDatabaseException;
    }

    /**
     * Set flag whether DatabaseException should be ignored. 
     */
    void setIgnoreDatabaseException(boolean ignoreDatabaseException) {
        this.ignoreDatabaseException = ignoreDatabaseException;
    }

    protected String getSequenceTableName(org.eclipse.persistence.sessions.Session session) {
        String sequenceTableName = null;
        if (session.getProject().usesSequencing()) {
            org.eclipse.persistence.sequencing.Sequence sequence = session.getLogin().getDefaultSequence();
            if (sequence instanceof org.eclipse.persistence.sequencing.TableSequence) {
                sequenceTableName = ((org.eclipse.persistence.sequencing.TableSequence)sequence).getTableName();
            }
        }
        return sequenceTableName;
    }
}
