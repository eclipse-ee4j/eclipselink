/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith
package org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem;

import jakarta.xml.bind.JAXBException;
import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class ObjectFactoryTestCases extends TestCase {

    public void testCreateJAXBContextWithDuplicateElem() throws JAXBException {
        try{
           JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem", this.getClass().getClassLoader());
        }catch(JAXBException e){
            Throwable nested = e.getLinkedException();
            if(nested instanceof org.eclipse.persistence.exceptions.JAXBException){
                assertEquals(org.eclipse.persistence.exceptions.JAXBException.DUPLICATE_ELEMENT_NAME, ((org.eclipse.persistence.exceptions.JAXBException)nested).getErrorCode());
                return;
            }
        }
        fail("A duplicate element exception should have occurred.");
    }

}
