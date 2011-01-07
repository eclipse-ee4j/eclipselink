/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.history;

import java.util.*;

import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.history.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.broker.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * <b>Purpose:</b>One stop shopping for all your history needs.
 * <p>
 * @author Stephen McRitchie
 * @since Oracle 10g AS
 */
public class HistoryFacade {

    private static Map timeOffsetsMap = new IdentityHashMap();

    protected HistoryFacade() {
    }

    protected static void buildHistoricalTableDefinition(HistoryPolicy policy, 
                                                         String name, 
                                                         TableDefinition def, 
                                                         TableCreator creator) {

        if (def == null) {
            return;
        }

        TableDefinition histDef = (TableDefinition)def.clone();
        histDef.setName(name);
        FieldDefinition fieldDef = new FieldDefinition();
        fieldDef.setName(policy.getStartFieldName());
        fieldDef.setType(ClassConstants.TIMESTAMP);
        histDef.addField(fieldDef);
        fieldDef = new FieldDefinition();
        fieldDef.setName(policy.getEndFieldName());
        fieldDef.setType(ClassConstants.TIMESTAMP);
        histDef.addField(fieldDef);
        histDef.setForeignKeys(new Vector());

        for (FieldDefinition fieldDef2 : histDef.getFields()) {
            // For now foreign key constraints are not supported, because shallow inserts are not...
            fieldDef2.setForeignKeyFieldName(null);
            if (fieldDef2.getName().equals("ROW_START") && 
                (!histDef.getPrimaryKeyFieldNames().isEmpty())) {
                fieldDef2.setIsPrimaryKey(true);
            }
            if (fieldDef2.getName().equals("VERSION") && 
                (!histDef.getPrimaryKeyFieldNames().isEmpty())) {
                fieldDef2.setIsPrimaryKey(true);
            }
        }
        creator.addTableDefinition(histDef);
    }

    public static long currentDatabaseTimeMillis(org.eclipse.persistence.sessions.Session session) {
        return currentDatabaseTimeMillis(session, null);
    }

    /**
     * PUBLIC:
     */
    public static long currentDatabaseTimeMillis(org.eclipse.persistence.sessions.Session session, 
                                   Class domainClass) {
        Session rootSession = session;
        while (rootSession.isUnitOfWork() || rootSession.isClientSession() || 
               rootSession instanceof HistoricalSession || 
               rootSession.isSessionBroker()) {
            if (rootSession.isUnitOfWork()) {
                rootSession = ((UnitOfWork)rootSession).getParent();
            } else if (rootSession.isClientSession()) {
                rootSession = ((ClientSession)rootSession).getParent();
            } else if (rootSession instanceof HistoricalSession) {
                rootSession = ((HistoricalSession)rootSession).getParent();
            } else {
                SessionBroker broker = (SessionBroker)rootSession;
                rootSession = broker.getSessionForClass(domainClass);
                if (rootSession == broker) {
                    break;
                }
            }
        }
        if (timeOffsetsMap.containsKey(rootSession)) {
            Long offset = (Long)timeOffsetsMap.get(rootSession);
            return System.currentTimeMillis() + offset.longValue();
        } else {
            DatabaseQuery query = 
                rootSession.getPlatform().getTimestampQuery();
            long startTime = System.currentTimeMillis();
            java.sql.Timestamp databaseTime = 
                (java.sql.Timestamp)rootSession.executeQuery(query);
            long endTime = System.currentTimeMillis();
            long jvmTime = (endTime - startTime) / 2 + startTime;
            long offset = databaseTime.getTime() - jvmTime;
            timeOffsetsMap.put(rootSession, new Long(offset));
            return jvmTime + offset;
        }
    }

    /**
     * PUBLIC:
     * Generates a mirroring historical schema given a
     * conventional schema and a session with HistoryPolicies set on
     * the descriptors.
     */
    public static void generateHistoricalTableDefinitions(TableCreator creator, 
                                            org.eclipse.persistence.sessions.Session session) {

        // First add all table definitions to a hashtable.
        Hashtable tableDefinitions = 
            new Hashtable(creator.getTableDefinitions().size());
        for (Enumeration enumtr = creator.getTableDefinitions().elements(); 
             enumtr.hasMoreElements(); ) {
            DatabaseObjectDefinition def = 
                (DatabaseObjectDefinition)enumtr.nextElement();
            tableDefinitions.put(def.getFullName(), def);
        }
        for (Iterator iterator = session.getDescriptors().values().iterator(); 
             iterator.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            HistoryPolicy policy = descriptor.getHistoryPolicy();
            if (policy != null) {
                Vector names = policy.getHistoryTableNames();
                for (int i = 0; i < descriptor.getTableNames().size(); i++) {
                    String name = 
                        (String)descriptor.getTableNames().elementAt(i);
                    String histName = (String)names.elementAt(i);
                    TableDefinition def = 
                        (TableDefinition)tableDefinitions.get(name);
                    buildHistoricalTableDefinition(policy, histName, def, 
                                                   creator);
                }
            }
            for (Enumeration mappings = descriptor.getMappings().elements(); 
                 mappings.hasMoreElements(); ) {
                DatabaseMapping mapping = 
                    (DatabaseMapping)mappings.nextElement();
                if (mapping.isManyToManyMapping()) {
                    ManyToManyMapping m2mMapping = (ManyToManyMapping)mapping;
                    policy = m2mMapping.getHistoryPolicy();
                    if (policy != null) {
                        String name = m2mMapping.getRelationTableName();
                        String histName = 
                            (String)policy.getHistoryTableNames().elementAt(0);
                        TableDefinition def = 
                            (TableDefinition)tableDefinitions.get(name);
                        buildHistoricalTableDefinition(policy, histName, def, 
                                                       creator);
                    }
                } else if (mapping.isDirectCollectionMapping()) {
                    DirectCollectionMapping dcMapping = 
                        (DirectCollectionMapping)mapping;
                    policy = dcMapping.getHistoryPolicy();
                    if (policy != null) {
                        String name = dcMapping.getReferenceTableName();
                        String histName = 
                            (String)policy.getHistoryTableNames().elementAt(0);
                        TableDefinition def = 
                            (TableDefinition)tableDefinitions.get(name);
                        buildHistoricalTableDefinition(policy, histName, def, 
                                                       creator);
                    }
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Generates HistoryPolicies for a given object model.  The policies
     * are of the recommended form, with START/END, and the history table name
     * being the current table name with a _HIST suffix.  Hence Employee would
     * become Employee_Hist.
     */
    public static void generateHistoryPolicies(Iterator descriptors, DatasourcePlatform platform) {
        HistoryPolicy basePolicy = new HistoryPolicy();
        basePolicy.addStartFieldName("ROW_START");
        basePolicy.addEndFieldName("ROW_END");

        HistoryPolicy policy = null;

        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            policy = (HistoryPolicy)basePolicy.clone();
            List<DatabaseTable> tables = descriptor.getTables();
            int size = tables.size();
            if (size == 0) {
                continue;
            }
            for (int i = 0; i < size; i++) {
                DatabaseTable table = tables.get(i);
                String name = table.getQualifiedNameDelimited(platform);
                String historicalName;
                if(table.shouldUseDelimiters()) {
                    historicalName = name.substring(0, name.length() - 1) + "_HIST" + Helper.getDefaultEndDatabaseDelimiter();
                } else { 
                    historicalName = name + "_HIST";
                }
                policy.addHistoryTableName(name, historicalName);
            }
            descriptor.setHistoryPolicy(policy);

            for (Enumeration mappings = descriptor.getMappings().elements(); 
                 mappings.hasMoreElements(); ) {
                DatabaseMapping mapping = 
                    (DatabaseMapping)mappings.nextElement();
                if (mapping instanceof ManyToManyMapping) {
                    ManyToManyMapping m2mMapping = (ManyToManyMapping)mapping;
                    policy = (HistoryPolicy)basePolicy.clone();
                    policy.addHistoryTableName(m2mMapping.getRelationTableName() + 
                                               "_HIST");
                    m2mMapping.setHistoryPolicy(policy);
                } else if (mapping instanceof DirectCollectionMapping) {
                    DirectCollectionMapping dcMapping = 
                        (DirectCollectionMapping)mapping;
                    policy = (HistoryPolicy)basePolicy.clone();
                    policy.addHistoryTableName(dcMapping.getReferenceTableName() + 
                                               "_HIST");
                    dcMapping.setHistoryPolicy(policy);
                }
            }
        }
    }

    public static void generateHistoryPolicies(org.eclipse.persistence.sessions.Project project) {
        generateHistoryPolicies(project.getDescriptors().values().iterator(), project.getDatasourceLogin().getPlatform());
    }

    public static void generateHistoryPolicies(org.eclipse.persistence.sessions.Session session) {
        generateHistoryPolicies(session.getDescriptors().values().iterator(), session.getPlatform());
    }
}
