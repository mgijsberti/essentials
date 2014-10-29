package org.onehippo.cms7.essentials.plugins.ckeditor;

import org.onehippo.cms7.essentials.dashboard.config.PluginParameterService;

public class CKEditorParameterService implements PluginParameterService {
    @Override
    public boolean hasSetup() {
        return true;
    }

    @Override
    public boolean hasGeneralizedSetupParameters() {
        return false;
    }

    @Override
    public boolean doesSetupRequireRebuild() {
        return true;
    }

    @Override
    public boolean hasConfiguration() {
        return true;
    }
}
