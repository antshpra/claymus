package com.claymus.site.module.content;

import java.io.Serializable;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.Resources;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
@PersistenceCapable(table = "ContentData", detachable = "true")
@Discriminator(column = "Type", strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class ContentType implements Serializable {

	@SuppressWarnings("unused")
	@Persistent(column = "Key", primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	/*
	 * Getter and Setter Methods
	 */

	final void setKey(Key key) {
    	this.key = key;
    }

	/*
	 * Methods to be overridden by Inheriting class
	 */

	@NotPersistent
	protected String gwtModule = null;

	@NotPersistent
	protected boolean richTextEditor = false;

	public boolean hasEditor() {
		return this.gwtModule != null || this.richTextEditor;
	}

	public String getEditor() {
		if(this.gwtModule == null && ! this.richTextEditor)
			return null;

		return "<div id='claymus-ContentEditor'></div>" +
				(this.richTextEditor ? Resources.getCKEditor() : "") +
				(this.gwtModule != null ? "<script type='text/javascript' src='/" + this.gwtModule + "/" + this.gwtModule	+ ".nocache.js'></script>" : "");
	}


    public abstract String getName();

	protected abstract String getHTML();


	protected ContentDTO getDTO() { return null; }

	protected void update(ContentDTO moduleDTO) {}

}