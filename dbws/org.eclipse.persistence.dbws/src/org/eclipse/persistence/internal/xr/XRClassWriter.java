/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;
import org.eclipse.persistence.internal.xr.IndexInfo;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

/**
 * <p>
 * <b>INTERNAL:</b> XRClassWriter uses ASM to dynamically
 * generate subclasses of {@link XRDynamicEntity}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

public class XRClassWriter extends DynamicClassWriter {

    private static final String MAP_CLASSNAME_SLASHES =
        Map.class.getName().replace('.', '/');
    private static final String STRING_CLASSNAME_SLASHES =
        String.class.getName().replace('.', '/');
    private static final String XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES =
        XRDynamicEntity.class.getName().replace('.', '/');
    private static final String XR_INDEX_INFO_CLASSNAME_SLASHES =
        IndexInfo.class.getName().replace('.', '/');
    private static final String XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES =
        XRDynamicEntity_CollectionWrapper.class.getName().replace('.', '/');

	public XRClassWriter() {
		super();
	}

    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {
		/*
		 * Pattern is as follows:
		 * public class Foo extends XRDynamicEntity {
		 *
		 *   public Foo(Map<String, IndexInfo> propertyNames2indexes) {
		 *     super(propertyNames2indexes);
		 *   }
		 * }
		 */
		String classNameAsSlashes = className.replace('.', '/');
		ClassWriter cw = new ClassWriter(true);
		CodeVisitor cv;
		// special-case: XRDynamicEntityCollection is a sub-class of XRDynamicEntity
		// with a single attribute "items"; constructor is slightly different
		if (className.endsWith(COLLECTION_WRAPPER_SUFFIX)) {
			cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes,
				XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, null, null);
			cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			cv.visitVarInsn(ALOAD, 0);
			cv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES,
				"<init>", "()V");
			cv.visitInsn(RETURN);
			cv.visitMaxs(0, 0);
		}
		else {
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes,
                XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, null, null);
            SignatureAttribute methodAttrs1 = 
                new SignatureAttribute("(L" + MAP_CLASSNAME_SLASHES +
                	"<L" + STRING_CLASSNAME_SLASHES + ";" +
                    "L" + XR_INDEX_INFO_CLASSNAME_SLASHES + ";>;)V");
            cv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + MAP_CLASSNAME_SLASHES + ";)V",
                null, methodAttrs1);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, 
                "<init>", "(L" + MAP_CLASSNAME_SLASHES + ";)V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
		}
		cw.visitEnd();
		return cw.toByteArray();
    }
}