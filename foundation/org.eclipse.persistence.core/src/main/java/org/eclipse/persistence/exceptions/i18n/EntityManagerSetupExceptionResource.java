/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for EntityManagerSetupException messages.
 *
 * @author Tom Ware
 */
public final class EntityManagerSetupExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "28001", "A ValidationException was thrown while trying to create session: [{0}]. " +
                                                   "The most likely causes of this issue are that your [{1}] file is not available on the classpath " +
                                                   "or it does not contain a session called: [{0}]." },
                                           { "28002", "EclipseLink is attempting to load a ServerSession named [{0}] from [{1}], and not getting a ServerSession." },
                                           { "28003", "EclipseLink has loaded Session [{0}] from [{1}] and it either does not have a server platform specified or specifies " +
                                                   "a server platform that does not use and external transaction controller.  Specify an appropriate server platform if you plan to use JTA." },
                                           { "28004", "Error in setup of EntityManager factory: JavaSECMPInitializer.initializeFromMain returned false." },
                                           { "28005", "An Exception was thrown in setup of EntityManager factory." },
                                           { "28006", "ClassNotFound: [{0}] specified in [{1}] property." },
                                           { "28007", "Failed to instantiate ServerPlatform of type [{0}] specified in [{1}] property." },
                                           { "28008", "Class: {0} was not found while processing annotations." },
                                           { "28009", "Attempted to redeploy a session named {0} without closing it." },
                                           { "28010", "PersistenceUnitInfo {0} has transactionType JTA, but does not have a jtaDataSource defined." },
                                           { "28011", "The session, [{0}], built for a persistence unit was not available at the time it was deployed.  This means that somehow the session was removed from the container in the middle of the deployment process." },
                                           { "28012", "Value [{0}] is of incorrect type for property [{2}], value type should be [{1}]." },
                                           { "28013", "Unable to deploy PersistenceUnit [{0}] in invalid state [{1}]." },
                                           { "28014", "Exception was thrown while processing property [{0}] with value [{1}]." },
                                           { "28015", "Failed to instantiate SessionLog of type [{0}] specified in [{1}] property." },
                                           { "28016", "The persistence unit with name [{0}] does not exist." },
                                           { "28017", "Unable to predeploy PersistenceUnit [{0}] in invalid state [{1}]." },
                                           { "28018", "Predeployment of PersistenceUnit [{0}] failed." },
                                           { "28019", "Deployment of PersistenceUnit [{0}] failed. Close all factories for this PersistenceUnit." },
                                           { "28020", "The session with name [{0}] loaded from [{1}] is [{2}], it however must be ServerSession." },
                                           { "28021", "PersistenceUnit [{0}] attempts to load a session from [{1}] without providing a session name.  A session name should be provided by defining the eclipselink.session-name property." },
                                           { "28022", "Value [true] for the property [eclipselink.weaving] is incorrect when global instrumentation is null, value should either be null, false, or static." },
                                           { "28023", "The method invocation of the method [{0}] on the object [{1}], of class [{2}], triggered an exception." },
                                           { "28024", "Cannot reflectively access the method [{0}] for object [{1}], of class [{2}]." },
                                           { "28025", "The persistence unit with name [{0}] has returned a [null] temporary classLoader - weaving has been disabled for this session.  You may use static weaving is an optional workaround." },
                                           { "28026", "org.eclipse.persistence.jpa.osgi.PersistenceProvider does not support container deployment (createContainerEntityManagerFactory).  Use org.eclipse.persistence.jpa.PersistenceProvider instead." },
                                           { "28027", "An attempt has been made to use PersistenceUnit [{0}], but no bundle is available that defines that persistence unit." },
                                           { "28028", "Failed to instantiate class instance [{0}] for persistence unit property [{1}], ensure constructor is defined correctly." },
                                           { "28029", "PersistenceUnit [{0}] tries both to use sessions.xml (specifies eclipselink.sessions-xml property) and to be a composite (specifies eclipselink.composite-unit property with value true). These modes are incompatible." },
                                           { "28030", "PersistenceUnit [{0}] specifies eclipselink.composite-unit.member property with value true. That means it cannot be used standalone, but only as a composite member." },
                                           { "28031", "Missing the required property [{0}]." },
                                           { "28032", "Failed to create temporary classloader with doPrivileged." }
   };

    /**
     * Default constructor.
     */
    public EntityManagerSetupExceptionResource() {
        // for reflection
    }

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
