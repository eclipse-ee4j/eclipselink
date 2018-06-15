/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//        dclarke/tware - initial
//     2014-09-01-2.6.0 Dmitry Kornilov
//       - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
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
import javax.persistence.RollbackException;
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
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.weaving.RestCollectionAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.weaving.RestReferenceAdapterV2ClassWriter;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.rs.annotations.RestPageableQueries;
import org.eclipse.persistence.jpa.rs.annotations.RestPageableQuery;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureSet;
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilter;
import org.eclipse.persistence.jpa.rs.util.CollectionWrapperBuilder;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.JTATransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.ObjectGraphBuilder;
import org.eclipse.persistence.jpa.rs.util.ResourceLocalTransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.TransactionWrapper;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;
import org.eclipse.persistence.jpa.rs.util.xmladapters.LinkAdapter;
import org.eclipse.persistence.jpa.rs.util.xmladapters.RelationshipLinkAdapter;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

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
    public static final String CLASS_NAME = PersistenceContext.class.getName();
    public static final String SESSION_VERSION_PROPERTY = "jaxb.context.version";

    protected List<XmlAdapter<?, ?>> adapters = null;

    /**
     * The name of the persistence context is used to look it up. By default it will be the
     * persistence unit name of the JPA persistence unit.
     */
    protected String name = null;

    /** The EntityManagerFactory used to interact using JPA **/
    protected EntityManagerFactory emf;

    /** The JAXBConext used to produce JSON or XML **/
    protected JAXBContext jaxbContext = null;

    /** The URI of the Persistence context. This is used to build Links in JSON and XML **/
    protected URI baseURI = null;

    private SessionLog sessionLog = null;

    protected TransactionWrapper transaction = null;

    private Boolean weavingEnabled = null;

    private ServiceVersion version = ServiceVersion.NO_VERSION;

    /** Builder for collection proxies used in JPARS 2.0. **/
    private CollectionWrapperBuilder collectionWrapperBuilder;

    /**
     * JPARS pageable queries map.
     * Key: named query name
     * Value: corresponding RestPageableQuery annotation
     */
    private Map<String, RestPageableQuery> pageableQueries;

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
        init(emfName, emf, defaultURI, ServiceVersion.NO_VERSION);
    }

    /**
     * Instantiates a new persistence context.
     *
     * @param emfName the emf name
     * @param emf the emf
     * @param defaultURI the default uri
     * @param version REST service version
     */
    public PersistenceContext(String emfName, EntityManagerFactoryImpl emf, URI defaultURI, ServiceVersion version) {
        super();
        init(emfName, emf, defaultURI, version);
    }

    private void init(String emfName, EntityManagerFactoryImpl emf, URI defaultURI, ServiceVersion version) {
        this.emf = emf;
        this.name = emfName;
        this.baseURI = defaultURI;
        this.sessionLog = emf.getServerSession().getSessionLog();

        if (version != null) {
            this.version = version;
        } else {
            this.version = ServiceVersion.NO_VERSION;
        }

        if (getServerSession().hasExternalTransactionController()) {
            transaction = new JTATransactionWrapper();
        } else {
            transaction = new ResourceLocalTransactionWrapper();
        }

        try {
            this.jaxbContext = createDynamicJAXBContext(emf.getDatabaseSession());
        } catch (JAXBException jaxbe) {
            JPARSLogger.exception(getSessionLog(), "exception_creating_jaxb_context", new Object[] { emfName, jaxbe.toString() }, jaxbe);
            emf.close();
        } catch (IOException e) {
            JPARSLogger.exception(getSessionLog(), "exception_creating_jaxb_context", new Object[] { emfName, e.toString() }, e);
            emf.close();
        }
    }

    /**
     * Checks if is weaving enabled.
     *
     * @return true, if is weaving enabled
     */
    public boolean isWeavingEnabled() {
        if (this.weavingEnabled == null) {
            this.weavingEnabled = getWeavingProperty();
        }
        return this.weavingEnabled;
    }

    /**
     * Gets the version as it appears in URI.
     *
     * @return The version.
     */
    public String getVersion() {
        return version.getCode();
    }

    /**
     * Gets JPARS version.
     *
     * @return JPARS version.
     */
    public ServiceVersion getServiceVersion() {
        return version;
    }

    /**
     * This method is used to help construct a JAXBContext from an existing EntityManagerFactory.
     *
     * For each package in the EntityManagerFactory, a MetadataSource that is capable of building a JAXBContext
     * that creates the same mappings in JAXB is created.  These MetadataSources are used to constuct the JAXContext
     * that is used for JSON and XML translation.
     * @param metadataSources
     * @param session
     */
    protected void addDynamicXMLMetadataSources(List<Object> metadataSources, AbstractSession session) {
        Set<String> packages = new HashSet<String>();
        for (Class<?> descriptorClass : session.getDescriptors().keySet()) {
            String packageName = "";
            int lastDotIndex = descriptorClass.getName().lastIndexOf('.');
            if (lastDotIndex > 0) {
                packageName = descriptorClass.getName().substring(0, lastDotIndex);
            }
            if (!packages.contains(packageName)) {
                packages.add(packageName);
            }
        }

        for (String packageName : packages) {
            metadataSources.add(getSupportedFeatureSet().getDynamicMetadataSource(session, packageName));
        }
    }

    /**
     * A part of the facade over the JPA API.
     * Persist an entity in JPA and commit.
     * @param tenantId
     * @param entity
     * @throws Exception
     */
    public void create(Map<String, String> tenantId, Object entity) throws Exception {
        EntityManager em = getEmf().createEntityManager(tenantId);
        try {
            transaction.beginTransaction(em);
            em.persist(entity);
            transaction.commitTransaction(em);
        } catch (RollbackException ex) {
            throw ex;
        } catch (Exception ex) {
            transaction.rollbackTransaction(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Create a JAXBContext based on the EntityManagerFactory for this PersistenceContext.
     * @param session
     * @return
     */
    protected JAXBContext createDynamicJAXBContext(AbstractSession session) throws JAXBException, IOException {
        final ServiceVersion cachedContextVersion = (ServiceVersion) session.getProperty(SESSION_VERSION_PROPERTY);
        final JAXBContext cachedContext = (JAXBContext) session.getProperty(JAXBContext.class.getName());
        if (cachedContext != null && cachedContextVersion != null && cachedContextVersion == version) {
            return cachedContext;
        }

        final Map<String, Object> properties = createJAXBProperties(session);
        final ClassLoader cl = session.getDatasourcePlatform().getConversionManager().getLoader();
        final JAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(cl, properties);

        session.setProperty(SESSION_VERSION_PROPERTY, version);
        session.setProperty(JAXBContext.class.getName(), jaxbContext);

        return jaxbContext;
    }

    /**
     * A part of the facade over the JPA API.
     * Create an EntityManagerFactory using the given PersistenceUnitInfo and properties.
     * @param info
     * @param properties
     * @return
     */
    protected EntityManagerFactoryImpl createEntityManagerFactory(PersistenceUnitInfo info, Map<String, ?> properties) {
        PersistenceProvider provider = new PersistenceProvider();
        EntityManagerFactory emf = provider.createContainerEntityManagerFactory(info, properties);
        return (EntityManagerFactoryImpl) emf;
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
        if (oxmLocation != null) {
            metadataLocations.add(oxmLocation);
        }

        Object passedOXMLocations = emf.getProperties().get(JAXBContextProperties.OXM_METADATA_SOURCE);
        if (passedOXMLocations != null) {
            if (passedOXMLocations instanceof Collection) {
                metadataLocations.addAll((Collection) passedOXMLocations);
            } else {
                metadataLocations.add(passedOXMLocations);
            }
        }

        // Add static metadata sources specific to current version
        for (MetadataSource metadataSource : getSupportedFeatureSet().getMetadataSources()) {
            metadataLocations.add(metadataSource);
        }

        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataLocations);

        SessionEventListener sessionEventListener = getSupportedFeatureSet().getSessionEventListener(session);
        if (sessionEventListener != null) {
            properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        }

        // Bug 410095 - JSON_WRAPPER_AS_ARRAY_NAME property doesn't work when jaxb context is created using DynamicJAXBContextFactory
        //properties.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

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
            if (entity != null) {
                em.remove(entity);
            }
            transaction.commitTransaction(em);
        } catch (RollbackException ex) {
            throw JPARSException.exceptionOccurred(ex);
        } catch (Exception ex) {
            transaction.rollbackTransaction(em);
            throw JPARSException.exceptionOccurred(ex);
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
    public boolean doesExist(Map<String, String> tenantId, Object entity) {
        DatabaseSession session = JpaHelper.getDatabaseSession(getEmf());
        return session.doesObjectExist(entity);
    }

    /**
     * Finalize.
     */
    @Override
    protected void finalize() throws Throwable {
        emf.close();
        super.finalize();
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
            ClassDescriptor descriptor = getServerSession().getClassDescriptor(getClass(entityName));
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            Object object;
            if (mapping == null) {
                return null;
            } else if (mapping.isObjectReferenceMapping() || mapping.isCollectionMapping()) {
                DatabaseMapping partnerMapping = null;
                if (partner != null) {
                    ClassDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
                    partnerMapping = referenceDescriptor.getMappingForAttributeName(partner);
                    if (partnerMapping == null) {
                        return null;
                    }
                }
                transaction.beginTransaction(em);
                try {
                    object = em.find(getClass(entityName), id, properties);
                    if (object == null) {
                        return null;
                    }
                    attributeValue = em.merge(attributeValue);
                    setMappingValueInObject(object, attributeValue, mapping, partnerMapping);
                    transaction.commitTransaction(em);
                } catch (RollbackException e) {
                    JPARSLogger.exception(getSessionLog(), "exception_while_updating_attribute", new Object[] { entityName, getName() }, e);
                    return null;
                } catch (Exception e) {
                    JPARSLogger.exception(getSessionLog(), "exception_while_updating_attribute", new Object[] { entityName, getName() }, e);
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
     * @param attribute the attribute
     * @param listItemId
     * @param entity
     * @param partner the partner
     * @return the object
     *
     */
    @SuppressWarnings({"rawtypes" })
    public Object removeAttribute(Map<String, String> tenantId, String entityName, Object id, String attribute, String listItemId, Object entity, String partner)
    {
        EntityManager em = getEmf().createEntityManager(tenantId);
        String fieldName = null;

        try {
            Class<?> clazz = getClass(entityName);
            ClassDescriptor descriptor = getServerSession().getClassDescriptor(clazz);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (mapping == null) {
                return null;
            } else if (mapping.isObjectReferenceMapping() || mapping.isCollectionMapping()) {
                DatabaseMapping partnerMapping = null;
                Object originalAttributeValue = null;
                ClassDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
                if (partner != null) {
                    partnerMapping = referenceDescriptor.getMappingForAttributeName(partner);
                    if (partnerMapping == null) {
                        return null;
                    }
                }
                Field[] fields;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    fields = AccessController.doPrivileged(new PrivilegedGetDeclaredFields(clazz));
                } else {
                    fields = PrivilegedAccessHelper.getDeclaredFields(clazz);
                }

                for (Field field : fields) {
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
            JPARSLogger.exception(getSessionLog(), "exception_while_removing_attribute", new Object[] { fieldName, entityName, getName() }, e);
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
                    Object value;
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        value = AccessController.doPrivileged(new PrivilegedMethodInvoker(getter, entity));
                    } else {
                        value = PrivilegedAccessHelper.invokeMethod(getter, entity);
                    }
                    return value;
                }
            }
        } catch (IntrospectionException | PrivilegedActionException | IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    protected void removeMappingValueFromObject(Object object, Object attributeValue, DatabaseMapping mapping, DatabaseMapping partner) {
        if (mapping.isObjectReferenceMapping()) {
            Object currentValue = mapping.getRealAttributeValueFromObject(object, (AbstractSession) getServerSession());
            if (currentValue.equals(attributeValue)) {
                ((ObjectReferenceMapping) mapping).getIndirectionPolicy().setRealAttributeValueInObject(object, null, true);
                if (partner != null) {
                    removeMappingValueFromObject(attributeValue, object, partner, null);
                }
            }
        } else if (mapping.isCollectionMapping()) {
            boolean removed = ((Collection) mapping.getRealAttributeValueFromObject(object, (AbstractSession) getServerSession())).remove(attributeValue);
            if (removed && partner != null) {
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
        if (descriptor == null) {
            return null;
        }
        return descriptor.getJavaClass();
    }

    /**
     * Gets the jpa server session.
     *
     * @return the jpa server session
     */
    public DatabaseSession getServerSession() {
        // Fix for bug 390786 - JPA-RS: ClassCastException retrieving metadata for Composite Persistence Unit
        return JpaHelper.getDatabaseSession(emf);
    }

    /**
     * Gets the client session.
     *
     * @param em the em
     * @return the client session
     */
    public AbstractSession getClientSession(EntityManager em) {
        UnitOfWork uow = JpaHelper.getEntityManager(em).getUnitOfWork();
        return (AbstractSession) uow;
    }

    /**
     * Lookup the descriptor for the given entity name.
     * This method will look first in the EntityManagerFactory wrapped by this persistence context
     * and return that descriptor.  If one does not exist, it search the JAXBContext and return
     * a descriptor from there.
     * @param entityName
     * @return
     */
    public ClassDescriptor getDescriptor(String entityName) {
        DatabaseSession session = getServerSession();
        ClassDescriptor descriptor = session.getDescriptorForAlias(entityName);
        if (descriptor == null) {
            for (Object ajaxBSession : getJAXBContext().getXMLContext().getSessions()) {
                descriptor = ((Session) ajaxBSession).getClassDescriptorForAlias(entityName);
                if (descriptor != null) {
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
    public ClassDescriptor getDescriptorForClass(Class clazz) {
        DatabaseSession session = getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(clazz);
        if (descriptor == null) {
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
        for (Object ajaxBSession : getJAXBContext().getXMLContext().getSessions()) {
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
        return jaxbContext;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    public SessionLog getSessionLog() {
        return sessionLog;
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
        Object mergedEntity;
        try {
            transaction.beginTransaction(em);
            if (entity instanceof List) {
                List<Object> mergeList = new ArrayList<Object>();
                for (Object o : (List) entity) {
                    mergeList.add(em.merge(o));
                }
                mergedEntity = mergeList;
            } else {
                mergedEntity = em.merge(entity);
            }
            transaction.commitTransaction(em);
            return mergedEntity;
        } catch (RollbackException ex) {
            throw JPARSException.exceptionOccurred(ex);
        } catch (Exception ex) {
            transaction.rollbackTransaction(em);
            throw JPARSException.exceptionOccurred(ex);
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
        DynamicEntity entity;
        try {
            entity = helper.newDynamicEntity(type);
        } catch (IllegalArgumentException e) {
            ClassDescriptor descriptor = getDescriptor(type);
            if (descriptor != null) {
                DynamicType jaxbType = (DynamicType) descriptor.getProperty(DynamicType.DESCRIPTOR_PROPERTY);
                if (jaxbType != null) {
                    return jaxbType.newDynamicEntity();
                }
            }
            JPARSLogger.exception(getSessionLog(), "exception_thrown_while_creating_dynamic_entity", new Object[] { type }, e);
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
        } catch (RollbackException ex) {
            throw JPARSException.exceptionOccurred(ex);
        } catch (Exception ex) {
            transaction.rollbackTransaction(em);
            throw JPARSException.exceptionOccurred(ex);
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
            Iterator<?> i = parameters.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
                String key = (String) entry.getKey();
                Class parameterClass = null;
                int index = dbQuery.getArguments().indexOf(key);
                if (index >= 0) {
                    parameterClass = dbQuery.getArgumentTypes().get(index);
                }
                Object parameter = entry.getValue();
                if (parameterClass != null) {
                    parameter = ConversionManager.getDefaultManager().convertObject(parameter, parameterClass);
                }
                query.setParameter(key, parameter);
            }
        }
        if (hints != null) {
            for (Map.Entry<String, ?> entry : hints.entrySet()) {
                query.setHint(entry.getKey(), entry.getValue());
            }
        }
        return query;
    }

    /**
     * Builds the query.
     *
     * @param tenantId the tenant id
     * @param name the name
     * @param parameters the parameters
     * @param hints the hints
     * @return the query
     */
    @SuppressWarnings("rawtypes")
    public Query buildQuery(Map<String, String> tenantId, String name, Map<?, ?> parameters, Map<String, ?> hints) {
        EntityManager em = getEmf().createEntityManager(tenantId);
        Query query = em.createNamedQuery(name);
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (parameters != null) {
            Iterator<?> i = parameters.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
                String key = (String) entry.getKey();
                Class parameterClass = null;
                int index = dbQuery.getArguments().indexOf(key);
                if (index >= 0) {
                    parameterClass = dbQuery.getArgumentTypes().get(index);
                }
                Object parameter = entry.getValue();
                if (parameterClass != null) {
                    parameter = ConversionManager.getDefaultManager().convertObject(parameter, parameterClass);
                }
                query.setParameter(key, parameter);
            }
        }
        if (hints != null) {
            for (Map.Entry<String, ?> entry : hints.entrySet()) {
                query.setHint(entry.getKey(), entry.getValue());
            }
        }
        return query;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void setMappingValueInObject(Object object, Object attributeValue, DatabaseMapping mapping, DatabaseMapping partner) {
        if (mapping.isObjectReferenceMapping()) {
            ((ObjectReferenceMapping) mapping).getIndirectionPolicy().setRealAttributeValueInObject(object, attributeValue, true);
            if (partner != null) {
                setMappingValueInObject(attributeValue, object, partner, null);
            }
        } else if (mapping.isCollectionMapping()) {
            ((Collection) mapping.getAttributeValueFromObject(object)).add(attributeValue);
            if (partner != null) {
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
        this.jaxbContext = null;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "PersistenceContext(name:" + getName() + ", version:" + getVersion() + ", identityHashCode:" + System.identityHashCode(this) + ")";
    }

    /**
     * Unmarshal entity.
     *
     * @param type the type of the entity to unmarshal
     * @param acceptedMediaType the accepted media type
     * @param in the input stream to unmarshal
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public Object unmarshalEntity(String type, MediaType acceptedMediaType, InputStream in) throws JAXBException {
        if (JPARSLogger.isLoggableFinest(getSessionLog())) {
            in = in.markSupported() ? in : new BufferedInputStream(in);
            // TODO: Make readlimit configurable. Some http servers allow http post size to be unlimited.
            // If this is the case and if an application is sending huge post requests while jpars log
            // level configured to finest, this readlimit might not be sufficient.
            in.mark(52428800); // (~50MB)
            JPARSLogger.entering(getSessionLog(), CLASS_NAME, "unmarshalEntity", in);
        }
        Object unmarshalled = unmarshal(getClass(type), acceptedMediaType, in);
        JPARSLogger.exiting(getSessionLog(), CLASS_NAME, "unmarshalEntity", new Object[] { unmarshalled.getClass().getName(), unmarshalled });
        return unmarshalled;
    }

    /**
     * Unmarshal.
     *
     * @param type the type of the entity to unmarshal
     * @param acceptedMediaType the accepted media type
     * @param in the input stream to unmarshal
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object unmarshal(Class type, MediaType acceptedMediaType, InputStream in) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, acceptedMediaType.toString());
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

        for (XmlAdapter adapter : getAdapters()) {
            unmarshaller.setAdapter(adapter);
        }

        if (acceptedMediaType == MediaType.APPLICATION_JSON_TYPE) {
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
    protected Object wrap(Object entity) {
        if ((entity != null) && (PersistenceWeavedRest.class.isAssignableFrom(entity.getClass()))) {
            if (!doesExist(null, entity)) {
                return entity;
            }
            ClassDescriptor descriptor = getJAXBDescriptorForClass(entity.getClass());
            if (entity instanceof FetchGroupTracker) {
                FetchGroup fetchGroup = new FetchGroup();
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (!(mapping instanceof XMLInverseReferenceMapping)) {
                        fetchGroup.addAttribute(mapping.getAttributeName());
                    }
                }
                (new FetchGroupManager()).setObjectFetchGroup(entity, fetchGroup, null);
                ((FetchGroupTracker) entity)._persistence_setSession(JpaHelper.getDatabaseSession(getEmf()));
            } else if (descriptor.hasRelationships()) {
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (mapping instanceof XMLInverseReferenceMapping) {
                        // we require Fetch groups to handle relationships
                        JPARSLogger.error(getSessionLog(), "weaving_required_for_relationships", new Object[] {});
                        throw JPARSException.invalidConfiguration();
                    }
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
        JPARSLogger.entering(getSessionLog(), CLASS_NAME, "marshallEntity", new Object[] { object, mediaType });
        marshall(object, mediaType, output, true);
        JPARSLogger.exiting(getSessionLog(), CLASS_NAME, "marshallEntity", this, object, mediaType);
    }

    /**
     * Marshall an entity to either JSON or XML.
     *
     * @param object the object to marshal.
     * @param filter the filter (included/excluded fields) to use.
     * @param mediaType the media type (XML/JSON).
     * @param output the result.
     * @throws JAXBException
     */
    public void marshallEntity(Object object, FieldsFilter filter, MediaType mediaType, OutputStream output) throws JAXBException {
        JPARSLogger.entering(getSessionLog(), CLASS_NAME, "marshallEntity", new Object[] { object, filter, mediaType });
        marshall(object, mediaType, output, true, filter);
        JPARSLogger.exiting(getSessionLog(), CLASS_NAME, "marshallEntity", this, object, mediaType);
    }

    /**
     * Marshall an entity to either JSON or XML.
     *
     * @param object
     * @param mediaType
     * @param output
     * @param sendRelationships if this is set to true, relationships will be sent as links instead of sending.
     * the actual objects in the relationships
     * @throws JAXBException
     */
    public void marshall(Object object, MediaType mediaType, OutputStream output, boolean sendRelationships) throws JAXBException {
        marshall(object, mediaType, output, sendRelationships, null);
    }

    /**
     * Marshall an entity to either JSON or XML.
     *
     * @param object the object to marshal.
     * @param mediaType the media type (XML/JSON).
     * @param output the result.
     * @param sendRelationships if this is set to true, relationships will be sent as links instead of sending
     *                          the actual objects in the relationships.
     * @param fieldsFilter      Specifies fields to include/exclude from the response.
     * @throws JAXBException
     */
    public void marshall(final Object object, final MediaType mediaType, final OutputStream output, boolean sendRelationships, final FieldsFilter fieldsFilter) throws JAXBException {
        if (version.compareTo(ServiceVersion.VERSION_2_0) < 0 && sendRelationships) {
            preMarshallEntity(object);
        }

        final Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType.toString());
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);
        marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

        marshaller.setAdapter(new LinkAdapter(getBaseURI().toString(), this));
        marshaller.setAdapter(new RelationshipLinkAdapter(getBaseURI().toString(), this));

        for (XmlAdapter<?, ?> adapter : getAdapters()) {
            marshaller.setAdapter(adapter);
        }

        // v2.0 and higher
        if (version.compareTo(ServiceVersion.VERSION_2_0) >= 0) {
            // Create proxies for collections
            getCollectionWrapperBuilder().wrapCollections(object);

            // Build object graph + fields filtering
            final ObjectGraphBuilder objectGraphBuilder = new ObjectGraphBuilder(this);
            final ObjectGraph objectGraph = objectGraphBuilder.createObjectGraph(object, fieldsFilter);
            if (objectGraph != null) {
                marshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, objectGraph);
            }
        }

        if (mediaType == MediaType.APPLICATION_XML_TYPE && object instanceof List) {
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer;
            try {
                writer = outputFactory.createXMLStreamWriter(output);
                writer.writeStartDocument();
                writer.writeStartElement(ReservedWords.JPARS_LIST_GROUPING_NAME);
                for (Object o : (List<Object>) object) {
                    marshaller.marshal(o, writer);
                }
                writer.writeEndDocument();
                writer.flush();
                postMarshallEntity(object);
            } catch (Exception ex) {
                throw JPARSException.exceptionOccurred(ex);
            }
        } else {
            marshaller.marshal(object, output);
            if (version.compareTo(ServiceVersion.VERSION_2_0) < 0) {
                postMarshallEntity(object);
            }
        }
    }

    /**
     * Process an entity and add any additional data that needs to be added prior to marshalling
     * This method will both single entities and lists of entities
     * @param object
     */
    @SuppressWarnings("rawtypes")
    protected void preMarshallEntity(Object object) {
        if (object instanceof List) {
            for (Object o : ((List) object)) {
                preMarshallIndividualEntity(o);
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
        if (entity instanceof ReportQueryResultListItem) {
            ReportQueryResultListItem item = (ReportQueryResultListItem) entity;
            for (JAXBElement field : item.getFields()) {
                // one or more fields in the MultiResultQueryListItem might be a domain object,
                // so, we need to set the relationshipInfo for those domain objects.
                setRelationshipInfo(field.getValue());
            }
        } else if (entity instanceof SingleResultQueryList) {
            SingleResultQueryList item = (SingleResultQueryList) entity;
            for (JAXBElement field : item.getFields()) {
                // one or more fields in the SingleResultQueryList might be a domain object,
                // so, we need to set the relationshipInfo for those domain objects.
                setRelationshipInfo(field.getValue());
            }
        } else if (entity instanceof ReportQueryResultList) {
            ReportQueryResultList list = (ReportQueryResultList) entity;
            for (ReportQueryResultListItem item : list.getItems()) {
                for (JAXBElement field : item.getFields()) {
                    // one or more fields in the MultiResultQueryList might be a domain object,
                    // so, we need to set the relationshipInfo for those domain objects.
                    setRelationshipInfo(field.getValue());
                }
            }
        } else if (entity instanceof ReadAllQueryResultCollection) {
            ReadAllQueryResultCollection list = (ReadAllQueryResultCollection) entity;
            List<Object> items = list.getItems();
            if ((items != null) && (!items.isEmpty())) {
                for (Object item : items) {
                    setRelationshipInfo(item);
                }
            }
        } else if (entity instanceof ReportQueryResultCollection) {
            ReportQueryResultCollection list = (ReportQueryResultCollection) entity;
            List<ReportQueryResultListItem> items = list.getItems();
            if ((items != null) && (!items.isEmpty())) {
                for (ReportQueryResultListItem item : items) {
                    setRelationshipInfo(item);
                }
            }
        } else {
            setRelationshipInfo(entity);
        }
    }

    private void setRelationshipInfo(Object entity) {
        if ((entity != null) && (entity instanceof PersistenceWeavedRest)) {
            ClassDescriptor descriptor = getServerSession().getClassDescriptor(entity.getClass());
            if (descriptor != null) {
                ((PersistenceWeavedRest) entity)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (mapping.isForeignReferenceMapping()) {
                        ForeignReferenceMapping frMapping = (ForeignReferenceMapping) mapping;
                        RelationshipInfo info = new RelationshipInfo();
                        info.setAttributeName(frMapping.getAttributeName());
                        info.setOwningEntity(entity);
                        info.setOwningEntityAlias(descriptor.getAlias());
                        info.setPersistencePrimaryKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entity, (AbstractSession) getServerSession()));
                        ((PersistenceWeavedRest) entity)._persistence_getRelationships().add(info);
                    } else if (mapping.isEISMapping()) {
                        if (mapping instanceof EISCompositeCollectionMapping) {
                            EISCompositeCollectionMapping eisMapping = (EISCompositeCollectionMapping) mapping;
                            RelationshipInfo info = new RelationshipInfo();
                            info.setAttributeName(eisMapping.getAttributeName());
                            info.setOwningEntity(entity);
                            info.setOwningEntityAlias(descriptor.getAlias());
                            info.setPersistencePrimaryKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(entity, (AbstractSession) getServerSession()));
                            ((PersistenceWeavedRest) entity)._persistence_getRelationships().add(info);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected void postMarshallEntity(Object object) {
        if (object instanceof List) {
            for (Object entity : ((List) object)) {
                if (entity instanceof PersistenceWeavedRest) {
                    ((PersistenceWeavedRest) entity)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
                }
            }
        } else if (object instanceof PersistenceWeavedRest) {
            ((PersistenceWeavedRest) object)._persistence_setRelationships(new ArrayList<RelationshipInfo>());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<XmlAdapter<?, ?>> getAdapters() throws JPARSException {
        if (adapters != null) {
            return adapters;
        }
        adapters = new ArrayList<XmlAdapter<?, ?>>();
        try {
            final ClassLoader cl = getServerSession().getDatasourcePlatform().getConversionManager().getLoader();

            for (ClassDescriptor desc : this.getServerSession().getDescriptors().values()) {
                if (version.compareTo(ServiceVersion.VERSION_2_0) >= 0) {
                    // Version is 2.0 or higher
                    // Collection adapter
                    final String collectionAdapterName = RestCollectionAdapterClassWriter.getClassName(desc.getJavaClass().getName());
                    final Class collectionAdaptorClass = Class.forName(collectionAdapterName, true, cl);
                    final Class[] argTypes = {PersistenceContext.class};
                    final Constructor collectionAdaptorConstructor = collectionAdaptorClass.getDeclaredConstructor(argTypes);
                    final Object[] args = new Object[]{this};
                    adapters.add((XmlAdapter) collectionAdaptorConstructor.newInstance(args));

                    // Reference adapter
                    final String refAdapterName = RestReferenceAdapterV2ClassWriter.getClassName(desc.getJavaClass().getName());
                    final Class refAdaptorClass = Class.forName(refAdapterName, true, cl);
                    final Class[] refAdapterTypes = {PersistenceContext.class};
                    final Constructor refAdaptorConstructor = refAdaptorClass.getDeclaredConstructor(refAdapterTypes);
                    final Object[] refAdapterArgs = new Object[]{this};
                    adapters.add((XmlAdapter) refAdaptorConstructor.newInstance(refAdapterArgs));
                } else {
                    // Version is 1.0 or below
                    // avoid embeddables
                    if (!desc.isAggregateCollectionDescriptor() && !desc.isAggregateDescriptor()) {
                        Class clz = desc.getJavaClass();
                        String referenceAdapterName = RestAdapterClassWriter.constructClassNameForReferenceAdapter(clz.getName());
                        Class referenceAdaptorClass = Class.forName(referenceAdapterName, true, cl);
                        Class[] argTypes1 = {String.class, PersistenceContext.class};
                        Constructor referenceAdaptorConstructor = referenceAdaptorClass.getDeclaredConstructor(argTypes1);
                        Object[] args1 = new Object[]{getBaseURI().toString(), this};
                        adapters.add((XmlAdapter) referenceAdaptorConstructor.newInstance(args1));
                    }
                }
            }
        } catch (RuntimeException | ReflectiveOperationException ex) {
            ex.printStackTrace();
            throw JPARSException.exceptionOccurred(ex);
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

    /**
     * Gets the supported feature set.
     *
     * @return the supported feature set.
     */
    public FeatureSet getSupportedFeatureSet() {
        return version.getFeatureSet();
    }

    /**
     * Finds out is given query pageable or not.
     *
     * @param queryName named query to check.
     * @return true if pageable, false if not.
     */
    public boolean isQueryPageable(String queryName) {
        return getPageableQueries().get(queryName) != null;
    }

    /**
     * Gets REST pageable query details by query name.
     *
     * @param queryName named query name.
     * @return RestPageableQuery or null if query couldn't be found.
     */
    public RestPageableQuery getPageableQuery(String queryName) {
        return getPageableQueries().get(queryName);
    }

    /**
     * Sets the version.
     *
     * @param version the new version.
     */
    public void setVersion(String version) {
        this.version = ServiceVersion.fromCode(version);
    }

    /**
     * Sets the base uri.
     *
     * @param baseURI the new base uri
     */
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }

    /**
     * Getter for pageableQueries property with lazy initialization.
     *
     * @return The initialized pageableQueries property.
     */
    private Map<String, RestPageableQuery> getPageableQueries() {
        // Lazy initialization
        if (pageableQueries == null) {
            initPageableQueries();
        }

        return pageableQueries;
    }

    /**
     * Initializes pageableQueries map by reading RestPageableQueries entity annotations.
     */
    private void initPageableQueries() {
        pageableQueries = new HashMap<String, RestPageableQuery>();

        // Iterate on all entity classes
        for (Class<?> clazz : getServerSession().getProject().getDescriptors().keySet()) {
            if (clazz.isAnnotationPresent(RestPageableQueries.class)) {
                final RestPageableQueries restPageableQueries = clazz.getAnnotation(RestPageableQueries.class);

                // Process each RestPageableQuery annotation in the list
                for (RestPageableQuery restPageableQuery : restPageableQueries.value()) {
                    pageableQueries.put(restPageableQuery.queryName(), restPageableQuery);
                }
            }
        }
    }

    /**
     * Getter for the collectionWrapperBuilder property with lazy initialization.
     *
     * @return the collectionWrapperBuilder.
     */
    public CollectionWrapperBuilder getCollectionWrapperBuilder() {
        if (collectionWrapperBuilder == null) {
            collectionWrapperBuilder = new CollectionWrapperBuilder(this);
        }
        return collectionWrapperBuilder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        PersistenceContext otherContext = (PersistenceContext) other;

        if (name == null) {
            if (otherContext.name != null) {
                return false;
            }
        } else if (!name.equals(otherContext.name)) {
            return false;
        }

        if (version == null) {
            if (otherContext.version != null) {
                return false;
            }
        } else if (!version.equals(otherContext.version)) {
            return false;
        }

        return true;
    }
}
