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
package net.sourceforge.veditor.editor;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.veditor.VerilogPlugin;
import net.sourceforge.veditor.parser.Module;
import net.sourceforge.veditor.parser.ModuleList;
import net.sourceforge.veditor.parser.ModuleVariable;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class VerilogCompletionProcessor extends HdlCompletionProcessor
{
	public List getInModuleProposals(HdlDocument doc, int offset,
			String replace)
	{
		int length = replace.length();
		List matchList = new ArrayList();

		//  template
		if (isMatch(replace, "always"))
			matchList.add(createAlways(doc, offset, length));
		if (isMatch(replace, "initial"))
			matchList.add(createInitial(doc, offset, length));
		if (isMatch(replace, "function"))
			matchList.add(createFunction(doc, offset, length));
		if (isMatch(replace, "task"))
			matchList.add(createTask(doc, offset, length));
		if (isMatch(replace, "generate"))
			matchList.add(createGenerate(doc, offset, length));

		//  reserved word
		String[] rwords = {"assign ", "integer ", "parameter "};
		for(int i = 0 ; i < rwords.length ; i++)
		{
			if (isMatch(replace, rwords[i]))
				matchList.add(getSimpleProposal(rwords[i], offset, length));
		}

		//  module instantiation
		ModuleList mlist = ModuleList.find(doc.getProject());
		String[] mnames = mlist.getModuleNames();
		for (int i = 0; i < mnames.length; i++)
		{
			if (isMatch(replace, mnames[i]))
			{
				matchList.add(new VerilogInstanceCompletionProposal(doc,
						mnames[i], offset, length));
			}
		}
		return matchList;
	}

	public List getInStatmentProposals(
		HdlDocument doc,
		int offset,
		String replace,
		String mname)
	{
		List matchList = new ArrayList();

		//  template
		if (isMatch(replace, "begin"))
			matchList.add(createBeginEnd(doc, offset, replace.length()));

		//  variable
		return addVariableProposals(doc, offset, replace, mname, matchList);
	}

	// code templates
	// TODO: need refactoring
	private ICompletionProposal createBeginEnd(IDocument doc, int offset, int length)
	{
		String indent = getIndent(doc, offset);
		String str = "begin\n" + indent + "\t\n" + indent + "end";
		int cursor = 7 + indent.length();
		return getCompletionProposal(str, offset, length, cursor, "begin/end");
	}
	private ICompletionProposal createAlways(IDocument doc, int offset, int length)
	{
		String first = "always @(posedge clk) begin\n\t";
		String second = "\nend\n";
		return getCompletionProposal(first + second, offset, length, first.length(), "always");
	}
	private ICompletionProposal createInitial(IDocument doc, int offset, int length)
	{
		String first = "initial begin\n\t";
		String second = "\nend\n";
		return getCompletionProposal(first + second, offset, length, first.length(), "initial");
	}
	private ICompletionProposal createFunction(IDocument doc, int offset, int length)
	{
		String first = "function ";
		String second = ";\nbegin\n\t\nend\nendfunction\n";
		return getCompletionProposal(first + second, offset, length, first.length(), "function");
	}
	private ICompletionProposal createTask(IDocument doc, int offset, int length)
	{
		String first = "task ";
		String second = ";\nbegin\n\t\nend\nendtask\n";
		return getCompletionProposal(first + second, offset, length, first.length(), "task");
	}
	private ICompletionProposal createGenerate(IDocument doc, int offset, int length)
	{
		String first = "generate\n";
		String second = "\nendgenerate\n";
		return getCompletionProposal(first + second, offset, length, first.length(), "generate");
	}

	private class VerilogInstanceCompletionProposal extends
			InstanceCompletionProposal 
	{
		public VerilogInstanceCompletionProposal(
			HdlDocument doc,
			String name,
			int offset,
			int length)
		{
			super(doc, name, offset, length);
		}

		public String getReplaceString(Module module)
		{
			String name = module.toString();
			String indent = "\n" + getIndentString();

			boolean isParams = VerilogPlugin
					.getPreferenceBoolean("ContentAssist.ModuleParameter");
			Object[] params = module.getParameters();
			isParams = isParams && (params.length > 0);

			StringBuffer replace = new StringBuffer(name + " ");
			
			if (isParams)
			{
				replace.append("#(");
				for (int i = 0; i < params.length; i++)
				{
					replace.append(indent + "\t");
					ModuleVariable param = (ModuleVariable)params[i];
					replace.append("." + param + "(" + param.getValue() + ")");
					if (i < params.length - 1)
						replace.append(",");
				}
				replace.append("\n)\n");
			}
			
			replace.append(name + "(");
			Object[] ports = module.getPorts();
			for (int i = 0; i < ports.length; i++)
			{
				replace.append(indent + "\t");

				String port = ports[i].toString();
				replace.append("." + port + "(" + port + ")");

				if (i < ports.length - 1)
					replace.append(",");
			}
			replace.append(indent + ");");
			return replace.toString();
		}
	}
}


