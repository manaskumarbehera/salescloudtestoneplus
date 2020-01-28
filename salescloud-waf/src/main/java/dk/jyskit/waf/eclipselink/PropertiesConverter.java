package dk.jyskit.waf.eclipselink;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Persist Properties via EclipseLink
 *
 * @author georgi.knox
 *
 */
@Converter(autoApply=false)
public class PropertiesConverter implements AttributeConverter<Properties, String> {

		@Override
		public String convertToDatabaseColumn(Properties attribute) {
			if (attribute == null) return null;
			try {
				StringWriter wr = new StringWriter();
				attribute.store(wr, null);
				String string = wr.getBuffer().toString();
				// first comment line
				if (string.startsWith("#")) {
					string = string.substring(string.indexOf('\n') + 1);
				}
				return string;
			} catch (IOException e) {
				throw new RuntimeException("PropertiesConverter cannot convert from propeties to string", e);
			}
		}

		@Override
		public Properties convertToEntityAttribute(String dbData) {
			if (dbData == null) return null;
    	try {
				Properties props = new Properties();
				props.load(new StringReader((dbData)));
				return props;
			} catch (IOException e) {
        throw new RuntimeException("Conversion exception, value is not of Properties type.", e);
			}
		}
}
