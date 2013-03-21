/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * A table per tenant multitenant policy. Tables can either be per schema
 * or augmented with a prefix or suffix per tenant.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class TablePerMultitenantPolicy implements MultitenantPolicy, Cloneable {
    protected ClassDescriptor descriptor;
    
    // Maps original tables with tenant per table ones. This map is used when 
    // building fields that referred to the original table.
    protected Map<DatabaseTable, DatabaseTable> tablePerTenantTables;
    protected TenantTableDiscriminatorType type;
    protected String contextProperty;
    protected String contextTenant;
    
    public TablePerMultitenantPolicy(ClassDescriptor desc) {
        descriptor = desc;
        type = TenantTableDiscriminatorType.SUFFIX;
        tablePerTenantTables = new HashMap(4);
        contextProperty = EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT;
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldsToRow(AbstractRecord row, AbstractSession session) {
        // No fields to add in table per tenant policy.
    }

    /**
     * INTERNAL:
     */    
    public void addToTableDefinition(TableDefinition tableDefinition) {
        // Nothing to do here. Called during DDL generation.
    }

    /**
     * INTERNAL:
     * Multitenant policies are cloned per inheritance subclass.
     */
    public MultitenantPolicy clone(ClassDescriptor descriptor) {
        TablePerMultitenantPolicy clonedPolicy = null;
        
        try {
            clonedPolicy = (TablePerMultitenantPolicy) super.clone();

            clonedPolicy.descriptor = descriptor;
            
            // Create a separate hashmap per clone. 
            clonedPolicy.tablePerTenantTables = new HashMap(4);
            for (DatabaseTable table : this.tablePerTenantTables.keySet()) {
                clonedPolicy.tablePerTenantTables.put(table, this.tablePerTenantTables.get(table));
            }
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
        
        return clonedPolicy;
    }
    
    /**
     * INTERNAL:
     * Return the context property for this table per tenant policy.
     */
    public String getContextProperty() {
        return contextProperty;
    }
    
    /**
     * INTERNAL:
     * Return the new database table associated with this tenant.
     */
    public DatabaseTable getTable(String tableName) {
        return tablePerTenantTables.get(new DatabaseTable(tableName));
    }
    
    /**
     * INTERNAL:
     * Return the new database table associated with this tenant.
     */
    public DatabaseTable getTable(DatabaseTable table) {
        return tablePerTenantTables.get(table);
    }

    /**
     * INTERNAL:
     * Return the tenant table name.
     */
    protected String getTableName(DatabaseTable table, String tenant) {
        if (isPrefixPerTable()) {
            return tenant + "_" + table.getName();
        } else {
            return table.getName() + "_" + tenant;
        }
    }
    
    /**
     * INTERNAL:
     * Return true if the tenant has been set for this policy.
     */
    public boolean hasContextTenant() {
        return this.contextTenant != null;
    }
    
    /**
     * INTERNAL:
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        // Add the context property to the session set.
        session.addMultitenantContextProperty(contextProperty);
    }
    
    /**
     * PUBLIC: 
     * Return true if this descriptor requires a prefix to the table per tenant.
     */
    public boolean isPrefixPerTable() {
        return type == TenantTableDiscriminatorType.PREFIX;
    }
    
    /**
     * PUBLIC: 
     * Return true if this descriptor requires a table schema per tenant.
     */
    public boolean isSchemaPerTable() {
        return type == TenantTableDiscriminatorType.SCHEMA;
    }

    /**
     * INTERNAL:
     */
    public boolean isSingleTableMultitenantPolicy() {
        return false;
    }
    
    /**
     * PUBLIC: 
     * Return true if this descriptor requires a suffix to the table per tenant.
     */
    public boolean isSuffixPerTable() {
        return (type == null || type == TenantTableDiscriminatorType.SUFFIX);
    }
    
    /**
     * INTERNAL:
     */
    public boolean isTablePerMultitenantPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public void postInitialize(AbstractSession session) {
        // Nothing to do here.
    }

    /**
     * INTERNAL:
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        // Nothing to do here.
    }
    
    /**
     * PUBLIC:
     * Set the tenant table discriminator type.
     */
    public void setTenantTableDiscriminatorType(TenantTableDiscriminatorType type) {
        this.type = type;
    }
    
    /**
     * PUBLIC:
     * Set the context property used to define the table per tenant. If it is
     * not set by the user, the policy defaults it to the multitenant property 
     * default of "eclipselink.tenant-id"
     */
    public void setContextProperty(String contextProperty) {
        this.contextProperty = contextProperty;
    }
    
    /**
     * INTERNAL:
     * This method is used to update the table per tenant descriptor with
     * a table per tenant prefix or suffix on its associated tables. This 
     * includes any relation tables from mappings. 
     * 
     * If the given session is a client session than we must clone the tables.
     * Outside of a client session, assume global usage and no cloning is 
     * needed.
     * 
     * This method should only be called at the start of a client session 
     * lifecycle and should only be called once.
     */
    protected void setTablePerTenant() {
        // Update the descriptor tables.
        Vector<DatabaseTable> tables = NonSynchronizedVector.newInstance(3);
        for (DatabaseTable table : descriptor.getTables()) {
            tables.add(updateTable(table));
        }
        descriptor.setTables(tables);
        
        // Multitple table foreign keys need to be updated as well
        Map<DatabaseTable, Set<DatabaseTable>> existingMultipleTables = descriptor.getMultipleTableForeignKeys();
        
        if (existingMultipleTables != null && ! existingMultipleTables.isEmpty()) {
            Map<DatabaseTable, Set<DatabaseTable>> updatedMultipleTables = new HashMap<DatabaseTable, Set<DatabaseTable>>();
            Set<DatabaseTable> secondaryTables = new HashSet<DatabaseTable>();

            for (DatabaseTable table : existingMultipleTables.keySet()) {
                for (DatabaseTable secondaryTable : existingMultipleTables.get(table)) {
                    DatabaseTable updatedSecondaryTable = getTable(secondaryTable);
                    
                    if (updatedSecondaryTable == null) {
                        secondaryTables.add(secondaryTable);
                    } else {
                        secondaryTables.add(updatedSecondaryTable);
                    }
                }
                
                DatabaseTable updatedTable = getTable(table);
                
                if (updatedTable == null) {
                    updatedMultipleTables.put(table, secondaryTables);
                } else {
                    updatedMultipleTables.put(updatedTable, secondaryTables);
                }
            }
            
            descriptor.setMultipleTableForeignKeys(updatedMultipleTables);
        }
            
        // Any mapping (owning) with a relation table will need to be updated.
        // Non-owning sides of a bidirectional mapping will be updated during
        // descriptor initialization.
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (mapping.isManyToManyMapping()) {
                // If the mapping is read only we are not the owner of the 
                // relationship meaning we need to look up the relation table 
                // name from the reference descriptor. This will be done later 
                // on in the initialization phase of the table mechanism (when 
                // a reference descriptor has been set and we can look up the
                // correct relation table)
                if (! mapping.isReadOnly()) {
                    ((ManyToManyMapping) mapping).setRelationTable(updateTable(((ManyToManyMapping) mapping).getRelationTable()));
                }
            } else if (mapping.isOneToOneMapping() && ((OneToOneMapping) mapping).hasRelationTable()) {
                ((OneToOneMapping) mapping).setRelationTable(updateTable(((OneToOneMapping) mapping).getRelationTable()));
            } else if (mapping.isDirectCollectionMapping()) {
                ((DirectCollectionMapping) mapping).setReferenceTable(updateTable(((DirectCollectionMapping) mapping).getReferenceTable()));
            }
        }
    }
    
    /**
     * INTERNAL:
     * This method is used to update the table per tenant descriptor with
     * a table per tenant schema. This includes any relation tables from 
     * mappings. This will be done through the setting of a table qualifier on
     * the tables.
     * 
     * This method should only be called at the start of a client session 
     * lifecycle and should only be called once. 
     */
    protected void setTableSchemaPerTenant() {
        descriptor.setTableQualifier(contextTenant);
        
        // Any mapping with a relation table will need to be updated.
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (mapping.isManyToManyMapping()) {
                ((ManyToManyMapping) mapping).getRelationTable().setTableQualifier(contextTenant);
            } else if (mapping.isOneToOneMapping() && ((OneToOneMapping) mapping).hasRelationTable()) {
                ((OneToOneMapping) mapping).getRelationTable().setTableQualifier(contextTenant);
            } else if (mapping.isDirectCollectionMapping()) {
                ((DirectCollectionMapping) mapping).getReferenceTable().setTableQualifier(contextTenant);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setContextTenant(String contextTenant) {
        this.contextTenant = contextTenant;
        
        if (isSchemaPerTable()) {
            setTableSchemaPerTenant();
        } else {
            setTablePerTenant();
        }
    }
    
    /**
     * INTERNAL:
     * This method is called during regular descriptor initialization. When
     * initializing at that level no cloning should be done on when setting
     * the context tenant.
     */
    public boolean shouldInitialize(AbstractSession session) {
        if (! hasContextTenant()) {
            // If we find our tenant property off the given session, update
            // our descriptors tables and return true for initialization.
            String tenant = (String) session.getProperty(contextProperty);
        
            if (tenant != null) {
                setContextTenant(tenant);
            }
        } 
        
        return hasContextTenant();
    }
    
    /**
     * INTERNAL:
     * This method will update the table by cloning it and setting a new name
     * on it. The table association will be stored here as well for ease of
     * future look up.
     */
    protected DatabaseTable updateTable(DatabaseTable table) {
        DatabaseTable tableClone = table.clone();
        tablePerTenantTables.put(table, tableClone);
        tableClone.setName(getTableName(tableClone, contextTenant));
        return tableClone;
    }
    
    /**
     * INTERNAL:
     * Return true if this policy accepts the given property.
     */
    public boolean usesContextProperty(String property) {
        return contextProperty.equals(property);
    }
}
