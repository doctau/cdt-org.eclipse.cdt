/*******************************************************************************
 * Copyright (c) 2006, 2007 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX - Initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.pdom.indexer;

import org.eclipse.cdt.core.dom.IPDOMIndexerTask;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.model.ITranslationUnit;

/**
 * Configures the abstract indexer to return tasks suitable for fast indexing.
 */
public class PDOMFastIndexer extends AbstractPDOMIndexer {
	// Must match extension id
	public static final String ID = IPDOMManager.ID_FAST_INDEXER;

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public IPDOMIndexerTask createTask(ITranslationUnit[] added, ITranslationUnit[] changed, ITranslationUnit[] removed) {
		return new PDOMFastIndexerTask(this, added, changed, removed);
	}
}
