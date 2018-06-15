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

import javax.validation.constraints.Pattern;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class PatternFacet implements Facet {

    private final String regexp;

    private final javax.validation.constraints.Pattern.Flag[] flags;

    public PatternFacet(String regexp, Pattern.Flag[] flags) {
        this.regexp = regexp;
        this.flags = flags;
    }

    public String getRegexp() {
        return regexp;
    }

    public Pattern.Flag[] getFlags() {
        return flags;
    }

    @Override
    public <R, P> R accept(FacetVisitor<R, P> visitor, P p) {
        return visitor.visit(this, p);
    }
}
