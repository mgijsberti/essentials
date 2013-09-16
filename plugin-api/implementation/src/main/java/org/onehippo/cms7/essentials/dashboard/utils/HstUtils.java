package org.onehippo.cms7.essentials.dashboard.utils;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.onehippo.cms7.essentials.dashboard.Asset;
import org.onehippo.cms7.essentials.dashboard.Plugin;
import org.onehippo.cms7.essentials.dashboard.model.hst.HstTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id: HstUtils.java 169724 2013-07-05 08:32:08Z dvandiepen $"
 */
public final class HstUtils {

    private static Logger log = LoggerFactory.getLogger(HstUtils.class);


    public static final String HST_CONFIGURATIONS_PATH = "/hst:hst/hst:configurations";

    private HstUtils() {
    }


/*
    // TODO code refactor, see {@link HstTemplate}.

    Example to store template based on asset.

    final HstTemplate template = HstUtils.createTemplateFromAsset(getDescriptor(), "customTemplate", "testtemplate");
    try {
        HstUtils.addTemplateNodeToConfiguration(getJCRSession(), "hippoplugins", template);
        getJCRSession().save();
    } catch (RepositoryException e) {
        resetSession();
    }

    Example asset for in plugin.xml

    <assets>
        <asset id="customTemplate" type="text/plain" url=""><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
        <p>freemarker template</p>]]></asset>
    </assets>
*/


    /**
     * Create a {@link HstTemplate} object with a script based on an asset provided in a plugin config.
     *
     * @param plugin       the plugin to get the asset from
     * @param assetId      the id of the asset to use
     * @param templateName the name of the template to create
     * @return a template object with name and script
     */
    public static HstTemplate createTemplateFromAsset(final Plugin plugin, final String assetId, final String templateName) {
        final Asset asset = plugin.getAsset(assetId);
        if (asset == null) {
            log.warn("Asset {} not available in plugin configuration", assetId);
            return null;
        }

        final HstTemplate template = new HstTemplate(templateName);
        template.setScript(asset.getData());
        return template;
    }

    /**
     * Store a {@link HstTemplate} to the hst:templates folder underneath the hst:configuration node with the provided
     * {@code #configName}.
     * <p/>
     * The changes are not saved in the JCR session. An explicit session save action is required to persist the changes
     * to the repository.
     *
     * @param session    the JCR session
     * @param configName the name of the hst:configurations folder
     * @param template   the template to add
     * @return the newly added template node
     * @throws RepositoryException exception when repository exception occurs
     */
    public static Node addTemplateNodeToConfiguration(final Session session, final String configName, final HstTemplate template) throws RepositoryException {
        final Node hstTemplatesNode = getHstTemplatesNode(session, configName);
        return HstTemplate.createTemplateNode(hstTemplatesNode, template);
    }

    /**
     * Retrieve the hst:templates node for a hst:configuration node with the given name. When there is no
     * hst:configuration with the provided ${@code configName} available, null will be returned.
     *
     * @param session    the JCR session
     * @param configName the name of the hst:configuration
     * @return the hst:templates node containing templates
     * @throws RepositoryException
     */
    public static Node getHstTemplatesNode(final Session session, final String configName) throws RepositoryException {
        if (StringUtils.isBlank(configName)) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("/hst:hst/hst:configurations/");
        sb.append(configName);
        sb.append("/hst:templates");
        return session.getNode(sb.toString());
    }

    /**
     * Return a set of node names of all hst configurations.
     *
     * @param session the JCR session
     * @return a set of node names
     * @throws RepositoryException when exception in repository occurs
     */
    public static Set<String> retrieveHstConfigurationNames(final Session session) throws RepositoryException {
        final Set<String> configurations = new HashSet<>();
        final Node hstConfigurationsNode = session.getNode(HST_CONFIGURATIONS_PATH);
        if (hstConfigurationsNode != null) {
            final NodeIterator iterator = hstConfigurationsNode.getNodes();
            while (iterator.hasNext()) {
                final Node node = iterator.nextNode();
                configurations.add(node.getName());
            }
        }
        return configurations;
    }

}