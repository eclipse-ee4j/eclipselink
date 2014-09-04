/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  Contributors:
 *      Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;

import java.util.List;

/**
 * JPARS 2.0 collection proxy interface. Implemented by all REST collection proxies.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public interface CollectionProxy {

    List<LinkV2> getLinks();

    void setLinks(List<LinkV2> links);
}
