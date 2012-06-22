/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.ExhaustivePartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.LowerCasePartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparator;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine.StringHolderPair;

public class ExhaustivePartialStringComparatorEngineTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ExhaustivePartialStringComparatorEngineTests.class);
	}
	
	public ExhaustivePartialStringComparatorEngineTests(String name) {
		super(name);
	}

	public void testSameSize() {
		PartialStringComparatorEngine engine = this. buildEngine();
		StringHolderPair[] pairs = engine.match(this.buildStringHoldersSameSize1(), this.buildStringHoldersSameSize2());
		Arrays.sort(pairs);
		this.dump(pairs);
		int i = 0;

		assertEquals("dadoorunrun", pairs[i].getStringHolder1().getString());
		assertEquals("da-doo-run-run", pairs[i++].getStringHolder2().getString());

		assertEquals("bar", pairs[i].getStringHolder1().getString());
		assertEquals("bbaarr", pairs[i++].getStringHolder2().getString());

		assertEquals("foo", pairs[i].getStringHolder1().getString());
		assertEquals("FOOOOO", pairs[i++].getStringHolder2().getString());

		assertEquals("chickenLittle", pairs[i].getStringHolder1().getString());
		assertEquals("MyChickenLittle", pairs[i++].getStringHolder2().getString());

	}

	private StringHolder[] buildStringHoldersSameSize1() {
		return new StringHolder[] {
				new SimpleStringHolder("foo"),
				new SimpleStringHolder("chickenLittle"),
				new SimpleStringHolder("dadoorunrun"),
				new SimpleStringHolder("bar")
		};
	}

	private StringHolder[] buildStringHoldersSameSize2() {
		return new StringHolder[] {
				new SimpleStringHolder("bbaarr"),
				new SimpleStringHolder("da-doo-run-run"),
				new SimpleStringHolder("FOOOOO"),
				new SimpleStringHolder("MyChickenLittle")
		};
	}

	public void testSmaller1() {
		PartialStringComparatorEngine engine = this. buildEngine();
		StringHolderPair[] pairs = engine.match(this.buildStringHoldersSmaller1_1(), this.buildStringHoldersSmaller1_2());
		Arrays.sort(pairs);
		this.dump(pairs);
		int i = 0;

		assertNull(pairs[i].getStringHolder1());
		assertEquals("MyChickenLittle", pairs[i++].getStringHolder2().getString());

		assertEquals("dadoorunrun", pairs[i].getStringHolder1().getString());
		assertEquals("da-doo-run-run", pairs[i++].getStringHolder2().getString());

		assertEquals("bar", pairs[i].getStringHolder1().getString());
		assertEquals("bbaarr", pairs[i++].getStringHolder2().getString());

		assertEquals("foo", pairs[i].getStringHolder1().getString());
		assertEquals("FOOOOO", pairs[i++].getStringHolder2().getString());

	}

	private StringHolder[] buildStringHoldersSmaller1_1() {
		return new StringHolder[] {
				new SimpleStringHolder("foo"),
				new SimpleStringHolder("dadoorunrun"),
				new SimpleStringHolder("bar")
		};
	}

	private StringHolder[] buildStringHoldersSmaller1_2() {
		return new StringHolder[] {
				new SimpleStringHolder("bbaarr"),
				new SimpleStringHolder("da-doo-run-run"),
				new SimpleStringHolder("FOOOOO"),
				new SimpleStringHolder("MyChickenLittle")
		};
	}

	public void testSmaller2() {
		PartialStringComparatorEngine engine = this. buildEngine();
		StringHolderPair[] pairs = engine.match(this.buildStringHoldersSmaller2_1(), this.buildStringHoldersSmaller2_2());
		Arrays.sort(pairs);
		this.dump(pairs);
		int i = 0;

		assertEquals("dadoorunrun", pairs[i].getStringHolder1().getString());
		assertNull(pairs[i++].getStringHolder2());

		assertEquals("foo", pairs[i].getStringHolder1().getString());
		assertEquals("da-doo-run-run", pairs[i++].getStringHolder2().getString());

		assertEquals("bar", pairs[i].getStringHolder1().getString());
		assertEquals("bbaarr", pairs[i++].getStringHolder2().getString());

		assertEquals("chickenLittle", pairs[i].getStringHolder1().getString());
		assertEquals("MyChickenLittle", pairs[i++].getStringHolder2().getString());

	}

	private StringHolder[] buildStringHoldersSmaller2_1() {
		return new StringHolder[] {
				new SimpleStringHolder("foo"),
				new SimpleStringHolder("chickenLittle"),
				new SimpleStringHolder("dadoorunrun"),
				new SimpleStringHolder("bar")
		};
	}

	private StringHolder[] buildStringHoldersSmaller2_2() {
		return new StringHolder[] {
				new SimpleStringHolder("bbaarr"),
				new SimpleStringHolder("da-doo-run-run"),
				new SimpleStringHolder("MyChickenLittle")
		};
	}

	protected PartialStringComparatorEngine buildEngine() {
		return new ExhaustivePartialStringComparatorEngine(this.buildComparator());
	}

	private PartialStringComparator buildComparator() {
		return new LowerCasePartialStringComparator(PartialStringComparator.DEFAULT_COMPARATOR);
	}

	private void dump(StringHolderPair[] stringHolderPairs) {
//		System.out.println();
//		Arrays.sort(stringHolderPairs);
//		for (int i = 0; i < stringHolderPairs.length; i++) {
//			System.out.println(stringHolderPairs[i]);
//		}
	}

}
