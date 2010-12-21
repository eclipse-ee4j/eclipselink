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
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;

/**
 * <p>
 * <b>INTERNAL:</b> XRDynamicClassLoader uses ASM to dynamically
 * generate subclasses of {@link XRDynamicEntity}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class XRDynamicClassLoader extends DynamicClassLoader {

    public static final String COLLECTION_WRAPPER_SUFFIX =
        "_CollectionWrapper";

    private Boolean generateSubclasses = Boolean.TRUE;

    public XRDynamicClassLoader(ClassLoader parentLoader) {
        super(parentLoader, new XRClassWriter());
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (!generateSubclasses.booleanValue()) {
            throw new ClassNotFoundException(className);
        }
        try {
            byte[] data = defaultWriter.writeClass(this, className);
            return defineClass(className, data, 0, data.length);
        }
        catch (ClassFormatError cfe) {
            throw new ClassNotFoundException(className, cfe);
        }
        catch (ClassCircularityError cce) {
            throw new ClassNotFoundException(className, cce);
        }
    }

    public void dontGenerateSubclasses() {
        this.generateSubclasses = Boolean.TRUE;
    }
}