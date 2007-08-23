/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

import org.eclipse.persistence.internal.helper.Helper;

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
                                           { "depth_reset_key", "(depth = {0}, reset key = {1})" },
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
                                           { "staticweave_commandline_help_message",
                                               "  Usage: StaticWeave [options] source target\r\r"+
                                               "  Options:\r"+
                                               "    -classpath classpath\r" +
                                               "           Set the user class path. use \";\" as delimiter in windows and \":\" in unix.\r"+
                                               "    -persistenceinfo \r" +
                                               "           Explicitly identify where META-INF/persistence.xml is stored. It must be the root of META-INF/persistence.xml.\r"+
                                               "    -log \r" +
                                               "           Specify logging file.\r"+
                                               "    -loglevel \r" +
                                               "           Specify a literal value of the toplink logging level(OFF,SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST).\r"+
                                               "    The classpath must contain all the classes necessary to load the classes in the source.\r"+ 
                                               "    The weaving will be performed in place if source and target point to the same location. Weaving in place is ONLY applicable for directory-based sources.\r"+ 
                                               "  Example:\r" +
                                               "    To weave all entites contained in c:\\foo-source.jar with its persistence.xml contained within the c:\\foo-containing-persistence-xml.jar,\r"+
                                               "    and output to c:\\foo-target.jar,\r"+
                                               "    StaticWeave -persistenceinfo c:\\foo-containing-persistence-xml.jar -classpath c:\\classpath1;c:\\classpath2 c:\\foo-source.jar c:\\foo-target.jar\r\r"},
                                           { "sdo_classgenerator_usage_help", "{0} Usage: org.eclipse.persistence.sdo.helper.{0} [-options]" + Helper.cr() + Helper.cr() + "Options:" + Helper.cr() + //
                                               	"    -help                        " + "Prints the help message text" + Helper.cr() + "    -sourceFile <FileName>       " + //
                                               	"The input schema file (required)" + Helper.cr() + "    -targetDirectory <DirName>   " + //
                                               	"The directory to generate Java source(optional)"  + 	Helper.cr() + "    -logLevel <level int>        " + //
                                               	"Specify the integer value of the toplink logging level(8=OFF,7=SEVERE,6=WARNING,5=INFO,4=CONFIG,3=FINE,2=FINER(default),1=FINEST,0=ALL)." + Helper.cr()},
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