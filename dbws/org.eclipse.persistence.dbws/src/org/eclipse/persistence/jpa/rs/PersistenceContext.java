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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.rs.config.ConfigDefaults;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSConfigurationException;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.JTATransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.PreLoginMappingAdapter;
import org.eclipse.persistence.jpa.rs.util.ResourceLocalTransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.TransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.list.MultiResultQueryList;
import org.eclipse.persistence.jpa.rs.util.list.MultiResultQueryListItem;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;
import org.eclipse.persistence.jpa.rs.util.metadatasources.DynamicXMLMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaLangMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaMathMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.JavaUtilMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.LinkMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.MultiResultQueryListItemMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.MultiResultQueryListMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.SimpleHomogeneousListMetadataSource;
import org.eclipse.persistence.jpa.rs.util.metadatasources.SingleResultQueryListMetadataSource;
import org.eclipse.persistence.jpa.rs.util.xmladapters.LinkAdapter;
import org.eclipse.persistence.jpa.rs.util.xmladapters.RelationshipLinkAdapter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

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

    public static final String JPARS_CONTEXT = "eclipselink.jpars.context";

    @SuppressWarnings("rawtypes")
    protected List<XmlAdapter> adapters = null;

    /**
     * The name of the persistence context is used to look it up. By default it will be the
     * persistence unit name of the JPA persistence unit.
     */
    protected String name = null;

    /** The EntityManagerFactory used to interact using JPA **/
    protected EntityManagerFactory emf;

    /** The JAXBConext used to produce JSON or XML **/
    protected JAXBContext context = null;

    /** The URI of the Persistence context.  This is used to build Links in JSON and XML **/
    protected URI baseURI = null;

    protected TransactionWrapper transaction = null;
    
    private Boolean weavingEnabled = null;
    
    private String version = null;

    protected PersistenceContext() {
    }

    /**
     * Instantiates a new persistence context.
     *
     * @param emfName the emf name
     * @param emf the emf
     * @param defaultURI the default uri
     */
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
            this.context = createDynamicJAXBContext(emf.getDatabaseSession());
        } catch (JAXBException jaxbe){
            JPARSLogger.exception("exception_creating_jaxb_context", new Object[]{emfName, jaxbe.toString()}, jaxbe);
            emf.close();
        } catch (IOException e){
            JPARSLogger.exception("exception_creating_jaxb_context", new Object[]{emfName, e.toString()}, e);
            emf.close();
        }
        setBaseURI(defaultURI);
    }

    public boolean isWeavingEnabled() {
        if (this.weavingEnabled == null) {
            this.weavingEnabled = getWeavingProperty();
        }
        return this.weavingEnabled;
    }

    public String getVersion() {
        return version;
    }

    protected void setVersion(String version) {
        this.version = version;
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
    @SuppressWarnings("rawtypes")
    protected void addDynamicXMLMetadataSources(List<Object> metadataSources, AbstractSession session) {
        Set<String> packages = new HashSet<String>();
        Iterator<Class> i = session.getDescriptors().keySet().iterator();
        while (i.hasNext()){
            Class<?> descriptorClass = i.next();
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
     * Create a JAXBContext based on the EntityManagerFactory for this PersistenceContext
     * @param session
     * @return
     */
    protected JAXBContext createDynamicJAXBContext(AbstractSession session) throws JAXBException, IOException {
        JAXBContext jaxbContext = (JAXBContext) session.getProperty(JAXBContext.class.getName());
        if (jaxbContext != null) {
            return jaxbContext;
        }

        Map<String, Object> properties = createJAXBProperties(session);      

        ClassLoader cl = session.getDatasourcePlatform().getConversionManager().getLoader();
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, Object> createJAXBProperties(AbstractSession session) throws IOException {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        List<Object> metadataLocations = new ArrayList<Object>();

        addDynamicXMLMetadataSources(metadataLocations, session);
        String oxmLocation = (String) emf.getProperties().get("eclipselink.jpa-rs.oxm");

        if (oxmLocation != null){
            metadataLocations.add(oxmLocation);
        }

        Object passedOXMLocations = emf.getProperties().get(JAXBContextProperties.OXM_METADATA_SOURCE);
        if (passedOXMLocations != null){
            if (passedOXMLocations instanceof Collection){
                metadataLocations.addAll((Collection)passedOXMLocations);
            } else {
                metadataLocations.add(passedOXMLocations);
            }
        }

        metadataLocations.add(new LinkMetadataSource());
        metadataLocations.add(new MultiResultQueryListMetadataSource());
        metadataLocations.add(new MultiResultQueryListItemMetadataSource());
        metadataLocations.add(new SingleResultQueryListMetadataSource());
        metadataLocations.add(new SimpleHomogeneousListMetadataSource());

        metadataLocations.add(new JavaLangMetadataSource());
        metadataLocations.add(new JavaMathMetadataSource());
        metadataLocations.add(new JavaUtilMetadataSource());

        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataLocations);

        properties.put("eclipselink.session-event-listener", new PreLoginMappingAdapter((AbstractSession)session));
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
     * Does exist.
     *
     * @param tenantId the tenant id
     * @param entity the entity
     * @return true, if successful
     */
    public boolean doesExist(Map<String, String> tenantId, Object entity){
        DatabaseSession session = JpaHelper.getDatabaseSession(getEmf());
        return session.doesObjectExist(entity);
    }

    /**
     * Finalize.
     */
    @Override
    public void finalize(){
        this.emf.close();
        this.emf = null;
        this.context = null;
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

    /**
     * Find attribute.
     *
     * @param tenantId the tenant id
     * @param entityName the entity name
     * @param id the id
     * @param properties the properties
     * @param attribute the attribute
     * @return the object
     */
    public Object findAttribute(Map<String, String> tenantId, String entityName, Object id, Map<String, Object> properties, String attribute) {
        EntityManager em = getEmf().createEntityManager(tenantId);

        try {
            Object object = em.find(getClass(entityName), id, properties);
            ClassDescriptor descriptor =getJpaSession().getClassDescriptor(getClass(entityName));
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);            
            if (mapping == null){
                return null;
            }
            return mapping.getRealAttributeValueFromAttribute(mapping.getAttributeValueFromObject(object), object, (AbstractSession) getJpaSession());
        } finally {
            em.close();
        }
    }

    /**
     * Update or add attribute.
     *
     * @param tenantId the tenant id
     * @param entityName the entity name
     * @param id the id
     * @param properties the properties
     * @param attribute the attribute
     * @param attributeValue the attribute value
     * @param partner the partner
     * @return the object
     */
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
                    return null;
                }
            } else {
                return null;
            }
            return object;
        } finally {
            em.close();
        }
    }

    /**
     * Removes the attribute.
     *
     * @param tenantId the tenant id
     * @param entityName the entity name
     * @param id the id
     * @param properties the properties
     * @param attribute the attribute
     * @param attributeValue the attribute value
     * @param partner the partner
     * @return the object
     *  
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object removeAttribute(Map<String, String> tenantId, String entityName, Object id, String attribute, String listItemId, Object entity, String partner)
    {
        EntityManager em = getEmf().createEntityManager(tenantId);
        String fieldName = null;

        try {
            Class<?> clazz = getClass(entityName);
            ClassDescriptor descriptor = getJpaSession().getClassDescriptor(clazz);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (mapping == null) {
                return null;
            } else if (mapping.isObjectReferenceMapping() || mapping.isCollectionMapping()) {
                DatabaseMapping partnerMapping = null;
                Object originalAttributeValue = null;
                ClassDescriptor referenceDescriptor = ((ForeignReferenceMapping) mapping).getReferenceDescriptor();
                if (partner != null) {
                    partnerMapping = referenceDescriptor.getMappingForAttributeName(partner);
                    if (partnerMapping == null) {
                        return null;
                    }
                }
                Field[] fields = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    fields = AccessController.doPrivileged(new PrivilegedGetDeclaredFields(clazz));
                } else {
                    fields = PrivilegedAccessHelper.getDeclaredFields(clazz);
                }

                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    fieldName = field.getName();
                    if (fieldName.equals(attribute)) {
                        try {
                            // call clear on this collection 
                            Object attributeValue = getAttribute(entity, attribute);
                            originalAttributeValue = attributeValue;
                            if (attributeValue instanceof Collection) {
                                if (listItemId == null) {
                                    // no collection member specified in request (listItemId=null) remove entire collection
                                    ((Collection) attributeValue).clear();
                                } else {
                                    Object realListItemId = IdHelper.buildId(this, referenceDescriptor.getAlias(), listItemId);
                                    Object member = this.find(referenceDescriptor.getAlias(), realListItemId);
                                    ((Collection) attributeValue).remove(member);
                                }
                            } else if (attributeValue instanceof Object) {
                                attributeValue = null;
                            } else {
                                attributeValue = 0;
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }

                transaction.beginTransaction(em);
                entity = em.merge(entity);
                removeMappingValueFromObject(entity, originalAttributeValue, mapping, partnerMapping);
                transaction.commitTransaction(em);
                return entity;
            }
            return null;
        } catch (Exception e) {
            JPARSLogger.fine("exception_while_removing_attribute", new Object[] { fieldName, entityName, getName(), e.toString() });
            transaction.rollbackTransaction(em);
            return null;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    private Object getAttribute(Object entity, String propertyName) {
        try {
            BeanInfo info = Introspector.getBeanInfo(entity.getClass(), Object.class);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                String name = pd.getName();
                if (propertyName.equals(name)) {
                    Method getter = pd.getReadMethod();
                    Object value = null;
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        value = AccessController.doPrivileged(new PrivilegedMethodInvoker(getter, entity));
                    } else {
                        value = PrivilegedAccessHelper.invokeMethod(getter, entity);
                    }
                    return value;
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    protected void removeMappingValueFromObject(Object object, Object attributeValue, DatabaseMapping mapping, DatabaseMapping partner){
        if (mapping.isObjectReferenceMapping()){
            Object currentValue = mapping.getRealAttributeValueFromObject(object, (AbstractSession) getJpaSession());
            if (currentValue.equals(attributeValue)){
                ((ObjectReferenceMapping)mapping).getIndirectionPolicy().setRealAttributeValueInObject(object, null, true);
                if (partner != null){
                    removeMappingValueFromObject(attributeValue, object, partner, null);
                }
            }
        } else if (mapping.isCollectionMapping()){
            boolean removed = ((Collection) mapping.getRealAttributeValueFromObject(object, (AbstractSession) getJpaSession())).remove(attributeValue);
            if (removed && partner != null){
                removeMappingValueFromObject(attributeValue, object, partner, null);
            }
        }
    }

    /**
     * Gets the base uri.
     *
     * @return the base uri
     */
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
     * Gets the jpa session.
     *
     * @return the jpa session
     */
    public DatabaseSession getJpaSession() {
        // Fix for bug 390786 - JPA-RS: ClassCastException retrieving metadata for Composite Persistence Unit
        DatabaseSession dbSession = JpaHelper.getDatabaseSession(emf);
        return dbSession;
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
        DatabaseSession session = getJpaSession();
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

    /**
     * Gets the descriptor for class.
     *
     * @param clazz the clazz
     * @return the descriptor for class
     */
    @SuppressWarnings("rawtypes")
    public ClassDescriptor getDescriptorForClass(Class clazz){
        DatabaseSession session = getJpaSession();
        ClassDescriptor descriptor = session.getDescriptor(clazz);
        if (descriptor == null){
            return getJAXBDescriptorForClass(clazz);
        }
        return descriptor;
    }

    /**
     * Gets the jAXB descriptor for class.
     *
     * @param clazz the clazz
     * @return the jAXB descriptor for class
     */
    @SuppressWarnings("rawtypes")
    public ClassDescriptor getJAXBDescriptorForClass(Class clazz) {
        ClassDescriptor descriptor = null;
        for (Object ajaxBSession : ((JAXBContext) getJAXBContext())
                .getXMLContext().getSessions()) {
            descriptor = ((Session) ajaxBSession).getClassDescriptor(clazz);
            if (descriptor != null) {
                break;
            }
        }
        return descriptor;
    }

    /**
     * Gets the emf.
     *
     * @return the emf
     */
    public EntityManagerFactory getEmf() {
        return emf;
    }

    /**
     * Gets the jAXB context.
     *
     * @return the jAXB context
     */
    public JAXBContext getJAXBContext() {
        return context;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
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

    @SuppressWarnings("rawtypes")
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
     * Query execute update.
     *
     * @param tenantId the tenant id
     * @param name the name
     * @param parameters the parameters
     * @param hints the hints
     * @return the int
     */
    public int queryExecuteUpdate(Map<String, String> tenantId, String name, Map<?, ?> parameters, Map<String, ?> hints) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        try {
            Query query = constructQuery(em, name, parameters, hints);
            transaction.beginTransaction(em);
            int result = query.executeUpdate();
            transaction.commitTransaction(em);
            return result;
        } finally {
            em.close();
        }
    }

    /**
     * Query multiple results.
     *
     * @param tenantId the tenant id
     * @param name the name
     * @param parameters the parameters
     * @param hints the hints
     * @return the list
     */
    @SuppressWarnings("rawtypes")
    public List queryMultipleResults(Map<String, String> tenantId, String name, Map<?, ?> parameters, Map<String, ?> hints) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        try {
            Query query = constructQuery(em, name, parameters, hints);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("rawtypes")
    protected Query constructQuery(EntityManager em, String name, Map<?, ?> parameters, Map<String, ?> hints) {
        Query query = em.createNamedQuery(name);
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (parameters != null) {
            Iterator i = parameters.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                Class parameterClass = null;
                int index = dbQuery.getArguments().indexOf(key);
                if (index >= 0) {
                    parameterClass = dbQuery.getArgumentTypes().get(index);
                }
                Object parameter = parameters.get(key);
                if (parameterClass != null) {
                    parameter = ConversionManager.getDefaultManager().convertObject(parameter, parameterClass);
                }
                query.setParameter(key, parameter);
            }
        }
        if (hints != null) {
            for (String key : hints.keySet()) {
                query.setHint(key, hints.get(key));
            }
        }
        return query;
    }

    @SuppressWarnings("rawtypes")
    public Query buildQuery(Map<String, String> tenantId, String name, Map<?, ?> parameters, Map<String, ?> hints) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        Query query = em.createNamedQuery(name);
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (parameters != null) {
            Iterator i = parameters.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                Class parameterClass = null;
                int index = dbQuery.getArguments().indexOf(key);
                if (index >= 0) {
                    parameterClass = dbQuery.getArgumentTypes().get(index);
                }
                Object parameter = parameters.get(key);
                if (parameterClass != null) {
                    parameter = ConversionManager.getDefaultManager().convertObject(parameter, parameterClass);
                }
                query.setParameter(key, parameter);
            }
        }
        if (hints != null) {
            for (String key : hints.keySet()) {
                query.setHint(key, hints.get(key));
            }
        }
        return query;
    }

    /**
     * Sets the base uri.
     *
     * @param baseURI the new base uri
     */
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
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
    public void stop() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        this.emf = null;
        this.context = null;
    }


    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        return "Application(" + getName() + ")::" + System.identityHashCode(this);
    }

    /**
     * Marshall an entity to either JSON or XML
     * Calling this method, will treat relationships as unfetched in the XML/JSON and marshall them as links
     * rather than attempting to marshall the data in those relationships
     * @param object
     * @param mediaType
     * @param output
     * @throws JAXBException
     */
    public Object unmarshalEntity(String type, MediaType acceptedMedia, InputStream in) throws JAXBException {
        return unmarshalEntity(getClass(type), acceptedMedia, in);
    }

    /**
     * Marshall an entity to either JSON or XML
     * @param object
     * @param mediaType
     * @param output
     * @param sendRelationships if this is set to true, relationships will be sent as links instead of sending 
     * the actual objects in the relationships
     * @throws JAXBException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object unmarshalEntity(Class type, MediaType acceptedMedia, InputStream in) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, acceptedMedia.toString());
        unmarshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            /*
             * ReferenceAdaptor unmarshal throws exception if the object referred by a link 
             * doesn't exist, and this handler is required to interrupt the unmarshal 
             * operation under this condition.
             * (non-Javadoc) @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent) 
             * 
             */
            public boolean handleEvent(ValidationEvent event) {
                if (event.getSeverity() != ValidationEvent.WARNING) {
                    // ValidationEventLocator eventLocator = event.getLocator();
                    // Throwable throwable = event.getLinkedException();
                    // nothing is really useful to check for us in eventLocator 
                    // and linked exception, just return false;
                    return false;
                }
                return true;
            }
        });

        for (XmlAdapter adapter:getAdapters()) {
            unmarshaller.setAdapter(adapter);
        }

        if (acceptedMedia == MediaType.APPLICATION_JSON_TYPE) {
            // Part of the fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=394059
            // This issue happens when request has objects derived from an abstract class. 
            // JSON_INCLUDE_ROOT is set to false for  JPA-RS. This means JSON requests won't have root tag.
            // The unmarshal method needs to be called with type, so that moxy can unmarshal the message based on type. 
            // For xml, root tag is always set, unmarshaller must use root of the message for unmarshalling and type should 
            // not be passed to unmarshal for xml type requests.
            JAXBElement<?> element = unmarshaller.unmarshal(new StreamSource(in), type);
            if (element.getValue() instanceof List<?>) {
                for (Object object : (List<?>) element.getValue()) {
                    wrap(object);
                }
                return element.getValue();
            } else {
                wrap(element.getValue());
            }
            return element.getValue();
        }

        Object domainObject = unmarshaller.unmarshal(new StreamSource(in));
        if (domainObject instanceof List<?>) {
            for (Object object : (List<?>) domainObject) {
                wrap(object);
            }
            return domainObject;
        } else {
            wrap(domainObject);
        }
        return domainObject;
    }

    /**
     * Make adjustments to an unmarshalled entity based on what is found in the weaved fields
     * 
     * @param entity
     * @return
     */
    protected Object wrap(Object entity){
        if (!doesExist(null, entity)){
            return entity;
        }
        ClassDescriptor descriptor = getJAXBDescriptorForClass(entity.getClass());
        if (entity instanceof FetchGroupTracker){
            FetchGroup fetchGroup = new FetchGroup();
            for (DatabaseMapping mapping: descriptor.getMappings()){
                if (!(mapping instanceof XMLInverseReferenceMapping)){
                    fetchGroup.addAttribute(mapping.getAttributeName());
                }
            }
            (new FetchGroupManager()).setObjectFetchGroup(entity, fetchGroup, null);
            ((FetchGroupTracker) entity)._persistence_setSession(JpaHelper.getDatabaseSession(getEmf()));
        } else if (descriptor.hasRelationships()){
            for (DatabaseMapping mapping: descriptor.getMappings()){
                if (mapping instanceof XMLInverseReferenceMapping){
                    // we require Fetch groups to handle relationships
                    throw new JPARSConfigurationException(LoggingLocalization.buildMessage("weaving_required_for_relationships", new Object[]{}));
                }
            }
        }
        return entity;
    }

    /**
     * Marshall an entity to either JSON or XML
     * Calling this method, will treat relationships as unfetched in the XML/JSON and marshall them as links
     * rather than attempting to marshall the data in those relationships
     * @param object
     * @param mediaType
     * @param output
     * @throws JAXBException
     */
    public void marshallEntity(Object object, MediaType mediaType, OutputStream output) throws JAXBException {
        marshallEntity(object, mediaType, output, true);
    }

    /**
     * Marshall an entity to either JSON or XML
     * @param object
     * @param mediaType
     * @param output
     * @param sendRelationships if this is set to true, relationships will be sent as links instead of sending 
     * the actual objects in the relationships
     * @throws JAXBException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void marshallEntity(Object object, MediaType mediaType, OutputStream output, boolean sendRelationships) throws JAXBException {
        if (sendRelationships) {
            preMarshallEntity(object);
        }

        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);

        marshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        marshaller.setAdapter(new RelationshipLinkAdapter(getBaseURI().toString(), this));

        for (XmlAdapter adapter:getAdapters()) {
            marshaller.setAdapter(adapter);
        }

        if (mediaType == MediaType.APPLICATION_XML_TYPE && object instanceof List) {
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = null;
            try {
                writer = outputFactory.createXMLStreamWriter(output);
                writer.writeStartDocument();
                writer.writeStartElement(ConfigDefaults.JPARS_LIST_GROUPING_NAME);
                for (Object o : (List<Object>) object) {
                    marshaller.marshal(o, writer);
                }
                writer.writeEndDocument();
                writer.flush();
                postMarshallEntity(object);
            } catch (Exception e) {
                e.printStackTrace();
                throw new JPARSException(e.toString());
            }
        } else {
            marshaller.marshal(object, output);
            postMarshallEntity(object);
        }
    }

    /**
     * Process an entity and add any additional data that needs to be added prior to marshalling
     * This method will both single entities and lists of entities
     * @param object
     */
    @SuppressWarnings("rawtypes")
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

    /**
     * Add any data required prior to marshalling an entity to XML or JSON
     * In general, this will only affect fields that have been weaved into the object
     * @param entity
     */
    @SuppressWarnings("rawtypes")
    protected void preMarshallIndividualEntity(Object entity) {
        if (entity instanceof MultiResultQueryListItem) {
            MultiResultQueryListItem item = (MultiResultQueryListItem) entity;
            List<JAXBElement> fields = item.getFields();
            for (int i = 0; i < fields.size(); i++) {
                // one or more fields in the MultiResultQueryListItem might be a domain object,
                // so, we need to set the relationshipInfo for those domain objects.
                setRelationshipInfo(fields.get(i).getValue());
            }
        } else if (entity instanceof SingleResultQueryList) {
            SingleResultQueryList item = (SingleResultQueryList) entity;
            List<JAXBElement> fields = item.getFields();
            for (int i = 0; i < fields.size(); i++) {
                // one or more fields in the SingleResultQueryList might be a domain object,
                // so, we need to set the relationshipInfo for those domain objects.
                setRelationshipInfo(fields.get(i).getValue());
            }
        } else if (entity instanceof MultiResultQueryList) {
            MultiResultQueryList list = (MultiResultQueryList) entity;
            List<MultiResultQueryListItem> items = list.getItems();
            for (int i = 0; i < items.size(); i++) {
                MultiResultQueryListItem item = items.get(i);
                List<JAXBElement> fields = item.getFields();
                for (int index = 0; index < fields.size(); index++) {
                    // one or more fields in the MultiResultQueryList might be a domain object,
                    // so, we need to set the relationshipInfo for those domain objects.
                    setRelationshipInfo(fields.get(index).getValue());
                }
            }
        } else {
            setRelationshipInfo(entity);
        }
    }

    private void setRelationshipInfo(Object entity) {
        if ((entity != null) && (entity instanceof PersistenceWeavedRest)) {
            ClassDescriptor descriptor = getJpaSession().getClassDescriptor(entity.getClass());
            if (descriptor != null) {
                ((PersistenceWeavedRest) entity)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (mapping.isForeignReferenceMapping()) {
                        ForeignReferenceMapping frMapping = (ForeignReferenceMapping) mapping;

                        RelationshipInfo info = new RelationshipInfo();

                        info.setAttributeName(frMapping.getAttributeName());
                        info.setOwningEntity(entity);
                        info.setOwningEntityAlias(descriptor.getAlias());
                        info.setPersistencePrimaryKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entity, (AbstractSession) getJpaSession()));
                        ((PersistenceWeavedRest) entity)._persistence_getRelationships().add(info);
                    }
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
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


    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<XmlAdapter> getAdapters() throws JPARSException {
        if (adapters != null) {
            return adapters;
        }
        adapters = new ArrayList<XmlAdapter>();
        try {
            for (ClassDescriptor desc : this.getJpaSession().getDescriptors().values()) {
                // avoid embeddables
                if (!desc.isAggregateCollectionDescriptor() && !desc.isAggregateDescriptor()) {
                    Class clz = desc.getJavaClass();
                    String referenceAdapterName = RestAdapterClassWriter.constructClassNameForReferenceAdapter(clz.getName());
                    ClassLoader cl = getJpaSession().getDatasourcePlatform().getConversionManager().getLoader();
                    Class referenceAdaptorClass = Class.forName(referenceAdapterName, true, cl);
                    Class[] argTypes = { String.class, PersistenceContext.class};
                    Constructor referenceAdaptorConstructor = referenceAdaptorClass.getDeclaredConstructor(argTypes);
                    Object[] args = new Object[] { getBaseURI().toString(), this };
                    adapters.add((XmlAdapter) referenceAdaptorConstructor.newInstance(args));
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new JPARSException(ex.getMessage());
        }
        return adapters;
    }
    
    
    private boolean getWeavingProperty() {
        // Initialize the properties with their defaults first
        boolean restWeavingEnabled = true;
        boolean fetchGroupWeavingEnabled = true;
        boolean weavingEnabled = true;

        Map<String, Object> properties = this.emf.getProperties();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.equals(PersistenceUnitProperties.WEAVING)) {
                if (!("true".equalsIgnoreCase((String) value)) && !("static".equalsIgnoreCase((String) value))) {
                    weavingEnabled = false;
                }
            }

            if (key.equals(PersistenceUnitProperties.WEAVING_REST)) {
                if (!("true".equalsIgnoreCase((String) value))) {
                    restWeavingEnabled = false;
                }
            }

            if (key.equals(PersistenceUnitProperties.WEAVING_FETCHGROUPS)) {
                if (!("true".equalsIgnoreCase((String) value))) {
                    fetchGroupWeavingEnabled = false;
                }
            }
        }
        return (weavingEnabled && restWeavingEnabled && fetchGroupWeavingEnabled);
    }
}
