/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Rick Barkhouse - 2.1 - Initial implementation
package org.eclipse.persistence.jaxb.dynamic;

import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext.MetadataContextInput;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext.SchemaContextInput;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext.SessionsXmlContextInput;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

/**
 * <p>
 * DynamicJAXBContextFactory allows the user to create a DynamicJAXBContext without having
 * realized Java classes available on the classpath.  During context creation, the user's
 * metadata will be analyzed, and in-memory classes will be generated.
 * </p>
 *
 * <p>
 * Objects that are returned by EclipseLink unmarshal methods will be subclasses of DynamicEntity.
 * DynamicEntities offer a simple get(propertyName) / set(propertyName, propertyValue) API to
 * manipulate their data.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <p><code>
 * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();<br>
 * InputStream iStream = classLoader.getResourceAsStream("resource/MySchema.xsd");<br><br>
 *
 * Map&lt;String, Object&gt; properties = new HashMap&lt;String, Object&gt;();<br>
 * properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);<br><br>
 *
 * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, properties);<br><br>
 *
 * DynamicEntity employee = jaxbContext.newDynamicEntity("org.example.Employee");<br>
 * employee.set("firstName", "Bob");<br>
 * employee.set("lastName", "Barker");<br>
 * jaxbContext.createMarshaller().(employee, System.out);
 * </code></p>
 *
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext
 * @see org.eclipse.persistence.dynamic.DynamicEntity
 * @see org.eclipse.persistence.dynamic.DynamicType
 *
 * @author rbarkhouse
 * @since EclipseLink 2.1
 */
public class DynamicJAXBContextFactory {

    public static final String XML_SCHEMA_KEY = "xml-schema";
    public static final String ENTITY_RESOLVER_KEY = "entity-resolver";
    public static final String EXTERNAL_BINDINGS_KEY = "external-bindings";
    public static final String SCHEMAMETADATA_CLASS_NAME = "org.eclipse.persistence.jaxb.dynamic.metadata.SchemaMetadata";

    /**
     * Create a <code>DynamicJAXBContext</code>, using either an XML Schema, EclipseLink OXM file,
     * or EclipseLink <code>sessions.xml</code> as the metadata source.  This creation method will be
     * called if the user calls the <code>newInstance()</code> method on <code>javax.xml.bind.JAXBContext</code>,
     * and has specified <code>javax.xml.bind.context.factory=org.eclipse.persistence.jaxb.DynamicJAXBContextFactory</code> in their
     * <code>jaxb.properties</code> file.<p>
     *
     * <b>-- Context Creation From XML Schema --</b><p>
     *
     * The <code>properties</code> map must contain the following key/value pairs:
     * <dl>
     * <dt>DynamicJAXBContextFactory.XML_SCHEMA_KEY
     * <dd>Either a <code>org.w3c.dom.Node</code>, <code>javax.xml.transform.Source</code>, or <code>java.io.InputStream</code> pointing to the XML Schema
     * <dt>DynamicJAXBContextFactory.ENTITY_RESOLVER_KEY
     * <dd>An <code>org.xml.sax.EntityResolver</code>, used to resolve schema imports.  Can be null.
     * </dl>
     *
     * <i>Example:</i>
     * <pre>
     * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     * InputStream iStream = classLoader.getResourceAsStream("resource/MySchema.xsd");
     *
     * Map&lt;String, Object&gt; properties = new HashMap&lt;String, Object&gt;();
     * properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);
     *
     * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, properties);
     * DynamicEntity emp = jaxbContext.newDynamicEntity("org.example.Employee");
     * ...
     * </pre>
     *
     * <b>Context Creation From EclipseLink OXM:</b><p>
     *
     * The <code>properties</code> map must contain the key <b>JAXBContextProperties.OXM_METADATA_SOURCE</b>, which can have
     * several possible values:
     *
     * <ul>
     * <li>One of the following, pointing to your OXM file: <code>java.io.File</code>, <code>java.io.InputStream</code>, <code>java.io.Reader</code>, <code>java.net.URL</code>,<br>
     * <code>javax.xml.stream.XMLEventReader</code>, <code>javax.xml.stream.XMLStreamReader</code>, <code>javax.xml.transform.Source</code>,<br>
     * <code>org.w3c.dom.Node</code>, or <code>org.xml.sax.InputSource</code>.
     * <li>A <code>List</code> of objects from the set above.
     * <li>A <code>Map&lt;String, Object&gt;</code>, where <code>String</code> is a package name, and <code>Object</code> is the pointer to the OXM file, from the set<br>
     * of possibilities above.  If using this option, a <code>package-name</code> element is not required in the <code>xml-bindings</code>
     * element of your OXM file.
     * </ul>
     *
     * <i>Example:</i>
     * <pre>
     * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     * InputStream iStream = classLoader.getResourceAsStream("resource/eclipselink-oxm.xml");
     *
     * Map&lt;String, Object&gt; properties = new HashMap&lt;String, Object&gt;();
     * properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
     *
     * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, properties);
     * DynamicEntity emp = jaxbContext.newDynamicEntity("org.example.Employee");
     * ...
     * </pre>
     *
     * <b>Context Creation From EclipseLink sessions.xml:</b><p>
     *
     * The <code>sessionNames</code> parameter is a colon-delimited list of session names within the
     * <code>sessions.xml</code> file.  <code>Descriptors</code> in this session's <code>Project</code> must not
     * have <code>javaClass</code> set, but must have <code>javaClassName</code> set.<p>
     *
     * <i>Example:</i>
     * <pre>
     * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     *
     * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, null);
     * DynamicEntity emp = jaxbContext.newDynamicEntity("org.example.Employee");
     * ...
     * </pre>
     *
     * @param contextPath
     *      A colon-delimited <code>String</code> specifying the packages containing <code>jaxb.properties</code>.  If bootstrapping
     *      from EclipseLink <code>sessions.xml</code>, this will also be the name(s) of your sessions.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <code>DynamicTypes</code> are generated.  Can be
     *      <code>null</code>, in which case <code>Thread.currentThread().getContextClassLoader()</code> will be used.
     * @param properties
     *      Map of properties to use when creating a new <code>DynamicJAXBContext</code>.  Can be null if bootstrapping from sessions.xml.
     *
     * @return
     *      A new instance of <code>DynamicJAXBContext</code>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <code>DynamicJAXBContext</code>.
     */
    public static DynamicJAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
        Object schema = null;
        EntityResolver resolver = null;
        Object bindings = null;

        if (properties != null) {
            schema = properties.get(XML_SCHEMA_KEY);
            resolver = (EntityResolver) properties.get(ENTITY_RESOLVER_KEY);
            if ((bindings = properties.get(JAXBContextProperties.OXM_METADATA_SOURCE)) == null) {
                // try looking up the 'old' metadata source key
                bindings = properties.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
            }
        }

        // First try looking for an XSD
        if (schema != null) {
            if (schema instanceof Node) {
                return createContextFromXSD((Node) schema, resolver, classLoader, properties);
            }
            if (schema instanceof InputStream) {
                return createContextFromXSD((InputStream) schema, resolver, classLoader, properties);
            }
            if (schema instanceof Source) {
                return createContextFromXSD((Source) schema, resolver, classLoader, properties);
            }
        }

        // Next, check for OXM
        if (bindings != null) {
            return createContextFromOXM(classLoader, properties);
        }

        // Lastly, try sessions.xml
        if (contextPath != null) {
            return new DynamicJAXBContext(new SessionsXmlContextInput(contextPath, properties, classLoader));
        } else {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullSessionName());
        }
    }

    /**
     * Unsupported Operation.  DynamicJAXBConexts can not be created from concrete classes.  Use the standard
     * JAXBContext to create a context from existing Classes.
     *
     * @see org.eclipse.persistence.jaxb.JAXBContext
     */
    public static DynamicJAXBContext createContext(Class<?>[] classes, Map<String, Object> properties) throws JAXBException {
        throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.cannotCreateDynamicContextFromClasses());
    }

    /**
     * Create a <code>DynamicJAXBContext</code>, using XML Schema as the metadata source.
     *
     * @param schemaDOM
     *      <code>org.w3c.dom.Node</code> representing the XML Schema.
     * @param resolver
     *      An <code>org.xml.sax.EntityResolver</code>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <code>DynamicTypes</code> are generated.  Can be
     *      <code>null</code>, in which case <code>Thread.currentThread().getContextClassLoader()</code> will be used.
     * @param properties
     *      Map of properties to use when creating a new <code>DynamicJAXBContext</code>.  Can be null.
     *
     * @return
     *      A new instance of <code>DynamicJAXBContext</code>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <code>DynamicJAXBContext</code>.
     */
    public static DynamicJAXBContext createContextFromXSD(Node schemaDOM, EntityResolver resolver, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
        if (schemaDOM == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullNode());
        }

        if (resolver != null) {
            // If schema and resolver are both specified, this indicates a schema import
            // This is not supported when boostrapping from a Node.
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.xsdImportNotSource());
        }

        DynamicJAXBContext ctx = new DynamicJAXBContext(new SchemaContextInput(schemaDOM, null, properties, classLoader));
        fixDateTimeConversion(ctx);
        return ctx;
    }

    /**
     * Create a <code>DynamicJAXBContext</code>, using XML Schema as the metadata source.
     *
     * @param schemaStream
     *      <code>java.io.InputStream</code> from which to read the XML Schema.
     * @param resolver
     *      An <code>org.xml.sax.EntityResolver</code>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <code>DynamicTypes</code> are generated.  Can be
     *      <code>null</code>, in which case <code>Thread.currentThread().getContextClassLoader()</code> will be used.
     * @param properties
     *      Map of properties to use when creating a new <code>DynamicJAXBContext</code>.  Can be null.
     *
     * @return
     *      A new instance of <code>DynamicJAXBContext</code>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <code>DynamicJAXBContext</code>.
     */
    public static DynamicJAXBContext createContextFromXSD(InputStream schemaStream, EntityResolver resolver, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (schemaStream == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullInputStream());
        }

        DynamicJAXBContext ctx = new DynamicJAXBContext(new SchemaContextInput(schemaStream, resolver, properties, classLoader));
        fixDateTimeConversion(ctx);
        return ctx;
    }

    /**
     * Create a <code>DynamicJAXBContext</code>, using XML Schema as the metadata source.
     *
     * @param schemaSource
     *      <code>javax.xml.transform.Source</code> from which to read the XML Schema.
     * @param resolver
     *      An <code>org.xml.sax.EntityResolver</code>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <code>DynamicTypes</code> are generated.  Can be
     *      <code>null</code>, in which case <code>Thread.currentThread().getContextClassLoader()</code> will be used.
     * @param properties
     *      Map of properties to use when creating a new <code>DynamicJAXBContext</code>.  Can be null.
     *
     * @return
     *      A new instance of <code>DynamicJAXBContext</code>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <code>DynamicJAXBContext</code>.
     */
    public static DynamicJAXBContext createContextFromXSD(Source schemaSource, EntityResolver resolver, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
        if (schemaSource == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullSource());
        }

        DynamicJAXBContext ctx = new DynamicJAXBContext(new SchemaContextInput(schemaSource, resolver, properties, classLoader));
        fixDateTimeConversion(ctx);
        return ctx;
    }

    /**
     * Create a <code>DynamicJAXBContext</code>, using an EclipseLink OXM file as the metadata source.
     *
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <code>DynamicTypes</code> are generated.  Can be
     *      <code>null</code>, in which case <code>Thread.currentThread().getContextClassLoader()</code> will be used.
     * @param properties
     *      Map of properties to use when creating a new <code>DynamicJAXBContext</code>.  This map must
     *      contain a key of JAXBContextProperties.OXM_METADATA_SOURCE, which can have several possible values:
     *
     * <ul>
     * <li>One of the following, pointing to your OXM file: <code>java.io.File</code>, <code>java.io.InputStream</code>, <code>java.io.Reader</code>, <code>java.net.URL</code>,<br>
     * <code>javax.xml.stream.XMLEventReader</code>, <code>javax.xml.stream.XMLStreamReader</code>, <code>javax.xml.transform.Source</code>,<br>
     * <code>org.w3c.dom.Node</code>, or <code>org.xml.sax.InputSource</code>.
     * <li>A <code>List</code> of objects from the set above.
     * <li>A <code>Map&lt;String, Object&gt;</code>, where <code>String</code> is a package name, and <code>Object</code> is the pointer to the OXM file, from the set<br>
     * of possibilities above.  If using this option, a <code>package-name</code> element is not required in the <code>xml-bindings</code>
     * element of your OXM file.
     * </ul>
     *
     *
     * @return
     *      A new instance of <code>DynamicJAXBContext</code>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <code>DynamicJAXBContext</code>.
     */
    public static DynamicJAXBContext createContextFromOXM(ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (properties == null || (properties.get(JAXBContextProperties.OXM_METADATA_SOURCE) == null && properties.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY) == null)) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.oxmKeyNotFound());
        }

        return new DynamicJAXBContext(new MetadataContextInput(properties, classLoader));
    }

    /**
     * Ensures that XSD dateTimes will always be unmarshalled as XMLGregorianCalendars, and never
     * as GregorianCalendars.  CALENDAR entries are removed from the default XMLConversionManager,
     * and replaced with XML_GREGORIAN_CALENDAR.
     */
    private static void fixDateTimeConversion(DynamicJAXBContext ctx) {
        XMLConversionManager conversionManager = (XMLConversionManager) ctx.getXMLContext().getSession().getDatasourcePlatform().getConversionManager();

        Map defaultXmlTypes = conversionManager.getDefaultXMLTypes();
        defaultXmlTypes.remove(Constants.DATE_TIME_QNAME);
        defaultXmlTypes.put(Constants.DATE_TIME_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);

        Map defaultJavaTypes = conversionManager.getDefaultJavaTypes();
        defaultJavaTypes.remove(CoreClassConstants.CALENDAR);
        defaultJavaTypes.put(CoreClassConstants.XML_GREGORIAN_CALENDAR, Constants.DATE_TIME_QNAME);
    }

}
