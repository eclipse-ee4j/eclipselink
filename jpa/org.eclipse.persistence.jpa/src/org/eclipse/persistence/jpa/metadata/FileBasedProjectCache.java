/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/01/2012-2.5 Chris Delahunt - Bug 371950 - Metadata caching
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.jpa.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>: Support serializing/deserializing a project representing application metadata
 * to/from a file.
 *
 */
public class FileBasedProjectCache implements ProjectCache {

    @Override
    public Project retrieveProject(Map properties, ClassLoader loader, SessionLog log) {
        Project project = null;
        java.io.ObjectInputStream in = null;
        String fileName = (String)getConfigPropertyLogDebug(
                PersistenceUnitProperties.PROJECT_CACHE_FILE,
                properties, log);
        if (fileName != null && fileName.length() > 0) {
            try {
                java.io.File file = new java.io.File(fileName);
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                in = new java.io.ObjectInputStream(fis);
                project = (Project)in.readObject();
            } catch (Exception e) {
              //need exception differentiation,logging and warnings
              //the project not being cached should be different than an exception from reading the stream
              log.logThrowable(SessionLog.WARNING, SessionLog.JPA, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (java.io.IOException ignore) {
                        //ignore exceptions from close
                    }
                }
            }
        }
        return project;
    }

    @Override
    public void storeProject(Project project, Map properties, SessionLog log) {
        String fileName = (String)getConfigPropertyLogDebug(
                PersistenceUnitProperties.PROJECT_CACHE_FILE,
                properties, log);
        if (fileName != null && fileName.length() > 0) {
            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            try {
                File file = new File(fileName);
                // creates the file
                file.createNewFile();
                fos = new FileOutputStream(file);
                out = new ObjectOutputStream(fos);
                out.writeObject(project);
            } catch (Exception e) {
                //the session is still usable, just not cachable so log a warning
                log.logThrowable(SessionLog.WARNING, SessionLog.JPA, e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        fos = null;
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (java.io.IOException ignore) {}
            }
        }
    }

    /**
     * Check the provided map for an object with the given name.  If that object is not available, check the
     * System properties.  Log the value returned if logging is enabled at the FINEST level
     * @param propertyName
     * @param properties
     * @param log
     * @return
     */
    public Object getConfigPropertyLogDebug(final String propertyName, Map properties, SessionLog log) {
        Object value = null;
        if (properties != null) {
            value = properties.get(propertyName);
        }
        if (value == null) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                value = AccessController.doPrivileged(new PrivilegedGetSystemProperty(propertyName));
            } else {
                value = System.getProperty(propertyName);
            }
        }
        if ((value != null) && (log !=  null)) {
            log.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyName, value});
        }
        return value;
    }
}
