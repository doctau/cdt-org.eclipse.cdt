/*******************************************************************************
 * Copyright (c) 2015 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.cdt.internal.ui.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IDE;

import org.eclipse.cdt.ui.CUIPlugin;

public class CHyperlink implements IHyperlink {

	private final IMarker marker;

	public CHyperlink(IMarker marker) {
		this.marker = marker;
	}

	@Override
	public void linkEntered() {
	}

	@Override
	public void linkExited() {
	}

	@Override
	public void linkActivated() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, marker);
		} catch (PartInitException e) {
			CUIPlugin.log(e);
		}
	}

}
