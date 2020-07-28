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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.oxm.mappings.Login;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.DatasourceLogin;

/**
 * In OX, the platform determines which parsing method will be used, DOM vs SAX.
 *
 *<p><em>Code Sample</em><br>
 * <code>
 *
 * XMLLogin xmlLogin = new XMLLogin(new org.eclipse.persistence.oxm.platform.DOMPlatform);<br>
 * Project myProject = new MyTopLinkProject(xmlLogin)<br>
 *
 * </code>
 *
 * @see org.eclipse.persistence.oxm.platform.SAXPlatform
 * @see org.eclipse.persistence.oxm.platform.DOMPlatform
 *
 */
public class XMLLogin extends DatasourceLogin implements Login<Platform> {
    private boolean equalNamespaceResolvers;

    private DocumentPreservationPolicy documentPreservationPolicy;
    /**
     * Default constructor.
     * Sets the platform to be the default platform which is org.eclipse.persistence.oxm.platform.SAXPlatform.
     */
    public XMLLogin() {
        this(new SAXPlatform());
    }

    /**
     * Constructor, create a new XMLLogin based on the given platform.
     * Valid platforms are instances of org.eclipse.persistence.oxm.platform.DOMPlaform and
     * instances of org.eclipse.persistence.oxm.platform.SAXPlatform.
     * @param platform The platform to base this login on
     */
    public XMLLogin(Platform platform) {
        super(platform);
        equalNamespaceResolvers = true;
    }

    /**
     * INTERNAL:
     * Returns the appropriate accessor
     * @return an instance of org.eclipse.persistence.internal.oxm.XMLAccessor
     */
    public Accessor buildAccessor() {
        return new org.eclipse.persistence.internal.oxm.XMLAccessor();
    }

    /**
     * Return a String representation of the object.
     * @return a string representation of the receiver
     */
    public String toString() {
        return Helper.getShortClassName(this) + "(" + this.getUserName() + ")\n\t( " + this.getPlatformClassName() + ")";
    }

    @Override
    public DocumentPreservationPolicy getDocumentPreservationPolicy() {
        return this.documentPreservationPolicy;
    }

    @Override
    public void setDocumentPreservationPolicy(DocumentPreservationPolicy policy) {
        this.documentPreservationPolicy = policy;
    }

    public void setEqualNamespaceResolvers(boolean equalNRs) {
        this.equalNamespaceResolvers = equalNRs;
    }

    @Override
    public boolean hasEqualNamespaceResolvers() {
        return equalNamespaceResolvers;
    }
}
