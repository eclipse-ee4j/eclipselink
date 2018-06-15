/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.jaxb.compiler.facets;

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
