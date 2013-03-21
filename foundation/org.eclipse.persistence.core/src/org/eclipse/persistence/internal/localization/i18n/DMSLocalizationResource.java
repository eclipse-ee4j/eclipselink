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
 *     tware - contribution direct from Oracle TopLink
 *     tware - updates for new SessionProfiling API
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

/**
* English ResourceBundle for DMSLocalization messages.
*
* @since TOPLink AS 10.0.3
*/
public class DMSLocalizationResource extends ListResourceBundle {
 static final Object[][] contents = {
                                        { "session_name", "The name of the session" },
                                        { "session_login_time", "Time the session was logged in" },
                                        { "client_session_count", "The number of ClientSession was logged in" },
                                        { "unitofwork_count", "Count of total number of UnitOfWork objects created"},
                                        { "unitofwork_commit", "Measures the commit process of the UnitOfWork" },
                                        { "unitofwork_commits", "Number of UnitOfWork commits" },
                                        { "unitofwork_rollback", "Number of UnitOfWork commits that were rollback" },
                                        { "optimistic_lock", "Number of optimistic lock exceptions which were thrown" },
                                         { "query", "Total time spent on operation: {0}" },
                                         { "query_misc", "Total time spent on operation: {0}.  This is for special operations not included in any query nouns, such as batch writing." },
                                        { "rcm_status", "One of [not configured, started, stopped]", "not configured" },
                                        { "rcm_message_received", "Number of messages that been received through the RCM" },
                                        { "rcm_message_sent", "Number of messages that been sent through the RCM" },
                                        { "remote_change_set", "Number of change sets received from remote machines and processed" },
                                        { "connection_in_used", "Number of connections in use per pool for any exclusive ConnectionPool(Write, ExclusiveRead)" },
                                        { "connect_call", "Total number of connect calls made" },
                                        { "disconnect_call", "Total number of disconnect calls made" },
                                        { "cache_hits", "The number of times that the object was found in the cache"},
                                        { "cache_misses", "The number of times that the object was not found in thecache" },
                                        { "sql_prepare", "Time spent in JDBC preparing the Statement." + "Also includes the time spent in EIS creating an Interaction associated with a connection, and creating input and output Record objects" },
                                         { "query_prepareation", "Time to prepare the query" },
                                         { "sql_generation", "Time spent generating SQL. In the case of TopLink expressions, time spent converting Expression to SQL" },
                                         { "database_execute", "Time spent in calls to the JDBC Statement, includes time spent in calls to: close, executeUpdate, and executeQuery" },
                                         { "row_fetch", "Time taken to build Record objects from the jdbc ResultSet. Includes regular SQL calls and stored procedure calls" },
                                         { "object_building", "Time spent build persistent objects from database rows" },
                                         { "merge_time", "Time spent merging changes into the shared cache." },
                                         { "unitofwork_register", "Includes time spent in registerExistingObject, registerNewContainerBean, registerNewContainerBeanForCMP, registerNewObject, registerObject, readIntoWorkingCopy)" },
                                         { "distributed_merge", "Time spent merging remote transaction changes into local shared cache. Appears when cache sync is used" },
                                         { "deleted_object", "Object need to be  removed from identityMap from ObjectChangeSet" },
                                         { "assigning_sequence_numbers", "Includes time spent maintaining the sequence number mechanism and actually setting the sequence number on objects" },
                                         { "caching", "Includes time spent adding, looking up, and removing objects in the cache" },
                                         { "connection", "Time spent managing connections including connecting, reconnecting, and disconnecting from a datasource" },
                                         { "change_set_processed", "The number of ObjectChangeSets was found in the cache" },
                                         { "change_set_not_processed", "The number of ObjectChangeSets thrown away because the object was NOT found in the cache" },
                                         { "logging", "Time spent logging TopLink activities" },
                                         { "wrapping", "Time spent wrapping both CMP and BMP beans" },
                                         { "descriptor_event", "Time spent on execute DescriptorEvent" },
                                         { "session_event", "Time spent on execute SessionEvent" },
                                         { "jts_aftercompletion", "Time spent on JTS afterCompletion(Object status) method" },
                                         { "jts_beforecompletion", "Time spent on JTS beforeCompletion() method" },
                                         { "connection_ping", "Time spent testing database connetion to determine if it can be used."}
};
 
 protected Object[][] getContents() {
     return contents;
 }
}

