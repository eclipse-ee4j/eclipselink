/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler.facets;

import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;

import java.util.List;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class PatternListFacet implements Facet {

    private final List<PatternFacet> patterns;

    public PatternListFacet(List<PatternFacet> patterns) {
        this.patterns = patterns;
    }

    public List<PatternFacet> getPatterns() {
        return patterns;
    }

    public void addPattern(PatternFacet pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public <R, P> R accept(FacetVisitor<R, P> visitor, P p) {
        return visitor.visit(this, p);
    }
}