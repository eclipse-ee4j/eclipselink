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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.weaving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeMethodInfo;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedChangeTracking;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;

/**
 * This class creates a ClassFileTransformer that is used for dynamic bytecode
 * weaving. It is called by {@link org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl#predeploy}
 * <p>
 * <i>Note:</i> The Session's Project is is scanned to ensure that weaving is
 * supported and is <b>modified</b> to suit (set the {@link org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy}
 * for the Descriptor).
 */
public class TransformerFactory {

    public static final String WEAVER_DISABLE_CT_NOT_SUPPORTED = "weaver_change_tracking_disabled_not_supported";
    public static final String WEAVER_FOUND_USER_IMPL_CT = "weaver_user_impl_change_tracking";
    public static final String WEAVER_NULL_PROJECT = "weaver_null_project";
    public static final String WEAVER_DISABLE_BY_SYSPROP = "weaver_disable_by_system_property";
    public static final String WEAVER_CLASS_NOT_IN_PROJECT = "weaver_class_not_in_project";
    public static final String WEAVER_PROCESSING_CLASS = "weaver_processing_class";
    public static final String CANNOT_WEAVE_CHANGETRACKING = "cannot_weave_changetracking";
    public static final String CANNOT_WEAVE_VIRTUAL_ONE_TO_ONE = "cannot_weave_virtual_one_to_one";

    protected Session session;
    protected Collection<MetadataClass> entityClasses;
    protected Map<String, ClassDetails> classDetailsMap;
    protected ClassLoader classLoader;
    protected boolean weaveChangeTracking;
    protected boolean weaveLazy;
    protected boolean weaveFetchGroups;
    protected boolean weaveInternal;

    public static PersistenceWeaver createTransformerAndModifyProject(
            Session session, Collection<MetadataClass> entityClasses, ClassLoader classLoader,
            boolean weaveLazy, boolean weaveChangeTracking, boolean weaveFetchGroups, boolean weaveInternal) {
        if (session == null) {
            throw new IllegalArgumentException("Weaver session cannot be null");
        }
        if (session.getProject() == null) {
            ((AbstractSession)session).log(SessionLog.SEVERE, SessionLog.WEAVER, WEAVER_NULL_PROJECT, null);
            throw new IllegalArgumentException("Weaver session's project cannot be null");
        }
        TransformerFactory tf = new TransformerFactory(session, entityClasses, classLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal);
        tf.buildClassDetailsAndModifyProject();
        return tf.buildPersistenceWeaver();
    }

    public TransformerFactory(Session session, Collection<MetadataClass> entityClasses, ClassLoader classLoader, boolean weaveLazy, boolean weaveChangeTracking, boolean weaveFetchGroups, boolean weaveInternal) {
        this.session = session;
        this.entityClasses = entityClasses;
        this.classLoader = classLoader;
        this.classDetailsMap = new HashMap<String, ClassDetails>();
        this.weaveLazy = weaveLazy;
        this.weaveChangeTracking = weaveChangeTracking;
        this.weaveFetchGroups = weaveFetchGroups;
        this.weaveInternal = weaveInternal;
    }

    /**
     * INTERNAL:
     * Look higher in the hierarchy for the mappings listed in the unMappedAttribute list.
     * 
     * We assume that if a mapping exists, the attribute must either be mapped from the owning
     * class or from a superclass.
     */
    public void addClassDetailsForMappedSuperClasses(MetadataClass clz, ClassDescriptor initialDescriptor, ClassDetails classDetails, Map classDetailsMap, List unMappedAttributes, boolean weaveChangeTracking){
        // This class has inheritance to a mapped entity rather than a MappedSuperClass
        if (initialDescriptor.getInheritancePolicyOrNull() != null && initialDescriptor.getInheritancePolicyOrNull().getParentClass() != null){
            return;
        }

        if (unMappedAttributes.isEmpty()){
            return;
        }

        MetadataClass superClz = clz.getSuperclass();
        if (superClz == null || superClz.isObject()){
            return;
        }
       
        boolean weaveValueHolders = canWeaveValueHolders(superClz, unMappedAttributes);

        List stillUnMappedMappings = null;
        ClassDetails superClassDetails = createClassDetails(superClz, weaveValueHolders, weaveChangeTracking, weaveFetchGroups, weaveInternal);
        superClassDetails.setIsMappedSuperClass(true);
        if (!initialDescriptor.usesPropertyAccessForWeaving()){
            superClassDetails.useAttributeAccess();
        }
        if (!classDetailsMap.containsKey(superClassDetails.getClassName())){
            stillUnMappedMappings = storeAttributeMappings(superClz, superClassDetails, unMappedAttributes, weaveValueHolders);
            classDetailsMap.put(superClassDetails.getClassName() ,superClassDetails);
        }

        if (((stillUnMappedMappings != null) && (stillUnMappedMappings.size() > 0))){
            addClassDetailsForMappedSuperClasses(superClz, initialDescriptor, classDetails, classDetailsMap, stillUnMappedMappings, weaveChangeTracking);
        }
    }

    public PersistenceWeaver buildPersistenceWeaver() {
        return new PersistenceWeaver(session, classDetailsMap);
    }

    /**
     * Build a list ClassDetails instance that contains a ClassDetails for each class
     * in our entities list.
     */
    public void buildClassDetailsAndModifyProject() {
        if (entityClasses != null && entityClasses.size() > 0) {
            // scan thru list building details of persistent classes
            for (MetadataClass metaClass : entityClasses) {
                // check to ensure that class is present in project
                // this will be a relational descriptor because MetadataClass only describes relational descriptors
                ClassDescriptor descriptor = findDescriptor(session.getProject(), metaClass.getName());
                if (descriptor == null) {
                    log(SessionLog.FINER, WEAVER_CLASS_NOT_IN_PROJECT, new Object[]{metaClass.getName()});
                } else {
                    log(SessionLog.FINER, WEAVER_PROCESSING_CLASS, new Object[]{metaClass.getName()});

                    boolean weaveValueHoldersForClass = weaveLazy && canWeaveValueHolders(metaClass, descriptor.getMappings());
                    boolean weaveChangeTrackingForClass = canChangeTrackingBeEnabled(descriptor, metaClass, weaveChangeTracking);
                    
                    ClassDetails classDetails = createClassDetails(metaClass, weaveValueHoldersForClass, weaveChangeTrackingForClass, weaveFetchGroups, weaveInternal);
                    if (descriptor.isDescriptorTypeAggregate()) {
                        classDetails.setIsEmbedable(true);
                        classDetails.setShouldWeaveFetchGroups(false);
                    }
                    if (!descriptor.usesPropertyAccessForWeaving()){
                        classDetails.useAttributeAccess();
                    }
                    
                    classDetails.getVirtualAccessMethods().addAll(descriptor.getVirtualAttributeMethods());

                    List unMappedAttributes = storeAttributeMappings(metaClass, classDetails, descriptor.getMappings(), weaveValueHoldersForClass);
                    classDetailsMap.put(classDetails.getClassName() ,classDetails);

                    classDetails.setShouldWeaveConstructorOptimization((classDetails.getDescribedClass().getFields().size() - (descriptor.getMappings().size() - unMappedAttributes.size()))<=0);

                    if (!unMappedAttributes.isEmpty()){
                        addClassDetailsForMappedSuperClasses(metaClass, descriptor, classDetails, classDetailsMap, unMappedAttributes, weaveChangeTracking);
                    }
                }
            }

            // hookup superClassDetails
            for (Iterator i = classDetailsMap.values().iterator(); i.hasNext();) {
                ClassDetails classDetails = (ClassDetails)i.next();
                ClassDetails superClassDetails = classDetailsMap.get(classDetails.getSuperClassName());
                if (superClassDetails == null) {
                    ClassDescriptor descriptor = findDescriptor(session.getProject(), classDetails.getClassName());
                    if (descriptor != null && descriptor.hasInheritance()){
                        superClassDetails = classDetailsMap.get(descriptor.getInheritancePolicy().getParentClassName());
                    }
                }
                if (superClassDetails != null) {
                    classDetails.setSuperClassDetails(superClassDetails);
                }
            }

            // Fix weaveChangeTracking based on superclasses,
            // we should only weave change tracking if our whole hierarchy can.
            for (Iterator i = classDetailsMap.values().iterator(); i.hasNext();) {
                ClassDetails classDetails = (ClassDetails)i.next();
                classDetails.setShouldWeaveChangeTracking(classDetails.canWeaveChangeTracking());
            }
        }
    }

    /**
     * Check to ensure the class meets all the conditions necessary to enable change tracking
     * This could occur either if the class already has change tracking implemented or if the
     * class is capable of having change tracking woven based on the descriptor.
     */
    protected boolean canChangeTrackingBeEnabled(ClassDescriptor descriptor, MetadataClass clz, boolean globalWeaveChangeTracking) {
        if (!globalWeaveChangeTracking) {
          return false;
        }
        // If the descriptor was configured to not use change tracking then disable it, also enable if configure explictly.
        if (descriptor.getObjectChangePolicyInternal() != null) {
            if (descriptor.getObjectChangePolicyInternal().isDeferredChangeDetectionPolicy()) {
                return false;
            } else if (descriptor.getObjectChangePolicyInternal().isObjectChangeTrackingPolicy()) {
                // Include object and attribute.
                return true;
            }
        }
        boolean canWeaveChangeTracking = descriptor.supportsChangeTracking(session.getProject());
        if (!canWeaveChangeTracking) {
            log(SessionLog.CONFIG, CANNOT_WEAVE_CHANGETRACKING, new Object[]{descriptor.getJavaClassName()});
        }
        return canWeaveChangeTracking;
    }

    protected boolean wasChangeTrackingAlreadyWeaved(Class clz){
        Class[] interfaces = clz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class c = interfaces[i];
            if (c.getName().equals(PersistenceWeavedChangeTracking.class.getName())){
                return true;                
            }
        }
        return false;
    }

    /**
     * Determine if value holders are required to be weaved into the class.
     */
    protected boolean canWeaveValueHolders(MetadataClass clz, List mappings) {
        // we intend to change to fetch=LAZY 1:1 attributes to ValueHolders
        boolean weaveValueHolders = false; 
        for (Iterator iterator = mappings.iterator(); iterator.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping)iterator.next();
            String attributeName = mapping.getAttributeName();
            if (mapping.isForeignReferenceMapping()) {
                ForeignReferenceMapping foreignReferenceMapping = (ForeignReferenceMapping)mapping;
                MetadataClass typeClass = getAttributeTypeFromClass(clz, attributeName, foreignReferenceMapping, true);
                if ((foreignReferenceMapping.getIndirectionPolicy() instanceof BasicIndirectionPolicy) &&
                        (typeClass != null)  && (!typeClass.extendsInterface(ValueHolderInterface.class))) {
                    weaveValueHolders = true;
                 }
             }
        }
        return weaveValueHolders;
    }

    private ClassDetails createClassDetails(MetadataClass metadataClass, boolean weaveValueHolders, boolean weaveChangeTracking, boolean weaveFetchGroups, boolean weaveInternal) {
        // compose className in JVM 'slash' format
        // instead of regular Java 'dotted' format
        String className = Helper.toSlashedClassName(metadataClass.getName());
        String superClassName = Helper.toSlashedClassName(metadataClass.getSuperclass().getName());
        ClassDetails classDetails = new ClassDetails();
        classDetails.setDescribedClass(metadataClass);
        classDetails.setClassName(className);
        classDetails.setSuperClassName(superClassName);
        classDetails.setShouldWeaveValueHolders(weaveValueHolders);
        classDetails.setShouldWeaveChangeTracking(weaveChangeTracking);
        classDetails.setShouldWeaveFetchGroups(weaveFetchGroups);
        classDetails.setShouldWeaveInternal(weaveInternal);
        MetadataMethod method = metadataClass.getMethod("clone", new ArrayList(), false);
        classDetails.setImplementsCloneMethod(method != null);
        return classDetails;
    }

    /**
     * Find a descriptor by name in the given project
     * used to avoid referring to descriptors by class.
     * This avoids having to construct a project by class facilitating weaving
     */
    protected ClassDescriptor findDescriptor(Project project, String className){
        Iterator iterator = project.getOrderedDescriptors().iterator();
        while (iterator.hasNext()){
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            if (descriptor.getJavaClassName().equals(className)){
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Return if the class contains the field.
     */
    protected boolean hasFieldInClass(MetadataClass metadataClass, String attributeName) {       
        return metadataClass.getField(attributeName) != null;
    }
    
    /**
     * Return the class which is the source of the attribute.
     * i.e. the class that defines the attribute in its class file.
     */
    private MetadataClass getAttributeDeclaringClass(MetadataClass metadataClass, String attributeName) {       
        MetadataField field = metadataClass.getField(attributeName);
        return field.getDeclaringClass();
    }

    /**
     * Use the database mapping for an attribute to find it's type.  The type returned will either be
     * the field type of the field in the object or the type returned by the getter method.
     */
    private MetadataClass getAttributeTypeFromClass(MetadataClass metadataClass, String attributeName, DatabaseMapping mapping, boolean checkSuperclass){       
        String getterMethod = mapping.getGetMethodName();
        if (mapping.isAbstractDirectMapping() && mapping.getAttributeAccessor().isVirtualAttributeAccessor()){
            return metadataClass.getMetadataClass(((AbstractDirectMapping)mapping).getAttributeClassificationName());
        } else if (mapping != null && getterMethod != null) {
            MetadataMethod method = metadataClass.getMethod(getterMethod, new ArrayList(), checkSuperclass);
            if (method == null) {
                return null;
            }
            return method.getMetadataClass(method.getReturnType());
        } else {
            MetadataField field = metadataClass.getField(attributeName, checkSuperclass);
            if (field == null) {
                return null;
            }
            return field.getMetadataClass(field.getType());
        }
    }

    /**
     *  INTERNAL:
     *  Store a set of attribute mappings on the given ClassDetails that correspond to the given class.
     *  Return the list of mappings that is not specifically found on the given class.  These attributes will 
     *  be found on MappedSuperclasses.
     */
    protected List storeAttributeMappings(MetadataClass metadataClass, ClassDetails classDetails, List mappings, boolean weaveValueHolders) {      
        List unMappedAttributes = new ArrayList();
        Map<String, AttributeDetails> attributesMap = new HashMap<String, AttributeDetails>();
        Map<String, AttributeDetails> settersMap = new HashMap<String, AttributeDetails>();
        Map<String, AttributeDetails> gettersMap = new HashMap<String, AttributeDetails>();

        for (Iterator iterator = mappings.iterator(); iterator.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping)iterator.next();
            
            // Can't weave something that isn't really there and not going to be there.
            if (mapping.isMultitenantPrimaryKeyMapping()) {
                continue;
            }
            
            String attribute = mapping.getAttributeName();
            AttributeDetails attributeDetails = new AttributeDetails(attribute, mapping);

            if (mapping.getAttributeAccessor().isVirtualAttributeAccessor()){
                attributeDetails.setVirtualProperty(mapping.getAttributeAccessor().isVirtualAttributeAccessor());
                if ((classDetails.getInfoForVirtualGetMethod(mapping.getGetMethodName()) == null) && (classDetails.getInfoForVirtualSetMethod(mapping.getSetMethodName()) == null)){
                    VirtualAttributeMethodInfo info = new VirtualAttributeMethodInfo(mapping.getGetMethodName(), mapping.getSetMethodName());
                    classDetails.getVirtualAccessMethods().add(info);
                }
            }
            // Initial look for the type of this attribute.
            MetadataClass typeClass = getAttributeTypeFromClass(metadataClass, attribute, mapping, false);
            if (typeClass == null) {
                attributeDetails.setAttributeOnSuperClass(true);
                unMappedAttributes.add(mapping);
            }

            // Set the getter and setter method names if the mapping uses property access.
            if (mapping.getGetMethodName() != null) {
                gettersMap.put(mapping.getGetMethodName(), attributeDetails);
                attributeDetails.setGetterMethodName(mapping.getGetMethodName());
                if (mapping.getSetMethodName() != null) {
                    settersMap.put(mapping.getSetMethodName(), attributeDetails);
                    attributeDetails.setSetterMethodName(mapping.getSetMethodName());
                }

                if (mapping.isForeignReferenceMapping() && ((ForeignReferenceMapping) mapping).requiresTransientWeavedFields()) {
                    attributeDetails.setWeaveTransientFieldValueHolders();
                }
                
                // If the property has a matching field, then weave it instead (unless internal weaving is disabled).
                if (this.weaveInternal) {
                    attributeDetails.setHasField(hasFieldInClass(metadataClass, attribute));
                }
            } else {
                attributeDetails.setHasField(true);
            }
            // If the attribute has a field, then the weaver needs to know in which class it was defined.
            if (attributeDetails.hasField()) {
                attributeDetails.setDeclaringType(Type.getType(getAttributeDeclaringClass(metadataClass, attribute).getTypeName()));
            }

            // Check for lazy/value-holder indirection.
            if (mapping.isForeignReferenceMapping()) {
                ForeignReferenceMapping foreignReferenceMapping = (ForeignReferenceMapping)mapping;

                // repopulate the reference class with the target of this mapping
                attributeDetails.setReferenceClassName(foreignReferenceMapping.getReferenceClassName());
                if (attributeDetails.getReferenceClassName() != null) {
                    MetadataClass referenceClass = metadataClass.getMetadataFactory().getMetadataClass(attributeDetails.getReferenceClassName());
                    attributeDetails.setReferenceClassType(Type.getType(referenceClass.getTypeName()));
                }

                // This time, look up the type class and include the superclass so we can check for lazy.
                if (typeClass == null){
                    typeClass = getAttributeTypeFromClass(metadataClass, attribute, foreignReferenceMapping, true);
                }
                if (weaveValueHolders && (foreignReferenceMapping.getIndirectionPolicy() instanceof BasicIndirectionPolicy) &&
                        (typeClass != null)  && (!typeClass.extendsInterface(ValueHolderInterface.class))) {
                    if (mapping.isObjectReferenceMapping() && attributeDetails.isVirtualProperty()){
                        classDetails.setShouldWeaveValueHolders(false);
                        log(SessionLog.WARNING, CANNOT_WEAVE_VIRTUAL_ONE_TO_ONE, new Object[]{classDetails.getClassName(), attributeDetails.getAttributeName()});
                    } else {
                        attributeDetails.weaveVH(weaveValueHolders, foreignReferenceMapping);
                    }
                }
            }

            if (attributeDetails.getReferenceClassType() == null){
                if (typeClass == null){
                    typeClass = getAttributeTypeFromClass(metadataClass, attribute, mapping, true);
                }
            }
            if (typeClass != null) {
                attributeDetails.setReferenceClassName(typeClass.getName());
                attributeDetails.setReferenceClassType(Type.getType(typeClass.getTypeName()));
            }
            attributesMap.put(attribute, attributeDetails);    
        }
        classDetails.setAttributesMap(attributesMap);
        classDetails.setGetterMethodToAttributeDetails(gettersMap);
        classDetails.setSetterMethodToAttributeDetails(settersMap);
        return unMappedAttributes;
    }

    protected void log(int level, String msg, Object[] params) {
        ((org.eclipse.persistence.internal.sessions.AbstractSession)session).log(level,
            SessionLog.WEAVER, msg, params);
    }

}
