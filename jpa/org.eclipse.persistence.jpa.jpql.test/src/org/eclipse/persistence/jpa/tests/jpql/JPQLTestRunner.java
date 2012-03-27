/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.model.AbstractActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.BaseJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.tests.jpql.model.IJPQLQueryBuilderTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.model.IJPQLQueryFormatterTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * This JUnit runner is the sole runner of Hermes unit-tests because it modifies the default
 * behavior by adding the following support:
 * <ul>
 * <li>Adds the ability to inject objects that are instantiated by a test suite into the unit-test
 * before they are run.
 * <li>Because one unit-test can be run more than once with different state of any given object,
 * this cause issues in Eclipse because the IDE uses {@link Object#toString()} and cannot
 * discriminate between those identical tests. This runner changes the test's description by adding
 * enough information in order to make each test's description unique.
 * </ul>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({ "nls", "restriction" })
public class JPQLTestRunner extends ParentRunner<Runner> {

	/**
	 * The cached {@link Description} so it's not recreated every time.
	 */
	private Description description;

	/**
	 * This contains the helpers that will be injected into each test.
	 */
	private DescriptionHelper descriptionHelper;

	/**
	 * The display string of this test suite.
	 */
	private String name;

	/**
	 * The {@link Runner runners} for the test classes defined in {@link org.junit.runners.Suite.SuiteClasses
	 * &#64;SuiteClasses}.
	 */
	private List<Runner> runners;

	/**
	 * The parent {@link SuiteHelper} or <code>null</code> if none was defined yet.
	 */
	private SuiteHelper suiteHelper;

	/**
	 * The list of registered helpers that inject values from the test suite into the unit-tests
	 * before they are running.
	 */
	private static final Map<Class<? extends Annotation>, DescriptionBuilder> testRunnerHelpers;

	/**
	 * Registers the supported test runner helpers.
	 */
	static {

		testRunnerHelpers = new HashMap<Class<? extends Annotation>, DescriptionBuilder>();

		testRunnerHelpers.put(IJPQLQueryBuilderTestHelper.class,   buildJPQLQueryBuilderTestHelperDescriptionBuilder());
		testRunnerHelpers.put(IJPQLQueryFormatterTestHelper.class, buildJPQLQueryFormatterTestHelperDescriptionBuilder());
		testRunnerHelpers.put(JPQLGrammarTestHelper.class,         buildJPQLGrammarTestHelperDescriptionBuilder());
		testRunnerHelpers.put(JPQLQueryHelperTestHelper.class,     buildJPQLQueryHelperTestHelperDescriptionBuilder());
		testRunnerHelpers.put(JPQLQueryTestHelperTestHelper.class, buildJPQLQueryTestHelperTestHelperDescriptionBuilder());
	}

	/**
	 * Creates a new <code>JPQLTestRunner</code>.
	 *
	 * @param testClass The class that is either a test suite or a unit-tests
	 * @throws InitializationError If the given test class is malformed
	 */
	public JPQLTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	/**
	 * Creates a new <code>JPQLTestRunner</code>.
	 *
	 * @param testClass The class that is either a test suite or a unit-tests
	 * @param suiteHelper The parent {@link SuiteHelper} or {@link null} if none was defined yet
	 * @throws InitializationError If the given test class is malformed
	 */
	public JPQLTestRunner(Class<?> testClass, SuiteHelper suiteHelper) throws InitializationError {
		this(testClass);
		this.suiteHelper = suiteHelper;
	}

	private static DescriptionBuilder buildJPQLGrammarTestHelperDescriptionBuilder() {
		return new DescriptionBuilder() {
			public String toString(Object object) {
				return object.toString();
			}
		};
	}

	private static DescriptionBuilder buildJPQLQueryBuilderTestHelperDescriptionBuilder() {
		return new DescriptionBuilder() {
			public String toString(Object object) {
				IJPQLQueryBuilder builder = (IJPQLQueryBuilder) object;
				StringBuilder sb = new StringBuilder();
				sb.append(builder.getClass().getSimpleName());
				sb.append("[");
				sb.append(builder.getGrammar().toString());
				sb.append("]");
				return sb.toString();
			}
		};
	}

	private static DescriptionBuilder buildJPQLQueryFormatterTestHelperDescriptionBuilder() {
		return new DescriptionBuilder() {
			public String toString(Object object) {
				BaseJPQLQueryFormatter formatter = (BaseJPQLQueryFormatter) object;
				StringBuilder sb = new StringBuilder();
				sb.append(formatter.getClass().getSimpleName());
				sb.append("[");
				sb.append(formatter.getIdentifierStyle().name());
				if (object instanceof AbstractActualJPQLQueryFormatter) {
					AbstractActualJPQLQueryFormatter actualFormatter = (AbstractActualJPQLQueryFormatter) object;
					sb.append("|");
					sb.append(actualFormatter.isUsingExactMatch());
				}
				sb.append("]");
				return sb.toString();
			}
		};
	}

	private static DescriptionBuilder buildJPQLQueryHelperTestHelperDescriptionBuilder() {
		return new DescriptionBuilder() {
			public String toString(Object object) {
				return object.getClass().getSimpleName();
			}
		};
	}

	private static DescriptionBuilder buildJPQLQueryTestHelperTestHelperDescriptionBuilder() {
		return new DescriptionBuilder() {
			public String toString(Object object) {
				return object.getClass().getSimpleName();
			}
		};
	}

	private List<Runner> buildChildren() {

		Class<?> testClass = getTestClass().getJavaClass();
		SuiteClasses suiteClasses = testClass.getAnnotation(SuiteClasses.class);

		if (suiteClasses == null) {
			return Collections.emptyList();
		}

		List<Runner> runners = new ArrayList<Runner>();

		for (Class<?> test : suiteClasses.value()) {
			if (descriptionHelper.helpers.isEmpty()) {
				Runner runner = buildRunner(test, suiteHelper);
				runners.add(runner);
			}
			else {
				for (SuiteHelper suiteHelper : buildSuiteHelpers()) {
					Runner runner = buildRunner(test, suiteHelper);
					runners.add(runner);
				}
			}
		}

		Collections.sort(runners, buildRunnerComparator());
		return runners;
	}

	private String buildDisplayString() {

		String displayString = super.getName();

		if (suiteHelper != null) {
			StringBuilder writer = new StringBuilder();
			writer.append(displayString);
			suiteHelper.addAdditionalInfo(writer);
			displayString = writer.toString();
		}

		return displayString;
	}

	private Runner buildRunner(Class<?> testClass, SuiteHelper suiteHelper) {

		try {

			// Create a runner for multiple unit-tests
			if (testClass.isAnnotationPresent(SuiteClasses.class)) {
				return new JPQLTestRunner(testClass, suiteHelper);
			}

			// Create a runner for a single unit-test
			if (JPQLBasicTest.class.isAssignableFrom(testClass)) {
				return new JPQLBasicTestRunner(testClass, suiteHelper);
			}

			// Create the default runner
			return new AllDefaultPossibilitiesBuilder(true).runnerForClass(testClass);
		}
		catch (Throwable e) {
			return new ErrorReportingRunner(testClass, e);
		}
	}

	private Comparator<Runner> buildRunnerComparator() {
		return new Comparator<Runner>() {
			public int compare(Runner runner1, Runner runner2) {
				String displayName1 = runner1.getDescription().getDisplayName();
				String displayName2 = runner1.getDescription().getDisplayName();
				return displayName1.compareTo(displayName2);
			}
		};
	}

	private List<SuiteHelper> buildSuiteHelpers() {

		List<SuiteHelper> suiteHelpers = new ArrayList<SuiteHelper>();
		Map<Class<? extends Annotation>, Object> singleHelpers = new HashMap<Class<? extends Annotation>, Object>();
		Collection<Class<? extends Annotation>> multipleHelpers = retrieveMultipleHelpers();

		for (Map.Entry<Class<? extends Annotation>, Object[]> helper : descriptionHelper.helpers.entrySet()) {
			if (!multipleHelpers.contains(helper.getKey())) {
				singleHelpers.put(helper.getKey(), helper.getValue()[0]);
			}
		}

		if (multipleHelpers.size() > 1) {
			for (Class<? extends Annotation> firstHelperKey : multipleHelpers) {
				for (Class<? extends Annotation> secondHelperKey : multipleHelpers) {
					if (firstHelperKey != secondHelperKey) {
						for (Object firstHelper : descriptionHelper.helpers.get(firstHelperKey)) {
							for (Object secondHelper : descriptionHelper.helpers.get(secondHelperKey)) {
								Map<Class<? extends Annotation>, Object> copy = new HashMap<Class<? extends Annotation>, Object>();
								copy.putAll(singleHelpers);
								copy.put(firstHelperKey,  firstHelper);
								copy.put(secondHelperKey, secondHelper);

								List<Class<? extends Annotation>> keys = new ArrayList<Class<? extends Annotation>>();
								keys.add(firstHelperKey);
								keys.add(secondHelperKey);

								suiteHelpers.add(new SuiteHelper(suiteHelper, copy, keys));
							}
						}
					}
				}
			}
		}
		else if (multipleHelpers.size() == 1) {
			for (Class<? extends Annotation> firstHelperKey : multipleHelpers) {
				for (Object firstHelper : descriptionHelper.helpers.get(firstHelperKey)) {
					Map<Class<? extends Annotation>, Object> copy = new HashMap<Class<? extends Annotation>, Object>();
					copy.putAll(singleHelpers);
					copy.put(firstHelperKey, firstHelper);

					List<Class<? extends Annotation>> keys = new ArrayList<Class<? extends Annotation>>();
					keys.add(firstHelperKey);

					suiteHelpers.add(new SuiteHelper(suiteHelper, copy, keys));
				}
			}
		}
		else {
			suiteHelpers.add(new SuiteHelper(suiteHelper, singleHelpers));
		}

		return suiteHelpers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		initializeDescriptionHelper(errors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Runner> getChildren() {
		// Cache the Description since JUnit always recreate it, this will increase performance
		if (runners == null) {
			runners = buildChildren();
		}
		return runners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Description getDescription() {
		// Cache the Description since JUnit always recreate it, this will increase performance
		if (description == null) {
			description = super.getDescription();
		}
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getName() {
		// Cache the Description since JUnit always recreate it, this will increase performance and
		// also, add the extra information otherwise Eclipse will not be able to update the status of
		// the tests, it uses the display string to retrieve the node from the JUnit view, if two
		// nodes have the same display string, then only the last one is updated
		if (name == null) {
			name = buildDisplayString();
		}
		return name;
	}

	private void initializeDescriptionHelper(List<Throwable> errors) {

		descriptionHelper = new DescriptionHelper();
		Class<?> unitTest = getTestClass().getJavaClass();

		for (Method method : unitTest.getDeclaredMethods()) {

			if (isHelperMethod(method)) {

				for (Class<? extends Annotation> annotation : testRunnerHelpers.keySet()) {

					if (method.isAnnotationPresent(annotation)) {

						try {
							method.setAccessible(true);
							Object value = method.invoke(null);

							descriptionHelper.helpers.put(
								annotation,
								value.getClass().isArray() ? (Object[]) value : new Object[] { value }
							);
						}
						catch (Exception e) {
							errors.add(e);
						}
					}
				}
			}
		}
	}

	private boolean isHelperMethod(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	private Collection<Class<? extends Annotation>> retrieveMultipleHelpers() {

		Collection<Class<? extends Annotation>> keys = new ArrayList<Class<? extends Annotation>>();

		for (Map.Entry<Class<? extends Annotation>, Object[]> helper : descriptionHelper.helpers.entrySet()) {
			if (helper.getValue().length > 1) {
				keys.add(helper.getKey());
			}
		}

		return keys;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runChild(Runner child, RunNotifier notifier) {
		child.run(notifier);
	}

	/**
	 * This interface is used to create the description of a unit-tests.
	 */
	private static interface DescriptionBuilder {

		/**
		 * Creates a string representation of the given object.
		 *
		 * @param object The object to convert into a human readable string
		 * @return A unique description for the given object
		 */
		String toString(Object object);
	}

	private static class DescriptionHelper {

		private Map<Class<? extends Annotation>, Object[]> helpers;

		DescriptionHelper() {
			super();
			helpers = new HashMap<Class<? extends Annotation>, Object[]>();
		}
	}

	private static class JPQLBasicTestRunner extends BlockJUnit4ClassRunner {

		private Description description;
		private SuiteHelper suiteHelper;
		private JPQLBasicTest test;

		JPQLBasicTestRunner(Class<?> testClass, SuiteHelper suiteHelper) throws InitializationError {
			super(testClass);
			this.suiteHelper = suiteHelper;
		}

		private Description buildDescription() {

			Description description = Description.createSuiteDescription(
				buildDisplayString(),
				getTestClass().getAnnotations()
			);

			Description superDescription = super.getDescription();
			description.getChildren().addAll(superDescription.getChildren());

			return description;
		}

		@Override
		protected List<FrameworkMethod> getChildren() {
			List<FrameworkMethod> methods = super.getChildren();
			Collections.sort(methods, buildMethodComparator());
			return methods;
		}

		private Comparator<FrameworkMethod> buildMethodComparator() {
			return new Comparator<FrameworkMethod>() {
				public int compare(FrameworkMethod method1, FrameworkMethod method2) {
					return method1.getName().compareTo(method2.getName());
				}
			};
		}

		private String buildDisplayString() {
			StringBuilder writer = new StringBuilder();
			writer.append(getName());
			suiteHelper.addAdditionalInfo(writer);
			return writer.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Statement classBlock(RunNotifier notifier) {
			Statement statement = new CreateTestStatement();
			statement = new SetUpClassStatement(statement);
			statement = new CompositeStatement(statement, childrenInvoker(notifier));
			statement = new TearDownClassStatement(statement);
			return statement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object createTest() throws Exception {
			return test;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Description getDescription() {
			if (description == null) {
				description = buildDescription();
			}
			return description;
		}

		private void instantiateTest() throws Throwable {

			Constructor<?> constructor = getTestClass().getJavaClass().getConstructor();
			constructor.setAccessible(true);
			test = (JPQLBasicTest) constructor.newInstance();

			// Inject the SuiteHelper' values into the test
			suiteHelper.injectValues(test);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Statement methodBlock(FrameworkMethod method) {
			Statement statement = new SetUpStatement();
			statement = new CompositeStatement(statement, super.methodBlock(method));
			statement = new TearDownStatement(statement);
			return statement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Statement methodInvoker(FrameworkMethod method, Object test) {
			this.test = (JPQLBasicTest) test;
			return super.methodInvoker(method, test);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String testName(FrameworkMethod method) {
			StringBuilder writer = new StringBuilder();
			writer.append(method.getName());
			suiteHelper.addAdditionalInfo(writer);
			return writer.toString();
		}

		private class CompositeStatement extends Statement {

			private Statement statement1;
			private Statement statement2;

			CompositeStatement(Statement statement1, Statement statement2) {
				super();
				this.statement1 = statement1;
				this.statement2 = statement2;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void evaluate() throws Throwable {
				statement1.evaluate();
				statement2.evaluate();
			}
		}

		/**
		 * This {@link Statement} evaluates the wrapped {@link Statement} and then invoke {@link
		 * JPQLBasicTest#setUpClass()}.
		 */
		private class CreateTestStatement extends Statement {
			@Override
			public void evaluate() throws Throwable {
				instantiateTest();
			}
		}

		/**
		 * This {@link Statement} evaluates the wrapped {@link Statement} and then invoke {@link
		 * JPQLBasicTest#setUpClass()}.
		 */
		private class SetUpClassStatement extends Statement {

			private Statement statement;

			SetUpClassStatement(Statement statement) {
				super();
				this.statement = statement;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void evaluate() throws Throwable {
				statement.evaluate();
				test.setUpClass();
			}
		}

		/**
		 * This {@link Statement} evaluates the wrapped {@link Statement} and then invoke {@link
		 * JPQLBasicTest#setUp()}.
		 */
		private class SetUpStatement extends Statement {
			@Override
			public void evaluate() throws Throwable {
				test.setUp();
			}
		}

		/**
		 * This {@link Statement} evaluates the wrapped {@link Statement} and then invoke {@link
		 * JPQLBasicTest#tearDownClass()}.
		 */
		private class TearDownClassStatement extends Statement {

			private Statement statement;

			TearDownClassStatement(Statement statement) {
				super();
				this.statement = statement;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void evaluate() throws Throwable {
				try {
					statement.evaluate();
				}
				finally {
					test.tearDownClass();
				}
			}
		}

		/**
		 * This {@link Statement} evaluates the wrapped {@link Statement} and then invoke {@link
		 * JPQLBasicTest#tearDown()}.
		 */
		private class TearDownStatement extends Statement {

			private Statement statement;

			TearDownStatement(Statement statement) {
				super();
				this.statement = statement;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void evaluate() throws Throwable {
				try {
					statement.evaluate();
				}
				finally {
					test.tearDown();
				}
			}
		}
	}

	private static class SuiteHelper {

		private Map<Class<? extends Annotation>, Object> helpers;
		private SuiteHelper parent;
		private List<Class<? extends Annotation>> primaryKeys;

		SuiteHelper(SuiteHelper parent,
		            Map<Class<? extends Annotation>, Object> helpers) {

			this(parent, helpers, Collections.<Class<? extends Annotation>>emptyList());
		}

		SuiteHelper(SuiteHelper parent,
		            Map<Class<? extends Annotation>, Object> helpers,
		            List<Class<? extends Annotation>> primaryKeys) {

			super();
			this.parent      = parent;
			this.helpers     = helpers;
			this.primaryKeys = primaryKeys;
		}

		void addAdditionalInfo(StringBuilder writer) {

			for (Class<? extends Annotation> primaryKey : primaryKeys) {
				writer.append(" - ");
				Object helper = helpers.get(primaryKey);
				DescriptionBuilder descriptionBuilder = testRunnerHelpers.get(primaryKey);
				writer.append(descriptionBuilder.toString(helper));
			}

			if (parent != null) {
				parent.addAdditionalInfo(writer);
			}
		}

		private void injectValues(Class<?> testClass, JPQLBasicTest test) throws Exception {

			if (testClass == Object.class) {
				return;
			}

			Field[] fields = testClass.getDeclaredFields();

			for (Field field : fields) {
				for (Map.Entry<Class<? extends Annotation>, Object> helper : helpers.entrySet()) {
					if (field.isAnnotationPresent(helper.getKey())) {
						field.setAccessible(true);
						field.set(test, helper.getValue());
					}
				}
			}

			injectValues(testClass.getSuperclass(), test);
		}

		void injectValues(JPQLBasicTest test) throws Exception {

			injectValues(test.getClass(), test);

			if (parent != null) {
				parent.injectValues(test);
			}
		}
	}
}