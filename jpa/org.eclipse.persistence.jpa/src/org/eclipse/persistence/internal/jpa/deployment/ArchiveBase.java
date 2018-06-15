/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - refactor of Archive to allow to work with Gemini
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Abstract base class of all Archives.  Holds the base URL and the location of the persistence
 * descriptor
 * @author tware
 *
 */
public abstract class ArchiveBase {

    protected URL rootURL;

    protected String descriptorLocation;

    /**
     * Default Constructor.  If you use this constructor, this class will assume
     * you set the rootURL and descriptorLocation prior to using it
     */
    public ArchiveBase(){
        super();
    }

    public ArchiveBase(URL rootUrl, String descriptorLocation){
        this.rootURL = rootUrl;
        this.descriptorLocation = descriptorLocation;
    }

    public InputStream getDescriptorStream() throws IOException {
        // this method can only be called if the descriptor location has been set
        assert(descriptorLocation != null);
        return getEntry(descriptorLocation);
    }

    public abstract InputStream getEntry(String entryPath) throws IOException;

    public URL getRootURL() {
        return rootURL;
    }

    public void setRootURL(URL rootURL) {
        this.rootURL = rootURL;
    }

    public String getDescriptorLocation() {
        return descriptorLocation;
    }

    public void setDescriptorLocation(String descriptorLocation) {
        this.descriptorLocation = descriptorLocation;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + this.rootURL + ")";
    }
}

