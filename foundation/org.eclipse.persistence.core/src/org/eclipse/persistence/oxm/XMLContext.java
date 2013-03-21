/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Context;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.accessor.OrmAttributeAccessor;
import org.eclipse.persistence.internal.oxm.documentpreservation.DescriptorLevelDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.NoDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
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
public class XMLContext extends Context<AbstractSession, XMLDescriptor, XMLField, NamespaceResolver, Project, DatabaseSession, SessionEventListener> {


    XMLContext(XMLContextState xmlContextState) {
        this.contextState = xmlContextState;
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
        contextState = new XMLContextState(this, sessionNames, classLoader, xmlResource);
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
        this (project, classLoader, (Collection<SessionEventListener>) null);
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
        Collection<SessionEventListener> sessionEventListeners = new ArrayList<SessionEventListener>(1);
        sessionEventListeners.add(sessionEventListener);
        contextState = new XMLContextState(this, project, classLoader, sessionEventListeners);
    }

    /**
     * Create a new XMLContext based on the specified Project and ClassLoader.
     *
     * @param project An EclipseLink project
     * @param classLoader The ClassLoader to be used 
     * @param sessionEventListeners If non-null, these listeners will be registered with the SessionEventManager
     * @see SessionEventListener
     * @see SessionEventManager
     */
    public XMLContext(Project project, ClassLoader classLoader, Collection<SessionEventListener> sessionEventListeners) {
        contextState = new XMLContextState(this, project, classLoader, sessionEventListeners);
    }

    public XMLContext(Collection projects) {
    	this(projects, Thread.currentThread().getContextClassLoader());
    }
    
    public XMLContext(Collection projects, ClassLoader classLoader) {
        contextState = new XMLContextState(this, projects, classLoader);
    }

    /**
     * INTERNAL:  Return the XMLContextState that represents the XMLContexts 
     * stateful information.  This method is provided for the benefits of layers
     * that build on top of the core OXM layer such as MOXy's JAXB
     * implementation.
     */
    public XMLContextState getXMLContextState() {
        return (XMLContextState) contextState;
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
            this.contextState = xcs;
        }
    }

    /**
     * INTERNAL: Add and initialize a new session to the list of sessions
     * associated with this XMLContext.
     */
    public void addSession(DatabaseSession sessionToAdd) {
        getXMLContextState().addSession(sessionToAdd);
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
        return getSession(object);
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
        return super.getSession(clazz);
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
        return super.getSession(xmlDescriptor);
    }

    /**
     * INTERNAL: Return the EclipseLink session used to marshal.
     */
    public List getSessions() {
        return getXMLContextState().getSessions();
    }

    /**
     * INTERNAL: <code>
     * XMLContext xmlContext = new XMLContext("path0:path1");<br>
     * DatabaseSession session = xmlContext.getSession(0);  // returns session for path0<br>
     * </code>
     */
    public DatabaseSession getSession(int index) {
        return getXMLContextState().getSession(index);
    }

    /**
     * INTERNAL: Return the session corresponding to this object. Since the
     * object may be mapped by more that one of the projects used to create the
     * XML Context, this method will return the first match.
     */
    public AbstractSession getSession(Object object) {
        return super.getSession(object);
    }

    /**
     * INTERNAL: Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the XML
     * Context, this method will return the first match.
     */
    public AbstractSession getSession(Class clazz) {
        return super.getSession(clazz);
    }

    /**
     * INTERNAL: Return the session corresponding to this XMLDescriptor. Since
     * the class may be mapped by more that one of the projects used to create
     * the XML Context, this method will return the first match.
     */
    public AbstractSession getSession(XMLDescriptor xmlDescriptor) {
        return super.getSession(xmlDescriptor);
    }

    /**
     * INTERNAL:
     */
    public void storeXMLDescriptorByQName(XMLDescriptor xmlDescriptor) {
        contextState.storeDescriptorByQName(xmlDescriptor, null);
    }

    /**
     * INTERNAL: Return the XMLDescriptor with the default root mapping matching
     * the QName parameter.
     */
    public XMLDescriptor getDescriptor(QName qName) {
        return super.getDescriptor(qName);
    }

    /**
     * INTERNAL: Return the XMLDescriptor with the default root mapping matching
     * the QName parameter.
     */
    public XMLDescriptor getDescriptor(XPathQName xpathQName) {
    	return super.getDescriptor(xpathQName);
    }
    
    public void addDescriptorByQName(QName qName, XMLDescriptor descriptor) {
        contextState.addDescriptorByQName(qName, descriptor);
    }
    
    /**
     * INTERNAL: Return the XMLDescriptor mapped to the global type matching the
     * XPathFragment parameter.
     */
    public XMLDescriptor getDescriptorByGlobalType(XPathFragment xPathFragment) {
        return super.getDescriptorByGlobalType(xPathFragment);
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
        getXMLContextState().setupDocumentPreservationPolicy(session);
    }

    /**
     * INTERNAL:
     * Return true if any session held onto by this context has a document preservation
     * policy that requires unmarshalling from a Node.
     */
    public boolean hasDocumentPreservation() {
        return getXMLContextState().hasDocumentPreservation();
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
                    Mapping oxmMapping = (Mapping) oxmDescriptor.getMappingForAttributeName(ormMapping.getAttributeName());
                    if(oxmMapping != null) {
                        CoreAttributeAccessor oxmAccessor = oxmMapping.getAttributeAccessor();
                        OrmAttributeAccessor newAccessor = new OrmAttributeAccessor(ormMapping.getAttributeAccessor(), oxmAccessor);
                        if(ormMapping.isOneToOneMapping() && ((OneToOneMapping)ormMapping).usesIndirection()) {
                            newAccessor.setValueHolderProperty(true);
                        }
                        newAccessor.setChangeTracking(ormDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
                        oxmMapping.setAttributeAccessor(newAccessor);
                        
                        //check to see if we need to deal with containerAccessor
                        CoreAttributeAccessor containerAccessor = null;
                        Class containerClass = null;
                        if(oxmMapping.isAbstractCompositeObjectMapping()) {
                            containerAccessor = ((CompositeObjectMapping)oxmMapping).getInverseReferenceMapping().getAttributeAccessor();
                            containerClass = ((CompositeObjectMapping)oxmMapping).getReferenceClass();
                        } else if(oxmMapping.isAbstractCompositeCollectionMapping()) {
                            containerAccessor = ((CompositeCollectionMapping)oxmMapping).getInverseReferenceMapping().getAttributeAccessor();
                            containerClass = ((CompositeCollectionMapping)oxmMapping).getReferenceClass();
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
                                    if(oxmMapping.isAbstractCompositeObjectMapping()) {
                                        ((CompositeObjectMapping)oxmMapping).getInverseReferenceMapping().setAttributeAccessor(ormAccessor);
                                    } else if(oxmMapping.isAbstractCompositeCollectionMapping()) {
                                        ((CompositeCollectionMapping)oxmMapping).getInverseReferenceMapping().setAttributeAccessor(ormAccessor);
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
                    if(nextMapping instanceof ObjectReferenceMapping) {
                        ObjectReferenceMapping refMapping = (ObjectReferenceMapping)nextMapping;
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
        return super.getValueByXPath(object, xPath, namespaceResolver, returnType);
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
        super.setValueByXPath(object, xPath, namespaceResolver, value);
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
        return super.createByXPath(parentObject, xPath, namespaceResolver, returnType);
    }


    private static class XMLContextState extends ContextState<AbstractSession, XMLDescriptor, Project, DatabaseSession, SessionEventListener> {

        private List<DatabaseSession> sessions;
        private boolean hasDocumentPreservation = false;

        protected XMLContextState(XMLContext xmlContext, Collection<Project> projects, ClassLoader classLoader) {
            this.context = xmlContext;
            sessions = new ArrayList(projects.size());
            Iterator<Project> iterator = projects.iterator();
            while (iterator.hasNext()) {
                Project project = iterator.next();
                preLogin(project, classLoader);
                DatabaseSession session = project.createDatabaseSession();

                // turn logging for this session off and leave the global
                // session up
                // Note: setting level to SEVERE or WARNING will printout
                // stacktraces for expected exceptions
                session.setLogLevel(SessionLog.OFF);
                // don't turn off global static logging
                // AbstractSessionLog.getLog().log(AbstractSessionLog.INFO,
                // "ox_turn_global_logging_off", getClass());
                // AbstractSessionLog.getLog().setLevel(AbstractSessionLog.OFF);
                setupSession(session);
                sessions.add(session);
                storeDescriptorsByQName(session);
            }
        }

        private XMLContextState(XMLContext xmlContext, String sessionNames, ClassLoader classLoader, String xmlResource) {
            super();
            this.context = xmlContext;
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
                storeDescriptorsByQName(sessions.get(x));
            }
        }

        protected XMLContextState(XMLContext xmlContext, Project project,
                ClassLoader classLoader,
                Collection<SessionEventListener> sessionEventListeners) {
            super(xmlContext, project, classLoader, sessionEventListeners);
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
                List<SessionEventListener> listeners = sessionToAdd.getEventManager().getListeners();
                int listenersSize = listeners.size();
                for (int x = 0; x < listenersSize; x++) {
                    session.getEventManager().addListener(listeners.get(x));
                }
            }
            session.setExceptionHandler(sessionToAdd.getExceptionHandler());
            session.setLogLevel(SessionLog.OFF);
            setupSession(session);
            if(null == sessions) {
                sessions = new ArrayList<DatabaseSession>();
            }
            if(null != this.session) {
                sessions.add(this.session);
            }
            sessions.add(session);

            storeDescriptorsByQName(session);
        }
 
        @Override
        protected void setupSession(DatabaseSession session) {
            session.login();
            setupDocumentPreservationPolicy(session);
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
                List<SessionEventListener> listeners = dbSession.getEventManager().getListeners();
                int listenersSize = listeners.size();
                for (int x = 0; x < listenersSize; x++) {
                    session.getEventManager().addListener(listeners.get(x));
                }
            }
            session.setExceptionHandler(dbSession.getExceptionHandler());
            session.setLogLevel(SessionLog.OFF);
            setupDocumentPreservationPolicy(session);
            session.login();
            return session;
        }

        @Override
        protected DatabaseSession getSession() {
            if(null == sessions) {
                return super.getSession();
            }
            return sessions.get(0);
        }

        /**
         * INTERNAL: Return the session corresponding to this class. Since the class
         * may be mapped by more that one of the projects used to create the XML
         * Context, this method will return the first match.
         */
        @Override
        protected AbstractSession getSession(Class clazz) {
            if (null == clazz) {
                return null;
            }
            if (null == sessions) {
                return super.getSession(clazz);
            }
            for (int x = 0, numberOfSessions = sessions.size(); x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getDescriptor(clazz) != null) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(clazz.getName());
        }

        /**
         * INTERNAL: Return the session corresponding to this XMLDescriptor. Since
         * the class may be mapped by more that one of the projects used to create
         * the XML Context, this method will return the first match.
         */
        @Override
        protected AbstractSession getSession(XMLDescriptor xmlDescriptor) {
            if (null == xmlDescriptor) {
                return null;
            }
            if(null == sessions) {
                return super.getSession(xmlDescriptor);
            }
            for (int x = 0, numberOfSessions = sessions.size(); x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getProject().getOrderedDescriptors().contains(xmlDescriptor)) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(xmlDescriptor.getJavaClass().getName());
        }

        private List<DatabaseSession> getSessions() {
            if(null == sessions) {
                return Collections.singletonList(session);
            }
            return sessions;
        }

        /**
         * INTERNAL: <code>
         * XMLContext xmlContext = new XMLContext("path0:path1");<br>
         * DatabaseSession session = xmlContext.getSession(0);  // returns session for path0<br>
         * </code>
         */
        private DatabaseSession getSession(int index) {
            if (null == sessions) {
                return session;
            }
            return sessions.get(index);
        }

        /**
         * INTERNAL: Return the session corresponding to this object. Since the
         * object may be mapped by more that one of the projects used to create the
         * XML Context, this method will return the first match.
         */
        @Override
        protected AbstractSession getSession(Object object) {
            if (null == object) {
                return null;
            }
            if (null == sessions) {
                return super.getSession(object);
            }
            for (int x = 0, numberOfSessions = sessions.size(); x < numberOfSessions; x++) {
                AbstractSession next = ((AbstractSession) sessions.get(x));
                if (next.getDescriptor(object) != null) {
                    return next;
                }
            }
            throw XMLMarshalException.descriptorNotFoundInProject(object.getClass().getName());
        }

        /**
         * INTERNAL:
         * Return true if any session held onto by this context has a document preservation
         * policy that requires unmarshalling from a Node.
         */
        private boolean hasDocumentPreservation() {
            return this.hasDocumentPreservation;
        }

        @Override
        protected void preLogin(Project project, ClassLoader classLoader) {
            if ((project.getDatasourceLogin() == null) || !(project.getDatasourceLogin().getDatasourcePlatform() instanceof XMLPlatform)) {
                XMLPlatform platform = new SAXPlatform();
                platform.getConversionManager().setLoader(classLoader);
                project.setLogin(new XMLLogin(platform));
            }
        }

        private void setupDocumentPreservationPolicy(DatabaseSession session) {
            XMLLogin login = (XMLLogin) session.getDatasourceLogin();
            if (login.getDocumentPreservationPolicy() == null) {
                Iterator iterator = session.getProject().getOrderedDescriptors().iterator();
                while (iterator.hasNext()) {
                    Descriptor xmlDescriptor = (Descriptor) iterator.next();
                    if (xmlDescriptor.shouldPreserveDocument()) {
                        login.setDocumentPreservationPolicy(new DescriptorLevelDocumentPreservationPolicy());
                        break;
                    }
                }
            }
            if (login.getDocumentPreservationPolicy() == null) {
                login.setDocumentPreservationPolicy(new NoDocumentPreservationPolicy());
            }
            
            login.getDocumentPreservationPolicy().initialize(context);

            if (login.getDocumentPreservationPolicy().shouldPreserveDocument() && !hasDocumentPreservation) {
                hasDocumentPreservation = true;
            }
        }

    }

    @Override
    protected XMLField createField(String path) {
        if(null == path) {
            return new XMLField();
        }
        return new XMLField(path);
    }

}