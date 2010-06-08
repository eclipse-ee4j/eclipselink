/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 *
 * English ResourceBundle for XMLValidationException.
 *
 * @author    Rick Barkhouse - rick.barkhouse@oracle.com
 * @since    OracleAS TopLink 10<i>g</i> (10.0.3), 03/25/2003 10:04:12
 */
public class XMLMarshalExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "25001", "Invalid XPath string: {0}" },
                                           { "25002", "An integer index could not be parsed from this XPath string: {0}" },
                                           { "25003", "An error occurred marshalling the object" },
                                           { "25004", "An error occurred unmarshalling the document" },
                                           { "25005", "An error occurred validating the object" },
                                           { "25006", "A default root element was not specified for the XMLDescriptor mapped to {0}" },
                                           { "25007", "A descriptor for class {0} was not found in the project" },
                                           { "25008", "A descriptor with default root element {0} was not found in the project" },
                                           { "25010", "A schema reference was not specified for the XMLDescriptor mapped to {0}" },
                                           { "25011", "A null argument was encountered" },
                                           { "25012", "An error occurred resolving the XML Schema." },
                                           { "25013", "An error occurred while trying to set the schemas." },
                                           { "25014", "An error occurred while trying to instantiate the schema platform." },
                                           { "25015", "An error occurred trying to resolve the namespace URI for {0}. A namespace resolver was not specified on the descriptor." },
                                           { "25016", "A namespace for the prefix {0} was not found in the namespace resolver." },
                                           { "25017", "Either enumClass or enumClassName must be set on the JAXBTypesafeEnumConverter." },
                                           { "25018", "The fromString method on the enum class {0} does not exist or could not be invoked." },
                                           { "25019", "The specified enum class {0} could not be found." },
                                           { "25020", "The method 'getResult()' must not be called before the 'endDocument()' event has been called." },
                                           { "25021", "Class {0} invalid for SwaRef. Must be javax.activation.DataHandler." },
                                           { "25022", "Unable to marshal Image. No encoder available for mimeType {0}." },
                                           { "25023", "No descriptor found while unmarshalling element mapped to attribute {0}." },
                                           { "25024", "An error occurred instantiating the UnmappedContentHandler class {0}." },
                                           { "25025", "The UnmappedContentHandler class {0} set on the XMLUnmarshaller must implement org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler." },
                                           { "25026", "Unable to update node [{0}], object not found in cache." },
                                           { "25027", "Unable to retrieve attachment with cid {0} because no AttachmentUnmarshaller was set."}
                                          
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
