/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dclarke/tware - initial API and implementation
//     05/26/2016-2.7 Tomas Kraus
//       - 494610: Session Properties map should be Map<String, Object>
package org.eclipse.persistence.jpa.rs.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.URLArchive;

/**
 * This archive is designed for use with dynamic persistence units
 * it is built with a stream that allows it to read a persistence.xml file and creates a fake base URL
 * based the classpath location of the InMemoryArchiveClass
 * @author tware
 *
 */
public class InMemoryArchive extends URLArchive {

    private InputStream stream = null;

    private InMemoryArchive(){
        super((URL)null, (String)null);
        String persistenceFactoryResource = InMemoryArchive.class.getName().replace('.', '/') + ".class";
        URL myURL = InMemoryArchive.class.getClassLoader().getResource(persistenceFactoryResource);
        try{
            myURL = PersistenceUnitProcessor.computePURootURL(myURL, persistenceFactoryResource);
        } catch (URISyntaxException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        this.rootURL = myURL;
    }

    public InMemoryArchive(InputStream stream){
        this();
        this.stream = stream;
    }

    @Override
    public InputStream getDescriptorStream() throws IOException {
        return stream;
    }

    @Override
    public void close() {
        super.close();
        try{
            stream.close();
        } catch (IOException e){};
        stream = null;

    }

}
