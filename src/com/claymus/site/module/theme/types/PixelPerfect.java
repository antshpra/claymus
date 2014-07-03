package com.claymus.site.module.theme.types;

import com.claymus.site.module.theme.Theme;

public class PixelPerfect extends Theme {

	private static final long serialVersionUID = 1190541676302680228L;

	@Override
	public String getName() {
		return "Pixel Perfect";
	}

	@Override
	public String getCSS() {
		return Theme.GWT_Standad
				+ super.getCSS()
				+ "<link type='text/css' rel='stylesheet' href='/com.claymus.site/theme.pixelperfect/style.css'>";
	}

}
