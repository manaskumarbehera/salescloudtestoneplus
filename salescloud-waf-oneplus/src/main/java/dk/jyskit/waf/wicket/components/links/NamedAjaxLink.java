/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.jyskit.waf.wicket.components.links;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;


@SuppressWarnings("serial")
public abstract class NamedAjaxLink extends AjaxLink<String>
{
	private String componentName;

	public NamedAjaxLink(String aId)
	{
		super(aId);
		this.componentName = aId;
	}

	public NamedAjaxLink(String aId, String aComponentName)
	{
		super(aId);
		this.componentName = aComponentName;
	}
	
	public NamedAjaxLink(final String aId, final IModel<String> aModel)
	{
		super(aId, aModel);
		this.componentName = aId;
	}

	public NamedAjaxLink(final String aId, String aComponentName, final IModel<String> aModel)
	{
		super(aId, aModel);
		this.componentName = aComponentName;
	}
	
	@Override
	protected void onComponentTag(ComponentTag aTag)
	{
		super.onComponentTag(aTag);
		aTag.put("name", componentName);
	}
	
	
}
