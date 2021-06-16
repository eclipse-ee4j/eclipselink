/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.queries;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchAttributeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchGroupMetadata;
import org.eclipse.persistence.jpa.config.FetchAttribute;
import org.eclipse.persistence.jpa.config.FetchGroup;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class FetchGroupImpl extends MetadataImpl<FetchGroupMetadata> implements FetchGroup {

    public FetchGroupImpl() {
        super(new FetchGroupMetadata());

        getMetadata().setFetchAttributes(new ArrayList<FetchAttributeMetadata>());
    }

    @Override
    public FetchAttribute addAttribute() {
        FetchAttributeImpl attribute = new FetchAttributeImpl();
        getMetadata().getFetchAttributes().add(attribute.getMetadata());
        return attribute;
    }

    @Override
    public FetchGroup setLoad(Boolean load) {
        getMetadata().setLoad(load);
        return this;
    }

    @Override
    public FetchGroup setName(String name) {
        getMetadata().setName(name);
        return this;
    }

}
