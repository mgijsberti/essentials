/*
 * Copyright 2013 Hippo B.V. (http://www.onehippo.com)
 */

package org.onehippo.cms7.essentials.dashboard.utils;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @version "$Id: EssentialConst.java 174363 2013-08-20 12:37:31Z mmilicevic $"
 */
public final class EssentialConst {


    /**
     * @see org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated#internalName()
     */
    public static final String ANNOTATION_ATTR_INTERNAL_NAME = "internalName";
    /**
     * @see org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated#date()
     */
    public static final String ANNOTATION_ATTR_DATE = "date";
    /**
     * @see org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated#allowModifications() ()
     */
    public static final String ANNOTATION_ATTR_ALLOW_MODIFICATIONS = "allowModifications";


    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_IMAGE_JPEG = "text/jpeg";
    public static final String MIME_IMAGE_GIF = "image/gif";
    public static final String MIME_IMAGE_PNG = "text/png";
    public static final String MIME_APPLICATION_PDF = "application/pdf";
    public static final String FILE_EXTENSION_JAVA = ".java";
    public static final String SOURCE_PATTERN_JAVA = "java";
    /**
     * Name of the system property set by cargo maven build
     */
    public static final String PROJECT_BASEDIR_PROPERTY = "project.basedir";
    /**
     * Hippo system andd plugin CND namespaces
     */
    // TODO add known plugin namespaces to this list
    public static final Set<String> HIPPO_BUILT_IN_NAMESPACES =
            ImmutableSet.of(
                    "dashboard",
                    "frontend",
                    "ef", // easy forms
                    "hippo",
                    "hst",
                    "system",
                    "hippogallery",
                    "hippogallery",
                    "hippostd",
                    "hippostdpubwf",
                    "hipposysedit",
                    "hippotaxonomy",
                    "hippogallerypicker");
    public static final String[] XML_FILTER = new String[]{"xml"};
    public static final String[] JAR_FILTER = new String[]{"jar"};
    public static final String NS_JCR_PRIMARY_TYPE = "jcr:primaryType";
    public static final String NS_HIPPOSYSEDIT_TEMPLATETYPE = "hipposysedit:templatetype";
    public static final String URI_JCR_NAMESPACE = "http://www.jcp.org/jcr/sv/1.0";
    public static final String URI_AUTOEXPORT_NAMESPACE = "http://www.onehippo.org/jcr/xmlimport";
    /**
     * Namespace of plugin.xml descriptor, HippoEssentials framework
     */
    public static final String URI_ESSENTIALS_PLUGIN = "http://www.onehippo.org/essentials";
    /**
     * Fully qualified name of HST node annotation
     */
    public static final String NODE_ANNOTATION_FULLY_QUALIFIED = "org.hippoecm.hst.content.beans.Node";
    /**
     * Name of HST Node annotation
     */
    public static final String NODE_ANNOTATION_NAME = "Node";
    public static final String INVALID_METHOD_NAME = "getTODO";
    public static final String INVALID_CLASS_NAME = "InvalidClassName";
    public static final String ANNOTATION_INTERNAL_NAME_ATTRIUBUTE = "internalName";

    private EssentialConst() {
    }
}