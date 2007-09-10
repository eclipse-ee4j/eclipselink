/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for MigrationUtilityException messages.
 *
 * @author: King Wang
 * @since: Oracle TopLink 10g (10.0.3)
 */
public class MigrationUtilityExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "26001", "WLS->OC4J TopLink migration exception: There contains multiple migratable JARs in the input EAR({0}), and orion-ejb-jar.xml is also provided in the input. " + "\nThe migration utility only supports single migratable JAR in an EAR if orion-ejb-jar.xml is also in the input." },
                                           { "26002", "The program security manager prevents the migration utility from creating a JAR class loader for Jar file({0})" },
                                           { "26003", "The program security manager prevents the migration utility from creating directory({0})" },
                                           { "26004", "The file({0}) is not accessible by the migration utility" },
                                           { "26005", "The program security manager prevents the migration utility from deleting directory({0})" },
                                           { "26006", "i/o exception occurs when reading data from an input stream" },
                                           { "26007", "i/o exception occurs when closing data from an input or output stream" },
                                           { "26008", "i/o exception occurs when closing data from an zip file" },
                                           { "26009", "Jar file({0}) not found or accessible in the specified path" },
                                           { "26010", "Query method({0}) with parameters({1}) of the entity({2}) defined in ejb-jar.xml is not well defined as it starts by neither 'find' nor 'ejbSelect'" },
                                           { "26011", "The class({1}) defined as a parameter type of the query({0}) of the entity({2}) in ejb-jar.xml could not be loaded. Most likely, the class is not included not in the classpath." },
                                           { "26012", "Finder method({0}) with parameters({1}) defined in ejb-jar.xml is not defined at the entity({2})'s (local and/or remote) home interface" },
                                           { "26013", "ejbSelect method({0}) with parameters({1}) defined in ejb-jar.xml is not defined at the entity({2})'s bean class" },
                                           { "26014", "Entity({0}) specified in weblogic-cmp-jar.xml is not defined in weblogic-ejb-jar.xml." },
                                           { "26015", "No entity element is defined in weblogic-cmp-rdbms-jar.xml file" },
                                           { "26016", "The weblogic cmp descriptor file({0}) specified in the weblogic-ejb-jar.xml is not found in the directory ({1})" },
                                           { "26017", "The cmp descriptor file({1}) is not found in the jar({0}) file to be migrated." },
                                           { "26018", "Some of the entity beans ({0}) defined at ejb-jar.xml in the jar ({1}) are not explicitly mapped in orion-ejb-jar.xml. You need to provide the completely mapped orion-ejb-jar.xml to the migration tool. You can obtain the completely mapped orion-ejb-jar.xml from the /application-deployment directory after deploying the application." },
                                           { "26019", "The entity ({0}) in orion-ejb-jar.xml is not mapped as no table or primary key mapping is specified.   You need to provide the completely mapped orion-ejb-jar.xml to the migration tool. You can obtain the completely mapped orion-ejb-jar.xml from the /application-deployment directory after deploying the application." },
                                           { "26020", "The entity ({0}) specified in orion-ejb-jar.xml is not defined in ejb-jar.xml." },
                                           { "26021", "The entity ({0}) in weblogic-cmp-jar.xml is not mapped as no table is specified.   You need to provide the completely mapped weblogic-cmp-jar.xml to the migration tool. You can obtain the completely mapped weblogic-ejb-jar.xml from the weblogic deployment directory after deploying the application." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}