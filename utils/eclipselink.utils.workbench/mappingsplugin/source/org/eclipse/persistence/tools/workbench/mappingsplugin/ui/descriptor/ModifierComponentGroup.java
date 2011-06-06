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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifiable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifier;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueStatePropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;



final class ModifierComponentGroup {
	private ApplicationContext context;

	Verifier verifier;
	private PropertyValueModel modifierHolder;
	private ValueModel allowedModifiersHolder;
	private PropertyValueModel accessLevelHolder;

	// access modifiers
	private JPanel accessModifierPanel;
	JRadioButton publicAccessRadioButton;
	JRadioButton protectedAccessRadioButton;
	JRadioButton defaultAccessRadioButton;
	JRadioButton privateAccessRadioButton;

	// other modifiers
	private JPanel otherModifiersPanel;
	JCheckBox abstractCheckBox;
	JCheckBox finalCheckBox;
	JCheckBox nativeCheckBox;
	JCheckBox staticCheckBox;
	JCheckBox synchronizedCheckBox;
	JCheckBox transientCheckBox;
	JCheckBox volatileCheckBox;


	ModifierComponentGroup(Verifier verifier, ValueModel modifiableHolder, ApplicationContext context) {
		super();
		this.context = context;
		this.verifier = verifier;
		this.modifierHolder = this.buildModifierAdapter(modifiableHolder);
		this.allowedModifiersHolder = this.buildAllowedModifiersAdapter();
		this.accessLevelHolder = this.buildAccessLevelAdapter();
	}


	// ********** initialization **********

	protected PropertyValueModel buildModifierAdapter(ValueModel modifiableHolder) {
		return new PropertyAspectAdapter(modifiableHolder) {
			protected Object getValueFromSubject() {
				return ((MWModifiable) this.subject).getModifier();
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierAdapter(ValueModel)";
			}
		};
	}

	private PropertyValueModel buildAllowedModifiersAdapter() {
		return new ValueStatePropertyValueModelAdapter(this.modifierHolder);
	}

	private PropertyValueModel buildAccessLevelAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.ACCESS_LEVEL_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWModifier) this.subject).getAccessLevel();
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setAccessLevel((String) value);
			}
			public String toString() {
				return "ModifierComponentGroup.buildAccessLevelAdapter()";
			}
		};
	}


	// ********** access modifiers panel **********
	
	JPanel getAccessModifiersPanel() {
		if (this.accessModifierPanel == null) {
			this.accessModifierPanel = this.buildAccessModifiersPanel();
		}
		return this.accessModifierPanel;
	}

	private JPanel buildAccessModifiersPanel() {
		JPanel panel = new TitledPanel(this.resourceRepository().getString("accessModifiers"));
		panel.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(this.resourceRepository().getString("accessModifiers")),
					BorderFactory.createEmptyBorder(0, 0, 5, 5)
				)
			);
		return panel;
	}

	private ButtonModel buildAccessLevelRadioButtonModelAdapter(String accessLevel) {
		return new RadioButtonModelAdapter(this.accessLevelHolder, accessLevel);
	}


	// ********** public **********

	JRadioButton getPublicAccessRadioButton() {
		if (this.publicAccessRadioButton == null) {
			this.publicAccessRadioButton = this.buildPublicAccessRadioButton();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierPublicListener());
		}
		return this.publicAccessRadioButton;
	}
	
	private JRadioButton buildPublicAccessRadioButton() {
		JRadioButton rb = SwingComponentFactory.buildRadioButton(
											"PUBLIC_ACCESS_MODIFIER",
											this.buildAccessLevelRadioButtonModelAdapter(MWModifier.PUBLIC),
											this.resourceRepository()
									);
		rb.setEnabled(this.publicShouldBeEnabled());
		return rb;
	}

	private PropertyChangeListener buildModifierPublicListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.publicAccessRadioButton.setEnabled(ModifierComponentGroup.this.publicShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierPublicListener()";
			}
		};
	}

	boolean publicShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetPublic();
	}


	// ********** protected **********

	JRadioButton getProtectedAccessRadioButton() {
		if (this.protectedAccessRadioButton == null) {
			this.protectedAccessRadioButton = this.buildProtectedAccessRadioButton();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierProtectedListener());
		}
		return this.protectedAccessRadioButton;
	}
	
	private JRadioButton buildProtectedAccessRadioButton() {
		JRadioButton rb = SwingComponentFactory.buildRadioButton(
											"PROTECTED_ACCESS_MODIFIER",
											this.buildAccessLevelRadioButtonModelAdapter(MWModifier.PROTECTED),
											this.resourceRepository()
									);
		rb.setEnabled(this.protectedShouldBeEnabled());
		return rb;
	}

	private PropertyChangeListener buildModifierProtectedListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.protectedAccessRadioButton.setEnabled(ModifierComponentGroup.this.protectedShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierProtectedListener()";
			}
		};
	}

	boolean protectedShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetProtected();
	}


	// ********** default **********

	JRadioButton getDefaultAccessRadioButton() {
		if (this.defaultAccessRadioButton == null) {
			this.defaultAccessRadioButton = this.buildDefaultAccessRadioButton();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierDefaultListener());
		}
		return this.defaultAccessRadioButton;
	}		
		
	private JRadioButton buildDefaultAccessRadioButton() {
		JRadioButton rb = SwingComponentFactory.buildRadioButton(
											"DEFAULT_ACCESS_MODIFIER",
											this.buildAccessLevelRadioButtonModelAdapter(MWModifier.PACKAGE),
											this.resourceRepository()
									);
		rb.setEnabled(this.defaultShouldBeEnabled());
		return rb;
	}

	private PropertyChangeListener buildModifierDefaultListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.defaultAccessRadioButton.setEnabled(ModifierComponentGroup.this.defaultShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierDefaultListener()";
			}
		};
	}

	boolean defaultShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetPackage();
	}


	// ********** private **********

	JRadioButton getPrivateAccessRadioButton() {
		if (this.privateAccessRadioButton == null) {
			this.privateAccessRadioButton = this.buildPrivateAccessRadioButton();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierPrivateListener());
		}
		return this.privateAccessRadioButton;
	}

	private JRadioButton buildPrivateAccessRadioButton() {
		JRadioButton rb = SwingComponentFactory.buildRadioButton(
											"PRIVATE_ACCESS_MODIFIER",
											this.buildAccessLevelRadioButtonModelAdapter(MWModifier.PRIVATE),
											this.resourceRepository()
									);
		rb.setEnabled(this.privateShouldBeEnabled());
		return rb;
	}

	private PropertyChangeListener buildModifierPrivateListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.privateAccessRadioButton.setEnabled(ModifierComponentGroup.this.privateShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierPrivateListener()";
			}
		};
	}

	boolean privateShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetPrivate();
	}


	// ********** other modifiers panel **********
	
	JPanel getOtherModifiersPanel() {
		if (this.otherModifiersPanel == null) {
			this.otherModifiersPanel = this.buildOtherModifiersPanel();
		}
		return this.otherModifiersPanel;
	}

	private JPanel buildOtherModifiersPanel() {
		JPanel panel = new TitledPanel(this.resourceRepository().getString("otherModifiers"));
		panel.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(this.resourceRepository().getString("otherModifiers")),
					BorderFactory.createEmptyBorder(0, 0, 5, 5)
				)
			);
		return panel;
	}


	// ********** abstract **********
	
	JCheckBox getAbstractCheckBox() {
		if (this.abstractCheckBox == null) {
			this.abstractCheckBox = this.buildAbstractCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierAbstractListener());
		}
		return this.abstractCheckBox;
	}
	
	private JCheckBox buildAbstractCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"ABSTRACT_MODIFIER",
									new CheckBoxModelAdapter(this.buildAbstractAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.abstractShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildAbstractAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isAbstract());
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setAbstract(((Boolean) value).booleanValue());
			}
			public String toString() {
				return "ModifierComponentGroup.buildAbstractAdapter()";
			}
		};
	}

	private PropertyChangeListener buildModifierAbstractListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.abstractCheckBox.setEnabled(ModifierComponentGroup.this.abstractShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierAbstractListener()";
			}
		};
	}

	boolean abstractShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetAbstract();
	}


	// ********** final **********

	JCheckBox getFinalCheckBox() {
		if (this.finalCheckBox == null) {
			this.finalCheckBox = this.buildFinalCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierFinalListener());
		}
		return this.finalCheckBox;
	}
	
	private JCheckBox buildFinalCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"FINAL_MODIFIER",
									new CheckBoxModelAdapter(this.buildFinalAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.finalShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildFinalAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isFinal());
			}
			protected void setValueOnSubject(Object value) {
				boolean finalFlag = ((Boolean) value).booleanValue();
				// verify the change
				if (ModifierComponentGroup.this.verifier.verifyFinalChange(finalFlag)) {
					((MWModifier) this.subject).setFinal(finalFlag);
				} else {
					// revert the check-box
					ModifierComponentGroup.this.getFinalCheckBox().setSelected( ! finalFlag);
				}
			}
			public String toString() {
				return "ModifierComponentGroup.buildFinalAdapter()";
			}
		};
	}
	
	private PropertyChangeListener buildModifierFinalListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.finalCheckBox.setEnabled(ModifierComponentGroup.this.finalShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierFinalListener()";
			}
		};
	}

	boolean finalShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetFinal();
	}

	
	// ********** native **********

	JCheckBox getNativeCheckBox() {
		if (this.nativeCheckBox == null) {
			this.nativeCheckBox = this.buildNativeCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierNativeListener());
		}
		return this.nativeCheckBox;
	}

	private JCheckBox buildNativeCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"NATIVE_MODIFIER",
									new CheckBoxModelAdapter(this.buildNativeAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.nativeShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildNativeAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isNative());
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setNative(((Boolean) value).booleanValue());
			}
			public String toString() {
				return "ModifierComponentGroup.buildNativeAdapter()";
			}
		};
	}

	private PropertyChangeListener buildModifierNativeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.nativeCheckBox.setEnabled(ModifierComponentGroup.this.nativeShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierNativeListener()";
			}
		};
	}

	boolean nativeShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetNative();
	}

	
	// ********** static **********

	JCheckBox getStaticCheckBox() {
		if (this.staticCheckBox == null) {
			this.staticCheckBox = this.buildStaticCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierStaticListener());
		}
		return this.staticCheckBox;
	}
	
	private JCheckBox buildStaticCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"STATIC_MODIFIER",
									new CheckBoxModelAdapter(this.buildStaticAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.staticShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildStaticAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isStatic());
			}
			protected void setValueOnSubject(Object value) {
				boolean staticFlag = ((Boolean) value).booleanValue();
				// verify the change
				if (ModifierComponentGroup.this.verifier.verifyStaticChange(staticFlag)) {
					((MWModifier) this.subject).setStatic(staticFlag);
				} else {
					// revert the check-box
					ModifierComponentGroup.this.getStaticCheckBox().setSelected( ! staticFlag);
				}
			}
			public String toString() {
				return "ModifierComponentGroup.buildStaticAdapter()";
			}
		};
	}
	
	private PropertyChangeListener buildModifierStaticListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.staticCheckBox.setEnabled(ModifierComponentGroup.this.staticShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierStaticListener()";
			}
		};
	}

	boolean staticShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetStatic();
	}

	
	// ********** synchronized **********
	
	JCheckBox getSynchronizedCheckBox() {
		if (this.synchronizedCheckBox == null) {
			this.synchronizedCheckBox = this.buildSynchronizedCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierSynchronizedListener());
		}
		return this.synchronizedCheckBox;
	}

	private JCheckBox buildSynchronizedCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"SYNCHRONIZED_MODIFIER",
									new CheckBoxModelAdapter(this.buildSynchronizedAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.synchronizedShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildSynchronizedAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isSynchronized());
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setSynchronized(((Boolean) value).booleanValue());
			}
			public String toString() {
				return "ModifierComponentGroup.buildSynchronizedAdapter()";
			}
		};
	}

	private PropertyChangeListener buildModifierSynchronizedListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.synchronizedCheckBox.setEnabled(ModifierComponentGroup.this.synchronizedShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierSynchronizedListener()";
			}
		};
	}

	boolean synchronizedShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetSynchronized();
	}

	
	// ********** transient **********
	
	JCheckBox getTransientCheckBox() {
		if (this.transientCheckBox == null) {
			this.transientCheckBox = this.buildTransientCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierTransientListener());
		}
		return this.transientCheckBox;
	}

	private JCheckBox buildTransientCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"TRANSIENT_MODIFIER",
									new CheckBoxModelAdapter(this.buildTransientAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.transientShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildTransientAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isTransient());
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setTransient(((Boolean) value).booleanValue());
			}
			public String toString() {
				return "ModifierComponentGroup.buildTransientAdapter()";
			}
		};
	}

	private PropertyChangeListener buildModifierTransientListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.transientCheckBox.setEnabled(ModifierComponentGroup.this.transientShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierTransientListener()";
			}
		};
	}

	boolean transientShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetTransient();
	}

	
	// ********** volatile **********
	
	JCheckBox getVolatileCheckBox() {
		if (this.volatileCheckBox == null) {
			this.volatileCheckBox = this.buildVolatileCheckBox();
			this.allowedModifiersHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildModifierVolatileListener());
		}
		return this.volatileCheckBox;
	}

	private JCheckBox buildVolatileCheckBox() {
		JCheckBox cb = SwingComponentFactory.buildCheckBox(
									"VOLATILE_MODIFIER",
									new CheckBoxModelAdapter(this.buildVolatileAdapter()),
									this.resourceRepository()
								);
		cb.setEnabled(this.volatileShouldBeEnabled());
		return cb;
	}

	private PropertyValueModel buildVolatileAdapter() {
		return new PropertyAspectAdapter(this.modifierHolder, MWModifier.CODE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWModifier) this.subject).isVolatile());
			}
			protected void setValueOnSubject(Object value) {
				((MWModifier) this.subject).setVolatile(((Boolean) value).booleanValue());
			}
			public String toString() {
				return "ModifierComponentGroup.buildVolatileAdapter()";
			}
		};
	}

	private PropertyChangeListener buildModifierVolatileListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ModifierComponentGroup.this.volatileCheckBox.setEnabled(ModifierComponentGroup.this.volatileShouldBeEnabled());
			}
			public String toString() {
				return "ModifierComponentGroup.buildModifierVolatileListener()";
			}
		};
	}

	boolean volatileShouldBeEnabled() {
		return (this.modifier() != null) && this.modifier().canBeSetVolatile();
	}


	// ********** misc **********

	private ResourceRepository resourceRepository() {
		return this.context.getResourceRepository();
	}

	private MWModifier modifier() {
		return (MWModifier) this.modifierHolder.getValue();
	}


	// ********** member classes **********

	/**
	 * In some situations we want to verify with the user that
	 * s/he wants to change a particular modifer.
	 */
	interface Verifier {
		boolean verifyFinalChange(boolean newModifierIsFinal);
		boolean verifyStaticChange(boolean newModifierIsStatic);

		Verifier NULL_INSTANCE = new Verifier() {
			public boolean verifyFinalChange(boolean newModifierIsFinal) {
				return true;
			}
			public boolean verifyStaticChange(boolean newModifierIsStatic) {
				return true;
			}
		};
	}

	private class TitledPanel extends JPanel {
		private final String title;
		TitledPanel(String title) {
			super();
			this.title = title;
		}
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();

			TitledBorder border = BorderFactory.createTitledBorder(this.title);
			Dimension borderSize = border.getMinimumSize(this);
			size.width  = Math.max(size.width, borderSize.width + 5); // +5 is to make sure there is a gap between the right border and the text
			size.height = Math.max(size.height, borderSize.height);

			return size;
		}
	}

}
