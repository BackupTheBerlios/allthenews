/*
 * Created on 25 janv. 2004
 *
 * (c)2004 Jérôme Nègre - http://www.jnegre.org/
 */

package org.jnegre.allthenews.search;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

    private Text searchText;
    private List list;
    private Text name;
    private Text siteUrl;
    private Text feedUrl;
    private Text version;
    private Text description;
    
    private XmlRpcClient xmlRpcClient;
    
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
        searchText = new Text(composite,SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        searchText.setLayoutData(gd);
        //Button "search!"
        Button searchButton = new Button(composite,0);
        searchButton.setText("Search!");
        searchButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //TODO use a worker
                //TODO change the cursor (wait...)
                try {
                    SearchDialog.this.searchNow();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //TODO put the original cursor back
            }
        });
        //List for the titles of the feeds
        list = new List(composite,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.SINGLE);
        gd = new GridData(GridData.FILL_BOTH);
        list.setLayoutData(gd);
        //Description of the selected feed
        Group group = new Group(composite,0);
        group.setText("Selected Feed");
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 3;
        group.setLayoutData(gd);
        group.setLayout(new GridLayout(2,false));
        //name
        new Label(group,0).setText("Name:");
        name = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //site url
        new Label(group,0).setText("Site URL:");
        siteUrl = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        siteUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //feed url
        new Label(group,0).setText("Feed URL:");
        feedUrl = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        feedUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //RSS version
        new Label(group,0).setText("RSS version:");
        version = new Text(group,SWT.BORDER|SWT.READ_ONLY);
        version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //description
        new Label(group,0).setText("Description:");
        description = new Text(group,SWT.BORDER|SWT.READ_ONLY|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL|SWT.WRAP);
        description.setLayoutData(new GridData(GridData.FILL_BOTH));
        return composite;
    }

    private XmlRpcClient getXmlRpcClient() throws MalformedURLException {
        if(this.xmlRpcClient == null) {
            this.xmlRpcClient = new XmlRpcClient("http://www.syndic8.com/xmlrpc.php");
        }
        return this.xmlRpcClient;
    }

    /**
     * 
     */
    protected void searchNow() throws Exception {
      XmlRpcClient client = getXmlRpcClient();
      //Get the list of ids
      Vector args = new Vector();
      args.add(searchText.getText());
      Vector ids = (Vector)client.execute("syndic8.FindFeeds",args);
      System.out.println("=> "+ids);
      //Get the descriptions of the feeds
      Vector fields = new Vector();
      fields.add("sitename");
      fields.add("siteurl");
      fields.add("dataurl");
      fields.add("rss_version");
      fields.add("description");
      args.clear();
      args.add(ids);
      args.add(fields);
      Vector infos = (Vector)client.execute("syndic8.GetFeedInfo",args);
      Hashtable info = (Hashtable)infos.get(0);
      name.setText((String)info.get("sitename"));
      siteUrl.setText((String)info.get("siteurl"));
      feedUrl.setText((String)info.get("dataurl"));
      version.setText((String)info.get("rss_version"));
      description.setText((String)info.get("description"));
      
    }

}
