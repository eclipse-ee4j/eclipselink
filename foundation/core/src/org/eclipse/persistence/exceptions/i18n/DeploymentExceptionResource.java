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
 * English ResourceBundle for DeploymentException messages.
 *
 * Creation date: (2/26/01 9:47:38 AM)
 * @author: TopLink maintenance team
 */
public class DeploymentExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "14001", "No TopLink project was specified for this bean." },
                                           { "14003", "No TopLink project was found to be associated with the given identifier: [{0}]" },
                                           { "14004", "An error occurred while instantiating the DeploymentCustomization class [{0}]. {1}[{2}]" },
                                           { "14005", "An error occurred while running the DeploymentCustomization class [{0}]. {1}[{2}]" },
                                           { "14007", "Invalid transaction isolation string: [{0}]" },
                                           { "14008", "Invalid cache usage: [{0}]" },
                                           { "14011", "Unable to create the InitialContext to connect to the specified dataSource [{0}]: [{1}]" },
                                           { "14016", "An error occurred while setting up the project: [{0}]" },
                                           { "14019", "Deployment descriptor {0} was not found" },
                                           { "14020", "Error reading deployment descriptor {0}: [{1}]" },
                                           { "14022", "An error occurred while configuring the descriptor for the bean [{0}].  " + "The project is set to use remote relationships but the bean does not contain " + "a remote home interface.  Add the correct remoteHome and remote bean interfaces " + "or change the project to use local relationships by removing the " + "use-remote-relationships tag in the toplink-ejb-jar.xml or setting it to false." },
                                           { "14023", "Could not find the generated subclass for the bean {0}.  " + "Make sure you've run ejbc to create the deployable jar file if you have " + "added any new beans to your project." },
                                           { "14024", "Could not read TopLink project during CMP concrete classes code generation" },
                                           { "14026", "For EJB2.0 descriptors, mapping for the attribute [{0}], in the class [{1}], must use Transparent indirection." },
                                           { "14027", "For EJB2.0 descriptors, one-to-one mapping for the attribute [{0}], in the class [{1}], must use ValueHolder indirection." },
                                           { "14028", "The descriptor for [{0}] does not contain a corresponding mapping for the container managed attribute [{1}]." },
                                           { "14029", "The finder {0} is declared in the ejb-jar.xml but not defined on the [{1}] home interface. " },
                                           { "14030", "The ejbSelect method {0} is declared in the ejb-jar.xml but not defined on the [{1}] abstract bean class. " },
                                           { "14031", "The cmp field [{0} is declared in the ejb-jar.xml but not defined on the bean [{1}]" },
                                           { "14032", "The cmp field [{0}] is declared in the ejb-jar.xml but the " + "corresponding abstract getter and/or setter is not defined on the [{1}] abstract bean class." },
                                           { "14033", "The descriptor for [{0}] is missing." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
