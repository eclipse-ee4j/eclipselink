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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes  
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataFactory;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Abstract/common level for JPA Object/Relational metadata. This class handles
 * the merging and overriding details for those metadata objects who care about
 * it. For consistency, and ease of future work, all metadata objects added 
 * should extend this class even though they may not currently have a need for 
 * merging and overriding.
 * 
 * Subclasses that care about merging need to concern themselves with the
 * following methods:
 * - getIdentifier() used to compare two named objects.
 * - equals() used to compare if two objects have similar metadata.
 * - setLocation() must be set on the accessible object. From annotations this 
 *   is handled in the constructor. For XML objects you need to ensure their 
 *   init method or processing method sets the location (that is, a mapping 
 *   file) where the element was found.
 *  
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public abstract class ORMetadata {
    // If loaded from an annotation this will be set and is used in the
    // ignore logging message. Note: in a defaulted annotation case, this
    // annotation will be null. This is not an issue though since we're
    // obviously not going to ignore and log a message for this case.
    private MetadataAnnotation m_annotation;
    
    // The accessible object this metadata is tied to.
    private MetadataAccessibleObject m_accessibleObject;
    
    // Location could be 2 things:
    // 1 - URL to a mapping file
    // 2 - Annotated element (Class, Method or Field)
    private Object m_location;
    
    private XMLEntityMappings m_entityMappings;
    
    // The tag name of the XML element. Used in logging messages and validation
    // exceptions.
    private String m_xmlElement;
    
    /**
     * INTERNAL:
     * Used for defaulting case.
     */
    protected ORMetadata() {}
    
    /**
     * INTERNAL:
     * Used for OX loading.
     */
    public ORMetadata(String xmlElement) {
        m_xmlElement = xmlElement;
    }
    
    /**
     * Used for defaulting.
     */
    public ORMetadata(MetadataAccessibleObject accessibleObject) {
        m_location = accessibleObject;
        m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Used for Annotation loading.
     */
    public ORMetadata(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject) {
        m_location = accessibleObject;
        m_annotation = annotation;
        m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Returns the accessible object for this accessor.
     */
    protected MetadataAccessibleObject getAccessibleObject() {
        return m_accessibleObject;
    }
    
    /**
     * INTERNAL:
     * This is a value is that is used when logging messages for overriding.
     * @see shouldOverride
     */
    protected MetadataAnnotation getAnnotation() {
        return m_annotation;
    }
    
    /**
     * INTERNAL:
     */
    public XMLEntityMappings getEntityMappings() {
        return m_entityMappings;
    }
    
    /**
     * INTERNAL:
     * Sub classed must that can uniquely be identified must override this
     * message to allow the overriding and merging to uniquely identify objects.
     * It will also be used when logging messages (that is provide a more
     * detailed message).
     * 
     * @see shouldOverride
     * @see mergeListsAndOverride
     */
    protected String getIdentifier() {
        return "";
    }
    
    /**
     * INTERNAL:
     * Return the Java class for the metadata class using the metadata loader.
     * The loader is the temp loader during predeploy, and the app loader during deploy.
     */
    public Class getJavaClass(MetadataClass metadataClass) {
        return getJavaClass(metadataClass.getName());
    }
    
    /**
     * INTERNAL:
     * Return the Java class for the class name using the metadata loader.
     * The loader is the temp loader during predeploy, and the app loader during deploy.
     */
    public Class getJavaClass(String className) {
        Class primitiveClass = ORMetadata.getPrimitiveClassForName(className);
        if (primitiveClass != null){
            return primitiveClass;
        }
        String convertedClassName = className;
        
        // Array type names need to be converted so they can be used with Class.forName()
        int index = className.indexOf('[');
        if ((index > 0) && (className.charAt(index + 1) == ']')){
            convertedClassName = "[L" + className.substring(0, index) + ";";
        }
        
        if (getEntityMappings() != null) {
            return getEntityMappings().getClassForName(convertedClassName);
        }
        
        return MetadataHelper.getClassForName(convertedClassName, getMetadataFactory().getLoader());
    }
    
    /**
     * INTERNAL:
     */
    public Object getLocation() {
        return m_location;
    }
    
    /**
     * INTERNAL:
     * Return the MetadataClass for the class.
     */
    public MetadataClass getMetadataClass(Class javaClass) {
        if (javaClass == null) {
            return null;
        }
        
        return getMetadataClass(javaClass.getName());
    }
    
    /**
     * INTERNAL:
     * Return the MetadataClass for the class name.
     */
    public MetadataClass getMetadataClass(String className) {
        return getMetadataFactory().getMetadataClass(className);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataFactory getMetadataFactory() {
        if (getAccessibleObject() != null) {
            return getAccessibleObject().getMetadataFactory();
        }
        return getEntityMappings().getMetadataFactory();
    }
    
    /**
     * INTERNAL:
     * This is a value is that is used when logging messages for overriding.
     * @see shouldOverride
     */
    protected String getXMLElement() {
        return m_xmlElement;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasIdentifier() {
        return ! getIdentifier().equals("");
    }
    
    /**
     * INTERNAL:
     * This method should only be called on those objects that were loaded 
     * from XML and that need to initialize a class name. The assumption
     * here is that an entity mappings object will be available. 
     */
    protected MetadataClass initXMLClassName(String className) {
        return getMetadataFactory().getMetadataClass(getEntityMappings().getFullClassName(className));
    }
    
    /**
     * INTERNAL:
     * Any subclass that cares to do any more initialization (e.g. initialize a
     * class) should override this method. 
     */
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        m_accessibleObject = accessibleObject;
        setEntityMappings(entityMappings);
    }
    
    /**
     * INTERNAL:
     */
    protected void initXMLObject(ORMetadata metadata, MetadataAccessibleObject accessibleObject) {
        if (metadata != null) {
            metadata.initXMLObject(accessibleObject, m_entityMappings);
        }
    }
    
    /**
     * INTERNAL:
     * It is assumed this is a list of ORMetadata
     */
    protected void initXMLObjects(List metadatas, MetadataAccessibleObject accessibleObject) {
        for (ORMetadata metadata : (List<ORMetadata>) metadatas) {
            metadata.initXMLObject(accessibleObject, m_entityMappings);
        }
    }
    
    /**
     * INTERNAL:
     * Note: That annotations can default so the annotation may be null.
     */
    public boolean loadedFromAnnotation() {
        return m_annotation != null || ! loadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    protected boolean loadedFromEclipseLinkXML() {
        if (loadedFromXML()) {
            return m_entityMappings.isEclipseLinkORMFile();
        }
        
        return false; 
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return m_entityMappings != null;
    }
    
    /**
     * INTERNAL:
     * Subclasses that care to handle deeper merges should extend this method.
     */
    protected void merge(ORMetadata metadata) {
        // Does nothing at this level ...
    }
    
    /**
     * INTERNAL:
     * Convenience method to merge two lists of metadata objects. This does
     * not check for duplicates or any overrides at this time. Just appends
     * all items from list2 to list1.
     */
    protected List mergeORObjectLists(List list1, List list2) {
        List<ORMetadata> newList = new ArrayList<ORMetadata>();
        
        for (ORMetadata obj1 : (List<ORMetadata>) list1) {
            boolean found = false;
            
            for (ORMetadata obj2 : (List<ORMetadata>) list2) {
                if (obj2.getIdentifier().equals(obj1.getIdentifier())) {
                    if (obj2.shouldOverride(obj1)) {
                        newList.add(obj2);
                    } else {
                        newList.add(obj1);
                    }
                    
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                newList.add(obj1);
            }
        }
        
        // Now go through m2 and see what is not in m1
        for (ORMetadata obj2 : (List<ORMetadata>) list2) {
            boolean found = false;
            
            for (ORMetadata obj1 : (List<ORMetadata>) list1) {
               if (obj2.getIdentifier().equals(obj1.getIdentifier())) {                    
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                newList.add(obj2);
            }
        }
        
        // Assign the first list to the newly built (merged and overridden list)
        return newList;
    }
    
    /**
     * INTERNAL:
     * Convenience method to merge two objects that were loaded from XML. The 
     * merge is complete. If value2 is specified it will override value1, 
     * otherwise, value1 does not change.
     */
    protected ORMetadata mergeORObjects(ORMetadata obj1, ORMetadata obj2) {
        if (obj2 != null) { 
            if (obj1 != null) {
                if (obj2.shouldOverride(obj1)) {
                    return obj2;
                }
            } else {
                return obj2;
            }
        }
        
        return obj1;
    }
    
    /**
     * INTERNAL:
     * Convenience method to merge two primitive boolean values. Merging
     * primitive booleans is a little trickier. Don't want to overwrite
     * a true boolean value with a false boolean when it came from an 
     * EclipseLink ORM file. (false being the default for boolean and meaning
     * the element was not specified)
     */
    protected boolean mergePrimitiveBoolean(boolean value1, boolean value2, ORMetadata otherMetadata, String xmlElement) {    
        Boolean bool1 = (value1) ? new Boolean(true) : null;
        Boolean bool2 = (value2) ? new Boolean(true) : null;
        
        if (bool1 == null && bool2 == null) {
            return false;
        } else {
            return ((Boolean) mergeSimpleObjects(bool1, bool2, otherMetadata, xmlElement)).booleanValue();
        }
    }
    
    /**
     * INTERNAL:
     * Convenience method to merge two objects that were loaded from XML. The 
     * merge is complete. If value2 is specified it will override value1, 
     * otherwise, value1 does not.
     */
    protected Object mergeSimpleObjects(Object obj1, Object obj2, ORMetadata otherMetadata, String xmlElement) {

        if (obj1 == null && obj2 == null) {
            return null;
        } else {
            SimpleORMetadata object1 = (obj1 == null) ? null : new SimpleORMetadata(obj1, getAccessibleObject(), getEntityMappings(), xmlElement);
            SimpleORMetadata object2 = (obj2 == null) ? null : new SimpleORMetadata(obj2, otherMetadata.getAccessibleObject(), otherMetadata.getEntityMappings(), xmlElement);
                    
            // After this call return the value from the returned simple object.
            return ((SimpleORMetadata) mergeORObjects(object1, object2)).getValue();
        }
    }
    
    /**
     * INTERNAL:
     * This method should be called to reload an entity (that was either
     * loaded from XML or an annotation) as a way of cloning it. This is needed
     * when we process TABLE_PER_CLASS inheritance. We must process the parent
     * classes for every subclasses descriptor. The processing is similar to
     * that of processing a mapped superclass, in that we process the parents 
     * with the subclasses context (that is, the descriptor we are given).
     */
    protected EntityAccessor reloadEntity(EntityAccessor entity, MetadataDescriptor descriptor) {
        if (getEntityMappings() == null) {
            // Create a new EntityAccesor.
            EntityAccessor entityAccessor = new EntityAccessor(entity.getAnnotation(), entity.getJavaClass(), entity.getProject());
            // Things we care about ...
            descriptor.setDefaultAccess(entity.getDescriptor().getDefaultAccess());
            entityAccessor.setDescriptor(descriptor);
            return entityAccessor;            
        } else {
            return getEntityMappings().reloadEntity(entity, descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * This method should be called to reload an entity (that was either
     * loaded from XML or an annotation) as a way of cloning it. This is needed
     * when processing TABLE_PER_CLASS inheritance and when building invidiual
     * entity accessor's mapped superclass list. 
     */
    protected MappedSuperclassAccessor reloadMappedSuperclass(MappedSuperclassAccessor mappedSuperclass, MetadataDescriptor descriptor) {
        if (getEntityMappings() == null) {
            // Changing the descriptor on the mapped superclass should work in 
            // theory, since we are just going to go through its accessor list.
            MappedSuperclassAccessor mappedSuperclassAccessor = new MappedSuperclassAccessor(mappedSuperclass.getAnnotation(), mappedSuperclass.getJavaClass(), descriptor);
            return mappedSuperclassAccessor;
        } else {
            return getEntityMappings().reloadMappedSuperclass(mappedSuperclass, descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Set the accessible object for this accessor.
     */
    public void setAccessibleObject(MetadataAccessibleObject accessibleObject) {
        m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Set the entity mappings (mapping file) for this OR object.
     */
    public void setEntityMappings(XMLEntityMappings entityMappings) {
        m_entityMappings = entityMappings;
        m_location = entityMappings.getMappingFileOrURL();
    }
    
    /**
     * INTERNAL:
     * Method to determine if this ORMetadata should override another. Assumes
     * all ORMetadata that call this method have correctly implemented their 
     * equals method.
     */
    public boolean shouldOverride(ORMetadata existing) {        
        MetadataLogger logger = getAccessibleObject().getLogger();
        
        if (existing == null) {
            // There is no existing, no override occurs, just use it!
            return true;
        } else if (existing.equals(this)) {
            // The objects are the same. Could be that they user accidently
            // cut and paste from one file to another or that we are processing
            // an object from a mapped superclass which we have already
            // processed. Therefore, log no messages, ignore it and fall 
            // through to return false.
        } else {
            // The objects are not the same ... need to look at them further.
            if (loadedFromXML() && existing.loadedFromAnnotation()) {
                // Need to override, log a message and return true;
                if (hasIdentifier()) {
                    logger.logConfigMessage(MetadataLogger.OVERRIDE_NAMED_ANNOTATION_WITH_XML, existing.getAnnotation(), getIdentifier(), existing.getLocation(), getLocation());
                } else {
                    logger.logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, existing.getAnnotation(), existing.getLocation(), getLocation());
                }
                
                return true;
            } else if (loadedFromAnnotation() && existing.loadedFromXML()) {
                // Log an override warning.
                if (hasIdentifier()) {
                    logger.logConfigMessage(MetadataLogger.OVERRIDE_NAMED_ANNOTATION_WITH_XML, m_annotation, getIdentifier(), getLocation(), existing.getLocation());
                } else {
                    logger.logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, m_annotation, getLocation(), existing.getLocation());
                }
            } else {
                // Before throwing an exception we need to examine where the
                // objects came from a little further. We know at this point
                // that both objects were either loaded from XML or from 
                // annotations.
                if (loadedFromEclipseLinkXML() && ! existing.loadedFromEclipseLinkXML()) {
                    // Need to override, log a message and return true.
                    if (hasIdentifier()) {
                        logger.logConfigMessage(MetadataLogger.OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML, existing.getXMLElement(), getIdentifier(), existing.getLocation(), getLocation());
                    } else {
                        logger.logConfigMessage(MetadataLogger.OVERRIDE_XML_WITH_ECLIPSELINK_XML, existing.getXMLElement(), existing.getLocation(), getLocation());
                    }
                    
                    return true;
                } else if (! loadedFromEclipseLinkXML() && existing.loadedFromEclipseLinkXML()) {
                    // Log an override warning.
                    if (hasIdentifier()) {
                        logger.logConfigMessage(MetadataLogger.OVERRIDE_NAMED_XML_WITH_ECLIPSELINK_XML, existing.getXMLElement(), getIdentifier(), getLocation(), existing.getLocation());
                    } else {
                        logger.logConfigMessage(MetadataLogger.OVERRIDE_XML_WITH_ECLIPSELINK_XML, existing.getXMLElement(), getLocation(), existing.getLocation());
                    }
                } else {
                    if (loadedFromAnnotation()) {
                        if (hasIdentifier()) {
                            throw ValidationException.conflictingNamedAnnotations(getIdentifier(), m_annotation, getLocation(), existing.getAnnotation(), existing.getLocation());
                        } else {
                            throw ValidationException.conflictingAnnotations(m_annotation, getLocation(), existing.getAnnotation(), existing.getLocation());
                        }
                    } else {
                        if (hasIdentifier()) {
                            throw ValidationException.conflictingNamedXMLElements(getIdentifier(), m_xmlElement, getLocation(), existing.getLocation());
                        } else {
                            throw ValidationException.conflictingXMLElements(m_xmlElement, getAccessibleObject(), getLocation(), existing.getLocation());
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Two lists are the same if they are the same size and their ordered
     * elements are the same.
     */
    protected boolean valuesMatch(List<Object> list1, List<Object> list2) {
        if (list1.size() == list2.size()) {
            for (Object obj1 : list1) {
                if (! list2.contains(obj1)) {
                    return false;
                }
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * INTERNAL:
     */
    protected boolean valuesMatch(Object value1, Object value2) {
        if ((value1 == null && value2 != null) || (value2 == null && value1 != null)) {
            return false;
        } else if (value1 == null && value2 == null) {
            return true;
        } else {
            return value1.equals(value2);
        }
    }
    
    /**
     * INTERNAL:
     * Internal class to represent java type objects. XML only.
     */
    private class SimpleORMetadata extends ORMetadata {
        private Object m_value;
        
        /**
         * INTERNAL:
         */
        public SimpleORMetadata(Object value, MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings, String xmlElement) {
            super(xmlElement);
            
            setAccessibleObject(accessibleObject);
            setEntityMappings(entityMappings);
            m_value = value;
        }
        
        /**
         * INTERNAL:
         */
        @Override
        public boolean equals(Object objectToCompare) {
            if (objectToCompare instanceof SimpleORMetadata) {
                return valuesMatch(getValue(), ((SimpleORMetadata) objectToCompare).getValue());
            }
            
            return false;
        }
        
        /**
         * INTERNAL:
         */
        public Object getValue() {
            return m_value;
        }
    }
        
    // Lookup of classname to Class to resolve primitive classes
    protected static Map<String, Class> primitiveClasses = null;
    
    protected static Class getPrimitiveClassForName(String className){
        if (primitiveClasses == null){
            primitiveClasses = new HashMap<String, Class>();
            primitiveClasses.put("", void.class);
            primitiveClasses.put("void", void.class);
            primitiveClasses.put("Boolean", Boolean.class);
            primitiveClasses.put("Byte", Byte.class);
            primitiveClasses.put("Character", Character.class);
            primitiveClasses.put("Double", Double.class);
            primitiveClasses.put("Float", Float.class);
            primitiveClasses.put("Integer", Integer.class);
            primitiveClasses.put("Long", Long.class);
            primitiveClasses.put("Number", Number.class);
            primitiveClasses.put("Short", Short.class);
            primitiveClasses.put("String", String.class);
            primitiveClasses.put("boolean", boolean.class);
            primitiveClasses.put("byte", byte.class);
            primitiveClasses.put("char", char.class);
            primitiveClasses.put("double", double.class);
            primitiveClasses.put("float", float.class);
            primitiveClasses.put("int", int.class);
            primitiveClasses.put("long", long.class);
            primitiveClasses.put("short", short.class);
            primitiveClasses.put("byte[]", new byte[0].getClass());
            primitiveClasses.put("char[]", new char[0].getClass());
            primitiveClasses.put("boolean[]", new boolean[0].getClass());
            primitiveClasses.put("double[]", new double[0].getClass());
            primitiveClasses.put("float[]", new float[0].getClass());
            primitiveClasses.put("int[]", new int[0].getClass());
            primitiveClasses.put("long[]", new long[0].getClass());
            primitiveClasses.put("short[]", new short[0].getClass());
        }
        if (className == null){
            return void.class;
        }
        return primitiveClasses.get(className);
    }

}


