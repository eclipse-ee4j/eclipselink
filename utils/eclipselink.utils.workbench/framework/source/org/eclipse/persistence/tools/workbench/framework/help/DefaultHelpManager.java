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
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * New and improved CSHManager:
 * - weak references to UI components
 * - provides CSH for all the child components of any added component
 * - only supports a single help book
 */	
class DefaultHelpManager
	implements InternalHelpManager
{
	/** browser for displaying help content*/
	private ExternalBrowserHandler browser;
	
	/** help topic URL map */
	private HashMap<String, String> topicIdtoUrlMap;
	
	/** listen for pop-up menu */
	private LocalMouseListener mouseListener;

	/** listen for F1 */
	private KeyListener keyListener;

	/** help topics, keyed by component */
	private Map topicIDs;

	/** labels, messages, etc. */
	private ResourceRepository resourceRepository;

	/**  */
	private boolean localHelpFailed;

    
    /** This should point to the executable for an HTML browser. */
	static final String BROWSER_PREFERENCE = "external web browser";
		static final String BROWSER_PREFERENCE_DEFAULT = "";


	// ********** constructor/initialization **********

	/**
	 * initialize the new instance
	 */
	private DefaultHelpManager() {
		super();
		this.initialize();
	}

	/**
	 * construct a help manager that uses the specified OHJ help and book
	 */
	DefaultHelpManager(ResourceRepository resourceRepository, Preferences preferences) {
		this();
		this.resourceRepository = resourceRepository;
		this.localHelpFailed = false;
		this.initialize(preferences);
	}

	protected void initialize() {
		this.keyListener = this.buildKeyListener();
		// allow the entries to be garbage-collected when the
		// components are no longer referenced anywhere else
		this.topicIDs = new WeakHashMap();
		
	}

	private void initialize(Preferences preferences) {
		Icon icon = this.resourceRepository.getIcon("oracle.logo.large");
		this.mouseListener = this.buildMouseListener(this.resourceRepository.getString("CSH_HELP"));
		this.browser = new ExternalBrowserHandler(preferences);
		this.topicIdtoUrlMap = initializeTopicMap();
	}

	/**
	 * when F1 is pressed we will display the help topic
	 * for the source component
	 */
	private KeyListener buildKeyListener() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (( ! e.isConsumed()) && (e.getKeyCode() == KeyEvent.VK_F1)) {
					DefaultHelpManager.this.showTopic((Component) e.getSource());
					e.consume();
				}
			}
		};
	}

	/**
	 * when a pop-up menu is requested we will add a menu
	 * item for Help to the source component's pop-up menu
	 */
	private LocalMouseListener buildMouseListener(String menuItemLabel) {
		return new LocalMouseListener(menuItemLabel);
  	}

    
	// ********** InternalHelpManager implementation **********

	/**
	 * @see InternalHelpManager#setLocalHelpFailed(boolean)
	 */
	public void setLocalHelpFailed(boolean localHelpFailed) {
		this.localHelpFailed = localHelpFailed;
	}

	/**
	 * The application has completed its launch, notify the user
	 * if we had problems loading his "local" help book.
	 * @see InternalHelpManager#launchComplete()
	 */
	public void launchComplete() {
		if (this.localHelpFailed) {
			this.showTopic("noHelp");
		}
	}


	// ********** HelpManager implementation **********

	/**
	 * @see HelpManager#showHelp()
	 */
	public void showHelp() {
		this.showTopic("default");
	}
		
	/**
	 * @see HelpManager#showTopic(String)
	 */
	public void showTopic(String topicID) {
		this.showTopicInternal(topicID);
	}
	
	public void showUrl(String url) {
		this.browser.handleValue(url);
	}

	/**
	 * if the specified component has not been added, we
	 * check for its parent's topic ID and on up the hierarchy
	 * @see HelpManager#showTopic(java.awt.Component)
	 */
	public void showTopic(Component component) {
		String topicID = this.getTopicID(component);
		if (topicID != null) {
			this.showTopicInternal(topicID, component);
		}
	}

	/**
	 * @see HelpManager#addTopicIDs(java.util.Map)
	 */
	public void addTopicIDs(Map componentsToTopicIDs) {
		for (Iterator stream = componentsToTopicIDs.entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			this.addTopicID((Component) entry.getKey(), (String) entry.getValue());
		}
	}

	/**
	 * we listen to the component and all its children;
	 * but we register the topic ID with only the one component
	 * @see HelpManager#addTopicID(java.awt.Component, String)
	 */
	public void addTopicID(Component component, String topicID) {
		this.listenTo(component);
		this.topicIDs.put(component, topicID);
	}

	/**
	 * @see HelpManager#removeTopicIDs(java.util.Map)
	 */
	public void removeTopicIDs(Map componentsToTopicIDs) {
		this.removeTopicIDs(componentsToTopicIDs.keySet());
	}

	/**
	 * @see HelpManager#removeTopicIDs(java.util.Collection)
	 */
	public void removeTopicIDs(Collection components) {
		for (Iterator stream = components.iterator(); stream.hasNext(); ) {
			this.removeTopicID((Component) stream.next());
		}
	}

	/**
	 * @see HelpManager#removeTopicID(java.awt.Component)
	 */
	public void removeTopicID(Component component) {
		this.topicIDs.remove(component);
		this.stopListeningTo(component);
	}
    
    public void addItemsToPopupMenuForComponent(JMenuItem[] menuItems, Component component) {
        this.mouseListener.addItemsToPopupMenuForComponent(menuItems, component);
    }
    
    
	/**
	 * @see HelpManager#shutDown()
	 */
	public void shutDown() {
	}


	// ********** queries **********

	private String getTopicID(Component component) {
		if (component == null) {
			// stop when we get to the top of the component hierarchy
			return null;
		}

		String topicID = (String) this.topicIDs.get(component);
		if (topicID == null) {
			// recurse up component hierarchy
			return this.getTopicID(component.getParent());
		}
		return topicID;
	}


	// ********** behavior **********

	/**
	 * Display Help for the specified Topic ID. If there are
	 * problems, a TopicDisplayException will be thrown.
	 */
	protected void showTopicInternal(String topicID) {
		String url = this.topicIdtoUrlMap.get(topicID);
		if (url == null) {
			url = this.topicIdtoUrlMap.get("default");
		}
		this.browser.handleValue(url);
	}

	/**
	 * Display Help for the specified Topic ID. If there are
	 * problems, a TopicDisplayException will be thrown.
	 */
	protected void showTopicInternal(String topicID, Component component) {
		String url = this.topicIdtoUrlMap.get(topicID);
		if (url == null) {
			url = this.topicIdtoUrlMap.get("default");
		}
		this.browser.handleValue(url, component);
	}

	private void listenTo(Component component) {
		// only check for our mouse listener...
		if (CollectionTools.contains(component.getMouseListeners(), this.mouseListener)) {
			return;
		}

		// *assume* that if we need to add our mouse listener
		// then we also need to add our key listener
		component.addMouseListener(this.mouseListener);
		component.addKeyListener(this.keyListener);

		// likewise, *assume* that if we are already listening to a component
		// then we are also already listening to its children
		if (component instanceof Container) {
			this.listenTo(((Container) component).getComponents());
		}
	}

	private void listenTo(Component[] components) {
		for (int i = components.length; i-- > 0; ) {
			this.listenTo(components[i]);
		}
	}

	private void stopListeningTo(Component component) {
		// only check for our mouse listener...
		if ( ! CollectionTools.contains(component.getMouseListeners(), this.mouseListener)) {
			return;
		}

		// *assume* that if we need to remove our mouse listener
		// then we also need to remove our key listener
		component.removeMouseListener(this.mouseListener);
		component.removeKeyListener(this.keyListener);

		// likewise, *assume* that if we are listening to a component
		// then we are also listening to its children
		if (component instanceof Container) {
			this.stopListeningTo(((Container) component).getComponents());
		}
	}

	private void stopListeningTo(Component[] components) {
		for (int i = components.length; i-- > 0; ) {
			this.stopListeningTo(components[i]);
		}
	}

	private void showNavigatorWindow(Class navigatorClass) {
	}
		

	// ********** nested classes **********
	
	/**
	 * This listener will respond to a mouse pop-up trigger and
	 * add the Help menu to the source component. Selecting
	 * the Help menu item will trigger the component's help
	 * topic to be displayed.
	 */
	private class LocalMouseListener extends MouseAdapter {
		/** the Help pop-up menu added to the component with focus */
        JPopupMenu popupMenu;
	
		/** the component with focus - only held temporarily */
		private Component component;
	
        /** popup menuItems, keyed by component */
        private Map popupMenuItems;
	
		// ********** constructor/initialization **********
	
		LocalMouseListener(String menuItemLabel) {
			super();
			this.initialize(menuItemLabel);
		}
	
		/**
		 * build the pop-up menu that will be added to any component
		 * that has CSH installed
		 */
		private void initialize(String menuItemLabel) {
            // allow the entries to be garbage-collected when the
            // components are no longer referenced anywhere else
            this.popupMenuItems = new WeakHashMap();
            JMenuItem item = new JMenuItem(menuItemLabel);
            item.addActionListener(this.buildMenuItemListener());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

            this.popupMenu = new JPopupMenu();
            this.popupMenu.add(item);
		}
	
		/**
		 * this is the action that is executed if the user selects
		 * the Help menu item from the pop-up menu
		 */
		private ActionListener buildMenuItemListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// display help for the component stashed away when the pop-up menu was first displayed
					LocalMouseListener.this.showHelp();
				}
			};
		}
	
	
		// ********** MouseListener implementation **********
	
		public void mousePressed(MouseEvent e) {
			this.handleMouseEvent(e);
		}
	
		public void mouseClicked(MouseEvent e) {
			// is this method needed??? copied from CSHManager...
			this.handleMouseEvent(e);
		}
	
		public void mouseReleased(MouseEvent e) {
			this.handleMouseEvent(e);
		}
	
		/**
		 * if the mouse event is a pop-up menu trigger,
		 * stash away the source component and add the
		 * Help pop-up menu to the component
		 */
		private void handleMouseEvent(MouseEvent e) {
            if ( ! e.isPopupTrigger()) {
                return;
            }

            if (this.component != null) {
               //had to handle adding and removing the menuItems here instead of in
               //a PopupMenuListener.  If the popupMenu is already visible
               //when you right click elsewhere then none of the events get fired (ListChooser might be the problem)
                removeMenuItemsFor(this.component);
            }
            // stash away the component so the action listener
            // can display the component's help if necessary
            this.component = (Component) e.getSource(); 
            insertMenuItemsFor(this.component);
            this.popupMenu.show(this.component, e.getX(), e.getY());
		}
	
        private synchronized void insertMenuItemsFor(Component c) {
            JMenuItem[] menuItems = menuItemsFor(c);
            if (menuItems != null) {
                for (int i = 0; i < menuItems.length; i++) {
                    this.popupMenu.insert(menuItems[i], i);
                }
            }
        }
        
        private synchronized void removeMenuItemsFor(Component c) {
            JMenuItem[] menuItems = menuItemsFor(c);
            if (menuItems != null) {
                for (int i = 0; i < menuItems.length; i++) {
                    this.popupMenu.remove(menuItems[i]);
                }
            }
        }	
        
		// ********** behavior **********
	
		/**
		 * show help for the pop-up menu's component
		 */
		void showHelp() {
			DefaultHelpManager.this.showTopic(this.component);
		}
        
        void addItemsToPopupMenuForComponent(JMenuItem[] menuItems, Component c) {
            this.popupMenuItems.put(c, menuItems);
        }
        
        private JMenuItem[] menuItemsFor(Component c) {
            return (JMenuItem[]) this.popupMenuItems.get(c);
        }

	}


	/**
	 * Private class used to handle invoking an external browser when a link is 
	 * clicked on in the HelpContentPanel. The link must have the syntax of:
	 * custom:external:<URL to invoke>
	 */
	private class ExternalBrowserHandler {
	 	private Preferences preferences;
		public final static String PROTOCOL_NAME = "external";


	 	public ExternalBrowserHandler(Preferences preferences) {
	 		super();
	 		this.preferences = preferences;
	 	}

		// ********** CustomProtocolHandler implementation **********

		/**
		 * If the user has specified a specific browser in the preferences,
		 * use that; otherwise use the platform-specific default browser.
		 * @see oracle.help.CustomProtocolHandler#handleValue(String)
		 */
		public void handleValue(String value, Component component) {
			String browser = this.preferences.get(BROWSER_PREFERENCE, BROWSER_PREFERENCE_DEFAULT);
			try {
				Runtime.getRuntime().exec(browser + " " + value);
			} catch (IOException ex) {
				showBrowserConfigMessages(component);
			}
	   }
	
		/**
		 * If the user has specified a specific browser in the preferences,
		 * use that; otherwise use the platform-specific default browser.
		 * @see oracle.help.CustomProtocolHandler#handleValue(String)
		 */
		public void handleValue(String value) {
			String browser = this.preferences.get(BROWSER_PREFERENCE, BROWSER_PREFERENCE_DEFAULT);
			try {
				Runtime.getRuntime().exec(browser + " " + value);
			} catch (IOException ex) {
				throw new RuntimeException(DefaultHelpManager.this.resourceRepository.getString("CONFIGURE_EXTERNAL_BROWSER"));
			}
	   }

		private void showBrowserConfigMessages(Component component) {
			JOptionPane.showMessageDialog(component, 
					DefaultHelpManager.this.resourceRepository.getString("CONFIGURE_EXTERNAL_BROWSER"), 
					DefaultHelpManager.this.resourceRepository.getString("CONFIGURE_EXTERNAL_BROWSER_TITLE"),
					JOptionPane.WARNING_MESSAGE);
			//DefaultHelpManager.this.showTopic("help.linux.config.externalbrowser");
		}

	}

	private HashMap<String, String> initializeTopicMap() {
		HashMap<String, String> topicMap = new HashMap<String, String>(1);
		topicMap.put("default", "http://wiki.eclipse.org/EclipseLink/UserGuide");
		topicMap.put("eclipselink_home", "http://www.eclipse.org/eclipselink/");
		topicMap.put("eclipslink_userguide", "http://wiki.eclipse.org/EclipseLink/UserGuide");
		topicMap.put("eclipselink_api",  "http://www.eclipse.org/eclipselink/api/1.0/index.html");
		topicMap.put("eclipselink_examples", "http://wiki.eclipse.org/EclipseLink/Examples");
		return topicMap;
	}
}
