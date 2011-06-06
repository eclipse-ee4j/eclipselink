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
package org.eclipse.persistence.tools.workbench.test.framework.ui.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceBundleStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.TriStateBooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.test.framework.resources.TestResourceBundle;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

public class TriStateBooleanCellRendererAdapterTest extends TestCase
{
    private StringRepository simpleRepos;

    public TriStateBooleanCellRendererAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(TriStateBooleanCellRendererAdapterTest.class);
	}

    protected void setUp() throws Exception {
        super.setUp();
        this.simpleRepos = buildStringRepository();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    
	public void testDefault() throws Exception
	{
		TriStateBooleanCellRendererAdapter decorator = 
            new TriStateBooleanCellRendererAdapter(this.simpleRepos) {
                protected String undefinedResourceKey() {
                    return "test1";
                }
                protected String falseResourceKey() {
                    return "test2";
                }
                protected String trueResourceKey() {
                    return "test3";                    
                }
        };

		String text = decorator.buildText(TriStateBoolean.UNDEFINED);
		assertEquals(text, "test1");

		text = decorator.buildText(TriStateBoolean.TRUE);
		assertEquals(text, "test3");

		text = decorator.buildText(TriStateBoolean.FALSE);
		assertEquals(text, "test2");

		text = decorator.buildText(null);
		assertEquals(text, null);

		text = decorator.buildText(decorator);
		assertEquals(text, null);
	}
    
    private StringRepository buildStringRepository() {
        return new ResourceBundleStringRepository(TestResourceBundle.class) {
            public String getString(String key) {
                return key;
            }
        };
    }
}
