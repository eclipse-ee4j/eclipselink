/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.xr;

//javase imports

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

/**
 * <p>
 * <b>INTERNAL:</b> Static helper methods that reads EclipseLink project deployment
 * XML without classes.
 * <p>
 * This API only supports EclipseLink 1.x format deployment XML
 */

public final class ProjectHelper {

    private ProjectHelper() {
        // no instance please
    }

    /**
     * INTERNAL: Fix the given EclipseLink OR and OX projects so that the
     * descriptors for all generated sub-classes of XRDynamicEntity have the correct
     * AttributeAccessors.
     */
    public static void fixOROXAccessors(Project orProject, Project oxProject) {
        for (Iterator<ClassDescriptor> i = orProject.getDescriptors().values().iterator(); i.hasNext();) {
            ClassDescriptor desc = i.next();
            if (!XRDynamicEntity.class.isAssignableFrom(desc.getJavaClass())) {
                continue;
            }
            Class<?> clz = desc.getJavaClass();
            ClassDescriptor xdesc = null;
            if (oxProject != null) {
                xdesc = oxProject.getDescriptorForAlias(desc.getAlias());
            }
            XRDynamicPropertiesManager xrDPM = null;
            if (!clz.getName().endsWith(COLLECTION_WRAPPER_SUFFIX)) {
                try {
                    XRDynamicEntity newInstance = (XRDynamicEntity) clz.getConstructor().newInstance();
                    xrDPM = newInstance.fetchPropertiesManager();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Set<String> propertiesNameSet = new HashSet<>();
            for (Iterator<DatabaseMapping> j = desc.getMappings().iterator(); j.hasNext();) {
                DatabaseMapping dm = j.next();
                String attributeName = dm.getAttributeName();
                DatabaseMapping xdm = null;
                if (xdesc != null) {
                    xdm = xdesc.getMappingForAttributeName(attributeName);
                }
                dm.setAttributeAccessor(new XRDynamicEntityAccessor(dm));
                if (xdm != null) {
                    if (dm.isForeignReferenceMapping()) {
                        ForeignReferenceMapping frm = (ForeignReferenceMapping)dm;
                        if (frm.usesIndirection() && frm.getIndirectionPolicy().getClass().
                            isAssignableFrom(BasicIndirectionPolicy.class)) {
                            xdm.setAttributeAccessor(new XRDynamicEntityVHAccessor(dm));
                        }
                        else {
                            // no indirection or indirection that is transparent enough (!) to work
                            xdm.setAttributeAccessor(new XRDynamicEntityAccessor(dm));
                        }
                    }
                    else {
                        xdm.setAttributeAccessor(new XRDynamicEntityAccessor(dm));
                    }
                }
                propertiesNameSet.add(attributeName);
            }
            if (xrDPM != null) {
                xrDPM.setPropertyNames(propertiesNameSet);
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
        if (cl instanceof XRDynamicClassLoader xrdecl) {
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
            if (cl instanceof XRDynamicClassLoader xrdecl) {
                xrdecl.dontGenerateSubclasses();
            }
        }
    }
}
