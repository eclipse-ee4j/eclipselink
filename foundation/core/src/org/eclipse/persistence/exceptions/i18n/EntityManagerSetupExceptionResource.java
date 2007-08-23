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
 * English ResourceBundle for EntityManagerSetupException messages.
 *
 * @author: Tom Ware
 */
public class EntityManagerSetupExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "28001", "A ValidationException was thrown while trying to create session: [{0}] " + ". The most likely causes of this issue are that your [{1}] file is not available on the classpath " + "or it does not contain a session called: [{0}]." },
                                           { "28002", "TopLink is attemting to load a ServerSession named [{0}] from [{1}], and not getting a ServerSession." },
                                           { "28003", "TopLink has loaded Session [{0}] from [{1}] and it either does not have a server platform specified or specifies " + "a server platform that does not use and external transaction controller.  Please specify an appropriate server platform if you plan to use JTA." },
                                           { "28004", "Error in setup of EntityManager factory: JavaSECMPInitializer.initializeFromMain returned false." },
                                           { "28005", "An Exception was thrown in setup of EntityManager factory." },
                                           { "28006", "ClassNotFound: [{0}] specified in [{1}] property." },
                                           { "28007", "Failed to instantiate ServerPlatform of type [{0}] specified in [{1}] property." },
                                           { "28008", "Class: {0} was not found while processing annotations." },
                                           { "28009", "Attempted to redeploy a session named {0} without closing it." },
                                           { "28010", "PersistenceUnitInfo {0} has transactionType JTA, but doesn't have jtaDataSource." },
                                           { "28011", "The session, [{0}], built for a persistence unit was not available at the time it was deployed.  This means that somehow the session was removed from the container in the middle of the deployment process." },
                                           { "28012", "Value [{0}] is of incorrect type for property [{2}], value type should be [{1}]." },
                                           { "28013", "Attempted to deploy PersistenceUnit [{0}] for which predeploy method either had not called or had failed" },
                                           { "28014", "Exception was thrown while processing property [{0}] with value [{1}]." },
                                           { "28015", "Failed to instantiate SessionLog of type [{0}] specified in [{1}] property." },
                                           { "28016", "The persistence unit with name [{0}] does not exist." },
                                           { "28017", "Unable to predeploy PersistenceUnit [{0}] in invalid state [{1}]" },
                                           { "28018", "Predeployment of PersistenceUnit [{0}] failed." },
                                           { "28019", "Deployment of PersistenceUnit [{0}] failed." },
                                           { "28020", "The session with name [{0}] loaded from [{1}] is [{2}], it however must be ServerSession." },
                                           { "28021", "PersistenceUnit [{0}] attempts to load a session from [{1}] without providing a session name.  A session name should be provided by defining the eclipselink.session-name property." },
                                           { "28022", "Value [true] for the property [eclipselink.weaving] is incorrect when global instrumentation is null, value should either be null or false." }
                                                                           
   };

    /**
      * Return the lookup table.
      */
    protected Object[][] getContents() {
        return contents;
    }
}
