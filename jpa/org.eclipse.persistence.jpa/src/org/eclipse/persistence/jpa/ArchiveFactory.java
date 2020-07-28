/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - extension to allow ArchiveFactories to be pluggable
package org.eclipse.persistence.jpa;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * This interface should be implemented by users that want to provide a custom way
 * of dealing with archives that contain persistence units.  An implementer of this class
 * can be enabled by providing a System property
 *
 * @see org.eclipse.persistence.config.SystemProperties
 * @author tware
 *
 */
public interface ArchiveFactory {

    /**
     * Return an instance of an implementer of Archive that can process the URL provided
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public Archive createArchive(URL rootUrl, Map properties) throws URISyntaxException, IOException;

    /**
     * Return an instance of an implementer of Archive that can process the URL provided
     * This instance will allow access to the persistence descriptor associated with
     * this archive through the getDescriptorStream() method
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public Archive createArchive(URL rootUrl, String descriptorLocation, Map properties) throws URISyntaxException, IOException;
}
