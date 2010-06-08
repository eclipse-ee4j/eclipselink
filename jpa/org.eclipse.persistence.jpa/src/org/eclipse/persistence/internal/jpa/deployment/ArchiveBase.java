/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - refactor of Archive to allow to work with Gemini
 ******************************************************************************/  
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

    
    
    
}
