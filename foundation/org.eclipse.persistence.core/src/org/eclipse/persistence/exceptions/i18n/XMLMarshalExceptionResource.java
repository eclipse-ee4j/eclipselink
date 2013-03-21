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
                                           { "25007", "A descriptor for class {0} was not found in the project.  For JAXB, if the JAXBContext was bootstrapped using TypeMappingInfo[] you must call a marshal method that accepts TypeMappingInfo as an input parameter." },
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
                                           { "25027", "Unable to retrieve attachment with cid {0} because no AttachmentUnmarshaller was set."},
                                           { "25028", "No reference descriptor found for mapping {1} due to an unknown xsi:type value: {0}."},
                                           { "25029", "For the prefix [{0}] class [{1}] attempted to assign the namespace URI [{2}], but its parent class [{3}] has already assigned the namespace URI [{4}]."},
                                           { "25030", "An error occured while invoking the {0} method on the custom NamespacePrefixMapper: {1}."},
                                           { "25031", "An error occured while processing the namespace prefix mapper: {1}. The method {0} was not found."},
                                           { "25032", "An error occured while invoking the {0} method on the custom CharacterEscapeHandler: {1}."},
                                           { "25033", "An error occured while processing the CharacterEscapeHandler: {1}. The method {0} was not found."},
                                           { "25034", "An error occured while invoking the {0} method on the custom IDResolver: {1}."},
                                           { "25035", "An error occured while processing the IDResolver: {1}. The method {0} was not found."},
                                           { "25036", "The custom IDResolver {1} does not support multiple XML IDs {0}.  Custom IDResolvers must be subclasses org.eclipse.persistence.jaxb.IDResolver if multiple IDs are used."},
                                           { "25037", "A cycle is detected in the object graph.  This will cause an infinite loop: {0}"},
                                           { "25038", "DOMPlatform is not supported with media type applicaion/json."},        
                                           { "25039", "An error occured unmarshalling from {0}"},
                                           { "25040", "An object of type {0} with ID {1} was not found."},
                                           { "25041", "The attribute group specified {0} is not defined for the class {1}."}
                                           
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
