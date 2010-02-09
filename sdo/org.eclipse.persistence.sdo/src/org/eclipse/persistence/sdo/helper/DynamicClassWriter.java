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
 *     bdoughan - Mar 18/2009 - 2.0 - Dynamically generated impl classes now
 *                                    implement correct interface.
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Constants;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import commonj.sdo.helper.HelperContext;

/*
 * Dynamically generate the implementation class for the SDO type.  If the type has an instance
 * (interface) class then the dynamically generated impl class must fully implement it.  Additionally
 * a constructor and a writeReplace() method are added.
 */
public class DynamicClassWriter {
    private static final String START_PROPERTY_INDEX = "START_PROPERTY_INDEX";
    private static final String END_PROPERTY_INDEX = "END_PROPERTY_INDEX";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String LIST = "List";
    private static final String WRITE_REPLACE = "writeReplace";

    private Class parentClass;
    private String typeImplClassDescriptor;
    private SDOType type;
    private Integer startPropertyIndex;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public DynamicClassWriter(String className, SDOType type, HelperContext aContext) {
        this.aHelperContext = aContext;
        this.parentClass = SDODataObject.class;
        this.typeImplClassDescriptor = className.replace('.', '/');
        this.type = type;
        initializeParentClass();

        if(type.isSubType()) {
            try {
                Field parentEndPropertyIndexField = PrivilegedAccessHelper.getField(parentClass, END_PROPERTY_INDEX, true);
                Integer parentEndPropertyIndex = (Integer) PrivilegedAccessHelper.getValueFromField(parentEndPropertyIndexField, parentClass);
                startPropertyIndex = parentEndPropertyIndex + 1;
            } catch(NoSuchFieldException e) {
                startPropertyIndex = new Integer(0);
            } catch(IllegalAccessException e) {
                startPropertyIndex = new Integer(0);
            }
        } else {
            startPropertyIndex = new Integer(0);
        }
    }

    private void initializeParentClass() {
        if (type.isSubType()) {
            SDOType parentSDOType = (SDOType)type.getBaseTypes().get(0);
            String parentClassName = parentSDOType.getInstanceClassName() + SDOConstants.SDO_IMPL_NAME;
            try {
                parentClass = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getLoader().loadClass(parentClassName, parentSDOType);
            } catch (Exception e) {
                parentClass = null;
            }
            if (parentClass == null) {
                parentClass = SDODataObject.class;
            }
        } else {
            parentClass = SDODataObject.class;
        }
    }

    public Class getParentClass() {
        return this.parentClass;
    }

    /**
     * This is where the byte codes for the generic subclass are defined and the
     * class is created dynamically from them.
     */
    public byte[] createClass() {
        ClassWriter cw = new ClassWriter(false);

        if(null == type.getInstanceClass()) {
            cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, typeImplClassDescriptor, Type.getType(parentClass).getInternalName(), null, null);
        } else {
            String[] interfaces = new String[1];
            interfaces[0] = type.getInstanceClassName().replace('.', '/');
            cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, typeImplClassDescriptor, Type.getType(parentClass).getInternalName(), interfaces, null);
            addPropertyIndices(cw);
            for(Object object: type.getDeclaredProperties()) {
                SDOProperty sdoProperty = (SDOProperty) object;
                addPropertyGetMethod(cw, sdoProperty);
                addPropertySetMethod(cw, sdoProperty);
            }
        }

        addConstructors(cw);
        addWriteReplace(cw);
        cw.visitEnd();

        return cw.toByteArray();
    }

    private void addPropertyIndices(ClassWriter cw) {
        cw.visitField(Constants.ACC_PUBLIC + Constants.ACC_FINAL + Constants.ACC_STATIC, START_PROPERTY_INDEX, "I", startPropertyIndex, null);
        int declaredPropsSize = type.getDeclaredProperties().size();

        Integer endPropertyIndex;
        if(declaredPropsSize > 0) {
            endPropertyIndex = startPropertyIndex + declaredPropsSize - 2;
        } else {
            endPropertyIndex = startPropertyIndex - 1;
        }
        cw.visitField(Constants.ACC_PUBLIC + Constants.ACC_FINAL + Constants.ACC_STATIC, END_PROPERTY_INDEX, "I", endPropertyIndex, null);
    }

    private void addConstructors(ClassWriter cw) {
        CodeVisitor mv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), new String[] { Type.getInternalName(Serializable.class) }, null);
        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitMethodInsn(Constants.INVOKESPECIAL, Type.getType(parentClass).getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
        mv.visitInsn(Constants.RETURN);
        mv.visitMaxs(1, 1);
    }

    private void addPropertyGetMethod(ClassWriter cw, SDOProperty property) {
        String returnType = SDOUtil.getJavaTypeForProperty(property);
        String outerGetMethodName = SDOUtil.getMethodName(property.getName(), returnType);
        
        if(property.getType() == SDOConstants.SDO_BOOLEAN || property.getType() == SDOConstants.SDO_BOOLEANOBJECT){
        	String booleanGetterName = SDOUtil.getBooleanGetMethodName(property.getName(), returnType);
        	addPropertyGetMethodInternal(cw, property, booleanGetterName, returnType);	
        }        
        addPropertyGetMethodInternal(cw, property, outerGetMethodName, returnType);        
    }
    
    private void addPropertyGetMethodInternal(ClassWriter cw, SDOProperty property, String outerGetMethodName, String returnType){
    	CodeVisitor mv;
        String propertyInstanceClassDescriptor;
        if(property.isMany()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(List.class);
        } else if(property.getType().isDataType()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(property.getType().getInstanceClass());
        } else {
            propertyInstanceClassDescriptor = "L" + returnType.replace('.', '/') + ";";
        }
        mv = cw.visitMethod(Constants.ACC_PUBLIC, outerGetMethodName, "()" + propertyInstanceClassDescriptor, null, null);

        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitIntInsn(Constants.BIPUSH, startPropertyIndex + property.getIndexInType());

        String builtIn = SDOUtil.getBuiltInType(returnType);
        if(null != builtIn) {
            if(property.getType().isDataType() && !builtIn.equals(LIST)) {
                mv.visitMethodInsn(Constants.INVOKEVIRTUAL, typeImplClassDescriptor, GET + builtIn, "(I)" + propertyInstanceClassDescriptor);
                int iReturnOpcode = Type.getType(property.getType().getInstanceClass()).getOpcode(Constants.IRETURN);
                mv.visitInsn(iReturnOpcode);
            } else {
                mv.visitMethodInsn(Constants.INVOKEVIRTUAL,  typeImplClassDescriptor, GET, "(I)Ljava/lang/Object;");                        
                mv.visitInsn(Constants.ARETURN);
            }
        } else {
            mv.visitMethodInsn(Constants.INVOKEVIRTUAL,  typeImplClassDescriptor, GET, "(I)Ljava/lang/Object;");                        
            mv.visitInsn(Constants.ARETURN);
        }
        mv.visitMaxs(2, 1);
    }

    private void addPropertySetMethod(ClassWriter cw, SDOProperty property) {
        String returnType = SDOUtil.getJavaTypeForProperty(property);
        String outerSetMethodName = SDOUtil.setMethodName(property.getName());

        CodeVisitor mv;
        String propertyInstanceClassDescriptor;

        if(property.isMany()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(List.class);
        } else if(property.getType().isDataType()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(property.getType().getInstanceClass());
        } else {
            propertyInstanceClassDescriptor = "L" + returnType.replace('.', '/') + ";";
        }
        mv = cw.visitMethod(Constants.ACC_PUBLIC, outerSetMethodName, "(" + propertyInstanceClassDescriptor + ")V", null, null);

        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitIntInsn(Constants.BIPUSH, startPropertyIndex + property.getIndexInType());

        String builtIn = SDOUtil.getBuiltInType(returnType);
        int iLoadOpcode = Constants.ALOAD;
        if(null != builtIn) {
            if(property.getType().isDataType() && !builtIn.equals(LIST)) {
                iLoadOpcode = Type.getType(property.getType().getInstanceClass()).getOpcode(Constants.ILOAD);
                mv.visitVarInsn(iLoadOpcode, 1);
                mv.visitMethodInsn(Constants.INVOKEVIRTUAL, typeImplClassDescriptor, SET + builtIn, "(I" + propertyInstanceClassDescriptor + ")V");
            } else {
                mv.visitVarInsn(iLoadOpcode, 1);
                mv.visitMethodInsn(Constants.INVOKEVIRTUAL,  typeImplClassDescriptor, SET, "(ILjava/lang/Object;)V");                        
            }
        } else {
            mv.visitVarInsn(iLoadOpcode, 1);
            mv.visitMethodInsn(Constants.INVOKEVIRTUAL,  typeImplClassDescriptor, SET, "(ILjava/lang/Object;)V");                        
        }

        mv.visitInsn(Constants.RETURN);
        if(iLoadOpcode == Constants.DLOAD || iLoadOpcode == Constants.LLOAD) {
            mv.visitMaxs(4, 3);
        } else {
            mv.visitMaxs(3, 2);
        }
    }

    private void addWriteReplace(ClassWriter cw) {
        Method method;
        try {
            method = parentClass.getDeclaredMethod(WRITE_REPLACE, new Class[0]);
        } catch (NoSuchMethodException e) {
            return;
        }

        CodeVisitor mv = cw.visitMethod(Constants.ACC_PROTECTED, method.getName(), Type.getMethodDescriptor(method), new String[] { Type.getInternalName(ObjectStreamException.class) }, null);

        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitMethodInsn(Constants.INVOKESPECIAL, Type.getInternalName(parentClass), method.getName(), Type.getMethodDescriptor(method));
        mv.visitInsn(Constants.ARETURN);
        mv.visitMaxs(1, 1);
    }

}
