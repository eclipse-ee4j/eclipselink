/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * These are properties that may be set on an instance of Unmarshaller.  Below is
 * an example of using the property mechanism to enable MOXy's JSON binding for 
 * an instance of Unmarshaller.
 * <pre>
 * Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 * unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
 * </pre>
 */
public class UnmarshallerProperties {

    /**
     * The Constant JSON_NAMESPACE_PREFIX_MAPPER. Provides a means to set a   
     * a Map<String, String> of uris to prefixes.  Alternatively can be an implementation 
     * of org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.4
     */
    public static final String JSON_NAMESPACE_PREFIX_MAPPER = JAXBContextProperties.NAMESPACE_PREFIX_MAPPER;

    /**
     * The Constant MEDIA_TYPE. This can be used to set the media type.  
     * Supported values are "application/xml" and "application/json".
     * @since 2.4
     */
    public static final String MEDIA_TYPE = JAXBContextProperties.MEDIA_TYPE;

    /**
     * The Constant ID_RESOLVER.  This can be used to specify a custom
     * IDResolver class, to allow customization of ID/IDREF processing.
     * @since 2.3.3
     */
    public static final String ID_RESOLVER = "eclipselink.id-resolver";

    /**
     * The Constant JSON_ATTRIBUTE_PREFIX. This can be used to specify a prefix that 
     * is prepended to attributes.  Only applicable if media type is "application/json".
     * @since 2.4
     */ 
    public static final String JSON_ATTRIBUTE_PREFIX = JAXBContextProperties.JSON_ATTRIBUTE_PREFIX;

    /**
     * The Constant JSON_INCLUDE_ROOT. This can be used  to specify if the root element 
     * should be unmarshalled.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_INCLUDE_ROOT = JAXBContextProperties.JSON_INCLUDE_ROOT;

    /**
     * The Constant JSON_VALUE_WRAPPER.  This can be used to specify the wrapper
     * that will be used around things mapped with @XmlValue.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_VALUE_WRAPPER = JAXBContextProperties.JSON_VALUE_WRAPPER;

    /**
     * The Constant JSON_NAMESPACE_SEPARATOR.  This can be used to specify the separator
     * that will be used when separating prefixes and localnames.  Only applicable when
     * namespaces are being used. Value should be a Character.
     * @since 2.4
     */
    public static final String JSON_NAMESPACE_SEPARATOR  = "eclipselink.json.namespace-separator";

}