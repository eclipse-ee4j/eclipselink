/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.internal.jpa.rs.weaving;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.libraries.asm.EclipseLinkASMClassWriter;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.Label;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

/**
 * Generates a subclass of given collection implementing CollectionProxy interface.
 * {@link org.eclipse.persistence.jpa.rs.util.CollectionProxy}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class CollectionProxyClassWriter implements EclipseLinkClassWriter, Opcodes {
    private static final String CLASS_NAME_SUFFIX = "CollectionProxy";
    private static final String INTERFACE = "org/eclipse/persistence/jpa/rs/util/CollectionProxy";

    private final String parentClassName;
    private final String entityName;
    private final String fieldName;

    /**
     * Creates a new CollectionProxyClassWriter for the given attribute of the given entity of given type.
     *
     * @param parentClassName the superclass name.
     * @param entityName entity name
     * @param fieldName entity attribute name
     */
    public CollectionProxyClassWriter(String parentClassName, String entityName, String fieldName) {
        this.parentClassName = parentClassName;
        this.entityName = entityName;
        this.fieldName = fieldName;
    }

    /**
     * Returns a class name for CollectionProxy based on parent class name and field name
     * to generate proxy for.
     * The name is constructed as _&lt;className&gt;_&lt;fieldName&gt;_RestCollectionProxy.
     *
     * @param entityName full class name (including package)
     * @param fieldName field name
     * @return Rest collection proxy name.
     */
    public static String getClassName(String entityName, String fieldName) {
        final int index = entityName.lastIndexOf('.');
        final String packageName = index >= 0 ? entityName.substring(0, index) : "";
        final String shortClassName = index >= 0 ? entityName.substring(index + 1) : entityName;
        return packageName + "._" + shortClassName + "_" + fieldName + "_" + CLASS_NAME_SUFFIX;
    }

    /**
     * Returns a class name for generated CollectionProxy.
     * {@link #getClassName(String, String)}
     *
     * @return Rest collection proxy name.
     */
    public String getClassName() {
        return getClassName(entityName, fieldName);
    }

    /**
     *  public class Proxy extends SuperType implements CollectionProxy {
     *      private List&lt;LinkV2&gt; links;
     *
     *      public CollectionProxy(Collection c) {
     *          super();
     *          this.addAll(c);
     *      }
     *
     *      &#064;Override
     *      public List&lt;LinkV2&gt; getLinks() {
     *          return links;
     *      }
     *
     *      &#064;Override
     *      public void setLinks(List&lt;LinkV2&gt; links) {
     *          this.links = links;
     *      }
     *  }
     *
     * @param loader
     * @param className
     * @return
     */
    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) {

        final EclipseLinkASMClassWriter cw = new EclipseLinkASMClassWriter(0);
        MethodVisitor mv;

        // public class Proxy extends SuperType implements CollectionProxy
        cw.visit(ACC_PUBLIC + ACC_SUPER, getASMClassName(), null, getASMParentClassName(), new String[]{INTERFACE});

        // private List<LinkV2> links;
        final FieldVisitor fv = cw.visitField(ACC_PRIVATE, "links", "Ljava/util/List;", "Ljava/util/List<Lorg/eclipse/persistence/internal/jpa/rs/metadata/model/LinkV2;>;", null);
        fv.visitEnd();

        // public CollectionProxy(Collection c) {
        //     super();
        //     this.addAll(c);
        // }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/Collection;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(15, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, getASMParentClassName(), "<init>", "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(16, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, getASMClassName(), "addAll", "(Ljava/util/Collection;)Z", false);
            mv.visitInsn(POP);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(17, l2);
            mv.visitInsn(RETURN);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable("this", "L" + getASMClassName() + ";", null, l0, l3, 0);
            mv.visitLocalVariable("c", "Ljava/util/Collection;", null, l0, l3, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        // @Override
        // public List<LinkV2> getLinks() {
        //    return links;
        // }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getLinks", "()Ljava/util/List;", "()Ljava/util/List<Lorg/eclipse/persistence/internal/jpa/rs/metadata/model/LinkV2;>;", null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(21, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, getASMClassName(), "links", "Ljava/util/List;");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + getASMClassName() + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        // @Override
        // public void setLinks(List<LinkV2> links) {
        //    this.links = links;
        // }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setLinks", "(Ljava/util/List;)V", "(Ljava/util/List<Lorg/eclipse/persistence/internal/jpa/rs/metadata/model/LinkV2;>;)V", null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(26, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, getASMClassName(), "links", "Ljava/util/List;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(27, l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "L" + getASMClassName()+ ";", null, l0, l2, 0);
            mv.visitLocalVariable("links", "Ljava/util/List;", "Ljava/util/List<Lorg/eclipse/persistence/internal/jpa/rs/metadata/model/LinkV2;>;", l0, l2, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        return cw.toByteArray();
    }

    @Override
    public boolean isCompatible(EclipseLinkClassWriter writer) {
        return getParentClassName().equals(writer.getParentClassName());
    }

    @Override
    public Class<?> getParentClass() {
        return null;
    }

    @Override
    public String getParentClassName() {
        return parentClassName;
    }

    private String getASMClassName() {
        return getClassName().replace('.', '/');
    }

    private String getASMParentClassName() {
        return parentClassName.replace('.', '/');
    }
}

