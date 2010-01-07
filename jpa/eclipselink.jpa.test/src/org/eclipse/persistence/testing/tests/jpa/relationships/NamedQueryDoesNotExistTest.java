/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.relationships;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * Tests trying to execute a named query that does not exist.
 * 
 * @author Guy Pelletier
 */
public class NamedQueryDoesNotExistTest extends EntityContainerTestBase {
    protected boolean m_reset = false; // reset gets called twice on error
  
    protected Exception m_exception;
    protected boolean m_npeCaught;
    protected boolean m_illegalArgumentExceptionCaught;

    public NamedQueryDoesNotExistTest()  {
        setDescription("Looks for an expected exception when running a named query that does not exist.");
    }
  
    public void setup (){
        super.setup();
        m_reset = true;
        m_npeCaught = false;
        m_illegalArgumentExceptionCaught = false;
    }
    
    public void reset (){
        if (m_reset) { //ensures it is only done once
            m_reset = false;
        }
    }
  
    public void test(){
        try {
            getEntityManager().createNamedQuery("doesNotExist").getResultList();
        } catch (NullPointerException e) {
            m_npeCaught = true;
        } catch (IllegalArgumentException e) {
            m_illegalArgumentExceptionCaught = true;
        } catch (Exception e) {
            m_exception = e;
        }
    }
  
    public void verify(){
        if (m_npeCaught) {
            throw new TestErrorException("A null pointer exception caught on the query.");
        } else if (!m_illegalArgumentExceptionCaught) {
            if (m_exception != null) {
                throw new TestErrorException("Expected IllegalArgumentException, caught: " + m_exception);
            } else {
                throw new TestErrorException("No exception was caught on a named query that does not exist.");
            }
        }
    }
}
