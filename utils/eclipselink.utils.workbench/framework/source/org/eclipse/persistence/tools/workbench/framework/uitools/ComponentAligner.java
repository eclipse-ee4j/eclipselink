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
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This class is responsible to set a preferred width on the registered objects
 * (either <code>JComponent</code> or <code>ComponentAligner</code>) based
 * on the widest component. Although <code>Component</code> should be accepted,
 * <code>JComponent</code> s are required because
 * {@link JComponent#setPreferredSize(java.awt.Dimension)}and
 * {@link JComponent#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)}
 * is not defined for <code>Component</code>.
 * <p>
 * Note: Apparently
 * {@link JComponent#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)}
 * will migrate to <code>Component</code> in JDK 1.5 but
 * {@link JComponent#setPreferredSize(java.awt.Dimension)} remains on
 * <code>JComponent</code>.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class ComponentAligner
{
	/**
	 * <code>true</code> if the length of every component needs to be updated
	 * when component are added or removed; <code>false</code> to add or remove
	 * the component and then at the end invoke {@link #revalidatePreferredSize()}.
	 */
	private boolean autoValidate;

	/**
	 * The utility class used to support bound properties.
	 */
	private PropertyChangeSupport changeSupport;

	/**
	 * Prevents infinite recursion when recalculating the preferred width.
	 * This happens in an hierarchy of <code>ComponentAligner</code>s. The lock
	 * has to be placed here and not in the {@link ComponentAlignerWrapper}.
	 */
	private boolean locked;

	/**
	 * The length of the widest component. If the length was not calulcated, then
	 * this value is -1.
	 */
	private int maximumWidth;

	/**
	 * The listener added to each of the components that listens only to a text
	 * change.
	 */
	private PropertyChangeListener propertyChangeListener;

	/**
	 * The collection of {@link Wrapper}s encapsulating either
	 * <code>JComponent</code>s or {@link ComponentAligner}s.
	 */
	private Collection/*<Wrapper>*/ wrappers;

	/**
	 * The property name used to listen to the change of text in a component.
	 */
	private static final String TEXT_PROPERTY = "text";

	/**
	 * Creates a new <code>ComponentAligner</code> with the autovalidating active.
	 */
	public ComponentAligner()
	{
		this(true);
	}

	/**
	 * Creates a new <code>ComponentAligner</code>.
	 *
	 * @param autoValidate <code>true</code> if the length of every component
	 * needs to be updated when component are added or removed; <code>false</code>
	 * to add or remove the component and then at the end invoke
	 * {@link #revalidatePreferredSize()} 
	 */
	public ComponentAligner(boolean autoValidate)
	{
		super();
		initialize(autoValidate);
	}

	/**
	 * Creates a new <code>ComponentAligner</code>.
	 *
	 * @param items The collection of <code>JComponent</code>s and/or
	 * <code>ComponentAligner</code>s
	 */
	public ComponentAligner(Collection components)
	{
		this();
		addAll(components);
	}

	/**
	 * Creates a new <code>ComponentAligner</code>.
	 *
	 * @param items The collection of <code>JComponent</code>s and/or
	 * <code>ComponentAligner</code>s
	 * @param autoValidate <code>true</code> if the length of every component
	 * needs to be updated when component are added or removed; <code>false</code>
	 * to add or remove the component and then at the end invoke
	 * {@link #revalidatePreferredSize()} 
	 */
	public ComponentAligner(Collection items, boolean autoValidate)
	{
		this(autoValidate);
		addAll(items);
	}

	/**
	 * Adds the given component. Its preferred width will be used along with the
	 * width of all the other components in order to get the widest component and
	 * use its width as the width for all the components.
	 * 
	 * @param componentAligner The <code>ComponentAligner</code> to be added
	 * @exception IllegalArgumentException Can't add the ComponentAligner to itself
	 */
	public void add(ComponentAligner componentAligner)
	{
		if (componentAligner == this)
			throw new IllegalArgumentException("Can't add the ComponentAligner to itself");

		Wrapper wrapper = buildWrapper(componentAligner);
		wrapper.addPropertyChangeListener(propertyChangeListener);
		wrappers.add(wrapper);

		if (!componentAligner.wrappers.isEmpty())
			revalidate();
	}

	/**
	 * Adds the given component. Its preferred width will be used along with the
	 * width of all the other componentw in order to get the widest component and
	 * use its width as the width for all the components.
	 * 
	 * @param component The component to be added
	 */
	public void add(JComponent component)
	{
		Wrapper wrapper = buildWrapper(component);
		wrapper.addPropertyChangeListener(propertyChangeListener);
		wrappers.add(wrapper);

		revalidate();
	}

	/**
	 * Adds the items contained in the given collection into this
	 * <code>ComponentAligner</code>. The preferred width of each item will be
	 * used along with the width of all the other items in order to get the
	 * widest component and use its width as the width for all the components.
	 * 
	 * @param items The collection of <code>JComponent</code>s and/or
	 * <code>ComponentAligner</code>s
	 */
	public void addAll(Collection items)
	{
		// Deactivate the auto validation while adding all the JComponents and/or
		// ComponentAligners in order to improve performance
		boolean oldAutoValidate = autoValidate;
		autoValidate = false;

		for (Iterator iter = items.iterator(); iter.hasNext();)
		{
			Object item = iter.next();

			if (item instanceof ComponentAligner)
				add((ComponentAligner) item);
			else
				add((JComponent) item);
		}

		autoValidate = oldAutoValidate;
		revalidate();
	}

   /**
	 * Adds a <code>PropertyChangeListener</code> for a specific property. The
	 * listener will be invoked only when a call on <code>firePropertyChange</code>
	 * names that specific property.
	 * 
	 * @param listener The <code>PropertyChangeListener</code> to be added
	 */
	private void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (changeSupport == null)
		    changeSupport = new SwingPropertyChangeSupport(this);

		changeSupport.addPropertyChangeListener(TEXT_PROPERTY, listener);
   }

   /**
	 * Creates a new <code>Wrapper</code> that encapsulates the given source.
	 *
	 * @param componentAligner The <code>ComponentAligner</code> to be wrapped
	 * @return A new {@link ComponentAlignerWrapper}
	 */
	private Wrapper buildWrapper(ComponentAligner componentAligner)
	{
		return new ComponentAlignerWrapper(componentAligner);
	}

	/**
	 * Creates a new <code>Wrapper</code> that encapsulates the given source.
	 *
	 * @param component The component to be wrapped
	 * @return A new {@link ComponentWrapper}
	 */
	private Wrapper buildWrapper(JComponent component)
	{
		return new ComponentWrapper(component);
	}

   /**
	 * Reports a bound property change.
	 * 
	 * @param oldValue the old value of the property (as an int)
	 * @param newValue the new value of the property (as an int)
	 */
	private void firePropertyChange(int oldValue, int newValue)
	{
		if ((changeSupport != null) && (oldValue != newValue))
		{
			changeSupport.firePropertyChange(TEXT_PROPERTY, new Integer(oldValue), new Integer(newValue));
		}
	}

	/**
	 * Returns the length of the widest component. If the length was not
	 * calulcated, then this value is -1.
	 *
	 * @return The width of the widest component or -1 if the length has not
	 * been calulated yet
	 */
	public int getMaximumWidth()
	{
		return maximumWidth;
	}

	/**
	 * Returns the preferred size by determining which component has the greatest
	 * width.
	 *
	 * @return The preferred size of this <code>ComponentAligner</code>, which is
	 * {@link #getMaximumWidth()} for the width
	 */
	private Dimension getPreferredSize()
	{
		if (maximumWidth == -1)
			recalculateWidth();

		return new Dimension(maximumWidth, 0);
	}

	/**
	 * Initializes this <code>ComponentAligner</code>.
	 *
	 * @param autoValidate <code>true</code> if the length of every component
	 * needs to be updated when component are added or removed; <code>false</code>
	 * to add or remove the component and then at the end invoke
	 * {@link #revalidatePreferredSize()} 
	 */
	private void initialize(boolean autoValidate)
	{
		this.autoValidate = autoValidate;
		this.maximumWidth = -1;
		this.propertyChangeListener = new PropertyChangeHandler();
		this.wrappers = new Vector/*<Wrapper>*/();
	}

	/**
	 * Invalidates the preferred size of the given object.
	 *
	 * @param source The source object to be invalidated
	 */
	private void invalidate(Object source)
	{
		Wrapper wrapper = retrieveWrapper(source);

		if (wrapper.isLocked())
			return;

		Dimension size = wrapper.getCachedSize();
		size.width  = 0;
		size.height = 0;

		wrapper.setPreferredSize(null);
	}

	/**
	 * Determines whether the length of each component should be set each time a
	 * component is added or removed. If the component's text is changed and
	 * {@link #autoValidate()}returns <code>true</code> then the length of each
	 * component is automatically updated. When <code>false</code> is returned,
	 * {@link #revalidatePreferredSize()}has to be called manually.
	 * 
	 * @return <code>true</code> to recalculate the length of every component
	 * when a component is either added or removed; <code>false</code> to allow
	 * all the components to be either added or removed before invoking
	 * {@link #revalidatePreferredSize()}
	 */
	public boolean isAutoValidate()
	{
		return autoValidate;
	}

	/**
	 * Updates the maximum length based on the widest component. This methods
	 * does not update the width of the components.
	 */
	private void recalculateWidth()
	{
		int width = -1;

		for (Iterator iter = wrappers(); iter.hasNext() ;)
		{
			Wrapper wrapper = (Wrapper) iter.next();
			Dimension size = wrapper.getCachedSize();

			// The preferred size has not been calculated yet
			if (size.height == 0)
			{
				Dimension newSize = wrapper.getPreferredSize();

				size.width  = newSize.width;
				size.height = newSize.height;
			}

			// Only keep the greatest width
			width = Math.max(size.width, width);
		}

		locked = true;
		setMaximumWidth(width);
		locked = false;
	}

	/**
	 * Removes the given <code>ComponentAligner</code>. Its preferred width
	 * will not be used when calculating the widest component.
	 * 
	 * @param componentAligner The <code>componentAligner</code> to be removed
	 */
	public void remove(ComponentAligner componentAligner)
	{
		Wrapper wrapper = retrieveWrapper(componentAligner);
		wrapper.removePropertyChangeListener(propertyChangeListener);
		wrappers.remove(wrapper);

		revalidate();
	}

	/**
	 * Removes the given component. Its preferred width will not be used when
	 * calculating the widest component.
	 * 
	 * @param component The component to be removed
	 */
	public void remove(JComponent component)
	{
		Wrapper wrapper = retrieveWrapper(component);
		wrapper.removePropertyChangeListener(propertyChangeListener);
		wrappers.remove(wrapper);

		revalidate();
	}

   /**
	 * Removes the given <code>PropertyChangeListener</code>.
	 * 
	 * @param listener The <code>PropertyChangeListener</code> to be removed
	 */
	private void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(TEXT_PROPERTY, listener);

		if (!changeSupport.hasListeners(TEXT_PROPERTY))
			changeSupport = null;
	}

	/**
	 * Retrieves the <code>Wrapper</code> that is encapsulating the given object.
	 *
	 * @param source Either a <code>JComponent</code> or a <code>ComponentAligner</code>
	 * @return Its <code>Wrapper</code>
	 */
	private Wrapper retrieveWrapper(Object source)
	{
		for (Iterator iter = wrappers(); iter.hasNext(); )
		{
			Wrapper wrapper = (Wrapper) iter.next();

			if (wrapper.getSource() == source)
				return wrapper;
		}

		throw new IllegalArgumentException("Can't retrieve the Wrapper for " + source);
	}

	/**
	 * If the count of component is greater than one and {@link #autoValidate()}
	 * returns <code>true<code>, then the preferred size of all the registered
	 * <code>JComponent</code>s will be udpated.
	 */
	private void revalidate()
	{
		if (isAutoValidate())
			revalidatePreferredSize();
	}

	/**
	 * Updates the preferred size of every component based on the widest
	 * component.
	 */
	public void revalidatePreferredSize()
	{
		recalculateWidth();
		revalidatePreferredSizeImp();
	}

	/**
	 * Updates the preferred size of every component based on the widest
	 * component.
	 */
	private void revalidatePreferredSizeImp()
	{
		// Set the preferred width for every component
		for (Iterator iter = wrappers(); iter.hasNext() ;)
		{
			Wrapper wrapper = (Wrapper) iter.next();
			Dimension size = wrapper.getCachedSize();

			size = new Dimension(maximumWidth, size.height);

			wrapper.setPreferredSize(size);
		}
	}

	/**
	 * Determines whether the length of each component should be set each time a
	 * component is added or removed. If the component's text is changed and
	 * {@link #autoValidate()}returns <code>true</code> then the length of each
	 * component is automatically updated. When <code>false</code> is returned,
	 * {@link #revalidatePreferredSize()}has to be called manually.
	 * 
	 * @param autoValidate <code>true</code> if the length of every component
	 * needs to be updated when components are added or removed; <code>false</code>
	 * to add or remove the component and then at the end invoke
	 * {@link #revalidatePreferredSize()}
	 */
	public void setAutoValidate(boolean autoValidate)
	{
		boolean oldAutoValidate = isAutoValidate();
		this.autoValidate = autoValidate;

		if (!oldAutoValidate)
			revalidate();
	}

	/**
	 * Sets the length of the widest component. If the length was not calulcated,
	 * then this value is -1.
	 * 
	 * @param maximumWidth The width of the widest component
	 */
	private void setMaximumWidth(int maximumWidth)
	{
		int oldMaximumWidth = getMaximumWidth();
		this.maximumWidth = maximumWidth;
		firePropertyChange(oldMaximumWidth, maximumWidth);
	}

	/**
	 * Returns an iterator over the set of <code>Component</code>s.
	 *
	 * @return The iterator of <code>Component</code>s
	 */
	private Iterator/*<Wrapper>*/ wrappers()
	{
		return wrappers.iterator();
	}

	/**
	 * This <code>Wrapper</code> encapsulates a {@link ComponentAligner}.
	 */
	private class ComponentAlignerWrapper implements Wrapper
	{
		/**
		 * The cached size, which is {@link ComponentAligner#maximumWidth}.
		 */
		private Dimension cachedSize;

		/**
		 * The <code>ComponentAligner</code> encapsulated by this
		 * <code>Wrapper</code>.
		 */
		private final ComponentAligner componentAligner;

		/**
		 * Creates a new <code>ComponentAlignerWrapper</code> that encapsulates
		 * the given <code>ComponentAligner</code>.
		 * 
		 * @param componentAligner The <code>ComponentAligner</code> to be
		 * encapsulated by this <code>Wrapper</code>
		 */
		private ComponentAlignerWrapper(ComponentAligner componentAligner)
		{
			super();

			this.componentAligner = componentAligner;
			cachedSize = new Dimension(componentAligner.maximumWidth, 0);
		}

		/**
		 * Adds a <code>PropertyChangeListener</code> for a specific property.
		 * The listener will be invoked only when a call on
		 * <code>firePropertyChange</code> names that specific property.
		 *
		 * @param listener The <code>PropertyChangeListener</code> to be added
		 */
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			componentAligner.addPropertyChangeListener(listener);
		}

		/**
		 * Returns the cached size of the encapsulated source.
		 *
		 * @return A non-<code>null</code> size
		 */
		public Dimension getCachedSize()
		{
			return cachedSize;
		}

		/**
		 * Returns the preferred size of the encapsulated source.
		 *
		 * @return The preferred size
		 */
		public Dimension getPreferredSize()
		{
			return componentAligner.getPreferredSize();
		}

		/**
		 * Returns the encapsulated object.
		 *
		 * @return A <code>ComponentAligner</code>
		 */
		public Object getSource()
		{
			return componentAligner;
		}

		/**
		 * Prevents infinite recursion when recalculating the preferred width.
		 * This happens in an hierarchy of <code>ComponentAligner</code>s.
		 *
		 * @return <code>true<code> to prevent this <code>Wrapper</code> from
		 * being invalidated; otherwise <code>false<code>
		 */
		public boolean isLocked()
		{
			return componentAligner.locked;
		}

		/**
		 * Removes a <code>PropertyChangeListener</code> for a specific property.
		 * If listener is <code>null</code>, no exception is thrown and no
		 * action is performed.
		 * 
		 * @param listener The <code>PropertyChangeListener</code> to be removed
		 */
		public void removePropertyChangeListener(PropertyChangeListener listener)
		{
			componentAligner.removePropertyChangeListener(listener);
		}

		/**
		 * Sets the preferred size on the encapsulated source.
		 *
		 * @param preferredSize The new preferred size
		 */
		public void setPreferredSize(Dimension preferredSize)
		{
			if (preferredSize == null)
			{
				componentAligner.maximumWidth = -1;
			}
			else if (componentAligner.maximumWidth != preferredSize.width)
			{
				componentAligner.maximumWidth = preferredSize.width;
				componentAligner.revalidatePreferredSizeImp();
			}
		}
	}

	/**
	 * This <code>Wrapper</code> encapsulates a {@link JComponent}.
	 */
	private class ComponentWrapper implements Wrapper
	{
		/**
		 * The cached size, which is component's preferred size.
		 */
		private Dimension cachedSize;

		/**
		 * The component to be encapsulated by this <code>Wrapper</code>.
		 */
		private final JComponent component;

		/**
		 * Creates a new <code>ComponentWrapper</code> that encapsulates the given
		 * component.
		 *
		 * @param component The component to be encapsulated by this <code>Wrapper</code>
		 */
		private ComponentWrapper(JComponent component)
		{
			super();

			this.component = component;
			cachedSize = new Dimension();
		}

		/**
		 * Adds a <code>PropertyChangeListener</code> for a specific property.
		 * The listener will be invoked only when a call on
		 * <code>firePropertyChange</code> names that specific property.
		 * 
		 * @param listener The <code>PropertyChangeListener</code> to be added
		 */
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			component.addPropertyChangeListener(TEXT_PROPERTY, listener);
		}

		/**
		 * Returns the cached size of the encapsulated source.
		 *
		 * @return A non-<code>null</code> size
		 */
		public Dimension getCachedSize()
		{
			return cachedSize;
		}

		/**
		 * Returns the preferred size of the encapsulated source.
		 *
		 * @return The preferred size
		 */
		public Dimension getPreferredSize()
		{
			return component.getPreferredSize();
		}

		/**
		 * Returns the encapsulated object.
		 *
		 * @return A <code>JComponent</code>
		 */
		public Object getSource()
		{
			return component;
		}

	   /**
		 * Prevents infinite recursion when recalculating the preferred width.
		 * This happens in an hierarchy of <code>ComponentAligner</code>s.
		 *
		 * @return <code>false<code> is always returned for a <code>JComponent</code>
		 */
		public boolean isLocked()
		{
			return false;
		}

		/**
		 * Removes a <code>PropertyChangeListener</code> for a specific property.
		 * If listener is <code>null</code>, no exception is thrown and no
		 * action is performed.
		 * 
		 * @param propertyName The name of the property that was listened on
		 * @param listener The <code>PropertyChangeListener</code> to be removed
		 */
		public void removePropertyChangeListener(PropertyChangeListener listener)
		{
			component.removePropertyChangeListener(TEXT_PROPERTY, listener);
		}

		/**
		 * Sets the preferred size on the encapsulated source.
		 *
		 * @param preferredSize The new preferred size
		 */
		public void setPreferredSize(Dimension preferredSize)
		{
			component.setPreferredSize(preferredSize);
		}
	}

	/**
	 * The listener added to each of the component that listens only to a text
	 * change.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			invalidate(e.getSource());
			revalidate();
		}
	}

	/**
	 * This <code>Wrapper</code> helps to encapsulate heterogeneous objects and
	 * apply the same behavior on them.
	 */
	private interface Wrapper
	{
	   /**
		 * Adds a <code>PropertyChangeListener</code> for a specific property.
		 * The listener will be invoked only when a call on
		 * <code>firePropertyChange</code> names that specific property.
		 * 
		 * @param listener The <code>PropertyChangeListener</code> to be added
		 */
		public void addPropertyChangeListener(PropertyChangeListener listener);

		/**
		 * Returns the cached size of the encapsulated source.
		 *
		 * @return A non-<code>null</code> size
		 */
		public Dimension getCachedSize();

		/**
		 * Returns the preferred size of the encapsulated source.
		 *
		 * @return The preferred size
		 */
		public Dimension getPreferredSize();

		/**
		 * Returns the encapsulated object.
		 *
		 * @return The object that is been wrapped
		 */
		public Object getSource();

		/**
		 * Prevents infinite recursion when recalculating the preferred width.
		 * This happens in an hierarchy of <code>ComponentAligner</code>s.
		 *
		 * @return <code>true<code> to prevent this <code>Wrapper</code> from
		 * being invalidated; otherwise <code>false<code>
		 */
		public boolean isLocked();

		/**
		 * Removes a <code>PropertyChangeListener</code> for a specific property.
		 * If listener is <code>null</code>, no exception is thrown and no
		 * action is performed.
		 * 
		 * @param listener The <code>PropertyChangeListener</code> to be removed
		 */
		public void removePropertyChangeListener(PropertyChangeListener listener);

		/**
		 * Sets the preferred size on the encapsulated source.
		 *
		 * @param preferredSize The new preferred size
		 */
		public void setPreferredSize(Dimension preferredSize);
	}
}
