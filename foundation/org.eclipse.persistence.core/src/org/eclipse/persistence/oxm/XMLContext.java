/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.oxm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.accessor.OrmAttributeAccessor;
import org.eclipse.persistence.internal.oxm.documentpreservation.DescriptorLevelDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.NoDocumentPreservationPolicy;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.SessionEventManager;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

/**
 * <p>
 * An XMLContext is created based on EclipseLink sessions or projects and can then
 * used to create instances of XMLMarshaller, XMLUnmarshaller and XMLValidator.
 *
 * <p>
 * There are constructors to create an XMLContext with a single EclipseLink project
 * or with a String which is a single EclipseLink session name or a ':' separated
 * list of EclipseLink session names.
 *
 * <p>
 * <em>Code Sample</em><br>
 * <code>
 *  XMLContext context = new XMLContext("mySessionName");<br>
 *  XMLMarshaller marshaller = context.createMarshaller();<br>
 *  XMLUnmarshaller unmarshaller = context.createUnmarshaller();<br>
 *  XMLValidator validator = context.createValidator();<br>
 *  <code>
 *
 *  <p>The XMLContext is thread-safe.  If multiple threads accessing the same XMLContext object
 *  request an XMLMarshaller, each will receive their own instance of XMLMarshaller, so any
 *  state that the XMLMarshaller maintains will be unique to that process.  The same is true
 *  of instances of XMLUnmarshaller and XMLValidator.
 *
 *  @see org.eclipse.persistence.oxm.XMLMarshaller
 *  @see org.eclipse.persistence.oxm.XMLUnmarshaller
 *  @see org.eclipse.persistence.oxm.XMLValidator
 *
 */
public class XMLContext {

    private volatile XMLContextState xmlContextState;

    XMLContext(XMLContextState xmlContextState) {
        this.xmlContextState = xmlContextState;
    }

    /**
     * Create a new XMLContext based on the specified session name or list of
     * session names
     *
     * @param sessionNames
     *            A single session name or multiple session names separated by a :
     */
    public XMLContext(String sessionNames) {
        this(sessionNames, PrivilegedAccessHelper.privilegedGetClassLoaderForClass(XMLContext.class));
    }

    /**
     * Create a new XMLContext based on the specified session name or list of
     * session names
     *
     * @param sessionNames
     *            A single session name or multiple session names separated by a :
     * @param classLoader
     *            classloader for loading sessions.xml
     */
    public XMLContext(String sessionNames, ClassLoader classLoader) {
        this(sessionNames, classLoader, null);
    }

    /**
     * Create a new XMLContext based on passed in session names and session meta
     * XML.
     *
     * @param sessionNames
     *            A single session name or multiple session names separated by
     *            a:
     * @param xmlResource
     *            path to XML file containing session meta data to initialize
     *            and load sessions.
     */
    public XMLContext(String sessionNames, String xmlResource) {
        this(sessionNames, PrivilegedAccessHelper.privilegedGetClassLoaderForClass(XMLContext.class), xmlResource);
    }

    /**
     * Create a new XMLContext based on passed in session names, classloader and
     * session meta XML.
     *
     * @param sessionNames
     *            A single session name or multiple session names separated by a :
     * @param classLoader
     *            classloader for loading sessions.xml
     * @param xmlResource
     *            path to XML file containing session meta data to initialize
     *            and load sessions.
     */
    public XMLContext(String sessionNames, ClassLoader classLoader, String xmlResource) {
        xmlContextState = new XMLContextState(this, sessionNames, classLoader, xmlResource);
    }

    /**
     * Create a new XMLContext based on the specified project
     *
     * @param project
     *            An EclipseLink project
     */
    public XMLContext(Project project) {
        this(project, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a new XMLContext based on the specified Project and ClassLoader.
     *
     * @param project An EclipseLink project
     * @param classLoader The ClassLoader to be used 
     */
    public XMLContext(Project project, ClassLoader classLoader) {
        this (project, classLoader, null);
    }
    
    /**
     * Create a new XMLContext based on the specified Project and ClassLoader.
     *
     * @param project An EclipseLink project
     * @param classLoader The ClassLoader to be used 
     * @param sessionEventListener If non-null, this listener will be registered with the SessionEventManager
     * @see SessionEventListener
     * @see SessionEventManager
     */
    public XMLContext(Project project, ClassLoader classLoader, SessionEventListener sessionEventListener) {
        xmlContextState = new XMLContextState(this, project, classLoader, sessionEventListener);
    }

    public XMLContext(Collection projects) {
    	this(projects, Thread.currentThread().getContextClassLoader());
    }
    
    public XMLContext(Collection projects, ClassLoader classLoader) {
        xmlContextState = new XMLContextState(this, projects, classLoader);
    }

    /**
     * INTERNAL:  Return the XMLContextState that represents the XMLContexts 
     * stateful information.  This method is provided for the benefits of layers
     * that build on top of the core OXM layer such as MOXy's JAXB
     * implementation.
     */
    public XMLContextState getXMLContextState() {
        return (XMLContextState) xmlContextState;
    }

    /**
     * INTERNAL: Set the stateful information for this XMLContext.  Once the new
     * state has been set, OXM operations (marshal, unmarshal, etc) will be 
     * based on this new state.  This method is provided for the benefit of 
     * layers that build on top of the core OXM layer such as MOXy's JAXB
     * 
     */
    public void setXMLContextState(XMLContextState xcs) {
        synchronized(this) {
            this.xmlContextState = xcs;
        }
    }

    /**
     * INTERNAL: Add and initialize a new session to the list of sessions
     * associated with this XMLContext.
     */
    public void addSession(DatabaseSession sessionToAdd) {
        xmlContextState.addSession(sessionToAdd);
    }

    /**
     * Create a new XMLUnmarshaller
     *
     * @return An XMLUnmarshaller based on this XMLContext
     */
    public XMLUnmarshaller createUnmarshaller() {
        return new XMLUnmarshaller(this);
    }

    /**
     * Create a new XMLUnmarshaller
     * <pre>
     * Map<String, Boolean> parserFeatures = new HashMap<String, Boolean>(1);
     * parserFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", false);
     * XMLUnmarshaller unmarshaller = xmlContext.createUnmarshaller(parserFeatures);
     * </pre>
     * @return An XMLUnmarshaller based on this XMLContext, the underlying
     * parser will use the passed in parser features.
     */
    public XMLUnmarshaller createUnmarshaller(Map<String, Boolean> parserFeatures) {
        return new XMLUnmarshaller(this, parserFeatures);
    }

    /**
     * Create a new XMLBinder
     * @return an XMLBinder based on this XMLContext
     */
    public XMLBinder createBinder() {
        return new XMLBinder(this);
    }

    /**
     * Create a new XMLMarshaller
     *
     * @return An XMLMarshaller based on this XMLContext
     */
    public XMLMarshaller createMarshaller() {
        return new XMLMarshaller(this);
    }

    /**
     * Create a new XMLValidator
     *
     * @return An XMLValidator based on this XMLContext
     */
    public XMLValidator createValidator() {
        return new XMLValidator(this);
    }

    /**
     * INTERNAL: Return the session corresponding to this object. Since the
     * object may be mapped by more that one of the projects used to create the
     * XML Context, this method will return the first match.
     *
     * The session will be a unit of work if document preservation is not
     * enabled.  This method will typically  be used for unmarshalling
     * when a non-shared cache is desired.
     */
    public AbstractSession getReadSession(Object object) {
        return xmlContextState.getReadSession(object);
    }

    /**
     * INTERNAL:
     * Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the XML
     * Context, this method will return the first match.
     *
     * The session will be a unit of work if document preservation is not
     * enabled.  This method will typically  be used for unmarshalling
     * when a non-shared cache is desired.
     */
    public AbstractSession getReadSession(Class clazz) {
        return xmlContextState.getReadSession(clazz);
    }

    /**
     * INTERNAL:
     * Return the session corresponding to this XMLDescriptor. Since
     * the class may be mapped by more that one of the projects used to create
     * the XML Context, this method will return the first match.
     *
     * The session will be a unit of work if document preservation is not
     * enabled.  This method will typically  be used for unmarshalling
     * when a non-shared cache is desired.
     */
    public AbstractSession getReadSession(XMLDescriptor xmlDescriptor) {
        return xmlContextState.getReadSession(xmlDescriptor);
    }

    /**
     * INTERNAL: Return the EclipseLink session used to marshal.
     */
    public List getSessions() {
        return xmlContextState.getSessions();
    }

    /**
     * INTERNAL: <code>
     * XMLContext xmlContext = new XMLContext("path0:path1");<br>
     * DatabaseSession session = xmlContext.getSession(0);  // returns session for path0<br>
     * </code>
     */
    public DatabaseSession getSession(int index) {
        return xmlContextState.getSession(index);
    }

    /**
     * INTERNAL: Return the session corresponding to this object. Since the
     * object may be mapped by more that one of the projects used to create the
     * XML Context, this method will return the first match.
     */
    public AbstractSession getSession(Object object) {
        return xmlContextState.getSession(object);
    }

    /**
     * INTERNAL: Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the XML
     * Context, this method will return the first match.
     */
    public AbstractSession getSession(Class clazz) {
        return xmlContextState.getSession(clazz);
    }

    /**
     * INTERNAL: Return the session corresponding to this XMLDescriptor. Since
     * the class may be mapped by more that one of the projects used to create
     * the XML Context, this method will return the first match.
     */
    public AbstractSession getSession(XMLDescriptor xmlDescriptor) {
        return xmlContextState.getSession(xmlDescriptor);
    }

    /**
     * INTERNAL:
     */
    public void storeXMLDescriptorByQName(XMLDescriptor xmlDescriptor) {
        xmlContextState.storeXMLDescriptorByQName(xmlDescriptor);
    }

    /**
     * INTERNAL: Return the XMLDescriptor with the default root mapping matching
     * the QName parameter.
     */
    public XMLDescriptor getDescriptor(QName qName) {
        return xmlContextState.getDescriptor(qName);
    }

    public void addDescriptorByQName(QName qName, XMLDescriptor descriptor) {
        xmlContextState.addDescriptorByQName(qName, descriptor);
    }
    
    /**
     * INTERNAL: Return the XMLDescriptor mapped to the global type matching the
     * XPathFragment parameter.
     */
    public XMLDescriptor getDescriptorByGlobalType(XPathFragment xPathFragment) {
        return xmlContextState.getDescriptorByGlobalType(xPathFragment);
    }

    /**
     * INTERNAL:
     * Return the DocumentPreservationPolicy associated with this session
     * @param session
     * @return
     */
    public DocumentPreservationPolicy getDocumentPreservationPolicy(AbstractSession session) {
        if (session == null) {
            return null;
        }
        XMLLogin login = (XMLLogin) session.getDatasourceLogin();
        return login.getDocumentPreservationPolicy();
    }

    public void setupDocumentPreservationPolicy(DatabaseSession session) {
        xmlContextState.setupDocumentPreservationPolicy(session);
    }

    /**
     * INTERNAL:
     * Return true if any session held onto by this context has a document preservation
     * policy that requires unmarshalling from a Node.
     */
    public boolean hasDocumentPreservation() {
        return xmlContextState.hasDocumentPreservation();
    }
    
    /**
    * ADVANCED:
    * Adjust the OXM metadata to take into account ORM mapping metadata,
    */
    public void applyORMMetadata(AbstractSession ormSession) {
        //Iterate over the ORM descriptors and check for matching OXM descriptors
        Iterator ormDescriptors = ormSession.getDescriptors().values().iterator();
        while(ormDescriptors.hasNext()) {
            ClassDescriptor ormDescriptor = (ClassDescriptor)ormDescriptors.next();
            Class javaClass = ormDescriptor.getJavaClass();
            AbstractSession oxmSession = null;
            try {
                oxmSession = this.getSession(javaClass);
            } catch(XMLMarshalException ex) {
                //if we couldn't find a session for this class, we
                //don't have an OX descriptor for it. 
            }
            if(oxmSession != null) {
                ClassDescriptor oxmDescriptor = oxmSession.getDescriptor(javaClass);
                //If we have an oxmDescriptor for this ORM descriptor, iterate over
                //mappings, and update the required OXM mappings attribute accessors
                Iterator<DatabaseMapping> ormMappings = ormDescriptor.getMappings().iterator();
                while(ormMappings.hasNext()) {
                    DatabaseMapping ormMapping = ormMappings.next();
                    DatabaseMapping oxmMapping = oxmDescriptor.getMappingForAttributeName(ormMapping.getAttributeName());
                    if(oxmMapping != null) {
                        AttributeAccessor oxmAccessor = oxmMapping.getAttributeAccessor();
                        OrmAttributeAccessor newAccessor = new OrmAttributeAccessor(ormMapping.getAttributeAccessor(), oxmAccessor);
                        if(ormMapping.isOneToOneMapping() && ((OneToOneMapping)ormMapping).usesIndirection()) {
                            newAccessor.setValueHolderProperty(true);
                        }
                        newAccessor.setChangeTracking(ormDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
                        oxmMapping.setAttributeAccessor(newAccessor);
                        
                        //check to see if we need to deal with containerAccessor
                        AttributeAccessor containerAccessor = null;
                        Class containerClass = null;
                        if(oxmMapping instanceof XMLCompositeObjectMapping) {
                            containerAccessor = ((XMLCompositeObjectMapping)oxmMapping).getInverseReferenceMapping().getAttributeAccessor();
                            containerClass = ((XMLCompositeObjectMapping)oxmMapping).getReferenceClass();
                        } else if(oxmMapping instanceof XMLCompositeCollectionMapping) {
                            containerAccessor = ((XMLCompositeCollectionMapping)oxmMapping).getInverseReferenceMapping().getAttributeAccessor();
                            containerClass = ((XMLCompositeCollectionMapping)oxmMapping).getReferenceClass();
                        }
                        if(containerAccessor != null) {
                            ClassDescriptor containerDescriptor = ormSession.getDescriptor(containerClass);
                            if(containerDescriptor != null) {
                                DatabaseMapping ormContainerMapping = containerDescriptor.getMappingForAttributeName(containerAccessor.getAttributeName());
                                if(ormContainerMapping != null) {
                                    //Check for indirection on the container mapping
                                    OrmAttributeAccessor ormAccessor = new OrmAttributeAccessor(ormContainerMapping.getAttributeAccessor(), containerAccessor);
                                    ormAccessor.setChangeTracking(containerDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
                                    ormAccessor.setValueHolderProperty(ormContainerMapping instanceof OneToOneMapping && ((OneToOneMapping)ormContainerMapping).usesIndirection());
                                    if(oxmMapping instanceof XMLCompositeObjectMapping) {
                                        ((XMLCompositeObjectMapping)oxmMapping).getInverseReferenceMapping().setAttributeAccessor(ormAccessor);
                                    } else if(oxmMapping instanceof XMLCompositeCollectionMapping) {
                                        ((XMLCompositeCollectionMapping)oxmMapping).getInverseReferenceMapping().setAttributeAccessor(ormAccessor);
                                    }
                                    
                                }
                            }
                        }
                    }
                }
                Iterator<DatabaseMapping> oxmMappingsIterator = oxmDescriptor.getMappings().iterator();
                while(oxmMappingsIterator.hasNext()) {
                    //iterate over the oxm mappings. Any ReferenceMappings that have a 
                    //collection as a backpointer, check to see if the container policy
                    //needs to be matched with the ORM project
                    DatabaseMapping nextMapping = oxmMappingsIterator.next();
                    if(nextMapping instanceof XMLObjectReferenceMapping) {
                        XMLObjectReferenceMapping refMapping = (XMLObjectReferenceMapping)nextMapping;
                        if(refMapping.getInverseReferenceMapping().getAttributeAccessor() != null && refMapping.getInverseReferenceMapping().getContainerPolicy() != null) {
                            ClassDescriptor refDescriptor = ormSession.getClassDescriptor(refMapping.getReferenceClass());
                            if(refDescriptor != null) {
                                DatabaseMapping backpointerMapping =refDescriptor.getMappingForAttributeName(refMapping.getInverseReferenceMapping().getAttributeName());
                                if(backpointerMapping != null && backpointerMapping.isCollectionMapping()) {
                                    refMapping.getInverseReferenceMapping().getContainerPolicy().setContainerClass(((CollectionMapping)backpointerMapping).getContainerPolicy().getContainerClass());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>Query the object model based on the corresponding XML document.  The following pairings are equivalent:</p> 
     * 
     * <i>Return the Customer's ID</i>
     * <pre> Integer id = xmlContext.getValueByXPath(customer, "@id", null, Integer.class);
     * Integer id = customer.getId();</pre>
     * 
     * <i>Return the Customer's Name</i>
     * <pre> String name = xmlContext.getValueByXPath(customer, "ns:personal-info/ns:name/text()", null, String.class);
     * String name = customer.getName();</pre>
     * 
     * <i>Return the Customer's Address</i>
     * <pre> Address address = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:address", aNamespaceResolver, Address.class);
     * Address address = customer.getAddress();</pre>
     * 
     * <i>Return all the Customer's PhoneNumbers</i> 
     * <pre> List phoneNumbers = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:phone-number", aNamespaceResolver, List.class);
     * List phoneNumbers = customer.getPhoneNumbers();</pre>
     * 
     * <i>Return the Customer's second PhoneNumber</i>
     * <pre> PhoneNumber phoneNumber = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:phone-number[2]", aNamespaceResolver, PhoneNumber.class);
     * PhoneNumber phoneNumber = customer.getPhoneNumbers().get(1);</pre>
     * 
     * <i>Return the base object</i>
     * <pre> Customer customer = xmlContext.getValueByXPath(customer, ".", aNamespaceResolver, Customer.class);
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
    public <T> T getValueByXPath(Object object, String xPath, NamespaceResolver namespaceResolver, Class<T> returnType) { 
        if(null == xPath) { 
            return null; 
         } 
        if(".".equals(xPath)) { 
           return (T) object; 
        } 
        Session session = this.getSession(object); 
        XMLDescriptor xmlDescriptor = (XMLDescriptor) session.getDescriptor(object); 
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, "/");
        T value = getValueByXPath(object, xmlDescriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, returnType);
        if(null == value) {
            DatabaseMapping selfMapping = xmlDescriptor.getObjectBuilder().getMappingForField(new XMLField("."));
            if(null != selfMapping) {
                return getValueByXPath(selfMapping.getAttributeValueFromObject(object), selfMapping.getReferenceDescriptor().getObjectBuilder(), new StringTokenizer(xPath, "/"), ((XMLDescriptor) selfMapping.getReferenceDescriptor()).getNamespaceResolver(), returnType);
            }
        }
        return value;
    } 
 
    private <T> T getValueByXPath(Object object, ObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NamespaceResolver namespaceResolver, Class<T> returnType) {
        if(null == object) {
            return null;
        }
        String xPath = ""; 
        XMLField xmlField = new XMLField(); 
        xmlField.setNamespaceResolver(namespaceResolver); 
        while(stringTokenizer.hasMoreElements()) {
            String nextToken = stringTokenizer.nextToken();
            xmlField.setXPath(xPath + nextToken);
            xmlField.initialize();
            DatabaseMapping mapping = objectBuilder.getMappingForField(xmlField); 
            if(null == mapping) {
                XPathFragment xPathFragment = new XPathFragment(nextToken);
                if(xPathFragment.getIndexValue() > 0) {
                    xmlField.setXPath(xPath + nextToken.substring(0, nextToken.indexOf('[')));
                    xmlField.initialize();
                    mapping = objectBuilder.getMappingForField(xmlField);
                    if(null != mapping) {
                        if(mapping.isCollectionMapping()) {
                            if(mapping.getContainerPolicy().isListPolicy()) {
                                Object childObject = ((ListContainerPolicy) mapping.getContainerPolicy()).get(xPathFragment.getIndexValue() - 1, mapping.getAttributeValueFromObject(object), null);
                                if(stringTokenizer.hasMoreElements()) {
                                    ObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder(); 
                                    return getValueByXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver, returnType); 
                                } else {
                                    return (T) childObject;
                                }
                            }
                        }
                    }
                }
            } else {
                if(stringTokenizer.hasMoreElements()) { 
                    Object childObject = mapping.getAttributeValueFromObject(object); 
                    ObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder(); 
                    return getValueByXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver, returnType); 
                } else { 
                    return (T) mapping.getAttributeValueFromObject(object); 
                } 
            } 
            xPath = xPath + nextToken + "/"; 
        }
        return null; 
    } 

    /**
     * <p>Set values in the object model based on the corresponding XML document.  The following pairings are equivalent:</p> 
     * 
     * <i>Set the Customer's ID</i>
     * <pre> xmlContext.setValueByXPath(customer, "@id", null, new Integer(123));
     * customer.setId(new Integer(123));</pre>
     * 
     * <i>Set the Customer's Name</i>
     * <pre> xmlContext.setValueByXPath(customer, "ns:personal-info/ns:name/text()", aNamespaceResolver, "Jane Doe");
     * customer.setName("Jane Doe");</pre>
     * 
     * <i>Set the Customer's Address</i>
     * <pre> xmlContext.setValueByXPath(customer, "ns:contact-info/ns:address", aNamespaceResolver, anAddress);
     * customer.setAddress(anAddress);</pre>
     * 
     * <i>Set the Customer's PhoneNumbers</i> 
     * <pre> xmlContext.setValueByXPath(customer, "ns:contact-info/ns:phone-number", aNamespaceResolver, phoneNumbers);
     * customer.setPhoneNumbers(phoneNumbers);</pre>
     * 
     * <i>Set the Customer's second PhoneNumber</i>
     * <pre> xmlContext.setValueByXPath(customer, "ns:contact-info/ns:phone-number[2]", aNamespaceResolver, aPhoneNumber);
     * customer.getPhoneNumbers().get(1);</pre>
     * 
     * @param object  The XPath will be executed relative to this object.
     * @param xPath The XPath statement
     * @param namespaceResolver A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param value The value to be set.
     */
    public void setValueByXPath(Object object, String xPath, NamespaceResolver namespaceResolver, Object value) { 
        Session session = this.getSession(object); 
        XMLDescriptor xmlDescriptor = (XMLDescriptor) session.getDescriptor(object); 
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, "/"); 
        setValueByXPath(object, xmlDescriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, value); 
    } 
 
    private void setValueByXPath(Object object, ObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NamespaceResolver namespaceResolver, Object value) { 
        String xPath = ""; 
        XMLField xmlField = new XMLField(); 
        xmlField.setNamespaceResolver(namespaceResolver); 
        while(stringTokenizer.hasMoreElements()) { 
            String nextToken = stringTokenizer.nextToken();
            xmlField.setXPath(xPath + nextToken); 
            xmlField.initialize();
            DatabaseMapping mapping = objectBuilder.getMappingForField(xmlField); 
            if(null == mapping) {
                XPathFragment xPathFragment = new XPathFragment(nextToken);
                if(xPathFragment.getIndexValue() > 0) {
                    xmlField.setXPath(xPath + nextToken.substring(0, nextToken.indexOf('[')));
                    xmlField.initialize();
                    mapping = objectBuilder.getMappingForField(xmlField);
                    if(null != mapping) {
                        if(mapping.isCollectionMapping()) {
                            if(mapping.getContainerPolicy().isListPolicy()) {
                                if(stringTokenizer.hasMoreElements()) {
                                    Object childObject = ((ListContainerPolicy) mapping.getContainerPolicy()).get(xPathFragment.getIndexValue() - 1, mapping.getAttributeValueFromObject(object), null);
                                    ObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder();
                                    setValueByXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver, value);
                                    return;
                                } else {
                                    List list = (List) mapping.getAttributeValueFromObject(object);
                                    list.add(xPathFragment.getIndexValue() - 1, value);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else {
                if(stringTokenizer.hasMoreElements()) { 
                    Object childObject = mapping.getAttributeValueFromObject(object); 
                    ObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder(); 
                    setValueByXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver, value); 
                    return; 
                } else { 
                    mapping.setAttributeValueInObject(object, value); 
                    return; 
                } 
            } 
            xPath = xPath + nextToken + "/"; 
        } 
    } 

    /**
     * Create a new object instance for a given XML namespace and name.
     *
     * @param namespace
     *      The namespace of the complex type to create a new Java instance of.
     * @param typeName
     *      The XML type name to create a new Java instance of.
     * @param isGlobalType
     *      True if the object to be created represents a global type, false if it 
     *      represents a global element.
     *
     * @return
     *      An instance of the Java class mapped to the indicated XML type, or null
     *      if no result was found.
     */
    public Object createByQualifiedName(String namespace, String typeName, boolean isGlobalType) throws IllegalArgumentException {
        QName qName = new QName(namespace, typeName);

        XMLDescriptor d = null;
        if (!isGlobalType) {
            d = getDescriptor(qName);
        } else {
            XPathFragment frag = new XPathFragment();
            frag.setNamespaceURI(namespace);
            frag.setLocalName(typeName);

            d = getDescriptorByGlobalType(frag);
        }

        if (d == null) {
            return null;
        }

        return d.getInstantiationPolicy().buildNewInstance();
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
     *      An instance of the Java class mapped to the supplied XML type, or null
     *      if no result was found.
     */
    public <T> T createByXPath(Object parentObject, String xPath, NamespaceResolver namespaceResolver, Class<T> returnType) {
        Session session = this.getSession(parentObject);
        XMLDescriptor xmlDescriptor = (XMLDescriptor) session.getDescriptor(parentObject);
        StringTokenizer stringTokenizer = new StringTokenizer(xPath, "/");
        return createByXPath(parentObject, xmlDescriptor.getObjectBuilder(), stringTokenizer, namespaceResolver, returnType);
    }

    private <T> T createByXPath(Object object, ObjectBuilder objectBuilder, StringTokenizer stringTokenizer, NamespaceResolver namespaceResolver, Class<T> returnType) {
        String xPath = "";
        XMLField xmlField = new XMLField();
        xmlField.setNamespaceResolver(namespaceResolver);
        while (stringTokenizer.hasMoreElements()) {
            String nextToken = stringTokenizer.nextToken();
            xmlField.setXPath(xPath + nextToken);
            xmlField.initialize();
            DatabaseMapping mapping = objectBuilder.getMappingForField(xmlField);
            if (null == mapping) {
                XPathFragment xPathFragment = new XPathFragment(nextToken);
                if (xPathFragment.getIndexValue() > 0) {
                    xmlField.setXPath(xPath + nextToken.substring(0, nextToken.indexOf('[')));
                    xmlField.initialize();
                    mapping = objectBuilder.getMappingForField(xmlField);
                    if (null != mapping) {
                        return (T) mapping.getReferenceDescriptor().getInstantiationPolicy().buildNewInstance();
                    }
                }
            } else {
                if (stringTokenizer.hasMoreElements()) {
                    Object childObject = mapping.getAttributeValueFromObject(object);
                    ObjectBuilder childObjectBuilder = mapping.getReferenceDescriptor().getObjectBuilder();
                    return createByXPath(childObject, childObjectBuilder, stringTokenizer, namespaceResolver, returnType);
                } else {
                    return (T) mapping.getReferenceDescriptor().getInstantiationPolicy().buildNewInstance();
                }
            }
        }
        return null;
    }

    public static class XMLContextState {

        private XMLContext xmlContext;
        private List sessions;
        private Map descriptorsByQName;
        private Map descriptorsByGlobalType;
        private boolean hasDocumentPreservation = false;

        private XMLContextState(XMLContext xmlContext, Collection projects, ClassLoader classLoader) {
            this.xmlContext = xmlContext;
            Iterator iterator = projects.iterator();
            sessions = new ArrayList(projects.size());
            descriptorsByQName = new HashMap();
            descriptorsByGlobalType = new HashMap();
            while(iterator.hasNext()) {
                Project project = (Project)iterator.next();
                if ((project.getDatasourceLogin() == null) || !(project.getDatasourceLogin().getDatasourcePlatform() instanceof XMLPlatform)) {
                    XMLPlatform platform = new SAXPlatform();
                    platform.getConversionManager().setLoader(classLoader);
                    project.setLogin(new XMLLogin(platform));
                }
                DatabaseSession session = project.createDatabaseSession();

                // turn logging for this session off and leave the global session up
                // Note: setting level to SEVERE or WARNING will printout stacktraces for expected exceptions
                session.setLogLevel(SessionLog.OFF);
                // don't turn off global static logging
                //AbstractSessionLog.getLog().log(AbstractSessionLog.INFO, "ox_turn_global_logging_off", getClass());
                //AbstractSessionLog.getLog().setLevel(AbstractSessionLog.OFF);
                setupDocumentPreservationPolicy(session);
                session.login();
                sessions.add(session);
                storeXMLDescriptorsByQName(session);
            }
        }

        private XMLContextState(XMLContext xmlContext, Project project, ClassLoader classLoader, SessionEventListener sessionEventListener) {
            this.xmlContext = xmlContext;
            if ((project.getDatasourceLogin() == null) || !(project.getDatasourceLogin().getDatasourcePlatform() instanceof XMLPlatform)) {
                XMLPlatform platform = new SAXPlatform();
                platform.getConversionManager().setLoader(classLoader);
                project.setLogin(new XMLLogin(platform));
            }
            sessions = new ArrayList(1);
            DatabaseSession session = project.createDatabaseSession();
            
            // if an event listener was passed in as a parameter, register it with the event manager
            if (sessionEventListener != null) {
                session.getEventManager().addListener(sessionEventListener);
            }

            // turn logging for this session off and leave the global session up
            // Note: setting level to SEVERE or WARNING will printout stacktraces for expected exceptions
            session.setLogLevel(SessionLog.OFF);
            // don't turn off global static logging
            //AbstractSessionLog.getLog().log(AbstractSessionLog.INFO, "ox_turn_global_logging_off", getClass());
            //AbstractSessionLog.getLog().setLevel(AbstractSessionLog.OFF);
            setupDocumentPreservationPolicy(session);

            session.login();
            sessions.add(session);
            descriptorsByQName = new HashMap();
            descriptorsByGlobalType = new HashMap();
            storeXMLDescriptorsByQName(session);
        }

        private XMLContextState(XMLContext xmlContext, String sessionNames, ClassLoader classLoader, String xmlResource) {
            this.xmlContext = xmlContext;
            XMLSessionConfigLoader loader = null;
            if (xmlResource != null) {
                loader = new XMLSessionConfigLoader(xmlResource);
            } else {
                loader = new XMLSessionConfigLoader();
            }
            descriptorsByQName = new HashMap();
            descriptorsByGlobalType = new HashMap();
            StringTokenizer st = new StringTokenizer(sessionNames, ":");
            sessions = new ArrayList(st.countTokens());
            int index = 0;
            while (st.hasMoreTokens()) {
                sessions.add(buildSession(st.nextToken(), classLoader, loader));
                index++;
            }
            for (int x = index - 1; x >= 0; x--) {
                storeXMLDescriptorsByQName((DatabaseSession) sessions.get(x));
            }
        }

        private void addDescriptorByQName(QName qName, XMLDescriptor descriptor) {
            descriptorsByQName.put(qName, descriptor);
        }

        /**
         * INTERNAL: Add and initialize a new session to the list of sessions
         * associated with this XMLContext.
         */
        private void addSession(DatabaseSession sessionToAdd) {
            if ((sessionToAdd.getDatasourceLogin() == null) || !(sessionToAdd.getDatasourceLogin().getDatasourcePlatform() instanceof XMLPlatform)) {
                XMLPlatform platform = new SAXPlatform();
                sessionToAdd.setLogin(new XMLLogin(platform));
            }
            DatabaseSession session = sessionToAdd.getProject().createDatabaseSession();
            if (sessionToAdd.getEventManager().hasListeners()) {
                List listeners = sessionToAdd.getEventManager().getListeners();
                int listenersSize = listeners.size();
                for (int x = 0; x < listenersSize; x++) {
                    session.getEventManager().addListener((SessionEventListener) listeners.get(x));
                }
            }
            session.setExceptionHandler(sessionToAdd.getExceptionHandler());
            session.setLogLevel(SessionLog.OFF);
            this.setupDocumentPreservationPolicy(session);
            session.login();
            sessions.add(session);

            storeXMLDescriptorsByQName(session);
        }

        private DatabaseSession buildSession(String sessionName, ClassLoader classLoader, XMLSessionConfigLoader sessionLoader) throws XMLMarshalException {
            DatabaseSession dbSession;
            if (classLoader != null) {
                dbSession = (DatabaseSession) SessionManager.getManager().getSession(sessionLoader, sessionName, classLoader, false, true);
            } else {
                dbSession = (DatabaseSession) SessionManager.getManager().getSession(sessionLoader, sessionName, PrivilegedAccessHelper.privilegedGetClassLoaderForClass(this.getClass()), false, false, false);
            }
            if ((dbSession.getDatasourceLogin() == null) || !(dbSession.getDatasourceLogin().getDatasourcePlatform() instanceof XMLPlatform)) {
                XMLPlatform platform = new SAXPlatform();
                dbSession.setLogin(new XMLLogin(platform));
            }
            DatabaseSession session = dbSession.getProject().createDatabaseSession();
            if (dbSession.getEventManager().hasListeners()) {
                List listeners = dbSession.getEventManager().getListeners();
                int listenersSize = listeners.size();
                for (int x = 0; x < listenersSize; x++) {
                    session.getEventManager().addListener((SessionEventListener) listeners.get(x));
                }
            }
            session.setExceptionHandler(dbSession.getExceptionHandler());
            session.setLogLevel(SessionLog.OFF);
            setupDocumentPreservationPolicy(session);
            session.login();
            return session;
        }

        /**
         * INTERNAL: Return the XMLDescriptor with the default root mapping matching
         * the QName parameter.
         */
        private XMLDescriptor getDescriptor(QName qName) {
            return (XMLDescriptor) descriptorsByQName.get(qName);
        }

        /**
         * INTERNAL: Return the XMLDescriptor mapped to the global type matching the
         * XPathFragment parameter.
         */
        private XMLDescriptor getDescriptorByGlobalType(XPathFragment xPathFragment) {
            return (XMLDescriptor) this.descriptorsByGlobalType.get(xPathFragment);
        }

        /**
         * INTERNAL:
         * Return the session corresponding to this class. Since the class
         * may be mapped by more that one of the projects used to create the XML
         * Context, this method will return the first match.
         *
         * The session will be a unit of work if document preservation is not
         * enabled.  This method will typically  be used for unmarshalling
         * when a non-shared cache is desired.
         */
        private AbstractSession getReadSession(Class clazz) {
            if (null == clazz) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                XMLDescriptor xmlDescriptor = (XMLDescriptor) next.getDescriptor(clazz);
                if (xmlDescriptor != null) {
                    // we don't currently support document preservation
                    // and non-shared cache (via unit of work)
                    //if (!documentPreservationPolicy.shouldPreserveDocument()) {
                    next = next.acquireUnitOfWork();
                    //}
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
        }

        /**
         * INTERNAL:
         * Return the session corresponding to this XMLDescriptor. Since
         * the class may be mapped by more that one of the projects used to create
         * the XML Context, this method will return the first match.
         *
         * The session will be a unit of work if document preservation is not
         * enabled.  This method will typically  be used for unmarshalling
         * when a non-shared cache is desired.
         */
        private AbstractSession getReadSession(XMLDescriptor xmlDescriptor) {
            if (null == xmlDescriptor) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getProject().getOrderedDescriptors().contains(xmlDescriptor)) {
                    // we don't currently support document preservation
                    // and non-shared cache (via unit of work)
                    //if (!documentPreservationPolicy.shouldPreserveDocument()) {
                    next = next.acquireUnitOfWork();
                    //}
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(xmlDescriptor.getJavaClass().getName());
        }

        /**
         * INTERNAL: Return the session corresponding to this object. Since the
         * object may be mapped by more that one of the projects used to create the
         * XML Context, this method will return the first match.
         *
         * The session will be a unit of work if document preservation is not
         * enabled.  This method will typically  be used for unmarshalling
         * when a non-shared cache is desired.
         */
        private AbstractSession getReadSession(Object object) {
            if (null == object) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                XMLDescriptor xmlDescriptor = (XMLDescriptor) next.getDescriptor(object);
                if (xmlDescriptor != null) {
                    // we don't currently support document preservation
                    // and non-shared cache (via unit of work)
                    //if (!documentPreservationPolicy.shouldPreserveDocument()) {
                    next = next.acquireUnitOfWork();
                    //}
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        /**
         * INTERNAL: Return the session corresponding to this class. Since the class
         * may be mapped by more that one of the projects used to create the XML
         * Context, this method will return the first match.
         */
        private AbstractSession getSession(Class clazz) {
            if (null == clazz) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getDescriptor(clazz) != null) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
        }

        /**
         * INTERNAL: <code>
         * XMLContext xmlContext = new XMLContext("path0:path1");<br>
         * DatabaseSession session = xmlContext.getSession(0);  // returns session for path0<br>
         * </code>
         */
        private DatabaseSession getSession(int index) {
            if (null == sessions) {
                return null;
            }
            return (DatabaseSession) sessions.get(index);
        }

        /**
         * INTERNAL: Return the session corresponding to this object. Since the
         * object may be mapped by more that one of the projects used to create the
         * XML Context, this method will return the first match.
         */
        public AbstractSession getSession(Object object) {
            if (null == object) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getDescriptor(object) != null) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        /**
         * INTERNAL: Return the session corresponding to this XMLDescriptor. Since
         * the class may be mapped by more that one of the projects used to create
         * the XML Context, this method will return the first match.
         */
        private AbstractSession getSession(XMLDescriptor xmlDescriptor) {
            if (null == xmlDescriptor) {
                return null;
            }
            int numberOfSessions = sessions.size();
            for (int x = 0; x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getProject().getOrderedDescriptors().contains(xmlDescriptor)) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(xmlDescriptor.getJavaClass().getName());
        }

        /**
         * INTERNAL: Return the EclipseLink session used to marshal.
         */
        private List getSessions() {
            return sessions;
        }

        /**
         * INTERNAL:
         * Return true if any session held onto by this context has a document preservation
         * policy that requires unmarshalling from a Node.
         */
        private boolean hasDocumentPreservation() {
            return this.hasDocumentPreservation;
        }

        private void setupDocumentPreservationPolicy(DatabaseSession session) {
            XMLLogin login = (XMLLogin) session.getDatasourceLogin();
            if (login.getDocumentPreservationPolicy() == null) {
                Iterator iterator = session.getProject().getOrderedDescriptors().iterator();
                while (iterator.hasNext()) {
                    XMLDescriptor xmlDescriptor = (XMLDescriptor) iterator.next();
                    if (xmlDescriptor.shouldPreserveDocument()) {
                        login.setDocumentPreservationPolicy(new DescriptorLevelDocumentPreservationPolicy());
                        break;
                    }
                }
            }
            if (login.getDocumentPreservationPolicy() == null) {
                login.setDocumentPreservationPolicy(new NoDocumentPreservationPolicy());
            }
            
            login.getDocumentPreservationPolicy().initialize(xmlContext);

            if (login.getDocumentPreservationPolicy().shouldPreserveDocument() && !hasDocumentPreservation) {
                hasDocumentPreservation = true;
            }
        }

        /**
         * INTERNAL:
         */
        private void storeXMLDescriptorByQName(XMLDescriptor xmlDescriptor) {
            QName descriptorQName;
            String defaultRootName;

            List tableNames = xmlDescriptor.getTableNames();
            for (int i = 0; i < tableNames.size(); i++) {
                defaultRootName = (String) tableNames.get(i);

                if (null != defaultRootName) {
                    int index = defaultRootName.indexOf(':');
                    String defaultRootLocalName = defaultRootName.substring(index + 1);
                    if(defaultRootLocalName != null && !(defaultRootLocalName.equals(XMLConstants.EMPTY_STRING))){
                        if (index > -1) {
                            String defaultRootPrefix = defaultRootName.substring(0, index);
                            String defaultRootNamespaceURI = xmlDescriptor.getNamespaceResolver().resolveNamespacePrefix(defaultRootPrefix);
                            descriptorQName = new QName(defaultRootNamespaceURI, defaultRootLocalName);
                        } else {
                            if(xmlDescriptor.getNamespaceResolver() != null) {
                                descriptorQName = new QName(xmlDescriptor.getNamespaceResolver().getDefaultNamespaceURI(), defaultRootLocalName);
                            } else {
                                descriptorQName = new QName(defaultRootLocalName);
                            }
                        }
                        if (!xmlDescriptor.hasInheritance() || xmlDescriptor.getInheritancePolicy().isRootParentDescriptor()) {
                            descriptorsByQName.put(descriptorQName, xmlDescriptor);
                        } else {
                            //this means we have a descriptor that is a child in an inheritance hierarchy
                            storeXMLDescriptorByQName((XMLDescriptor) xmlDescriptor.getInheritancePolicy().getParentDescriptor());
                            XMLDescriptor existingDescriptor = (XMLDescriptor) descriptorsByQName.get(descriptorQName);
                            if (existingDescriptor == null) {
                                descriptorsByQName.put(descriptorQName, xmlDescriptor);
                            }
                        }
                    }
                }
            }

            XMLSchemaReference xmlSchemaReference = xmlDescriptor.getSchemaReference();
            if (null != xmlSchemaReference) {
                String schemaContext = xmlSchemaReference.getSchemaContext();
                if ((xmlSchemaReference.getType() == XMLSchemaReference.COMPLEX_TYPE) || (xmlSchemaReference.getType() == XMLSchemaReference.SIMPLE_TYPE)) {
                    if ((null != schemaContext) && (schemaContext.lastIndexOf('/') == 0)) {
                        schemaContext = schemaContext.substring(1, schemaContext.length());
                        XPathFragment typeFragment = new XPathFragment(schemaContext);
                        if (null != xmlDescriptor.getNamespaceResolver()) {
                           String uri = xmlDescriptor.getNamespaceResolver().resolveNamespacePrefix(typeFragment.getPrefix());
                           if(uri == null && xmlSchemaReference.getSchemaContextAsQName() != null){
                               uri = xmlSchemaReference.getSchemaContextAsQName().getNamespaceURI();
                           }
                           typeFragment.setNamespaceURI(uri);
                        }
                        this.descriptorsByGlobalType.put(typeFragment, xmlDescriptor);
                    } else {
                        QName qname = xmlSchemaReference.getSchemaContextAsQName();
                        if (qname != null) {
                            if (xmlDescriptor.isWrapper() && xmlDescriptor.getJavaClassName().contains("ObjectWrapper")) {
                                return;
                            }
                            XPathFragment typeFragment = new XPathFragment();
                            typeFragment.setLocalName(qname.getLocalPart());
                            typeFragment.setNamespaceURI(qname.getNamespaceURI());
                            this.descriptorsByGlobalType.put(typeFragment, xmlDescriptor);
                        }
                    }
                }
            }
        }

        private void storeXMLDescriptorsByQName(DatabaseSession session) {
            Iterator iterator = session.getProject().getOrderedDescriptors().iterator();
            while (iterator.hasNext()) {
                XMLDescriptor xmlDescriptor = (XMLDescriptor) iterator.next();
                storeXMLDescriptorByQName(xmlDescriptor);
            }
        }

    }

}