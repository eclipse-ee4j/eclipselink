/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.rs.eventlistener.ChangeListener;
import org.eclipse.persistence.jpa.rs.eventlistener.DatabaseEventListenerFactory;
import org.eclipse.persistence.jpa.rs.eventlistener.DescriptorBasedDatabaseEventListener;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;
import org.eclipse.persistence.jpa.rs.util.DynamicXMLMetadataSource;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.JTATransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.LinkAdapter;
import org.eclipse.persistence.jpa.rs.util.LinkMetadataSource;
import org.eclipse.persistence.jpa.rs.util.PreLoginMappingAdapter;
import org.eclipse.persistence.jpa.rs.util.RelationshipLinkAdapter;
import org.eclipse.persistence.jpa.rs.util.ResourceLocalTransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.TransactionWrapper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * A wrapper around the JPA and JAXB artifacts used to persist an application.
 * 
 * A PersistenceContext provides the capability of using the same persistence unit in JPA to
 * to interact with a Database or other JPA-capable data source and in JAXB to interact with either 
 * XML or JSON.
 * 
 * A PersistenceContext can wrap either an existing persistence unit (EntityManagerFactory), or it can be used to bootstrap a 
 * fully dynamic persistence unit.
 * 
 * @author douglas.clarke, tom.ware
 */
public class PersistenceContext {
    
    /** A factory class that will provide listeners for events provided by the database
     *  Setting this provides a hook to allow applications that are capable of receiving
     *  events from the database to do so.
     */
    public static DatabaseEventListenerFactory EVENT_LISTENER_FACTORY = null;
    
    /** This internal property is used to save a change listener on the session for later retreival.**/
    public static final String CHANGE_NOTIFICATION_LISTENER = "jpars.change-notification-listener";
    
    public static final String JPARS_CONTEXT = "eclipselink.jpars.context";


    /**
     * Static setter for the EVENT_LISTENER_FACTORY
     * Part of the mechanism for plugging in code that can react to database events
     * @param eventListenerFactory
     */
    public static void setEventListenerFactory(
            DatabaseEventListenerFactory eventListenerFactory) {
        EVENT_LISTENER_FACTORY = eventListenerFactory;
    }
    
    /**
     * The name of the persistence context is used to look it up. By default it will be the
     * persistence unit name of the JPA persistence unit.
     */
    private String name = null;
    
    /** The EntityManagerFactory used to interact using JPA **/
    private EntityManagerFactory emf;
    
    /** The JAXBConext used to produce JSON or XML **/
    private JAXBContext context = null;
    
    /** The URI of the Persistence context.  This is used to build Links in JSON and XML **/
    private URI baseURI = null;
    
    /** 
     * An application provided listener that can be used if the application has a framework
     * to listen to database events.
     */
    private DescriptorBasedDatabaseEventListener databaseEventListener = null;
    
    private TransactionWrapper transaction = null;

    public PersistenceContext(Archive archive, Map<String, Object> properties, ClassLoader classLoader) {
        super();
        List<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, classLoader);
        SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.get(0);
        
        this.name = persistenceUnitInfo.getPersistenceUnitName();

        EntityManagerFactoryImpl emf = createEntityManagerFactory(persistenceUnitInfo, properties);
        this.emf = emf;
        
        if (getJpaSession().hasExternalTransactionController()){
            transaction = new JTATransactionWrapper();
        } else {
            transaction = new ResourceLocalTransactionWrapper();
        }
        try{
            JAXBContext jaxbContext = createDynamicJAXBContext(emf.getServerSession());
            this.context = jaxbContext;
        } catch (JAXBException jaxbe){
            JPARSLogger.fine("exception_creating_jaxb_context", new Object[]{persistenceUnitInfo.getPersistenceUnitName(), jaxbe.toString()});
            emf.close();
        } catch (IOException e){
            JPARSLogger.fine("exception_creating_jaxb_context", new Object[]{persistenceUnitInfo.getPersistenceUnitName(), e.toString()});
            emf.close();
        }
    }
    
    public PersistenceContext(String emfName, EntityManagerFactoryImpl emf, URI defaultURI) {
        super();
        this.emf = emf;
        this.name = emfName;
        if (getJpaSession().hasExternalTransactionController()){
            transaction = new JTATransactionWrapper();
        } else {
            transaction = new ResourceLocalTransactionWrapper();
        }
        try{
            JAXBContext jaxbContext = createDynamicJAXBContext(emf.getServerSession());
            this.context = jaxbContext;
        } catch (JAXBException jaxbe){
            JPARSLogger.fine("exception_creating_jaxb_context", new Object[]{emfName, jaxbe.toString()});
            emf.close();
        } catch (IOException e){
            JPARSLogger.fine("exception_creating_jaxb_context", new Object[]{emfName, e.toString()});
            emf.close();
        }
        setBaseURI(defaultURI);
    }
 
    /**
     * This method is used to help construct a JAXBContext from an existing EntityManagerFactory.
     * 
     * For each package in the EntityManagerFactory, a MetadataSource that is capable of building a JAXBContext
     * that creates the same mappings in JAXB is created.  These MetadataSources are used to constuct the JAXContext
     * that is used for JSON and XML translation
     * @param metadataSources
     * @param persistenceUnitName
     * @param session
     */
    protected void addDynamicXMLMetadataSources(List<Object> metadataSources, Server session){
        Set<String> packages = new HashSet<String>();
        Iterator<Class> i = session.getDescriptors().keySet().iterator();
        while (i.hasNext()){
            Class descriptorClass = i.next();
            String packageName = "";
            if (descriptorClass.getName().lastIndexOf('.') > 0){
                packageName = descriptorClass.getName().substring(0, descriptorClass.getName().lastIndexOf('.'));
            }
            if (!packages.contains(packageName)){
                packages.add(packageName);
            }
        }
        
        for(String packageName: packages){
            metadataSources.add(new DynamicXMLMetadataSource(session, packageName));
        }
    }
    
    /**
     * Add a listener that can react to DatabaseChange notifications
     */
    public void addListener(ChangeListener listener) {
        DescriptorBasedDatabaseEventListener changeListener = (DescriptorBasedDatabaseEventListener) getJpaSession().getProperty(CHANGE_NOTIFICATION_LISTENER);
        if (changeListener == null) {
            throw new JPARSException(LoggingLocalization.buildMessage("jpars_could_not_add_listener", new Object[]{}));
        }
        changeListener.addChangeListener(listener);
    }
    
    /**
     * A part of the facade over the JPA API
     * Persist an entity in JPA and commit
     * @param tenantId
     * @param entity
     */
    public void create(Map<String, String> tenantId, Object entity) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        try {
            transaction.beginTransaction(em);
            em.persist(entity);
            transaction.commitTransaction(em);
        } finally {
            em.close();
        }
    }
    
    /**
     * Create a JAXBConext based on the EntityManagerFactory for this PersistenceContext
     * @param session
     * @return
     */
    protected JAXBContext createDynamicJAXBContext(Server session) throws JAXBException, IOException {
        JAXBContext jaxbContext = (JAXBContext) session.getProperty(JAXBContext.class.getName());
        if (jaxbContext != null) {
            return jaxbContext;
        }

        Map<String, Object> properties = createJAXBProperties(session);      

        ClassLoader cl = session.getPlatform().getConversionManager().getLoader();
        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(cl, properties);
        
        session.setProperty(JAXBContext.class.getName(), jaxbContext);

        return jaxbContext;
    }
    
    /**
     * A part of the facade over the JPA API
     * Create an EntityManagerFactory using the given PersistenceUnitInfo and properties
     * @param info
     * @param properties
     * @return
     */
    protected EntityManagerFactoryImpl createEntityManagerFactory(PersistenceUnitInfo info, Map<String, ?> properties){
        PersistenceProvider provider = new PersistenceProvider();
        EntityManagerFactory emf = provider.createContainerEntityManagerFactory(info, properties);
        return (EntityManagerFactoryImpl)emf;
    }
    
    /**
     * A part of the facade over the JPA API
     * Create an EntityManager from the EntityManagerFactory wrapped by this persistence context
     * @param tenantId
     * @return
     */
    protected EntityManager createEntityManager(String tenantId) {
        return getEmf().createEntityManager();
    }
    
    /**
     * Build the set of properties used to create the JAXBContext based on the EntityManagerFactory that
     * this PersistenceContext wraps
     * @param persistenceUnitName
     * @param session
     * @return
     * @throws IOException
     */
    protected Map<String, Object> createJAXBProperties(Server session) throws IOException{
        Map<String, Object> properties = new HashMap<String, Object>(1);
        List<Object> metadataLocations = new ArrayList<Object>();

        addDynamicXMLMetadataSources(metadataLocations, session);
        String oxmLocation = (String) emf.getProperties().get("eclipselink.jpa-rs.oxm");

        if (oxmLocation != null){
            metadataLocations.add(new org.eclipse.persistence.jaxb.metadata.XMLMetadataSource((new URL(oxmLocation)).openStream()));
        }
        
        Object passedOXMLocations = emf.getProperties().get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
        if (passedOXMLocations != null){
            if (passedOXMLocations instanceof String){
                metadataLocations.add(new org.eclipse.persistence.jaxb.metadata.XMLMetadataSource((new URL((String)passedOXMLocations)).openStream()));
            } else if (passedOXMLocations instanceof List){
                for (Object location: (List)passedOXMLocations){
                    if (location instanceof String){
                        metadataLocations.add(new org.eclipse.persistence.jaxb.metadata.XMLMetadataSource((new URL((String)location)).openStream()));
                    }
                }
            }
            oxmLocation = (String)emf.getProperties().get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
            if (oxmLocation != null){
                metadataLocations.add(new org.eclipse.persistence.jaxb.metadata.XMLMetadataSource((new URL(oxmLocation)).openStream()));
            }
            
        }

        
        metadataLocations.add(new LinkMetadataSource());
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataLocations);
        
        properties.put("eclipselink.session-event-listener", new PreLoginMappingAdapter());
        return properties;
    }
    
    /**
     *  A part of the facade over the JPA API
     *  Delete the given entity in JPA and commit the changes
     */
    public void delete(Map<String, String> tenantId, String type, Object id) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            transaction.beginTransaction(em);
            Object entity = em.find(getClass(type), id);
            if (entity != null){
                em.remove(entity);
            }
            transaction.commitTransaction(em);
        } finally {
            em.close();
        }
    }
    
    /**
     * A part of the facade over the JPA API
     * Find an entity with the given name and id in JPA
     * @param entityName
     * @param id
     * @return
     */
    public Object find(String entityName, Object id) {
        return find(null, entityName, id);
    }

    /**
     * A part of the facade over the JPA API
     * Find an entity with the given name and id in JPA
     * @param tenantId
     * @param entityName
     * @param id
     * @return
     */
    public Object find(Map<String, String> tenantId, String entityName, Object id) {
        return find(tenantId, entityName, id, null);
    }
    
    /**
     * A part of the facade over the JPA API
     * Find an entity with the given name and id in JPA
     * @param tenantId
     * @param entityName
     * @param id
     * @param properties - query hints used on the find
     * @return
     */
    public Object find(Map<String, String> tenantId, String entityName, Object id, Map<String, Object> properties) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            return em.find(getClass(entityName), id, properties);
        } finally {
            em.close();
        }
    }
    
    public Object findAttribute(Map<String, String> tenantId, String entityName, Object id, Map<String, Object> properties, String attribute) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            Object object = em.find(getClass(entityName), id, properties);
            ClassDescriptor descriptor =getJpaSession().getClassDescriptor(getClass(entityName));
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);            
            if (mapping == null){
                return null;
            }
            return mapping.getAttributeValueFromObject(object);
        } finally {
            em.close();
        }
    }
    
    public Object updateOrAddAttribute(Map<String, String> tenantId, String entityName, Object id, Map<String, Object> properties, String attribute, Object attributeValue, String partner) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            ClassDescriptor descriptor = getJpaSession().getClassDescriptor(getClass(entityName));
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            Object object = null;
            if (mapping == null){
                return null;
            } else if (mapping.isObjectReferenceMapping() || mapping.isCollectionMapping()){
                DatabaseMapping partnerMapping = null;
                if (partner != null){
                    ClassDescriptor referenceDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                    partnerMapping = referenceDescriptor.getMappingForAttributeName(partner);
                    if (partnerMapping == null){
                        return null;
                    }
                }
                transaction.beginTransaction(em);
                try{
                    object = em.find(getClass(entityName), id, properties);
                    if (object == null){
                        return null;
                    }
                    attributeValue = em.merge(attributeValue);
                    setMappingValueInObject(object, attributeValue, mapping, partnerMapping);
                    transaction.commitTransaction(em);
                } catch (Exception e){
                    JPARSLogger.fine("exception_while_updating_attribute", new Object[]{entityName, getName(), e.toString()});
                    transaction.rollbackTransaction(em);
                }
            } else {
                return null;
            }
            return object;
        } finally {
            em.close();
        }
    }
    
    public Object removeAttribute(Map<String, String> tenantId, String entityName, Object id, Map<String, Object> properties, String attribute, Object attributeValue, String partner) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            Object object = em.find(getClass(entityName), id, properties);
            ClassDescriptor descriptor = getJpaSession().getClassDescriptor(getClass(entityName));
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (mapping == null){
                return null;
            } else if (mapping.isObjectReferenceMapping() || mapping.isCollectionMapping()){
                DatabaseMapping partnerMapping = null;
                if (partner != null){
                    ClassDescriptor referenceDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                    partnerMapping = referenceDescriptor.getMappingForAttributeName(partner);
                    if (partnerMapping == null){
                        return null;
                    }
                }
                transaction.beginTransaction(em);
                try{
                    attributeValue = em.merge(attributeValue);
                    removeMappingValueFromObject(object, attributeValue, mapping, partnerMapping);
                    transaction.commitTransaction(em);
                } catch (Exception e){
                    transaction.rollbackTransaction(em);
                }
            } else {
                return null;
            }
            return object;
        } finally {
            em.close();
        }
    }
    
    protected void removeMappingValueFromObject(Object object, Object attributeValue, DatabaseMapping mapping, DatabaseMapping partner){
        if (mapping.isObjectReferenceMapping()){
            Object currentValue = mapping.getRealAttributeValueFromObject(object, getJpaSession());
            if (currentValue.equals(attributeValue)){
                ((ObjectReferenceMapping)mapping).getIndirectionPolicy().setRealAttributeValueInObject(object, null, true);
                if (partner != null){
                    removeMappingValueFromObject(attributeValue, object, partner, null);
                }
            }
        } else if (mapping.isCollectionMapping()){
            boolean removed = ((Collection)mapping.getRealAttributeValueFromObject(object, getJpaSession())).remove(attributeValue);
            if (removed && partner != null){
                removeMappingValueFromObject(attributeValue, object, partner, null);
            }
        }
    }
    
    public URI getBaseURI() {
        return baseURI;
    }
    
    /**
     * Look-up the given entity name in the EntityManagerFactory and return the class
     * is describes
     * @param entityName
     * @return
     */
    public Class<?> getClass(String entityName) {
        ClassDescriptor descriptor = getDescriptor(entityName);
        if (descriptor == null){
            return null;
        }
        return descriptor.getJavaClass();
    }
    
    public ServerSession getJpaSession(){
        return (ServerSession)JpaHelper.getServerSession(emf);
    }
    
    /**
     * Lookup the descriptor for the given entity name.
     * This method will look first in the EntityManagerFactory wrapped by this persistence context
     * and return that descriptor.  If one does not exist, it search the JAXBContext and return
     * a descriptor from there.
     * @param entityName
     * @return
     */
    public ClassDescriptor getDescriptor(String entityName){
        Server session = getJpaSession();
        ClassDescriptor descriptor = session.getDescriptorForAlias(entityName);
        if (descriptor == null){
            for (Object ajaxBSession:((JAXBContext)getJAXBContext()).getXMLContext().getSessions() ){
                descriptor = ((Session)ajaxBSession).getClassDescriptorForAlias(entityName);
                if (descriptor != null){
                    break;
                }
            }
        }
        return descriptor;
    }
    
    public ClassDescriptor getDescriptorForClass(Class clazz){
        Server session = getJpaSession();
        ClassDescriptor descriptor = session.getDescriptor(clazz);
        if (descriptor == null){
            for (Object ajaxBSession:((JAXBContext)getJAXBContext()).getXMLContext().getSessions() ){
                descriptor = ((Session)ajaxBSession).getClassDescriptor(clazz);
                if (descriptor != null){
                    break;
                }
            }
        }
        return descriptor;
    }
    
    public EntityManagerFactory getEmf() {
        return emf;
    }

    public JAXBContext getJAXBContext() {
        return context;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * A part of the facade over the JPA API
     * Call jpa merge on the given object and commit
     * If the passed object is a list, we will iterate through the
     * list and merge each member
     * @param tenantId
     * @param entity
     * @return
     */
    public Object merge(Map<String, String> tenantId, Object entity) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        Object mergedEntity = null;
        try {
            transaction.beginTransaction(em);
            if (entity instanceof List){
                List<Object> mergeList = new ArrayList<Object>();
                for (Object o: (List)entity){
                    mergeList.add(em.merge(o));
                }
                mergedEntity = mergeList;
            } else {
                mergedEntity = em.merge(entity);
            }
            transaction.commitTransaction(em);
            return mergedEntity;
        } finally {
            em.close();
        }
    }
    
    /**
     * A convenience method to create a new dynamic entity of the given type
     * @param type
     * @return
     */
    public DynamicEntity newEntity(String type) {
        return newEntity(null, type);
    }
    
    /**
     * A convenience method to create a new dynamic entity of the given type
     * @param tenantId
     * @param type
     * @return
     */
    public DynamicEntity newEntity(Map<String, String> tenantId, String type) {
        JPADynamicHelper helper = new JPADynamicHelper(getEmf());
        DynamicEntity entity = null;
        try{
            entity = helper.newDynamicEntity(type);
        } catch (IllegalArgumentException e){
            ClassDescriptor descriptor = getDescriptor(type);
            if (descriptor != null){
                DynamicType jaxbType = (DynamicType) descriptor.getProperty(DynamicType.DESCRIPTOR_PROPERTY);
                if (jaxbType != null){
                    return jaxbType.newDynamicEntity();
                }
            }
            JPARSLogger.fine("exception_thrown_while_creating_dynamic_entity", new Object[]{type, e.toString()});
            throw e;
        }
        return entity;
    }
    
    /**
     * A part of the facade over the JPA API
     * Run a query with the given name in JPA and return the result
     * @param name
     * @param parameters
     * @return
     */
    public Object query(Map<String, String> tenantId, String name, Map<?, ?> parameters) {
        return query(tenantId, name, parameters, null, false, false);
    }
    
    /**
     * A part of the facade over the JPA API
     * Run a query with the given name in JPA and return the result
     * @param name
     * @param parameters
     * @param hints
     * @param returnSingleResult
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Object query(Map<String, String> tenantId, String name, Map<?, ?> parameters, Map<String, ?> hints, boolean returnSingleResult, boolean executeUpdate) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        try{
            Query query = em.createNamedQuery(name);
            DatabaseQuery dbQuery = ((EJBQueryImpl<?>)query).getDatabaseQuery();
            if (parameters != null){
                Iterator i=parameters.keySet().iterator();
                while (i.hasNext()){
                    String key = (String)i.next();
                    Class parameterClass = null;
                    int index = dbQuery.getArguments().indexOf(key);
                    if (index >= 0){
                        parameterClass = dbQuery.getArgumentTypes().get(index);
                    }
                    Object parameter = parameters.get(key);
                    if (parameterClass != null){
                        parameter = ConversionManager.getDefaultManager().convertObject(parameter, parameterClass);
                    }
                    query.setParameter(key, parameter);
                }
            }
            if (hints != null){
                for (String key:  hints.keySet()){
                    query.setHint(key, hints.get(key));
                }
            }
            if (executeUpdate){
                transaction.beginTransaction(em);
                Object result = query.executeUpdate();
                transaction.commitTransaction(em);
                return result;
            } else if (returnSingleResult){
                return query.getSingleResult();
            } else {
                return query.getResultList();
            }
        } finally {
            em.close();
        }
    }
    
    /**
     * Remove a given change listener.  Used in interacting with an application-provided mechanism for listenig
     * to database events.
     * @param listener
     */
    public void remove(ChangeListener listener) {
        DescriptorBasedDatabaseEventListener changeListener = (DescriptorBasedDatabaseEventListener) getJpaSession().getProperty(CHANGE_NOTIFICATION_LISTENER);
        if (changeListener != null) {
            changeListener.removeChangeListener(listener);
        }
    }
    
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }
    

    protected void setMappingValueInObject(Object object, Object attributeValue, DatabaseMapping mapping, DatabaseMapping partner){
        if (mapping.isObjectReferenceMapping()){
            ((ObjectReferenceMapping)mapping).getIndirectionPolicy().setRealAttributeValueInObject(object, attributeValue, true);
            if (partner != null){
                setMappingValueInObject(attributeValue, object, partner, null);
            }
        } else if (mapping.isCollectionMapping()){
            ((Collection)mapping.getAttributeValueFromObject(object)).add(attributeValue);
            if (partner != null){
                setMappingValueInObject(attributeValue, object, partner, null);
            }
        }
    }
    
    /**
     * Stop the current application instance
     */
    protected void stop() {
        emf.close();
        this.emf = null;
        this.context = null;
    }
    
    /**
     * Trigger the mechanism to allow persistence context to react to database change events.
     * @param descriptorAlias
     * @return
     */
    public DatabaseEventListener subscribeToEventNotification(String descriptorAlias) {
        ClassDescriptor descriptor = getJpaSession().getDescriptorForAlias(descriptorAlias);
        if (descriptor == null){
            JPARSLogger.warning("jpars_could_not_find_descriptor", new Object[]{descriptorAlias});
            return null;
        }
        return subscribeToEventNotification(emf, descriptor);
    }
    
    /**
     * Trigger the mechanism to allow persistence context to react to database change events.
     * @param emf
     * @param descriptor
     * @return
     */
    public DatabaseEventListener subscribeToEventNotification(EntityManagerFactory emf, ClassDescriptor descriptor) {
        ServerSession session = getJpaSession();
        if (databaseEventListener == null){
            if (EVENT_LISTENER_FACTORY != null){
                databaseEventListener = EVENT_LISTENER_FACTORY.createDatabaseEventListener();
                session.setDatabaseEventListener(databaseEventListener);
                session.setProperty(CHANGE_NOTIFICATION_LISTENER, databaseEventListener);
            } else {
                JPARSLogger.warning("jpars_could_not_find_descriptor", new Object[]{descriptor.getAlias()});
                return null;
           }
        }
        databaseEventListener.initialize(descriptor, session);
        databaseEventListener.register(session, descriptor);
        return databaseEventListener;
    }

    public String toString() {
        return "Application(" + getName() + ")::" + System.identityHashCode(this);
    }
    
    public Object unmarshalEntity(String type, MediaType acceptedMedia, InputStream in) throws JAXBException {
        return unmarshalEntity(getClass(type), acceptedMedia, in);
    }
    
    public Object unmarshalEntity(Class type, MediaType acceptedMedia, InputStream in) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, acceptedMedia.toString());
        unmarshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        JAXBElement<?> element = unmarshaller.unmarshal(new StreamSource(in), type);
        if (element.getValue() instanceof List<?>){
            for (Object object: (List<?>)element.getValue()){
                wrap(object);
            }
            return element.getValue();
        } else {
            wrap(element.getValue());
        }
        return element.getValue();
    }
    
    protected Object wrap(Object entity){
        ClassDescriptor descriptor = getJpaSession().getDescriptor(entity);
        if (entity instanceof FetchGroupTracker){
            FetchGroup fetchGroup = new FetchGroup();
            for (DatabaseMapping mapping: descriptor.getMappings()){
                if (!mapping.isForeignReferenceMapping() || mapping.isPrivateOwned()){
                    fetchGroup.addAttribute(mapping.getAttributeName());
                }
            }
            (new FetchGroupManager()).setObjectFetchGroup(entity, fetchGroup, null);
        } else if (descriptor.hasRelationships()){
            for (DatabaseMapping mapping: descriptor.getMappings()){
                if (mapping.isForeignReferenceMapping() && !mapping.isPrivateOwned()){
                    // we require Fetch groups to handle relationships
                    throw new JPARSException(LoggingLocalization.buildMessage("weaving_required_for_relationships", new Object[]{}));
                }
            }
        }
        return entity;
    }
    
    public void marshallEntity(Object object, MediaType mediaType, OutputStream output) throws JAXBException {
        preMarshallEntity(object);
        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        marshaller.setAdapter(new RelationshipLinkAdapter(getBaseURI().toString(), this));
        marshaller.setListener(new Marshaller.Listener() {
            @Override
            public void beforeMarshal(Object source) {   
                if (source instanceof DynamicEntity){
                    DynamicEntityImpl sourceImpl = (DynamicEntityImpl)source;
                    PropertyChangeListener listener = sourceImpl._persistence_getPropertyChangeListener();
                    sourceImpl._persistence_setPropertyChangeListener(null);
                    ((DynamicEntity)source).set("self", source);
                    sourceImpl._persistence_setPropertyChangeListener(listener);
                }
            }
        });
        
        if (mediaType == MediaType.APPLICATION_XML_TYPE && object instanceof List){
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = null;
            try{
                writer = outputFactory.createXMLStreamWriter(output);
                writer.writeStartDocument();
                writer.writeStartElement("List");
                for (Object o: (List<Object>)object){
                    marshaller.marshal(o, writer);  
                }
                writer.writeEndDocument();
            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                postMarshallEntity(object);
            }
        } else {       
            marshaller.marshal(object, output);
            postMarshallEntity(object);
        }
    }
    
    protected void preMarshallEntity(Object object){
        if (object instanceof List){
            Iterator i = ((List)object).iterator();
            while (i.hasNext()){
                preMarshallIndividualEntity(i.next());
            }
        } else {
            preMarshallIndividualEntity(object);
        }
    }
    
    protected void preMarshallIndividualEntity(Object entity){
        if (entity instanceof PersistenceWeavedRest){
            ClassDescriptor descriptor = getJpaSession().getClassDescriptor(entity.getClass());
            if (descriptor != null){
                ((PersistenceWeavedRest)entity)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
                for (DatabaseMapping mapping : descriptor.getMappings()){
                    if (mapping.isForeignReferenceMapping()){
                        ForeignReferenceMapping frMapping = (ForeignReferenceMapping)mapping;
                        if (!frMapping.isPrivateOwned()){

                            RelationshipInfo info = new RelationshipInfo();

                            info.setAttributeName(frMapping.getAttributeName());
                            info.setOwningEntity(entity);
                            info.setOwningEntityAlias(descriptor.getAlias());
                            info.setPersistencePrimaryKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entity, getJpaSession()));
                            ((PersistenceWeavedRest)entity)._persistence_getRelationships().add(info);
                        }
                    }
                }
            }
            
        }
    }
    
    protected void postMarshallEntity(Object object){
        if (object instanceof List){
            Iterator i = ((List)object).iterator();
            while (i.hasNext()){
                Object entity = i.next();
                if (entity instanceof PersistenceWeavedRest){
                    ((PersistenceWeavedRest)entity)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
                }
            }
        } else {
            if (object instanceof PersistenceWeavedRest){
                ((PersistenceWeavedRest)object)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
            }
        }
    }
    
    

}
