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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.CompositeIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.IconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsio.ReadOnlyFilesException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.AddOrRefreshClassesAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.AutomappableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.CreateNewClassAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ExportDeploymentXmlAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ExportModelJavaSourceAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ManageNonDescriptorClassesAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RefreshClassesAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode.DescriptorNodeBuilder;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.EisProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.OXProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.BlockIcon;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This node is a bit different from the typical node in that its children
 * do not correspond 1:1 with its value's children. This node's children
 * consists of a database node and a collection of descriptor package
 * nodes; while the MWProject holds on to a database, it does not
 * have descriptor packages - it holds on to the descriptors directly.
 */
public abstract class ProjectNode
	extends MappingsApplicationNode
	implements AutomappableNode
{
	/** this is passed to new descriptor package nodes */
	private DescriptorNodeBuilder descriptorNodeBuilder;

	/** this listens for descriptors being added or removed */
	private CollectionChangeListener descriptorsListener;

	/**
	 * this listens for descriptor name changes, which can
	 * result in a descriptor changing its package
	 */
	private PropertyChangeListener descriptorNameListener;

	/**
	 * we control the descriptor package nodes directly by
	 * listening to the project's 'descriptors' collection and
	 * the descriptors' 'name's
	 */
	private CollectionValueModel descriptorPackageNodesHolder;

	/**
	 * subclasses will build the node that goes in here
	 */
	private PropertyValueModel metaDataRepositoryNodeHolder;

	/**
	 * this will be a composite of the descriptor package nodes and
	 * the meta-data repository node
	 */
	private ListValueModel childrenModel;
	
	/**
	 * used to temporarily disable our prompting the user
	 * to update the ejb-jar.xml file
	 */
	private boolean promptsForEjbJarXmlWrite;


	protected static final String[] PROJECT_ICON_PROPERTY_NAMES = (String[]) CollectionTools.addAll(DEFAULT_ICON_PROPERTY_NAMES, new String[] {MWProject.VALIDATING_PROPERTY});
	protected static final Icon VALIDATING_ICON = new BlockIcon(2);
	protected static final String[] PROJECT_DISPLAY_STRING_PROPERTY_NAMES = {MWProject.NAME_PROPERTY};

	// ********** preferences ********

	public static final String WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE = "write ejb-jar.xml on save ";
		public static final String WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_ALWAYS = "Yes";
		public static final String WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_NEVER = "No";
		public static final String WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_PROMPT = "Prompt";
		public static final String WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_DEFAULT = WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_PROMPT;


	// ********** Static Methods **********

	public static ProjectNode forProject(MWProject project, ApplicationContext context, MappingsPlugin plugin) {
		if (project instanceof MWRelationalProject) {
			return new RelationalProjectNode((MWRelationalProject) project, context, plugin);
		} else if (project instanceof MWOXProject) {
			return new OXProjectNode((MWOXProject) project, context, plugin);
		} else if (project instanceof MWEisProject) {
			return new EisProjectNode((MWEisProject) project, context, plugin);
		} else {
			throw new IllegalArgumentException(project.toString());
		}
	}


	// ********** constructors/initialization **********

	protected ProjectNode(MWProject project, MappingsPlugin plugin, ApplicationContext context) {
		super(project, context.getNodeManager().getRootNode(), plugin, context);
	}

	protected void initialize() {
		super.initialize();
		this.descriptorNodeBuilder = this.buildDescriptorNodeBuilder();
		this.descriptorsListener = this.buildDescriptorsListener();
		this.descriptorNameListener = this.buildDescriptorNameListener();
		this.descriptorPackageNodesHolder = new SimpleCollectionValueModel();
		this.metaDataRepositoryNodeHolder = new SimplePropertyValueModel();
		this.childrenModel = this.buildChildrenModel();
		this.promptsForEjbJarXmlWrite = true;
	}

	protected abstract DescriptorNodeBuilder buildDescriptorNodeBuilder();

	private CollectionChangeListener buildDescriptorsListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				ProjectNode.this.addDescriptorNodesFor(e.items());
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				ProjectNode.this.removeDescriptorNodesFor(e.items());
			}
			public void collectionChanged(CollectionChangeEvent e) {
				ProjectNode.this.rebuildDescriptorNodes();
			}
			public String toString() {
				return "descriptors listener";
			}
		};
	}

	private PropertyChangeListener buildDescriptorNameListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String oldPackageName = ClassTools.packageNameForClassNamed((String) e.getOldValue());
				String newPackageName = ClassTools.packageNameForClassNamed((String) e.getNewValue());
				if ( ! newPackageName.equals(oldPackageName)) {
					ProjectNode.this.descriptorChangedPackage((MWDescriptor) e.getSource(), oldPackageName, newPackageName);
				}
			}
			public String toString() {
				return "descriptor name listener";
			}
		};
	}

	private ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildChildrenDisplayStringAdapter(), Child.COMPARATOR) {
            protected int indexToAddItems() {
                //add new items before the metaDataRepositoryNode so it doesn't resort
            	//LDD of course if the item happens to be the metaDataRepository, this response throws an indexOutOfBoundsException
            	//so we need to account for that case.
            	int index = super.indexToAddItems();
                return (index > 0) ? --index : index;
            }
			public String toString() {
				return "children model";
			}
        };
	}

	/**
	 * the display string (name) of each child node can change
	 * and trigger a re-sort of the list; actually, the it is unlikely
	 * the display strings will change in a way that requires the
	 * list to be re-sorted, but we need to listen to them or
	 * they will be null
	 */
	private ListValueModel buildChildrenDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildUnsortedChildrenModel(), DISPLAY_STRING_PROPERTY);
	}

	private CollectionValueModel buildUnsortedChildrenModel() {
		CollectionValueModel container = new SimpleCollectionValueModel();
		container.addItem(new PropertyCollectionValueModelAdapter(this.metaDataRepositoryNodeHolder));
		container.addItem(this.descriptorPackageNodesHolder);
		return new CompositeCollectionValueModel(container, Transformer.NULL_INSTANCE);
	}

	protected ApplicationContext expandContext(ApplicationContext context) {
		return super.expandContext(context).
            buildExpandedResourceRepositoryContext(UiProjectBundle.class).
            buildExpandedResourceRepositoryContext(UiQueryBundle.class).
            buildExpandedResourceRepositoryContext(UiMappingBundle.class).   
            buildExpandedResourceRepositoryContext(UiDescriptorBundle.class).        
            buildExpandedResourceRepositoryContext(UiCommonBundle.class);        
	}


	// ********** MappingsApplicationNode overrides **********

	private Iterator descriptors() {
		return new CompositeIterator(
				new TransformationIterator(this.descriptorPackageNodes()) {
					protected Object transform(Object next) {
						return ((DescriptorPackageNode) next).descriptors();
					}
				}
		);
	}

	public void addDescriptorsTo(Collection descriptors) {
		CollectionTools.addAll(descriptors, this.descriptors());
	}

	public boolean isAutoMappable() {
		return true;
	}
	

	// ********** AbstractApplicationNode overrides **********

	protected void engageValue() {
		super.engageValue();
		this.getProject().addCollectionChangeListener(MWProject.DESCRIPTORS_COLLECTION, this.descriptorsListener);
		this.addDescriptorNodesFor(this.getProject().descriptors());
		this.metaDataRepositoryNodeHolder.setValue(this.buildMetaDataRepositoryNode());
	}

	/**
	 * Build the appropriate node to represent the project's "meta-data
	 * repository".
	 */
	protected abstract Child buildMetaDataRepositoryNode();

	protected void disengageValue() {
		this.metaDataRepositoryNodeHolder.setValue(null);
		this.removeDescriptorNodesFor(this.getProject().descriptors());
		this.getProject().removeCollectionChangeListener(MWProject.DESCRIPTORS_COLLECTION, this.descriptorsListener);
		super.disengageValue();
	}

	protected String[] iconPropertyNames() {
		return PROJECT_ICON_PROPERTY_NAMES;
	}

	protected IconBuilder buildIconBuilder() {
		IconBuilder ib = super.buildIconBuilder();
		return this.getApplicationContext().getApplication().isDevelopmentMode() ?
				this.buildDevelopmentModeIconBuilder(ib)
			:
				ib;
	}

	private IconBuilder buildDevelopmentModeIconBuilder(IconBuilder ib) {
		return new CompositeIconBuilder(
				ib,
				this.getProject().isValidating(),
				VALIDATING_ICON,
				-17,	// gap - overlay the "leading" edge of the unadorned icon
				SwingConstants.HORIZONTAL,	// orientation
				SwingConstants.TOP,	// alignment
				null	// description
		);
	}

	protected String[] displayStringPropertyNames() {
		return PROJECT_DISPLAY_STRING_PROPERTY_NAMES;
	}


	// ********** ApplicationNode implementation **********
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		GroupContainerDescription desc = new RootMenuDescription();
		context = this.buildLocalWorkbenchContext(context);
		this.addToMenuDescription(desc, context);
		return desc;
	}
	
	protected abstract void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context);

	protected MenuGroupDescription buildClassActionGroup(WorkbenchContext context) {
		MenuGroupDescription classActionGroup = new MenuGroupDescription();
		classActionGroup.add(this.getRefreshClassesAction(context));
		classActionGroup.add(this.getAddOrRefreshClassesAction(context));
		classActionGroup.add(this.getCreateNewClassAction(context));
		classActionGroup.add(this.getManageNonDescriptorClassesAction(context));
		return classActionGroup;
	}
		
	protected MenuGroupDescription buildCloseDeleteActionGroup(WorkbenchContext context) {
		MenuGroupDescription closeDeleteActionGroup = new MenuGroupDescription();
		closeDeleteActionGroup.add(getCloseAction(context));
		closeDeleteActionGroup.add(getDeleteProjectAction(context));
		return closeDeleteActionGroup;
	}
	
	protected MenuGroupDescription buildSaveActionGroup(WorkbenchContext context) {
		MenuGroupDescription saveGroup = new MenuGroupDescription();
		saveGroup.add(getSaveAction(context));
		saveGroup.add(getSaveAsAction(context));
		return saveGroup;
	}
	
	protected MenuGroupDescription buildExportActionGroup(WorkbenchContext context) {
		MenuGroupDescription exportGroup = new MenuGroupDescription();
		exportGroup.add(this.buildExportMenuDescription(context));
		return exportGroup;
	}
	
	protected abstract GroupContainerDescription buildExportMenuDescription(WorkbenchContext context);

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	public String helpTopicID() {
		return "project";
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		return new ToolBarDescription();
	}

	protected final FrameworkAction getDeleteProjectAction(WorkbenchContext context) {
		return new DeleteProjectAction(context);
	}
	
	protected ExportDeploymentXmlAction getExportDeploymentXmlAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getExportDeploymentXmlAction(context);
	}
    
    protected ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(WorkbenchContext context) {
        return this.getMappingsPlugin().getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(context);
    }
    
	protected RefreshClassesAction getRefreshClassesAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getRefreshClassesAction(context);
	}

	protected AddOrRefreshClassesAction getAddOrRefreshClassesAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getAddOrRefreshClassesAction(context);
	}

	protected CreateNewClassAction getCreateNewClassAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getCreateNewClassAction(context);
	}
	
	protected ManageNonDescriptorClassesAction getManageNonDescriptorClassesAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getManageNonDescriptorClassesAction(context);
	}

	protected ExportModelJavaSourceAction getModelJavaSourceAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getExportModelJavaSourceAction(context);
	}

	protected FrameworkAction getSaveAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getSaveAction();
	}

	protected FrameworkAction getSaveAsAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getSaveAsAction();
	}

	protected FrameworkAction getCloseAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getCloseAction();
	}


	public File saveFile() {
		return this.getProject().saveFile();
	}

	public boolean save(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {
		boolean persistLastRefresh = preferences().getBoolean(MWClassRepository.PERSIST_LAST_REFRESH_PREFERENCE, MWClassRepository.PERSIST_LAST_REFRESH_PREFERENCE_DEFAULT);
		this.getProject().getClassRepository().setPersistLastRefresh(persistLastRefresh);
		File saveFile = this.saveFile();
		if (saveFile == null) {
			// the save file will be null on new and legacy projects
			if (this.getProject().isLegacyProject()) {
				JOptionPane.showMessageDialog(
					workbenchContext.getCurrentWindow(),
					this.resourceRepository().getString("PROJECT_SAVED_IN_NEW_FILE_FORMAT_DIALOG")
				);
			}
			return this.saveAs(mostRecentSaveDirectory, workbenchContext);
		}
		workbenchContext = this.buildLocalWorkbenchContext(workbenchContext);
		return this.saveInternal(saveFile, workbenchContext);
	}

	private boolean promptToWriteEjbJarXml(WorkbenchContext workbenchContext) {
		if (preferences().get(WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE, WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_DEFAULT).equals(WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_ALWAYS)) {
			return true;
		} else if (preferences().get(WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE, WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_DEFAULT).equals(WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_NEVER)) {
			return false;
		}
		
		int option =
			JOptionPane.showConfirmDialog(
					workbenchContext.getCurrentWindow(),
					resourceRepository().getString("WRITE_PROJECT_EJB_JAR_ON_SAVE_DIALOG"),
					resourceRepository().getString("WRITE_PROJECT_EJB_JAR_ON_SAVE_DIALOG.title"),
					JOptionPane.YES_NO_OPTION
			);
		return option == JOptionPane.YES_OPTION;
	}
	
	public boolean saveAs(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {
		File saveFile = this.saveFile();
		File directory = this.getProject().getSaveDirectory();

		if (directory == null) {
			directory = mostRecentSaveDirectory;
			saveFile = new File(mostRecentSaveDirectory, this.getProject().getName() + MWProject.FILE_NAME_EXTENSION);
		}

		workbenchContext = this.buildLocalWorkbenchContext(workbenchContext);
		LocalFileChooser chooser = new LocalFileChooser(workbenchContext);
		chooser.setDialogTitle(this.resourceRepository().getString("SAVE_AS_DIALOG_TITLE"));
		chooser.setCurrentDirectory(directory);
		chooser.setSelectedFile(saveFile);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(this.buildSaveAsDialogFileFilter(workbenchContext));
		int result = chooser.showSaveDialog(workbenchContext.getCurrentWindow());

		if (result != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		saveFile = chooser.getSelectedFile();
		File saveDir = saveFile.getParentFile();
		this.getProject().setSaveDirectory(saveDir);

		String projectName = saveFile.getName();
		if (projectName.toLowerCase().endsWith(MWProject.FILE_NAME_EXTENSION)) {
			projectName = FileTools.stripExtension(projectName);
		}
		this.getProject().setName(projectName);

		workbenchContext.getNavigatorSelectionModel().pushExpansionState();
		boolean saved = saveInternal(saveFile, workbenchContext);
		workbenchContext.getNavigatorSelectionModel().popAndRestoreExpansionState();
		return saved;
	}


	private FileFilter buildSaveAsDialogFileFilter(final WorkbenchContext workbenchContext) {
		return new FileFilter () {
			public String getDescription() {
				return workbenchContext.getApplicationContext().getResourceRepository().getString("SAVE_AS_DIALOG_MWP_FILE_FILTER");
			}

			public boolean accept(File file) {
				return file.isDirectory() || MWProject.FILE_NAME_EXTENSION.equals(FileTools.extension(file));
			}
		};
	}

	private boolean saveInternal(File saveFile, WorkbenchContext workbenchContext) {
		boolean saved = false;
		ProjectIOManager ioMgr = this.getMappingsPlugin().getIOManager();
		try {
			ioMgr.write(this.getProject());
			saved = true;
		} catch (ReadOnlyFilesException exception) {
			ReadOnlyFileDialog dialog = new ReadOnlyFileDialog(
				workbenchContext, 
				CollectionTools.collection(exception.getFiles())
			);
			dialog.show();

			if (dialog.saveAsWasPressed()) {
				return this.saveAs(null, workbenchContext);
			}
			if (dialog.saveWasPressed()) {
				// try again: recurse...
				return this.saveInternal(saveFile, workbenchContext);
			}
		}

		return saved;
	}
	

	// ********** AutomappableNode implementation **********
	
	public String getAutomapSuccessfulStringKey() {
		return "AUTOMAP_PROJECT_SUCCESSFUL";
	}
	
	public boolean canAutomapDescriptors() {
		return this.getProject().canAutomapDescriptors();
    }
    
    public abstract String getCannotAutomapDescriptorsStringKey();
    
    
	// ********** queries **********

	public MWProject getProject() {
		return (MWProject) this.getValue();
	}

	private Iterator descriptorPackageNodes() {
		return (Iterator) this.descriptorPackageNodesHolder.getValue();
	}

	private DescriptorPackageNode descriptorPackageNodeFor(MWDescriptor descriptor) {
		return this.descriptorPackageNodeNamed(descriptor.packageName());
	}

	/**
	 * return null if the node is not found
	 */
	public DescriptorPackageNode descriptorPackageNodeNamed(String name) {
		for (Iterator stream = this.descriptorPackageNodes(); stream.hasNext(); ) {
			DescriptorPackageNode descriptorPackageNode = (DescriptorPackageNode) stream.next();
			if (descriptorPackageNode.getName().equals(name)) {
				return descriptorPackageNode;
			}
		}
		return null;
	}

    public DescriptorNode descriptorNodeFor(MWDescriptor descriptor) {
        return descriptorPackageNodeNamed(descriptor.packageName()).descriptorNodeFor(descriptor);
    }
    
	public abstract boolean supportsExportProjectJavaSource();
	
	public abstract boolean supportsExportTableCreatorJavaSource();
	
	protected DescriptorNodeBuilder getDescriptorNodeBuilder() {
		return this.descriptorNodeBuilder;
	}


	// ********** behavior **********

    public void selectDescriptorNodeFor(MWDescriptor descriptor, NavigatorSelectionModel navigatorSelectionModel) {
        selectDescriptorNode(descriptorNodeFor(descriptor), navigatorSelectionModel);
    }
    
    public void selectDescriptorNode(DescriptorNode descriptorNode, NavigatorSelectionModel navigatorSelectionModel) {
        navigatorSelectionModel.setSelectedNode(descriptorNode);
        
    }

    public void selectMappingNodeFor(MWMapping mapping, NavigatorSelectionModel nsm) {
        MappingDescriptorNode descriptorNode = (MappingDescriptorNode) descriptorNodeFor(mapping.getParentDescriptor());
        descriptorNode.selectMappingNodeFor(mapping, nsm);    
    }
    
    public void selectMappingNodeFor(MWClassAttribute attribute, NavigatorSelectionModel nsm) {
        MappingDescriptorNode descriptorNode = (MappingDescriptorNode) descriptorNodeFor(getProject().descriptorForType((MWClass) attribute.getParent()));
        descriptorNode.selectMappingNodeFor(attribute, nsm);
    }
    
    public void selectMethod(MWMethod method, WorkbenchContext context) {
        MWDescriptor descriptor = getProject().descriptorForType((MWClass) method.getParent());
        if (descriptor != null) {
            MappingDescriptorNode descriptorNode = (MappingDescriptorNode) descriptorNodeFor(descriptor);
            selectDescriptorNode(descriptorNode, context.getNavigatorSelectionModel());
            descriptorNode.selectMethod(method, context);
        }
    }
    
	/**
	 * this allows a client to temporarily disable our prompting the user
	 * to update the ejb-jar.xml file
	 */
	public void setPromptsForEjbJarXmlWrite(boolean b) {
		this.promptsForEjbJarXmlWrite = b;
	}

	protected abstract DescriptorPackageNode buildDescriptorPackageNodeFor(MWDescriptor descriptor);

	void addDescriptorNodesFor(Iterator descriptors) {
		while (descriptors.hasNext()) {
			this.addDescriptorNodeFor((MWDescriptor) descriptors.next());
		}
	}

	private void addDescriptorNodeFor(MWDescriptor descriptor) {
		descriptor.addPropertyChangeListener(MWDescriptor.NAME_PROPERTY, this.descriptorNameListener);
		this.addDescriptorNodeTo(descriptor, this.descriptorPackageNodeFor(descriptor));
	}

	private void addDescriptorNodeTo(MWDescriptor descriptor, DescriptorPackageNode dpn) {
		if (dpn == null) {
			dpn = this.buildDescriptorPackageNodeFor(descriptor);
			this.descriptorPackageNodesHolder.addItem(dpn);
		}
		dpn.addDescriptorNodeFor(descriptor);
	}

	void removeDescriptorNodesFor(Iterator descriptors) {
		while (descriptors.hasNext()) {
			this.removeDescriptorNodeFor((MWDescriptor) descriptors.next());
		}
	}

	private void removeDescriptorNodeFor(MWDescriptor descriptor) {
		this.removeDescriptorNodeFrom(descriptor, this.descriptorPackageNodeFor(descriptor));
		descriptor.removePropertyChangeListener(MWDescriptor.NAME_PROPERTY, this.descriptorNameListener);
	}

	private void removeDescriptorNodeFrom(MWDescriptor descriptor, DescriptorPackageNode dpn) {
		dpn.removeDescriptorNodeFor(descriptor);
		if (dpn.descriptorNodesSize() == 0) {
			this.descriptorPackageNodesHolder.removeItem(dpn);
		}
	}

	void rebuildDescriptorNodes() {
		Collection descriptors = CollectionTools.list(this.descriptors());	// make a copy before removing them
		this.removeDescriptorNodesFor(descriptors.iterator());
		this.addDescriptorNodesFor(this.getProject().descriptors());
	}

	void descriptorChangedPackage(MWDescriptor descriptor, String oldPackageName, String newPackageName) {
		this.removeDescriptorNodeFrom(descriptor, this.descriptorPackageNodeNamed(oldPackageName));
		// keep listening to the descriptor's name property
		this.addDescriptorNodeTo(descriptor, this.descriptorPackageNodeNamed(newPackageName));
	}


	// ********** nested interface **********

	/**
	 * This interface defines the information a ProjectNode requires
	 * of its children so it can sort them.
	 */
	public interface Child extends Comparable {

		/**
		 * Return a priority that can be used to sort the nodes under
		 * a ProjectNode. Nodes of the same priority will be compared
		 * directly, via java.util.Comparable#compareTo(Object).
		 */
		int getProjectNodeChildPriority();

		Comparator COMPARATOR =
			new Comparator() {
				public int compare(Object o1, Object o2) {
					int priority1 = ((Child) o1).getProjectNodeChildPriority();
					int priority2 = ((Child) o2).getProjectNodeChildPriority();
					if (priority1 == priority2) {
						return ((Comparable) o1).compareTo(o2);
					}
					return priority1 - priority2;
				}
				public String toString() {
					return "ProjectNode.Child.COMPARATOR";
				}
			};

	}

	private class LocalFileChooser extends JFileChooser {
		private WorkbenchContext workbenchContext;

		private LocalFileChooser(WorkbenchContext workbenchContext) {
			super();
			this.workbenchContext = workbenchContext;
		}

		/**
		 * Determines whether the selected file can be used as the new location to
		 * persist the document.
		 */
		public void approveSelection() {
			int result = canReplaceExistingFile();

			if (result == JOptionPane.YES_OPTION)
				super.approveSelection();
			else if (result == JOptionPane.CANCEL_OPTION)
				cancelSelection();
		}

		private ResourceRepository resourceRepository() {
			return this.workbenchContext.getApplicationContext().getResourceRepository();
		}

		/**
		 * Verifies if the file (which is currently selected) can be replaced with
		 * the document to be saved.
		 *
		 * @return <code>JOptionPane.YES_OPTION</code> if the file is not
		 * Read-Only, not opened or can be replaced, <code>JOptionPane.NO_OPTION</code>
		 * if the document can't be saved because the selected file is Read-Only
		 * or the user said no to replace it, or <code><code>JOptionPane.NO_OPTION</code></code>
		 * if the user does not want to replace the file and canceled the confirm
		 * dialog
		 */
		private int canReplaceExistingFile() {
			File file = getSelectedFile();
			String applicationName = this.workbenchContext.getApplicationContext().getApplication().getShortProductName();

			// The file is actually opened
			if (this.isDocumentOpened(file)) {

				String message = this.resourceRepository().getString("SAVE_AS_DIALOG_ALREADY_OPENED", applicationName, file, StringTools.CR);
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				JOptionPane.showMessageDialog (
					this.workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.WARNING_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			// The file exist but is marked as Read-Only, show we can't save it
			if (file.exists() && !file.canWrite()) {
				String message = this.resourceRepository().getString("SAVE_AS_DIALOG_CANT_SAVE", file, StringTools.CR);
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				JOptionPane.showMessageDialog (
				this.workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.WARNING_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			// The file exists and is the file to save, ask to replace it
			if ((saveFile() != null) &&
			     file.exists() &&
			     saveFile().equals(file)) {

				String message = this.resourceRepository().getString("SAVE_AS_DIALOG_REPLACE", getSelectedFile().getPath());
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				return JOptionPane.showConfirmDialog (
					this.workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.YES_NO_CANCEL_OPTION
				);
			}

			// The directory contains another TopLink Map project
			if (!this.isValidSaveDirectory(file.getParentFile())) {
				String message = this.resourceRepository().getString("NEED_EMPTY_DIRECTORY_TO_SAVE_DIALOG.message");
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				JOptionPane.showMessageDialog (
					this.workbenchContext.getCurrentWindow(),
					label,
					this.resourceRepository().getString("NEED_EMPTY_DIRECTORY_TO_SAVE_DIALOG.title"),
					JOptionPane.ERROR_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			return JOptionPane.YES_OPTION;
		}

		private boolean isValidSaveDirectory(File saveDirectory) {
			File[] files = saveDirectory.listFiles();
			if (files == null || files.length == 0) {
				return true;	// an empty directory is OK
			}
			for (int i = files.length; i-- > 0; ) {
				File file = files[i];
				// if the file is not a directory and has the extension .mwp we disallow the save
				if ( ! file.isDirectory() && file.getName().endsWith(MWProject.FILE_NAME_EXTENSION)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Determines whether a file with the given path is already opened. The
		 * location of this document is not considered during the check.
		 */
		private boolean isDocumentOpened(File file) {
			ApplicationNode[] appNodes = nodeManager().projectNodesFor(getPlugin());

			for (int i = 0; i < appNodes.length; i++) {
				ProjectNode node = (ProjectNode) appNodes[i];

				// This is this node, continue
				if (node == ProjectNode.this) {
					continue;
				}

				File anotherOpenedFile = node.saveFile();

				if ((anotherOpenedFile != null) &&
				    file.equals(anotherOpenedFile)) {
					return true;
				}
			}

			return false;
		}
	}

}
