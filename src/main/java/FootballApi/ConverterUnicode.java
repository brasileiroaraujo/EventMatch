package FootballApi;

import org.apache.commons.lang3.StringEscapeUtils;

public class ConverterUnicode {
	public static String converter(String text) {
		return StringEscapeUtils.unescapeJava(text);
	}
}

