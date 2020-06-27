package dk.jyskit.salescloud.application.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class JacksonJava8Module extends SimpleModule {

	@FunctionalInterface
	public interface SerializerFunction<T, G> {
		void apply(T t, G g) throws IOException, JsonProcessingException;
	}

	@FunctionalInterface
	public interface DeserializerFunction<P, R> {
		R apply(P p) throws IOException, JsonProcessingException;
	}

	public <T> void addSerializer(Class<T> cls, SerializerFunction<T, JsonGenerator> serializeFunction) {
		JsonSerializer<T> jsonSerializer = new JsonSerializer<T>() {
			@Override
			public void serialize(T t,
								  JsonGenerator jgen,
								  SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jgen.writeStartObject();
				serializeFunction.apply(t, jgen);
				jgen.writeEndObject();
			}
		};
		addSerializer(cls, jsonSerializer);
	}

	public <T> void addDeserializer(Class<T> cls, DeserializerFunction<JsonParser, T> deserializeFunction) {
		JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<T>() {
			@Override
			public T deserialize(JsonParser jsonParser,
								 DeserializationContext deserializationContext) throws IOException {
				return deserializeFunction.apply(jsonParser);
			}
		};
		addDeserializer(cls, jsonDeserializer);
	}
}