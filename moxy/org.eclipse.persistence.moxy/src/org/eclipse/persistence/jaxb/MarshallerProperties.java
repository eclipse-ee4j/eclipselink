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
     * The Constant CHARACTER_ESCAPE_HANDLER.  Allows for customization of 
     * character escaping when marshalling.  Value should be an implementation 
     * of org.eclipse.persistence.oxm.CharacterEscapeHandler.
     * @since 2.3.3
     * @see org.eclipse.persistence.oxm.CharacterEscapeHandler
     */
    public static final String CHARACTER_ESCAPE_HANDLER = "eclipselink.character-escape-handler";

    /**
     * The Constant INDENT_STRING. Property used to set the string used when 
     * indenting formatted marshalled documents. The default for formatted 
     * documents is &quot;   &quot; (three spaces).
     * @since 2.3.3
     */
    public static final String INDENT_STRING = "eclipselink.indent-string";

    /**
     * The name of the property used to specify a value that will be prepended 
     * to all keys that are mapped to an XML attribute. By default there is no 
     * attribute prefix.  There is no effect when media type is 
     * "application/xml".  When this property is specified at the
     * <i>JAXBContext</i> level all instances of <i>Marshaller</i> and 
     * <i>Unmarshaller</i> will default to this attribute prefix.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties.JSON_ATTRIBUTE_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX
     */
    public static final String JSON_ATTRIBUTE_PREFIX = JAXBContextProperties.JSON_ATTRIBUTE_PREFIX;

    /**
     * The name of the property used to specify in the root node should be
     * included in the message (default is true). There is no effect when media
     * type is "application/xml".  When this property is specified at the
     * <i>JAXBContext</i> level all instances of <i>Marshaller</i> and 
     * <i>Unmarshaller</i> will default to this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties.JSON_INCLUDE_ROOT
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_INCLUDE_ROOT
     */
    public static final String JSON_INCLUDE_ROOT = JAXBContextProperties.JSON_INCLUDE_ROOT;

    /**
     * The name of the property used to specify the character (default is '.')
     * that separates the prefix from the key name. It is only used if namespace
     * qualification has been enabled be setting a namespace prefix mapper.  
     * When this property is specified at the <i>JAXBContext</i> level all 
     * instances of <i>Marshaller</i> and <i>Unmarshaller</i> will default to 
     * this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties.NAMESPACE_SEPARATOR
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.NAMESPACE_SEPARATOR
     */
    public static final String JSON_NAMESPACE_SEPARATOR  = "eclipselink.json.namespace-separator";

    /**
     * The name of the property used to specify the key that will correspond to
     * the property mapped with <i>@XmlValue</i>.  This key will only be used if
     * there are other mapped properties.  When this property is specified at 
     * the <i>JAXBContext</i> level all instances of <i>Marshaller</i> and 
     * <i>Unmarshaller</i> will default to this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextPropertes.JSON_VALUE_WRAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_VALUE_WRAPPER
     */
    public static final String JSON_VALUE_WRAPPER = JAXBContextProperties.JSON_VALUE_WRAPPER;

    /**
     * The name of the property used to specify the type of binding to be 
     * performed.  When this property is specified at the <i>JAXBContext</i>
     * level all instances of <i>Marshaller</i> and <i>Unmarshaller</i> will
     * default to this media type. Supported values are:
     * <ul>
     * <li>MediaType.APPLICATION_XML (default)
     * <li>MediaType.APPLICATION_JSON
     * <li>"application/xml"
     * <li>"application/json"
     * </ul>
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties.MEDIA_TYPE
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.MEDIA_TYPE
     * @see org.eclipse.persistence.oxm.MediaType
     */
    public static final String MEDIA_TYPE = JAXBContextProperties.MEDIA_TYPE;

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the
     * namespace prefixes used while marshalling to XML.  Used for both marshal
     * and unmarshal when mediaType is set to "application/json". Value is 
     * either a Map<String, String> of URIs to prefixes, or an implementation of 
     * org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.3.3
     * @see org.eclipse.persistence.oxm.NamespacePrefixMapper 
     */
    public static final String NAMESPACE_PREFIX_MAPPER = JAXBContextProperties.NAMESPACE_PREFIX_MAPPER;

    /**
     * The Constant JSON_MARSHAL_EMPTY_COLLECTIONS.  If true an empty collection
     * will be marshalled as an empty array, if false it will be represented as
     * an absent node.
     * @since 2.4
     */
    public static final String JSON_MARSHAL_EMPTY_COLLECTIONS = "eclipselink.json.marshal-empty-collections";

}