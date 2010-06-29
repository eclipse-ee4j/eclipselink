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

//javase imports
import java.lang.reflect.Field;
import java.util.Iterator;

//java eXtension imports

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.internal.xr.XRDynamicEntity.XR_FIELD_INFO_STATIC;

/**
 * <p>
 * <b>INTERNAL:</b> Static helper methods that reads EclipseLink project deployment
 * XML without classes.
 * <p>
 * This API only supports EclipseLink 1.x format deployment XML
 */

@SuppressWarnings({"unchecked"/*, "rawtypes"*/})
public class ProjectHelper {
    /**
     * INTERNAL: Fix the given EclipseLink OR and OX projects so that the
     * descriptors for all generated sub-classes of XRDynamicEntity have the correct
     * AttributeAccessors.
     */
    public static void fixOROXAccessors(Project orProject, Project oxProject) {
        for (Iterator i = orProject.getDescriptors().values().iterator(); i.hasNext();) {
            ClassDescriptor desc = (ClassDescriptor)i.next();
            Class clz = desc.getJavaClass();
            if (!XRDynamicEntity.class.isAssignableFrom(clz)) {
                continue;
            }
            ClassDescriptor xdesc = null;
            if (oxProject != null) { 
                xdesc = oxProject.getDescriptorForAlias(desc.getAlias());
            }
            int idx = 0;
            XRFieldInfo xrfi = null;
            if (!clz.getName().endsWith(COLLECTION_WRAPPER_SUFFIX)) {
                try {
                    Field xrfiField = Helper.getField(clz, XR_FIELD_INFO_STATIC);
                    xrfi = (XRFieldInfo)xrfiField.get(null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Iterator j = desc.getMappings().iterator(); j.hasNext();) {
                DatabaseMapping dm = (DatabaseMapping)j.next();
                String attributeName = dm.getAttributeName();
                DatabaseMapping xdm = null;
                if (xdesc != null) {
                    xdm = xdesc.getMappingForAttributeName(attributeName);
                }
                dm.setAttributeAccessor(new XRDynamicEntityAccessor(attributeName, idx));
                if (xdm != null) {
                    if (dm.isForeignReferenceMapping()) {
                        ForeignReferenceMapping frm = (ForeignReferenceMapping)dm;
                        if (frm.usesIndirection() && frm.getIndirectionPolicy().getClass().
                            isAssignableFrom(BasicIndirectionPolicy.class)) {
                            xdm.setAttributeAccessor(new XRDynamicEntityVHAccessor(attributeName, idx));
                        }
                        else {
                            // no indirection or indirection that is transparent enough (!) to work
                            xdm.setAttributeAccessor(new XRDynamicEntityAccessor(attributeName, idx));
                        }
                    }
                    else {
                        xdm.setAttributeAccessor(new XRDynamicEntityAccessor(attributeName, idx));
                        /*
                        if (xdm.isDirectToFieldMapping()) {
                            XMLDirectMapping xmlDM = (XMLDirectMapping)xdm;
                            XMLField xmlField = (XMLField)xmlDM.getField();
                            Class schemaClass = SCHEMA_2_CLASS.get(xmlField.getSchemaType());
                            if (schemaClass != null) {
                                xmlField.setType(schemaClass);
                            }
                            else {
                                xmlField.setType(OBJECT);
                            }
                        }
                        else if (xdm.isAbstractCompositeDirectCollectionMapping()) {
                            AbstractCompositeDirectCollectionMapping acdcm =
                                (AbstractCompositeDirectCollectionMapping)xdm;
                            XMLField xmlField = (XMLField)acdcm.getField();
                            Class schemaClass = SCHEMA_2_CLASS.get(xmlField.getSchemaType());
                            if (clz != null) {
                                xmlField.setType(schemaClass);
                            }
                            else {
                                xmlField.setType(OBJECT);
                            }
                        }
                        */
                    }
                }
                if (xrfi != null) {
                    xrfi.addFieldInfo(attributeName, idx);
                }
                idx++;
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
        if (cl != null && cl instanceof XRDynamicClassLoader) {
            XRDynamicClassLoader xrdecl = (XRDynamicClassLoader)cl;
            xrdecl.dontGenerateSubclasses();
        }
        if (oxProject != null) {
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
            if (cl != null && cl instanceof XRDynamicClassLoader) {
                XRDynamicClassLoader xrdecl = (XRDynamicClassLoader)cl;
                xrdecl.dontGenerateSubclasses();
            }
        }
    }
}