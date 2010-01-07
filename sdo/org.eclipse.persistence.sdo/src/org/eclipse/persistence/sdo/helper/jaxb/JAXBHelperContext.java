/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.IdentityWeakHashMap;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOCopyHelper;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOEqualityHelper;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegate;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * <p>The JAXBHelperContext is a bridge between POJOs and SDO DataObjects.  The bridge
 * is based on their corresponding XML representations.  For the POJOs the XML
 * representation is specified using JAXB annotations or object-to-XML mappings.</p>
 * 
 * <p>The following steps are required to create the JAXBHelperContext.  The XML schema
 * used in step #3 is the same one that the POJOs are mapped to.  This step has been 
 * separated so that SDO annotations could be added to the XML schema.  
 * <p>Step #1 - Create the JAXBContext</p>
 * <pre>
 * JAXBContext jaxbContext = JAXBContext.newInstance("com.example.customer");
 * </pre>
 * <p>Step #2 - Create the JAXBHelperContext</p>
 * <pre>
 * JAXBHelperContext jaxbHelperContext = new JAXBHelperContext(jaxbContext);
 * </pre>
 * <p>Step #3 - Create the SDO Metadata from an XML Schema</p>
 * <pre>
 * jaxbHelperContext.getXSDHelper().define(xmlSchema);
 * </pre>
 * </p>
 * 
 * <p>
 * The JAXBHelperContext allows you to convert between POJOs and DataObjects using
 * a wrap operation.
 * <pre>
 * Customer customer = new Customer();
 * Address address new Address();
 * address.setStreet("123 Any Street");
 * customer.set(address);
 * 
 * DataObject customerDO = jaxbHelperContext.wrap(customer);
 * customerDO.getString("address/street");  // returns "123 Any Street"
 * </pre> 
 * </p>
 * 
 * <p>
 * The JAXBHelperContext allows you to convert between DataObjects and POJOs using
 * an unwrap operation.
 * <pre>
 * Type customerType = jaxbHelperContext.getType(Customer.class);
 * DataObject customerDO = jaxbHelperContext.getDataFactory().create(customerType);
 * customerDO.set("first-name", "Jane");
 * 
 * Customer customer = jaxbHelperContext.unwrap(customerDO);
 * customer.getFirstName();  // returns "Jane"
 * </pre>
 * </p>
 * 
 * <p>
 * Of course the POJOs may be JPA entities.  Below is an example of wrapping the 
 * results of a JPA query.
 * <pre>
 * EntityManagerFactory emf = Persistence.createEntityManagerFactory("CustomerExample");      
 * EntityManager em = emf.createEntityManager();
 * List<MyEntity> entities = em.createQuery("SELECT e FROM MyEntity e WHERE ...").getResultList();
 * List<DataObject> dataObjects = hc.wrap(entities);
 * </pre> 
 * </p>
 */
public class JAXBHelperContext extends SDOHelperContext {

    private org.eclipse.persistence.jaxb.JAXBContext jaxbContext;
    private Map<Object, SDODataObject> wrapperDataObjects;

    /**
     * Create a new instance of JAXBHelperContext  
     * @param aJAXBContext - The JAXBContext representing the class to XML schema mapping.
     */
    public JAXBHelperContext(JAXBContext aJAXBContext) {
        this(aJAXBContext, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a new instance of JAXBHelperContext  
     * @param aJAXBContext - The JAXBContext representing the class to XML schema mapping.
     * @param aClassLoader - The ClassLoader containing the generated SDO classes/interfaces (if any).
     */
    public JAXBHelperContext(JAXBContext aJAXBContext, ClassLoader aClassLoader) {
        super(aClassLoader);
        wrapperDataObjects = new IdentityWeakHashMap<Object, SDODataObject>();
        jaxbContext = (org.eclipse.persistence.jaxb.JAXBContext) aJAXBContext;
    }

    /**
     * Override the default helpers/factories, replacing them
     * with JAXB aware ones where necessary.
     */
    @Override
    protected void initialize(ClassLoader aClassLoader)  {
        copyHelper = new SDOCopyHelper(this);
        dataFactory = new JAXBDataFactory(this);
        dataHelper = new SDODataHelper(this);
        equalityHelper = new SDOEqualityHelper(this);
        xmlHelper = new JAXBXMLHelper(this, aClassLoader);
        typeHelper = new SDOTypeHelperDelegate(this);
        xsdHelper = new SDOXSDHelperDelegate(this);
    }

    /**
     * Return the JAXBContext.  The JAXBContext represents the Java 
     * class to XML schema information for the POJOs. 
     */
    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    /**
     * Return the SDO type corresponding to the wrapped class. 
     * <pre>
     * Type customerType = jaxbHelperContext.getType(Customer.class);
     * DataObject customerDO = jaxbHelperContext.getDataFactory().create(customerType);
     * </pre>
     */
    public Type getType(Class entityClass) {
        if(null == entityClass) {
            return null;
        }

        XMLDescriptor entityDescriptor = null;
        try {
            entityDescriptor = (XMLDescriptor) jaxbContext.getXMLContext().getSession(entityClass).getDescriptor(entityClass);
        } catch(Exception e) {
            return null;
        }

        XMLSchemaReference schemaReference = entityDescriptor.getSchemaReference();
        if(null == schemaReference) {
            throw SDOException.sdoJaxbNoSchemaReference(entityClass);
        }

        QName qName = schemaReference.getSchemaContextAsQName(entityDescriptor.getNamespaceResolver());
        if(null == qName) {
            throw SDOException.sdoJaxbNoSchemaContext(entityClass);
        }

        Type wrapperType;
        if(entityDescriptor.getSchemaReference().getType() == XMLSchemaReference.COMPLEX_TYPE) {
            wrapperType = getTypeHelper().getType(qName.getNamespaceURI(), qName.getLocalPart());            
        } else {
            Property property = getXSDHelper().getGlobalProperty(qName.getNamespaceURI(), qName.getLocalPart(), true);
            wrapperType = property.getType();
        }
        if(null == wrapperType) {
            throw SDOException.sdoJaxbNoTypeForClassBySchemaContext(entityClass, qName);
        }
        return wrapperType;
    }

    /**
     * Return the wrapped class corresponding to the SDO type. 
     * <pre>
     * Type customerType = jaxbHelperContext.getTypeHelper().getType("urn:customer", "customer");
     * Class customerClass = jaxbHelperContext.getClass(customerType);
     * </pre>
     */
    public Class getClass(Type type) {
        if(null == type) {
            return null;
        }
        return getObjectDescriptor((SDOType) type).getJavaClass();
    }

    /**
     * Return a DataObject that wraps a POJO.  This call should be 
     * made on the root POJO.
     * <pre>
     * Customer customer = new Customer();
     * Address address new Address();
     * address.setStreet("123 Any Street");
     * customer.set(address);
     * 
     * DataObject customerDO = jaxbHelperContext.wrap(customer);
     * customerDO.getString("address/street");  // returns "123 Any Street"
     * </pre> 
     * Multiple calls to wrap for the same instance POJO return the 
     * same instance of DataObject, in other words the following is 
     * always true:
     * <pre>
     * jaxbHelperContext.wrap(customer123) == jaxbHelperContext.wrap(customer123)
     * jaxbHelperContext.wrap(customer123) != jaxbHelperContext.wrap(customer456)
     * </pre>
     */
    public DataObject wrap(Object entity) {
        if(null == entity) {
            return null;
        }
        SDODataObject wrapperDO = wrapperDataObjects.get(entity);
        if(null != wrapperDO) {
            return wrapperDO;
        }

        Type wrapperType = getType(entity.getClass());
        if(null == wrapperType) {
           throw SDOException.sdoJaxbNoTypeForClass(entity.getClass());
        }
        wrapperDO = (SDODataObject) getDataFactory().create(wrapperType);

        JAXBValueStore jaxbValueStore = new JAXBValueStore(this, entity); 
        wrapperDO._setCurrentValueStore(jaxbValueStore);
        jaxbValueStore.initialize(wrapperDO);

        wrapperDataObjects.put(entity, wrapperDO);
        return wrapperDO;
    }

    /**
     * Helper method that configures container information. 
     */
    DataObject wrap(Object entity, Property containmentProperty, DataObject container) {
        SDODataObject sdoDataObject = (SDODataObject) wrap(entity);
        if(null == container) {
            sdoDataObject._setContainmentPropertyName(null);            
        } else {
            sdoDataObject._setContainmentPropertyName(containmentProperty.getName());            
        }
        sdoDataObject._setContainer(container);
        return sdoDataObject;
    }

    /**
     * Perform the wrap operation on each of the POJOs in the collection,
     * and return the results as a List.
     */
    public List<DataObject> wrap(Collection<Object> entities) {
        return wrap(entities, null, null);
    }

    /**
     * Helper method that configures container information. 
     */
    List<DataObject> wrap(Collection<Object> entities, Property containmentProperty, DataObject container) {
        if(null == entities) {
            return new ArrayList<DataObject>(0);
        }
        List<DataObject> dataObjects = new ArrayList<DataObject>(entities.size());
        for(Object entity: entities) {
            dataObjects.add(wrap(entity, containmentProperty, container));
        }
        return dataObjects;
    }

    /**
     * Return the POJO that is wrapped by the DataObject.
     * <pre>
     * Type customerType = jaxbHelperContext.getType(Customer.class);
     * DataObject customerDO = jaxbHelperContext.getDataFactory().create(customerType);
     * DataObject addressDO = customerDO.create("address");
     * addressDO.set("street", "123 Any Street");
     * 
     * Customer customer = (Customer) jaxbHelperContext.unwrap(customerDO);
     * customer.getAddress().getStreet();  // returns "123 Any Street"
     * </pre>
     * Multiple calls to unwrap for the same DataObject must return the 
     * same instance of Object, in other words the following is always true:
     * <pre>
     * jaxbHelperContext.unwrap(customerDO123) == jaxbHelperContext.unwrap(customerDO123)
     * jaxbHelperContext.unwrap(customerDO123) != jaxbHelperContext.unwrap(customerDO456)
     * customer123 == jaxbHelperContext.unwrap(jaxbHelperContext.wrap(customer123))
     * </pre>
     */
    public Object unwrap(DataObject dataObject) {
        try {
            if(null == dataObject) {
                return null;
            }
            SDODataObject sdoDataObject = (SDODataObject) dataObject;
            JAXBValueStore jpaValueStore = (JAXBValueStore) sdoDataObject._getCurrentValueStore();
            return jpaValueStore.getEntity();
        } catch(ClassCastException e) {
            return null;
        }
    }

    /**
     * Perform the unwrap operation on each of the DataObjects in the collection,
     * and return the results as a List.
     */
    public List<Object> unwrap(Collection<DataObject> dataObjects) {
        if(null == dataObjects) {
            return new ArrayList<Object>(0);
        }
        List<Object> entities = new ArrayList<Object>(dataObjects.size());
        for(DataObject dataObject: dataObjects) {
            entities.add(unwrap(dataObject));
        }
        return entities;
    }

    /**
     * Maintain an association between this POJO and DataObject. 
     */
    void putWrapperDataObject(Object anObject, SDODataObject aDataObject) {
        wrapperDataObjects.put(anObject, aDataObject);
    }
    
    /**
     * Get the XML descriptor for the entity class corresponding to the SDO type.
     */
    XMLDescriptor getObjectDescriptor(SDOType sdoType) {
        QName xsdQName = sdoType.getXsdType();
        if(null == xsdQName) {
            xsdQName = sdoType.getQName();
        }
        XPathFragment xPathFragment = new XPathFragment(xsdQName.getLocalPart());
        xPathFragment.setNamespaceURI(xsdQName.getNamespaceURI());
        XMLDescriptor xmlDescriptor = jaxbContext.getXMLContext().getDescriptorByGlobalType(xPathFragment);
        if (null == xmlDescriptor) {
            xmlDescriptor = jaxbContext.getXMLContext().getDescriptor(xsdQName);
            if (null == xmlDescriptor) {
                throw SDOException.sdoJaxbNoDescriptorForType(sdoType.getQName(), xsdQName);
            }
        }
        return xmlDescriptor;
    }

}
