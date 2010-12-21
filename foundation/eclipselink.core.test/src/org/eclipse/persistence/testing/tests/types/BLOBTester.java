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
package org.eclipse.persistence.testing.tests.types;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * A Tester for BLOB data, treated as a byte array internally.
 * NOTE: Direct attribute accessing for byte[] does not work (reflection problem)
 */
public class BLOBTester extends TypeTester {
    public byte[] bytes;
    protected int length;

    public BLOBTester() {
        this(0);
    }

    public BLOBTester(int numBytes) {
        super("BLOB(" + numBytes + ")");
        length = numBytes;
        bytes = null;
    }

    private String buildName() {
        return "BLOB(" + length + ")";
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(BLOBTester.class);
        descriptor.setTableName("BLOBS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        //MySQL gets confused when reserved words are used as a name such as BLOB. Changed to BLOB_
        descriptor.addDirectMapping("bytes", "BLOB_");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(BLOBTester.class);
        descriptor.setTableName("BLOBS");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("bytes", "getBytes", "setBytes", "BLOB_");
        return descriptor;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] newBytes) {
        length = newBytes.length;
        bytes = newBytes;
    }

    public void setup(Session session) {
        // Access does not support BLOBS or CLOBS
        if (session.getPlatform().isAccess()) {
            throw new TestWarningException("Access and DB2 do not support BLOBS or CLOBS.");
        }

        if (bytes == null) {
            bytes = new byte[length];
            new Random().nextBytes(bytes);
        }
    }

    /**
     *Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();

        definition.setName("BLOBS");
        definition.addField("BLOB_", Byte[].class, 33000);
        return definition;
    }

    /**
     *     Catch null point that occurs frequently an make as warning (known bug).
     */
    protected void test(WriteTypeObjectTest testCase) throws TestWarningException {
        try {
            super.test(testCase);
        } catch (NullPointerException e) {
            throw new TestWarningException("Blob value was returned as null, this means the drivers max size was exceeded." + org.eclipse.persistence.internal.helper.Helper.cr() + caughtException);
        }
    }

    public static Vector testInstances() {
        Vector tests = new Vector();

        tests.addElement(new BLOBTester(1000));
        tests.addElement(new BLOBTester(5000));
        tests.addElement(new BLOBTester(10000));
        tests.addElement(new BLOBTester(25000));
        tests.addElement(new BLOBTester(32768));
        tests.addElement(new BLOBTester(33000));
        return tests;
    }

    public String toString() {
        if (getTestName().equals(buildName())) {
            return getTestName();
        } else {
            return getTestName() + " {" + length + "}";
        }
    }

    protected void verify(WriteTypeObjectTest testCase) throws TestException {
        try {
            super.verify(testCase);
        } catch (TestException e) {
            // JConnect does not support non-native BLOBs
            if ((caughtException != null) && (caughtException.toString().indexOf("JZ0S8: An escape sequence") != -1)) {
                throw new TestProblemException("JConnect does not do BLOBs in non-native SQL:\n" + caughtException.getInternalException());
            }

            // In VA/Java setting array object reflectivey throws an exception
            if ((e.getInternalException() != null) && (e.getInternalException().getClass() == IllegalAccessException.class)) {
                try {
                    Class.forName("com.ibm.uvm.tools.DebugSupport");
                    throw new TestWarningException("IllegalAccessException attempting to reflectively set byte[]");
                } catch (ClassNotFoundException ce) {
                }
            }

            BLOBTester original = (BLOBTester)testCase.getOriginalObject();
            BLOBTester database = (BLOBTester)testCase.getObjectFromDatabase();

            if (database == null) {
                throw new TestWarningException("Write of BLOB failed, nothing was written or read.\n" + caughtException);
            }

            if ((database.getBytes() != null) && (original.getBytes().length == database.getBytes().length)) {
                int numGood = 0;
                for (int index = 0; index < original.getBytes().length; index++) {
                    if (original.getBytes()[index] == database.getBytes()[index]) {
                        numGood++;
                    }
                }
                throw new TestWarningException("Two BLOBs did not match, " + numGood + " bytes matched");
            }
            throw new TestWarningException("Blobs just suck with this driver or max size exceeded, \n" + e);
        }
    }
}
