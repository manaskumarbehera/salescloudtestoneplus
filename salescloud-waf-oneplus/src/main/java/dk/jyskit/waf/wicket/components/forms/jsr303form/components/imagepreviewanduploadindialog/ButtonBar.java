/*
 *  Copyright 2008 Hippo.
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
 */
package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagepreviewanduploadindialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;


class ButtonBar extends Panel {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    public ButtonBar(String id, ImageUploadAndCropWizard wizard) {
        super(id);
        add(new BackLink("back", wizard));
        add(new CancelLink("cancel", wizard));
        add(new NextLink("next", wizard));
        add(new FinishLink("finish", wizard));
    }

    private class CancelLink extends AbstractWizardLink {
        private static final long serialVersionUID = 1L;

        public CancelLink(String id, ImageUploadAndCropWizard wizard) {
            super(id, wizard);
        }
        
        @Override
        public void onClick(AjaxRequestTarget target) {
            wizard.getWizardModel().cancel();
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isVisible() {
            return false;
        }
    }

    private class BackLink extends AbstractWizardLink {
        private static final long serialVersionUID = 1L;

        public BackLink(String id, ImageUploadAndCropWizard wizard) {
            super(id, wizard);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            wizard.getWizardModel().previous();
            target.add(wizard);
        }

        @Override
        public boolean isEnabled() {
            return wizard.getWizardModel().isPreviousAvailable();
        }
    }

    private class NextLink extends AbstractWizardLink {
        private static final long serialVersionUID = 1L;

        public NextLink(String id, ImageUploadAndCropWizard wizard) {
            super(id, wizard);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            wizard.getWizardModel().next();
            target.add(wizard);
        }

        @Override
        public boolean isEnabled() {
        	return ((wizard.getImageDataSource() != null) &&
        			wizard.getWizardModel().isNextAvailable());
        }
    }

    private class FinishLink extends AbstractWizardLink {
        private static final long serialVersionUID = 1L;

        public FinishLink(String id, ImageUploadAndCropWizard wizard) {
            super(id, wizard);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            wizard.getWizardModel().finish();
        }

        @Override
        public boolean isEnabled() {
            return !wizard.getWizardModel().isNextAvailable();
        }
    }

    private abstract class AbstractWizardLink extends AjaxLink {
        private static final long serialVersionUID = 1L;

        protected final ImageUploadAndCropWizard wizard;

        public AbstractWizardLink(String id, ImageUploadAndCropWizard wizard) {
            super(id);
            setOutputMarkupId(true);
            this.wizard = wizard;
        }
    }
}