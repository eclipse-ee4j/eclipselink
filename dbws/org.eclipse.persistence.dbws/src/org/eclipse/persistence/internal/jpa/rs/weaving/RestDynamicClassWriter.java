/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.rs.weaving;

import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.ITEM_LINKS_SIGNATURE;
import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.LINK_SIGNATURE;
import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.LIST_RELATIONSHIP_INFO_GENERIC_SIGNATURE;
import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.LIST_RELATIONSHIP_INFO_SIGNATURE;
import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.PERSISTENCE_FIELDNAME_PREFIX;
import static org.eclipse.persistence.internal.jpa.weaving.ClassWeaver.WEAVED_REST_LAZY_SHORT_SIGNATURE;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.GETFIELD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.PUTFIELD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.RETURN;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDynamicClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
/**
 * <p>
 * <b>INTERNAL:</b> RestClassWriter uses ASM to dynamically generate subclasses of
 * {@link DynamicEntity} with REST specific methods.
 *
 * @since EclipseLink 3.0
 */
public class RestDynamicClassWriter extends MetadataDynamicClassWriter {

    public RestDynamicClassWriter(MetadataDynamicClassWriter w) {
        super(w.getDescriptor());
        addInterface(WEAVED_REST_LAZY_SHORT_SIGNATURE);
    }

    @Override
    protected void addFields(ClassWriter cw, String parentClassType) {
        super.addFields(cw, parentClassType);
        // protected transient List<RelationshipInfo> _persistence_relationshipInfo;
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_TRANSIENT, PERSISTENCE_FIELDNAME_PREFIX + "relationshipInfo", LIST_RELATIONSHIP_INFO_SIGNATURE, LIST_RELATIONSHIP_INFO_GENERIC_SIGNATURE, null);
        fv.visitEnd();
        // protected transient Link _persistence_href;
        fv = cw.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_TRANSIENT, PERSISTENCE_FIELDNAME_PREFIX + "href", LINK_SIGNATURE, null, null);
        fv.visitEnd();
        // protected transient ItemLinks _persistence_links;
        fv = cw.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_TRANSIENT, PERSISTENCE_FIELDNAME_PREFIX + "links", ITEM_LINKS_SIGNATURE, null, null);
        fv.visitEnd();
    }

    @Override
    protected void addMethods(ClassWriter cw, String parentClassType) {
        String clsName = getDescriptor().getJavaClassName().replace('.', '/');
        super.addMethods(cw, parentClassType);

        // public List<RelationshipInfo> _persistence_getRelationships() {
        //   return this._persistence_relationshipInfo;
        // }
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "getRelationships", "()" + LIST_RELATIONSHIP_INFO_SIGNATURE, "()" + LIST_RELATIONSHIP_INFO_GENERIC_SIGNATURE, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "relationshipInfo", LIST_RELATIONSHIP_INFO_SIGNATURE);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // public void _persistence_setRelationships(List<RelationshipInfo> paramList) {
        //   this._persistence_relationshipInfo = paramList;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "setRelationships", "(" + LIST_RELATIONSHIP_INFO_SIGNATURE + ")V", "(" + LIST_RELATIONSHIP_INFO_GENERIC_SIGNATURE + ")V", null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "relationshipInfo", LIST_RELATIONSHIP_INFO_SIGNATURE);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // public Link _persistence_getHref() {
        //   return this._persistence_href;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "getHref", "()" + LINK_SIGNATURE, null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "href", LINK_SIGNATURE);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // public void _persistence_setHref(Link paramLink)
        //   this._persistence_href = paramLink;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "setHref", "(" + LINK_SIGNATURE + ")V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "href", LINK_SIGNATURE);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // public ItemLinks _persistence_getLinks() {
        //   return this._persistence_links;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "getLinks", "()" + ITEM_LINKS_SIGNATURE, null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "links", ITEM_LINKS_SIGNATURE);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // public void _persistence_setLinks(ItemLinks paramItemLinks) {
        //   this._persistence_links = paramItemLinks;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, PERSISTENCE_FIELDNAME_PREFIX + "setLinks", "(" + ITEM_LINKS_SIGNATURE + ")V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, clsName, PERSISTENCE_FIELDNAME_PREFIX + "links", ITEM_LINKS_SIGNATURE);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }
}
