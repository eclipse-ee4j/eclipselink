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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.rs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;

/**
 * <p>This is an implementation of <i>MessageBodyReader</i>/<i>MessageBodyWriter
 * </i> that can be used to enable EclipseLink JAXB (MOXy) as the the JSON 
 * provider.</p>
 * <p>
 * <b>Supported Media Type Patterns</b>
 * <ul>
 * <li>*&#47;json (i.e. application/json and text/json)</li>
 * <li>*&#47;*+json</li>
 * </ul>
 * </p>
 * 
 * <p>Below are some different usage options.</p>
 * 
 * <b>Option #1 - <i>MOXyJsonProvider</i> Default Behavior</b>
 * <p>You can use the <i>Application</i> class to specify that 
 * <i>MOXyJsonProvider</i> should be used with your JAX-RS application.</p>
 * <pre>
 * package org.example;

 * import java.util.*;
 * import javax.ws.rs.core.Application;
 * import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
 *
 * public class ExampleApplication  extends Application {
 *
 *     &#64;Override
 *     public Set&lt;Class&lt;?>> getClasses() {
 *         HashSet&lt;Class&lt;?>> set = new HashSet&lt;Class&lt;?>>(2);
 *         set.add(MOXyJsonProvider.class);
 *         set.add(ExampleService.class);
 *         return set;
 *     }
 *
 * }
 * </pre>
 * 
 * <b>Option #2 - Customize <i>MOXyJsonProvider</i></b>
 * <p>You can use the <i>Application</i> class to specify a configured instance 
 * of <i>MOXyJsonProvider</i> should be used with your JAX-RS application.</p>
 * <pre>
 * package org.example;
 *
 * import java.util.*;
 * import javax.ws.rs.core.Application;
 * import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
 *
 * public class CustomerApplication  extends Application {
 *
 *     &#64;Override
 *     public Set&lt;Class&lt;?>> getClasses() {
 *         HashSet&lt;Class&lt;?>> set = new HashSet&lt;Class&lt;?>>(1);
 *         set.add(ExampleService.class);
 *         return set;
 *     }

 *     &#64;Override
 *     public Set&lt;Object> getSingletons() {
 *         moxyJsonProvider moxyJsonProvider = new MOXyJsonProvider();
 *         moxyJsonProvider.setFormattedOutput(true);
 *         moxyJsonProvider.setIncludeRoot(true);
 *
 *         HashSet&lt;Object> set = new HashSet&lt;Object>(2);
 *         set.add(moxyJsonProvider);
 *         return set;
 *     }
 *
 * } 
 * </pre>
 * <b>Option #3 - Extend MOXyJsonProvider</b>
 * <p>You can use MOXyJsonProvider for creating your own 
 * <i>MessageBodyReader</i>/<i>MessageBodyWriter</i>.</p>
 * <pre>
 * package org.example;
 *
 * import java.lang.annotation.Annotation;
 * import java.lang.reflect.Type;
 *
 * import javax.ws.rs.*;
 * import javax.ws.rs.core.*;
 * import javax.ws.rs.ext.Provider;
 * import javax.xml.bind.*;
 *
 * import org.eclipse.persistence.jaxb.MarshallerProperties;
 * import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
 *
 * &#64;Provider
 * &#64;Produces(MediaType.APPLICATION_JSON)
 * &#64;Consumes(MediaType.APPLICATION_JSON)
 * public class CustomerJSONProvider extends MOXyJsonProvider {

 *     &#64;Override
 *     public boolean isReadable(Class&lt;?> type, Type genericType,
 *             Annotation[] annotations, MediaType mediaType) {
 *         return getDomainClass(genericType) == Customer.class;
 *     }
 *
 *     &#64;Override
 *     public boolean isWriteable(Class&lt;?> type, Type genericType,
 *             Annotation[] annotations, MediaType mediaType) {
 *         return isReadable(type, genericType, annotations, mediaType);
 *     }
 *
 *     &#64;Override
 *     protected void preReadFrom(Class&lt;Object> type, Type genericType,
 *             Annotation[] annotations, MediaType mediaType,
 *             MultivaluedMap<String, String> httpHeaders,
 *             Unmarshaller unmarshaller) throws JAXBException {
 *         unmarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "$");
 *     }
 *
 *     &#64;Override
 *     protected void preWriteTo(Object object, Class&lt;?> type, Type genericType,
 *             Annotation[] annotations, MediaType mediaType,
 *             MultivaluedMap&lt;String, Object> httpHeaders, Marshaller marshaller)
 *             throws JAXBException {
 *         marshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "$");
 *     }
 *
 * }
 * </pre>
 * @since 2.4
 */
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
public class MOXyJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>{
 
    private static final String CHARSET = "charset";
    private static final String JSON = "json";
    private static final String PLUS_JSON = "+json";

    @Context
    protected Providers providers;

    private String attributePrefix = null;
    private boolean formattedOutput = false;
    private boolean includeRoot = false;
    private boolean marshalEmptyCollections = true;
    private Map<String, String> namespacePrefixMapper;
    private char namespaceSeperator = XMLConstants.DOT;
    private String valueWrapper;


    /**
     * The value that will be prepended to all keys that are mapped to an XML
     * attribute.  By default there is no attribute prefix.
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_ATTRIBUTE_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_ATTRIBUTE_PREFIX
     */
    public String getAttributePrefix() {
        return attributePrefix;
    }

    /**
     * A convenience method to get the domain class (i.e. <i>Customer</i>) from 
     * the parameter/return type (i.e. <i>Customer</i> or <i>List&lt;Customer></i>).
     * @param genericType - The parameter/return type of the JAX-RS operation.
     * @return The corresponding domain class.
     */
    protected Class<?> getDomainClass(Type genericType) {
        if(genericType instanceof Class) {
            return (Class<?>) genericType;
        } else if(genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            return null;
        }
    }

    /**
     * Return the <i>JAXBContext</i> that corresponds to the domain class.  This
     * method does the following:
     * <ol>
     * <li>If an EclipseLink JAXB (MOXy) <i>JAXBContext</i> is available from
     * a <i>ContextResolver</i> then use it.</li>
     * <li>If an existing <i>JAXBContext</i> was not found in step one, then 
     * create a new one on the domain class.</li>
     * </ol>
     * @param domainClass - The domain class we need a <i>JAXBContext</i> for.
     * @param annotations - The annotations corresponding to domain object.
     * @param mediaType - The media type for the HTTP entity.
     * @param httpHeaders - HTTP headers associated with HTTP entity.
     * @return
     * @throws JAXBException
     */
    protected JAXBContext getJAXBContext(Class<?> domainClass, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, ?> httpHeaders) throws JAXBException {
        ContextResolver<JAXBContext> resolver = providers.getContextResolver(JAXBContext.class, mediaType);
        JAXBContext jaxbContext;
        if(null == resolver || null == (jaxbContext = resolver.getContext(domainClass))) {
            return JAXBContextFactory.createContext(new Class[] {domainClass}, null); 
        } else if (jaxbContext instanceof org.eclipse.persistence.jaxb.JAXBContext) {
            return jaxbContext;
        } else {
            return JAXBContextFactory.createContext(new Class[] {domainClass}, null); 
        }
    }

    /**
     * By default the JSON-binding will ignore namespace qualification. If this 
     * property is set the portion of the key before the namespace separator
     * will be used to determine the namespace URI.
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.NAMESPACE_PREFIX_MAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER
     */
    public Map<String, String> getNamespacePrefixMapper() {
        return namespacePrefixMapper;
    }

    /**
     * This character (default is '.') separates the prefix from the key name.
     * It is only used if namespace qualification has been enabled be setting a
     * namespace prefix mapper.
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.NAMESPACE_SEPARATOR
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.NAMESPACE_SEPARATOR
     */
    public char getNamespaceSeparator() {
        return this.namespaceSeperator;
    }

    /*
     * @return -1 since the size of the JSON message is not known.
     * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * The key that will correspond to the property mapped with @XmlValue.  This
     * key will only be used if there are other mapped properties.
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_VALUE_WRAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_VALUE_WRAPPER
     */
    public String getValueWrapper() {
        return valueWrapper;
    }

    /**
     * @return true if the JSON output should be formatted (default is false).
     */
    public boolean isFormattedOutput() {
        return formattedOutput;
    }

    /**
     * @return true if the root node is included in the JSON message (default is
     * false).
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_INCLUDE_ROOT
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_INCLUDE_ROOT
     */
    public boolean isIncludeRoot() {
        return includeRoot;
    }

    /**
     * If true empty collections will be marshalled as empty arrays, else the 
     * collection will not be marshalled to JSON (default is true).
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS
     */
    public boolean isMarshalEmptyCollections() {
        return marshalEmptyCollections;
    }

    /**
     * This method will return true for all inputs.  This means that 
     * <i>MOXyJsonProvider</i> will always be used for the JSON binding.
     * @return true
     */
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return supportsMediaType(mediaType);
    }
 
    /**
     * This method will return true for all inputs.  This means that 
     * <i>MOXyJsonProvider</i> will always be used for the JSON binding.
     * @return true
     */
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return supportsMediaType(mediaType);
    }

    /**
     * Subclasses of <i>MOXyJsonProvider</i> can override this method to 
     * customize the instance of <i>Unmarshaller</i> that will be used to 
     * unmarshal the JSON message in the readFrom call.
     * @param type - The Class to be unmarshalled (i.e. <i>Customer</i> or 
     * <i>List</i>)
     * @param genericType - The type of object to be unmarshalled (i.e 
     * <i>Customer</i> or <i>List&lt;Customer></i>).
     * @param annotations - The annotations corresponding to domain object.
     * @param mediaType - The media type for the HTTP entity.
     * @param httpHeaders - HTTP headers associated with HTTP entity.
     * @param unmarshaller - The instance of <i>Unmarshaller</i> that will be 
     * used to unmarshal the JSON message.
     * @throws JAXBException
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties
     */
    protected void preReadFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, Unmarshaller unmarshaller) throws JAXBException {
    }

    /**
     * Subclasses of <i>MOXyJsonProvider</i> can override this method to 
     * customize the instance of <i>Marshaller</i> that will be used to marshal 
     * the domain objects to JSON in the writeTo call.
     * @param object - The domain object that will be marshalled to JSON.
     * @param type - The Class to be marshalled (i.e. <i>Customer</i> or 
     * <i>List</i>)
     * @param genericType - The type of object to be marshalled (i.e 
     * <i>Customer</i> or <i>List&lt;Customer></i>).
     * @param annotations - The annotations corresponding to domain object.
     * @param mediaType - The media type for the HTTP entity.
     * @param httpHeaders - HTTP headers associated with HTTP entity.
     * @param marshaller - The instance of <i>Marshaller</i> that will be used 
     * to marshal the domain object to JSON.
     * @throws JAXBException
     * @see org.eclipse.persistence.jaxb.MarshallerProperties
     */
    protected void preWriteTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, Marshaller marshaller) throws JAXBException {
    }

    /*
     * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
     */
    public final Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            Class<?> domainClass = getDomainClass(genericType);
            JAXBContext jaxbContext = getJAXBContext(domainClass, annotations, mediaType, httpHeaders);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, attributePrefix);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, includeRoot);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespacePrefixMapper);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, namespaceSeperator);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, valueWrapper);
            preReadFrom(type, genericType, annotations, mediaType, httpHeaders, unmarshaller);

            StreamSource jsonSource;
            Map<String, String> mediaTypeParameters = mediaType.getParameters();
            if(mediaTypeParameters.containsKey(CHARSET)) {
                String charSet = mediaTypeParameters.get(CHARSET);
                Reader entityReader = new InputStreamReader(entityStream, charSet);
                jsonSource = new StreamSource(entityReader);
            } else {
                jsonSource = new StreamSource(entityStream);
            }

            return unmarshaller.unmarshal(jsonSource, domainClass).getValue();
        } catch(JAXBException jaxbException) {
            throw new WebApplicationException(jaxbException);
        }
    }

    /**
     * Specify a value that will be prepended to all keys that are mapped to an
     * XML attribute.  By default there is no attribute prefix.
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_ATTRIBUTE_PREFIX
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_ATTRIBUTE_PREFIX
     */
    public void setAttributePrefix(String attributePrefix) {
        this.attributePrefix = attributePrefix;
    }

    /**
     * Specify if the JSON output should be formatted (default is false).
     * @param formattedOutput - true if the output should be formatted, else 
     * false.
     */
    public void setFormattedOutput(boolean formattedOutput) {
        this.formattedOutput = formattedOutput;
    }

    /**
     * Specify if the root node should be included in the JSON message (default
     * is false).
     * @param includeRoot - true if the message includes the root node, else 
     * false.
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_INCLUDE_ROOT
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_INCLUDE_ROOT
     */
    public void setIncludeRoot(boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    /**
     * If true empty collections will be marshalled as empty arrays, else the 
     * collection will not be marshalled to JSON (default is true).
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS
     */
    public void setMarshalEmptyCollections(boolean marshalEmptyCollections) {
        this.marshalEmptyCollections = marshalEmptyCollections;
    }

   /**
     * By default the JSON-binding will ignore namespace qualification. If this
     * property is set then a prefix corresponding to the namespace URI and a 
     * namespace separator will be prefixed to the key.
     * include it you can specify a Map of namespace URI to prefix.
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.NAMESPACE_PREFIX_MAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER
     */
    public void setNamespacePrefixMapper(Map<String, String> namespacePrefixMapper) {
        this.namespacePrefixMapper = namespacePrefixMapper;
    }

    /**
     * This character (default is '.') separates the prefix from the key name.
     * It is only used if namespace qualification has been enabled be setting a
     * namespace prefix mapper.
     * @see org.eclipse.persistence.jaxb.MarshallerProperties.NAMESPACE_SEPARATOR
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties.NAMESPACE_SEPARATOR
     */
    public void setNamespaceSeparator(char namespaceSeparator) {
        this.namespaceSeperator = namespaceSeparator;
    }

    /**
     * Specify the key that will correspond to the property mapped with
     * @XmlValue.  This key will only be used if there are other mapped
     * properties.
     * @see org.eclipse.persistence.jaxb.MarshallerPropertes.JSON_VALUE_WRAPPER
     * @see org.eclipse.persistence.jaxb.UnmarshallerPropertes.JSON_VALUE_WRAPPER
     */
    public void setValueWrapper(String valueWrapper) {
        this.valueWrapper = valueWrapper;
    }

    private boolean supportsMediaType(MediaType mediaType) {
        String subtype = mediaType.getSubtype();
        return subtype.equals(JSON) || subtype.endsWith(PLUS_JSON);
    }

    /*
     * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
     */
    public final void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            Class<?> domainClass = getDomainClass(genericType);
            JAXBContext jaxbContext = getJAXBContext(domainClass, annotations, mediaType, httpHeaders);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            marshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, attributePrefix);
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, includeRoot);
            marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, marshalEmptyCollections);
            marshaller.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, namespaceSeperator);
            marshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, valueWrapper);
            marshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespacePrefixMapper);

            Map<String, String> mediaTypeParameters = mediaType.getParameters();
            if(mediaTypeParameters.containsKey(CHARSET)) {
                String charSet = mediaTypeParameters.get(CHARSET);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, charSet);
            }

            preWriteTo(object, type, genericType, annotations, mediaType, httpHeaders, marshaller);
            marshaller.marshal(object, entityStream);
        } catch(JAXBException jaxbException) {
            throw new WebApplicationException(jaxbException);
        }
    }

}