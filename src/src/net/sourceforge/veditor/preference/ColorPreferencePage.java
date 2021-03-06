/*******************************************************************************
 * Copyright (c) 2004, 2006 KOBAYASHI Tadashi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    KOBAYASHI Tadashi - initial API and implementation
 *******************************************************************************/
package net.sourceforge.veditor.preference;

import net.sourceforge.veditor.VerilogPlugin;
import net.sourceforge.veditor.editor.HdlTextAttribute;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * Color Preference page
 */
public class ColorPreferencePage extends AbstractPreferencePage
{
	public ColorPreferencePage()
	{
	}

    private List colorList;
	private ColorSelector colorSelector;
	private Button boldButton;
    private Button italicButton;
    private FieldListener fieldListener = new FieldListener();

    private class TextAttribute
	{
		public String key;
		public String label;
		
		public TextAttribute(String key, String label)
		{
			this.key = key;
			this.label = label;
		}
		public void updateFields()
		{
			RGB color = VerilogPlugin.getPreferenceColor("Color." + key);
			colorSelector.setColorValue(color);
			
			boolean bold = VerilogPlugin.getPreferenceBoolean("Bold." + key);
			boldButton.setSelection(bold);
			
			boolean italic = VerilogPlugin.getPreferenceBoolean("Italic." + key );
			italicButton.setSelection(italic);
		}
		public void updatePreference()
		{
			RGB color = colorSelector.getColorValue();
			VerilogPlugin.setPreference("Color." + key, color);
			
			boolean bold = boldButton.getSelection();
			VerilogPlugin.setPreference("Bold." + key, bold);

			boolean italic = italicButton.getSelection();
			VerilogPlugin.setPreference("Italic." + key, italic);
		}
		public void loadDefault()
		{
			VerilogPlugin.setDefaultPreference("Color." + key);
			VerilogPlugin.setDefaultPreference("Bold." + key);
			VerilogPlugin.setDefaultPreference("Italic." + key);
		}
	}
    private TextAttribute[] attributes = {
			new TextAttribute("Default", "Default"),
			new TextAttribute("SingleLineComment", "Single line comment"),
			new TextAttribute("MultiLineComment", "Multi line comment"),
			new TextAttribute("DoxygenComment", "Doxygen comment"),
			new TextAttribute("KeyWord", "Reserved word"),
			new TextAttribute("String", "String"),
			new TextAttribute("Directive", "Directive"),
			new TextAttribute("Types", "Types"),
			new TextAttribute("AutoTasks", "Auto Tasks (FIXME,TODO,etc)")};

    protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);

		createColorSection(composite);

		// initializeValues();
		return composite;
	}

    private void createColorSection(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		colorList = new List(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		Composite attrField = new Composite(parent, SWT.NONE);
		attrField.setLayout(new GridLayout(2, false));

		new Label(attrField, SWT.NONE).setText("Color:");
		colorSelector = new ColorSelector(attrField);
		new Label(attrField, SWT.NONE).setText("Bold:");
		boldButton = new Button(attrField, SWT.CHECK);
		new Label(attrField, SWT.NONE).setText("Italic:");
		italicButton = new Button(attrField, SWT.CHECK);
		attributes[0].updateFields();

		String labels[] = new String[attributes.length];
		for (int i = 0; i < attributes.length; i++)
		{
			labels[i] = attributes[i].label;
			//itemList.setItem(i, attributes[i].label);
		}
		colorList.setItems(labels);
		colorList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int idx = colorList.getSelectionIndex();
				if (idx >= 0)
					attributes[idx].updateFields();
			}
		});
		colorList.select(0);

		colorSelector.addListener(fieldListener);
		boldButton.addSelectionListener(fieldListener);
		italicButton.addSelectionListener(fieldListener);
	}
    
    private class FieldListener implements IPropertyChangeListener,
			SelectionListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			updatePreference();
		}

		public void widgetSelected(SelectionEvent e)
		{
			updatePreference();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			updatePreference();
		}

		private void updatePreference()
		{
			int idx = colorList.getSelectionIndex();
			if (idx >= 0)
				attributes[idx].updatePreference();
		}
	}

    public boolean performOk()
	{
		super.performOk();
		HdlTextAttribute.init();
		return true;
	}
    
    protected void performDefaults()
    {
    	super.performDefaults();
		for (int i = 0; i < attributes.length; i++)
		{
			attributes[i].loadDefault();
		}
		int idx = colorList.getSelectionIndex();
		if (idx >= 0)
			attributes[idx].updateFields();
    }

}

