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

/**
 * Visitor for {@link Facet}.
 *
 * @param <R> the return type of this visitor's methods.  Use {@link
 *            Void} for visitors that do not need to return results.
 * @param <P> the type of the additional parameter to this visitor's
 *            methods.  Use {@code Void} for visitors that do not need an
 *            additional parameter.
 *
 * @see Facet#accept(FacetVisitor, P)
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public interface FacetVisitor<R, P> {

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(DecimalMinFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(DecimalMaxFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(DigitsFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(MaxFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(MinFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(PatternFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(PatternListFacet t, P p);

    /**
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(SizeFacet t, P p);

}