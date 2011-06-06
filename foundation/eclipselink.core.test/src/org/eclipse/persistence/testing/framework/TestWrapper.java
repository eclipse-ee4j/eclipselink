/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework;


/**
 * <p>Purpose<b></b>:A wrapper around TestCase.
 * Use it to aggregate test instead of inheriting from it.
 * Suppose we need to amend in the same way
 * setup and reset for unspecified number of TestCase's subclasses.
 * Using TestWrapper, we need only one new class to define:
 * public class MyTestWrapper extends TestWrapper {
 *   public MyTestWrapper(TestCase test) {
 *      super(test);
 *   }
 *   protected void setup() throws Exception {
 *       // add something
 *       super.setup();
 *   }
 *   public void reset() throws Exception {
 *      super.reset();
 *      // add something
 *   }
 * }
 *
 * Note that TestWrapper doesn't call the "wrapped" test set/get methods,
 * except setExecutor(..)/getExecutor().
 * That allows to use several wrappers around the same "wrapped" test,
 * and it is important for TestVariation.
 *
 */
public class TestWrapper extends TestCase {
    protected TestCase test;

    public TestWrapper(TestCase test) {
        this.test = test;
        setName(test.getName());
        setDescription(test.getDescription());
    }

    protected void setup() throws Throwable {
        test.setup();
    }

    protected void test() throws Throwable {
        test.test();
    }

    protected void verify() throws Throwable {
        test.verify();
    }

    public void reset() throws Throwable {
        test.reset();
    }

    protected void resetVerify() throws Throwable {
        test.resetVerify();
    }

    public TestExecutor getExecutor() {
        return test.getExecutor();
    }

    public void setExecutor(TestExecutor anExecutor) {
        test.setExecutor(anExecutor);
    }
    
    public TestCase getWrappedTest() {
        return test;
    }
}
