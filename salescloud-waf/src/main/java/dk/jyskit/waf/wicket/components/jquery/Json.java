package dk.jyskit.waf.wicket.components.jquery;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;

/** 
 * Customized JSON encoder. 
 * TODO add {@link JsonSerializer}s to support missing data types.
 * @author palfred
 *
 */
public class Json {
	private Json() {

	}

	private static class Factory extends MappingJsonFactory {
		@Override
		public JsonGenerator createJsonGenerator(Writer out) throws IOException {
			return super.createGenerator(out).useDefaultPrettyPrinter();
		}

		@Override
		public JsonGenerator createJsonGenerator(File f, JsonEncoding enc) throws IOException {
			return super.createGenerator(f, enc).useDefaultPrettyPrinter();
		}

		@Override
		public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
			return super.createGenerator(out, enc).useDefaultPrettyPrinter();
		}
	}

	public static String toJson(Object object) {
		ObjectMapper mapper = new ObjectMapper(new Factory());
		SimpleModule module = new SimpleModule("jyskitjquery", new Version(1, 0, 0, null, "dk.jyskit", "wicketjquery"));
		module.addSerializer(new DateTimeSerializer());
		module.addSerializer(new LocalTimeSerializer());
		module.addSerializer(new ScriptSerializer());
		mapper.registerModule(module);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException("Error encoding object: " + object + " into JSON string", e);
		}
	}

	public static class DateTimeSerializer extends JsonSerializer<DateTime> {
		@Override
		public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
			jgen.writeString(ISODateTimeFormat.dateTime().print(value));
		}

		@Override
		public Class<DateTime> handledType() {
			return DateTime.class;
		}

	}

	public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
		@Override
		public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
			jgen.writeString(value.toString("h:mmaa"));
		}

		@Override
		public Class<LocalTime> handledType() {
			return LocalTime.class;
		}

	}

	public static class CssClassSerializer extends JsonSerializer<ICssClassNameProvider> {

		@Override
		public void serialize(ICssClassNameProvider value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeString(value.cssClassName());
		}
		
		@Override
		public Class<ICssClassNameProvider> handledType() {
			return ICssClassNameProvider.class;
		}
	}
	
	public static class ScriptSerializer extends JsonSerializer<JsScript> {
		@Override
		public void serialize(JsScript value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
			jgen.writeRawValue(value.toScript());
		}

		@Override
		public Class<JsScript> handledType() {
			return JsScript.class;
		}

	}

}