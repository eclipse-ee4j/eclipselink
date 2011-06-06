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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.AffixStrippingPartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.PartialStringComparatorEngine;
import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringHolder;

public class PrefixStrippingPartialStringComparatorEngineTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(PrefixStrippingPartialStringComparatorEngineTests.class);
	}
	
	public PrefixStrippingPartialStringComparatorEngineTests(String name) {
		super(name);
	}

	public void testPrefixVariable1() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders1.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders1.addAll(this.buildPrefixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders2.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders2.addAll(this.buildPrefixStringHolders("zzz", 75));

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders1);

				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedPrefixEngine(new LocalEngine(), 0.75f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testPrefixVariable2() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders1.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders1.addAll(this.buildPrefixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders2.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders2.addAll(this.buildPrefixStringHolders("zzz", 40));
		holders2.addAll(this.buildPrefixStringHolders("zzzaaa", 35));

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders1);

				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("aaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedPrefixEngine(new LocalEngine(), 0.50f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testPrefixVariable3() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders1.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders1.addAll(this.buildPrefixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders2.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders2.addAll(this.buildPrefixStringHolders("zzz", 40));
		holders2.addAll(this.buildPrefixStringHolders("zzzaaa", 35));
		holders2.add(new SimpleStringHolder("zzz"));		// add a string that is ALL prefix!

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders1);

				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("yyy", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzzaaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildFixedPrefixEngine(new LocalEngine(), 0.25f);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	public void testPrefixFixed() {
		Collection holders1 = new ArrayList(100);
		holders1.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders1.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders1.addAll(this.buildPrefixStringHolders("zzz", 75));

		Collection holders2 = new ArrayList(100);
		holders2.addAll(this.buildPrefixStringHolders("xxx", 5));
		holders2.addAll(this.buildPrefixStringHolders("yyy", 20));
		holders2.addAll(this.buildPrefixStringHolders("zzz", 40));
		holders2.addAll(this.buildPrefixStringHolders("zzzaaa", 35));
		holders2.add(new SimpleStringHolder("zzz"));		// add a string that is ALL prefix!

		class LocalEngine implements PartialStringComparatorEngine {
			public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("yyy", stringHolders1);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders1);

				PrefixStrippingPartialStringComparatorEngineTests.this.assertSomePrefixes("xxx", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("yyy", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzz", stringHolders2);
				PrefixStrippingPartialStringComparatorEngineTests.this.assertNoPrefixes("zzzaaa", stringHolders2);

				return new StringHolderPair[0];
			}
		}
		PartialStringComparatorEngine engine = this. buildVariablePrefixEngine(new LocalEngine(), 20);
		engine.match((StringHolder[]) holders1.toArray(new StringHolder[holders1.size()]), (StringHolder[]) holders2.toArray(new StringHolder[holders2.size()]));
	}

	private Collection buildPrefixStringHolders(String prefix, int count) {
		Collection result = new ArrayList(count);
		for (int i = 1; i <= count; i++) {
			result.add(new SimpleStringHolder(prefix + i));
		}
		return result;
	}

	void assertNoPrefixes(String prefix, StringHolder[] stringHolders) {
		for (int i = stringHolders.length; i-- > 0;) {
			if (stringHolders[i].getString().startsWith(prefix)) {
				fail("prefix should have been stripped: " + prefix);
			}
		}
	}

	void assertSomePrefixes(String prefix, StringHolder[] stringHolders) {
		for (int i = stringHolders.length; i-- > 0;) {
			if (stringHolders[i].getString().startsWith(prefix)) {
				return;
			}
		}
		fail("prefix should NOT have been stripped: " + prefix);
	}

	private PartialStringComparatorEngine buildVariablePrefixEngine(PartialStringComparatorEngine engine, int threshold) {
		return AffixStrippingPartialStringComparatorEngine.forPrefixStripping(engine, threshold);
	}

	private PartialStringComparatorEngine buildFixedPrefixEngine(PartialStringComparatorEngine engine, float threshold) {
		return AffixStrippingPartialStringComparatorEngine.forPrefixStripping(engine, threshold);
	}

}
