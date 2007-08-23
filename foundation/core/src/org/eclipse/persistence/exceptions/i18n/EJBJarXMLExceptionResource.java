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
 * English ResourceBundle for EJBJarXMLException and org.eclipse.persistence.tools.ejbjar.ValidationManager messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: TopLink maintenance team
 */
public class EJBJarXMLExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "72000", "Error reading ejb-jar.xml file" },
                                           
    { "72001", "ejb-jar.xml does not use expected doc type " + "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN" },
                                           
    { "72002", "There was a concrete instance variable named \"{0}\" already defined on the class \"{1}\".  Please either 1) remove this instance variable from the class, refresh in the MW, and update from the ejb-jar.xml again, or 2) remove the field from the ejb-jar.xml file and update again." },
                                           { "72003", "A mapping for the field named \"{0}\" could not be created on the descriptor \"{1}\".  Check to see that there are abstract getters and setters accessible for this field." },
                                           { "72021", "The class \"{0}\" could not be found on the classpath.  A descriptor could not be created for it." },
                                           { "72004", "Neither local home nor remote home interface for the class \"{0}\" was found on the classpath.  The finders for this class were not updated." },
                                           { "72005", "The finder method named \"{0}\" was not defined on a home interface for the entity \"{1}\".  The finder was not updated." },
                                           { "72006", "TopLink Mapping Workbench could not determine a project persistence type from the xml file." },
                                           { "72007", "Could not find a corresponding ejbSelect method in the bean class: \"{0}\".  No information was updated for this query." },
                                           
    { "72008", "The EJB descriptor, \"{0}\", must have an EJB name specified." },
                                           { "72009", "The EJB descriptor, \"{0}\", must have a primary key class specified." },
                                           { "72010", "The EJB name, \"{0}\", was used by more than one descriptor.  EJB names must be unique." },
                                           { "72011", "The element \"{1}\" cannot have an empty text attribute \"{0}\"" },
                                           { "72012", "There were multiple entities with the ejb-name \"{0}\" in the xml file." },
                                           { "72013", "The entity \"{0}\" did not have a valid cmp-version.  The version must be \"1.x\" or \"2.x\"." },
                                           { "72014", "The ejb-name \"{0}\" in the ejb-relationship-role \"{1}\" is not found in any entity of the xml file" },
                                           { "72015", "There was an invalid multiplicity in a relationship involving the mapping \"{0}\" in the descriptor \"{1}\".  The multiplicity must be \"One\" or \"Many\"." },
                                           { "72016", "The entity \"{0}\" did not have a valid persistence-type.  The type must be \"Bean\" or \"Container\"." },
                                           { "72017", "The query method-name \"{0}\" does not start with \"find\" or \"ejbSelect\".  No information was updated for this query." },
                                           { "72018", "TopLink Mapping Workbench requires that all entities in the xml file for this project have the same persistence-type and/or cmp-version.  The project will be set according to the persistence-type and/or cmp-version of the first entity in the file." },
                                           { "72019", "The project, \"{0}\", must have at least one EJB descriptor." },
                                           { "72022", "There is an element, \"{1}\", that does not have a required attribute, \"{0}\"." },
                                           { "72023", "EntityBean {1} has an ABSTRACT setter with an EntityBean attribute type for attribute {0} but no corresponding CMR field." },
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}