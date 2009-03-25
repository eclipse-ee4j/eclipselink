/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 11/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * <b>Purpose:</b><p>English ResourceBundle for JAXBException.</p>
 */
public class JAXBExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
        {"50000", "The path {0} contains no ObjectFactory or jaxb.index file and no sessions.xml was found"},
        {"50001", "The class {0} requires a zero argument constructor or a specified factory method"},
        {"50002", "Factory class specified without factory method on class {0}"},
        {"50003", "The factory method named {0} is not declared on the class {1}"},
        {"50004", "XmlAnyAttribute is invalid on property {0}. Must be used with a property of type Map"},
        {"50005", "Only one property with XmlAnyAttribute allowed on class {0}"},
        {"50006", "Invalid XmlElementRef on property {0} on class {1}. Referenced Element not declared"},
        {"50007", "Name collision.  Two class have the XML type with uri {0} and name {1}"},
    };
    
    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }    

}
