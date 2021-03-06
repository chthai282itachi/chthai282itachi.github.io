package ch.unibas.medizin.osce.client.style.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface AnamnesisCheckImageResources extends ClientBundle {
		
	@Source("images/up.png")
//	@ImageOptions(repeatStyle = RepeatStyle.Horizontal, flipRtl = true)
	ImageResource upImage();

	@Source("images/down.png")
//	@ImageOptions(repeatStyle = RepeatStyle.Horizontal, flipRtl = true)
	ImageResource downImage();
	
	@Source("images/right.png")
//	@ImageOptions(repeatStyle = RepeatStyle.Horizontal, flipRtl = true)
	ImageResource rightImage();
	
	
}
