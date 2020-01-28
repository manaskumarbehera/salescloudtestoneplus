package dk.jyskit.salescloud.application.services.cvr;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.commons.collections4.map.LRUMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mysql.jdbc.util.LRUCache;

public class CVRService {
	private final Fetcher fetcher;
	private final Gson gson;
	private static LRUMap<Integer, String> cache = new LRUMap<>(400);

	public CVRService(Fetcher fetcher) {
		this.fetcher = requireNonNull(fetcher);
		gson = new GsonBuilder()
			.registerTypeAdapter(Boolean.class, BOOLEAN_AS_INT_ADAPTER)
			.registerTypeAdapter(boolean.class, BOOLEAN_AS_INT_ADAPTER)
			.setDateFormat("dd/MM - yyyy")
			.create();
	}

	public CVRService() {
		this(new DefaultFetcher());
	}

	public Response fetchDetails(int cvr) {
		String response = cache.get(cvr);
		if (response == null) {
			response = fetcher.getUrl("http://cvrapi.dk/api?search=" + cvr + "&country=dk&version=6");
			cache.put(cvr, response);
		}
		return gson.fromJson(response, Response.class);
	}

	public static interface Fetcher {
		String getUrl(String url);
	}

	private static final TypeAdapter<Boolean> BOOLEAN_AS_INT_ADAPTER = new TypeAdapter<Boolean>() {
		@Override
		public void write(JsonWriter out, Boolean value) throws IOException {
			if (value == null)
				out.nullValue();
			else
				out.value(value);
		}

		@Override
		public Boolean read(JsonReader in) throws IOException {
			JsonToken type = in.peek();
			switch (type) {
			case BOOLEAN:
				return in.nextBoolean();
			case NUMBER:
				return in.nextInt() != 0;
			case STRING:
				return in.nextString().equals("1");
			default:
				throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + type);
			}
		}
	};

	public static class Response {
		@SerializedName("vat")
		public int cvr;
		
		@SerializedName("name")
		public String navn;
		
		@SerializedName("address")
		public String adresse;
		
		@SerializedName("zipcode")
		public String postnr;
		
		@SerializedName("city")
		public String by;
		
		@SerializedName("protected")
		public boolean rbeskyttet;
		
		@SerializedName("phone")
		public String telefon;
		
		public String fax;
		
		public String email;
		
		@SerializedName("startdate")
		public Date startdato;
		
		@SerializedName("industrycode")
		public int branchekode;
		
		@SerializedName("industrydesc")
		public String branchetekst;
		
		@SerializedName("companycode")
		public int virkformkode;
		
		@SerializedName("companydesc")
		public String virkformtekst;
		
		@SerializedName("employees")
		public String ansatte;
		
//		@SerializedName("owners")
//		public String ejer;
	}

	private static class DefaultFetcher implements Fetcher {
		@Override
		public String getUrl(String url) {
			StringBuilder content = new StringBuilder();

			// many of these calls can throw exceptions, so i've just
			// wrapped them all in one try/catch statement.
			try {
				URLConnection urlConnection = new URL(url).openConnection();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

				String line;

				while ((line = bufferedReader.readLine()) != null) {
					content.append(line + "\n");
				}
				bufferedReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return content.toString();
		}
	}
}