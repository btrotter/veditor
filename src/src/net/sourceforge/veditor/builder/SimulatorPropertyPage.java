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

package net.sourceforge.veditor.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.dialogs.PropertyPage;

public class SimulatorPropertyPage extends PropertyPage
{
	private Button enableButton;
	private Text simCommand;
	private Text workFolder;
	private Text simArguments;
	private Combo errParser;

	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		enableButton = new Button(composite, SWT.CHECK);
		enableButton.setText("Enable Verilog/VHDL Builder");
		
		int style;
		Composite group;

		style = SWT.SINGLE | SWT.BORDER;
		group = createGroup(composite, 3);
		workFolder = createStringField(group, "&Working folder:", style);
		Button button = new Button(group, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new BrowseListener());

		simCommand = createStringField(group, "&Simulate command:", style);
		createNull(group);

		errParser = createCombo(group, "&Error parser");
		ErrorParser[] parsers = ErrorParser.getParsers();
		for(int i = 0; i < parsers.length; i++)
		{
			errParser.add(parsers[i].getCompilerName());
		}

		style = SWT.MULTI | SWT.BORDER | SWT.V_SCROLL;
		group = createGroup(composite, 1);
		simArguments = createStringField(group, "&Arguments:", style);
		
		initContents();
        return composite;
	}

	private Text createStringField(Composite parent, String labelText, int style)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Text text = new Text(parent, style);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		
		if ((style & SWT.MULTI) != 0)
			gd.heightHint = 150;
		text.setLayoutData(gd);

		return text;
	}

	private Combo createCombo(Composite parent, String labelText)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Combo combo = new Combo(parent, SWT.NONE);
				
		return combo;
	}
	
	private Composite createGroup(Composite parent, int column)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(column, false));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		group.setLayoutData(gd);
		return group;
	}
	private void createNull(Composite parent)
	{
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.heightHint = 0;
		gd.widthHint = 0;
		group.setLayoutData(gd);
	}
	
	private IProject getProject()
	{
		IAdaptable element = getElement();
		if (element != null && element instanceof IProject)
			return (IProject)element;
		else
			return null;
	}
	private String getProjectPath()
	{
		return "/" + getProject().getName();
	}
	
	private void initContents()
	{
		IProject project = getProject();
		HdlNature nature = new HdlNature(project);
		ICommand command = nature.getSimulateCommand();
		if (command == null)
		{
			setDefaults();
			command = nature.getSimulateCommand();
		}
		
		Map args = command.getArguments();

		String enableValue = getArg(args, "enable");
		boolean enable = enableValue.equals("true");
		enableButton.setSelection(enable);

		workFolder.setText(getArg(args, "work"));
		simCommand.setText(getArg(args, "command"));
		errParser.setText(getArg(args, "parser"));

		String linesep = System.getProperty("line.separator");
		String argstext = getArg(args, "arguments");
		simArguments.setText(argstext.replaceAll("\\\\n", linesep));
	}
	private String getArg(Map args, String name)
	{
		Object obj = args.get(name);
		if (obj == null)
			return "";
		else
			return obj.toString();
	}
	
	private void setDefaults()
	{
		IProject project = getProject();
		HdlNature nature = new HdlNature(project);
	
		ICommand command = nature.createSimulateCommand();
		Map args = new HashMap();
		args.put("enable", "false");
		args.put("work", "bench");
		args.put("command", "");
		args.put("arguments", "");
		args.put("parser", "");

		command.setArguments(args);
		nature.setSimulateCommand(command);
	}

	public boolean performOk()
	{
		super.performOk();
		IProject project = getProject();
		HdlNature nature = new HdlNature(project);
		ICommand command = nature.createSimulateCommand();

		Map args = new HashMap();
		args.put("enable", Boolean.toString(enableButton.getSelection()));
		args.put("work", workFolder.getText());
		args.put("command", simCommand.getText());
		args.put("parser", errParser.getText());
		
		String linesep = System.getProperty("line.separator");
		String argstext = simArguments.getText().replaceAll(linesep, "\\\\n");
		args.put("arguments", argstext);
		
		command.setArguments(args);
		nature.setSimulateCommand(command);
		return true;
	}

	protected void performDefaults()
	{
		super.performDefaults();
		setDefaults();
	}
	
	private class BrowseListener extends SelectionAdapter
	{
		public void widgetSelected(SelectionEvent e)
		{
			ContainerSelectionDialog dialog = new ContainerSelectionDialog(
					getShell(), getProject(), false, "Select a working folder");
			dialog.setValidator(new ISelectionValidator()
			{
				public String isValid(Object selection)
				{
					if (selection.toString().indexOf(getProjectPath()) == 0)
						return null;
					else
						return "Cannot select a folder in other projects";
				}
				
			});
			if (dialog.open() == ContainerSelectionDialog.OK)
			{
				Object[] result = dialog.getResult();
				if (result.length == 1)
				{
					String path = ((Path)result[0]).toString();
					path = path.substring(getProjectPath().length() + 1);
					workFolder.setText(path);
				}
			}
		}
	}
}


	



