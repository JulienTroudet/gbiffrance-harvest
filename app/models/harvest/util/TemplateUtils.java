package models.harvest.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;


/**
 * Utilities for working with velocity templates
 * @author timrobertson
 */
public class TemplateUtils {
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(TemplateUtils.class);

	/**
	 * Initialise Velocity once - log the error, which is fatal
	 */
	static {
		try {
			InputStream velPropsIS = TemplateUtils.class.getClassLoader().getResourceAsStream("models/harvest/resources/velocity.properties");
			Properties velProps = new Properties();
			velProps.load(velPropsIS);
			Velocity.init(velProps);			
		} catch (Exception e) {
			log.error("Error initialising velocity: " + e.getMessage() + e.getCause(), e);
			log.error("Velocity initialisation failed, and thus no indexing activities will run correctly " +
					" - please check that velocity.properties is available on the CP root, and is correct!");
		}
		log.info("Velocity is successfully loaded !");
	}
	
	/**
	 * Merges the template at the given location with the parameters provided to a String 
	 * @param location from the root of the classpath - e.g. org/test/mytemplate.vm
	 * @param parameters To merge in
	 * @return The String representation of the merged template
	 * @throws Exception On error - Velocity throws this, so no more specifics can be offered, although it is 
	 * likely to be an error in the location, or a parameter problem
	 */
	public static String getAndMerge(String location, Map<String,String> parameters) throws Exception {
		Template template = getTemplate(location);
		log.debug("Template has been successfully loaded from: " + location);
		VelocityContext context = new VelocityContext();
		for (String key : parameters.keySet()) {
			context.put(key, parameters.get(key));
		}
		String response = merge(template, context); 
		log.debug("Template and parameters merge to: " + response);
		return response;
	}
	
	/**
	 * Gets the velocity template for the location provided
	 * @param location To get the template from
	 * @return The template
	 * @throws Exception On error - normally the location cannot be found
	 */
	public static Template getTemplate(String location) throws Exception {
		return Velocity.getTemplate(location);
	}	
	
	/**
	 * Merges the template with the supplied context to the writer
	 * @param template To merge
	 * @param context To merge with
	 * @param writer To merge to
	 * @throws ResourceNotFoundException When the template is loading something not
	 * found in the context
	 * @throws ParseErrorException If the template cannot be parsed correctly
	 * @throws MethodInvocationException If the template tries to call a method on
	 * a context object that does not exist 
	 * @throws Exception On some other internal error - e.g the context object calling failed
	 */
	public static void merge(Template template, VelocityContext context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		template.merge(context, writer);
	}
	
	/**
	 * Merges the temlate and returns it as a string.
	 * @see TempalteUtils.merge(Template, VelocityContext, Writer)
	 */
	public static String merge(Template template, VelocityContext context) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		StringWriter writer = new StringWriter();
		merge(template, context, writer);
		return writer.toString();
	}	
}
