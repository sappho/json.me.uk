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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Path("/string-to-json")
public class ConvertStringToJson {

    @FormParam("string")
    private String string;
    @Context
    ServletContext servletContext;

    @POST
    @Produces(MediaType.TEXT_HTML)
    public String getProperty() throws IOException, TemplateException {

        Configuration freemarkerConfiguration = new Configuration();
        freemarkerConfiguration.setTemplateLoader(
                new WebappTemplateLoader(servletContext, "ask/string-to-json"));
        freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        Template template = freemarkerConfiguration.getTemplate("results.ftl");
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> report = new HashMap<String, Object>();
        report.put("json", new Gson().toJson(string));
        template.process(report, stringWriter);
        stringWriter.close();
        return stringWriter.toString();
    }
}
