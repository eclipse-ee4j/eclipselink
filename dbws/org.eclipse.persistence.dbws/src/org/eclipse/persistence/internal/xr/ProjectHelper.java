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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.lang.reflect.Method;
import java.util.Iterator;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.dynamicpersist.BaseEntity;
import org.eclipse.persistence.internal.dynamicpersist.BaseEntityAccessor;
import org.eclipse.persistence.internal.dynamicpersist.BaseEntityClassLoader;
import org.eclipse.persistence.internal.dynamicpersist.BaseEntityVHAccessor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.internal.helper.ClassConstants.OBJECT;
import static org.eclipse.persistence.internal.xr.Util.SCHEMA_2_CLASS;


/**
 * <p>
 * <b>INTERNAL:</b> Static helper methods that reads EclipseLink project deployment
 * XML without classes.
 * <p>
 * This API only supports EclipseLink 1.x format deployment XML
 */

public class ProjectHelper {

    /**
     * INTERNAL: Fix the given TopLink OR and OX projects so that the
     * descriptors for all generated sub-classes of BaseEntity have the correct
     * AttributeAccessors.
     */
    @SuppressWarnings("unchecked")
    public static void fixOROXAccessors(Project orProject, Project oxProject) {

        for (Iterator i = orProject.getDescriptors().values().iterator(); i.hasNext();) {
            ClassDescriptor desc = (ClassDescriptor) i.next();
            if (!BaseEntity.class.isAssignableFrom(desc.getJavaClass())) {
                continue;
            }
            ClassDescriptor xdesc = oxProject.getDescriptorForAlias(desc.getAlias());
            int idx = 0;
            for (Iterator j = desc.getMappings().iterator(); j.hasNext();) {
                DatabaseMapping dm = (DatabaseMapping) j.next();
                String attributeName = dm.getAttributeName();
                DatabaseMapping xdm = xdesc.getMappingForAttributeName(attributeName);
                dm.setAttributeAccessor(new BaseEntityAccessor(attributeName, idx));
                if (xdm != null) {
                    if (dm.isForeignReferenceMapping()) {
                        ForeignReferenceMapping frm = (ForeignReferenceMapping)dm;
                        if (frm.usesIndirection() && frm.getIndirectionPolicy().getClass().
                            isAssignableFrom(BasicIndirectionPolicy.class)) {
                            xdm.setAttributeAccessor(new BaseEntityVHAccessor(attributeName, idx));
                        } else {
                            // no indirection or indirection that is transparent enough (!) to work
                            xdm.setAttributeAccessor(new BaseEntityAccessor(attributeName, idx));
                        }
                    }
                    else {
                        xdm.setAttributeAccessor(new BaseEntityAccessor(attributeName, idx));
                        if (xdm.isDirectToFieldMapping()) {
                            XMLDirectMapping xmlDM = (XMLDirectMapping)xdm;
                            XMLField xmlField = (XMLField)xmlDM.getField();
                            Class clz = SCHEMA_2_CLASS.get(xmlField.getSchemaType());
                            if (clz != null) {
                                xmlField.setType(clz);
                            }
                            else {
                                xmlField.setType(OBJECT);
                            }
                        }
                        else if (xdm.isAbstractCompositeDirectCollectionMapping()) {
                            AbstractCompositeDirectCollectionMapping acdcm =
                                (AbstractCompositeDirectCollectionMapping)xdm;
                            XMLField xmlField = (XMLField)acdcm.getField();
                            Class clz = SCHEMA_2_CLASS.get(xmlField.getSchemaType());
                            if (clz != null) {
                                xmlField.setType(clz);
                            }
                            else {
                                xmlField.setType(OBJECT);
                            }
                        }
                    }
                }
                idx++;
            }
            try {
                Class clz = desc.getJavaClass();
                Method setNumAttrs = clz.getMethod("setNumAttributes", Integer.class);
                setNumAttrs.invoke(clz, new Integer(idx));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // turn-off dynamic class generation
        ClassLoader cl = null;
        Login login = orProject.getDatasourceLogin();
        if (login != null) {
            Platform platform = login.getDatasourcePlatform();
            if (platform != null) {
                ConversionManager conversionManager = platform.getConversionManager();
                if (conversionManager != null) {
                    cl = conversionManager.getLoader();
                }
            }
        }
        if (cl != null && cl instanceof BaseEntityClassLoader) {
            BaseEntityClassLoader becl = (BaseEntityClassLoader)cl;
            becl.dontGenerateSubclasses();
        }
        cl = null;
        login = oxProject.getDatasourceLogin();
        if (login != null) {
            Platform platform = login.getDatasourcePlatform();
            if (platform != null) {
                ConversionManager conversionManager = platform.getConversionManager();
                if (conversionManager != null) {
                    cl = conversionManager.getLoader();
                }
            }
        }
        if (cl != null && cl instanceof BaseEntityClassLoader) {
            BaseEntityClassLoader becl = (BaseEntityClassLoader)cl;
            becl.dontGenerateSubclasses();
        }
    }
}
