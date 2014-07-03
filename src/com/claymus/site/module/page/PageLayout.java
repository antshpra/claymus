package com.claymus.site.module.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import com.claymus.site.module.content.Content;
import com.claymus.site.module.theme.Theme;

@SuppressWarnings("serial")
public abstract class PageLayout implements Serializable {

	public abstract String getName();

	public String getDefaultLocation() {
		return "CENTER";
	}

	public String[][] getLocations() {
		return new String[][] {
				{ "Center", "CENTER" }
		};
	}

	public Object[][][] get() {
		return new Object[][][] {
				{ { 1, 1, "CENTER" } }
		};
	}

	public void generate(List<List<Content>> content, Theme theme, PrintWriter out) throws IOException {
		theme.printContent(content.get(0), out);
	}

}
