/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.oxm.XMLRoot;

public class Root {
    List companies;

    public Root() {
        companies = new ArrayList();
    }

    public void setCompanies(List companies) {
        this.companies = companies;
    }

    public List getCompanies() {
        return companies;
    }

    public boolean equals(Object o) {
        try {
            Root theRoot = (Root)o;

            if (theRoot.getCompanies().size() != getCompanies().size()) {
                return false;
            }

            /*if ((!theRoot.getCompanies().containsAll(getCompanies())) || (!getCompanies().containsAll(theRoot.getCompanies()))) {
                return false;
            }*/
            for (int i = 0; i < getCompanies().size(); i++) {
                Object first = getCompanies().get(i);
                Object second = theRoot.getCompanies().get(i);
                if (first instanceof Company && second instanceof Company) {
                    if (!first.equals(second)) {
                        return false;
                    }
                } else if (first instanceof XMLRoot && second instanceof XMLRoot) {
                    if (!(((XMLRoot)first).getLocalName().equals(((XMLRoot)second).getLocalName()))) {
                        return false;
                    }
                    if (!(((XMLRoot)first).getNamespaceURI().equals(((XMLRoot)second).getNamespaceURI()))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public String toString() {
        String string = "Root:";
        for (int i = 0; i < getCompanies().size(); i++) {
            string += getCompanies().get(i).toString();
        }
        return string;
    }
}
