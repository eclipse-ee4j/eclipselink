/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.weaving;

import java.util.*;

import org.eclipse.persistence.internal.libraries.asm.*;
import org.eclipse.persistence.internal.libraries.asm.commons.*;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.Annotation;

/**
 * INTERNAL:
 * Weaves classes to allow them to support TopLink indirection.
 * Classes are weaved to add a variable of type ValueHolderInterface for each attribute
 * that uses indirection.  In addition, access methods are added for the new variable.
 * Also, triggers the process of weaving the methods of the class.  
 * @see org.eclipse.persistence.internal.weaving.MethodWeaver
 */

public class ClassWeaver extends ClassAdapter implements Constants {

    // PersistenceWeaved
    public static final String PERSISTENCE_WEAVED_SHORT_SIGNATURE = "org/eclipse/persistence/internal/weaving/PersistenceWeaved";
    
    // ValueHolders
    public static final String TW_LAZY_SHORT_SIGNATURE = "org/eclipse/persistence/internal/weaving/PersistenceWeavedLazy";
    public static final String VHI_CLASSNAME = "org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface";
    public static final String VH_SHORT_SIGNATURE = "org/eclipse/persistence/indirection/ValueHolder";
    public static final String VHI_SHORT_SIGNATURE = "org/eclipse/persistence/indirection/WeavedAttributeValueHolderInterface";
    public static final String VHI_SIGNATURE = "L" + VHI_SHORT_SIGNATURE +";";
    
    // Change tracking
    public static final String TW_CT_SHORT_SIGNATURE = "org/eclipse/persistence/internal/weaving/PersistenceWeavedChangeTracking";
    public static final String PCL_SHORT_SIGNATURE = "java/beans/PropertyChangeListener";
    public static final String PCL_SIGNATURE = "L" + PCL_SHORT_SIGNATURE +";";
    public static final String CT_SHORT_SIGNATURE = "org/eclipse/persistence/descriptors/changetracking/ChangeTracker";
    public static final String PCE_SHORT_SIGNATURE = "java/beans/PropertyChangeEvent";
    public static final String PCE_SIGNATURE = "L" + PCE_SHORT_SIGNATURE +";";
    
    // PersistenceEntity
    public static final String TOPLINK_ENTITY_SHORT_SIGNATURE = "org/eclipse/persistence/internal/descriptors/PersistenceEntity";
    public static final String VECTOR_SIGNATURE = "Ljava/util/Vector;";
    public static final String CACHEKEY_SIGNATURE = "Lorg/eclipse/persistence/internal/identitymaps/CacheKey;";
    
    // Fetch groups
    public static final String WEAVED_FETCHGROUPS_SHORT_SIGNATURE = "org/eclipse/persistence/internal/weaving/PersistenceWeavedFetchGroups";
    public static final String FETCHGROUP_TRACKER_SHORT_SIGNATURE = "org/eclipse/persistence/queries/FetchGroupTracker";
    public static final String FETCHGROUP_SHORT_SIGNATURE = "org/eclipse/persistence/queries/FetchGroup";
    public static final String FETCHGROUP_SIGNATURE = "Lorg/eclipse/persistence/queries/FetchGroup;";
    public static final String SESSION_SIGNATURE = "Lorg/eclipse/persistence/sessions/Session;";
    public static final String PBOOLEAN_SIGNATURE = "Z";
    public static final String LONG_SIGNATURE = "J";
    
    // Cloneable
    public static final String CLONEABLE_SHORT_SIGNATURE = "java/lang/Cloneable";
    
    /** Stores information on the class gathered from the temp class loader and descriptor. */
    protected ClassDetails classDetails;
    /** Used to generate the serialization serial UUID based on the original class. */
    protected SerialVersionUIDAdder uuidGenerator;
    
    // Keep track of what was weaved.
    protected boolean alreadyWeaved = false;
    public boolean weaved = false;
    public boolean weavedLazy = false;
    public boolean weavedPersistenceEntity = false;
    public boolean weavedChangeTracker = false;
    public boolean weavedFetchGroups = false;
    
    /**
     * Used for primitive conversion.  Returns the name of the class that wraps a given type.
     */
    public static String wrapperFor(int sort) {
        switch (sort) {
            case Type.BOOLEAN:
                return "java/lang/Boolean";
            case Type.BYTE:
                return "java/lang/Byte";
            case Type.CHAR:
                return "java/lang/Character";
            case Type.SHORT:
                return "java/lang/Short";
            case Type.INT:
                return "java/lang/Integer";
            case Type.FLOAT:
                return "java/lang/Float";
            case Type.LONG:
                return "java/lang/Long";
            case Type.DOUBLE:
                return "java/lang/Double";
        }
        return null;
    }

    /**
     * Return the get method name weaved for a value-holder attribute.
     */
    public static String getWeavedValueHolderGetMethodName(String attributeName) {
        return "_persistence_get" + attributeName + "_vh";
    }
    
    /**
     * Return the set method name weaved for a value-holder attribute.
     */
    public static String getWeavedValueHolderSetMethodName(String attributeName) {
        return "_persistence_set" + attributeName + "_vh";
    }
    
    public ClassWeaver(ClassWriter classWriter, ClassDetails classDetails) {
        super(classWriter);
        this.classDetails = classDetails;
        this.uuidGenerator = new SerialVersionUIDAdder(classWriter);
    }

    /**
     * Add a variable of type ValueHolderInterface to the class.  When this method has been run, the
     * class will contain a variable declaration similar to the following:
     *  
     * private ValueHolderInterface _persistence_variableName_vh;
     */
    public void addValueHolder(AttributeDetails attributeDetails){
        String attribute = attributeDetails.getAttributeName();
        RuntimeVisibleAnnotations annotations = null;
        // only mark @Transient if this is property access.  Otherwise, the @Transient annotation could mistakenly
        // cause the class to use attribute access.
        if (attributeDetails.getGetterMethodName() == null || attributeDetails.getGetterMethodName().equals("")){
            annotations = getTransientAnnotation();
        }
        cv.visitField(ACC_PROTECTED, "_persistence_" + attribute + "_vh", VHI_SIGNATURE, null, annotations);
    }
    
    /**
     * Add a variable of type PropertyChangeListener to the class.  When this method has been run, the class
     * will contain a variable declaration similar to the following
     * 
     * private transient _persistence_listener;
     */
    public void addPropertyChangeListener(boolean attributeAccess){
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_listener", PCL_SIGNATURE, null, null);
    }
    
    /**
     * Add the implementation of the changeTracker_getPropertyChangeListener method to the class.  The
     * result is a method that looks as follows:
     * 
     * public PropertyChangeListener _persistence_getPropertyChangeListener() {
     *     return _persistence_listener;
     * }
     */
    public void addGetPropertyChangeListener(ClassDetails classDetails) {
        CodeVisitor cv_getPCL =  cv.visitMethod(ACC_PUBLIC, "_persistence_getPropertyChangeListener", "()" + PCL_SIGNATURE, null, null);
        cv_getPCL.visitVarInsn(ALOAD, 0);
        cv_getPCL.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_listener", PCL_SIGNATURE);
        cv_getPCL.visitInsn(ARETURN);
        cv_getPCL.visitMaxs(0, 0);
    }
        
    /**
     * Add the implementation of the changeTracker_setPropertyChangeListener method to the class.  The
     * result is a method that looks as follows:
     * 
     * public void _persistence_setPropertyChangeListener(PropertyChangeListener propertychangelistener){
     *     _persistence_listener = propertychangelistener;
     * }
     */
    public void addSetPropertyChangeListener(ClassDetails classDetails){
        RuntimeVisibleAnnotations annotations = null;
        if (!classDetails.usesAttributeAccess()){
            annotations = getTransientAnnotation();
        }
        CodeVisitor cv_setPCL =  cv.visitMethod(ACC_PUBLIC, "_persistence_setPropertyChangeListener", "(" + PCL_SIGNATURE + ")V", null, annotations);
        cv_setPCL.visitVarInsn(ALOAD, 0);
        cv_setPCL.visitVarInsn(ALOAD, 1);
        cv_setPCL.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_listener", PCL_SIGNATURE);
        cv_setPCL.visitInsn(RETURN);
        cv_setPCL.visitMaxs(0, 0);
    }

    /**
     * Add a method to track property changes.  The method will look as follows:
     * 
     * public void _toplink_propertyChange(String s, Object obj, Object obj1){
     *     if(_persistence_listener != null && obj != obj1){
     *         _persistence_listener.propertyChange(new PropertyChangeEvent(this, s, obj, obj1));
     *     }
     * }
     */
    public void addPropertyChange(ClassDetails classDetails){
        // create the _toplink_propertyChange() method
        CodeVisitor cv_addPC = cv.visitMethod(ACC_PUBLIC, "_persistence_propertyChange", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
        
        // if (_toplink_Listener != null)
        cv_addPC.visitVarInsn(ALOAD, 0);
        cv_addPC.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_listener", PCL_SIGNATURE);
        Label l0 = new Label();
        cv_addPC.visitJumpInsn(IFNULL, l0);
        
        // if (obj != obj1)
        cv_addPC.visitVarInsn(ALOAD, 2);
        cv_addPC.visitVarInsn(ALOAD, 3);
        cv_addPC.visitJumpInsn(IF_ACMPEQ, l0);
        
        // _toplink_listener.propertyChange(...);
        cv_addPC.visitVarInsn(ALOAD, 0);
        cv_addPC.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_listener", PCL_SIGNATURE);
        cv_addPC.visitTypeInsn(NEW, PCE_SHORT_SIGNATURE);
        cv_addPC.visitInsn(DUP);
        
        // new PropertyChangeEvent(this, s, obj, obj1)
        cv_addPC.visitVarInsn(ALOAD, 0);
        cv_addPC.visitVarInsn(ALOAD, 1);
        cv_addPC.visitVarInsn(ALOAD, 2);
        cv_addPC.visitVarInsn(ALOAD, 3);
        cv_addPC.visitMethodInsn(INVOKESPECIAL, PCE_SHORT_SIGNATURE, "<init>", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V");
        cv_addPC.visitMethodInsn(INVOKEINTERFACE, PCL_SHORT_SIGNATURE, "propertyChange", "(" + PCE_SIGNATURE + ")V");
        
        // }
        cv_addPC.visitLabel(l0);        

        cv_addPC.visitInsn(RETURN);
        cv_addPC.visitMaxs(0, 0);
    }
    
    /**
     * Add a method that allows us to lazily initialize a valueholder we have woven in
     * This allows us to avoid initializing valueholders in the constructor.
     * 
     * protected void _persistence_initialize_attribute_vh(){
     *     if(_persistence_attribute_vh == null){
     *          _persistence_attribute_vh = new ValueHolder(this.attribute); // or new ValueHolder() if property access.
     *          _persistence_attribute_vh.setIsNewlyWeavedValueHolder(true);
     *      }
     *  }
     */
    public void addInitializerForValueHolder(ClassDetails classDetails, AttributeDetails attributeDetails) {
        String attribute = attributeDetails.getAttributeName();
        String className = classDetails.getClassName();
        
        // Create a getter method for the new valueholder
        // protected void _persistence_initialize_attribute_vh(){
        CodeVisitor cv_init_VH = cv.visitMethod(ACC_PROTECTED, "_persistence_initialize_" + attribute + "_vh", "()V", null, null);

        // if(_persistence_attribute_vh == null){
        cv_init_VH.visitVarInsn(ALOAD, 0);
        cv_init_VH.visitFieldInsn(GETFIELD, className, "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        Label l0 = new Label();
        cv_init_VH.visitJumpInsn(IFNONNULL, l0);
        
        // _persistence_attribute_vh = new ValueHolder(this.attribute);
        cv_init_VH.visitVarInsn(ALOAD, 0);
        cv_init_VH.visitTypeInsn(NEW, VH_SHORT_SIGNATURE);
        cv_init_VH.visitInsn(DUP);
        if (attributeDetails.hasField()) {
            cv_init_VH.visitVarInsn(ALOAD, 0);
            cv_init_VH.visitFieldInsn(GETFIELD, className, attribute, attributeDetails.getReferenceClassType().getDescriptor());
            cv_init_VH.visitMethodInsn(INVOKESPECIAL, VH_SHORT_SIGNATURE, "<init>", "(Ljava/lang/Object;)V");
        } else {
            cv_init_VH.visitMethodInsn(INVOKESPECIAL, VH_SHORT_SIGNATURE, "<init>", "()V");
        }
        cv_init_VH.visitFieldInsn(PUTFIELD, className, "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        
        // _persistence_attribute_vh.setIsNewlyWeavedValueHolder(true);
        cv_init_VH.visitVarInsn(ALOAD, 0);
        cv_init_VH.visitFieldInsn(GETFIELD, className, "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        cv_init_VH.visitInsn(ICONST_1);
        cv_init_VH.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "setIsNewlyWeavedValueHolder", "(Z)V");
        
        // }
        cv_init_VH.visitLabel(l0);
        
        cv_init_VH.visitInsn(RETURN);
        cv_init_VH.visitMaxs(0, 0);        
    }
    
    /**
     * Add a get method for the newly added valueholder.  Adds a method of the following form:
     * 
     *  public WeavedAttributeValueHolderInterface _persistence_getfoo_vh(){
     *      _persistence_initialize_attributeName_vh();
     *      if (_persistence_vh.isCoordinatedWithProperty() || _persistence_foo_vh.isNewlyWeavedValueHolder()){
     *          EntityC object = (EntityC)getFoo();
     *          if (object != _persistence_foo_vh.getValue()){
     *              setFoo(object);
     *          }
     *      }
     *      return _persistence_foo_vh;
     *  }
     */
    public void addGetterMethodForValueHolder(ClassDetails classDetails, AttributeDetails attributeDetails){
        String attribute = attributeDetails.getAttributeName();
        String className = classDetails.getClassName();
        // Create a getter method for the new valueholder
        CodeVisitor cv_get_VH = cv.visitMethod(ACC_PUBLIC, "_persistence_get" + attribute + "_vh", "()" + VHI_SIGNATURE, null, null);
        
        // _persistence_initialize_attributeName_vh();
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_initialize_" + attributeDetails.getAttributeName() + "_vh", "()V");

        // if (_toplink_foo_vh.isCoordinatedWithProperty() || _toplink_foo_vh.isNewlyWeavedValueHolder()){
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        cv_get_VH.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "isCoordinatedWithProperty", "()Z");       
        Label l0 = new Label();
        cv_get_VH.visitJumpInsn(IFNE, l0);
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        cv_get_VH.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "isNewlyWeavedValueHolder", "()Z");
        Label l1 = new Label();
        cv_get_VH.visitJumpInsn(IFEQ, l1);
        cv_get_VH.visitLabel(l0);
        cv_get_VH.visitVarInsn(ALOAD, 0);    
        
        // EntityC object = (EntityC)getFoo();
        if (attributeDetails.getGetterMethodName() != null){
            cv_get_VH.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), attributeDetails.getGetterMethodName(), "()L" + attributeDetails.getReferenceClassName().replace('.','/') + ";");    
            cv_get_VH.visitTypeInsn(CHECKCAST, attributeDetails.getReferenceClassName().replace('.','/'));
        } else {
            cv_get_VH.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_get" + attributeDetails.attributeName, "()L" + attributeDetails.getReferenceClassName().replace('.','/') + ";");                
        }
        cv_get_VH.visitVarInsn(ASTORE, 1);

        // if (object != _toplink_foo_vh.getValue()){
        cv_get_VH.visitVarInsn(ALOAD, 1);
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        cv_get_VH.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "getValue", "()Ljava/lang/Object;");
        cv_get_VH.visitJumpInsn(IF_ACMPEQ, l1);
        
        // setFoo(object);
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitVarInsn(ALOAD, 1);
        if (attributeDetails.getSetterMethodName() != null){
            cv_get_VH.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), attributeDetails.getSetterMethodName(), "(L" + attributeDetails.getReferenceClassName().replace('.','/') + ";)V");
        } else {
            cv_get_VH.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_set" + attributeDetails.getAttributeName(), "(L" + attributeDetails.getReferenceClassName().replace('.','/') + ";)V");
        }
        
        // }
        cv_get_VH.visitLabel(l1); 
        
        // return _toplink_foo_vh;
        cv_get_VH.visitVarInsn(ALOAD, 0);
        cv_get_VH.visitFieldInsn(GETFIELD, className, "_persistence_" + attribute + "_vh", VHI_SIGNATURE);
        cv_get_VH.visitInsn(ARETURN);

        cv_get_VH.visitMaxs(0, 0);
    }
    
    /**
     * Add a set method for the newly added ValueHolder.  Adds a method of this form:
     * 
     *  public void _persistence_setfoo_vh(WeavedAttributeValueHolderInterface valueholderinterface){
     *      _persistence_foo_vh = valueholderinterface;
     *      if (valueholderinterface.isInstantiated()){
     *          Object object = getFoo();
     *          Object value = valueholderinterface.getValue();
     *              if (object != value){
     *                  setFoo((EntityC)value);
     *              }  
     *      }
     *  }
     */
    public void addSetterMethodForValueHolder(ClassDetails classDetails, AttributeDetails attributeDetails){
        String attribute = attributeDetails.getAttributeName();
        String className = classDetails.getClassName();
        // create a setter method for the new valueholder
        CodeVisitor cv_set_value = cv.visitMethod(ACC_PUBLIC, "_persistence_set" + attribute + "_vh", "(" + VHI_SIGNATURE + ")V", null, null);                                 
        
        // _toplink_foo_vh = valueholderinterface;
        cv_set_value.visitVarInsn(ALOAD, 0);
        cv_set_value.visitVarInsn(ALOAD, 1);
        cv_set_value.visitFieldInsn(PUTFIELD, className, "_persistence_" + attribute + "_vh", VHI_SIGNATURE);    
        
        // if (valueholderinterface.isInstantiated()){
        cv_set_value.visitVarInsn(ALOAD, 1);
        cv_set_value.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "isInstantiated", "()Z");
        Label l0 = new Label();
        cv_set_value.visitJumpInsn(IFEQ, l0);
        
        // Object object = getFoo();
        cv_set_value.visitVarInsn(ALOAD, 0);            
         if (attributeDetails.getGetterMethodName() != null){
            cv_set_value.visitMethodInsn(INVOKEVIRTUAL, className, attributeDetails.getGetterMethodName(), "()L" + attributeDetails.getReferenceClassName().replace('.','/') + ";");    
         } else {
            cv_set_value.visitMethodInsn(INVOKEVIRTUAL, className, "_persistence_get" + attributeDetails.attributeName, "()L" + attributeDetails.getReferenceClassName().replace('.','/') + ";");                
        }
        cv_set_value.visitVarInsn(ASTORE, 2);
        
        // Object value = valueholderinterface.getValue();
        cv_set_value.visitVarInsn(ALOAD, 1);
        cv_set_value.visitMethodInsn(INVOKEINTERFACE, VHI_SHORT_SIGNATURE, "getValue", "()Ljava/lang/Object;");
        cv_set_value.visitVarInsn(ASTORE, 3);       
        
        // if (object != value){
        cv_set_value.visitVarInsn(ALOAD, 2);
        cv_set_value.visitVarInsn(ALOAD, 3);
        cv_set_value.visitJumpInsn(IF_ACMPEQ, l0);
        
        // setFoo((EntityC)value);
        cv_set_value.visitVarInsn(ALOAD, 0);
        cv_set_value.visitVarInsn(ALOAD, 3);
        cv_set_value.visitTypeInsn(CHECKCAST, attributeDetails.getReferenceClassName().replace('.','/'));
         if (attributeDetails.getSetterMethodName() != null){
            cv_set_value.visitMethodInsn(INVOKEVIRTUAL, className, attributeDetails.getSetterMethodName(), "(L" + attributeDetails.getReferenceClassName().replace('.','/') + ";)V");
        } else {
            cv_set_value.visitMethodInsn(INVOKEVIRTUAL, className, "_persistence_set" + attributeDetails.getAttributeName(), "(L" + attributeDetails.getReferenceClassName().replace('.','/') + ";)V");
        }

        cv_set_value.visitLabel(l0);

        cv_set_value.visitInsn(RETURN);
        cv_set_value.visitMaxs(0, 0);
        
    }
    
    /**
     * Adds a convenience method used to replace a PUTFIELD when field access is used. The method follows
     * the following form:
     *     
     *     public void _persistence_setvariableName((VariableClas) argument) {
     *          _persistence_getvariableName();   
     *          _persistence_propertyChange("variableName", this.variableName, argument); // if change tracking enabled, wrapping primitives, i.e. new Long(item)
     *          this.variableName = argument;
     *          _persistence_variableName_vh.setValue(variableName); // if lazy enabled
     *      }
     */
    public void addSetterMethodForFieldAccess(ClassDetails classDetails, AttributeDetails attributeDetails){
        String attribute = attributeDetails.getAttributeName();
        
        // create _persistence_setvariableName
        CodeVisitor cv_set = cv.visitMethod(ACC_PUBLIC, "_persistence_set" + attribute, "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V", null, null);
        
        // Get the opcode for the load instruction.  This may be different depending on the type
        int opcode = attributeDetails.getReferenceClassType().getOpcode(Constants.ILOAD);
        
        // First call the get to ensure the attribute is instantiated correctly,
        // otherwise setting to null in change tracking will think nothing changed.
        cv_set.visitVarInsn(ALOAD, 0);
        // _persistence_getvariableName();
        cv_set.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_get" + attribute, "()" + attributeDetails.getReferenceClassType().getDescriptor());
        
        if (classDetails.shouldWeaveChangeTracking()) {
            // load the string attribute name as the first agument of the property change call
            cv_set.visitVarInsn(ALOAD, 0);
            cv_set.visitLdcInsn(attribute);
            
            // if the attribute is a primitive, wrap it
            // e.g. if it is an integer: new Integer(attribute)
            // This is the first part of the wrapping
            String wrapper = ClassWeaver.wrapperFor(attributeDetails.getReferenceClassType().getSort());
            if (wrapper != null){
                cv_set.visitTypeInsn(NEW, wrapper);
                cv_set.visitInsn(DUP);
            }
            
            // load the method argument
            cv_set.visitVarInsn(ALOAD, 0);
            cv_set.visitFieldInsn(GETFIELD, classDetails.getClassName(), attribute, attributeDetails.getReferenceClassType().getDescriptor());
    
            if (wrapper != null){
                // invoke the constructor for wrapping
                // e.g. new Integer(variableName)
                cv_set.visitMethodInsn(INVOKESPECIAL, wrapper, "<init>", "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
                
                // wrap the method argument
                // e.g. new Integer(argument)
                cv_set.visitTypeInsn(NEW, wrapper);
                cv_set.visitInsn(DUP);
                cv_set.visitVarInsn(opcode, 1);
                cv_set.visitMethodInsn(INVOKESPECIAL, wrapper, "<init>", "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
            } else {
                // if we are not wrapping the argument, just load it
                cv_set.visitVarInsn(ALOAD, 1);
            }
            // _persistence_propertyChange("variableName", variableName, argument);
            cv_set.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_propertyChange", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V");
        }
        
        // Must set variable after raising change event, so event has old and new value.
        // variableName = argument
        cv_set.visitVarInsn(ALOAD, 0);
        cv_set.visitVarInsn(opcode, 1);
        cv_set.visitFieldInsn(PUTFIELD, classDetails.getClassName(), attribute, attributeDetails.getReferenceClassType().getDescriptor());
        
        if (attributeDetails.weaveValueHolders()) {
            // _persistence_variableName_vh.setValue(argument);
            cv_set.visitVarInsn(ALOAD, 0);
            cv_set.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attribute + "_vh", ClassWeaver.VHI_SIGNATURE);
            cv_set.visitVarInsn(ALOAD, 1);
            cv_set.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "setValue", "(Ljava/lang/Object;)V");
        }
        
        cv_set.visitInsn(RETURN);
        cv_set.visitMaxs(0 ,0);
    }
    
    /**
     * Adds a convenience method used to replace a GETFIELD when field access is used. The method follows
     * the following form:
     * 
     *      public (VariableClass) _persistence_getvariableName() {
     *          _persistence_checkFetched("variableName");
     *          _persistence_initialize_variableName_vh();
     *          this.variableName = ((VariableClass))_persistence_variableName_vh.getValue();
     *          return this.variableName;
     *      }
     */
    public void addGetterMethodForFieldAccess(ClassDetails classDetails, AttributeDetails attributeDetails){
        String attribute = attributeDetails.getAttributeName();

        // create the _persistenc_getvariableName method
        CodeVisitor cv_get = cv.visitMethod(ACC_PUBLIC, "_persistence_get" + attribute, "()" + attributeDetails.getReferenceClassType().getDescriptor(), null, null);
        
        if (classDetails.shouldWeaveFetchGroups()) {
            cv_get.visitVarInsn(ALOAD, 0);
            cv_get.visitLdcInsn(attribute);
            // _persistence_checkFetched("variableName");
            cv_get.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_checkFetched", "(Ljava/lang/String;)V");
        }
        
        if (attributeDetails.weaveValueHolders()) {
            // _persistence_initialize_variableName_vh();
            cv_get.visitVarInsn(ALOAD, 0);
            cv_get.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_initialize_" + attributeDetails.getAttributeName() + "_vh", "()V");
            
            // _persistenc_variableName_vh.getValue();
            cv_get.visitVarInsn(ALOAD, 0);
            cv_get.visitVarInsn(ALOAD, 0);
            cv_get.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attribute + "_vh", ClassWeaver.VHI_SIGNATURE);
            cv_get.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "getValue", "()Ljava/lang/Object;");
            
            // Add the cast: (<VariableClass>)_persistenc_variableName_vh.getValue()
            cv_get.visitTypeInsn(CHECKCAST, attributeDetails.getReferenceClassName().replace('.','/'));
            
            // add the assignment: this.variableName = (<VariableClass>)_persistenc_variableName_vh.getValue();
            cv_get.visitFieldInsn(PUTFIELD, classDetails.getClassName(), attribute, attributeDetails.getReferenceClassType().getDescriptor());
        }
        
        // return this.variableName;
        cv_get.visitVarInsn(ALOAD, 0);
        cv_get.visitFieldInsn(GETFIELD, classDetails.getClassName(), attribute, attributeDetails.getReferenceClassType().getDescriptor());
        // Get the opcode for the return insturction.  This may be different depending on the type.
        int opcode = attributeDetails.getReferenceClassType().getOpcode(Constants.IRETURN);
        cv_get.visitInsn(opcode);
        cv_get.visitMaxs(0, 0);
    }
    
    /**
     * Add a variable of type Vector, CacheKey to the class.
     * When this method has been run, the class will contain a variable declarations similar to the following:
     *  
     *  private Vector _persistence_primaryKey;
     *  private Vector _persistence_cacheKey;
     */
    public void addPersistenceEntityVariables() {
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_primaryKey", VECTOR_SIGNATURE, null, null);
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_cacheKey", CACHEKEY_SIGNATURE, null, null);
    }
    
    /**
     * Add an internal post clone method.
     * This will clone value holders to avoid change original/clone to effect the other.
     * 
     * public Object _persistence_post_clone() {
     *     this._attribute_vh = this._attribute_vh.clone();
     *     ...
     *     this._persistence_listener = null;
     *     return this;
     * }
     */
    public void addPersistencePostClone(ClassDetails classDetails) {
        // create the _persistence_post_clone() method
        CodeVisitor cv_clone = cv.visitMethod(ACC_PUBLIC, "_persistence_post_clone", "()Ljava/lang/Object;", null, null);

        if (classDetails.shouldWeaveValueHolders()) {
            for (Iterator iterator = classDetails.getAttributesMap().values().iterator(); iterator.hasNext(); ) {
                AttributeDetails attributeDetails = (AttributeDetails)iterator.next();
                if (attributeDetails.weaveValueHolders()) { // && !attributeDetails.isAttributeOnSuperClass()) {
                    // clone._attribute_vh = this._attribute_vh.clone();
                    cv_clone.visitVarInsn(ALOAD, 0);
                    cv_clone.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                    Label label = new Label();
                    cv_clone.visitJumpInsn(IFNULL, label);
                    cv_clone.visitVarInsn(ALOAD, 0);
                    cv_clone.visitVarInsn(ALOAD, 0);
                    cv_clone.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                    cv_clone.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "clone", "()Ljava/lang/Object;");
                    cv_clone.visitTypeInsn(CHECKCAST, ClassWeaver.VHI_SHORT_SIGNATURE);
                    cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                    cv_clone.visitLabel(label);
                }
            }
        }
        if (classDetails.shouldWeaveChangeTracking()) {
            // clone._persistence_listener = null;
            cv_clone.visitVarInsn(ALOAD, 0);
            cv_clone.visitInsn(ACONST_NULL);
            cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_listener", PCL_SIGNATURE);
        }
        if (classDetails.shouldWeaveFetchGroups()) {
            // clone._persistence_fetchGroup = null;
            // clone._persistence_session = null;
            cv_clone.visitVarInsn(ALOAD, 0);
            cv_clone.visitInsn(ACONST_NULL);
            cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_fetchGroup", FETCHGROUP_SIGNATURE);
            cv_clone.visitVarInsn(ALOAD, 0);
            cv_clone.visitInsn(ACONST_NULL);
            cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_session", SESSION_SIGNATURE);
        }
        
        if (!classDetails.isEmbedable()){
	        // clone._persistence_primaryKey = null;
	        cv_clone.visitVarInsn(ALOAD, 0);
	        cv_clone.visitInsn(ACONST_NULL);
	        cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_primaryKey", VECTOR_SIGNATURE);
	        // clone._persistence_cacheKey = null;
	        cv_clone.visitVarInsn(ALOAD, 0);
	        cv_clone.visitInsn(ACONST_NULL);
	        cv_clone.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_cacheKey", CACHEKEY_SIGNATURE);
        }
        
        // return clone;
        cv_clone.visitVarInsn(ALOAD, 0);
        cv_clone.visitInsn(ARETURN);
        cv_clone.visitMaxs(0, 0);
    }
    
    /**
     * Add an internal shallow clone method.
     * This can be used to optimize uow cloning.
     * 
     * public void _persistence_shallow_clone() {
     *     return Object.clone();
     * }
     */
    public void addShallowClone(ClassDetails classDetails) {
        // create the clone() method
        CodeVisitor cv_clone = cv.visitMethod(ACC_PUBLIC, "_persistence_shallow_clone", "()Ljava/lang/Object;", null, null);

        // ClassType clone = (ClassType)super.clone();
        cv_clone.visitVarInsn(ALOAD, 0);
        cv_clone.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "clone", "()Ljava/lang/Object;");
        
        cv_clone.visitInsn(ARETURN);
        cv_clone.visitMaxs(0, 0);
    }
    
    /**
     * Adds get/set method for PersistenceEntity interface.
     * This adds the following methods:
     * 
     *  public CacheKey _persistence_getCacheKey() {
     *      return _persistence_cacheKey;
     *  }
     *  public void _persistence_setCacheKey(CacheKey key) {
     *      this._persistence_cacheKey = key;
     *  }
     *   
     *  public Vector _persistence_getPKVector() {
     *      return _persistence_primaryKey;
     *  }
     *  public void _persistence_setPKVector(Vector pk) {
     *      this._persistence_primaryKey = pk;
     *  }
     */
    public void addPersistenceEntityMethods(ClassDetails classDetails) {
        CodeVisitor cv_getCacheKey = cv.visitMethod(ACC_PUBLIC, "_persistence_getCacheKey", "()" + CACHEKEY_SIGNATURE, null, null);
        cv_getCacheKey.visitVarInsn(ALOAD, 0);
        cv_getCacheKey.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_cacheKey", CACHEKEY_SIGNATURE);
        cv_getCacheKey.visitInsn(ARETURN);
        cv_getCacheKey.visitMaxs(0, 0);
        
        CodeVisitor cv_setCacheKey = cv.visitMethod(ACC_PUBLIC, "_persistence_setCacheKey", "(" + CACHEKEY_SIGNATURE + ")V", null, null);
        cv_setCacheKey.visitVarInsn(ALOAD, 0);
        cv_setCacheKey.visitVarInsn(ALOAD, 1);
        cv_setCacheKey.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_cacheKey", CACHEKEY_SIGNATURE);
        cv_setCacheKey.visitInsn(RETURN);
        cv_setCacheKey.visitMaxs(0 ,0);
        
        CodeVisitor cv_getPKVector = cv.visitMethod(ACC_PUBLIC, "_persistence_getPKVector", "()" + VECTOR_SIGNATURE, null, null);
        cv_getPKVector.visitVarInsn(ALOAD, 0);
        cv_getPKVector.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_primaryKey", VECTOR_SIGNATURE);
        cv_getPKVector.visitInsn(ARETURN);
        cv_getPKVector.visitMaxs(0, 0);
        
        CodeVisitor cv_setPKVector = cv.visitMethod(ACC_PUBLIC, "_persistence_setPKVector", "(" + VECTOR_SIGNATURE + ")V", null, null);
        cv_setPKVector.visitVarInsn(ALOAD, 0);
        cv_setPKVector.visitVarInsn(ALOAD, 1);
        cv_setPKVector.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_primaryKey", VECTOR_SIGNATURE);
        cv_setPKVector.visitInsn(RETURN);
        cv_setPKVector.visitMaxs(0 ,0);
    }
    
    /**
     * Add a variable of type FetchGroup, Session to the class.
     * When this method has been run, the class will contain a variable declarations similar to the following:
     *  
     *  private FetchGroup _persistence_fetchGroup;
     *  private boolean _persistence_shouldRefreshFetchGroup;
     *  private Session _persistence_session;
     */
    public void addFetchGroupVariables() {
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_fetchGroup", FETCHGROUP_SIGNATURE, null, null);
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_shouldRefreshFetchGroup", PBOOLEAN_SIGNATURE, null, null);
        cv.visitField(ACC_PROTECTED + ACC_TRANSIENT, "_persistence_session", SESSION_SIGNATURE, null, null);
    }
    
    /**
     * Adds get/set method for FetchGroupTracker interface.
     * This adds the following methods:
     * 
     *  public Session _persistence_getSession() {
     *      return _persistence_session;
     *  }
     *  public void _persistence_setSession(Session session) {
     *      this._persistence_session = session;
     *  }
     *  
     *  public FetchGroup _persistence_getFetchGroup() {
     *      return _persistence_fetchGroup;
     *  }
     *  public void _persistence_setFetchGroup(FetchGroup fetchGroup) {
     *      this._persistence_fetchGroup = fetchGroup;
     *  }
     *  
     *  public boolean _persistence_shouldRefreshFetchGroup() {
     *      return _persistence_shouldRefreshFetchGroup;
     *  }
     *  public void _persistence_setShouldRefreshFetchGroup(boolean shouldRefreshFetchGroup) {
     *      this._persistence_shouldRefreshFetchGroup = shouldRefreshFetchGroup;
     *  }
     *  
     *  public void _persistence_resetFetchGroup() {
     *  }
     *
     *  public void _persistence_isAttributeFetched(String attribute) {
     *      return this._persistence_fetchGroup == null || _persistence_fetchGroup.containsAttribute(attribute);
     *  }
     *  
     *  public void _persistence_checkFetched(String attribute) {
     *      if (!_persistence_isAttributeFetched(attribute)) {
     *          JpaHelper.loadUnfetchedObject(this);
     *      }
     *  }
     */
    public void addFetchGroupMethods(ClassDetails classDetails) {
        CodeVisitor cv_getSession = cv.visitMethod(ACC_PUBLIC, "_persistence_getSession", "()" + SESSION_SIGNATURE, null, null);
        cv_getSession.visitVarInsn(ALOAD, 0);
        cv_getSession.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_session", SESSION_SIGNATURE);
        cv_getSession.visitInsn(ARETURN);
        cv_getSession.visitMaxs(0, 0);
        
        CodeVisitor cv_setSession = cv.visitMethod(ACC_PUBLIC, "_persistence_setSession", "(" + SESSION_SIGNATURE + ")V", null, null);
        cv_setSession.visitVarInsn(ALOAD, 0);
        cv_setSession.visitVarInsn(ALOAD, 1);
        cv_setSession.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_session", SESSION_SIGNATURE);
        cv_setSession.visitInsn(RETURN);
        cv_setSession.visitMaxs(0 ,0);
        
        CodeVisitor cv_getFetchGroup = cv.visitMethod(ACC_PUBLIC, "_persistence_getFetchGroup", "()" + FETCHGROUP_SIGNATURE, null, null);
        cv_getFetchGroup.visitVarInsn(ALOAD, 0);
        cv_getFetchGroup.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_fetchGroup", FETCHGROUP_SIGNATURE);
        cv_getFetchGroup.visitInsn(ARETURN);
        cv_getFetchGroup.visitMaxs(0, 0);
        
        CodeVisitor cv_setFetchGroup = cv.visitMethod(ACC_PUBLIC, "_persistence_setFetchGroup", "(" + FETCHGROUP_SIGNATURE + ")V", null, null);
        cv_setFetchGroup.visitVarInsn(ALOAD, 0);
        cv_setFetchGroup.visitVarInsn(ALOAD, 1);
        cv_setFetchGroup.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_fetchGroup", FETCHGROUP_SIGNATURE);
        cv_setFetchGroup.visitInsn(RETURN);
        cv_setFetchGroup.visitMaxs(0 ,0);
        
        CodeVisitor cv_shouldRefreshFetchGroup = cv.visitMethod(ACC_PUBLIC, "_persistence_shouldRefreshFetchGroup", "()" + PBOOLEAN_SIGNATURE, null, null);
        cv_shouldRefreshFetchGroup.visitVarInsn(ALOAD, 0);
        cv_shouldRefreshFetchGroup.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_shouldRefreshFetchGroup", PBOOLEAN_SIGNATURE);
        cv_shouldRefreshFetchGroup.visitInsn(IRETURN);
        cv_shouldRefreshFetchGroup.visitMaxs(0, 0);
        
        CodeVisitor cv_setShouldRefreshFetchGroup = cv.visitMethod(ACC_PUBLIC, "_persistence_setShouldRefreshFetchGroup", "(" + PBOOLEAN_SIGNATURE + ")V", null, null);
        cv_setShouldRefreshFetchGroup.visitVarInsn(ALOAD, 0);
        cv_setShouldRefreshFetchGroup.visitVarInsn(ILOAD, 1);
        cv_setShouldRefreshFetchGroup.visitFieldInsn(PUTFIELD, classDetails.getClassName(), "_persistence_shouldRefreshFetchGroup", PBOOLEAN_SIGNATURE);
        cv_setShouldRefreshFetchGroup.visitInsn(RETURN);
        cv_setShouldRefreshFetchGroup.visitMaxs(0 ,0);
        
        CodeVisitor cv_resetFetchGroup = cv.visitMethod(ACC_PUBLIC, "_persistence_resetFetchGroup", "()V", null, null);
        cv_resetFetchGroup.visitInsn(RETURN);
        cv_resetFetchGroup.visitMaxs(0, 0);
        
        CodeVisitor cv_isAttributeFetched = cv.visitMethod(ACC_PUBLIC, "_persistence_isAttributeFetched", "(Ljava/lang/String;)Z", null, null);
        cv_isAttributeFetched.visitVarInsn(ALOAD, 0);
        cv_isAttributeFetched.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_fetchGroup", FETCHGROUP_SIGNATURE);
        Label gotoTrue = new Label();
        cv_isAttributeFetched.visitJumpInsn(IFNULL, gotoTrue);
        cv_isAttributeFetched.visitVarInsn(ALOAD, 0);
        cv_isAttributeFetched.visitFieldInsn(GETFIELD, classDetails.getClassName(), "_persistence_fetchGroup", FETCHGROUP_SIGNATURE);
        cv_isAttributeFetched.visitVarInsn(ALOAD, 1);
        cv_isAttributeFetched.visitMethodInsn(INVOKEVIRTUAL, FETCHGROUP_SHORT_SIGNATURE, "containsAttribute", "(Ljava/lang/String;)Z");
        Label gotoFalse = new Label();
        cv_isAttributeFetched.visitJumpInsn(IFEQ, gotoFalse);
        cv_isAttributeFetched.visitLabel(gotoTrue);
        cv_isAttributeFetched.visitInsn(ICONST_1);
        Label gotoReturn = new Label();
        cv_isAttributeFetched.visitJumpInsn(GOTO, gotoReturn);
        cv_isAttributeFetched.visitLabel(gotoFalse);
        cv_isAttributeFetched.visitInsn(ICONST_0);
        cv_isAttributeFetched.visitLabel(gotoReturn);
        cv_isAttributeFetched.visitInsn(IRETURN);
        cv_isAttributeFetched.visitMaxs(0 ,0);
        
        CodeVisitor cv_checkFetched = cv.visitMethod(ACC_PUBLIC, "_persistence_checkFetched", "(Ljava/lang/String;)V", null, null);
        cv_checkFetched.visitVarInsn(ALOAD, 0);
        cv_checkFetched.visitVarInsn(ALOAD, 1);
        cv_checkFetched.visitMethodInsn(INVOKEVIRTUAL, classDetails.getClassName(), "_persistence_isAttributeFetched", "(Ljava/lang/String;)Z");
        gotoReturn = new Label();
        cv_checkFetched.visitJumpInsn(IFNE, gotoReturn);
        cv_checkFetched.visitVarInsn(ALOAD, 0);
        cv_checkFetched.visitMethodInsn(INVOKESTATIC, "org/eclipse/persistence/jpa/JpaHelper", "loadUnfetchedObject", "(Ljava/lang/Object;)V");
        cv_checkFetched.visitLabel(gotoReturn);
        cv_checkFetched.visitInsn(RETURN);
        cv_checkFetched.visitMaxs(0 ,0);
    }

    /**
     * Return the JPA transient annotation for weaving.
     */
    protected RuntimeVisibleAnnotations getTransientAnnotation(){
        RuntimeVisibleAnnotations attrs = new RuntimeVisibleAnnotations();
        //Annotation transientAnnotation = new Annotation("Ljavax/persistence/Transient;");
        Annotation transientAnnotation = new Annotation(Type.getDescriptor(javax.persistence.Transient.class));
        attrs.annotations.add(transientAnnotation);
        return attrs;
    }
    
    /**
     * Visit the class byte-codes and modify to weave Persistence interfaces.
     * This add PersistenceWeaved, PersistenceWeavedLazy, PersistenceWeavedChangeTracking, PersistenceEntity, ChangeTracker.
     * The new interfaces are pass to the super weaver.
     */
    public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
        this.uuidGenerator.visit(version, access, name, superName, interfaces, sourceFile);
        boolean weaveCloneable = true;
        // To prevent 'double' weaving: scan for PersistenceWeaved interface.
        for (int index = 0; index < interfaces.length; index++) {
            String existingInterface = interfaces[index];
            if (PERSISTENCE_WEAVED_SHORT_SIGNATURE.equals(existingInterface)) {
                this.alreadyWeaved = true;
                super.visit(version, access, name, superName, interfaces, sourceFile);
                return;
            } else if (CT_SHORT_SIGNATURE.equals(existingInterface)) {
                // Disable weaving of change tracking if already implemented (such as by user).
                classDetails.setShouldWeaveChangeTracking(false);
            } else if (CLONEABLE_SHORT_SIGNATURE.equals(existingInterface)) {
                weaveCloneable = false;
            }
        }
        int newInterfacesLength = interfaces.length;
        // Cloneable
        int cloneableIndex = 0;
        weaveCloneable = classDetails.shouldWeaveInternal() && weaveCloneable && (classDetails.getSuperClassDetails() == null);
        if (weaveCloneable) {
            cloneableIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        // PersistenceWeaved
        int persistenceWeavedIndex = newInterfacesLength;
        newInterfacesLength++;
        // PersistenceEntity
        int toplinkEntityIndex = 0;
        boolean persistenceEntity = classDetails.shouldWeaveInternal() && (classDetails.getSuperClassDetails() == null) && (!classDetails.isEmbedable());
        if (persistenceEntity) {
            toplinkEntityIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        // FetchGroupTracker
        int fetchGroupTrackerIndex = 0;
        boolean fetchGroupTracker = classDetails.shouldWeaveFetchGroups() && (classDetails.getSuperClassDetails() == null);
        if (fetchGroupTracker) {
            fetchGroupTrackerIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        int persistenceWeavedFetchGroupsIndex = 0;
        if (classDetails.shouldWeaveFetchGroups()) {
            persistenceWeavedFetchGroupsIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        // PersistenceWeavedLazy
        int persistenceWeavedLazyIndex = 0;
        if (classDetails.shouldWeaveValueHolders()) {
            persistenceWeavedLazyIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        // ChangeTracker
        boolean changeTracker = !classDetails.doesSuperclassWeaveChangeTracking() && classDetails.shouldWeaveChangeTracking();
        int persistenceWeavedChangeTrackingIndex = 0;
        int changeTrackerIndex = 0;
        if (changeTracker) {
            changeTrackerIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        if (classDetails.shouldWeaveChangeTracking()) {
            persistenceWeavedChangeTrackingIndex = newInterfacesLength;
            newInterfacesLength++;
        }
        
        String[] newInterfaces = new String[newInterfacesLength];
        System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        // Add 'marker' org.eclipse.persistence.internal.weaving.PersistenceWeaved interface.
        newInterfaces[persistenceWeavedIndex] = PERSISTENCE_WEAVED_SHORT_SIGNATURE;            
        weaved = true;
        // Add Cloneable interface.
        if (weaveCloneable) {
            newInterfaces[cloneableIndex] = CLONEABLE_SHORT_SIGNATURE;
        }
        // Add org.eclipse.persistence.internal.descriptors.PersistenceEntity interface.
        if (persistenceEntity) {
            newInterfaces[toplinkEntityIndex] = TOPLINK_ENTITY_SHORT_SIGNATURE;
        }
        // Add org.eclipse.persistence.queries.FetchGroupTracker interface.
        if (fetchGroupTracker) {
            newInterfaces[fetchGroupTrackerIndex] = FETCHGROUP_TRACKER_SHORT_SIGNATURE;
        }
        if (classDetails.shouldWeaveFetchGroups()) {
            newInterfaces[persistenceWeavedFetchGroupsIndex] = WEAVED_FETCHGROUPS_SHORT_SIGNATURE;
        }
        // Add marker interface for LAZY.
        if (classDetails.shouldWeaveValueHolders()) {
            newInterfaces[persistenceWeavedLazyIndex] = TW_LAZY_SHORT_SIGNATURE;
        }
        // Add marker interface and change tracker interface for change tracking.
        if (changeTracker) {
            newInterfaces[changeTrackerIndex] = CT_SHORT_SIGNATURE;
        }
        if (classDetails.shouldWeaveChangeTracking()) {
            newInterfaces[persistenceWeavedChangeTrackingIndex] = TW_CT_SHORT_SIGNATURE;
        }
        super.visit(version, access, name, superName, newInterfaces, sourceFile);
    }
    
    public void visitField (int access, String name, String desc, Object value, Attribute attrs) {
        this.uuidGenerator.visitField(access, name, desc, value, attrs);        
        super.visitField(access, name, desc, value, attrs);
    }
  
    /**
     * Construct a MethodWeaver and allow it to process the method.
     */
    public CodeVisitor visitMethod(int access, String methodName, String desc, String[] exceptions, Attribute attrs) {
        this.uuidGenerator.visitMethod(access, methodName, desc, exceptions, attrs);
        if (!alreadyWeaved) {
            // skip constructors, they will not changed
            if ("<init>".equals(methodName)||"<cinit>".equals(methodName)) {
                return super.visitMethod(access, methodName, desc, exceptions, attrs);
            } else {
                // remaining modifications to the 'body' of the class are delegated to MethodWeaver
                return new MethodWeaver(this, methodName, desc, cv.visitMethod(access, methodName, desc, exceptions, attrs));                
            }
        } else {
            return super.visitMethod(access, methodName, desc, exceptions, attrs);
        }
    }    

    public void visitAttribute(Attribute attr) {
        if (!alreadyWeaved) {
            cv.visitAttribute(attr);
        } else {
            super.visitAttribute(attr);
        }
    }

    /**
     * Visit the end of the class byte codes.
     * Add any new methods or variables to the end.
     */
    public void visitEnd() {
        if (alreadyWeaved) {
            return;
        }
        
        if (this.classDetails.shouldWeaveInternal()) {
            // Add a serial UID if one was not defined in the class to allow portable serialization.
            if (!this.uuidGenerator.hasSVUID()) {
                long suid = this.uuidGenerator.computeSVUID();
                this.cv.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, "serialVersionUID", LONG_SIGNATURE, suid, null);
            }
            // Add a persistence and shallow clone method.
            addPersistencePostClone(this.classDetails);
            if (this.classDetails.getSuperClassDetails() == null) {
                addShallowClone(this.classDetails);        
                if (!this.classDetails.isEmbedable()) {
                    // Add PersistenceEntity variables and methods.
                    addPersistenceEntityVariables();
                    addPersistenceEntityMethods(this.classDetails);
                    weavedPersistenceEntity = true;
                }
            }
        }
        
        boolean attributeAccess = false;
        // For each attribute we need to check what methods and variables to add.
        for (Iterator iterator = this.classDetails.getAttributesMap().values().iterator(); iterator.hasNext();) {
            AttributeDetails attributeDetails = (AttributeDetails)iterator.next();
            // Only add to classes that actually contain the attribute we are processing
            // an attribute could be in the classDetails but not actually in the class
            // if it is owned by a MappedSuperClass.
            if (!attributeDetails.isAttributeOnSuperClass()) {
                if (attributeDetails.weaveValueHolders()) {
                    // We will add valueholders and methods to classes that have not already been weaved
                    // and classes that actually contain the attribute we are processing
                    // an attribute could be in the classDetails but not actually in the class
                    // if it is owned by a MappedSuperClass.
                    if (!attributeDetails.isAttributeOnSuperClass()) {
                        weaved = true;
                        weavedLazy = true;
                        addValueHolder(attributeDetails);
                        addInitializerForValueHolder(classDetails, attributeDetails);
                        addGetterMethodForValueHolder(classDetails, attributeDetails);
                        addSetterMethodForValueHolder(classDetails, attributeDetails);                        
                    }
                }
                if (classDetails.shouldWeaveChangeTracking() || classDetails.shouldWeaveFetchGroups() || attributeDetails.weaveValueHolders()) {
                    if (attributeDetails.hasField()) {
                        weaved = true;
                        addGetterMethodForFieldAccess(classDetails, attributeDetails);
                        addSetterMethodForFieldAccess(classDetails, attributeDetails);
                        attributeAccess = true;
                    }
                }
            }
        }
        if (classDetails.shouldWeaveChangeTracking()) {
            weaved = true;
            weavedChangeTracker = true;
            if ((classDetails.getSuperClassDetails() == null) || (!classDetails.doesSuperclassWeaveChangeTracking())) {
                addPropertyChangeListener(attributeAccess);
                addGetPropertyChangeListener(classDetails);
                addSetPropertyChangeListener(classDetails);
                addPropertyChange(classDetails);
            }
        }
        if (classDetails.shouldWeaveFetchGroups()) {
            weaved = true;
            weavedFetchGroups = true;
            if (classDetails.getSuperClassDetails() == null) {          
                addFetchGroupVariables();
                addFetchGroupMethods(this.classDetails);
            }
        }
        super.visitEnd();
    }
}
