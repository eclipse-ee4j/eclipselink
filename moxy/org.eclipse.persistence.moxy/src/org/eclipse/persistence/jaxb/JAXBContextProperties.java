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
     * The Constant JSON_ATTRIBUTE_PREFIX. This can be used as the property name with 
     * JAXBMarshaller.setProperty and JAXBUnmarshaller.setProperty or used in 
     * the properties supplied during JAXBContext creation to specify a prefix to prepend
     * to attributes.  No effect when media type is "application/xml".
     * @since 2.4
     */ 
    public static final String JSON_ATTRIBUTE_PREFIX = "eclipselink.json.attribute-prefix";

    /**
     * The Constant JSON_INCLUDE_ROOT. This can be used as the property name with 
     * JAXBMarshaller.setProperty and JAXBUnmarshaller.setProperty or used in 
     * the properties supplied during JAXBContext creation to specify if the 
     * @XmlRootElement should be marshalled/unmarshalled.  Not applicable if 
     * eclipselink.media-type is set to "application/xml".
     * @since 2.4 
     */
    public static final String JSON_INCLUDE_ROOT = "eclipselink.json.include-root";

    /**
     * The Constant JSON_NAMESPACE_SEPARATOR.  This can be used to specify the separator
     * that will be used when separating prefixes and localnames.  Only applicable when
     * namespaces are being used. Value should be a Character.
     * @since 2.4
     */
    public static final String JSON_NAMESPACE_SEPARATOR  = "eclipselink.json.namespace-separator";

    /**
     * The Constant JSON_VALUE_WRAPPER.  This can be used to specify the wrapper
     * that will be used around things mapped with @XmlValue.  Not applicable if the
     * eclipselink.media.type is set to "application/xml".
     * @since 2.4
     */
    public static final String JSON_VALUE_WRAPPER = "eclipselink.json.value-wrapper";

    /**
     * The Constant MEDIA_TYPE. This can be used as the property name with 
     * JAXBMarshaller.setProperty and JAXBUnmarshaller.setProperty or used in 
     * the properties supplied during JAXBContext creation to set the media type. 
     * Supported values are "application/xml" and "application/json".
     * @since 2.4
     */
    public static final String MEDIA_TYPE = "eclipselink.media-type";

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the namespace prefixes used 
     * while marshalling to XML.  Used for both marshal and unmarshal when mediaType is set to "application/json".
     * Value is either a Map<String, String> of URIs to prefixes, or an implementation of 
     * org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.4
     */
    public static final String NAMESPACE_PREFIX_MAPPER = "eclipselink.namespace-prefix-mapper";

    /**
     * The Constant SESSION_EVENT_LISTENER.  This can be used to specify a
     * SessionEventListener that can be used to customize the metadata before or
     * after it has been initialized.
     * @see org.eclipse.persistence.sessions.SessionEventListener
     * @since 2.4
     */
    public static final String SESSION_EVENT_LISTENER = "eclipselink.session-event-listener";

}