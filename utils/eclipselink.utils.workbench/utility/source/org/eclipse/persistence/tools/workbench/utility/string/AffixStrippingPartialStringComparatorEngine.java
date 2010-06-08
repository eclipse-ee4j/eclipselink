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
package org.eclipse.persistence.tools.workbench.utility.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This partial string comparator engine will strip the prefixes
 * or suffixes off of the sets of strings before passing them to another
 * engine for matching. The number of strings that must contain the
 * same affix before the affix is stripped can be configured by clients
 * on construction of the engine.
 */
public class AffixStrippingPartialStringComparatorEngine
	implements PartialStringComparatorEngine
{
	private final PartialStringComparatorEngine engine;
	private final ThresholdCalculator thresholdCalculator;
	private final StringHolderWrapperFactory wrapperFactory;


	/**
	 * Construct a stripping engine that requires every string to have the same
	 * prefix before the prefix is stripped from all the strings.
	 */
	public static PartialStringComparatorEngine forPrefixStripping(PartialStringComparatorEngine engine) {
		return new AffixStrippingPartialStringComparatorEngine(engine, StringHolderWrapperFactory.PREFIX);
	}

	/**
	 * Construct a stripping engine that requires the specified percentage
	 * of strings to have the same prefix before the prefix is stripped from
	 * the strings.
	 */
	public static PartialStringComparatorEngine forPrefixStripping(PartialStringComparatorEngine engine, float factor) {
		return new AffixStrippingPartialStringComparatorEngine(engine, factor, StringHolderWrapperFactory.PREFIX);
	}

	/**
	 * Construct a stripping engine that requires the specified number
	 * of strings to have the same prefix before the prefix is stripped from
	 * the strings.
	 */
	public static PartialStringComparatorEngine forPrefixStripping(PartialStringComparatorEngine engine, int count) {
		return new AffixStrippingPartialStringComparatorEngine(engine, count, StringHolderWrapperFactory.PREFIX);
	}

	/**
	 * Construct a stripping engine that requires every string to have the same
	 * prefix before the prefix is stripped from all the strings.
	 */
	public static PartialStringComparatorEngine forSuffixStripping(PartialStringComparatorEngine engine) {
		return new AffixStrippingPartialStringComparatorEngine(engine, StringHolderWrapperFactory.SUFFIX);
	}

	/**
	 * Construct a stripping engine that requires the specified percentage
	 * of strings to have the same suffix before the suffix is stripped from
	 * the strings.
	 */
	public static PartialStringComparatorEngine forSuffixStripping(PartialStringComparatorEngine engine, float factor) {
		return new AffixStrippingPartialStringComparatorEngine(engine, factor, StringHolderWrapperFactory.SUFFIX);
	}

	/**
	 * Construct a stripping engine that requires the specified number
	 * of strings to have the same suffix before the suffix is stripped from
	 * the strings.
	 */
	public static PartialStringComparatorEngine forSuffixStripping(PartialStringComparatorEngine engine, int count) {
		return new AffixStrippingPartialStringComparatorEngine(engine, count, StringHolderWrapperFactory.SUFFIX);
	}

	private AffixStrippingPartialStringComparatorEngine(PartialStringComparatorEngine engine, StringHolderWrapperFactory wrapperFactory) {
		this(engine, 1.0f, wrapperFactory);
	}

	private AffixStrippingPartialStringComparatorEngine(PartialStringComparatorEngine engine, float factor, StringHolderWrapperFactory wrapperFactory) {
		super();
		this.engine = engine;
		this.thresholdCalculator = new VariableThresholdCalculator(factor);
		this.wrapperFactory = wrapperFactory;
	}

	private AffixStrippingPartialStringComparatorEngine(PartialStringComparatorEngine engine, int count, StringHolderWrapperFactory wrapperFactory) {
		super();
		this.engine = engine;
		this.thresholdCalculator = new FixedThresholdCalculator(count);
		this.wrapperFactory = wrapperFactory;
	}

	public StringHolderPair[] match(StringHolder[] stringHolders1, StringHolder[] stringHolders2) {
		return this.unwrap(this.engine.match(this.wrap(stringHolders1), this.wrap(stringHolders2)));
	}

	private StringHolder[] wrap(StringHolder[] stringHolders) {
		int len = stringHolders.length;
		StringHolderWrapper[] wrappers = new StringHolderWrapper[len];
		for (int i = len; i-- > 0; ) {
			wrappers[i] = this.wrap(stringHolders[i]);
		}
		if (len > 1) {	// there can't be a common affix with only a single string
			int thresholdCount = this.thresholdCalculator.thresholdCountFor(wrappers);
			if (thresholdCount > 1) {	// there has to be more than a single string with the common affix
				this.stripAffixes(wrappers, thresholdCount);
			}
		}
		return wrappers;
	}

	private StringHolderWrapper wrap(StringHolder stringHolder) {
		return this.wrapperFactory.buildStringHolderWrapper(stringHolder);
	}

	private void stripAffixes(StringHolderWrapper[] wrappers, int thresholdCount) {
		// group the wrappers by their first/last letters
		HashMap charGroups = new HashMap(100);
		for (int i = wrappers.length; i-- > 0; ) {
			StringHolderWrapper wrapper = wrappers[i];
			char c = wrapper.outsideChar();
			if (c == 0) {
				// the char will be 0 if we have exceeded the string's length,
				// so drop it from the entries to be examined
				continue;	// skip to the next wrapper
			}
			Character bigC = new Character(c);
			ArrayList group = (ArrayList) charGroups.get(bigC);
			if (group == null) {
				group = new ArrayList();
				charGroups.put(bigC, group);
			}
			group.add(wrapper);
		}
		// continue processing any groups that exceed the threshold
		for (Iterator stream = charGroups.entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			ArrayList group = (ArrayList) entry.getValue();
			if (group.size() >= thresholdCount) {
				StringHolderWrapper[] groupArray = (StringHolderWrapper[]) group.toArray(new StringHolderWrapper[group.size()]);
				for (int i = groupArray.length; i-- > 0; ) {
					groupArray[i].incrementAffixSize();
				}
				// recurse
				this.stripAffixes(groupArray, thresholdCount);
			}
		}
	}

	private StringHolderPair[] unwrap(StringHolderPair[] stringHolderPairs) {
		StringHolderPair[] result = new StringHolderPair[stringHolderPairs.length];
		for (int i = stringHolderPairs.length; i-- > 0; ) {
			StringHolderPair wrappedPair = stringHolderPairs[i];
			result[i] = new SimpleStringHolderPair(
					this.unwrap(wrappedPair.getStringHolder1()),
					this.unwrap(wrappedPair.getStringHolder2()),
					wrappedPair.getScore()
			);
		}
		return result;
	}

	private StringHolder unwrap(StringHolder stringHolder) {
		return (stringHolder == null) ?
				null
			:
				((StringHolderWrapper) stringHolder).getStringHolder();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		this.wrapperFactory.toString(sb);
		sb.append(" : ");
		this.thresholdCalculator.toString(sb);
		sb.append(')');
		return sb.toString();
	}

	// ********** member classes **********

	/**
	 * This interface allows us to use a pluggable threshold calculation
	 * (e.g. percentage-based, fixed count-based).
	 */
	private interface ThresholdCalculator {

		/**
		 * Calculate the threshold count for the specified set of string holders.
		 */
		int thresholdCountFor(StringHolder[] stringHolders);

		/**
		 * Append something useful to the specified string buffer.
		 */
		void toString(StringBuffer sb);

	}


	private class VariableThresholdCalculator implements ThresholdCalculator {
		private float factor;

		VariableThresholdCalculator(float factor) {
			super();
			if ((factor <= 0.0f) || (factor > 1.0f)) {
				throw new IllegalArgumentException("valid range: 0.0 < factor <= 1.0");
			}
			this.factor = factor;
		}

		public int thresholdCountFor(StringHolder[] stringHolders) {
			return (int) (stringHolders.length * this.factor);
		}

		public void toString(StringBuffer sb) {
			sb.append(this.factor);
		}

	}


	private class FixedThresholdCalculator implements ThresholdCalculator {
		private int count;

		FixedThresholdCalculator(int count) {
			super();
			if (count <= 1) {
				throw new IllegalArgumentException("valid range: count > 1");
			}
			this.count = count;
		}

		public int thresholdCountFor(StringHolder[] stringHolders) {
			return this.count;
		}

		public void toString(StringBuffer sb) {
			sb.append(this.count);
		}

	}


	private interface StringHolderWrapperFactory {

		StringHolderWrapper buildStringHolderWrapper(StringHolder stringHolder);
		void toString(StringBuffer sb);

		StringHolderWrapperFactory PREFIX = new StringHolderWrapperFactory() {
			public StringHolderWrapper buildStringHolderWrapper(StringHolder stringHolder) {
				return StringHolderWrapper.forPrefixStripping(stringHolder);
			}
			public void toString(StringBuffer sb) {
				sb.append("PREFIX");
			}
		};

		StringHolderWrapperFactory SUFFIX = new StringHolderWrapperFactory() {
			public StringHolderWrapper buildStringHolderWrapper(StringHolder stringHolder) {
				return StringHolderWrapper.forSuffixStripping(stringHolder);
			}
			public void toString(StringBuffer sb) {
				sb.append("SUFFIX");
			}
		};
	}


	private static class StringHolderWrapper implements StringHolder {
		private final AffixPolicy affixPolicy;
		private final StringHolder stringHolder;
		private int stringHolderStringLength;
		private int affixLength;
		private String string;		// this is lazy-initialized by the nested engine

		static StringHolderWrapper forPrefixStripping(StringHolder stringHolder) {
			return new StringHolderWrapper(stringHolder, AffixPolicy.PREFIX);
		}

		static StringHolderWrapper forSuffixStripping(StringHolder stringHolder) {
			return new StringHolderWrapper(stringHolder, AffixPolicy.SUFFIX);
		}

		private StringHolderWrapper(StringHolder stringHolder, AffixPolicy affixPolicy) {
			super();
			this.stringHolder = stringHolder;
			this.stringHolderStringLength = stringHolder.getString().length();
			this.affixPolicy = affixPolicy;
			this.affixLength = 0;
			this.string = null;
		}

		public String getString() {
			if (this.string == null) {
				this.string = this.buildString();
			}
			return this.string;
		}

		private String buildString() {
			return this.affixPolicy.buildString(this.stringHolder.getString(), this.affixLength);
		}

		StringHolder getStringHolder() {
			return this.stringHolder;
		}

		char outsideChar() {
			// return the NULL character if we are past the end of the string
			return (this.affixLength == this.stringHolderStringLength) ? 0 : this.affixPolicy.outsideChar(this.stringHolder.getString(), this.affixLength);
		}

		void incrementAffixSize() {
			// don't bump the prefix length past the end of the string
			if (this.affixLength < this.stringHolderStringLength) {
				this.affixLength++;
			}
		}

		public String toString() {
			return StringTools.buildToStringFor(this, this.buildString());
		}

		private interface AffixPolicy {

			String buildString(String string, int affixLength);

			char outsideChar(String string, int affixLength);

			AffixPolicy PREFIX = new AffixPolicy() {
				public String buildString(String string, int affixLength) {
					return string.substring(affixLength);
				}
				public char outsideChar(String string, int affixLength) {
					return string.charAt(affixLength);
				}
			};

			AffixPolicy SUFFIX = new AffixPolicy() {
				public String buildString(String string, int affixLength) {
					return string.substring(0, string.length() - affixLength);
				}
				public char outsideChar(String string, int affixLength) {
					return string.charAt(string.length() - affixLength - 1);
				}
			};

		}

	}

}
