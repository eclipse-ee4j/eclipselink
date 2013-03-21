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
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.core.sessions.CoreSessionEventListener;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;
import org.eclipse.persistence.internal.core.descriptors.CoreObjectBuilder;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

public abstract class Context<
    ABSTRACT_SESSION extends CoreAbstractSession,
    DESCRIPTOR extends Descriptor<?, ?, ?, ?, ?, NAMESPACE_RESOLVER, ?, ?, ?>,
    FIELD extends Field,
    NAMESPACE_RESOLVER extends NamespaceResolver,
    PROJECT extends CoreProject,
    SESSION extends CoreSession,
    SESSION_EVENT_LISTENER extends CoreSessionEventListener> {

    public static class ContextState<
        ABSTRACT_SESSION extends CoreAbstractSession,
        DESCRIPTOR extends Descriptor,
        PROJECT extends CoreProject,
        SESSION extends CoreSession,
        SESSION_EVENT_LISTENER extends CoreSessionEventListener> {

        protected Context context;
        protected Map<XPathQName, DESCRIPTOR> descriptorsByQName;
        protected Map<XPathFragment, DESCRIPTOR> descriptorsByGlobalType;
        protected SESSION session;
        private Collection<SESSION_EVENT_LISTENER> sessionEventListeners;

        protected ContextState() {
            descriptorsByQName = new HashMap<XPathQName, DESCRIPTOR>();
            descriptorsByGlobalType = new HashMap<XPathFragment, DESCRIPTOR>();
        }

        protected ContextState(Context context, PROJECT project, ClassLoader classLoader, Collection<SESSION_EVENT_LISTENER> sessionEventListeners) {
            this();
            this.context = context;
            preLogin(project, classLoader);
            session = (SESSION) project.createDatabaseSession();
        
            // if an event listener was passed in as a parameter, register it with the event manager
            if (sessionEventListeners != null) {
                for(SESSION_EVENT_LISTENER sessionEventListener : sessionEventListeners) {
                    session.getEventManager().addListener(sessionEventListener);
                }
            }

            // turn logging for this session off and leave the global session up
            // Note: setting level to SEVERE or WARNING will printout stacktraces for expected exceptions
            session.setLogLevel(SessionLog.OFF);
            // don't turn off global static logging
            //AbstractSessionLog.getLog().log(AbstractSessionLog.INFO, "ox_turn_global_logging_off", getClass());
            //AbstractSessionLog.getLog().setLevel(AbstractSessionLog.OFF);

            setupSession(session);
            storeDescriptorsByQName(session);
        }

        public void addDescriptorByQName(QName qName, DESCRIPTOR descriptor) {
            XPathQName xpathQName = new XPathQName(qName, true);
            addDescriptorByQName(xpathQName, descriptor);
        }

        private void addDescriptorByQName(XPathQName qName, DESCRIPTOR descriptor) {
            descriptorsByQName.put(qName, descriptor);
        }

        protected void preLogin(PROJECT project, ClassLoader classLoader) {
        }

        /**
         * INTERNAL: Return the Descriptor with the default root mapping matching
         * the QName parameter.
         */
        private DESCRIPTOR getDescriptor(QName qName) {
            XPathQName xpathQName = new XPathQName(qName, true);
            return descriptorsByQName.get(xpathQName);
        }

        /**
         * INTERNAL: Return the Descriptor with the default root mapping matching
         * the QName parameter.
         */
        private DESCRIPTOR getDescriptor(XPathQName qName) {
            return descriptorsByQName.get(qName);
        }
    
        /**
         * INTERNAL: Return the Descriptor mapped to the global type matching the
         * XPathFragment parameter.
         */
        private DESCRIPTOR getDescriptorByGlobalType(XPathFragment xPathFragment) {
            return this.descriptorsByGlobalType.get(xPathFragment);
        }
    
        protected SESSION getSession() {
            return session;
        }

        /**
         * INTERNAL: Return the session corresponding to this class. Since the class
         * may be mapped by more that one of the projects used to create the
         * Context, this method will return the first match.
         */
        protected ABSTRACT_SESSION getSession(Class clazz) {
            if (null == clazz) {
                return null;
            }
            if (session.getDescriptor(clazz) != null) {
                return (ABSTRACT_SESSION) session;
            }
            throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
        }
    
        /**
         * INTERNAL: Return the session corresponding to this Descriptor. Since
         * the class may be mapped by more that one of the projects used to create
         * the Context, this method will return the first match.
         */
        protected ABSTRACT_SESSION getSession(DESCRIPTOR descriptor) {
            if (null == descriptor) {
                return null;
            }
            if (session.getProject().getOrderedDescriptors().contains(descriptor)) {
                return (ABSTRACT_SESSION) session;
            }
            throw XMLMarshalException.descriptorNotFoundInProject(descriptor.getJavaClass().getName());
        }

        /**
         * INTERNAL: Return the session corresponding to this object. Since the
         * object may be mapped by more that one of the projects used to create the
         * Context, this method will return the first match.
         */
        protected ABSTRACT_SESSION getSession(Object object) {
            if (null == object) {
                return null;
            }
            if (session.getDescriptor(object) != null) {
                return (ABSTRACT_SESSION) session;
            }
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }
    
        protected void setupSession(SESSION session) {
        }

        /**
         * INTERNAL:
         */
        public void storeDescriptorByQName(DESCRIPTOR descriptor, CorePlatform platform) {
            XPathQName descriptorQName;
            String defaultRootName;
    
            List tableNames = descriptor.getTableNames();
            for (int i = 0; i < tableNames.size(); i++) {
                defaultRootName = (String) tableNames.get(i);
    
                if (null != defaultRootName) {
                    int index = defaultRootName.indexOf(':');
                    String defaultRootLocalName = defaultRootName.substring(index + 1);
                    if(defaultRootLocalName != null && !(defaultRootLocalName.equals(Constants.EMPTY_STRING))){
                        if (index > -1) {
                            String defaultRootPrefix = defaultRootName.substring(0, index);
                            String defaultRootNamespaceURI = descriptor.getNamespaceResolver().resolveNamespacePrefix(defaultRootPrefix);
                            descriptorQName = new XPathQName(defaultRootNamespaceURI, defaultRootLocalName, true);
                        } else {
                            if(descriptor.getNamespaceResolver() != null) {
                                descriptorQName = new XPathQName(descriptor.getNamespaceResolver().getDefaultNamespaceURI(), defaultRootLocalName, true);
                            } else {
                                descriptorQName = new XPathQName(defaultRootLocalName, true);
                            }
                        }
                        if (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().isRootParentDescriptor()) {
                            addDescriptorByQName(descriptorQName, descriptor);
                        } else {
                            //this means we have a descriptor that is a child in an inheritance hierarchy
                            storeDescriptorByQName((DESCRIPTOR) descriptor.getInheritancePolicy().getParentDescriptor(), platform);
                            Descriptor existingDescriptor = getDescriptor(descriptorQName);
                            if (existingDescriptor == null) {
                                addDescriptorByQName(descriptorQName, descriptor);
                            }
                        }
                    }
                }
            }
    
            XMLSchemaReference xmlSchemaReference = descriptor.getSchemaReference();
            if (null != xmlSchemaReference) {
                String schemaContext = xmlSchemaReference.getSchemaContext();
                if ((xmlSchemaReference.getType() == XMLSchemaReference.COMPLEX_TYPE) || (xmlSchemaReference.getType() == XMLSchemaReference.SIMPLE_TYPE)) {
                    if ((null != schemaContext) && (schemaContext.lastIndexOf('/') == 0)) {
                        schemaContext = schemaContext.substring(1, schemaContext.length());
                        XPathFragment typeFragment = new XPathFragment(schemaContext);
                        if (null != descriptor.getNamespaceResolver()) {
                           String uri = descriptor.getNamespaceResolver().resolveNamespacePrefix(typeFragment.getPrefix());
                           if(uri == null && xmlSchemaReference.getSchemaContextAsQName() != null){
                               uri = xmlSchemaReference.getSchemaContextAsQName().getNamespaceURI();
                           }
                           typeFragment.setNamespaceURI(uri);
                        }
                        this.descriptorsByGlobalType.put(typeFragment, descriptor);
                    } else {
                        QName qname = xmlSchemaReference.getSchemaContextAsQName();
                        if (qname != null) {
                            if (descriptor.isWrapper() && descriptor.getJavaClassName().contains("ObjectWrapper")) {
                                return;
                            }
                            XPathFragment typeFragment = new XPathFragment();
                            typeFragment.setLocalName(qname.getLocalPart());
                            typeFragment.setNamespaceURI(qname.getNamespaceURI());
                            this.descriptorsByGlobalType.put(typeFragment, descriptor);
                        }
                    }
                }
            }
        }
    
        public void storeDescriptorsByQName(CoreSession session) {
            Iterator iterator = session.getProject().getOrderedDescriptors().iterator();
            while (iterator.hasNext()) {
                DESCRIPTOR descriptor = (DESCRIPTOR) iterator.next();
                storeDescriptorByQName(descriptor, session.getDatasourcePlatform());
            }
        }
    
    }

    private class XPathQueryResult {
        /*
         * Mapping corresponding to the XPath query
         */
        private CoreMapping mapping;

        /*
         * Mapping's owning object
         */
        private Object owner;

        /*
         * Index into mapping, from XPath query (may be null)
         */
        private Integer index;
    }

    protected volatile ContextState<ABSTRACT_SESSION, DESCRIPTOR, PROJECT, SESSION, SESSION_EVENT_LISTENER> contextState;

    private <T> T createByXPath(Object object, CoreObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NAMESPACE_RESOLVER namespaceResolver, Class<T> returnType) {
        XPathQueryResult queryResult = getMappingForXPath(object, objectBuilder, stringTokenizer, namespaceResolver);

        if (null != queryResult.mapping) {
            DESCRIPTOR refDescriptor = (DESCRIPTOR) queryResult.mapping.getReferenceDescriptor();
            if (null != refDescriptor) {
                return (T) refDescriptor.getInstantiationPolicy().buildNewInstance();
            }
        }
        return null;
    }

    /**
     * Create a new object instance for a given XPath, relative to the parentObject.
     *
     * @param <T>
     *      The return type of this method corresponds to the returnType parameter.
     * @param parentObject
     *      The XPath will be executed relative to this object.
     * @param xPath
     *      The XPath statement.
     * @param namespaceResolver
     *      A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param returnType
     *      The return type.
     *
     * @return
     *      An instance of the Java class mapped to the supplied return type, or null
     *      if no result was found.
     */
    public <T> T createByXPath(Object parentObject, String xPath, NAMESPACE_RESOLVER namespaceResolver, Class<T> returnType) {
        ABSTRACT_SESSION session = this.getSession(parentObject);
        DESCRIPTOR descriptor = (DESCRIPTOR) session.getDescriptor(parentObject);
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, "/");
        return createByXPath(parentObject, descriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, returnType);
    }

    protected abstract FIELD createField(String path);

    public abstract Marshaller createMarshaller();

    public abstract Unmarshaller createUnmarshaller();

    /**
     * INTERNAL: 
     * Return the Descriptor with the default root mapping matching the QName 
     * parameter.
     */
    public DESCRIPTOR getDescriptor(QName qName) {
        XPathQName xpathQName = new XPathQName(qName, true);
        return contextState.getDescriptor(xpathQName);
    }

    /**
     * INTERNAL: 
     * Return the Descriptor with the default root mapping matching the
     * XPathQName parameter.
     */
    public DESCRIPTOR getDescriptor(XPathQName xpathQName) {
        return contextState.getDescriptor(xpathQName);
    }

    /**
     * INTERNAL:
     * Return the Descriptor mapped to the global type matching the
     * XPathFragment parameter.
     */
    public DESCRIPTOR getDescriptorByGlobalType(XPathFragment xPathFragment) {
        return contextState.getDescriptorByGlobalType(xPathFragment);
    }

    private XPathQueryResult getMappingForXPath(Object object, CoreObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NAMESPACE_RESOLVER namespaceResolver) {
        XPathQueryResult queryResult = new XPathQueryResult();

        String xPath = "";
        FIELD field = createField(null);
        field.setNamespaceResolver(namespaceResolver);
        while (stringTokenizer.hasMoreElements()) {
            String nextToken = stringTokenizer.nextToken();
            field.setXPath(xPath + nextToken);
            field.initialize();
            CoreMapping mapping = objectBuilder.getMappingForField(field);
            if (null == mapping) {
                // XPath might have indexes, while the mapping's XPath may not,
                // so remove them and look again
                XPathFragment xPathFragment = new XPathFragment(nextToken);
                int fieldIndex = field.getXPathFragment().getIndexValue();
                int fragmentIndex = xPathFragment.getIndexValue();
                if (fieldIndex > 0 || fragmentIndex > 0) {
                    int index = fieldIndex - 1;
                    if (index < 0) {
                        index = fragmentIndex - 1;
                    }
                    String xPathNoIndexes = removeIndexesFromXPath(field.getXPath());
                    field.setXPath(xPathNoIndexes);
                    field.initialize();
                    mapping = objectBuilder.getMappingForField(field);
                    if (null == mapping) {
                        // Try adding /text()
                        field.setXPath(xPathNoIndexes + Constants.XPATH_SEPARATOR + Constants.TEXT);
                        field.initialize();
                        mapping = objectBuilder.getMappingForField(field);
                    }
                    if (null != mapping) {
                        if (field.getXPath().endsWith(Constants.TEXT) || !stringTokenizer.hasMoreElements()) {
                            // End of the line, we found a mapping so return it
                            queryResult.mapping = mapping;
                            queryResult.owner = object;
                            queryResult.index = index;
                            return queryResult;
                        } else {
                            // We need to keep looking -- get the mapping value,
                            // then recurse into getMappingForXPath with new root object
                            Object childObject = mapping.getAttributeValueFromObject(object);
                            if (mapping.isCollectionMapping()) {
                                Object collection = mapping.getAttributeValueFromObject(object);
                                if (null != collection  && List.class.isAssignableFrom(collection.getClass())) {
                                    List list = (List) collection;
                                    if (index >= list.size()) {
                                        // Index used in query is out of range, no matches
                                        return null;
                                    }
                                    childObject = list.get(index);
                                }
                            }
                            if (null == childObject) {
                                childObject = mapping.getReferenceDescriptor().getObjectBuilder().buildNewInstance();
                            }
                            CoreObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder();
                            return getMappingForXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver);
                        }
                    }
                }
            } else {
                if (!stringTokenizer.hasMoreElements()) {
                    // End of the line, we found a mapping so return it
                    queryResult.mapping = mapping;
                    queryResult.owner = object;
                    return queryResult;
                } else {
                    // We need to keep looking -- get the mapping value,
                    // then recurse into getMappingForXPath with new root object
                    Object childObject = mapping.getAttributeValueFromObject(object);
                    if (mapping.isCollectionMapping()) {
                        Object collection = mapping.getAttributeValueFromObject(object);
                        if (null != collection && List.class.isAssignableFrom(collection.getClass())) {
                            List list = (List) collection;
                            if (0 >= list.size()) {
                                return null;
                            }
                            childObject = list.get(0);
                        }
                    }
                    if (null == childObject) {
                        childObject = mapping.getReferenceDescriptor().getObjectBuilder().buildNewInstance();
                    }
                    CoreObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder();
                    return getMappingForXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver);
                }
            }
            xPath = xPath + nextToken + Constants.XPATH_SEPARATOR;
        }
        return null;
    }

    /**
     * INTERNAL: 
     * Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the
     * Context, this method will return the first match.
     */
    public ABSTRACT_SESSION getSession(Class clazz) {
        return contextState.getSession(clazz);
    }

    /**
     * INTERNAL: 
     */
    public SESSION getSession() {
        return contextState.getSession();
    }

    public ABSTRACT_SESSION getSession(DESCRIPTOR descriptor) {
        return contextState.getSession(descriptor);
    }

    /**
     * INTERNAL: 
     * Return the session corresponding to this object. Since the
     * object may be mapped by more that one of the projects used to create the
     * Context, this method will return the first match.
     */
    public ABSTRACT_SESSION getSession(Object object) {
        return contextState.getSession(object);
    }

    /**
     * <p>Query the object model based on the corresponding document.  The following pairings are equivalent:</p> 
     * 
     * <i>Return the Customer's ID</i>
     * <pre> Integer id = context.getValueByXPath(customer, "@id", null, Integer.class);
     * Integer id = customer.getId();</pre>
     * 
     * <i>Return the Customer's Name</i>
     * <pre> String name = context.getValueByXPath(customer, "ns:personal-info/ns:name/text()", null, String.class);
     * String name = customer.getName();</pre>
     * 
     * <i>Return the Customer's Address</i>
     * <pre> Address address = context.getValueByXPath(customer, "ns:contact-info/ns:address", aNamespaceResolver, Address.class);
     * Address address = customer.getAddress();</pre>
     * 
     * <i>Return all the Customer's PhoneNumbers</i> 
     * <pre> List phoneNumbers = context.getValueByXPath(customer, "ns:contact-info/ns:phone-number", aNamespaceResolver, List.class);
     * List phoneNumbers = customer.getPhoneNumbers();</pre>
     * 
     * <i>Return the Customer's second PhoneNumber</i>
     * <pre> PhoneNumber phoneNumber = context.getValueByXPath(customer, "ns:contact-info/ns:phone-number[2]", aNamespaceResolver, PhoneNumber.class);
     * PhoneNumber phoneNumber = customer.getPhoneNumbers().get(1);</pre>
     * 
     * <i>Return the base object</i>
     * <pre> Customer customer = context.getValueByXPath(customer, ".", aNamespaceResolver, Customer.class);
     * Customer customer = customer;
     * </pre>
     * 
     * @param <T> The return type of this method corresponds to the returnType parameter.
     * @param object  The XPath will be executed relative to this object.
     * @param xPath The XPath statement
     * @param namespaceResolver A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param returnType The return type.
     * @return The object corresponding to the XPath or null if no result was found.
     */
    public <T> T getValueByXPath(Object object, String xPath, NAMESPACE_RESOLVER namespaceResolver, Class<T> returnType) {
        if (null == xPath || null == object) {
            return null;
        }
        if (".".equals(xPath)) {
            return (T) object;
        }
        ABSTRACT_SESSION session = this.getSession(object);
        DESCRIPTOR descriptor = (DESCRIPTOR) session.getDescriptor(object);
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, Constants.XPATH_SEPARATOR);
        T value = getValueByXPath(object, descriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, returnType);
        if (null == value) {
            CoreMapping selfMapping = descriptor.getObjectBuilder().getMappingForField(createField(String.valueOf(Constants.DOT)));
            if (null != selfMapping) {
                return getValueByXPath(selfMapping.getAttributeValueFromObject(object), selfMapping.getReferenceDescriptor().getObjectBuilder(),
                        new StringTokenizer(xPath, Constants.XPATH_SEPARATOR),  ((DESCRIPTOR) selfMapping.getReferenceDescriptor()).getNamespaceResolver(), returnType);
            }
        }
        return value;
    }

    private <T> T getValueByXPath(Object object, CoreObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NAMESPACE_RESOLVER namespaceResolver, Class<T> returnType) {
        XPathQueryResult queryResult = getMappingForXPath(object, objectBuilder, stringTokenizer, namespaceResolver);
        
        if (null != queryResult) {
            CoreMapping mapping = queryResult.mapping;
            Object owner = queryResult.owner;
            Integer index = queryResult.index;
            
            if (null != owner) {
                Object childObject = null;
                if (mapping.isCollectionMapping()) {
                    Object collection = mapping.getAttributeValueFromObject(owner);
                    if (List.class.isAssignableFrom(collection.getClass())) {
                        List list = (List) collection;
                        if (null == index) {
                            return (T) collection;
                        }
                        if (index >= list.size()) {
                            return null;
                        }
                        childObject = list.get(index);
                    }
                } else {
                    childObject = mapping.getAttributeValueFromObject(owner);
                }
                return (T) childObject;
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Return true if any session held onto by this context has a document preservation
     * policy that requires unmarshalling from a Node.
     */
    public abstract boolean hasDocumentPreservation();

    private String removeIndexesFromXPath(String xpathWithIndexes) {
        String newXPath = xpathWithIndexes;
        while (newXPath.contains(Constants.XPATH_INDEX_OPEN)) {
            int open = newXPath.lastIndexOf(Constants.XPATH_INDEX_OPEN);
            int closed = newXPath.lastIndexOf(Constants.XPATH_INDEX_CLOSED);
            newXPath = newXPath.substring(0, open) + newXPath.substring(closed + 1);
        }
        return newXPath;
    }

    private void setValueByXPath(Object object, CoreObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NAMESPACE_RESOLVER namespaceResolver, Object value) {
        XPathQueryResult queryResult = getMappingForXPath(object, objectBuilder, stringTokenizer, namespaceResolver);
        
        if (null != queryResult) {
            CoreMapping mapping = queryResult.mapping;
            Object owner = queryResult.owner;
            Integer index = queryResult.index;

            if (null != owner) {
                if (mapping.isCollectionMapping()) {
                    Object childObject = null;
                    Object collection = mapping.getAttributeValueFromObject(owner);
                    if (List.class.isAssignableFrom(collection.getClass())) {
                        List list = (List) collection;
                        if (null == index) {
                            // We are setting the whole collection, not an element in the collection
                            if (value.getClass().isArray()) {
                                ArrayList newList = new ArrayList();
                                int length = Array.getLength(value);
                                for (int i = 0; i < length; i++) {
                                    newList.add(Array.get(value, i));
                                }
                                value = newList;
                            }
                            mapping.setAttributeValueInObject(owner, value);
                            return;
                        }
                        if (index >= list.size()) {
                            return;
                        }
                        // Set into collection
                        list.set(index, value);
                        mapping.setAttributeValueInObject(owner, list);
                        return;
                    }
                } else {
                    mapping.setAttributeValueInObject(owner, value);
                }
            }
        }
    }

    /**
     * <p>Set values in the object model based on the corresponding document.  The following pairings are equivalent:</p> 
     * 
     * <i>Set the Customer's ID</i>
     * <pre> context.setValueByXPath(customer, "@id", null, new Integer(123));
     * customer.setId(new Integer(123));</pre>
     * 
     * <i>Set the Customer's Name</i>
     * <pre> context.setValueByXPath(customer, "ns:personal-info/ns:name/text()", aNamespaceResolver, "Jane Doe");
     * customer.setName("Jane Doe");</pre>
     * 
     * <i>Set the Customer's Address</i>
     * <pre> context.setValueByXPath(customer, "ns:contact-info/ns:address", aNamespaceResolver, anAddress);
     * customer.setAddress(anAddress);</pre>
     * 
     * <i>Set the Customer's PhoneNumbers</i> 
     * <pre> context.setValueByXPath(customer, "ns:contact-info/ns:phone-number", aNamespaceResolver, phoneNumbers);
     * customer.setPhoneNumbers(phoneNumbers);</pre>
     * 
     * <i>Set the Customer's second PhoneNumber</i>
     * <pre> context.setValueByXPath(customer, "ns:contact-info/ns:phone-number[2]", aNamespaceResolver, aPhoneNumber);
     * customer.getPhoneNumbers().get(1);</pre>
     * 
     * @param object  The XPath will be executed relative to this object.
     * @param xPath The XPath statement
     * @param namespaceResolver A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param value The value to be set.
     */
    public void setValueByXPath(Object object, String xPath, NAMESPACE_RESOLVER namespaceResolver, Object value) { 
        ABSTRACT_SESSION session = this.getSession(object); 
        DESCRIPTOR descriptor = (DESCRIPTOR) session.getDescriptor(object); 
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, "/"); 
        setValueByXPath(object, descriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, value); 
    } 

}