/*******************************************************************************
 * Copyright (c) 2007 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Intel Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.newui;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PropertyPage;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.ui.CUIPlugin;

/**
 * This class is intended to handle  
 * 
 * When new propertypage is created, it should request 
 * project description by method 
 * getProjectDescription()
 * This method, in addition, registers page in list.
 * 
 * While page is active, it can change this description
 * but should not set it, to avoid inconsistency.
 * 
 * When page's "performOK" called, it should call
 * manager's  method
 * performOk()   
 *
 * In addition, there are utility methods for pages: 
 * getPagesCount()
 * getPage()
 * isSaveDone()
 */
public class CDTPropertyManager {

	private static ArrayList pages = new ArrayList();
	private static ICProjectDescription prjd = null;
	private static boolean saveDone  = false;
	private static IProject project = null;
	private static DListener dListener = new DListener();
	
	
	public static ICProjectDescription getProjectDescription(PropertyPage p, IProject prj) {
		return get(p, prj);
	} 	
	public static ICProjectDescription getProjectDescription(Widget w, IProject prj) {
		return get(w, prj);
	} 	
	public static ICProjectDescription getProjectDescription(IProject prj) {
		return get(null, prj);
	} 	
	
	private static ICProjectDescription get(Object p, IProject prj) {
		// New session - clean static variables
		if (pages.size() == 0) {
			project = null;
			prjd = null;
			saveDone = false;
		}
		// Register new client
		if (p != null && !pages.contains(p)) {
			pages.add(p);
			if (p instanceof PropertyPage) {
				if (((PropertyPage)p).getControl() != null) 
					((PropertyPage)p).getControl().addDisposeListener(dListener);
			} else if (p instanceof Widget) {
				((Widget)p).addDisposeListener(dListener);
			}
		}
		// Check that we are working with the same project 
		if (project == null || !project.equals(prj)) {
			project = prj;
			prjd = null;
		}
		// obtain description if it's needed.
		if (prjd == null) {
			prjd = CoreModel.getDefault().getProjectDescription(prj);
		}
		return prjd;
	}

	/**
	 * 
	 * @param p - widget which calls this functionality
	 */
	public static void performOk(Object p) {
		if (saveDone) return;
		saveDone = true;
		try {
			CoreModel.getDefault().setProjectDescription(project, prjd);
		} catch (CoreException e) {
			CUIPlugin.getDefault().logErrorMessage(UIMessages.getString("AbstractPage.11") + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}
	
	// pages utilities
	public static boolean isSaveDone() { return saveDone; }	
	public static int getPagesCount() {	return pages.size(); }
	public static Object getPage(int index) { return pages.get(index); }
	
	// Removes disposed items from list
	static class DListener implements DisposeListener {
		public void widgetDisposed(DisposeEvent e) {
			Widget w = e.widget;
			if (pages.contains(w)) { // Widget ?	
				pages.remove(w); 
			} else {                 // Property Page ?
				Iterator it = pages.iterator();
				while (it.hasNext()) {
					Object ob = it.next();
					if (ob != null && ob instanceof PropertyPage) {
						if (((PropertyPage)ob).getControl().equals(w)) {
							pages.remove(ob);
							break;
						}
					}
				}
			}
		}
	}
	
}
