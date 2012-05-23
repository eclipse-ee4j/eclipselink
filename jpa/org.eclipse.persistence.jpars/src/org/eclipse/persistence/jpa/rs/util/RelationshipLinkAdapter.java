/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.metadata.model.Link;

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
        return null;
    }
    
    @Override
    public Link marshal(RelationshipInfo v) throws Exception {
        if (null == v) {
            return null;
        }
        String href = baseURI + context.getName() + "/entity/"  + v.getOwningEntityAlias() + "/" + IdHelper.stringifyId(v.getOwningEntity(), v.getOwningEntityAlias(), context) + "/" + v.getAttributeName();
        return new Link(v.getAttributeName(), null, href);
    }
}
