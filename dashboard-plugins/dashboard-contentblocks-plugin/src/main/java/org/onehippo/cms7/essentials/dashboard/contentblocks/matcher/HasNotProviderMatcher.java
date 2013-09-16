package org.onehippo.cms7.essentials.dashboard.contentblocks.matcher;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.cms7.essentials.dashboard.utils.JcrMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id$"
 */
public class HasNotProviderMatcher implements JcrMatcher {

    private static Logger log = LoggerFactory.getLogger(HasNotProviderMatcher.class);

    @Override
    public boolean matches(final Node typeNode) throws RepositoryException {
        return false;
    }
}