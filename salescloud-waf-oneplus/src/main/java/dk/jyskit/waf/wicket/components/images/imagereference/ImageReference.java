/*
 *  Copyright 2010 Richard Nichols.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package dk.jyskit.waf.wicket.components.images.imagereference;

import java.io.Serializable;

/**
 * An interface which gives reference to an image.
 *
 * Returning null for any method indicates property is unknown.
 *
 * @version $Id: ImageReference.java 119 2010-02-23 12:03:28Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public interface ImageReference extends Serializable {

    Integer getWidthOverride();

    Integer getHeightOverride();

    String getURL();
}
