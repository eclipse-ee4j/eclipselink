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
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

/**
 * English ResourceBundle for ToStringLocalization messages.
 *
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class ToStringLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "datasource_name", "datasource name" },
                                           { "datasource", "datasource" },
                                           { "error_printing_expression", "Error printing expression" },
                                           { "not_instantiated", "not instantiated" },
                                           { "connected", "connected" },
                                           { "disconnected", "disconnected" },
                                           { "nest_level", "(nest level = {0})" },
                                           { "commit_depth", "(commit depth = {0})" },
                                           { "empty_commit_order_dependency_node", "Empty Commit Order Dependency Node" },
                                           { "node", "Node ({0})" },
                                           { "platform", "platform" },
                                           { "user_name", "user name" },
                                           { "server_name", "server name" },
                                           { "database_name", "database name" },
                                           { "datasource_URL", "datasource URL" },
                                           { "depth_reset_key", "(depth = {0}, reset key = {1}, reset version = {2})" },
                                           { "pooled", "pooled" },
                                           { "login", "login" },
                                           { "lazy", "lazy" },
                                           { "non_lazy", "non-lazy" },
                                           { "min_max", "(minimum connections = {0}, maximum connections = {1})" },
                                           { "begin_profile_of", "Begin profile of" },
                                           { "end_profile", "End profile" },
                                           { "profile", "Profile" },
                                           { "class", "class" },
                                           { "number_of_objects", "number of objects" },
                                           { "total_time", "total time" },
                                           { "local_time", "local time" },
                                           { "profiling_time", "profiling time" },
                                           { "time_object", "time/object" },
                                           { "objects_second", "objects/second" },
                                           { "shortestTime", "shortest time" },
                                           { "longestTime", "longest time" },
                                           { "context", "Context:	" },
                                           { "name", "Name: " },
                                           { "schema", "Schema: " },
                                           { "no_streams", "no stream(s)" },
                                           { "reader", "reader" },
                                           { "multiple_readers", "multiple readers" },
                                           { "writer", "writer" },
                                           { "no_files", "no file(s)" },
                                           { "mulitple_files", "mulitple files" },
                                           { "unknown", "unknown" },
                                           { "connector", "connector" },
                                           { "staticweave_processor_unknown_outcome", "Weaving classes stored in a directory and outputing to a JAR often leads to unexpected results." },
                                           
                                           { "staticweave_commandline_help_message_1of19", "  Usage: StaticWeave [options] source target" },
                                           { "staticweave_commandline_help_message_2of19", "  Options:" },
                                           { "staticweave_commandline_help_message_3of19", "    -classpath classpath" },
                                           { "staticweave_commandline_help_message_4of19", "           Set the user class path.  Use \";\" as delimiter in Windows and \":\" in Unix." },
                                           { "staticweave_commandline_help_message_5of19", "    -persistenceinfo" },
                                           { "staticweave_commandline_help_message_6of19", "           Explicitly identify where META-INF/persistence.xml is stored.  It must be the root of META-INF/persistence.xml." },
                                           { "staticweave_commandline_help_message_7of19", "           This option is typically used to specify the main jar when weaving files referenced by <jar-file> in persistence.xml." },
                                           { "staticweave_commandline_help_message_8of19", "    -persistencexml" },
                                           { "staticweave_commandline_help_message_9of19", "           Identify the location of the persistence.xml relative to the root of the persistence unit if it is somewhere other that META-INF/persistence.xml" },
                                           { "staticweave_commandline_help_message_10of19", "    -log" },
                                           { "staticweave_commandline_help_message_11of19", "           Specify logging file." },
                                           { "staticweave_commandline_help_message_12of19", "    -loglevel" },
                                           { "staticweave_commandline_help_message_13of19", "           Specify the integer value of the logging level (8=OFF,7=SEVERE,6=WARNING,5=INFO,4=CONFIG,3=FINE,2=FINER(default),1=FINEST,0=ALL)." },
                                           { "staticweave_commandline_help_message_14of19", "    The classpath must contain all the classes necessary to load the classes in the source." },
                                           { "staticweave_commandline_help_message_15of19", "    The weaving will be performed in place if source and target point to the same location.  Weaving in place is ONLY applicable for directory-based sources." },
                                           { "staticweave_commandline_help_message_16of19", "  Example:" },
                                           { "staticweave_commandline_help_message_17of19", "    To weave all entites contained in C:\\foo-source.jar with its persistence.xml contained within the C:\\foo-containing-persistence-xml.jar," },
                                           { "staticweave_commandline_help_message_18of19", "    and output to C:\\foo-target.jar:" },
                                           { "staticweave_commandline_help_message_19of19", "    StaticWeave -persistenceinfo C:\\foo-containing-persistence-xml.jar -classpath C:\\classpath1;C:\\classpath2 C:\\foo-source.jar C:\\foo-target.jar"},
                                           
                                           { "sdo_classgenerator_usage_help_1of8", "{0} Usage: org.eclipse.persistence.sdo.helper.{0} [-options]" },
                                           { "sdo_classgenerator_usage_help_2of8", "Options:" },
                                           { "sdo_classgenerator_usage_help_3of8", "    -help                        Prints the help message text" },
                                           { "sdo_classgenerator_usage_help_4of8", "    -sourceFile <FileName>       The input schema file (required)" },
                                           { "sdo_classgenerator_usage_help_5of8", "    -targetDirectory <DirName>   The directory to generate Java source (optional)" },
                                           { "sdo_classgenerator_usage_help_6of8", "    -logLevel <level int>        Specify the integer value of the logging level (8=OFF,7=SEVERE,6=WARNING,5=INFO,4=CONFIG,3=FINE,2=FINER(default),1=FINEST,0=ALL)." },
                                           { "sdo_classgenerator_usage_help_7of8", "    -noInterfaces                Do not generate interface classes" },
                                           { "sdo_classgenerator_usage_help_8of8", "    -noImpls                     Do not generate impl classes" },
                                           
                                           { "sdo_classgenerator_usage_missing_sourcefile_value", "{0} The value of -sourceFile was not specified."},
                                           { "sdo_classgenerator_usage_missing_targetdir", "{0} The value of -targetDirectory was not specified."},
                                           { "sdo_classgenerator_usage_missing_sourcefile", "{0} The -sourceFile parameter was not specified and is required."}
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
