/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

/**
 * These are properties that may be set on an instance of Marshaller.  Below is
 * an example of using the property mechanism to enable MOXy's JSON binding for 
 * an instance of Marshaller.
 * <pre>
 * Marshaller marshaller = jaxbContext.createMarshaller();
 * marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
 * </pre>
 */
public class MarshallerProperties {

    /**
     * The Constant CHARACTER_ESCAPE_HANDLER.  Allows for customization of character escaping when marshalling.
     * Value should be an implementation of org.eclipse.persistence.oxm.CharacterEscapeHandler.
     * @since 2.3.3
     */
    public static final String CHARACTER_ESCAPE_HANDLER = "eclipselink.character-escape-handler";

    /**
     * The Constant INDENT_STRING. Property used to set the string used when indenting formatted marshalled documents.
     * The default for formatted documents is &quot;   &quot; (three spaces).
     * @since 2.3.3
     */
    public static final String INDENT_STRING = "eclipselink.indent-string";

    /**
     * The Constant JSON_ATTRIBUTE_PREFIX. This can be used to specify a prefix to prepend
     * to attributes.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_ATTRIBUTE_PREFIX = JAXBContextProperties.JSON_ATTRIBUTE_PREFIX;

    /**
     * The Constant JSON_INCLUDE_ROOT. This can be used  to specify if the
     * @XmlRootElement should be marshalled.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_INCLUDE_ROOT = JAXBContextProperties.JSON_INCLUDE_ROOT;

    /**
     * The Constant JSON_NAMESPACE_SEPARATOR.  This can be used to specify the separator
     * that will be used when separating prefixes and localnames.  Only applicable when
     * namespaces are being used. Value should be a Character.
     * @since 2.4
     */
    public static final String JSON_NAMESPACE_SEPARATOR  = JAXBContextProperties.JSON_NAMESPACE_SEPARATOR;

    /**
     * The Constant JSON_VALUE_WRAPPER.  This can be used to specify the wrapper
     * that will be used around things mapped with @XmlValue.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_VALUE_WRAPPER = JAXBContextProperties.JSON_VALUE_WRAPPER;

    /**
     * The Constant JSON_MARSHAL_EMPTY_COLLECTIONS.  This can be used to specify the wrapper
     * that will be used around things mapped with @XmlValue.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_MARSHAL_EMPTY_COLLECTIONS = "eclipselink.json.marshal-empty-collections";
    
    /**
     * The Constant MEDIA_TYPE. This can be used to set the media type.
     * Supported values are "application/xml" and "application/json".
     * @since 2.4
     */
    public static final String MEDIA_TYPE = JAXBContextProperties.MEDIA_TYPE;

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the namespace prefixes used 
     * while marshalling to XML.  Used for both marshal and unmarshal when mediaType is set to "application/json".
     * Value is either a Map<String, String> of URIs to prefixes, or an implementation of 
     * org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.3.3 
     */
    public static final String NAMESPACE_PREFIX_MAPPER = JAXBContextProperties.NAMESPACE_PREFIX_MAPPER;

}