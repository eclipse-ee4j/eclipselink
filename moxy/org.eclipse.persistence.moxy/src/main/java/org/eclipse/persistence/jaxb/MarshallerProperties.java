/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
//     Marcel Valovy  - 2.6   - added case insensitive unmarshalling property
//                            - added bean validation properties
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
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_ATTRIBUTE_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_ATTRIBUTE_PREFIX
     */
    public static final String JSON_ATTRIBUTE_PREFIX = JAXBContextProperties.JSON_ATTRIBUTE_PREFIX;

    /**
     * The name of the property used to specify in the root node should be
     * included in the message (default is true). There is no effect when media
     * type is "application/xml".  When this property is specified at the
     * <i>JAXBContext</i> level all instances of <i>Marshaller</i> and
     * <i>Unmarshaller</i> will default to this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_INCLUDE_ROOT
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_INCLUDE_ROOT
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
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_NAMESPACE_SEPARATOR
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_NAMESPACE_SEPARATOR
     */
    public static final String JSON_NAMESPACE_SEPARATOR  = JAXBContextProperties.JSON_NAMESPACE_SEPARATOR;

    /**
     * The name of the property used to specify the key that will correspond to
     * the property mapped with <i>@XmlValue</i>.  This key will only be used if
     * there are other mapped properties.  When this property is specified at
     * the <i>JAXBContext</i> level all instances of <i>Marshaller</i> and
     * <i>Unmarshaller</i> will default to this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_VALUE_WRAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_VALUE_WRAPPER
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
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#MEDIA_TYPE
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#MEDIA_TYPE
     * @see org.eclipse.persistence.oxm.MediaType
     */
    public static final String MEDIA_TYPE = JAXBContextProperties.MEDIA_TYPE;

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the
     * namespace prefixes used while marshalling to XML.  Used for both marshal
     * and unmarshal when mediaType is set to "application/json". Value is
     * either a {@literal Map} of URIs to prefixes, or an
     * implementation of org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.3.3
     * @see org.eclipse.persistence.oxm.NamespacePrefixMapper
     */
    public static final String NAMESPACE_PREFIX_MAPPER = JAXBContextProperties.NAMESPACE_PREFIX_MAPPER;

    /**
     * The Constant JSON_MARSHAL_EMPTY_COLLECTIONS.  If true an empty or null
     * collection will be marshalled as null or empty array, if false both will be
     * represented as an absent node.
     * @since 2.4
     */
    public static final String JSON_MARSHAL_EMPTY_COLLECTIONS = "eclipselink.json.marshal-empty-collections";

    /**
     * The Constant JSON_REDUCE_ANY_ARRAYS.  If true arrays that have just one item in them
     * will be reduced and marshalled as a single item and not as a collection.  ie: no [ ] in the marshalled JSON
     * Default is false so all collections (even size 1) will have the [ ] around them.
     * @since 2.4.2
     */
    public static final String JSON_REDUCE_ANY_ARRAYS = "eclipselink.json.reduce-any-arrays";

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     *
     * @since 2.6.0
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_USE_XSD_TYPES_WITH_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_USE_XSD_TYPES_WITH_PREFIX
     */
    public static final String JSON_USE_XSD_TYPES_WITH_PREFIX = JAXBContextProperties.JSON_USE_XSD_TYPES_WITH_PREFIX;

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     * @since 2.6.0
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_TYPE_COMPATIBILITY
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_TYPE_COMPATIBILITY
     */
    public static final String JSON_TYPE_COMPATIBILITY = JAXBContextProperties.JSON_TYPE_COMPATIBILITY;

    /**
     * Override default type property name for JSON as MOXy type discriminator. Settings from binding file have higher priority.
     *
     * <p><b>Example</b></p>
     * <p>Given the following property</p>
     * <pre>
     * marshaller.setProperty(MarshallerProperties.JSON_TYPE_ATTRIBUTE_NAME, "mytype");
     * </pre>
     * <p>If the property is set the JSON output will be:</p>
     * <pre>
     * ...
     * {
     *      "mytype": "phone",
     *      "contactId": 3,
     *      "number": "987654321"
     * }
     * ...
     * </pre>
     * <p>for following object model</p>
     * <pre>
     * &#64;XmlSeeAlso({Address.class, Phone.class})
     * public class Contact {
     *
     *      public int contactId;
     *      ...
     * </pre>
     * <pre>
     * public class Phone extends Contact{
     *
     *      public String number;
     *      ...
     * </pre>
     * <p>If the property is not set (default value) the JSON output will be:</p>
     * <pre>
     * ...
     * {
     *      "type": "phone",
     *      "contactId": 3,
     *      "number": "987654321"
     * }
     * ...
     * </pre>
     * Unmarshaller will use it as type discriminator to select right child class.
     * @since 2.7.4
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_TYPE_ATTRIBUTE_NAME
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_TYPE_ATTRIBUTE_NAME
     */
    public static final String JSON_TYPE_ATTRIBUTE_NAME = JAXBContextProperties.JSON_TYPE_ATTRIBUTE_NAME;

    /**
     * Disable generation of "item" name in nested arrays in JSON. Settings from binding file have higher priority.
     *
     * <p><b>Example</b></p>
     * <p>Given the following property</p>
     * <pre>
     * marshaller.setProperty(MarshallerProperties.JSON_DISABLE_NESTED_ARRAY_NAME, "false");
     * </pre>
     * <p>If the property value is set to <b>false</b> (default) the JSON output will be:</p>
     * <pre>
     * ...
     * "char2dArray":[
     *      {"item":["a","b","c","d"]},
     *      {"item":["e","f","g","h"]}
     * ]
     * ...
     * </pre>
     * <p>for following object model</p>
     * <pre>
     * ...
     * &#64;XmlElement(name="char2dArray")
     * private char[][] char2dArray;
     * ...
     * </pre>
     * <p>If the property value is set to <b>true</b> (default) the JSON output will be:</p>
     * <pre>
     * ...
     * "char2dArray": [
     *      ["a","b","c","d"],
     *      ["e","f","g","h"]
     * ]
     * ...
     * </pre>
     * @since 2.7.5
     */
    public static final String JSON_DISABLE_NESTED_ARRAY_NAME = "eclipselink.json.disable-nested-array-name";

    /**
     *
     */
    public static final String OBJECT_GRAPH = JAXBContextProperties.OBJECT_GRAPH;

    /**
     * The Constant JSON_WRAPPER_AS_ARRAY_NAME. If true the grouping
     * element will be used as the JSON key. There is no effect when media type
     * is "application/xml".  When this property is specified at the
     * <i>JAXBContext</i> level all instances of <i>Marshaller</i> and
     * <i>Unmarshaller</i> will default to this.
     *
     * <p><b>Example</b></p>
     * <p>Given the following class:</p>
     * <pre>
     * &#64;XmlAccessorType(XmlAccessType.FIELD)
     * public class Customer {
     *
     *     &#64;XmlElementWrapper(name="phone-numbers")
     *     &#64;XmlElement(name="phone-number")
     *     private {@literal List<PhoneNumber>} phoneNumbers;
     *
     * }
     * </pre>
     * <p>If the property is set to false (the default) the JSON output will be:</p>
     * <pre>
     * {
     *     "phone-numbers" : {
     *         "phone-number" : [ {
     *             ...
     *         }, {
     *             ...
     *         }]
     *     }
     * }
     * </pre>
     * <p>And if the property is set to true, then the JSON output will be:</p>
     * <pre>
     * {
     *     "phone-numbers" : [ {
     *         ...
     *     }, {
     *         ...
     *     }]
     * }
     * </pre>
     * @since 2.4.2
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_WRAPPER_AS_ARRAY_NAME
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#JSON_WRAPPER_AS_ARRAY_NAME
     */
    public static final String JSON_WRAPPER_AS_ARRAY_NAME = JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME;

    /**
     * Property for setting bean validation mode.
     * Valid values {@link BeanValidationMode#AUTO} (default),{@link BeanValidationMode#CALLBACK}, {@link BeanValidationMode#NONE}.
     *
     * @since 2.6
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#BEAN_VALIDATION_MODE
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#BEAN_VALIDATION_MODE
     */
    public static final String BEAN_VALIDATION_MODE = JAXBContextProperties.BEAN_VALIDATION_MODE;

    /**
     * Property for setting preferred or custom validator factory.
     * Mapped value must be instance of {@link javax.validation.ValidatorFactory}.
     *
     * @since 2.6
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#BEAN_VALIDATION_FACTORY
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#BEAN_VALIDATION_FACTORY
     */
    public static final String BEAN_VALIDATION_FACTORY = JAXBContextProperties.BEAN_VALIDATION_FACTORY;

    /**
     * Property for setting bean validation target groups.
     * Mapped value must be of type {@literal Class[]}.
     *
     * @since 2.6
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#BEAN_VALIDATION_GROUPS
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#BEAN_VALIDATION_GROUPS
     */
    public static final String BEAN_VALIDATION_GROUPS = JAXBContextProperties.BEAN_VALIDATION_GROUPS;

    /**
     * Property for disabling Bean Validation optimisations.
     * Bean Validation in MOXy features optimisations, which are used to skip BV processes on non-constrained objects.
     *
     * This is to make maintenance easier and to allow for debugging in case that some object is not validated,
     * but should be.
     *
     * Usage: set to {@link Boolean#TRUE} to disable optimisations, set to {@link Boolean#FALSE} to re-enable them
     * again.
     *
     * @since 2.6
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#BEAN_VALIDATION_NO_OPTIMISATION
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#BEAN_VALIDATION_NO_OPTIMISATION
     */
    public static final String BEAN_VALIDATION_NO_OPTIMISATION = JAXBContextProperties.BEAN_VALIDATION_NO_OPTIMISATION;

    /**
     * Property for MOXy logging level.
     *
     * This is to make maintenance easier and to allow MOXy generate more diagnostic log messages.
     *
     * Allowed values are specified in {@link org.eclipse.persistence.logging.LogLevel}
     * Default value is {@link org.eclipse.persistence.logging.LogLevel#INFO}
     *
     * @since 3.0
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#MOXY_LOGGING_LEVEL
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#MOXY_LOGGING_LEVEL
     * @see org.eclipse.persistence.logging.LogLevel
     */
    public static final String MOXY_LOGGING_LEVEL = "eclipselink.logging.level.moxy";

    /**
     * Property for logging Entities content during marshalling/unmarshalling operation in MOXy.
     * It calls toString() method from entity.
     *
     * This is to make maintenance easier and to allow for debugging to check marshalled/unmarshalled content.
     * Use it carefully. It can produce high amount of data in the log files.
     *
     * Usage: set to {@link Boolean#TRUE} to enable payload logging, set to {@link Boolean#FALSE} to disable it.
     * It can be set via system property with name "eclipselink.logging.payload.moxy" too.
     * By default it is disabled.
     *
     * @since 3.0
     * @see org.eclipse.persistence.jaxb.JAXBContextProperties#MOXY_LOG_PAYLOAD
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties#MOXY_LOG_PAYLOAD
     */
    public static final String MOXY_LOG_PAYLOAD = JAXBContextProperties.MOXY_LOG_PAYLOAD;
}
