/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

/**
 * These are properties that may be passed in to create a JAXBContext:
 * <pre>
 * Map&lt;String, Object> properties = new HashMap<String, Object>(1);
 * properties.put();
 * JAXBContext jc = JAXBContext.newInstance(new Class[] {Foo.class}, properties);
 * </pre>
 * @since 2.4.0
 */
public class JAXBContextProperties {

    /**
     * The name of the property used to specify a value that will be prepended 
     * to all keys that are mapped to an XML attribute. By default there is no 
     * attribute prefix.  There is no effect when media type is 
     * "application/xml".  When this property is specified at the
     * <i>JAXBContext</i> level all instances of <i>Marshaller</i> and 
     * <i>Unmarshaller</i> will default to this attribute prefix.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.JSON_ATTRIBUTE_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX
     */ 
    public static final String JSON_ATTRIBUTE_PREFIX = "eclipselink.json.attribute-prefix";

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
    public static final String JSON_INCLUDE_ROOT = "eclipselink.json.include-root";

    /**
     * The name of the property used to specify the character (default is '.')
     * that separates the prefix from the key name. It is only used if namespace
     * qualification has been enabled be setting a namespace prefix mapper.  
     * When this property is specified at the <i>JAXBContext</i> level all 
     * instances of <i>Marshaller</i> and <i>Unmarshaller</i> will default to 
     * this setting.
     * @since 2.4
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.NAMESPACE_SEPARATOR
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
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_VALUE_WRAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_VALUE_WRAPPER
     */
    public static final String JSON_VALUE_WRAPPER = "eclipselink.json.value-wrapper";

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
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.MEDIA_TYPE
     * @see org.eclipse.persistence.oxm.MediaType
     */
    public static final String MEDIA_TYPE = "eclipselink.media-type";

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the
     * namespace prefixes used  while marshalling to XML.  Used for both marshal
     * and unmarshal when mediaType is set to "application/json".  Value is
     * either a Map<String, String> of URIs to prefixes, or an implementation of 
     * org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.4
     * @see org.eclipse.persistence.oxm.NamespacePrefixMapper
     */
    public static final String NAMESPACE_PREFIX_MAPPER = "eclipselink.namespace-prefix-mapper";

    /**
     * The name of the property used to specify a SessionEventListener that can
     * be used to customize the metadata before or after it has been 
     * initialized.
     * @see org.eclipse.persistence.sessions.SessionEventListener
     * @since 2.4
     */
    public static final String SESSION_EVENT_LISTENER = "eclipselink.session-event-listener";

    /**
     * The name of the property used to specify one or more EclipseLink OXM 
     * metadata sources.
     * 
     * The metadata source can be one of the following:
     * <ul>
     * <li>java.io.File
     * <li>java.io.InputStream
     * <li>java.io.Reader
     * <li>java.lang.String
     * <li>java.net.URL
     * <li>javax.xml.stream.XMLEventReader
     * <li>javax.xml.stream.XMLStreamReader
     * <li>javax.xml.transform.Source
     * <li>org.eclipse.persistence.jaxb.metadata.MetadataSource
     * <li>org.w3c.dom.Node
     * <li>org.xml.sax.InputSource
     * </ul>
     * 
     * To specify multiple metadata sources, the following can be used:
     * <ul>
     * <li>java.util.Map<String, Object>
     * <li>java.util.List<Object>
     * </ul>
     * 
     * @since 2.4, replaces JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY
     */
    public static final String OXM_METADATA_SOURCE = "eclipselink.oxm.metadata-source";

    /**
     * The name of the property used to specify a default target namespace.
     * 
     * @since 2.4, replaces JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY
     */
    public static final String DEFAULT_TARGET_NAMESPACE = "eclipselink.default-target-namespace";
    
    /**
     * The name of the property used to specify an AnnotationHelper instance.  
     * An AnnotationHelper is responsible for returning Annotations from 
     * AnnotatedElements.
     * @see org.eclipse.persistence.jaxb.javamodel.reflection.AnnotationHelper
     * @see java.lang.annotation.Annotation
     * @see java.lang.reflect.AnnotatedElement
     * 
     * @since 2.4, replaces JAXBContextFactory.ANNOTATION_HELPER_KEY
     */
    public static final String ANNOTATION_HELPER = "eclipselink.annotation-helper";

    /**
     * The name of the boolean property used to enable custom XmlAccessorFactories.
     * 
     * @see com.sun.xml.bind.XmlAccessorFactory
     *
     * @since 2.4.2
     */
    public static final String XML_ACCESSOR_FACTORY_SUPPORT = "eclipselink.xml-accessor-factory.support";
    
    /**
     * The name of the property used to specify an ObjectGraph instance or name of an 
     * ObjectGraph to be used on Unmarshallers and Marshallers created by the context. 
     */
    public static final String OBJECT_GRAPH = "eclipselink.object-graph";

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
     *     private List<PhoneNumber> phoneNumbers;
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
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME
     */
    public static final String JSON_WRAPPER_AS_ARRAY_NAME = "eclipselink.json.wrapper-as-array-name";

}