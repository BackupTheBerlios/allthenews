package org.jnegre.allthenews;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
//import org.eclipse.update.internal.ui.UpdatePerspective;
/**
 * @author Laurent Fourrier, Jérôme Nègre
 *  
 */
public class Perspective implements IPerspectiveFactory {
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.70f, editorArea);
		bottom.addView("org.jnegre.allthenews.view.headline");
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.30f, editorArea);
		left.addView("org.jnegre.allthenews.view.explorer");
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.70f, editorArea);
		right.addView("org.jnegre.allthenews.view.browser");
	}
}