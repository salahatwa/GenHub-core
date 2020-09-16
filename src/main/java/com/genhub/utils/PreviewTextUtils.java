package com.genhub.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class PreviewTextUtils {
	public static String getText(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.none()).trim();
	}

	public static String getText(String html, int length) {
		String text = getText(html);
		text = StringUtils.abbreviate(text, length);
		return text;
	}

	public static String getSimpleHtml(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.simpleText());
	}

	public static String removeHideHtml(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, (new Whitelist()).addTags("hide"));
	}

	public static List<String> extractImage(String html) {
		List<String> urls = new ArrayList<>();
		if (html == null)
			return urls;
		Document doc = Jsoup.parseBodyFragment(html);
		Elements images = doc.select("img");
		if (null != images) {
			for (Element el : images) {
				urls.add(el.attr("src"));
			}
		}
		return urls;
	}

}
