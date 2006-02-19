//
//  Copyright 2004, 2006 KOBAYASHI Tadashi
//  $Id$
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package net.sourceforge.veditor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

public class VerilogPerspective implements IPerspectiveFactory {

    public VerilogPerspective()
	{
		super();
	}

	public void createInitialLayout(IPageLayout layout)
	{
		defineLayout(layout);
	}

	private void defineLayout(IPageLayout layout)
	{
		IFolderLayout left= layout.createFolder("Left",
				IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
		IFolderLayout bottom = layout.createFolder("Bottom",
				IPageLayout.BOTTOM, 0.75f, IPageLayout.ID_EDITOR_AREA);
		IFolderLayout right = layout.createFolder("Right",
				IPageLayout.RIGHT, 0.75f, IPageLayout.ID_EDITOR_AREA);
		
		addView(layout, left, IPageLayout.ID_RES_NAV);
		addView(layout, right, IPageLayout.ID_OUTLINE); 
		addView(layout, right, "net.sourceforge.veditor.editor.ModuleHierarchyView");
		addView(layout, bottom, IPageLayout.ID_PROBLEM_VIEW);
		addView(layout, bottom, "org.eclipse.ui.console.ConsoleView");
	}

	private void addView(IPageLayout parent, IFolderLayout folder, String viewid)
	{
		folder.addView(viewid);
		IViewLayout layout = parent.getViewLayout(viewid);
		if (layout != null)
		{
			layout.setCloseable(true);
			layout.setMoveable(true);
		}
	}

}







