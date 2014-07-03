package com.claymus.site.module.block.types;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.User;
import com.claymus.UserData;
import com.claymus.site.module.block.BlockType;

@PersistenceCapable(detachable = "true")
public class Profile extends BlockType {

	private static final long serialVersionUID = -7243927244314656923L;

	@Override
	public String getName() {
		return "Profile";
	}

	@Override
	protected String getHTML() {
		String content = "<div class='claymus-h2'>Profile</div>";
		User user = UserData.getUser();

		content += "<div class='claymus-t2'>";
		if(user.equals(UserData.getAnonymous())) {
			content += user.getName() + " <span class='claymus-sub-text'>( <a href='" + UserData.getLoginURL() + "'>login</a> )</span>" + "<br/>";
			content += user.getRole().getName();
		} else {
			content += user.getName() + " <span class='claymus-sub-text'>( <a href='" + UserData.getLogoutURL() + "'>logout</a> )</span>" + "<br/>";
			content += user.getEmail() + "<br/>";
			content += user.getRole().getName();
		}
		content += "</div>";

		return content;
	}

}
