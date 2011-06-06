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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.util.ListResourceBundle;

// TODO move to public package? this class must be public
public final class FrameworkResourceBundle extends ListResourceBundle {
	
	public FrameworkResourceBundle() {
		super();
	}

	static final Object[][] contents = { 
		
		{"COPYRIGHT", "Copyright \u00A9 1997, 2011 Oracle. All rights reserved. <p> This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html"},

		// Helpful when having more than one Browse button in a panel
		{"BROWSE_BUTTON_1", "&Browse..."},
		{"BROWSE_BUTTON_2", "B&rowse..."},
		{"BROWSE_BUTTON_3", "Br&owse..."},
		{"BROWSE_BUTTON_4", "Bro&wse..."},
		{"BROWSE_BUTTON_5", "Brow&se..."},
		{"BROWSE_BUTTON_6", "Brows&e..."},
		{"BROWSE_BUTTON_7", "Browse&..."},

		// Main
		{"UNEXPECTED_EXCEPTION", "Unexpected exception in thread: ''{0}''"},

		// Help set-up
		{"MISSING_DEFAULT_HELP_SET", "The default help-set file was not found on the classpath or is invalid: \"{0}\". Help will be disabled."},
		{"LOCAL_FILE_PREFERENCE_EMPTY", "The local help-set file is not specified. Help will use the default help-set file. Check your Help settings in \"Preferences\"."},
		{"LOCAL_FILE_PREFERENCE_INVALID", "The local help-set file is invalid: \"{0}\". Help will use the default help-set file. Check your Help settings in \"Preferences\"."},
		{"INVALID_HELP_SET", "The help-set file is invalid: \"{0}\". Help will be disabled."},
		{"LOCAL_FAILED", "The local help-set file failed to load. Help will continue, using the default help-set file.{0}Check your Help settings in \"Preferences\"."},
		{"HELP_INITIALIZATION_ERROR", "Unable to initialize help. Check your Help Settings in \"Preferences\" and ensure your selected Help location is available.{0}Proceeding with the Help system disabled."},
		{"LOCALIZED_FILE_NOT_FOUND", "A localized help JAR was not found for the system locale, defaulting to English"},
		{"CONFIGURE_EXTERNAL_BROWSER", "To display help content an external HTML browser must first be configured in help preferences found under the Tools menu."},
		{"CONFIGURE_EXTERNAL_BROWSER_TITLE", "No External Broswer Configured"},
		
		// FrameworkLogHandlerDialog
		{"UNEXPECTED_ERROR", "Unexpected Error"},
		{"STACK_TRACE", "Stack Trace"},
		
		// FrameworkConsole
		{"UNEXPECTED_OUTPUT_LOG_TITLE", "Unexpected Output Log"},
		{"UNEXPECTED_OUTPUT_ERROR_MESSAGE", "The application has encountered unexpected output. The following was written to the console:"},
		{"CONSOLE.OK_BUTTON_TEXT",     "  OK  "},
		{"CONSOLE.HELP_BUTTON_TEXT",   "  Help  "},
		
		// FrameworkNodeManager
		{"VALIDATION_EXCEPTION", "An exception occurred validating a model: ''{0}''"},

		// AbstractDialog
		{"DIALOG.OK_BUTTON_TEXT",     "     OK     "},
		{"DIALOG.CANCEL_BUTTON_TEXT", "Cancel"},
		{"DIALOG.HELP_BUTTON_TEXT",   "  Help  "},
		
		// TextAreaDialog
		{"TEXT_AREA_DIALOG.COPY_BUTTON_TEXT",			"&Copy"},
		
		// NewNameDialog
		{"NEW_NAME_DIALOG.EMPTY_VALUE",			"The value cannot be empty."},
		{"NEW_NAME_DIALOG.ORIGINAL_VALUE",		"This is the original value, use a different value."},
		{"NEW_NAME_DIALOG.DUPLICATE_VALUE",	"The value is already used."},
		{"NEW_NAME_DIALOG.ILLEGAL_VALUE",		"The value is not allowed."},

		// ClassChooserPanel
		{"CLASS_CHOOSER_BROWSE_BUTTON", "&Browse..."},
		{"CLASS_CHOOSER_BROWSE_BUTTON_W_MNEMONIC", "Bro&wse..."},

		// ClassChooserDialog
		{"CLASS_CHOOSER_DIALOG.TITLE", "Choose Class"},
		{"CLASS_CHOOSER_DIALOG.TEXT_FIELD_LABEL", "&Choose a class (? = any character, * = any string):"},
		{"CLASS_CHOOSER_DIALOG.CLASS_LIST_BOX_LABEL", "&Matching classes:"},
		{"CLASS_CHOOSER_DIALOG.PACKAGE_LIST_BOX_LABEL", "&Packages:"},
		{"CLASS_CHOOSER_DIALOG.REFRESH_BUTTON", "&Refresh"},

		// MultipleClassChooserDialog
		{"selectClasses.title", "Select Classes"},
		{"availablePackages/classes", "&Available Packages/Classes:"},
		{"selectedClasses", "Selected &Classes:"},
		{"addSelectedClassesToList", "Add Selected Classes To List"},
		{"removeSelectedClassesFromList", "Remove Selected Classes From List"},
		{"refresh","Refresh List"},

		// NodeChooserDialog
		{"NODE_CHOOSER_DIALOG.TITLE", "Choose Node"},
		{"NODE_CHOOSER_DIALOG.TEXT_FIELD_LABEL", "&Choose a node (? = any character, * = any string):"},
		{"NODE_CHOOSER_DIALOG.NODE_LIST_BOX_LABEL", "&Matching nodes:"},
		{"NODE_CHOOSER_DIALOG.PATH_LIST_BOX_LABEL", "&Path:"},

		// ProjectLoader
		{"PROJECT_LOADER_DIALOG.TITLE", "Opening Project"},
		{"OPENING_PROJECT_MESSAGE",      "Opening ''{0}''...."},
		{"OPEN_EXCEPTION", "An exception occurred while opening the file: ''{0}''"},
		{"UNSUPPORTED_FILE_TYPE", "Unsupported file type: ''{0}''"},

		// AboutDialogPane
		{"ABOUT", "&About"},
		{"BUILD", "Build {0}"},
		{"VERSION", "Version {0}"},

		// WorkbenchWindow
		{"FILE_MENU", "&File"}, 
		{"WORKBENCH_MENU", "Work&bench"},
		{"SELECTED_MENU", "&Selected"},
		{"TOOLS_MENU", "&Tools"},
		{"DEV_MENU", "&Development"},
		{"WINDOW_MENU", "&Window"},
		{"HELP_MENU", "&Help"},
		{"REOPEN_SUB_MENU", "&Reopen"},
		{"REVERT_TO_SAVED.title", "Revert to saved?"},
		{"REVERT_TO_SAVED.message", "Do you want to revert to the saved ''{0}''?"},
		{"MAIN_TOOL_BAR", "Main"},
		{"SELECTION_TOOL_BAR", "Selection"},

		// NavigatorView	
		{"NAVIGATOR_LABEL", "&Navigator"},
		
		// EditorView
		{"EDITOR_LABEL", "&Editor"},		

		// ProblemsView
		{"PROBLEMS_LABEL", "&Problems"},
		{"PROBLEMS_MISSING_MESSAGE", "<missing problem message>"},	
		{"PROBLEM_CODE", "Code"},
		{"PROBLEM_MESSAGE", "Message"},	
		{"PROBLEM_NODE", "Node"},

		// GoToAction
		{"GO_TO", "Go to..."},
		{"GO_TO.ACCELERATOR", "control shift G"},

		// ProblemReportAction
		{"PROBLEM_REPORT", "Problem Report..."},
		{"PROBLEM_REPORT.ACCELERATOR", "control shift P"},
		{"PROBLEM_REPORT_DIALOG_TITLE", "Problem Report"},

		// Accessibility
		{"ACCESSIBLE_NODE", "{0}"},

		// AboutAction
		{"about", "About {0}"},

		// CloseAction
		{"file.close", "&Close"},
		{"file.close.toolTipText", "Close"},
		{"file.close.longToolTipText", "Closes the selected documents. If some documents are modified, prompt to save them first."},
		{"file.close.ACCELERATOR", "control F4"},

		// CloseAllAction
		{"file.closeAll","C&lose All"},
		{"file.closeAll.toolTipText", "Close All"},
		{"file.closeAll.longToolTipText", "Closes all the opened documents. If some documents are modified, prompt to save them first."},
		{"file.closeAll.ACCELERATOR", "control shift F4"},

		// OpenFileAction
		{"file.open", "&Open..."},
		{"file.open.toolTipText", "Open"},
		{"file.open.longToolTipText", "Shows the file chooser in order to open a document."},
		{"file.open.ACCELERATOR", "control O"},
		{"fileAlreadyOpenMessage", "Cannot open a document that is already opened, select a different file."},
		{"fileAlreadyOpenTitle", "Already Open"},

		// NewAction
		{"file.new", "&New"},
		{"file.new.toolTipText", "New"},
		{"file.new.longToolTipText", "Creates a new document."},

		// MigrateAction
		{"file.migrate", "&Migrate"},
		{"file.migrate.toolTipText", "Migrate"},
		{"file.migrate.longToolTipText", "Migrate an existing project."},

		// ExitAction
		{"EXIT_ACTION", "E&xit"},
		{"EXIT_ACTION.TOOL_TIP", "Exit"},

		// SynchronousProblemsAction
		{"tools.synchronousProblems", "&Synchronous Problems"},
		{"tools.synchronousProblems.toolTipText", "Synchronous Problems"},
		{"tools.synchronousProblems.longToolTipText", "Toggle the creation of new projects with \"synchronous\" validators."},

		// NewWindowAction
		{"window.newWindow", "&New Window"},
		{"window.newWindow.toolTipText", "Open a new window"},

		// ShowProblemsAction
		{"window.showProblems", "&Show Problems"},
		{"window.showProblems.toolTipText", "Show Problems"},
		{"window.showProblems.longToolTipText", "Toggle the display of the problems of the currently selected item(s)."},
		{"window.showProblems.ACCELERATOR", "control P"},

		// HelpAction
		{"HELP", "&Help Topics"},
		{"HELP.TOOL_TIP", "Help Topics"},
		
		// HomeAction
		{"ECLIPSELINK_HOME", "EclipseLink &Home"},
		
		// IndexAction
		{"HELP_INDEX", "&Index Search"},
		
		// SearchAction
		{"HELP_SEARCH", "&Full Text Search"},

		// TutorialsAction
		{"TUTORIALS", "T&utorials"},
		
		// ReleaseNotesAction
		{"RELEASE_NOTES", "Release &Notes"},
		
		//UserGuideAction
		{"USERGUIDE", "User &Guide"},
		
		//ExamplesAction
		{"EXAMPLES", "&Examples"},
		
		// JavaDocAction
		{"API", "API &Reference"},
		
		// WelcomeAction
		{"HELP_WELCOME", "&Welcome Page"},
		
		// PreferencesAction
		{"PREFERENCES", "&Preferences"},
		{"PREFERENCES.TOOL_TIP", "Preferences"},
		{"PREFERENCES.DIALOG.TITLE", "Preferences"},
		{"PREFERENCES.DIALOG.IMPORT", "Import..."},
		{"PREFERENCES.DIALOG.EXPORT", "Export..."},
		{"PREFERENCES.DIALOG.EXPORT.SUCCESS.DIALOG", "Preferences have been exported."},
		{"PREFERENCES.DIALOG.IMPORT.SUCCESS.DIALOG", "Preferences have been imported."},
		{"PREFERENCES.DIALOG.IMPORT.INVALID_IMPORT_FILE", "The file ''{0}'' does not exist.  Select a valid file."},

		{"PREFERENCES.GENERAL", "General"},
		{"PREFERENCES.GENERAL.DISPLAY_SPLASH_SCREEN", "Display &Splash Screen"},
		{"PREFERENCES.GENERAL.RECENT_FILES_SIZE", "Size of &recently opened files list:"},
		{"PREFERENCES.GENERAL.LOOK_AND_FEEL", "&Look and Feel:"},
		{"PREFERENCES.GENERAL.HTTP.PROXY.HOST", "HTTP Proxy &Host:"},
		{"PREFERENCES.GENERAL.HTTP.PROXY.PORT", "HTTP Proxy &Port:"},
		{"PREFERENCES.GENERAL.NETWORK.CONNECT_TIMEOUT", "Network &Connect Timeout (seconds):"},
		{"PREFERENCES.GENERAL.NETWORK.READ_TIMEOUT", "&Network Read Timeout (seconds):"},
		{"PREFERENCES.GENERAL.REOPEN_PROJECTS", "Re&open Projects on Startup"},

		{"PREFERENCES.GENERAL.HELP", "Help"},
		{"PREFERENCES.GENERAL.HELP.DISPLAY_WELCOME", "&Display Welcome at Startup"},
		{"PREFERENCES.GENERAL.HELP.LOCAL", "Help &Jar:"},
		{"PREFERENCES.GENERAL.HELP.LOCAL_BROWSER_CHOOSER_BUTTON", "B&rowse..."},
		{"PREFERENCES.GENERAL.HELP.BROWSER_CHOOSER", "&External HTML Browser:"},
		{"PREFERENCES.GENERAL.HELP.BROWSER_CHOOSER_BUTTON", "&Browse..."},
		
		// PreferencesNavigatorView
		{"HELP_POPUP", "&Help"},
		
		// DevelopmentConsoleAction
		{"DEVELOPMENT_CONSOLE", "&Development Console"},

		// HelpTopicIDWindowAction
		{"HELP_TOPIC_ID_WINDOW", "&Help Topic ID Window"},
		{"HELP_TOPIC_ID_WINDOW.TOOL_TIP", "Help Topic ID Window"},

		// ThreadBrowserAction
		{"THREAD_BROWSER", "&Thread Browser"},

		// JavaHeapInformationAction
		{"JAVA_HEAP_INFO", "&Java Heap Information"},

		// SaveAction
		{"file.save", "&Save"},
		{"file.save.toolTipText", "Save"},
		{"file.save.longToolTipText", "Saves the selected documents."},
		{"file.save.ACCELERATOR", "control S"},

		// SaveModifiedProjectsDialog
		{"selectAll", "&Select All"},
		{"saveModifiedDocuments.message", "Select the &projects to save:"},
		{"saveModifiedDocuments.title", "Save Projects"},
		{"deselectAll", "&Deselect All"},

		// SaveAllAction
		{"file.saveAll", "Sav&e All"},
		{"file.saveAll.toolTipText", "Save All"},
		{"file.saveAll.longToolTipText", "Saves all the documents."},
		{"file.saveAll.ACCELERATOR", "control shift S"},

		// SaveAsAction
		{"file.saveAs", "Save &As..."},
		{"file.saveAs.toolTipText", "Save As"},
		{"file.saveAs.longToolTipText", "Saves the selected document to another location."},
		{"file.saveAs.ACCELERATOR", "control shift A"},
			
		// MultiSelectionPseudoNode
		{"MULTI_SELECTION_DISPLAY_STRING",    "Multi-selection"},
		
		// used a number of places
		{"COMMENT_LABEL",    "&Comment"},
		
		// context-sensitive help pop-up menu item
		{"CSH_HELP", "Help"},

		// RecentsAction
		{"recentProjectFileNotFound.title", "Project File Not Found"},
		{"recentProjectFileNotFound.message", "The file ''{0}'' cannot be found and will be removed from the recents list."},	
		
		//DefaultHelpManager
		{"TOPIC_ID_DOESNT_EXIST", "The help topic {0} doesn't not exist."},

	};

	public Object[][] getContents() {
		return contents;
	}
}
