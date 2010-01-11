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
package org.eclipse.persistence.testing.tests.dbchangenotification;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.testing.tests.returning.ProjectAndDatabaseAdapter;
import org.eclipse.persistence.testing.framework.*;

public class DbChangeNotificationAdapter implements ProjectAndDatabaseAdapter {

    // doesn't create a queue, just uses the existing one

    public DbChangeNotificationAdapter(String queueName) {
        this.queueName = queueName;
    }

    // creates a queue

    public DbChangeNotificationAdapter(String queueName, String queueTableName, boolean useMultipleConsumers) {
        this(queueName);
        this.queueTableName = queueTableName;
        this.useMultipleConsumers = useMultipleConsumers;
    }

    public boolean isOriginalSetupRequired() {
        return false;
    }

    String queueName;
    String queueTableName;
    boolean useMultipleConsumers;
    Hashtable tableNamesToPkFields = new Hashtable();

    public void updateProject(Project project, Session session) {
        Iterator it = project.getDescriptors().values().iterator();
        while (it.hasNext()) {
            ClassDescriptor desc = (ClassDescriptor)it.next();
            Enumeration enumDescTableNames = desc.getTables().elements();
            while (enumDescTableNames.hasMoreElements()) {
                String tableName = ((DatabaseTable)enumDescTableNames.nextElement()).getName();
                if (!tableNamesToPkFields.containsKey(tableName)) {
                    while (desc.isChildDescriptor()) {
                        desc = project.getClassDescriptor(desc.getInheritancePolicy().getParentClass());
                    }
                    tableNamesToPkFields.put(tableName, desc.getPrimaryKeyFields());
                }
            }
        }
    }

    public void updateDatabase(Session session) {
        if (!session.getPlatform().isOracle()) {
            throw new TestWarningException("Currently supports Oracle platform only");
        }
        try {
            if (queueTableName != null) {
                dropAndCreateQueue(session);
            }
            createOrReplaceStoredProcedureNOTIFY_ENQUEUE(session);
            createOrReplaceStoredProcedureNOTIFY_SET_APPID(session);
            createOrReplaceStoredFunctionNOTIFY_GET_APPID(session);
            createOrReplaceStoredFunctionNOTIFY_IS_ENABLED(session);
            createOrReplaceStoredFunctionNOTIFY_MAKE_MSG(session);
            createOrReplaceTriggers(session);
            //		disableTriggers(session);
        } finally {
            clear();
        }
    }

    protected void dropAndCreateQueue(Session session) {
        //	execute(session, "BEGIN DBMS_AQADM.STOP_QUEUE (queue_name => '" + queueName + "'); END;", false);
        //	execute(session, "BEGIN DBMS_AQADM.DROP_QUEUE (queue_name => '" + queueName + "'); END;", false);
        //	execute(session, "BEGIN DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => '" + queueTableName + "'); END;", false);
        execute(session, "BEGIN DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => '" + queueTableName + "', force => TRUE); END;", false);

        execute(session, "BEGIN DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => '" + queueTableName + "', multiple_consumers => " + useMultipleConsumers + ", queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE'); END;", true);
        //	execute(session, "BEGIN DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => '" + queueTableName + "', multiple_consumers => TRUE, queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE', message_grouping => DBMS_AQADM.TRANSACTIONAL); END;", true);
        execute(session, "BEGIN DBMS_AQADM.CREATE_QUEUE (queue_name => '" + queueName + "', queue_table => '" + queueTableName + "'); END;", true);
        execute(session, "BEGIN DBMS_AQADM.START_QUEUE (queue_name => '" + queueName + "'); END;", true);
    }

    protected void createOrReplaceStoredProcedureNOTIFY_SET_APPID(Session session) {
        String[] str = { "CREATE OR REPLACE  PROCEDURE NOTIFY_SET_APPID(", "  app_id VARCHAR2 DEFAULT NULL", ")", "as", "BEGIN", "  DBMS_APPLICATION_INFO.SET_CLIENT_INFO(app_id);", "END;" };
        execute(session, str, true);
    }

    protected void createOrReplaceStoredFunctionNOTIFY_GET_APPID(Session session) {
        String[] str = { "CREATE OR REPLACE  FUNCTION NOTIFY_GET_APPID RETURN VARCHAR2", "as", "BEGIN", "  RETURN SYS_CONTEXT('USERENV', 'CLIENT_INFO');", "END;" };
        execute(session, str, true);
    }

    protected void createOrReplaceStoredFunctionNOTIFY_IS_ENABLED(Session session) {
        String[] str = { "CREATE OR REPLACE  FUNCTION NOTIFY_IS_ENABLED RETURN BOOLEAN", "as", "BEGIN", "  RETURN NOTIFY_GET_APPID IS NOT NULL;", "END;" };
        execute(session, str, true);
    }

    protected void createOrReplaceStoredFunctionNOTIFY_MAKE_MSG(Session session) {
        String[] str =
        //					"  msg.set_userid(NOTIFY_GET_APPID);",
        { "CREATE OR REPLACE  FUNCTION NOTIFY_MAKE_MSG (", "  table_name VARCHAR2", ")", "RETURN SYS.AQ$_JMS_TEXT_MESSAGE", "as", "  msg SYS.AQ$_JMS_TEXT_MESSAGE;", "BEGIN", "  msg := SYS.AQ$_JMS_TEXT_MESSAGE.CONSTRUCT();", "  msg.set_string_property('APP', NOTIFY_GET_APPID);", "  msg.set_string_property('TABLE', table_name);", "  RETURN msg;", "END;" };
        execute(session, str, true);
    }

    protected void createOrReplaceStoredProcedureNOTIFY_ENQUEUE(Session session) {
        String[] str =
        //					"    queue_name => '" + queueName + "',",
        { "CREATE OR REPLACE  PROCEDURE NOTIFY_ENQUEUE (", "  p_queue_name VARCHAR2,", "  msg SYS.AQ$_JMS_TEXT_MESSAGE", ")", "as", "  queue_options   DBMS_AQ.ENQUEUE_OPTIONS_T;", "  msg_properties  DBMS_AQ.MESSAGE_PROPERTIES_T;", "  msg_id          RAW(16);", "  no_recipients_for_message EXCEPTION;", "  PRAGMA EXCEPTION_INIT(no_recipients_for_message, -24033);", "BEGIN", "  DBMS_AQ.ENQUEUE(", "    queue_name => p_queue_name,", "    enqueue_options => queue_options,", "    message_properties => msg_properties,", "    payload => msg,", "    msgid => msg_id);", "  EXCEPTION", "    WHEN no_recipients_for_message THEN", "      NULL;-- should be ignored", "END;" };
        execute(session, str, true);
    }

    protected void createOrReplaceTriggers(Session session) {
        Enumeration enumTableNames = tableNamesToPkFields.keys();
        while (enumTableNames.hasMoreElements()) {
            String tableName = (String)enumTableNames.nextElement();
            List pkFields = (List)tableNamesToPkFields.get(tableName);
            createOrReplaceTrigger(session, tableName, pkFields);
        }
    }

    protected void disableTriggers(Session session) {
        Enumeration enumTableNames = tableNamesToPkFields.keys();
        while (enumTableNames.hasMoreElements()) {
            String tableName = (String)enumTableNames.nextElement();
            String triggerName = getTriggerNameFromTableName(tableName);
            String str = "ALTER TRIGGER " + triggerName + " DISABLE";
            execute(session, str, true);
        }
    }

    protected void createOrReplaceTrigger(Session session, String tableName, List pkFields) {
        String str = "";
        String triggerName = getTriggerNameFromTableName(tableName);
        String[] strBegin = { "CREATE OR REPLACE  TRIGGER " + triggerName + " AFTER", "UPDATE OR DELETE ON " + tableName + " FOR EACH ROW", "DECLARE", "  msg SYS.AQ$_JMS_TEXT_MESSAGE;", "BEGIN", "  IF NOT NOTIFY_IS_ENABLED THEN", "    RETURN;", "  END IF;", "  msg := NOTIFY_MAKE_MSG('" + tableName + "');" };
        for (int i = 0; i < strBegin.length; i++) {
            str = str + strBegin[i] + '\n';
        }

        Iterator itFields = pkFields.iterator();
        while (itFields.hasNext()) {
            str = str + getPkFieldString((DatabaseField)itFields.next()) + '\n';
        }

        str = str + "  NOTIFY_ENQUEUE('" + queueName + "', msg);\n";
        //	str = str + "  NOTIFY_ENQUEUE(msg);\n";
        str = str + "END;";

        execute(session, str, true);
    }

    protected String getPkFieldString(DatabaseField field) {
        String name = field.getName();
        Class type = field.getType();
        String str = "  msg.set_" + getJmsPropertyTypeName(type) + "_property('" + name + "', :old." + name + ");";
        return str;
    }

    protected String getJmsPropertyTypeName(Class type) {
        if (Helper.getShortClassName(type).equals("BigDecimal")) {
            return "double";
        } else if (Helper.getShortClassName(type).equals("String")) {
            return "String";
        } else {
            throw new TestProblemException("No JMS property type corresponds to the DatabaseField type " + type, null);
        }
    }

    protected String getTriggerNameFromTableName(String tableName) {
        return "NOTIFY_" + tableName;
    }

    protected void execute(Session session, String[] strArray, boolean shouldThrowException) {
        String str = "";
        for (int i = 0; i < strArray.length; i++) {
            str = str + strArray[i];
            if (i < strArray.length - 1) {
                str = str + '\n';
            }
        }
        execute(session, str, shouldThrowException);
    }

    protected void execute(Session session, String str, boolean shouldThrowException) {
        try {
            // For some reason DML must not usee binding on Oracle.
            DataModifyQuery query = new DataModifyQuery(str);
            query.setShouldBindAllParameters(false);
            session.executeQuery(query);
        } catch (Exception e) {
            if (shouldThrowException) {
                throw new TestErrorException("FAILED: " + str, e);
            }
        }
    }

    protected void clear() {
        tableNamesToPkFields.clear();
    }
}
