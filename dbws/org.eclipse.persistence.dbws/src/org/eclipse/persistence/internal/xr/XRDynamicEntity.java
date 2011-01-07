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
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;

/**
 * <p>
 * <b>INTERNAL:</b> XRDynamicEntity is used for models where Java classes do not
 * exist.
 * <p>
 * EclipseLink is based around mapping attributes of a Java class to a table (or
 * tables) with the attributes representing either the column data or
 * foreign-key contraints as relationships to other (mapped) classes. For
 * applications that are based around meta-data and the Java class is either not
 * needed or not available, this basic entity can be used. Subclasses of this
 * abstract class can be dynamically generated at runtime.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public abstract class XRDynamicEntity extends DynamicEntityImpl {

    public XRDynamicEntity() {
        super();
    }

    // use co-variant return override capability (since JDK 1.5)
    @Override
    public abstract XRDynamicPropertiesManager fetchPropertiesManager();

}