/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     09/24/2014-2.6 Rick Curtis
//       - 443762 : Misc message cleanup.
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

public class PersistenceUnitLoadingExceptionResource extends ListResourceBundle {

        static final Object[][] contents = {
                                           { "30001", "An exception was thrown while trying to load a persistence unit for directory: {0}"},
                                           { "30002", "An exception was thrown while trying to load a persistence unit for jar file: {0}"},
                                           { "30003", "An exception was thrown while processing persistence unit at URL: {0}"},
                                           { "30004", "An exception was thrown while processing persistence.xml from URL: {0}"},
                                           { "30005", "An exception was thrown while searching for persistence archives with ClassLoader: {0}"},
                                           { "30006", "An exception was thrown while searching for entities at URL: {0}"},
                                           { "30007", "An exception was thrown while loading class: {0} to check whether it implements @Entity, @Embeddable, or @MappedSuperclass."},
                                           { "30008", "File path returned was empty or null"},
                                           { "30009", "An exception was thrown while trying to load persistence unit at url: {0}"},
                                           { "30010", "An exception was thrown while loading ORM XML file: {0}"},
                                           { "30011", "EclipseLink could not get classes from the URL: {0}.  EclipseLink attempted to read this URL as a jarFile and as a Directory and was unable to process it."},
                                           { "30012", "EclipseLink could not get persistence unit info from the URL:{0}"},
                                           { "30013", "An exception was thrown while trying to build a persistence unit name for the persistence unit [{1}] from URL: {0}."},
                                           { "30014", "The persistence unit specifies validation-mode as \"CALLBACK\" but a Bean Validation ValidatorFactory could not be initialized. Please refer to the nested exception for details. Please ensure that Bean Validation API and Bean Validation Provider is available in classpath."},
                                           { "30015", "An exception was thrown while loading validation group class: {0}."},
                                           { "30016", "Session name {0} cannot be used by persistence unit {1}, it is already used by persistence unit {2}"},
                                           { "30017", "Persistence unit {0} is defined in both URL:{1} and URL:{2}. Cannot have several persistence units with the same name loaded by the same classloader."},
                                           { "30018", "Exception: {1} occurred while trying to instantiate user-specified ArchiveFactory: {0}."},
                                           { "30019", "You are calling refreshMetadata on an EntityManagerFactory for persistence unit {0}.  The metadata cannot be refreshed because this EntityManagerFactory was created from a session object rather derived from a persistence unit."}
    };

    /**
      * Return the lookup table.
      */
    protected Object[][] getContents() {
        return contents;
    }
}
