package org.jnegre.allthenews;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.update.internal.ui.UpdatePerspective;

/**
 * @author Laurent Fourrier
 *
 */
public class Perspective implements IPerspectiveFactory {

/**
* @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(IPageLayout)
*/
public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
layout.setEditorAreaVisible(false);
      
        IFolderLayout left =
                layout.createFolder("left", IPageLayout.LEFT, (float) 0.20, editorArea);
        left.addView("org.jnegre.allthenews.view");
        IFolderLayout right =
                layout.createFolder("right", IPageLayout.RIGHT, (float) 0.80,editorArea);
        right.addView(UpdatePerspective.ID_BROWSER);
}
}
