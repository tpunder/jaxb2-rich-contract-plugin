/*
 * MIT License
 *
 * Copyright (c) 2014 Klemm Software Consulting, Mirko Klemm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.kscs.util.plugins.xjc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Generates a formatted output of plugin usage information
 *
 * @author mirko 2014-04-03
 */
public class PluginUsageBuilder {
	private final StringWriter stringWriter = new StringWriter();
	private final PrintWriter writer = new PrintWriter(this.stringWriter);
	private final ResourceBundle resourceBundle;
	private final String keyBase;

	public PluginUsageBuilder(final ResourceBundle resourceBundle, final String keyBase) {
		this.resourceBundle = resourceBundle;
		this.keyBase = keyBase;
	}

	public PluginUsageBuilder addMain(final String optionName) {
		this.writer.print(this.resourceBundle.getString(this.keyBase + ".usage"));
		this.writer.println(": -X" + optionName);
		this.writer.println();
		for (final String line : chopLines(80, this.resourceBundle.getString(this.keyBase))) {
			this.writer.println(line);
		}
		this.writer.println("\n"+this.resourceBundle.getString(this.keyBase + ".options") + ":");
		return this;
	}

	public PluginUsageBuilder addOption(final String optionName, final boolean defaultValue) {
		final String key = this.keyBase + "." + transformName(optionName);
		this.writer.println();
		this.writer.print("\t-");
		this.writer.println(optionName + "={y|n} ("+(defaultValue ? "y" : "n")+") :");
		for (final String line : chopLines(70, this.resourceBundle.getString(key))) {
			this.writer.print("\t\t");
			this.writer.println(line);
		}
		this.writer.println();
		return this;
	}


	private static String transformName(final String xmlName) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String word : xmlName.split("\\-")) {
			if (first) {
				sb.append(word);
				first = false;
			} else {
				sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
			}
		}
		return sb.toString();
	}

	private static List<String> chopLines(final int maxLineLength, final String input) {
		final String[] words = input.split(" ");
		final List<String> result = new ArrayList<String>();
		StringBuilder sb = null;
		for (final String word : words) {
			if (word.length() > 0) {
				if (sb == null || sb.length() <= maxLineLength) {
					if (sb == null) {
						sb = new StringBuilder(maxLineLength);
					} else {
						sb.append(" ");
					}
					if (sb.indexOf("\n") >= 0) {
						result.add(sb.substring(0, sb.indexOf("\n")));
						final String rest = sb.substring(sb.indexOf("\n") + 1);
						sb = new StringBuilder(maxLineLength);
						sb.append(rest);
						sb.append(" ");
					}
					sb.append(word);
				} else {
					result.add(sb.toString());
					sb = new StringBuilder(maxLineLength);
					sb.append(word);
				}
			}
		}
		if(sb != null) result.add(sb.toString());
		return result;
	}


	public String build() {
		this.writer.close();
		return this.stringWriter.toString();
	}
}
