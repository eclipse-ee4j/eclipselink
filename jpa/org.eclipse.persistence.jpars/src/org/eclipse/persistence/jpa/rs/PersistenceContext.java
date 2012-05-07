/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.rs.eventlistener.ChangeListener;
import org.eclipse.persistence.jpa.rs.eventlistener.DatabaseEventListenerFactory;
import org.eclipse.persistence.jpa.rs.eventlistener.DescriptorBasedDatabaseEventListener;
import org.eclipse.persistence.jpa.rs.util.DynamicXMLMetadataSource;
import org.eclipse.persistence.jpa.rs.util.JTATransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.LinkAdapter;
import org.eclipse.persistence.jpa.rs.util.ResourceLocalTransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.TransactionWrapper;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.queries.DatabaseQuery;
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

    public PersistenceContext(Archive archive, Map<String, Object> properties, ClassLoader classLoader){
        super();
        List<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, classLoader);
        SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.get(0);
        
        this.name = persistenceUnitInfo.getPersistenceUnitName();

        EntityManagerFactoryImpl emf = createEntityManagerFactory(persistenceUnitInfo, properties);
        this.emf = emf;
        
        if (JpaHelper.getServerSession(emf).hasExternalTransactionController()){
            transaction = new JTATransactionWrapper();
        } else {
            transaction = new ResourceLocalTransactionWrapper();
        }
        try{
            JAXBContext jaxbContext = createDynamicJAXBContext(persistenceUnitInfo.getPersistenceUnitName(), emf.getServerSession());
           this.context = jaxbContext;
        } catch (Exception e){
            emf.close();
            throw new RuntimeException("JAXB Creation Exception", e);
        }
    }
    
    public PersistenceContext(String emfName, EntityManagerFactoryImpl emf, URI defaultURI){
        super();
        this.emf = emf;
        this.name = emfName;
        if (JpaHelper.getServerSession(emf).hasExternalTransactionController()){
            transaction = new JTATransactionWrapper();
        } else {
            transaction = new ResourceLocalTransactionWrapper();
        }
        try{
            JAXBContext jaxbContext = null;
            jaxbContext = createDynamicJAXBContext(emfName, emf.getServerSession());
           this.context = jaxbContext;
        } catch (Exception e){
            throw new RuntimeException("JAXB Creation Exception", e);
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
    protected void addDynamicXMLMetadataSources(List<Object> metadataSources, String persistenceUnitName, Server session){
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
            metadataSources.add(new DynamicXMLMetadataSource(persistenceUnitName, session, packageName));
        }
    }
    
    /**
     * Add a listener that can react to DatabaseChange notifications
     */
    public void addListener(ChangeListener listener) {
        DescriptorBasedDatabaseEventListener changeListener = (DescriptorBasedDatabaseEventListener) JpaHelper.getDatabaseSession(getEmf()).getProperty(CHANGE_NOTIFICATION_LISTENER);
        if (changeListener == null) {
            throw new RuntimeException("Change Listener not registered properly");
        }
        changeListener.addChangeListener(listener);
    }
    
    /**
     * A part of the facade over the JPA API
     * Persist an entity in JPA and commit
     * @param tenantId
     * @param entity
     */
    public void create(String tenantId, Object entity) {
        EntityManager em = getEmf().createEntityManager();
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
    protected JAXBContext createDynamicJAXBContext(String persistenceUnitName, Server session) throws JAXBException, IOException {
        JAXBContext jaxbContext = (JAXBContext) session.getProperty(JAXBContext.class.getName());
        if (jaxbContext != null) {
            return jaxbContext;
        }

        Map<String, Object> properties = createJAXBProperties(persistenceUnitName, session);      

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
    protected Map<String, Object> createJAXBProperties(String persistenceUnitName, Server session) throws IOException{
        String oxmLocation = (String) emf.getProperties().get("eclipselink.jpa-rs.oxm");
        
        Map<String, Object> properties = new HashMap<String, Object>(1);
        List<Object> metadataLocations = new ArrayList<Object>();

        addDynamicXMLMetadataSources(metadataLocations, persistenceUnitName, session);
        if (oxmLocation != null){
            metadataLocations.add(new org.eclipse.persistence.jaxb.metadata.XMLMetadataSource((new URL(oxmLocation)).openStream()));
        }
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataLocations);
        return properties;
    }
    
    /**
     *  A part of the facade over the JPA API
     *  Delete the given entity in JPA and commit the changes
     */
    public void delete(String tenantId, String type, Object id) {
        EntityManager em = getEmf().createEntityManager();

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
    public Object find(String tenantId, String entityName, Object id) {
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
    public Object find(String tenantId, String entityName, Object id, Map<String, Object> properties) {
        EntityManager em = getEmf().createEntityManager();

        try {
            return em.find(getClass(entityName), id, properties);
        } finally {
            em.close();
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
    
    /**
     * Lookup the descriptor for the given entity name.
     * This method will look first in the EntityManagerFactory wrapped by this persistence context
     * and return that descriptor.  If one does not exist, it search the JAXBContext and return
     * a descriptor from there.
     * @param entityName
     * @return
     */
    public ClassDescriptor getDescriptor(String entityName){
        Server session = JpaHelper.getServerSession(getEmf());
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
        Server session = JpaHelper.getServerSession(getEmf());
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
    public Object merge(String tenantId, Object entity) {
        EntityManager em = getEmf().createEntityManager();
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
    public DynamicEntity newEntity(String tenantId, String type) {
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
    public Object query(String name, Map<?, ?> parameters) {
        return query(name, parameters, null, false, false);
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
    public Object query(String name, Map<?, ?> parameters, Map<String, ?> hints, boolean returnSingleResult, boolean executeUpdate) {
        EntityManager em = getEmf().createEntityManager();
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
        DescriptorBasedDatabaseEventListener changeListener = (DescriptorBasedDatabaseEventListener) JpaHelper.getDatabaseSession(getEmf()).getProperty(CHANGE_NOTIFICATION_LISTENER);
        if (changeListener != null) {
            changeListener.removeChangeListener(listener);
        }
    }
    
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
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
        ServerSession session = (ServerSession) JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getDescriptorForAlias(descriptorAlias);
        if (descriptor == null){
            throw new RuntimeException("Could not find " + descriptorAlias + " for subscription");
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
        ServerSession session = (ServerSession) JpaHelper.getServerSession(emf);
        if (databaseEventListener == null){
            if (EVENT_LISTENER_FACTORY != null){
                databaseEventListener = EVENT_LISTENER_FACTORY.createDatabaseEventListener();
                session.setDatabaseEventListener(databaseEventListener);
                session.setProperty(CHANGE_NOTIFICATION_LISTENER, databaseEventListener);
            } else {
                throw new RuntimeException("Could not subscribe to change notification for " + descriptor.getAlias());
            }
        }
        databaseEventListener.initialize(descriptor, session);
        databaseEventListener.register(session, descriptor);
        return databaseEventListener;
    }

    public String toString() {
        return "Application(" + getName() + ")::" + System.identityHashCode(this);
    }
    
    public Object unmarshalEntity(String type, String tenantId, MediaType acceptedMedia, InputStream in) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, acceptedMedia.toString());
        unmarshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        JAXBElement<?> element = unmarshaller.unmarshal(new StreamSource(in), getClass(type));
        return element.getValue();
    }
    
    public void marshallEntity(Object object, MediaType mediaType, OutputStream output) throws JAXBException {              
        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
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
            }          
        } else {       
            marshaller.marshal(object, output);      
        }
    }

}
