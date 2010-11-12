/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.dynamic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

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
 * String sessionName = "mynamespace.Employee"; <br>
 * ClassLoader loader = Thread.currentThread().getContextClassLoader(); <br><br>
 *
 * DynamicJAXBContext dContext = DynamicJAXBContextFactory.createContext(sessionName, null); <br><br>
 *
 * DynamicEntity employee = dContext.newDynamicEntity("mynamespace.Employee"); <br>
 * employee.set("firstName", "Bob"); <br>
 * employee.set("lastName", "Barker"); <br>
 * dContext.createMarshaller().(employee, System.out);
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

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using either an XML Schema, EclipseLink OXM file,
     * or EclipseLink sessions.xml as the metadata source, according to the contents of the
     * <tt>properties</tt> argument.<p>
     *
     * This creation method will be called if the user uses the <tt>newInstance()</tt> method
     * on <tt>javax.xml.bind.JAXBContext</tt>.<p>
     *
     * <b>Context Creation From XML Schema:</b><p>
     *
     * The <tt>properties</tt> map must contain the following key/value pairs:
     * <dl>
     * <dt>DynamicJAXBContextFactory.XML_SCHEMA_KEY
     * <dd>Either a <tt>Node</tt>, <tt>Source</tt>, or <tt>InputStream</tt> pointing to the XML Schema
     * <dt>DynamicJAXBContextFactory.ENTITY_RESOLVER_KEY
     * <dd>An <tt>org.xml.sax.EntityResolver</tt>, used to resolve schema imports.  Can be null.
     * </dl>
     *
     * <i>Example:</i>
     * <pre>
     * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     * InputStream iStream = classLoader.getResourceAsStream("resource/MySchema.xsd");
     *
     * Map<String, InputStream> properties = new HashMap<String, InputStream>();
     * properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);
     *
     * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, properties);
     * DynamicEntity emp = jaxbContext.newDynamicEntity("org.example.Employee");
     * ...
     * </pre>
     *
     * <b>Context Creation From EclipseLink OXM:</b><p>
     *
     * The <tt>properties</tt> map must contain the following key/value pair:
     * <dl>
     * <dt>JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY
     * <dd>A <tt>Map&lt;String, Source&gt;, containing one or more <tt>Sources</tt> pointing to OXM files, keyed on package name.</tt>
     * </dl>
     *
     * <i>Example:</i>
     * <pre>
     * ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     * InputStream iStream = classLoader.getResourceAsStream("resource/eclipselink-oxm.xml");
     *
     * HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
     * metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.dynamic", new StreamSource(iStream));
     *
     * Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
     * properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
     *
     * DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.example", classLoader, properties);
     * DynamicEntity emp = jaxbContext.newDynamicEntity("org.example.Employee");
     * ...
     * </pre>
     *
     * The <tt>sessionNames</tt> parameter is a colon-delimited list of session names within the
     * <tt>sessions.xml</tt> file.  <tt>Descriptors</tt> in this session's <tt>Project</tt> must <i>not</i>
     * have <tt>javaClass</tt> set, but <i>must</i> have <tt>javaClassName</tt> set.
     *
     * @param sessionNames
     *      A colon-delimited <tt>String</tt> specifying the session names from the <tt>sessions.xml</tt> file.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     * @param properties
     *      Map of properties to use when creating a new <tt>DynamicJAXBContext</tt>.  Can be null.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContext(String sessionNames, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        Object schema = null;
        EntityResolver resolver = null;
        Object bindings = null;

        if (properties != null) {
            schema = properties.get(XML_SCHEMA_KEY);
            resolver = (EntityResolver) properties.get(ENTITY_RESOLVER_KEY);
            bindings = properties.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
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
        if (sessionNames != null) {
            DynamicJAXBContext dContext = new DynamicJAXBContext();
            dContext.initializeFromSessionsXML(sessionNames, classLoader);
            return dContext;
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
    public static DynamicJAXBContext createContext(Class<?>[] classes, Map<String, ?> properties) throws JAXBException {
        throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.cannotCreateDynamicContextFromClasses());
    }

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using XML Schema as the metadata source.
     *
     * @param schemaDOM
     *      <tt>org.w3c.dom.Node</tt> representing the XML Schema.
     * @param resolver
     *      An <tt>org.xml.sax.EntityResolver</tt>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     * @param properties
     *      Map of properties to use when creating a new <tt>DynamicJAXBContext</tt>.  Can be null.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContextFromXSD(Node schemaDOM, EntityResolver resolver, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (schemaDOM == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullNode());
        }

        DynamicJAXBContext dContext = new DynamicJAXBContext();
        dContext.initializeFromXSDNode(schemaDOM, classLoader, resolver, properties);

        return dContext;
    }

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using XML Schema as the metadata source.
     *
     * @param schemaStream
     *      <tt>java.io.InputStream</tt> from which to read the XML Schema.
     * @param resolver
     *      An <tt>org.xml.sax.EntityResolver</tt>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     * @param properties
     *      Map of properties to use when creating a new <tt>DynamicJAXBContext</tt>.  Can be null.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContextFromXSD(InputStream schemaStream, EntityResolver resolver, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (schemaStream == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullInputStream());
        }

        InputSource schemaSource = new InputSource(schemaStream);

        DynamicJAXBContext dContext = new DynamicJAXBContext();
        dContext.initializeFromXSDInputSource(schemaSource, classLoader, resolver, properties);

        return dContext;
    }

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using XML Schema as the metadata source.
     *
     * @param schemaSource
     *      <tt>javax.xml.transform.Source</tt> from which to read the XML Schema.
     * @param resolver
     *      An <tt>org.xml.sax.EntityResolver</tt>, used to resolve schema imports.  Can be null.
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     * @param properties
     *      Map of properties to use when creating a new <tt>DynamicJAXBContext</tt>.  Can be null.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContextFromXSD(Source schemaSource, EntityResolver resolver, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (schemaSource == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.nullSource());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        XMLTransformer t = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        t.transform(schemaSource, result);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        InputSource schemaInputSource = new InputSource(bais);
        schemaInputSource.setSystemId(schemaSource.getSystemId());

        DynamicJAXBContext dContext = new DynamicJAXBContext();
        dContext.initializeFromXSDInputSource(schemaInputSource, classLoader, resolver, properties);

        return dContext;
    }

    /**
     * Create a <tt>DynamicJAXBContext</tt>, using an EclipseLink OXM file as the metadata source.
     *
     * @param classLoader
     *      The application's current class loader, which will be used to first lookup
     *      classes to see if they exist before new <tt>DynamicTypes</tt> are generated.  Can be
     *      <tt>null</tt>, in which case <tt>Thread.currentThread().getContextClassLoader()</tt> will be used.
     * @param properties
     *      Map of properties to use when creating a new <tt>DynamicJAXBContext</tt>.  This map must
     *      contain a key of JAXBContext.ECLIPSELINK_OXM_XML_KEY, with a value of Map<String, Source>,
     *      where String is the package name and Source is the metadata file for that package.
     *
     * @return
     *      A new instance of <tt>DynamicJAXBContext</tt>.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the <tt>DynamicJAXBContext</tt>.
     */
    public static DynamicJAXBContext createContextFromOXM(ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        if (properties == null || properties.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY) == null) {
            throw new JAXBException(org.eclipse.persistence.exceptions.JAXBException.oxmKeyNotFound());
        }

        DynamicJAXBContext dContext = new DynamicJAXBContext();
        dContext.initializeFromOXM(classLoader, properties);

        return dContext;
    }

}