/*******************************************************************************
 * Copyright (c) 2015 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.qt.ui.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.internal.qt.core.QtNature;
import org.eclipse.cdt.internal.qt.ui.QtUIPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Detects the addition or removal of a file to a Qt project. If one of these resource changes is found, it triggers an update of
 * the project's *.pro file to reflect the change.
 */
public class QtResourceChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// No need to check for any events other than POST_CHANGE
		if ((event.getType() & (IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.POST_BUILD)) == 0) {
			return;
		}

		final List<IResourceDelta> deltaList = new ArrayList<>();
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) {
				IResource resource = delta.getResource();

				if (resource.getType() == IResource.ROOT) {
					// Always traverse children of the workspace root
					return true;
				} else if (resource.getType() == IResource.PROJECT) {
					// Only traverse children of Qt Projects
					try {
						IProject project = (IProject) resource;
						if (project.hasNature(QtNature.ID)) {
							return true;
						}
					} catch (CoreException e) {
						QtUIPlugin.log(e);
					}
					return false;
				} else if (resource.getType() == IResource.FOLDER) {
					// First, make sure this isn't the "build" folder
					if (resource.getType() == IResource.FOLDER) {
						if (resource.getName().equals("build")) { //$NON-NLS-1$
							return false;
						}
					}

					// Then check to make sure that the folder lies in a Qt Project
					try {
						IProject project = resource.getProject();
						if (project != null && project.hasNature(QtNature.ID)) {
							return true;
						}
					} catch (CoreException e) {
						QtUIPlugin.log(e);
					}
					return false;
				}

				// We only care about added and removed resources at this point
				if ((delta.getKind() & (IResourceDelta.ADDED | IResourceDelta.REMOVED)) == 0) {
					return false;
				}

				if ("cpp".equals(resource.getFileExtension()) //$NON-NLS-1$
						|| "h".equals(resource.getFileExtension())) { //$NON-NLS-1$
					// If we make it to this point, then we have a .cpp or .h file that's been added to or removed from a Qt
					// Project. Add it to the list of deltas so we can update the project file later.
					deltaList.add(delta);
				}

				// Doesn't really matter since this line can only be reached if we're dealing with a file that shouldn't have
				// children anyway
				return false;
			}
		};

		try {
			// Check all projects starting at the workspace root
			event.getDelta().accept(visitor);
		} catch (CoreException e) {
			QtUIPlugin.log(e);
		}

		// Schedule the job to update the .pro files
		if (!deltaList.isEmpty()) {
			new QtProjectFileUpdateJob(deltaList).schedule();
		}
	}
}
