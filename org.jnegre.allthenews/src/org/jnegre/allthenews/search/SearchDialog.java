/*
 * Created on 25 janv. 2004
 *
 * (c)2004 Jérôme Nègre - http://www.jnegre.org/
 */

package org.jnegre.allthenews.search;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jérôme Nègre
 *
 */
public class SearchDialog extends Dialog {


    /**
     * @param parentShell
     */
    public SearchDialog(Shell parentShell) {
        super(parentShell);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MODELESS);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Search using http://www.syndic8.com/");
    }

    /**
     * Adds the controls to the dialog
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        GridLayout gl = (GridLayout)composite.getLayout();
        gl.numColumns = 4;
        //Text to enter the searched words
        Text searchText = new Text(composite,SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        searchText.setLayoutData(gd);
        //Button "search!"
        Button searchButton = new Button(composite,0);
        searchButton.setText("Search!");
        //List for the titles of the feeds
        List list = new List(composite,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.SINGLE);
        gd = new GridData(GridData.FILL_BOTH);
        list.setLayoutData(gd);
        list.add("Feed 1");
        list.add("Feed 2");
        list.add("Feed 3");
        //Description of the selected feed
        Group group = new Group(composite,0);
        group.setText("Selected Field");
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 3;
        group.setLayoutData(gd);
        group.setLayout(new GridLayout(2,false));
        //name
        new Label(group,0).setText("Name:");
        Text feedName = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        feedName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //site url
        new Label(group,0).setText("Site URL:");
        Text feedSiteUrl = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        feedSiteUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //feed url
        new Label(group,0).setText("Feed URL:");
        Text feedUrl = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        feedUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //RSS version
        new Label(group,0).setText("RSS version:");
        Text feedRssVersion = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        feedRssVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //description
        new Label(group,0).setText("Description:");
        Text feedDescription = new Text(group,SWT.BORDER|SWT.READ_ONLY|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
        feedDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
        feedDescription.setText("ligne1\nligne2\nligne3\nligne4");
        return composite;
    }

}
