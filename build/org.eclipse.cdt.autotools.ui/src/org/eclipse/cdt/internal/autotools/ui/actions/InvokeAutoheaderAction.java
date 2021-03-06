/*******************************************************************************
 * Copyright (c) 2009 Red Hat Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Incorporated - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.autotools.ui.actions;

import org.eclipse.cdt.internal.autotools.core.AutotoolsPropertyConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;


public class InvokeAutoheaderAction extends InvokeAction {

	private static final String DEFAULT_OPTION = ""; //$NON-NLS-1$
	private static final String DEFAULT_COMMAND = "autoheader"; //$NON-NLS-1$

	@Override
	public void run(IAction action) {

		IContainer container = getSelectedContainer();
		if (container == null) {
			return;
		}
		IPath execDir = getExecDir(container);
		String cwd = InvokeMessages.getString("CWD") + getCWD(container); //$NON-NLS-1$

		InputDialog optionDialog = new SingleInputDialog(new Shell(), cwd,
				InvokeMessages.getString("InvokeAutoheaderAction.windowTitle.options"), //$NON-NLS-1$
				InvokeMessages.getString("InvokeAutoheaderAction.message.options.otherOptions"), //$NON-NLS-1$
				DEFAULT_OPTION, null);
		optionDialog.open();

		// chop args into string array
		String rawArgList = optionDialog.getValue();

		String[] optionsList = simpleParseOptions(rawArgList);

		String[] argumentList = new String[optionsList.length];

		System.arraycopy(optionsList, 0, argumentList, 0, optionsList.length);

		String autoheaderCommand = null;
		IProject project = getSelectedContainer().getProject();
		try {
			autoheaderCommand = project.getPersistentProperty(AutotoolsPropertyConstants.AUTOHEADER_TOOL);
		} catch (CoreException e) {
			// do nothing
		}

		// If unset, use default system path
		if (autoheaderCommand == null) {
			autoheaderCommand = DEFAULT_COMMAND;
		}

		executeConsoleCommand(DEFAULT_COMMAND, autoheaderCommand, argumentList, execDir);

	}

	@Override
	public void dispose() {

	}

}
