/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     08/02/2022-4.0.0 Tomas Kraus - 1613: FileBasedProjectCache retrieveProject contains readObject with no data filtering
package org.eclipse.persistence.jpa.metadata;

import java.io.ObjectInputFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;

/**
 * Verify content of FileBasedProjectCache being read with ObjectInputStream.
 */
class FileBasedProjectCacheFilter implements ObjectInputFilter {

    private final SessionLog log;

    /**
     * Creates an instance of ObjectInputStream data of FileBasedProjectCache verifier.
     */
    FileBasedProjectCacheFilter(SessionLog log) {
        this.log = log;
        //log.setLevel(SessionLog.FINEST);
    }

    // ObjectInputStream data verification entry point.
    // This method is being called for every object instance being deserialized.
    @Override
    public Status checkInput(FilterInfo info) {
        try {
            // Skip checks when decision was already made by higher level filter.
            ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
            if (serialFilter != null) {
                ObjectInputFilter.Status status = serialFilter.checkInput(info);
                if (status != ObjectInputFilter.Status.UNDECIDED) {
                    // The process-wide filter overrides this filter
                    return status;
                }
            }
            // Check current object instance.
            final Class<?> serialClass = info.serialClass();
            if (serialClass != null) {
                log.log(SessionLog.FINE, String.format(
                        "FileBasedProjectCacheFilter: processing level %d class %s",
                        info.depth(), serialClass.getName()));
                // Only Project class instance is allowed on top level of the structure
                // and class must also come from known .jar file name.
                if (info.depth() == 1L) {
                    if (serialClass != Project.class || !isEclipseLinkFileJar(serialClass, log)) {
                        log.log(SessionLog.WARNING, String.format(
                                "FileBasedProjectCacheFilter: rejected illegal top level FileBasedProjectCache class: %s",
                                serialClass.getName()));
                        return Status.REJECTED;
                    }
                }
                // Remote interface must be rejected
                if (info.serialClass() != null && Remote.class.isAssignableFrom(info.serialClass())) {
                    log.log(SessionLog.WARNING, String.format(
                            "FileBasedProjectCacheFilter: rejected illegal Remote FileBasedProjectCache class: %s",
                            serialClass.getName()));
                    return Status.REJECTED;
                }
            }
            return Status.UNDECIDED;
        } catch (Throwable t) {
            log.log(SessionLog.WARNING, "Exception in FileBasedProjectCacheFilter check", t);
            throw t;
        }
    }

    // Check whether Eclipselink own class is from known .jar file name.
    private static boolean isEclipseLinkFileJar(final Class<?> serialClass, final SessionLog log) {
        final URL classUrl = serialClass.getResource(
                '/' + serialClass.getName().replace('.', '/') + ".class");
        if (classUrl != null && "jar".equals(classUrl.getProtocol())) {
            final String classFile = classUrl.getPath();
            try {
                final URL fileUrl = new URL(classFile);
                if ("file".equals(fileUrl.getProtocol())) {
                    final String filePaths = fileUrl.getPath();
                    final int delimPos = filePaths.indexOf('!');
                    String file;
                    if (delimPos > 0) {
                        file = filePaths.substring(0, delimPos);
                    } else {
                        file = filePaths;
                    }
                    final int pathSepPos = file.lastIndexOf('/');
                    if (pathSepPos > 0 && file.length() > pathSepPos + 1) {
                        file = file.substring(pathSepPos + 1);
                    }
                    if (file.startsWith("org.eclipse.persistence") && file.contains(Version.getVersion())) {
                        log.log(SessionLog.FINER, String.format(
                                "FileBasedProjectCacheFilter: class %s from known jar file %s",
                                serialClass.getName(), file));
                        return true;
                    }
                }
            } catch (MalformedURLException e) {
                log.log(SessionLog.WARNING, String.format(
                        "FileBasedProjectCacheFilter: could not find source .jar of class %s",
                        serialClass.getName()));
            }
        } else {
            log.log(SessionLog.WARNING, String.format(
                    "FileBasedProjectCacheFilter: could not find source .jar of class %s",
                    serialClass.getName()));
        }
        return false;
    }

}
