/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     tware - added handling of database delimiters
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.io.*;
import java.util.List;
import java.util.Vector;
import org.eclipse.persistence.internal.databaseaccess.*;

/**
 * INTERNAL:
 * <p> <b>Purpose</b>:
 * Define a fully qualified table name.<p>
 * <b>Responsibilities</b>:    <ul>
 *    <li> Allow specification of a qualifier to the table, i.e. creator or database.
 *    </ul>
 *@see DatabaseField
 */
public class DatabaseTable implements Cloneable, Serializable {
    protected String name;
    protected String tableQualifier;
    protected String qualifiedName;
    protected Vector<List<String>> uniqueConstraints; //Element is columnNames
    protected boolean useDelimiters = false;
    
    /** 
     * Initialize the newly allocated instance of this class.
     * By default their is no qualifier.
     */
    public DatabaseTable() {
        name = "";
        tableQualifier = "";
        uniqueConstraints = new Vector<List<String>>();
    }

    public DatabaseTable(String possiblyQualifiedName) {
        setPossiblyQualifiedName(possiblyQualifiedName);
        uniqueConstraints = new Vector<List<String>>();
    }

    public DatabaseTable(String tableName, String qualifier) {
        setName(tableName);
        this.tableQualifier = qualifier;
        uniqueConstraints = new Vector<List<String>>();
    }
    
    public DatabaseTable(String tableName, String qualifier, boolean useDelimiters) {
        this(tableName, qualifier);
        this.useDelimiters = useDelimiters;
    }

    /**
     * Add the unique constraint for the columns names. Used for DDL generation.
     */
    public void addUniqueConstraints(List<String> columnNames) {
        uniqueConstraints.add(columnNames);
    }
    
    /** 
     * Return a shallow copy of the receiver.
     * @return Object An Object must be returned or the signature of this method
     * will conflict with the signature in Object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
        }

        return null;
    }

    /** 
     * Two tables are equal if their names and tables are equal,
     * or their names are equal and one does not have a qualifier assigned.
     * This allows an unqualified table to equal the same fully qualified one.
     */
    public boolean equals(Object object) {
        if (object instanceof DatabaseTable) {
            return equals((DatabaseTable)object);
        }
        return false;
    }

    /**
     * Two tables are equal if their names and tables are equal,
     * or their names are equal and one does not have a qualifier assigned.
     * This allows an unqualified table to equal the same fully qualified one.
     */
    public boolean equals(DatabaseTable table) {
        if (this == table) {
            return true;
        }
        if (DatabasePlatform.shouldIgnoreCaseOnFieldComparisons()) {
            if (getName().equalsIgnoreCase(table.getName())) {
                if ((getTableQualifier().length() == 0) || (table.getTableQualifier().length() == 0) || (getTableQualifier().equalsIgnoreCase(table.getTableQualifier()))) {
                    return true;
                }
            }
        } else {
            if (getName().equals(table.getName())) {
                if ((getTableQualifier().length() == 0) || (table.getTableQualifier().length() == 0) || (getTableQualifier().equals(table.getTableQualifier()))) {
                    return true;
                }
            }
        }

        return false;
    }

    /** 
     * Get method for table name.
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Get method for table name.
     */
    public String getNameDelimited() {
        if (useDelimiters){
            return Helper.getStartDatabaseDelimiter() + name + Helper.getEndDatabaseDelimiter();
        }
        return name;
    }

    public String getQualifiedName() {
        if (qualifiedName == null) {
            if (tableQualifier.equals("")) {
                qualifiedName = getName();
            } else {
                qualifiedName = getTableQualifier() + "." + getName();
            }
        }

        return qualifiedName;
    }
    
    public String getQualifiedNameDelimited() {
        if (qualifiedName == null) {
            if (tableQualifier.equals("")) {
                if (useDelimiters){
                    qualifiedName = Helper.getStartDatabaseDelimiter() + getName() + Helper.getEndDatabaseDelimiter();
                } else {
                    qualifiedName = getName();
                }
            } else {
                if (useDelimiters){
                    qualifiedName = Helper.getStartDatabaseDelimiter() + getTableQualifier() + Helper.getEndDatabaseDelimiter() + "." 
                      + Helper.getStartDatabaseDelimiter() + getName() + Helper.getEndDatabaseDelimiter();
                } else {
                    qualifiedName = getTableQualifier() + "." + getName();
                }
            }
        }
        return qualifiedName;
    }

    public String getTableQualifier() {
        if (useDelimiters && tableQualifier != null && !tableQualifier.equals("")){
            return Helper.getStartDatabaseDelimiter() + tableQualifier + Helper.getEndDatabaseDelimiter();
        }
        return tableQualifier;
    }
    
    /**
     * Return a vector of the unique constraints for this table.
     * Used for DDL generation.
     */
    public Vector<List<String>> getUniqueConstraints() {
        return uniqueConstraints;
    }

    /** 
     * Return the hashcode of the name, because it is fairly unique.
     */
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Determine whether the receiver has any identification information.
     * Return true if the name or qualifier of the receiver are nonempty.
     */
    public boolean hasName() {
        if ((getName().length() == 0) && (getTableQualifier().length() == 0)) {
            return false;
        }

        return true;
    }

    /**
     * INTERNAL:
     * Is this decorated / has an AS OF (some past time) clause.
     * <b>Example:</b>
     * SELECT ... FROM EMPLOYEE AS OF TIMESTAMP (exp) t0 ...
     */
    public boolean isDecorated() {
        return false;
    }

    protected void resetQualifiedName() {
        this.qualifiedName = null;
    }

    /**
     * This method will set the table name regardless if the name has
     * a qualifier. Used when aliasing table names.
     * 
     * If the name contains database delimiters, they will be stripped and a flag will be set to have them 
     * added when the DatabaseTable is written to SQL
     * 
     * @param name
     */
    public void setName(String name) {
        if (name != null && name.startsWith(Helper.getStartDatabaseDelimiter()) && name.endsWith(Helper.getEndDatabaseDelimiter())){
            this.name = name.substring(1, name.length() - 1);
            useDelimiters = true;
        } else {
            this.name = name ;
        }
        resetQualifiedName();
    }
    
    /**
     * Used to map the project xml. Any time a string name is read from the
     * project xml, we must check if it is fully qualified and split the
     * actual name from the qualifier.
     * 
     * @param possiblyQualifiedName 
     */
    public void setPossiblyQualifiedName(String possiblyQualifiedName) {
        resetQualifiedName();
        
        int index = possiblyQualifiedName.lastIndexOf('.');

        if (index == -1) {
            setName(possiblyQualifiedName);
            this.tableQualifier = "";
        } else {
            setName(possiblyQualifiedName.substring(index + 1, possiblyQualifiedName.length()));
            setTableQualifier(possiblyQualifiedName.substring(0, index));

            if(possiblyQualifiedName.startsWith(Helper.getStartDatabaseDelimiter()) && possiblyQualifiedName.endsWith(Helper.getEndDatabaseDelimiter())) {
                // It's 'Qualifier.Name' - it should be treated as a single string.
                // Would that be 'Qualifier'.'Name' both setName and setTableQualifier methods would have set useDelimeters to true.
                if(!this.useDelimiters) {
                    setName(possiblyQualifiedName);
                    this.tableQualifier = "";
                }
            }
        }
    }

    public void setTableQualifier(String qualifier) {
        if (qualifier.startsWith(Helper.getStartDatabaseDelimiter()) && qualifier.endsWith(Helper.getEndDatabaseDelimiter())){
            this.tableQualifier = qualifier.substring(1, qualifier.length() - 1);
            useDelimiters = true;
        } else {
            this.tableQualifier = qualifier;
        }
        resetQualifiedName();
    }

    public String toString() {
        return "DatabaseTable(" + getQualifiedName() + ")";
    }
    
    public void setUseDelimiters(boolean useDelimiters) {
        this.useDelimiters = useDelimiters;
    }
    
    public boolean shouldUseDelimiters() {
        return useDelimiters;
    }
}
