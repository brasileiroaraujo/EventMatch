package WikiAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;

public class WikiTest {
	 public static void main(String[] args) throws IOException {
		URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exintro=&explaintext=&generator=random&grnnamespace=0");
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		
//		JsonElement jsonElement = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
//		JsonElement pages = jsonElement.getAsJsonObject().get("query").getAsJsonObject().get("pages");
//		
//		Set<Entry<String, JsonElement>> entrySet = pages.getAsJsonObject().entrySet();
//		
//		JsonElement yourDesiredElement = null;
//		
//		for(Map.Entry<String,JsonElement> entry : entrySet){
//		    yourDesiredElement = entry.getValue();
//		}
	}
}
