/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      tware - initial
package org.eclipse.persistence.jpa.rs.util.xmladapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.weaving.RelationshipInfo;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.util.IdHelper;

public class RelationshipLinkAdapter extends XmlAdapter<Link, RelationshipInfo> {

    private String baseURI = null;
    private PersistenceContext context = null;

    public RelationshipLinkAdapter(){
    }

    public RelationshipLinkAdapter(String baseURI, PersistenceContext context){
        this.baseURI = baseURI;
        this.context = context;
    }

    @Override
    public RelationshipInfo unmarshal(Link v) throws Exception {
        RelationshipInfo info = new RelationshipInfo();
        info.setAttributeName(v.getRel());
        return info;
    }

    @Override
    public Link marshal(RelationshipInfo v) throws Exception {
        if (null == v) {
            return null;
        }

        String version = context.getVersion();
        String href = null;
        if (version != null) {
            href = baseURI + version + "/" + context.getName() + "/entity/"  + v.getOwningEntityAlias() + "/" + IdHelper.stringifyId(v.getOwningEntity(), v.getOwningEntityAlias(), context) + "/" + v.getAttributeName();
        } else {
            href = baseURI + context.getName() + "/entity/"  + v.getOwningEntityAlias() + "/" + IdHelper.stringifyId(v.getOwningEntity(), v.getOwningEntityAlias(), context) + "/" + v.getAttributeName();
        }
        return new Link(v.getAttributeName(), null, href);
    }
}
