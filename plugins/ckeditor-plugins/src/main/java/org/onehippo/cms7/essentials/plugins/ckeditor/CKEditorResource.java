package org.onehippo.cms7.essentials.plugins.ckeditor;


import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import net.sf.json.JSONObject;
import org.onehippo.cms7.essentials.dashboard.ctx.PluginContext;
import org.onehippo.cms7.essentials.dashboard.ctx.PluginContextFactory;
import org.onehippo.cms7.essentials.dashboard.rest.BaseResource;
import org.onehippo.cms7.essentials.dashboard.rest.MessageRestful;
import org.onehippo.cms7.essentials.dashboard.rest.PostPayloadRestful;
import org.onehippo.cms7.essentials.dashboard.utils.GlobalUtils;
import org.onehippo.cms7.essentials.dashboard.utils.PayloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.*;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.*;

/**
 * @version "$Id$"
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
@Path("ckeditor-plugins")
public class CKEditorResource  extends BaseResource {
    private static Logger log = LoggerFactory.getLogger(CKEditorResource.class);
    private static final String CKEDITOR_CONFIG = "ckeditor.config.overlayed.json";

    @POST
    @Path("/")
    public MessageRestful addDocuments(final PostPayloadRestful payloadRestful, @Context ServletContext servletContext) {

        final PluginContext context = PluginContextFactory.getContext();
        final Session session = context.createSession();
        try {
            final Map<String, String> values = payloadRestful.getValues();
            final String documents = values.get("documents");
            final String prefix = context.getProjectNamespacePrefix();
            String plugins = GlobalUtils.readStreamAsText(getClass().getResourceAsStream("/ckeditor_plugins.csv"));

            final Collection<String> changedDocuments = new HashSet<>();

            if (!Strings.isNullOrEmpty(documents)) {

                final String[] docs = PayloadUtils.extractValueArray(values.get("documents"));
                for (final String document : docs) {
                    final String nodeTypePath = MessageFormat.format("/hippo:namespaces/{0}/{1}/hipposysedit:nodetype/hipposysedit:nodetype", prefix, document);
                    if (!session.nodeExists(nodeTypePath)) {
                        log.info("Node type [{}] does not exists.", nodeTypePath);
                        continue;
                    }
                    Node nodeType = session.getNode(nodeTypePath);
                    final String defaultTemplatePath = MessageFormat.format("/hippo:namespaces/{0}/{1}/editor:templates/_default_", prefix, document);
                    if (!session.nodeExists(defaultTemplatePath)) {
                        log.info("Path [{}] does not exists].", defaultTemplatePath);
                        continue;
                    }
                    //Find html fields based on condition that the hypposysedit:type property = hippostd:html
                    List<String> fields = getHtmlFieldsFor(nodeType);
                    if(fields.size() == 0){
                        log.info("Document [{}] does not have html field",document);
                    }
                    for(String field : fields) {
                        final String fieldImportPath = MessageFormat.format("{0}/{1}/cluster.options", defaultTemplatePath, field);
                        if (!session.nodeExists(fieldImportPath)) {
                            log.info("cluster.options does not exists for namespace [{}] and document [{}] and field [{}].", prefix, document, field);
                            continue;
                        }
                        Node fieldNode = session.getNode(fieldImportPath);
                        setPluginConfiguration(fieldNode,plugins);
                        session.save();
                        changedDocuments.add(document);
                    }
                }
            }
            if (changedDocuments.size() > 0) {
                final String join = Joiner.on(',').join(changedDocuments);
                return new MessageRestful("Added CKEditor plugins to the following documents: " + join);
            }

        } catch (RepositoryException e) {
            log.error("Error adding CKEditor plugins", e);
        } finally {
            GlobalUtils.cleanupSession(session);
        }
        return new MessageRestful("No CKEDitor plugins were added");

    }

//static helper classes
    protected static List<String> getHtmlFieldsFor(Node node) throws RepositoryException {
        List<String> htmlFields = new ArrayList<>();
        NodeIterator nodeIterator = node.getNodes();
        while(nodeIterator.hasNext()){
            Node next = nodeIterator.nextNode();
            if(next.hasProperty("hipposysedit:type")){
                Property type = next.getProperty("hipposysedit:type");
                if("hippostd:html".equals(type.getValue().getString())){
                    htmlFields.add(next.getName());
                }
            }
        }
        return htmlFields;
    }

    protected static String extractPluginsFrom(String json) {
        return (String) JSONObject.fromObject(json).get("extraPlugins");
    }

    protected static String combine(String existingPlugins, String newPlugins) {
        if(existingPlugins == null || existingPlugins.isEmpty()){
            return newPlugins;
        }
        if(newPlugins == null || newPlugins.isEmpty()){
            return existingPlugins;
        }
        HashSet set = new HashSet();
        List<String> existingPluginsList = convertToList(existingPlugins);
        set.addAll(existingPluginsList);
        List<String> newPluginList = convertToList(newPlugins);
        set.addAll(newPluginList);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator i = set.iterator();
        while(i.hasNext()){
            if(!stringBuilder.toString().isEmpty()){
                stringBuilder.append(",");
            }
            stringBuilder.append(i.next());
        }
        return stringBuilder.toString();
    }

    private static List<String> convertToList(String values){
        String[] valuesArray = values.split(",");
        return Arrays.asList(valuesArray);
    }

    protected static void setPluginConfiguration(Node node, String plugins) throws RepositoryException{
        String combinedPlugins = null;
        //handle existing ckeditor.config.overlayed.json
        if (node.hasProperty(CKEDITOR_CONFIG)) {
            String existingPlugins = extractPluginsFrom(node.getProperty(CKEDITOR_CONFIG).getValue().getString());
            combinedPlugins = combine(existingPlugins, plugins);
            setConfig(node,combinedPlugins);
        } else {
            setConfig(node, plugins);
        }
    }
    protected static void setConfig(Node node, String plugins) throws RepositoryException{
        String overlay = "{ extraPlugins: '"+plugins+"' }";
        log.info("Set property [{}] with value [{}] on node [{}]", CKEDITOR_CONFIG, plugins, node.getPath());
        node.setProperty(CKEDITOR_CONFIG,overlay);
    }
}
