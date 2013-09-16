package org.onehippo.cms7.essentials.dashboard.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.onehippo.cms7.essentials.dashboard.ctx.PluginContext;
import org.onehippo.cms7.essentials.dashboard.utils.beansmodel.HippoEssentialsGeneratedObject;
import org.onehippo.cms7.essentials.dashboard.utils.beansmodel.MemoryBean;
import org.onehippo.cms7.essentials.dashboard.utils.beansmodel.MemoryProperty;
import org.onehippo.cms7.essentials.dashboard.utils.code.EssentialsGeneratedMethod;
import org.onehippo.cms7.essentials.dashboard.utils.code.ExistingMethodsVisitor;
import org.onehippo.cms7.essentials.dashboard.utils.code.NoAnnotationMethodVisitor;
import org.onehippo.cms7.essentials.dashboard.utils.xml.NodeOrProperty;
import org.onehippo.cms7.essentials.dashboard.utils.xml.XmlNode;
import org.onehippo.cms7.essentials.dashboard.utils.xml.XmlProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id: BeanWriterUtils.java 174577 2013-08-21 16:12:51Z mmilicevic $"
 */
public class BeanWriterUtils {
    private static Logger log = LoggerFactory.getLogger(BeanWriterUtils.class);


    private BeanWriterUtils() {
    }

    public static void addMissingMethods(final PluginContext context, final List<MemoryBean> memoryBeans, final String fileExtension) {

        final Collection<MemoryBean> missing = createMissingBeans(context, memoryBeans, fileExtension);
        memoryBeans.removeAll(missing);
        for (MemoryBean bean : memoryBeans) {
            // update all bean methods
            updateBeanMethods(context, bean);
        }

        for (MemoryBean bean : memoryBeans) {
            populateSupertypes(bean, memoryBeans);
            MemoryBean supertype = bean.getSupertype();
            // loop all supertypes:
            Set<String> superMethodNames = Collections.emptySet();
            while (supertype != null) {
                final NoAnnotationMethodVisitor methodCollection = JavaSourceUtils.getAnnotateMethods(context, supertype.getBeanPath());
                superMethodNames = methodCollection.getModifiableMethodsInternalNames();
                populateSupertypes(supertype, memoryBeans);
                supertype = supertype.getSupertype();
            }
            final NoAnnotationMethodVisitor methodCollection = JavaSourceUtils.getAnnotateMethods(context, bean.getBeanPath());
            final ExistingMethodsVisitor existingMethodCollection = JavaSourceUtils.getMethodCollection(bean.getBeanPath());
            final Set<String> existing = existingMethodCollection.getMethodInternalNames();
            // add also supertype methods:
            final MemoryBean mySuperType = bean.getSupertype();
            if (mySuperType != null) {
                final ExistingMethodsVisitor superExistingMethodCollection = JavaSourceUtils.getMethodCollection(mySuperType.getBeanPath());
                final Set<String> superExisting = superExistingMethodCollection.getMethodInternalNames();
                existing.addAll(superExisting);
            }

            final Set<String> methodsNames = methodCollection.getModifiableMethodsInternalNames();
            // use supertype methods as well:
            methodsNames.addAll(superMethodNames);
            final List<MemoryProperty> properties = bean.getProperties();
            for (MemoryProperty property : properties) {
                final String name = property.getName();
                if (!existing.contains(name)) {
                    // add new method:
                    log.debug("processing missing property: {}", property);
                    final String type = property.getType();
                    if (type == null) {
                        log.error("Missing type for property, cannot create method {}", property);
                        continue;
                    }
                    final boolean multiple = property.isMultiple();
                    String methodName;
                    switch (type) {
                        // TODO add other  methods
                        case "String":
                        case "Html":
                        case "Password":
                        case "Docbase":
                        case "Text":
                            methodName = GlobalUtils.createMethodName(name);

                            JavaSourceUtils.addBeanMethodString(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "Boolean":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodBoolean(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "Long":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodLong(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "Double":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodDouble(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "hippostd:html":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodHippoHtml(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "hippo:mirror":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodHippoMirror(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        case "hippogallerypicker:imagelink":
                            methodName = GlobalUtils.createMethodName(name);
                            JavaSourceUtils.addBeanMethodImageLink(bean.getBeanPath(), methodName, name, multiple);
                            existing.add(name);
                            log.debug("@@@ added [{}] method", methodName);
                            break;
                        default:
                            methodName = GlobalUtils.createMethodName(name);
                            // check if project namespace
                            if (type.startsWith(bean.getNamespace())) {
                                for (MemoryBean memoryBean : memoryBeans) {
                                    if (memoryBean.getPrefixedName().equals(type)) {
                                        final String beanName = FilenameUtils.removeExtension(memoryBean.getBeanPath().toFile().getName());
                                        if (multiple) {
                                            JavaSourceUtils.addImport(bean.getBeanPath(), "java.util.List");
                                            JavaSourceUtils.addTwoArgumentsMethod("getBeans", String.format("List<%s>", beanName), bean.getBeanPath(), methodName, name);
                                        } else {
                                            JavaSourceUtils.addTwoArgumentsMethod("getBean", beanName, bean.getBeanPath(), methodName, name);
                                        }
                                        existing.add(name);
                                    }
                                }
                            } else {
                                log.error("####### FAILED to add [{}] method for type", type);
                            }

                    }

                } else {
                    log.debug("###  Method for property already exists: {}", name);
                }
            }
        }
    }

    private static Collection<MemoryBean> createMissingBeans(final PluginContext context, final Iterable<MemoryBean> memoryBeans, final String fileExtension) {
        Collection<MemoryBean> missing = new ArrayList<>();
        for (MemoryBean memoryBean : memoryBeans) {
            if (memoryBean.getBeanPath() == null) {
                missing.add(memoryBean);
            }
        }
        for (MemoryBean memoryBean : missing) {
            populateSupertypes(memoryBean, memoryBeans);
            final MemoryBean supertype = memoryBean.getSupertype();
            if (supertype != null && supertype.getBeanPath() != null) {
                final String extendsName = FilenameUtils.removeExtension(supertype.getBeanPath().toFile().getName());

                final String prefixedName = memoryBean.getPrefixedName();
                final String className = GlobalUtils.createClassName(prefixedName);
                final Path javaClass = JavaSourceUtils.createJavaClass(context.getSiteJavaRoot(), className, context.beansPackageName(), fileExtension);
                JavaSourceUtils.addExtendsClass(javaClass, extendsName);
                JavaSourceUtils.createHippoBean(javaClass, context.beansPackageName(), prefixedName, prefixedName);
                memoryBean.setBeanPath(javaClass);
            }

        }
        missing = new ArrayList<>();
        for (MemoryBean memoryBean : memoryBeans) {
            if (memoryBean.getBeanPath() == null) {
                missing.add(memoryBean);

            }
        }

        return missing;
    }

    /**
     * Builds an in-memory graph by parsing XML namespaces. This graph can be used to write HST beans
     *
     * @param directory       starting directory (where we scan for document templates)
     * @param context         plugin context instance
     * @param sourceExtension extension used for source files e.g. {@code "java"}
     * @return a list of MemoryBeans or empty list if nothing is found
     */
    public static List<MemoryBean> buildBeansGraph(final Path directory, PluginContext context, final String sourceExtension) {
        final List<XmlNode> templateDocuments = XmlUtils.findTemplateDocuments(directory, context);
        final String projectNamespacePrefix = context.getProjectNamespacePrefix();
        final List<MemoryBean> beans = new ArrayList<>();
        for (XmlNode templateDocument : templateDocuments) {
            log.debug("templateDocument {}", templateDocument.getName());
            MemoryBean bean = processTemplate(templateDocument, projectNamespacePrefix);
            beans.add(bean);
        }
        // we need to annotate existing beans before pro
        BeanWriterUtils.annotateExistingBeans(context, sourceExtension);
        final List<Path> existing = BeanWriterUtils.findExitingBeans(context, sourceExtension);
        final Collection<HippoEssentialsGeneratedObject> generatedObjectList = new ArrayList<>();
        for (Path path : existing) {
            final HippoEssentialsGeneratedObject generatedObject = JavaSourceUtils.getHippoGeneratedAnnotation(path);
            if (generatedObject != null) {
                generatedObjectList.add(generatedObject);
            }
        }
        BeanWriterUtils.processSupertypes(beans, generatedObjectList);
        return beans;

    }

    public static void processSupertypes(final Iterable<MemoryBean> allBeans, final Iterable<HippoEssentialsGeneratedObject> descriptors) {
        for (HippoEssentialsGeneratedObject descriptor : descriptors) {
            for (MemoryBean myBean : allBeans) {
                final String fullName = myBean.getPrefixedName();
                if (fullName.equals(descriptor.getInternalName())) {
                    myBean.setBeanPath(descriptor.getFilePath());
                }
            }
        }

    }

    /**
     * Find all existing HST beans (which annotated with {@code @Node})
     *
     * @param context             plugin context instance
     * @param sourceFileExtension file extension, e.g. {@code "java"}
     * @return a list of beans or an empty list if nothing was found
     */
    public static List<Path> findExitingBeans(final PluginContext context, final String sourceFileExtension) {
        final Path startDir = context.getBeansPackagePath();
        final List<Path> existingBeans = new ArrayList<>();
        final List<Path> directories = new ArrayList<>();
        GlobalUtils.populateDirectories(startDir, directories);
        final String pattern = "*." + sourceFileExtension;
        for (Path directory : directories) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(directory, pattern)) {
                for (Path path : stream) {
                    final String nodeJcrType = JavaSourceUtils.getNodeJcrType(path);
                    if (nodeJcrType != null) {
                        existingBeans.add(path);
                    }
                }
            } catch (IOException e) {
                log.error("Error reading java files", e);
            }
        }

        return existingBeans;

    }

    /**
     * Adds {@code org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated} annotation (if missing) to the existing beans
     *
     * @param context             plugin context instance
     * @param sourceFileExtension file extension, e.g. {@code "java"}
     * @see org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated
     */
    public static void annotateExistingBeans(final PluginContext context, final String sourceFileExtension) {
        final List<Path> existingBeans = findExitingBeans(context, sourceFileExtension);
        for (Path path : existingBeans) {
            if (!JavaSourceUtils.hasHippoEssentialsAnnotation(path)) {
                JavaSourceUtils.addHippoGeneratedBeanAnnotation(path);
            }
        }

    }

    /**
     * Adds (missing) methods to a HippoBean
     *
     * @param bean    instance of memory bean
     * @param context plugin context instance
     */
    public static void updateBeanMethods(final PluginContext context, final MemoryBean bean) {
        final NoAnnotationMethodVisitor methodCollection = JavaSourceUtils.getAnnotateMethods(context, bean.getBeanPath());
        final List<EssentialsGeneratedMethod> methodsNames = methodCollection.getModifiableMethods();
        for (EssentialsGeneratedMethod method : methodsNames) {
            JavaSourceUtils.annotateMethod(method, bean);
        }

    }

    //############################################
    //  private methods
    //############################################

    private static void populateSupertypes(final MemoryBean bean, final Iterable<MemoryBean> memoryBeans) {
        final Set<String> superTypeValues = bean.getSuperTypeValues();
        String supertypeName = null;
        for (String superTypeValue : superTypeValues) {
            if (superTypeValue.startsWith(bean.getNamespace())) {
                supertypeName = superTypeValue;
            }
        }
        if (supertypeName == null) {
            return;
        }
        log.debug("Found supertypeName {}", supertypeName);
        for (MemoryBean memoryBean : memoryBeans) {
            if (memoryBean.getPrefixedName().equals(supertypeName)) {
                bean.setSupertype(memoryBean);
                return;
            }
        }
    }

    private static void processKid(final MemoryBean bean, final XmlNode templateDocument, final NodeOrProperty nodeOrProperty, final String projectNamespacePrefix) {
        final String name = nodeOrProperty.getName();
        if (name.startsWith(projectNamespacePrefix)) {
            addBeanProperty(bean, templateDocument, nodeOrProperty, name);
        }

        final Collection<NodeOrProperty> childNodes = nodeOrProperty.getXmlNodeOrXmlProperty();
        for (NodeOrProperty childNode : childNodes) {
            String aName = childNode.getName();
            if (aName.startsWith(projectNamespacePrefix)) {
                addBeanProperty(bean, templateDocument, childNode, aName);
            }
            processKid(bean, templateDocument, childNode, projectNamespacePrefix);
        }
    }

    private static void addBeanProperty(final MemoryBean bean, final XmlNode templateDocument, final NodeOrProperty nodeOrProperty, final String name) {
        final MemoryProperty property = new MemoryProperty(bean);
        property.setName(name);
        property.setMultiple(nodeOrProperty.getMultiple());
        // find a right type:

        XmlProperty typeProperty = null;
        final Collection<XmlNode> subnodesByName = templateDocument.getTemplates();
        for (XmlNode node : subnodesByName) {
            final Collection<XmlProperty> properties = node.getProperties();
            for (XmlProperty xmlProperty : properties) {
                if (xmlProperty.getName().equals("hipposysedit:path")) {
                    if (name.equals(xmlProperty.getSingleValue())) {
                        typeProperty = node.getXmlPropertyByName("hipposysedit:type");
                        break;
                    }
                }
            }
        }

        if (typeProperty != null) {
            property.setType(typeProperty.getSingleValue());
        } else {
            log.debug("Missing subnodesByName {}", subnodesByName);
        }

        bean.addProperty(property);
    }

    private static MemoryBean processTemplate(final XmlNode templateDocument, final String projectNamespacePrefix) {
        final String name = templateDocument.getName();
        final MemoryBean bean = new MemoryBean(name, projectNamespacePrefix);
        final Collection<String> values = templateDocument.getSupertypeProperty().getValues();
        bean.setSuperTypeValues(new HashSet<>(values));
        final Collection<NodeOrProperty> xmlNodeOrXmlProperty = templateDocument.getXmlNodeOrXmlProperty();
        for (NodeOrProperty nodeOrProperty : xmlNodeOrXmlProperty) {
            processKid(bean, templateDocument, nodeOrProperty, projectNamespacePrefix);
        }
        return bean;

    }
}