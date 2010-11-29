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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     07/15/2008-1.0.1 Guy Pelletier 
 *       - 240679: MappedSuperclass Id not picked when on get() method accessor
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     01/26/2010-2.0.1 Guy Pelletier 
 *       - 299893: @MapKeyClass does not work with ElementCollection
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     05/04/2010-2.1 Guy Pelletier 
 *       - 309373: Add parent class attribute to EclipseLink-ORM
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     06/22/2010-2.2 Guy Pelletier 
 *       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     09/16/2010-2.2 Guy Pelletier 
 *       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.CopyPolicy;
import org.eclipse.persistence.annotations.InstantiationCopyPolicy;
import org.eclipse.persistence.annotations.CloneCopyPolicy;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.VariableOneToOne;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DerivedIdClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.changetracking.ChangeTrackingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CustomCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.InstantiationCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CloneCopyPolicyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ObjectAccessor;

/**
 * INTERNAL:
 * A abstract class accessor. Holds common metadata for entities, embeddables
 * and mapped superclasses.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
@SuppressWarnings("deprecation")
public abstract class ClassAccessor extends MetadataAccessor {
    private boolean m_isPreProcessed = false;
    private boolean m_isProcessed = false;
    
    private Boolean m_excludeDefaultMappings;
    private Boolean m_metadataComplete;
    
    private ChangeTrackingMetadata m_changeTracking;

    // Various copy policies. Represented individually to facilitate XML writing.
    private CloneCopyPolicyMetadata m_cloneCopyPolicy;
    private CustomCopyPolicyMetadata m_customCopyPolicy;
    private InstantiationCopyPolicyMetadata m_instantiationCopyPolicy;
    
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<MappedSuperclassAccessor> m_mappedSuperclasses = new ArrayList<MappedSuperclassAccessor>();
    
    // In the normal case owning descriptors is a single list. Could only be
    // multiples when dealing with embeddable accessors.
    private List<MetadataDescriptor> m_owningDescriptors = new ArrayList<MetadataDescriptor>();
    
    private MetadataClass m_customizerClass;
    private MetadataClass m_parentClass;
    
    private String m_className;
    private String m_customizerClassName;
    private String m_parentClassName;
    private String m_description;
    
    private XMLAttributes m_attributes;
    
    /**
     * INTERNAL:
     */
    protected ClassAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, new MetadataDescriptor(cls), project);
        
        // Set the class accessor reference on the descriptor.
        getDescriptor().setClassAccessor(this);
    }
    
    /**
     * INTERNAL:
     * Called from MappedSuperclassAccessor. We want to avoid setting the
     * class accessor on the descriptor to be the MappedSuperclassAccessor.
     */
    protected ClassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataDescriptor descriptor) {    
        super(annotation, cls, descriptor, descriptor.getProject());
    }
    
    /**
     * INTERNAL:
     * Add the accessor to the descriptor
     */
    protected void addAccessor(MappingAccessor accessor) {
        if (accessor != null) {
            // Process any converters on this mapping accessor.
            accessor.processConverters();
            
            // Add any embeddedid references to the list of 
            // (@IdClass and @EmbeddedId reference classes) id 'used' classes.
            if (accessor.isEmbeddedId()) {
                getProject().addIdClass(accessor.getReferenceClassName());
            }

            // Add the embeddable accessor to the project. In the case of
            // pre-processing, if we are an embeddable accessor the nested 
            // embeddable will be pre-processed now.
            addPotentialEmbeddableAccessor(accessor.getReferenceClass(), accessor.getClassAccessor());
            
            // Tell an embeddable accessor that is a map key to a collection
            // to pre-process itself.
            if (accessor.isMappedKeyMapAccessor()) {
                MappedKeyMapAccessor mapAccessor = (MappedKeyMapAccessor) accessor;
                MetadataClass mapKeyClass = mapAccessor.getMapKeyClass();
                
                // If the map key class is not specified, we need to look it 
                // up from the accessor type.
                if (mapKeyClass == null || mapKeyClass.equals(void.class)) {
                    // Try to extract the map key class from a generic 
                    // specification. This will throw an exception if it can't.
                    mapKeyClass = accessor.getMapKeyReferenceClass();
                    
                    // Set the map key class.    
                    mapAccessor.setMapKeyClass(mapKeyClass);
                }
                
                // Add the embeddable accessor to the project. In the case of
                // pre-processing, if we are an embeddable accessor the nested 
                // embeddable will be pre-processed now.
                addPotentialEmbeddableAccessor(mapKeyClass, accessor.getClassAccessor());
            }
         
            // Add the accessor to the descriptor.
            getDescriptor().addMappingAccessor(accessor);
        }
    }
    
    /**
     * INTERNAL:
     * Add the accessors from this class accessors java class to the descriptor
     * tied to this class accessor. This method is called for every class
     * accessor and is also called from parent class accessors to each of its
     * subclasses of a TABLE_PER_CLASS inheritance strategy.
     * 
     * Add accessors is called in the preProcess stage and must not be called
     * until its owning class accessor has processed its access type.
     */
    public void addAccessors() {      
        if (m_attributes != null) {
            for (MappingAccessor accessor : m_attributes.getAccessors()) {
                // Load the accessible object from the class.
                MetadataAccessibleObject accessibleObject = null;
                
                // We must init all xml mapping accessors with a reference
                // of their owning class accessor. The mapping accessors 
                // require metatata information from them to ensure they 
                // process themselves correctly.
                accessor.initXMLMappingAccessor(this);
                
                // To load the accessible object we must check the access type
                // on the individual accessors. If no type is defined we will
                // ask the class accessor which can either return an explicit
                // type it specified or a default type (pu default or inherited
                // from a parent class)
                if (accessor.usesVirtualAccess()) {
                    accessibleObject = getAccessibleVirtualMethod(accessor);
                } else if (accessor.usesPropertyAccess()) {
                    accessibleObject = getAccessibleMethod(accessor);
                } else {
                    accessibleObject = getAccessibleField(accessor);
                }
                
                // If we have no accessible object at this point and no
                // exception has been thrown then the user decorated an invalid
                // attribute. A log warning will have been issued, do not
                // further process this accessor.
                if (accessibleObject != null) {
                    // Initialize the accessor with its real accessible object 
                    // now, that is a field or method since it will currently 
                    // hold a reference to its owning class' accessible object.
                    accessor.initXMLObject(accessibleObject, getEntityMappings());
                
                    // It's now safe to init the correct access type for this
                    // mapping accessor since we now have set the actual 
                    // accessible object for this mapping accessor. Note: the 
                    // initAccess call was originally in initXMLObject, but with 
                    // the current processing setup that isn't valid since 
                    // mapping accessors have their accessible object 'faked' 
                    // out for xml merging purposes during XMLAttributes 
                    // initXMLObject call. Doing the access initialization there 
                    // could cause one of two problems: Firstly, an incorrect 
                    // access type setting and secondly and more importantly, a 
                    // null pointer exception (bug 264596) since our descriptor 
                    // hasn't been set which we use to retrieve the default 
                    // access type.
                    accessor.initAccess();
                    
                    // After the accessor has been fully initialized we can ask 
                    // the accessor to validate an attribute type specification 
					// for a virtual class.
                    if (accessor.usesVirtualAccess() && ! accessor.hasAttributeType()) {
                        throw ValidationException.noAttributeTypeSpecification(accessor.getAttributeName(), getJavaClassName(), getLocation());
                    }
                    
                    // Add the accessor to the descriptor's list
                    addAccessor(accessor);
                }
            }
        }
        
        // Process the fields or methods on the class for annotations. Unless
        // we are processing a virtual access type which means we should not 
        // look any further then what is defined in XML.
        if (! usesVirtualAccess()) {
            if (usesPropertyAccess()) {
                addAccessorMethods(false);
            } else {
                addAccessorFields(false);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Create mappings from the fields directly. If the mustBeExplicit flag
     * is true, then we are processing the inverse of an explicit access
     * setting and for a field to be processed it must have a Access(FIELD) 
     * setting.
     */
    protected void addAccessorFields(boolean processingInverse) {
        for (MetadataField metadataField : getJavaClass().getFields().values()) {
            if (metadataField.isAnnotationPresent(Transient.class, getDescriptor()) || metadataField.shouldBeIgnored()) {
                if (!metadataField.areAnnotationsCompatibleWithTransient(getDescriptor())) {
                    throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(metadataField);
                }
            } else {
                // The is valid check will throw an exception if needed.
                if (metadataField.isValidPersistenceField(processingInverse, getDescriptor())) {
                    // If the accessor already exists, it may have come from XML 
                    // or because of an explicit access type setting. E.G. 
                    // Access type is property and we processed the access 
                    // methods for this field, however the field has been tagged 
                    // as access field. We must therefore overwrite the previous 
                    // accessor with this explicit one.
                    if (! getDescriptor().hasMappingAccessor(metadataField.getAttributeName()) || (getDescriptor().hasMappingAccessor(metadataField.getAttributeName()) && processingInverse)) {
                        addAccessor(buildAccessor(metadataField));
                    }
                }
            }
        }
        
        // If we have an explicit access setting we must process the inverse
        // for those accessors that have an Access(PROPERTY) setting.
        if (hasAccess() && ! processingInverse) {
            addAccessorMethods(true);
        }  
    }
    
    /**
     * INTERNAL:
     * Create mappings via the class properties. If the mustBeExplicit flag
     * is true, then we are processing the inverse of an explicit access
     * setting and for a field to be processed it must have a Access(PROPERTY) 
     * setting.
     */
    protected void addAccessorMethods(boolean processingInverse) {
        for (MetadataMethod metadataMethod : getJavaClass().getMethods().values()) {
            if ( metadataMethod.isAnnotationPresent(Transient.class, getDescriptor())) {    
                if (!metadataMethod.areAnnotationsCompatibleWithTransient(getDescriptor())) {
                    throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(metadataMethod);
                }
            } else {
                // The is valid check will throw an exception if needed.
                if (metadataMethod.isValidPersistenceMethod(processingInverse, getDescriptor())) {
                    // If the accessor already exists, it may have come from XML 
                    // or because of an explicit access type setting. E.G. 
                    // Access type is field however the user indicated the we 
                    // should use its access methods. We must therefore 
                    // overwrite the previous accessor with this explicit one.
                    if (! getDescriptor().hasMappingAccessor(metadataMethod.getAttributeName()) || (getDescriptor().hasMappingAccessor(metadataMethod.getAttributeName()) && processingInverse)) {
                        addAccessor(buildAccessor(metadataMethod));
                    }
                }
            }
        }
        
        // If we have an explicit access setting we must process the inverse
        // for those accessors that have an Access(FIELD)setting. 
        if (hasAccess() && ! processingInverse) {
            addAccessorFields(true);
        }  
    }
    
    /**
     * INTERNAL
     * Add an embeddable class to the embeddable accessor list if it is
     * indeed an embeddable. This method is overridden in EmbeddableAccessor
     * and is called during pre-process. At the entity level all we want to do
     * is set the owning descriptor whereas for nested embeddables they'll 
     * need the list of owning descriptors. Any nested embeddables will be 
     * discovered and pre-processed when pre-processing the known list of root 
     * embeddables.
     * @see MetadataProject processStage1()
     */
    protected void addPotentialEmbeddableAccessor(MetadataClass potentialEmbeddableClass, ClassAccessor embeddingAccessor) {
        if (potentialEmbeddableClass != null) {
            EmbeddableAccessor embeddableAccessor = getProject().getEmbeddableAccessor(potentialEmbeddableClass);
        
            if (embeddableAccessor != null) {
                embeddableAccessor.addEmbeddingAccessor(embeddingAccessor);
                embeddableAccessor.addOwningDescriptor(getDescriptor());
                getProject().addRootEmbeddableAccessor(embeddableAccessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Add mapped superclass accessors to inheriting entities.
     * Add new descriptors for these mapped superclasses to the core project.
     */
    protected void addPotentialMappedSuperclass(MetadataClass metadataClass, boolean addMappedSuperclassAccessors) {
        // Get the mappedSuperclass that was stored previously on the project
        MappedSuperclassAccessor accessor = getProject().getMappedSuperclassAccessor(metadataClass);

        if (accessor == null) {
            // If the mapped superclass was not defined in XML then check for a 
            // MappedSuperclass annotation unless the addMappedSuperclassAccessors 
            // flag is false, meaning we are pre-processing for the canonical 
            // model and any and all mapped superclasses should have been 
            // discovered and we need not investigate this class further.
            if (addMappedSuperclassAccessors) {
                if (metadataClass.isAnnotationPresent(MappedSuperclass.class)) {
                    m_mappedSuperclasses.add(new MappedSuperclassAccessor(metadataClass.getAnnotation(MappedSuperclass.class), metadataClass, getDescriptor()));
                    
                    // 266912: process and store mappedSuperclass descriptors on 
                    // the project for later use by the Metamodel API.
                    getProject().addMetamodelMappedSuperclass(new MappedSuperclassAccessor(metadataClass.getAnnotation(MappedSuperclass.class), metadataClass, getProject()), getDescriptor());
                }
            }
        } else {
            // For the canonical model pre-processing we do not need to do any
            // of the reloading (cloning) that we require for the regular
            // metadata processing. Therefore, just add the mapped superclass
            // directly leaving its current descriptor as is. When a mapped
            // superclass accessor is reloaded for a sub entity, its descriptor 
            // is set to that entity's descriptor.
            if (addMappedSuperclassAccessors) {
                // Reload the accessor from XML to get our own instance not 
                // already on the project
                m_mappedSuperclasses.add(reloadMappedSuperclass(accessor, getDescriptor()));
                
                // 266912: process and store mappedSuperclass descriptors on the 
                // project for later use by the Metamodel API Note: we must 
                // again reload our accessor from XML or we will be sharing 
                // instances of the descriptor
                getProject().addMetamodelMappedSuperclass(reloadMappedSuperclass(accessor,  new MetadataDescriptor(metadataClass)), getDescriptor());
            } else {
                m_mappedSuperclasses.add(accessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Create and return the appropriate accessor based on the accessible 
     * object given. Order of checking is important, careful when modifying
     * or adding, check what the isXyz call does to determine if the accessor
     * is of type xyz.
     */
    protected MappingAccessor buildAccessor(MetadataAnnotatedElement accessibleObject) {
        if (accessibleObject.isBasicCollection(getDescriptor())) {
            return new BasicCollectionAccessor(accessibleObject.getAnnotation(BasicCollection.class), accessibleObject, this);
        } else if (accessibleObject.isBasicMap(getDescriptor())) {
            return new BasicMapAccessor(accessibleObject.getAnnotation(BasicMap.class), accessibleObject, this);
        } else if (accessibleObject.isElementCollection(getDescriptor())) {
            return new ElementCollectionAccessor(accessibleObject.getAnnotation(ElementCollection.class), accessibleObject, this);
        } else if (accessibleObject.isVersion(getDescriptor())) {
            return new VersionAccessor(accessibleObject.getAnnotation(Version.class), accessibleObject, this);
        } else if (accessibleObject.isId(getDescriptor()) && ! accessibleObject.isDerivedId(getDescriptor())) {
            return new IdAccessor(accessibleObject.getAnnotation(Id.class), accessibleObject, this);
        } else if (accessibleObject.isDerivedIdClass(getDescriptor())) {
            return new DerivedIdClassAccessor(accessibleObject, this);
        } else if (accessibleObject.isBasic(getDescriptor())) {
            return new BasicAccessor(accessibleObject.getAnnotation(Basic.class), accessibleObject, this);
        } else if (accessibleObject.isEmbedded(getDescriptor())) {
            return new EmbeddedAccessor(accessibleObject.getAnnotation(Embedded.class), accessibleObject, this);
        } else if (accessibleObject.isEmbeddedId(getDescriptor())) {
            return new EmbeddedIdAccessor(accessibleObject.getAnnotation(EmbeddedId.class), accessibleObject, this);
        } else if (accessibleObject.isTransformation(getDescriptor())) { 
            return new TransformationAccessor(accessibleObject.getAnnotation(Transformation.class), accessibleObject, this);
        } else if (accessibleObject.isManyToMany(getDescriptor())) {
            return new ManyToManyAccessor(accessibleObject.getAnnotation(ManyToMany.class), accessibleObject, this);
        } else if (accessibleObject.isManyToOne(getDescriptor())) {
            return new ManyToOneAccessor(accessibleObject.getAnnotation(ManyToOne.class), accessibleObject, this);
        } else if (accessibleObject.isOneToMany(getDescriptor())) {
            // A OneToMany can default and doesn't require an annotation to be present.
            return new OneToManyAccessor(accessibleObject.getAnnotation(OneToMany.class), accessibleObject, this);
        } else if (accessibleObject.isOneToOne(getDescriptor())) {
            // A OneToOne can default and doesn't require an annotation to be present.
            return new OneToOneAccessor(accessibleObject.getAnnotation(OneToOne.class), accessibleObject, this);
        } else if (accessibleObject.isVariableOneToOne(getDescriptor())) {
            // A VariableOneToOne can default and doesn't require an annotation to be present.
            return new VariableOneToOneAccessor(accessibleObject.getAnnotation(VariableOneToOne.class), accessibleObject, this);
        } else if (getDescriptor().ignoreDefaultMappings()) {
            return null;
        } else {
            // Default case (everything else falls into a Basic)
            return new BasicAccessor(accessibleObject.getAnnotation(Basic.class), accessibleObject, this);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void clearMappedSuperclassesAndInheritanceParents() {
        // Re-initialize the mapped superclass list.
        m_mappedSuperclasses.clear();
        
        // Null out the inheritance parent and root descriptor before we start
        // since they will be recalculated and used to determine when to stop
        // looking for mapped superclasses.
        getDescriptor().setInheritanceParentDescriptor(null);
        getDescriptor().setInheritanceRootDescriptor(null);
    }
    
    /**
     * INTERNAL:
     * In some cases the pre-processing may need to be re-done. Namely, during
     * the canonical model generation between compile rounds.
     */
    public void clearPreProcessed() {
        m_isPreProcessed = false;
        
        // Clear any accessors previously gathered.
        getDescriptor().clearMappingAccessors();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ClassAccessor) {
            ClassAccessor accessor = (ClassAccessor) objectToCompare;
            return valuesMatch(getJavaClassName(), accessor.getJavaClassName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean excludeDefaultMappings() {
        return m_excludeDefaultMappings != null && m_excludeDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * Return the accessible field for the given mapping accessor. Validation is
     * performed on the existence of the field.
     */
    protected MetadataField getAccessibleField(MappingAccessor accessor) {
        MetadataField field = getJavaClass().getField(accessor.getName());
        
        if (field == null) {
            throw ValidationException.invalidFieldForClass(accessor.getName(), getJavaClass());
        } else {
            // True will force an exception to be thrown if it is not a valid 
            // field. However, if it is a transient accessor, don't validate it 
            // and return.
            if (accessor.isTransient() || field.isValidPersistenceField(getDescriptor(), true)) {
                return field;    
            }
            
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Return the accessible method for the given mapping accessor. Validation 
     * is performed on the existence of the method by property name or by
     * the access methods if specified.
     */
    protected MetadataMethod getAccessibleMethod(MappingAccessor accessor) {
        if (accessor.hasAccessMethods()) {
            MetadataMethod getMethod = getJavaClass().getMethod(accessor.getGetMethodName(), new String[]{});
            MetadataMethod setMethod = getJavaClass().getMethod(accessor.getSetMethodName(), Arrays.asList(new String[]{getMethod.getReturnType()}));
            getMethod.setSetMethod(setMethod);
            return getMethod;
        } else {
            MetadataMethod method = getJavaClass().getMethodForPropertyName(accessor.getName());

            if (method == null) {
                throw ValidationException.invalidPropertyForClass(accessor.getName(), getJavaClass());
            } else {
                // True will force an exception to be thrown if it is not a 
                // valid method. However, if it is a transient accessor, don't 
                // validate it and return.
                if (accessor.isTransient() || method.isValidPersistenceMethod(getDescriptor(), true)) {
                    return method;
                }
                
                return null;
            }  
        }
    }
    
    /**
     * INTERNAL:
     * This method should only be called when using virtual access and 
     * presumably for dynamic persistence. No method validation is done and 
     * either the access methods specified or the default get and set methods 
     * for name access will be used.
     */
    protected MetadataMethod getAccessibleVirtualMethod(MappingAccessor accessor) {
        // If the mapping accessor does not have access methods specified,
        // set the default access methods.
        if (! accessor.hasAccessMethods()) {
            accessor.setAccessMethods(getDescriptor().getDefaultAccessMethods());
        }

        MetadataMethod getMethod = new MetadataMethod(getMetadataFactory(), getJavaClass());
        MetadataMethod setMethod = new MetadataMethod(getMetadataFactory(), getJavaClass());
        
        // Set the set method on the getMethod and return it.
        getMethod.setSetMethod(setMethod);

        // Make sure we set the attribute name on the getMethod.
        getMethod.setAttributeName(accessor.getName());
        
        // Set the get and set method names.
        getMethod.setName(accessor.getGetMethodName());
        setMethod.setName(accessor.getSetMethodName());

        return getMethod;
    }
    
    /**
     * INTERNAL:
     * Return the access type of this accessor. Assumes all access processing
     * has been performed before calling this method.
     */
    public String getAccessType() {
        if (hasAccess()) {    
            return getAccess();
        } else {
            return getDescriptor().getDefaultAccess();
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public XMLAttributes getAttributes() {
        return m_attributes;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public ChangeTrackingMetadata getChangeTracking() {
        return m_changeTracking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * INTERNAL:
     */
    public CopyPolicyMetadata getCopyPolicy(){
        if (m_cloneCopyPolicy != null){
            return m_cloneCopyPolicy;
        } else if (m_instantiationCopyPolicy != null){
            return m_instantiationCopyPolicy;
        } else {
            return m_customCopyPolicy;
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public CloneCopyPolicyMetadata getCloneCopyPolicy(){
        return m_cloneCopyPolicy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public CustomCopyPolicyMetadata getCustomCopyPolicy(){
        return m_customCopyPolicy;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getCustomizerClass() {
        return m_customizerClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCustomizerClassName() {
        return m_customizerClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getExcludeDefaultMappings() {
        return m_excludeDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getJavaClassName();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public InstantiationCopyPolicyMetadata getInstantiationCopyPolicy(){
        return m_instantiationCopyPolicy;
    }
    
    /**
     * INTERNAL:
     * Return the java class that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    @Override
    public MetadataClass getJavaClass() {
        return (MetadataClass) getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Return the java class name that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    @Override
    public String getJavaClassName() {
        return getJavaClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the mapped superclasses associated with this entity accessor.
     * A call to discoverMappedSuperclassesAndInheritanceParents() should be
     * made before calling this method. 
     * @see preProcess()
     */
    public List<MappedSuperclassAccessor> getMappedSuperclasses() {
        return m_mappedSuperclasses;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getMetadataComplete() {
        return m_metadataComplete;
    }
    
    /**
     * INTERNAL:
     * In most cases the owning descriptor is the descriptor associated with
     * this class accessor. Owning descriptors come into play when dealing
     * with embeddable classes and their accessors. Processing certain accessors 
     * from an embeddable class requires knowledge of owning descriptors that 
     * require metadata settings from processing the embeddable metadata.
     * @see EmbeddableAccessor
     */
    public MetadataDescriptor getOwningDescriptor() {
        return getDescriptor();
    }
    
    /**
     * INTERNAL:
     * In most cases the owning descriptors is the single descriptor associated 
     * with this class accessor. Owning descriptors come into play when dealing
     * with shared embeddable classes (included nested) and their accessors. 
     * Processing certain accessors from an embeddable class requires knowledge 
     * of owning descriptors that require metadata settings from processing the 
     * embeddable metadata.
     * @see EmbeddableAccessor
     */
    public List<MetadataDescriptor> getOwningDescriptors() {
        if (m_owningDescriptors.isEmpty() && ! isEmbeddableAccessor()) {
            m_owningDescriptors.add(getDescriptor());
        }
        
        return m_owningDescriptors;
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataClass getParentClass() {
        return m_parentClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getParentClassName() {
        return m_parentClassName;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasDerivedId() {
        return ! getDescriptor().getDerivedIdAccessors().isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasParentClass() {
        return m_parentClass != null && ! m_parentClass.equals(void.class);
    }
    
    /**
     * INTERNAL:
     * Return whether this ClassAccessor is a MappedSuperclassAccessor
     */
    public boolean isMappedSuperclass() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isMetadataComplete() {
        return m_metadataComplete != null && m_metadataComplete;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has been pre-processed.
     */
    public boolean isPreProcessed() {
        return m_isPreProcessed;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has been processed.
     */
    public boolean isProcessed() {
        return m_isProcessed;
    }
    
    /**
     * INTERNAL: 
     * This method should be subclassed in those methods that need to do 
     * extra initialization.
     */
    public void initXMLClassAccessor(MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project, XMLEntityMappings entityMappings) {
        initXMLAccessor(descriptor, project);
        initXMLObject(accessibleObject, entityMappings);
        
        // Since the the descriptor, project and accessible object are all 
        // available at this point, it is now safe to initialize our access
        // type.
        initAccess();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize single objects.
        initXMLObject(m_changeTracking, accessibleObject);
        initXMLObject(m_cloneCopyPolicy, accessibleObject);
        initXMLObject(m_customCopyPolicy, accessibleObject);
        initXMLObject(m_instantiationCopyPolicy, accessibleObject);
        initXMLObject(m_attributes, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_attributeOverrides, accessibleObject);
        
        // Initialize simple class objects.
        m_customizerClass = initXMLClassName(m_customizerClassName);
        m_parentClass = initXMLClassName(m_parentClassName);
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents a class.
     */
    public boolean isClassAccessor() {
        return true;
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents an embeddable class.
     */
    public boolean isEmbeddableAccessor() {
        return false;
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents an entity class.
     */
    public boolean isEntityAccessor() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Generic class level merging details for entities, mapped superclasses
     * and embeddables.
     */
    @Override
    public void merge(ORMetadata metadata) {
        super.merge(metadata);
        
        ClassAccessor accessor = (ClassAccessor) metadata;
        
        // Simple object merging.
        m_customizerClass = (MetadataClass) mergeSimpleObjects(m_customizerClass, accessor.getCustomizerClass(), accessor, "<customizer>");
        m_customizerClassName = (String) mergeSimpleObjects(m_customizerClassName, accessor.getCustomizerClassName(), accessor, "<customizer>");
        m_parentClass = (MetadataClass) mergeSimpleObjects(m_parentClass, accessor.getParentClass(), accessor, "<parent-class>");
        m_parentClassName = (String) mergeSimpleObjects(m_parentClassName, accessor.getParentClassName(), accessor, "<parent-class>");
        m_description = (String) mergeSimpleObjects(m_description, accessor.getDescription(), accessor, "<description>");
        m_metadataComplete = (Boolean) mergeSimpleObjects(m_metadataComplete, accessor.getMetadataComplete(), accessor, "@metadata-complete");
        m_excludeDefaultMappings = (Boolean) mergeSimpleObjects(m_excludeDefaultMappings, accessor.getExcludeDefaultMappings(), accessor, "@exclude-default-mappings");
        
        // ORMetadata object merging.        
        m_cloneCopyPolicy = (CloneCopyPolicyMetadata) mergeORObjects(m_cloneCopyPolicy, accessor.getCloneCopyPolicy());
        m_customCopyPolicy = (CustomCopyPolicyMetadata) mergeORObjects(m_customCopyPolicy, accessor.getCustomCopyPolicy());
        m_instantiationCopyPolicy = (InstantiationCopyPolicyMetadata) mergeORObjects(m_instantiationCopyPolicy, accessor.getInstantiationCopyPolicy());
        m_changeTracking = (ChangeTrackingMetadata) mergeORObjects(m_changeTracking, accessor.getChangeTracking());
        
        // ORMetadata list merging. 
        m_associationOverrides = mergeORObjectLists(m_associationOverrides, accessor.getAssociationOverrides());
        m_attributeOverrides = mergeORObjectLists(m_attributeOverrides, accessor.getAttributeOverrides());
        
        // ORObjects that merge further ...
        if (m_attributes == null) {
            m_attributes = accessor.getAttributes();
        } else {
            m_attributes.merge(accessor.getAttributes());
        }
    }
    
    /**
     * INTERNAL:
     * The pre-process method is called during regular deployment and metadata
     * processing. 
     */
    public void preProcess() {
        // Process the metadata complete flag now before we start looking
        // for annotations.
        processMetadataComplete();
        
        // Process the exclude default mappings flag now before we start
        // looking for annotations.
        processExcludeDefaultMappings();
        
        // Process the global converters.
        processConverters();
        
        // Add the accessors (won't be processed till process).
        addAccessors();
        
        // Pre-process our list of mapped superclass accessors now.
        for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
            preProcessMappedSuperclassMetadata(mappedSuperclass);
        }
     
        // Mark the class accessor as pre-processed.
        setIsPreProcessed();
    }
    
    /**
     * INTERNAL:
     * The pre-process for canonical model method is called (and only called) 
     * during the canonical model generation. The use of this pre-process allows
     * us to remove some items from the regular pre-process that do not apply
     * to the canonical model generation.
     */
    public void preProcessForCanonicalModel() {
        // Process the correct access type before any other processing.
        processAccessType();
        
        // Process the metadata complete flag now before we start looking
        // for annotations.
        processMetadataComplete(); 
        
        // Process the exclude default mappings flag now before we start
        // looking for annotations.
        processExcludeDefaultMappings();
        
        // Add the accessors and converters on this embeddable.
        addAccessors();
        
        // Mark the class accessor as pre-processed.
        setIsPreProcessed();
    }
    
    /**
     * INTERNAL:
     * Sub classes that support extending mapped superclasses should override 
     * this method to control what is pre-processed from a mapped superclass. 
     * By default it does full pre-processing.
     * 
     * @see EmbeddableAccessor 
     */
    protected void preProcessMappedSuperclassMetadata(MappedSuperclassAccessor mappedSuperclass) {
        mappedSuperclass.preProcess();
    }
    
    /**
     * INTERNAL:
     * This method should be overridden by all class accessors to process their 
     * specific class metadata first then call up to this method to process the 
     * common metadata.
     */
    @Override
    public void process() {
        // Process the attribute override metadata.
        processAttributeOverrides();
                    
        // Process the association override metadata.
        processAssociationOverrides();
        
        // Process the change tracking metadata.
        processChangeTracking();
        
        // Process the customizer metadata.
        processCustomizer();
        
        // Process the copy policy metadata.
        processCopyPolicy();
        
        processPartitioning();
        
        // Process the property metadata.
        processProperties();
        
        // Process the MappedSuperclass(es) metadata now after all our. There 
        // may be several MappedSuperclasses for any given Entity or Embeddable.
        for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
            processMappedSuperclassMetadata(mappedSuperclass);
        }
     
        // Mark the class accessor as processed.
        setIsProcessed();
    }
    
    /**
     * INTERNAL:
     */
    protected abstract void processAccessType();
    
    /**
     * INTERNAL:
     * Process the association override metadata specified on an entity or 
     * mapped superclass. For any given class, XML association overrides are
     * always added first (see processAssociationOverrides()).
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride) {
        // If an association override already exists, need to make some checks
        // to determine if we should throw an exception or log an ignore
        // message.
        if (associationOverride.shouldOverride(getDescriptor().getAssociationOverrideFor(associationOverride.getName()), getLogger(), getDescriptor().getJavaClassName())) {
            getDescriptor().addAssociationOverride(associationOverride);
        }
    }
    
    /**
     * INTERNAL:
     * Process the association override metadata specified on an entity or 
     * mapped superclass. Once the association overrides are processed from
     * XML process the association overrides from annotations. This order of
     * processing must be maintained.
     */
    protected void processAssociationOverrides() {
        // Process the XML association override elements first.
        for (AssociationOverrideMetadata associationOverride : m_associationOverrides) {
            // Process the association override.
            processAssociationOverride(associationOverride);
        }
        
        // Process the association override annotations.
        // Look for an @AssociationOverrides.
        MetadataAnnotation associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Object associationOverride : (Object[]) associationOverrides.getAttributeArray("value")) {
                processAssociationOverride(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, getAccessibleObject()));
            }
        }
        
        // Look for an @AssociationOverride.
        MetadataAnnotation associationOverride = getAnnotation(AssociationOverride.class);
        if (associationOverride != null) {
            processAssociationOverride(new AssociationOverrideMetadata(associationOverride, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. For any given class, XML attribute overrides are
     * always added first (see processAttributeOverrides()).
     */
    protected void processAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        // If an attribute override already exists, need to make some checks
        // to determine if we should throw an exception or log an ignore
        // message.
        if (attributeOverride.shouldOverride(getDescriptor().getAttributeOverrideFor(attributeOverride.getName()), getLogger(), getDescriptor().getJavaClassName())) {
            getDescriptor().addAttributeOverride(attributeOverride);
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. Once the attribute overrides are processed from
     * XML process the attribute overrides from annotations. This order of 
     * processing must be maintained.
     */
    protected void processAttributeOverrides() {
        // Process the XML attribute overrides first.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            // Process the attribute override.
            processAttributeOverride(attributeOverride);
        }
        
        // Process the attribute override annotations.
        // Look for an @AttributeOverrides.
        MetadataAnnotation attributeOverrides = getAnnotation(AttributeOverrides.class);    
        if (attributeOverrides != null) {
            for (Object attributeOverride : (Object[]) attributeOverrides.getAttribute("value")){ 
                processAttributeOverride(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, getAccessibleObject()));
            }
        }
        
        // Look for an @AttributeOverride.
        MetadataAnnotation attributeOverride = getAnnotation(AttributeOverride.class);
        if (attributeOverride != null) {
            processAttributeOverride(new AttributeOverrideMetadata(attributeOverride, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the change tracking setting for this accessor.
     */
    protected void processChangeTracking() {
        MetadataAnnotation changeTracking = getAnnotation(ChangeTracking.class);
        
        if (m_changeTracking != null || changeTracking != null) {
            if (getDescriptor().hasChangeTracking()) {    
                // We must be processing a mapped superclass setting for an
                // entity that has its own change tracking setting. Ignore it 
                // and log a warning.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_changeTracking == null) {
                    new ChangeTrackingMetadata(changeTracking, getAccessibleObject()).process(getDescriptor());
                } else {
                    if (changeTracking != null) {
                        getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, changeTracking, getJavaClassName(), getLocation());
                    }
                    
                    m_changeTracking.process(getDescriptor());
                }
            }
        }   
    }
    
    /**
     * INTERNAL:
     */
    protected void processCopyPolicy(){
        MetadataAnnotation copyPolicy = getAnnotation(CopyPolicy.class);
        MetadataAnnotation instantiationCopyPolicy = getAnnotation(InstantiationCopyPolicy.class);
        MetadataAnnotation cloneCopyPolicy = getAnnotation(CloneCopyPolicy.class);

        if (getCopyPolicy() != null || copyPolicy != null || instantiationCopyPolicy != null || cloneCopyPolicy != null) {
            if (getDescriptor().hasCopyPolicy()){
                // We must be processing a mapped superclass ...
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_COPY_POLICY, getDescriptor().getJavaClass(), getJavaClass());
            }
            
            if (getCopyPolicy() == null) {
                // Look at the annotations.
                if (copyPolicy != null) {
                    if (instantiationCopyPolicy != null || cloneCopyPolicy != null) {
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClassName());
                    }

                    new CustomCopyPolicyMetadata(copyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
                if (instantiationCopyPolicy != null){
                    if (cloneCopyPolicy != null) {
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClassName());
                    }
                    
                    new InstantiationCopyPolicyMetadata(instantiationCopyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
                if (cloneCopyPolicy != null){
                    new CloneCopyPolicyMetadata(cloneCopyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
            } else {
                // We have a copy policy specified in XML.
                if (copyPolicy != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, copyPolicy, getJavaClassName(), getLocation());
                }
                
                if (instantiationCopyPolicy != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, instantiationCopyPolicy, getJavaClassName(), getLocation());
                }
                
                if (cloneCopyPolicy != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, cloneCopyPolicy, getJavaClassName(), getLocation());
                }
                
                getCopyPolicy().process(getDescriptor());
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processCustomizer() {
        MetadataAnnotation customizer = getAnnotation(Customizer.class);
        
        if ((m_customizerClass != null && ! m_customizerClass.equals(void.class)) || customizer != null) {
            if (getDescriptor().hasCustomizer()) {
                // We must be processing a mapped superclass and its subclass
                // override the customizer class, that is, defined its own. Log 
                // a warning that we are ignoring the Customizer metadata on the 
                // mapped superclass for the descriptor's java class.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_customizerClass == null || m_customizerClass.equals(void.class)) { 
                    // Use the annotation value.
                    m_customizerClass = getMetadataClass((String)customizer.getAttribute("value"));
                } else {
                    // Use the xml value and log a message if necessary.
                    if (customizer != null) {
                        getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, customizer, getJavaClassName(), getLocation());
                    }
                }
                
                getProject().addAccessorWithCustomizer(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Allows for processing derived ids, either from an Id or MapsId
     * specification. All referenced accessors are processed first to ensure 
     * the necessary fields are set before the derived id is processed and
     * circular references are checked. 
     */
    public void processDerivedId(HashSet<ClassAccessor> processing, HashSet<ClassAccessor> processed) {
        // Process only if we haven't already done so (inheritance case)
        if (! processed.contains(this)) {            
            // If we appear in the processing list, through the chain of derived
            // id we have come back to ourself. We have a circular reference,
            // throw an exception.
            if (processing.contains(this)) {
                throw ValidationException.idRelationshipCircularReference(processing);
            }
            
            processing.add(this);
            
            for (ObjectAccessor accessor : getDescriptor().getDerivedIdAccessors()) {
                // Check the reference accessor for a derived id and fast
                // track its processing if need be.
                MetadataDescriptor referenceDescriptor = accessor.getReferenceDescriptor();
                ClassAccessor referenceAccessor = referenceDescriptor.getClassAccessor();
                
                if (referenceAccessor.hasDerivedId()) {    
                    referenceAccessor.processDerivedId(processing, processed);
                }
                
                // Now process the relationship, and the derived id.
                if (! accessor.isProcessed()) {
                    accessor.process();
                }
            }
            
            // Once we're done, we'll move ourselves from the processing to 
            // processed step.
            processing.remove(this);
            processed.add(this);
        }
    }

    /**
     * INTERNAL:
     * Process the exclude default mappings metadata. May be specified directly
     * on this class accessor or one of its mapped superclasses.
     */
    protected void processExcludeDefaultMappings() {
        // Set an exclude default mappings flag if specified on the entity class
        // or a mapped superclass.
        if (getExcludeDefaultMappings() != null) {
            getDescriptor().setIgnoreDefaultMappings(excludeDefaultMappings());
        } 
    }
    
    /**
     * INTERNAL
     * Sub classes that support extending mapped superclasses should override 
     * this method to control what is processed from a mapped superclass. By 
     * default it does full processing.
     * 
     * @see EmbeddableAccessor 
     */
    protected void processMappedSuperclassMetadata(MappedSuperclassAccessor mappedSuperclass) {
        mappedSuperclass.process();
    }
    
    /**
     * INTERNAL:
     * Process the accessors for the given class.
     */
    public void processMappingAccessors() {
        // Now tell the descriptor to process its accessors.
        getDescriptor().processMappingAccessors();
    }
    
    /**
     * INTERNAL:
     * Process the metadata complete metadata. May be specified directly on this 
     * class accessor or one of its mapped superclasses.
     */
    protected void processMetadataComplete() {
        // Set a metadata complete flag if specified on the entity class or a 
        // mapped superclass.
        if (getMetadataComplete() != null) {
            getDescriptor().setIgnoreAnnotations(isMetadataComplete());
        } 
    }
    
    /**
     * INTERNAL:
     * If the user specified a parent class set it on the metadata class
     * for this accessor. The parent class is only ever required in a VIRTUAL
     * case when no java class file is available (otherwise we look at the
     * class for the parent). 
     */
    public void processParentClass() {
        if (hasParentClass()) {
            // Set the class the user specified.
            getJavaClass().setSuperclass(getParentClass());
        } else if (getJavaClass().getSuperclass() == null) {
            // Default the superclass to Object.class if no superclass exists.
            getJavaClass().setSuperclass(getMetadataClass(Object.class));   
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the descriptor.
     */
    protected void processProperties() {        
        // Add the XML properties first.
        for (PropertyMetadata property : getProperties()) {
            getDescriptor().addProperty(property);
        }

        // Now add the properties defined in annotations.
        MetadataAnnotation properties = getAnnotation(Properties.class);
        if (properties != null) {
            for (Object property : (Object[]) properties.getAttributeArray("value")) {
                getDescriptor().addProperty(new PropertyMetadata((MetadataAnnotation)property, getAccessibleObject()));
            }
        }
        
        MetadataAnnotation property = getAnnotation(Property.class);
        if (property != null) {
            getDescriptor().addProperty(new PropertyMetadata(property, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * If this class accessor uses VIRTUAL access and is not accessible, add it
     * to our list of virtual classes that will be dynamically created.
     */
    protected void processVirtualClass() {
        if (usesVirtualAccess() && ! getJavaClass().isAccessible()) {
            getProject().addVirtualClass(this);
            
            // In a dynamic configuration, descriptors are dealt/referenced 
            // using an alias. JPA does not provide a way of specifying an alias 
            // for embeddable descriptors and defaulting them in a JPA 
            // configuration could lead to clashes for similarly named 
            // embeddables across packages. So for dynamic use cases, this is
            // currently a limitation, that is, embedabbles must be uniquely
            // named across the persistence unit.
            if (isEmbeddableAccessor()) {
                // Add an alias for this embeddable descriptor.
                getProject().addAlias(Helper.getShortClassName(getJavaClassName()), getDescriptor());
            }
        }
    }
    
    /**
     * INTERNAL:
     * This method resolves generic types. Resolving generic types will be the 
     * responsibility of the metadata factory since each factory could have its 
     * own means to do so and not respect a generic format on the metadata
     * objects.
     */
    protected void resolveGenericTypes(List<String> genericTypes, MetadataClass parent) {
        getMetadataFactory().resolveGenericTypes(getJavaClass(), genericTypes, parent, getDescriptor());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        m_associationOverrides = associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributes(XMLAttributes attributes) {
        m_attributes = attributes;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setChangeTracking(ChangeTrackingMetadata changeTracking) {
        m_changeTracking = changeTracking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setCloneCopyPolicy(CloneCopyPolicyMetadata copyPolicy){
        m_cloneCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setCustomCopyPolicy(CustomCopyPolicyMetadata copyPolicy){
        m_customCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCustomizerClassName(String customizerClassName) {
        m_customizerClassName = customizerClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDescription(String description) {
        m_description = description;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setExcludeDefaultMappings(Boolean excludeDefaultMappings) {
        m_excludeDefaultMappings = excludeDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setInstantiationCopyPolicy(InstantiationCopyPolicyMetadata copyPolicy){
        m_instantiationCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     */
    protected void setIsPreProcessed() {
        m_isPreProcessed = true;    
    }
    
    /**
     * INTERNAL:
     */
    protected void setIsProcessed() {
        m_isProcessed = true;    
    }
    
    /**
     * INTERNAL:
     * Set the java class for this accessor. This is currently called after
     * the class loader has changed and we are adding entity listeners.
     */
    public void setJavaClass(MetadataClass cls) {
        setAccessibleObject(cls);
        getDescriptor().setJavaClass(cls);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMetadataComplete(Boolean metadataComplete) {
        m_metadataComplete = metadataComplete;
    }
    
    /**
     * INTERNAL:
     */
    protected void setParentClass(MetadataClass parentClass) {
        m_parentClass = parentClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setParentClassName(String parentClassName) {
        m_parentClassName = parentClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getJavaClassName();
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses field access. It will first check for 
     * an explicit access type specification, otherwise will use the default 
     * access as specified on the descriptor for this accessor since we may be 
     * processing a mapped superclass.
     */
    public boolean usesFieldAccess() {
        return getAccessType().equals(MetadataConstants.FIELD);
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses property access. It will first check for 
     * an explicit access type specification, otherwise will use the default 
     * access as specified on the descriptor for this accessor since we may be 
     * processing a mapped superclass.
     */
    public boolean usesPropertyAccess() {
        return getAccessType().equals(MetadataConstants.PROPERTY);
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses virtual access. It will first check for 
     * an explicit access type specification, otherwise will use the default 
     * access as specified on the descriptor for this accessor since we may be 
     * processing a mapped superclass.
     */
    public boolean usesVirtualAccess() {
        return getAccessType().equals(MetadataConstants.VIRTUAL);
    }
}
