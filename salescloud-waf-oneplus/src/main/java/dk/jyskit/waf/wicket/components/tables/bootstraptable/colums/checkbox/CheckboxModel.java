/*
 * $Id: org.eclipse.jdt.ui.prefs 367 2005-10-11 16:06:41 -0700 (Tue, 11 Oct 2005) ivaynberg $
 * $Revision: 367 $
 * $Date: 2005-10-11 16:06:41 -0700 (Tue, 11 Oct 2005) $
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkbox;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.model.IModel;

/**
 * A model for checkboxes that represent a more-than-one-selectedTokens.
 * 
 * @author ivaynberg
 * 
 */
public class CheckboxModel extends AbstractCheckBoxModel {
	private static final long serialVersionUID = 1L;
	private final IModel<Collection<Serializable>> selectedTokens;
	private final Serializable token;

	/**
	 * Constructor
	 * 
	 * @param selectedTokens
	 *            model that contains a collection of tokens which will
	 *            represent selectedTokens state
	 * @param token
	 *            token whose presence in the collection represents a selectedTokens
	 *            state
	 */
	public CheckboxModel(IModel<Collection<Serializable>> selectedTokens, Serializable token) {
		super();
		this.selectedTokens = selectedTokens;
		this.token = token;
	}

	@Override
	public boolean isSelected() {
		return selectedTokens.getObject().contains(token);
	}

	@Override
	public void select() {
		selectedTokens.getObject().add(token);
	}

	@Override
	public void unselect() {
		selectedTokens.getObject().remove(token);
	}

}
