package org.jnegre.allthenews.pref;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.jnegre.allthenews.Plugin;

public class SiteListEditor extends ListEditor {

    //protected Button editButton;
    
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
     * @see org.eclipse.jface.preference.ListEditor#getButtonBoxControl(Composite)
     */
//    public Composite getButtonBoxControl(Composite parent) {
//        Composite composite = super.getButtonBoxControl(parent);
//        editButton = new Button(composite,SWT.CENTER);
//        editButton.setText("&Edit");
//        //Copy the layout data...
//        GridData gridData = new GridData();
//        GridData originalGridData = (GridData)composite.getChildren()[0].getLayoutData();
//        gridData.grabExcessHorizontalSpace = originalGridData.grabExcessHorizontalSpace;
//        gridData.grabExcessVerticalSpace = originalGridData.grabExcessVerticalSpace;
//        gridData.heightHint = originalGridData.heightHint;
//        gridData.horizontalAlignment = originalGridData.horizontalAlignment;
//        gridData.horizontalIndent = originalGridData.horizontalIndent;
//        gridData.horizontalSpan = originalGridData.horizontalSpan;
//        gridData.verticalAlignment = originalGridData.verticalAlignment;
//        gridData.verticalSpan = originalGridData.verticalSpan;
//        gridData.widthHint = originalGridData.widthHint;
//        editButton.setLayoutData(gridData);
//        editButton.addSelectionListener(new SelectionAdapter(){
//            /**
//             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(SelectionEvent)
//             */
//            public void widgetSelected(SelectionEvent e) {
//                super.widgetSelected(e);
//                /**@todo... */
//            }
//
//        });
//        return composite;
//    }

}

