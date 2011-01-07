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
package org.eclipse.persistence.testing.models.sequencing;

/**
 * Test Data for the Sequence Test Project
 */
public class SequenceTestData {
    public static Object example1() {
        SeqTestClass1 example1 = new SeqTestClass1();
        example1.test1 = "Example1";
        example1.test2 = "SequenceTestData";
        return example1;
    }

    public static Object example2() {
        SeqTestClass1 example2 = new SeqTestClass1();
        example2.test1 = "Example2";
        example2.test2 = "SequenceTestData";
        return example2;
    }

    public static Object example3() {
        SeqTestClass1 example3 = new SeqTestClass1();
        example3.test1 = "Example3";
        example3.test2 = "SequenceTestData";
        return example3;
    }

    public static Object example4() {
        SeqTestClass2 example4 = new SeqTestClass2();
        example4.test1 = "Example4";
        example4.test2 = "SequenceTestData";
        return example4;
    }

    public static Object example5() {
        SeqTestClass2 example5 = new SeqTestClass2();
        example5.test1 = "Example5";
        example5.test2 = "SequenceTestData";
        return example5;
    }

    public static Object example6() {
        SeqTestClass2 example6 = new SeqTestClass2();
        example6.test1 = "Example6";
        example6.test2 = "SequenceTestData";
        return example6;
    }
}
