package org.jnegre.allthenews.pref;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.search.SearchDialog;

public class SiteListEditor extends ListEditor {

    protected Button searchButton;
    
    /**
     * Constructor for SiteListEditor
     */
    protected SiteListEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    /**
     * @see ListEditor#parseString(String)
     */
    protected String[] parseString(String stringList) {
        return ListEncoder.decode(stringList);
    }

    /**
     * @see ListEditor#getNewInputObject()
     */
    protected String getNewInputObject() {
        InputDialog dialog;
        String result;
        dialog = new InputDialog(this.getShell(),"All The News","Enter new site name","",null);
        dialog.open();
        if("".equals(dialog.getValue()) || dialog.getValue()==null)
        	return null;
        result = dialog.getValue();
        dialog = new InputDialog(this.getShell(),"All The News","Enter new site URL","",null);
        dialog.open();
        if("".equals(dialog.getValue()) || dialog.getValue()==null)
        	return null;
        result += " \u00B6 "+dialog.getValue();
        return result;
    }

    /**
     * @see ListEditor#createList(String[])
     */
    protected String createList(String[] items) {
        return ListEncoder.encode(items);
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    protected void doStore() {
        super.doStore();
        Plugin.getDefault().updateChannelList();
    }


    /**
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    protected void doLoadDefault() {
        super.doLoadDefault();
        Plugin.getDefault().updateChannelList();
    }


    /**
     * Adds the search button to usual buttons
     * @see org.eclipse.jface.preference.ListEditor#getButtonBoxControl(Composite)
     */
    public Composite getButtonBoxControl(Composite parent) {
        Composite composite = super.getButtonBoxControl(parent);
        searchButton = new Button(composite,SWT.CENTER);
        searchButton.setText("&Search...");
        //Copy the layout data...
        GridData gridData = new GridData();
        GridData originalGridData = (GridData)composite.getChildren()[0].getLayoutData();
        gridData.grabExcessHorizontalSpace = originalGridData.grabExcessHorizontalSpace;
        gridData.grabExcessVerticalSpace = originalGridData.grabExcessVerticalSpace;
        gridData.heightHint = originalGridData.heightHint;
        gridData.horizontalAlignment = originalGridData.horizontalAlignment;
        gridData.horizontalIndent = originalGridData.horizontalIndent;
        gridData.horizontalSpan = originalGridData.horizontalSpan;
        gridData.verticalAlignment = originalGridData.verticalAlignment;
        gridData.verticalSpan = originalGridData.verticalSpan;
        gridData.widthHint = originalGridData.widthHint;
        searchButton.setLayoutData(gridData);
        searchButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                SearchDialog sd = new SearchDialog(SiteListEditor.this.getShell());
                sd.open();
                //TODO use the result...
            }
        });
        return composite;
    }

}

