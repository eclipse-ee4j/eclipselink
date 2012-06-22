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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui;

// JDK
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
public abstract class AbstractPanelTest extends TestCase
{

	/**
	 * This component is used to grab the focus between test for transfering the
	 * focus on the desired component.
	 */
	private JComponent focusGrabber;

	/**
	 * The pane to be tested.
	 */
	private JComponent pane;

	/**
	 * The parent of this <code>AbstractPanelTest</code> if this one is ran
	 * inside of another one.
	 */
	final AbstractPanelTest parentTest;

	/**
	 * This <code>Robot</code> is used to simulate a ALT-[mnemonic] in order to
	 * transfer the focus on a component.
	 */
	private Robot robot;

	/**
	 * Determines if this test is run as a standalone application.
	 */
	private boolean standalone;

	/**
	 * The main window used for testing.
	 */
	protected JFrame window;

	/**
	 * The identifier for components that are an instance of <code>JCheckBox</code>.
	 */
	public static final int COMPONENT_CHECK_BOX = 1;

	/**
	 * The identifier for components that are an instance of <code>JComboBox</code>.
	 */
	public static final int COMPONENT_COMBO_BOX = 2;

	/**
	 * The identifier for components that are an instance of <code>JList</code>.
	 */
	public static final int COMPONENT_LIST = 3;

	/**
	 * The identifier for components that are an instance of <code>JMenu</code>.
	 */
	public static final int COMPONENT_MENU = 4;

	/**
	 * The identifier for components that are an instance of <code>JRadioButton</code>.
	 */
	public static final int COMPONENT_RADIO_BUTTON = 5;

	/**
	 * The identifier for components that are an instance of <code>JSpinner</code>.
	 */
	public static final int COMPONENT_SPINNER = 6;

	/**
	 * The identifier for components that are an instance of <code>JTable</code>.
	 */
	public static final int COMPONENT_TABLE = 7;

	/**
	 * The identifier for components that are an instance of <code>JTextArea</code>.
	 */
	public static final int COMPONENT_TEXT_AREA = 8;

	/**
	 * The identifier for components that are an instance of <code>JTextField</code>.
	 */
	public static final int COMPONENT_TEXT_COMPONENT = 9;

	/**
	 * The identifier for components that are an instance of <code>JTextField</code>.
	 */
	public static final int COMPONENT_TEXT_FIELD = 10;

	/**
	 * The identifier for components that are an instance of <code>JTree</code>.
	 */
	public static final int COMPONENT_TREE = 11;

	/**
	 * A constant corresponding to not use any mask over a keyboard key.
	 */
	public static final int NO_MASK = -1;

	/**
	 * The IP address of Pascal's computer.
	 */
	static final String PASCAL_COMPUTER_IP_ADDRESS = "138.2.91.79";

	/**
	 * The prefix of the methods that test entering values into fields, which is
	 * "_testComponentEnabler".
	 */
	protected static final String TEST_COMPONENT_ENABLER_SIGNATURE = "_testComponentEnabler";

	/**
	 * The prefix of the methods that test entering values into fields, which is
	 * "_testComponentEntry".
	 */
	protected static final String TEST_COMPONENT_ENTRY_SIGNATURE = "_testComponentEntry";

	/**
	 * The prefix of the methods that test entering values into fields, which is
	 * "_testExtra".
	 */
	protected static final String TEST_EXTRA_SIGNATURE = "_testExtra";

	/**
	 * The prefix of the methods that test the transfer of the focus, which is
	 * "_testFocusTransfer".
	 */
	protected static final String TEST_FOCUS_TRANSFER_SIGNATURE = "_testFocusTransfer";

	/**
	 * The prefix of the methods that is requesting another
	 * <code>AbstractPanelTest</code> to be run within this one, which is
	 * "_testSubPane".
	 */
	protected static final String TEST_SUB_PANE_SIGNATURE = "_testSubPane";

	/**
	 * The IP address of Tran's computer.
	 */
	static final String TRAN_COMPUTER_IP_ADDRESS = "138.2.91.83";

	/**
	 * Allows to change the Look and Feel once before starting the test.
	 */
	static
	{
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		// Make sure the locale is French so on French-Canadian keyboard, the
		// key mapping is correctly done
		try
		{
			InetAddress inetAddress = InetAddress.getLocalHost();

			// Pascal's work computer
			if (PASCAL_COMPUTER_IP_ADDRESS.equals(inetAddress.getHostAddress()))
				Locale.setDefault(Locale.CANADA_FRENCH);

			// Pascal's home computer
			else if ("EnzoMatrix".equals(inetAddress.getHostName()))
				Locale.setDefault(Locale.CANADA_FRENCH);
		}
		catch (UnknownHostException e)
		{
			// Ignore
		}

		// Set the look and feel
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//			UIManager.setLookAndFeel("oracle.bali.ewt.olaf.OracleLookAndFeel");
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new <code>AbstractPanelTest</code>.
	 *
	 * @param parentTest The parent where this <code>TestCase</code> will be ran from
	 */
	protected AbstractPanelTest(AbstractPanelTest parentTest)
	{
		this("SubTest", parentTest);
	}

	/**
	 * Creates a new <code>AbstractPanelTest</code>.
	 *
	 * @param name The name of this class
	 */
	protected AbstractPanelTest(String name)
	{
		this(name, null);
	}

	/**
	 * Creates a new <code>AbstractPanelTest</code>.
	 *
	 * @param name The name of this class
	 * @param parentTest The parent where this <code>TestCase</code> will be ran from
	 */
	private AbstractPanelTest(String name, AbstractPanelTest parentTest)
	{
		super(name);
		this.parentTest = parentTest;
	}

	/**
	 * Tests the UI by invoking a set of methods.
	 *
	 * @throws Throwable
	 */
	final void _subTestUI() throws Throwable
	{
		_testFocusTransferByMnemonic();
		_testComponentEntry();
		_testComponentEnabler();
		_testExtra();
		_testSubTest();
	}

	/**
	 * Retrieves all the methods from the JUnit test class, which is a subclass
	 * of this class that begin with {@link #TEST_COMPONENT_ENABLER_SIGNATURE}
	 * and invoke them.
	 * 
	 * @throws Throwable If any problems occurred during the execution of a method
	 */
	private void _testComponentEnabler() throws Throwable
	{
		runMethods(retrieveMethods(TEST_COMPONENT_ENABLER_SIGNATURE));
	}

	/**
	 * Retrieves all the methods from the JUnit test class, which is a subclass
	 * of this class that begin with {@link #TEST_COMPONENT_ENTRY_SIGNATURE}and
	 * invoke them.
	 * 
	 * @throws Throwable If any problems occurred during the execution of a method
	 */
	private void _testComponentEntry() throws Throwable
	{
		runMethods(retrieveMethods(TEST_COMPONENT_ENTRY_SIGNATURE));
	}

	/**
	 * Tests
	 *
	 * @param component
	 * @param componentType
	 */
	private void _testComponentType(Component component, int componentType)
	{
		switch (componentType)
		{
			case COMPONENT_CHECK_BOX:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JCheckBox);
				break;
			}

			case COMPONENT_COMBO_BOX:
			{
				component = retrieveComboBox(component);

				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JComboBox);
				break;
			}

			case COMPONENT_LIST:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JList);
				break;
			}

			case COMPONENT_MENU:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JMenu);
				break;
			}

			case COMPONENT_SPINNER:
			{
				component = retrieveSpinner(component);

				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JSpinner);
				break;
			}

			case COMPONENT_RADIO_BUTTON:
			{
				assertTrue("The wrong component received the focus: ",
							  component instanceof JRadioButton);
				break;
			}

			case COMPONENT_TABLE:
			{
				assertTrue("The wrong component received the focus: ",
							  component instanceof JTable);
				break;
			}

			case COMPONENT_TEXT_COMPONENT:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JTextComponent);
				break;
			}

			case COMPONENT_TEXT_AREA:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JTextArea);
				break;
			}

			case COMPONENT_TEXT_FIELD:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JTextField);
				break;
			}

			case COMPONENT_TREE:
			{
				assertTrue("The wrong component received the focus: " + component,
							  component instanceof JTree);
				break;
			}

			default:
			{
				assertTrue("The component is of an unknown type: " + component, false);
			}
		}
	}

	/**
	 * Tests to make sure a pane does not have duplicate mnemonic. If sub-pane
	 * can be changed based on certain values, then new sub-pane can be tested
	 * by overriding {@link #canContinueTestingDuplicateMnemonic()} and perform
	 * one change at a time and once all the possible changes have been tested,
	 * <code>false</code> will stop the testing.
	 */
	private void _testDuplicateMnemonicImp() throws Throwable
	{
		if (this.pane == null)
			this.pane = buildPaneImp();

		// Prepare the mnemonic list and add the reserved mnemonic found in the
		// menu bar: File | Workbench | Selected | Tools | Window | Help
		Hashtable mnemonics = new Hashtable();
		registerMnemonic(mnemonics, "FILE_MENU");
//		registerMnemonic(mnemonics, "WORKBENCH_MENU");
		registerMnemonic(mnemonics, "SELECTED_MENU");
		registerMnemonic(mnemonics, "TOOLS_MENU");
		registerMnemonic(mnemonics, "WINDOW_MENU");
		registerMnemonic(mnemonics, "HELP_MENU");

		registerMnemonic(mnemonics, "NAVIGATOR_LABEL");
		registerMnemonic(mnemonics, "EDITOR_LABEL");

		Hashtable copy = (Hashtable) mnemonics.clone();

		// First test with the way the pane first appear on screen
		_testDuplicateMnemonicImp(this.pane, copy);

		// Dump the duplicate mnemonic into the console
		dumpDuplicateMnemonicResults(copy);

		// Then ask the subclass to complete the testing by changing any values
		// in order for PanelSwitcherAdapter to change their content
		while (canContinueTestingDuplicateMnemonic())
		{
			// Continue with the original mnemonic dictionary
			copy = (Hashtable) mnemonics.clone();

			// Retest because the content of the pane changed
			_testDuplicateMnemonicImp(this.pane, copy);

			// Dump the duplicate mnemonic into the console
			dumpDuplicateMnemonicResults(copy);
		}
	}

	/**
	 * Tests to make sure a pane does not have duplicate mnemonic.
	 *
	 * @param container
	 * @param mnemonics
	 */
	private void _testDuplicateMnemonicImp(Container container, Map mnemonics)
	{
		Component[] children = container.getComponents();

		for (int index = 0; index < children.length; index++)
		{
			Component component = children[index];

			// Retrieve the viewport's view
			if (component instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane) component;
				component = scrollPane.getViewport().getView();

				// Recurse to be safe
				_testDuplicateMnemonicImp((Container) component, mnemonics);
			}
			// Recurse through panel
			else if (component instanceof JPanel)
			{
				_testDuplicateMnemonicImp((Container) component, mnemonics);
			}
			// The mnemonic is not accessible, it is the responsibility of the
			// JUnit test to active the component during this test to make sure it
			// is safe to use the mnemonic since something the same mnemonic can be
			// shared by more than one component but some are disabled and only one
			// is enabled
			else if (component.isEnabled() &&
						component.isVisible())
			{
				// JLabel, get the text and mnemonic
				if (component instanceof JLabel)
				{
					JLabel label = (JLabel) component;
					storeMnemonic(mnemonics, label.getText(), label.getDisplayedMnemonic());
				}
				// JButton, get the text and mnemonic
				else if (component instanceof AbstractButton)
				{
					AbstractButton button = (AbstractButton) component;
					storeMnemonic(mnemonics, button.getText(), button.getMnemonic());
				}
			}
		}
	}

	/**
	 * Retrieves all the methods from the JUnit test class, which is a subclass
	 * of this class that begin with {@link #TEST_EXTRA_SIGNATURE} and invoke
	 * them.
	 * 
	 * @throws Throwable If any problems occurred during the execution of a method
	 */
	private void _testExtra() throws Throwable
	{
		runMethods(retrieveMethods(TEST_EXTRA_SIGNATURE));
	}

	/**
	 * Tests to see if the mnemonics of a pane are properly transfered.
	 *
	 * @throws Throwable
	 */
	private void _testFocusTransferByMnemonic() throws Throwable
	{
		runMethods(retrieveMethods(TEST_FOCUS_TRANSFER_SIGNATURE));
	}

	/**
	 * Tests
	 *
	 * @param component
	 * @param componentType
	 * @param labelText
	 */
	private void _testLabeledByText(Component component,
											  int componentType,
											  String key)
	{
		switch (componentType)
		{
			case COMPONENT_SPINNER:
			{
				component = retrieveSpinner(component);
				assertEquals(component.getName(), key);
				break;
			}

			case COMPONENT_COMBO_BOX:
			{
				component = retrieveComboBox(component);
				assertEquals(component.getName(), key);
				break;
			}

			default:
			{
				assertEquals(component.getName(), key);
				break;
			}
		}
	}

	/**
	 * Retrieves all the methods from the JUnit test class, which is a subclass
	 * of this class that begin with {@link #TEST_SUB_PANE_SIGNATURE}and invoke
	 * them.
	 * 
	 * @throws Throwable If any problems occurred during the execution of a method
	 */
	private void _testSubTest() throws Throwable
	{
		runMethods(retrieveMethods(TEST_SUB_PANE_SIGNATURE));
	}

	/**
	 * Acquires the <code>Robot</code> that is responsible to simulate user actions.
	 */
	private void acquireRobot()
	{
		try
		{
			if (this.robot == null)
				this.robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		}
		catch (Throwable e)
		{
			fail("Robot could not be acquired: " + e);
		}
	}

	/**
	 * Determines
	 *
	 * @param pane
	 * @return
	 */
	protected final boolean areChildrenDisabled(JComponent pane)
	{
		return checkChildrenEnableState(pane, false);
	}

	/**
	 * Determines
	 *
	 * @param pane
	 * @return
	 */
	protected final boolean areChildrenEnabled(JComponent pane)
	{
		return checkChildrenEnableState(pane, true);
	}

	/**
	 * Creates a component that can receives the focus but will not alter the
	 * testing.
	 */
	private void buildFocusGrabber()
	{
		if (this.parentTest == null)
		{
			this.focusGrabber = new JButton("Focus Grabber");
			this.focusGrabber.setFocusable(true);
			this.focusGrabber.setRequestFocusEnabled(true);
		}
		else
		{
			this.focusGrabber = this.parentTest.focusGrabber;
		}
	}

	/**
	 * Creates the pane used for testing its widgets.
	 *
	 * @return The <code>JComponent</code> with all its widgets
	 */
	protected abstract JComponent buildPane() throws Exception;

	/**
	 * Creates the pane to be tested by this JUnit test.
	 *
	 * @throws Exception If any problem occurs during the creation of the pane
	 */
	private JComponent buildPaneImp() throws Exception
	{
		if (this.pane == null)
		{
			if (this.parentTest != null)
				this.pane = this.parentTest.pane;
			else
				this.pane = buildPane();
		}

		return this.pane;
	}

	/**
	 * Creates the window that will be used to test different UI features.
	 */
	protected final void buildWindow()
	{
		if (this.window == null)
		{
			if (this.parentTest == null)
				this.window = new JFrame(windowTitle());
			else
				this.window = this.parentTest.window;
		}
	}

	/**
	 * Tests to make sure a pane does not have duplicate mnemonic.
	 *
	 * @param count
	 * @return
	 */
	protected boolean canContinueTestingDuplicateMnemonic()
	{
		return false;
	}

	/**
	 * Determines
	 *
	 * @param pane
	 * @return
	 */
	protected final boolean checkChildrenEnableState(Container pane, boolean enabled)
	{
		for (int index = pane.getComponentCount(); --index >= 0;)
		{
			Component component = pane.getComponent(index);

			// JPanel are usually not disabled, check its children
			if ((component instanceof JPanel) &&
				 (!checkChildrenEnableState((JPanel) component, enabled)))
			{
				return false;
			}

			// Check the scroll pane's view
			if (component instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane) component;
				component = scrollPane.getViewport().getView();

				if (! checkChildrenEnableState((Container) component, enabled))
					return false;
			}

			// At this point, we should be at component that would be disabled
			if (pane.getComponent(index).isEnabled() != enabled)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Makes sure the focus owner, if it is a text component, has been cleared
	 * from its default value so that entering a new value will not affect the
	 * conclusion of the test.
	 */
	private void clearTextInput() throws Exception
	{
		// Only need to do CTRL-A to select all the text, typing will clear the
		// text automatically
		simulateKeyImp(KeyEvent.VK_CONTROL, 'A');
	}

	/**
	 * Asserts when there are duplicate mnemonics.
	 *
	 * @param mnemonics The table containing the result of the scan
	 */
	private void dumpDuplicateMnemonicResults(Hashtable mnemonics)
	{
		StringBuffer dump = new StringBuffer();

		for (Iterator iter = mnemonics.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Vector duplicate = (Vector) entry.getValue();

			if (duplicate.size() > 1)
			{
				Integer mnemonic = (Integer) entry.getKey();

				dump.append(StringTools.CR);
				dump.append(new Character((char) mnemonic.intValue()));
				dump.append(" is used by multiple labels: ");
				dump.append(duplicate);
			}
		}

		if (dump.length() > 0)
		{
			fail(dump.toString());
		}
	}

	/**
	 * Executes this JUnit test as a standalone application by using its own
	 * window.
	 *
	 * @param arguments The list of arguments passed by the main method
	 */
	protected void execute(String[] arguments) throws Exception
	{
		this.standalone = true;
		setUp();
		this.window.setVisible(true);
	}

	
	/**
	 * Returns the pane used for testing its widgets.
	 *
	 * @return The <code>JComponent</code> with all its widgets
	 */
	protected final JComponent getPane()
	{
		return this.pane;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	protected abstract ResourceRepository getResourceRepository();

	/**
	 * Iniatlizes the main window where the page is displayed.
	 *
	 * @Throwable Throwable
	 */
	protected void initializeWindow() throws Exception
	{
		if (this.parentTest != null)
			return;

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(buildPaneImp(), BorderLayout.CENTER);

		if (!this.standalone)
			panel.add(this.focusGrabber, BorderLayout.SOUTH);

		this.window.getContentPane().add(panel, "Center");
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.pack();
		this.window.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				try
				{
					if (AbstractPanelTest.this.standalone)
						tearDown();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Translates the given character into the key code. If the character is
	 * [a-z|A-Z|0-9] then the char is simply converted into an integer.
	 *
	 * @param character The character to be converted
	 * @return The key code of the given character
	 * @see KeyEvent
	 */
	private int[] keyCode(int key)
	{
		boolean french = "fr".equalsIgnoreCase(Locale.getDefault().getLanguage());

		// Common keys
		switch (key)
		{
			case '!': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_1 };
			case '$': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_4 };
			case '%': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_5 };
			case '^': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_6 };
			case '&': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_7 };
			case '*': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_8 };
			case '(': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_9 };
			case ')': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_0 };
			case '_': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS };
			case '+': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS };

			case ':': return new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON };
		}

		// Mapping French-Canadian keyboard
		if (french)
		{
			switch (key)
			{
				case '#':  return new int[] { NO_MASK,               KeyEvent.VK_BACK_QUOTE };
				case '@':  return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_2 };
				case '[':  return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_OPEN_BRACKET };
				case ']':  return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_CLOSE_BRACKET };
				case '{':  return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_QUOTE };
				case '}':  return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_BACK_SLASH };
				case '|':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_BACK_QUOTE };
				case '\\': return new int[] { KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_BACK_QUOTE };
				case '\'': return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_PERIOD };
				case '"':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_2 };
				case '<':  return new int[] { NO_MASK,               KeyEvent.VK_BACK_SLASH };
				case '>':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_BACK_SLASH };
				case '/':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_3 };
				case '?':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_6 };
			}
		}
		// Mapping US-English keyboard
		else
		{
			switch (key)
			{
				case '#':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_3 };
				case '@':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_2 };
				case '[':  return new int[] { NO_MASK,               KeyEvent.VK_OPEN_BRACKET };
				case ']':  return new int[] { NO_MASK,               KeyEvent.VK_CLOSE_BRACKET };
				case '{':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_OPEN_BRACKET };
				case '}':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_CLOSE_BRACKET };

				case '|':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_BACK_SLASH };
				case '\\': return new int[] { NO_MASK,               KeyEvent.VK_BACK_SLASH };
				case '\'': return new int[] { NO_MASK,               KeyEvent.VK_QUOTE };
				case '"':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_QUOTE };
				case '<':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_COMMA };
				case '>':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_PERIOD };
				case '/':  return new int[] { NO_MASK,               KeyEvent.VK_SLASH };
				case '?':  return new int[] { KeyEvent.VK_SHIFT,     KeyEvent.VK_SLASH };
			}
		}

		boolean upperCase = Character.isUpperCase((char) key);

		return new int[]
		{
			upperCase ? KeyEvent.VK_SHIFT : NO_MASK,
			Character.toUpperCase((char) key)
		};
	}

	/**
	 * Registers the mnemonic associated with the given key into the given table.
	 *
	 * @param table The table where to add the mnemonic along with an empty list
	 * that will be used to store the duplicate mnemonics
	 * @param key The key used to retrieve the mnemonic
	 */
	private void registerMnemonic(Map table, String key)
	{
		ResourceRepository repository = getResourceRepository();

		Vector vector = new Vector();
		vector.add(repository.getString(key));

		table.put(new Integer(repository.getMnemonic(key)), vector);
	}

	/**
	 * Makes sure the focus is not on any components in the tested pane.
	 */
	private void requestFocus(final Component component) throws Exception
	{
		component.requestFocus();
		sleep();
	}

	/**
	 * Makes sure the focus is not on any components in the tested pane.
	 */
	private void resetFocus() throws Exception
	{
		requestFocus(this.focusGrabber);
		assertTrue(this.focusGrabber.hasFocus());
	}

	/**
	 * Retrieves the child component from the given container if one exist that
	 * has the given key as its name.
	 *
	 * @param key The name of the component to look for
	 * @param container The container to traverse
	 * @return Either the desired component or <code>null</code> if it could not
	 * be found within the children of the given container
	 */
	protected final JComponent retrieveChildComponent(String key, JComponent container)
	{
		for (int index = 0; index < container.getComponentCount(); index++)
		{
			JComponent childComponent = (JComponent) container.getComponent(index);

			// Directly retrieve the scroll pane's view
			if (childComponent instanceof JScrollPane)
			{
				JScrollPane scrollPane = (JScrollPane) childComponent;
				childComponent = (JComponent) scrollPane.getViewport().getView();
			}

			// The component was found
			if (key.equals(childComponent.getName()) &&
				 !(childComponent instanceof JLabel))
			{
				return childComponent;
			}
			// We'll assume only JPanel have children that we are interested to scan
			else if (childComponent instanceof JPanel)
			{
				childComponent = retrieveChildComponent(key, childComponent);

				if (childComponent != null)
					return childComponent;
			}
		}

		return null;
	}

	/**
	 * Editable <code>JComBoxBox</code> does not receive the focus but it's
	 * editor's component.
	 *
	 * @param component The component used to retrieve the combo box: either
	 * itself or one of its parent
	 * @return The given component if it is a <code>JComboBox</code> otherwise
	 * one of its parent
	 */
	private Component retrieveComboBox(Component component)
	{
		if (component == null)
			return null;

		if (component instanceof JComboBox)
			return component;

		return retrieveComboBox(component.getParent());
	}

	/**
	 * Retrieves the value contained by the <code>JTextComponent</code> that has
	 * the given key as its name.
	 *
	 * @param key The name of the component to search
	 * @return The <code>JTextComponent</code>'s text
	 */
	protected final String retrieveFieldEntry(String key)
	{
		JComponent component = retrieveChildComponent(key, this.pane);
		_testComponentType(component, COMPONENT_TEXT_COMPONENT);

		JTextComponent textComponent = (JTextComponent) component;
		String entry = textComponent.getText();

		if (entry.length() == 0)
			entry = null;

		return entry;
	}

	/**
	 * Retrieves all the methods where their names start with the given
	 * signature.
	 *
	 * @param testClass The class scanned for methods
	 * @param signature The prefix of the method's name
	 * @param testMethods The collection of methods where the methods need to be
	 * added to
	 */
	private void retrieveMethods(Class testClass,
										  String signature,
										  Collection testMethods)
	{
		if (AbstractPanelTest.class.equals(testClass))
			return;

		Method[] methods = testClass.getDeclaredMethods();

		for (int index = methods.length; --index >= 0;)
		{
			Method method = methods[index];

			if (method.getName().startsWith(signature) &&
				 (method.getParameterTypes().length == 0))
			{
				testMethods.add(method);
			}
		}

		retrieveMethods(testClass.getSuperclass(), signature, testMethods);
	}

	/**
	 * Retrieves all the methods where their names start with the given
	 * signature.
	 *
	 * @param signature The prefix of the method's name
	 * @return A collection of methods where their names start with the given
	 * signature
	 */
	private Collection retrieveMethods(String signature)
	{
		Vector testMethods = new Vector();
		retrieveMethods(getClass(), signature, testMethods);
		return testMethods;
	}

	/**
	 * Editable <code>JSpinner</code> does not receive the focus but it's
	 * editor's component.
	 *
	 * @param component The component used to retrieve the spinner: either
	 * itself or one of its parent
	 * @return The given component if it is a <code>JSpinner</code> otherwise one
	 * of its parent
	 */
	private JSpinner retrieveSpinner(Component component)
	{
		if (component == null)
			return null;

		if (component instanceof JSpinner)
			return (JSpinner) component;

		return retrieveSpinner(component.getParent());
	}

	/**
	 * Retrieves the value contained by the <code>JTextComponent</code> that has
	 * the given key as its name.
	 *
	 * @param key The name of the component to search
	 * @return The <code>JTextComponent</code>'s text
	 */
	protected final Number retrieveSpinnerEntry(String key)
	{
		JComponent component = retrieveChildComponent(key, this.pane);
		_testComponentType(component, COMPONENT_SPINNER);

		JSpinner spinner = (JSpinner) component;
		return (Number) spinner.getValue();
	}

	/**
	 * Runs all the methods contained in the given collection. If one fails, then
	 * the process is stopped.
	 *
	 * @param methods The methods to be ran
	 * @throws Throwable If any problem was encountered in an invoked method
	 */
	private void runMethods(Collection methods) throws Throwable
	{
		for (Iterator iter = methods.iterator(); iter.hasNext() ;)
		{
			try
			{
				updateWindowState();
				resetFocus();

				Method method = (Method) iter.next();
				method.setAccessible(true);
				method.invoke(this, (Object[])null);
			}
			catch (InvocationTargetException e)
			{
				throw e.getCause();
			}
		}
	}

	/**
	 * Runs the test from another test case inside of this test case.
	 *
	 * @param testCase The test to be run inside of this one
	 * @throws Throwable If any problem was encountered during the execute of
	 * another JUnit test as a child of this one
	 */
	protected final void runTestCase(TestCase testCase) throws Throwable
	{
		new TestSuiteWrapper(testCase).runBare();
	}

	/**
	 * Initializes the necessary for running this test.
	 *
	 * @throws Exception If any problem was encountered during the initialization
	 * (preparation) of this test
	 */
	protected void setUp() throws Exception
	{
		buildFocusGrabber();
		buildWindow();
		initializeWindow();
	}

	/**
	 * Simulates
	 *
	 * @param item
	 */
	protected final void simulateComboBoxSelection(Object item)
	{
		Component focusOwner = this.window.getFocusOwner();
		assertTrue(String.valueOf(focusOwner), focusOwner instanceof JComboBox);

		JComboBox comboBox = (JComboBox) focusOwner;
		comboBox.setSelectedItem(item);
	}

	/**
	 * Simulates
	 *
	 * @param localizedString
	 */
	protected final void simulateComboBoxSelectionByRenderer(String localizedString)
	{
		Component focusOwner = this.window.getFocusOwner();
		assertTrue(String.valueOf(focusOwner), focusOwner instanceof JComboBox);

		JComboBox comboBox = (JComboBox) focusOwner;
		ComboPopup comboPopup = (ComboPopup) comboBox.getAccessibleContext().getAccessibleChild(0);
		JList list = comboPopup.getList();

		ComboBoxModel model = comboBox.getModel();
		int count = model.getSize();
		Object selectedItem = model.getSelectedItem();

		for (int index = 0; index < count; index++)
		{
			Object item = model.getElementAt(index);
			JLabel label = (JLabel) comboBox.getRenderer().getListCellRendererComponent(list, item, index, item == selectedItem, true);

			if (localizedString.equals(label.getText()))
			{
				comboBox.setSelectedItem(item);
				break;
			}
		}
	}

	/**
	 * Simulates
	 *
	 * @param input
	 * @throws Exception
	 */
	protected final void simulateComboBoxTextInput(String input) throws Exception
	{
		simulateTextInput(input);
		simulateKey(KeyEvent.VK_TAB);
	}

	/**
	 * Simulates a key stroke with the given key code. If the given key code
	 * represents an upper case letter, than the SHIFT key will be simulated as
	 * well.
	 *
	 * @param text The text to be programmatically typed
	 */
	protected final void simulateFormattedTextInput(String text) throws Exception
	{
		simulateTextInput(text);      // Enter the new value
		simulateKey(KeyEvent.VK_TAB); // Push the value into the model
	}

	/**
	 * Simulates the given character. If the given key code represents an upper
	 * case letter, than the SHIFT key will be simulated as well.
	 * <p>
	 * Note: The only supported key code right now are [a-z|A-Z|0-9], it seems a
	 * letter (represented by a Unicode char) can't be easily converted into a
	 * key code.
	 * 
	 * @param character The value of the key used to simulate the event
	 */
	protected final void simulateKey(char character) throws Exception
	{
		int[] keyCodes = keyCode(character);
		simulateKeyImp(keyCodes[0], keyCodes[1]);
	}

	/**
	 * Simulates the given key.
	 * 
	 * @param key The value of the key used to simulate the event
	 */
	protected final void simulateKey(int key) throws Exception
	{
		simulateKeyImp(NO_MASK, key);
	}

	/**
	 * Simulates a key stroke with the given mnemonic key and maintain the given
	 * mask pressed during the letter is typed.
	 *
	 * @param mask Either <code>KeyEvent.VK_ALT</code>, <code>KeyEvent.VK_CONTROL</code>
	 * or <code>KeyEvent.VK_SHIFT</code>
	 * @param letter The keyboard key to simulate
	 */
	protected final void simulateKey(int mask, int key) throws Exception
	{
		simulateKeyImp(mask, key);
	}

	/**
	 * Simulates a key stroke with the given mnemonic key and maintain the given
	 * mask pressed during the letter is typed.
	 *
	 * @param mask Either <code>KeyEvent.VK_ALT</code>, <code>KeyEvent.VK_CONTROL</code>
	 * or <code>KeyEvent.VK_SHIFT</code>
	 * @param letter The keyboard key to simulate
	 * @param sleep Determines whether this Thread needs to sleep once the
	 * simulation of the given key is performed
	 */
	private void simulateKeyImp(int mask, int key) throws Exception
	{
		simulateKeyImp(mask, key, true);
	}

	/**
	 * Simulates a key stroke with the given mnemonic key and maintain the given
	 * mask pressed during the letter is typed.
	 *
	 * @param mask Either <code>KeyEvent.VK_ALT</code>, <code>KeyEvent.VK_CONTROL</code>
	 * or <code>KeyEvent.VK_SHIFT</code>
	 * @param letter The keyboard key to simulate
	 * @param sleep Determines whether this Thread needs to sleep once the
	 * simulation of the given key is performed
	 */
	private void simulateKeyImp(final int mask, final int key, boolean sleep) throws Exception
	{
		acquireRobot();

		if (mask != NO_MASK)
			this.robot.keyPress(mask);

		try
		{
			this.robot.keyPress(key);
			this.robot.keyRelease(key);
		}
		finally
		{
			if (mask != NO_MASK)
				this.robot.keyRelease(mask);
		}

		if (sleep)
			sleep();
	}

	/**
	 * Simulates a key stroke with the given mnemonic key.
	 *
	 * @param mnemonicKey The value of the mnemonic used to simulate the event
	 */
	protected final void simulateMnemonic(int mnemonicKey) throws Exception
	{
		simulateKeyImp(KeyEvent.VK_ALT, mnemonicKey);
	}

	/**
	 * Simulates an ALT-[mnemonic] with the mnemonic key by retrieving it from
	 * the repository with the given key.
	 *
	 * @param repositoryKey The key used to retrieve the mnemonic from the
	 * repository
	 */
	protected final void simulateMnemonic(String repositoryKey) throws Exception
	{
		simulateMnemonic(getResourceRepository().getMnemonic(repositoryKey));
	}

	/**
	 * Simulates programmatically the input of the given number.
	 *
	 * @param number The value to be entered into a <code>JSpinner</code>
	 */
	protected final void simulateSpinnerInput(double number) throws Exception
	{
		simulateSpinnerInput(Double.toString(number));
	}

	/**
	 * Simulates programmatically the input of the given number.
	 *
	 * @param number The value to be entered into a <code>JSpinner</code>
	 */
	protected final void simulateSpinnerInput(float number) throws Exception
	{
		simulateSpinnerInput(Float.toString(number));
	}

	/**
	 * Simulates programmatically the input of the given number.
	 *
	 * @param number The value to be entered into a <code>JSpinner</code>
	 */
	protected final void simulateSpinnerInput(int number) throws Exception
	{
		simulateSpinnerInput(Integer.toString(number));
	}

	/**
	 * Simulates programmatically the input of the given number.
	 *
	 * @param number The value to be entered into a <code>JSpinner</code>
	 */
	protected final void simulateSpinnerInput(long number) throws Exception
	{
		simulateSpinnerInput(Long.toString(number));
	}

	/**
	 * Simulates programmatically the input of the given number.
	 *
	 * @param number The value to be entered into a <code>JSpinner</code>
	 */
	protected final void simulateSpinnerInput(short number) throws Exception
	{
		simulateSpinnerInput(Short.toString(number));
	}

	/**
	 * Simulates the given text by simulating every character.
	 *
	 * @param text The text to be programmatically typed
	 */
	protected final void simulateSpinnerInput(String text) throws Exception
	{
		Component focusOwner = this.window.getFocusOwner();
		JSpinner spinner = retrieveSpinner(focusOwner);
		assertTrue(String.valueOf(focusOwner), spinner != null);

		// For some reason, the focus needs to be transfered
		// to the editor's text component
		transferFocusToSpinnerEditor(spinner);

		// Clear the entry first
		clearTextInput();

		// Simulate the entry
		simulateTextInput(text);

		// This will commit the value
		simulateKey(KeyEvent.VK_TAB);
	}

	/**
	 * Simulates the given text by simulating each letter of the string.
	 *
	 * @param text The text to be programmatically typed
	 */
	protected final void simulateTextInput(String text) throws Exception
	{
		Component focusOwner = this.window.getFocusOwner();
		assertTrue(String.valueOf(focusOwner), focusOwner instanceof JTextComponent);

		clearTextInput();

		for (int index = 0; index < text.length(); index++)
		{
			try
			{
				int[] keyCodes = keyCode(text.charAt(index));
				simulateKeyImp(keyCodes[0], keyCodes[1], false);
			}
			catch (IllegalArgumentException e)
			{
				fail("The character " + text.charAt(index) + " could not be simulated.");
			}
		}

		sleep();
	}

	/**
	 * Runs a <code>Runnable</code> inside of the Event Dispatch Thread so that
	 * it will do everything until this one is done and this JUnit test can
	 * continue with its testing thinking the GUI is done with its tasks.
	 */
	protected final void sleep() throws Exception
	{
		Thread.sleep(200);
	}

	/**
	 * Stores into the given map the given mnemonic and text in order to dump any
	 * duplicate mnemonics.
	 *
	 * @param mnemonics The table of mnemonic associated with label or button text
	 * @param text The text of either a button or a label
	 * @param mnemonic The mnemonic to cache
	 */
	private void storeMnemonic(Map mnemonics,
										String text,
										int mnemonic)
	{
		if (mnemonic != '\0')
		{
			Integer character = new Integer(mnemonic);

			if (mnemonics.containsKey(character))
			{
				Vector duplicate = (Vector) mnemonics.get(character);
				duplicate.add(text);
			}
			else
			{
				Vector duplicate = new Vector();
				mnemonics.put(character, duplicate);
				duplicate.add(text);
			}
		}
	}

	/**
	 * Nullified everything that was initialized.
	 *
	 * @throws Exception
	 */
	protected void tearDown() throws Exception
	{
		this.window.dispose();
		this.window = null;
		this.focusGrabber = null;
	}

	/**
	 * Tests whether the focus was transfered properly using the given mnemonic.
	 * Once the focus has been transfered, make sure it was transfered onto the
	 * desired component.
	 *
	 * @param mnemonicKey The mnemonic used to trigger the change of focus
	 * @param labelText The text of the label used to test if the focus was
	 * transfered from the desired mnemonic assigned from the right label
	 * @param componentType The type of the component that should receive the
	 * focus
	 */
	protected final void testFocusTransferByMnemonic(final int mnemonicKey,
																	 final String key,
																	 final int componentType) throws Exception
	{
		updateWindowState();
		resetFocus();

		// Simulate an ALT-mnemonic event
		simulateMnemonic(mnemonicKey);

		// Test the focus component
		Component component = this.window.getFocusOwner();

		assertTrue("The focus was not transfered, no component received the focus",
					  component != null);

		assertTrue("The focus was not transfered",
					  component != this.focusGrabber);

		assertFalse("The focus component is a JLabel, this could be because its associated component is disabled",
						component instanceof JLabel);

		// Make sure the component that received the focus is the proper component
		_testComponentType(component, componentType);

		// Make sure the component, even though is of the correct type,
		// is the expected component
		_testLabeledByText(component, componentType, key);
	}

	/**
	 * Tests
	 *
	 * @param key
	 * @param componentType
	 */
	protected final void testFocusTransferByMnemonic(String key, int componentType) throws Exception
	{
		ResourceRepository repository = getResourceRepository();
		testFocusTransferByMnemonic(repository.getMnemonic(key), key, componentType);
	}

	/**
	 * Tests the UI panels with several test suites.
	 *
	 * @throws Throwable
	 */
	public final void testUI() throws Throwable
	{
		_testDuplicateMnemonicImp();

		try
		{
			updateWindowState();
			_subTestUI();
		}
		finally
		{
			this.window.setVisible(false);
		}
	}

	/**
	 * Transfer to the first text component contained in the given container.
	 *
	 * @param container The container to traverse in order to find a text component
	 * @return <code>true<code> if a text component was found; <code>false<code>
	 * otherwise
	 */
	private boolean transferFocusToSpinnerEditor(Container container) throws Exception
	{
		Component[] components = container.getComponents();

		for (int index = 0; index < components.length; index++)
		{
			Component component = components[index];

			if (components[index] instanceof JTextComponent)
			{
				requestFocus(components[index]);
				return true;
			}
			else if (component instanceof Container)
			{
				if (transferFocusToSpinnerEditor((Container) component))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Makes sure the window is visible and in front of all the other windows.
	 * Once that is done, reset the focus.
	 */
	private void updateWindowState() throws Exception
	{
		EventQueue.invokeAndWait(new Runnable()
		{
			public void run()
			{
				if (!AbstractPanelTest.this.window.isVisible())
					AbstractPanelTest.this.window.setVisible(true);

				AbstractPanelTest.this.window.toFront();
			}
		});
	}

	/**
	 * Returns the title for the main window and the clone window.
	 *
	 * @return The title of the main window and close window
	 */
	protected abstract String windowTitle();

	/**
	 * 
	 */
	private class TestCaseWrapper
	{
		private String fName;
		private TestCase testCase;

		private TestCaseWrapper(TestCase testCase, String fName)
		{
			super();

			this.fName = fName;
			this.testCase = testCase;
		}

		private Method retrieveMethod(Class theClass, String methodName) throws Throwable
		{
			if (theClass == null)
				return null;

			try
			{
				Method method = theClass.getDeclaredMethod(methodName, new Class[0]);

				if (method != null)
					return method;
			}
			catch (SecurityException e)
			{
				// Ignore, let scan the superclass
			}
			catch (NoSuchMethodException e)
			{
				// Ignore, let scan the superclass
			}

			return retrieveMethod(theClass.getSuperclass(), methodName);
		}

		/**
		 * Runs the bare test sequence.
		 * 
		 * @exception Throwable if any exception is thrown
		 */
		void runBare() throws Throwable
		{
			setUp();

			try
			{
				runMethod(this.fName);
			}
			finally
			{
				tearDown();
			}
		}

		private void runMethod(String methodName) throws Throwable
		{
			assertNotNull(methodName);
			Method runMethod = null;

			try
			{
				// use getMethod to get all public inherited
				// methods. getDeclaredMethods returns all
				// methods of this class but excludes the
				// inherited ones.
				runMethod = retrieveMethod(this.testCase.getClass(), methodName);
				runMethod.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				fail("Method \"" + methodName + "\" not found");
			}

			try
			{
				runMethod.invoke(this.testCase, (Object[])new Class[0]);
			}
			catch (InvocationTargetException e)
			{
				e.fillInStackTrace();
				throw e.getTargetException();
			}
			catch (IllegalAccessException e)
			{
				e.fillInStackTrace();
				throw e;
			}
		}

		private void setUp() throws Throwable
		{
			runMethod("setUp");
		}

		private void tearDown() throws Throwable
		{
			runMethod("tearDown");
		}
	}

	/**
	 * This <code>TestSuiteWrapper</code> will run all the tests defined in a
	 * <code>TestCase</code>. {@link AbstractPanelTest#testUI()} will not be ran
	 * but {@link AbstractPanelTest#_subTestUI()} will be used instead in order
	 * to use the parent test's window.
	 */
	private class TestSuiteWrapper
	{
		/**
		 * The <code>TestCase</code> to be ran inside of the parent <code>TestCase</code>.
		 */
		private final TestCase testCase;

		/**
		 * Creates a new <code>TestSuiteWrapper</code>.
		 * 
		 * @param testCase The <code>TestCase</code> to be ran inside of the
		 * parent <code>TestCase</code>
		 */
		private TestSuiteWrapper(TestCase testCase)
		{
			super();
			this.testCase = testCase;
		}

		/**
		 * Creates the list of tests to be ran from the given test class. This
		 * method retrieves all the public test methods at the exception of
		 * {@link #testUI()}since it has to be run inside of another
		 * <code>AbstractPanelTest</code>, in that case, {@link #_subTestUI()}
		 * will be used instead.
		 * 
		 * @param theClass The class used to retrieve the name of the test methods
		 * @return The list of names of the test to be run
		 * @throws Throwable If any introspection calls on a Class failed
		 */
		private List createTestNames(Class theClass) throws Throwable
		{
			Class superClass = theClass;
			Vector names = new Vector();

			while (Test.class.isAssignableFrom(superClass))
			{
				Method[] methods = superClass.getDeclaredMethods();

				for (int index = 0; index < methods.length; index++)
				{
					Method method = methods[index];
					String name = method.getName();

					if (names.contains(name))
						continue;

					// testUI is run inside of another test by _subTestUI()
					if (name.equals("_subTestUI"))
					{
						names.add(name);
					}
					// Signature: public void testX()
					else if (!name.equals("testUI") &&
								isPublicTestMethod(method))
					{
						names.add(name);
					}
				}

				superClass = superClass.getSuperclass();
			}

			return names;
		}

		/**
		 * Determines
		 *
		 * @param method
		 * @return
		 */
		private boolean isPublicTestMethod(Method method)
		{
			return isTestMethod(method) &&
					 Modifier.isPublic(method.getModifiers());
		}

		/**
		 * Determines
		 *
		 * @param m
		 * @return
		 */
		private boolean isTestMethod(Method method)
		{
			String name = method.getName();
			Class[] parameters = method.getParameterTypes();
			Class returnType = method.getReturnType();

			return parameters.length == 0 &&
					 name.startsWith("test") &&
					 returnType.equals(Void.TYPE);
		}

		/**
		 * Runs
		 *
		 * @throws Throwable
		 */
		void runBare() throws Throwable
		{
			Class testClass = this.testCase.getClass();
			List tests = createTestNames(testClass);

			for (Iterator iter = tests.iterator(); iter.hasNext();)
			{
				TestCaseWrapper wrapper = new TestCaseWrapper(this.testCase, (String) iter.next());
				wrapper.runBare();
			}
		}
	}
}
