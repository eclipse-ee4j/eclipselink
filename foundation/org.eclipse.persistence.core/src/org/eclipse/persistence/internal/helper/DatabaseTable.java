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
 *     tware - added handling of database delimiters
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 *     12/07/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.IndexDefinition;

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
    
    /** JPA 2.1 Foreign key specification data */
    protected Map<String, ForeignKeyConstraint> foreignKeyConstraints;
    
    /**
     * Contains the user specified unique constraints. JPA 2.0 introduced 
     * the name element, therefore, if specified we will use that name
     * to create the constraint. Constraints with no name will be added to the
     * map under the null key and generated with a default name.
     * Therefore, when a name is given the list size should only ever be
     * 1. We will validate. The null key could have multiples however they will
     * have their names defaulted (as we did before).
     */
    protected Map<String, List<List<String>>> uniqueConstraints;

    /**
     * Store the set of indexes defined through meta-data for the table.
     */
    protected List<IndexDefinition> indexes;

    protected boolean useDelimiters = false;

    protected String creationSuffix;

    /** 
     * Initialize the newly allocated instance of this class.
     * By default their is no qualifier.
     */
    public DatabaseTable() {
        name = "";
        tableQualifier = "";
    }

    public DatabaseTable(String possiblyQualifiedName) {
        this(possiblyQualifiedName, null, null);
    }
    
    public DatabaseTable(String possiblyQualifiedName, String startDelimiter, String endDelimiter) {
        setPossiblyQualifiedName(possiblyQualifiedName, startDelimiter, endDelimiter);
    }

    public DatabaseTable(String tableName, String qualifier) {
        this(tableName, qualifier, false, null, null);
    }
    
    public DatabaseTable(String tableName, String qualifier, boolean useDelimiters, String startDelimiter, String endDelimiter) {
        setName(tableName, startDelimiter, endDelimiter);
        this.tableQualifier = qualifier;
        this.useDelimiters = useDelimiters;
    }

    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint) {
        if (foreignKeyConstraints == null) {
            foreignKeyConstraints = new HashMap<String, ForeignKeyConstraint>();
        }
        
        foreignKeyConstraints.put(foreignKeyConstraint.getName(), foreignKeyConstraint);
    }
    
    /**
     * Add an index definition to this table.
     */
    public void addIndex(IndexDefinition index) {
        getIndexes().add(index);
    }
    
    /**
     * Add the unique constraint for the columns names. Used for DDL generation.
     * For now we just add all the unique constraints as we would have before
     * when we didn't have a name.
     */
    public void addUniqueConstraints(String name, List<String> columnNames) {
        if (getUniqueConstraints().containsKey(name)) {
            getUniqueConstraints().get(name).add(columnNames);
        } else {
            List<List<String>> value = new ArrayList<List<String>>();
            value.add(columnNames);
            getUniqueConstraints().put(name, value);
        }
    }
    
    /** 
     * Return a shallow copy of the receiver.
     */
    public DatabaseTable clone() {
        try {
            return (DatabaseTable)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
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
     * returns the suffix applied to the CREATE table statement on this field for DDL generation.
     */
    public String getCreationSuffix() {
        return creationSuffix;
    }
    
    public ForeignKeyConstraint getForeignKeyConstraint(String name) {
        return foreignKeyConstraints.get(name);
    }
    
    public Map<String, ForeignKeyConstraint> getForeignKeyConstraints() {
        return foreignKeyConstraints;
    }

    /**
     * Return a list of index definitions.
     * Used for DDL generation.
     */
    public List<IndexDefinition> getIndexes() {
        if (this.indexes == null) {
            this.indexes = new ArrayList<IndexDefinition>();
        }
        return this.indexes;
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
    public String getNameDelimited(DatasourcePlatform platform) {
        if (useDelimiters){
            return platform.getStartDelimiter() + name + platform.getEndDelimiter();
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
    
    public String getQualifiedNameDelimited(DatasourcePlatform platform) {
        if (tableQualifier.equals("")) {
            if (useDelimiters){
                return platform.getStartDelimiter() + getName() + platform.getEndDelimiter();
            } else {
                return getName();
            }
        } else {
            if (useDelimiters){
                return platform.getStartDelimiter() + getTableQualifier() + platform.getEndDelimiter() + "." 
                  + platform.getStartDelimiter() + getName() + platform.getEndDelimiter();
            } else {
                return getTableQualifier() + "." + getName();
            }
        }
    }
    
    /**
     * Print the table's SQL from clause.
     */
    public void printSQL(ExpressionSQLPrinter printer) throws IOException {
        printer.getWriter().write(getQualifiedNameDelimited(printer.getPlatform()));
    }
    
    public String getTableQualifierDelimited(DatasourcePlatform platform) {
        if (useDelimiters && tableQualifier != null && !tableQualifier.equals("")){
            return platform.getStartDelimiter() + tableQualifier + platform.getEndDelimiter();
        }
        return tableQualifier;
    }
    
    public String getTableQualifier() {
        return tableQualifier;
    }

    public boolean hasUniqueConstraints() {
        return (this.uniqueConstraints != null) && (!this.uniqueConstraints.isEmpty());
    }
    
    public boolean hasForeignKeyConstraints() {
        return foreignKeyConstraints != null;
    }
    
    /** 
     * Return the hashcode of the name, because it is fairly unique.
     */
    public int hashCode() {
        return getName().hashCode();
    }
    
    public boolean hasIndexes() {
        return (this.indexes != null) && (!this.indexes.isEmpty());
    }
    
    /**
     * Return a list of the unique constraints for this table.
     * Used for DDL generation.
     */
    public Map<String, List<List<String>>> getUniqueConstraints() {
        if (this.uniqueConstraints == null) {
            this.uniqueConstraints = new HashMap<String, List<List<String>>>();
        }
        return this.uniqueConstraints;
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
    
    public void setCreationSuffix(String creationSuffix) {
        this.creationSuffix = creationSuffix;
    }

    /**
     * Set the table name. 
     * Used when aliasing table names.
     * @param name
     */
    public void setName(String name) {
        setName(name, null, null);
    }
    
    /**
     * Set the table name.
     * Used when aliasing table names.
     * 
     * If the name contains database delimiters, they will be stripped and a flag will be set to have them 
     * added when the DatabaseTable is written to SQL
     * 
     * @param name
     */
    public void setName(String name, String startDelimiter, String endDelimiter) {
        if (name != null && (startDelimiter != null) && (endDelimiter != null) && !startDelimiter.equals("")&& !endDelimiter.equals("") && name.startsWith(startDelimiter) && name.endsWith(endDelimiter)){
            this.name = name.substring(startDelimiter.length(), name.length() - endDelimiter.length());
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
        setPossiblyQualifiedName(possiblyQualifiedName, null, null);
    }
    
    public void setPossiblyQualifiedName(String possiblyQualifiedName, String startDelimiter, String endDelimiter) {
        resetQualifiedName();
        
        int index = possiblyQualifiedName.lastIndexOf('.');

        if (index == -1) {
            setName(possiblyQualifiedName, startDelimiter, endDelimiter);
            this.tableQualifier = "";
        } else {
            setName(possiblyQualifiedName.substring(index + 1, possiblyQualifiedName.length()), startDelimiter, endDelimiter);
            setTableQualifier(possiblyQualifiedName.substring(0, index), startDelimiter, endDelimiter);

            if((startDelimiter != null) && possiblyQualifiedName.startsWith(startDelimiter) && (endDelimiter != null) && possiblyQualifiedName.endsWith(endDelimiter)) {
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
        setTableQualifier(qualifier, null, null);
    }

    public void setTableQualifier(String qualifier, String startDelimiter, String endDelimiter) {
        if ((startDelimiter != null) && (endDelimiter != null) && !startDelimiter.equals("")&& !endDelimiter.equals("") && qualifier.startsWith(startDelimiter) && qualifier.endsWith(endDelimiter)){
            this.tableQualifier = qualifier.substring(startDelimiter.length(), qualifier.length() - endDelimiter.length());
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
