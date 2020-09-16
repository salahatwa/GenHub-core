package com.genhub.utils;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MarkdownUtils {
	private static final List<Extension> EXTENSIONS = Arrays.asList(YamlFrontMatterExtension.create(),
			TablesExtension.create());

	private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

	private static final HtmlRenderer RENDERER = HtmlRenderer.builder().extensions(EXTENSIONS)
			.attributeProviderFactory(context -> new BlogAttributeProvider()).build();

	public static String renderMarkdown(String content) {
		final Node document = PARSER.parse(content);
		return RENDERER.render(document);
	}

	static class BlogAttributeProvider implements AttributeProvider {

		@Override
		public void setAttributes(Node node, String s, Map<String, String> map) {
			if (node instanceof TableBlock) {
				map.put("class", "table table-bordered");
			}
		}
	}
}
