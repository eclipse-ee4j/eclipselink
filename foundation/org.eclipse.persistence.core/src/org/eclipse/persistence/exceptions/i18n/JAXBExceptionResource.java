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
        {"50001", "The class {0} requires a zero argument constructor or a specified factory method.  Note that non-static inner classes do not have zero argument constructors and are not supported."},
        {"50002", "Factory class specified without factory method on class {0}"},
        {"50003", "The factory method named {0} is not declared on the class {1}"},
        {"50004", "XmlAnyAttribute is invalid on property {0}. Must be used with a property of type Map"},
        {"50005", "Only one property with XmlAnyAttribute allowed on class {0}"},
        {"50006", "Invalid XmlElementRef on property {0} on class {1}. Referenced Element not declared"},
        {"50007", "Name collision.  Two classes have the XML type with uri {0} and name {1}"},
        {"50008", "Unsupported Node class {0}.  The createBinder(Class) method only supports the class org.w3c.dom.Node"},
        {"50009", "The property or field {0} is annotated to be transient so can not be included in the proporder annotation."},
        {"50010", "The property or field {0} must be an attribute because another field or property is annotated with XmlValue."},
        {"50011", "The property or field {0} can not be annotated with XmlValue since it is a subclass of another XML-bound class."},
        {"50012", "The property or field {0} was specified in propOrder but is not a valid property."},
        {"50013", "The property or field {0} is required to be included in the propOrder element of the XMLType annotation."},
        {"50014", "The property or field {0} with the XmlValue annotation must be of a type that maps to a simple schema type."},
        {"50015", "XmlElementWrapper is only allowed on a collection or array property but [{0}] is not a collection or array property."},
        {"50016", "Property [{0}] has an XmlID annotation but its type is not String."},
        {"50017", "Invalid XmlIDREF on property [{0}].  Class [{1}] is required to have a property annotated with XmlID."},
        {"50018", "XmlList is only allowed on a collection property but [{0}] is not a collection property."},
        {"50019", "Invalid parameter type encountered while processing external metadata via properties Map.  The Value associated with Key [eclipselink-oxm-xml] is required to be one of [Map<String, Source>], where String = package, Source = handle to metadata file."},
        {"50021", "Invalid parameter type encountered while processing external metadata via properties Map.  It is required that the Key be of type [String] (indicating package name)."},
        {"50022", "Invalid parameter type encountered while processing external metadata via properties Map.  It is required that the Value be of type [Source] (handle to metadata file)."},
        {"50023", "A null Value for Key [{0}] was encountered while processing external metadata via properties Map.  It is required that the Value be non-null and of type [Source] (handle to metadata file)."},
        {"50024", "A null Key was encountered while processing external metadata via properties Map.  It is required that the Key be non-null and of type [String] (indicating package name)."},
        {"50025", "Could not load class [{0}] declared in the external metadata file.  Please ensure that the class name is correct, and that the correct ClassLoader has been set."},
        {"50026", "An exception occurred while attempting to create a JAXBContext for the XmlModel."},
        {"50027", "An exception occurred while attempting to unmarshal externalized metadata file."},
        {"50028", "A new instance of [{0}] could not be created."},
        {"50029", "The class [{0}] provided on the XmlCustomizer does not implement the org.eclipse.persistence.config.DescriptorCustomizer interface."},
        {"50030", "An attempt was made to set more than one XmlID property on class [{1}].  Property [{0}] cannot be set as XmlID, because property [{2}] is already set as XmlID."},
        {"50031", "An attempt was made to set more than one XmlValue property on class [{0}].  Property [{1}] cannot be set as XmlValue, because property [{2}] is already set as XmlValue."},
        {"50032", "An attempt was made to set more than one XmlAnyElement property on class [{0}].  Property [{1}] cannot be set as XmlAnyElement, because property [{2}] is already set as XmlAnyElement."},
        {"50033", "The DomHandlerConverter for DomHandler [{0}] set on property [{1}] could not be initialized."},
        {"50034", "The property or field [{0}] can not be annotated with XmlAttachmentRef since it is not a DataHandler."},
        {"50035", "Since the property or field [{0}] is set as XmlIDREF, the target type of each XmlElement declared within the XmlElements list must have an XmlID property.  Please ensure the target type of XmlElement [{1}] contains an XmlID property."},
        {"50036", "The TypeMappingInfo with XmlTagName QName [{0}] needs to have a non-null Type set on it."},
        {"50037", "The java-type with package [{0}] is not allowed in the bindings file keyed on package [{1}]."},
        {"50038", "DynamicJAXBContext can not be created from concrete Classes.  Please use org.eclipse.persistence.jaxb.JAXBContext, or specify org.eclipse.persistence.jaxb.JAXBContextFactory in your jaxb.properties file, to create a context from existing Classes."},
        {"50039", "Error creating DynamicJAXBContext: Node must be an instance of either Document or Element."},
        {"50040", "Error creating DynamicJAXBContext."},
        {"50041", "Enum constant [{0}] not found."},
        {"50042", "Error creating DynamicJAXBContext: Session name was null."},
        {"50043", "Error creating DynamicJAXBContext: Source was null."},
        {"50044", "Error creating DynamicJAXBContext: InputStream was null."},
        {"50045", "Error creating DynamicJAXBContext: Node was null."},
        {"50046", "Error creating DynamicJAXBContext: XJC was unable to generate a CodeModel."},
        {"50047", "Class [{0}] not found."},
        {"50048", "The read transformer specified for property [{0}] has both class and method. Either class or method is required, but not both."},
        {"50049", "The read transformer specified for property [{0}] has neither class nor method. A class or method is required."},
        {"50050", "The write transformer specified for the xml-path  [{1}] of property [{0}] has both class and method. Either class or method is required, but not both."},
        {"50051", "The write transformer specified for the xml-path  [{1}] of property [{0}] has neither class nor method. A class or method is required."},
        {"50052", "The write transformer specified for property [{0}] does not have an xml-path set. An xml-path is required."},
        {"50053", "The transformation method [{0}] with parameters (), (AbstractSession) or (Session) not found."},
        {"50054", "Transformer class [{0}] not found. Please ensure that the class name is correct, and that the correct ClassLoader has been set."},
        {"50055", "Error creating DynamicJAXBContext: ECLIPSELINK_OXM_XML_KEY not found in properties map, or map was null."},
        {"50056", "Property [{0}] contains an XmlJoinNode declaration, but the referenced class [{1}] is not applicable for this type of relationship."},
        {"50057", "Property [{1}] in class [{0}] references a class [{2}] that is marked transient, which is not allowed."}
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }

}
