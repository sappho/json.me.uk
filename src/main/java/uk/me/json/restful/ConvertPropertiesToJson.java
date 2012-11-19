/**
 *** This software is licensed under the GNU General Public License, version 3.
 *** See http://www.gnu.org/licenses/gpl.html for full details of the license terms.
 *** Copyright 2012 Andrew Heald.
 */

package uk.me.json.restful;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

@Path("/properties-to-json")
public class ConvertPropertiesToJson {

    @FormParam("properties")
    private String propertiesString;
    @Context
    ServletContext servletContext;

    @POST
    @Produces(MediaType.TEXT_HTML)
    public String getProperty() throws IOException, TemplateException {

        Properties properties = new Properties();
        boolean error = false;
        try {
            properties.loadFromXML(new ByteArrayInputStream(propertiesString.getBytes()));
        } catch (Throwable xmlError) {
            try {
                properties.load(new StringReader(propertiesString));
            } catch (Throwable propertiesError) {
                error = true;
            }
        }
        SortedMap<String, Object> sortedProperties = new TreeMap<String, Object>();
        Enumeration<?> keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            sortedProperties.put(key, properties.get(key));
        }
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent("    ");
        new Gson().toJson(sortedProperties, sortedProperties.getClass(), jsonWriter);
        jsonWriter.close();
        propertiesString = stringWriter.toString();
        Configuration freemarkerConfiguration = new Configuration();
        freemarkerConfiguration.setTemplateLoader(
                new WebappTemplateLoader(servletContext, "ask/properties-to-json"));
        freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        Template template = freemarkerConfiguration.getTemplate("results.ftl");
        stringWriter = new StringWriter();
        Map<String, Object> report = new HashMap<String, Object>();
        report.put("json", propertiesString);
        report.put("error", error);
        template.process(report, stringWriter);
        stringWriter.close();
        return stringWriter.toString();
    }
}
