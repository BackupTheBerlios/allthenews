package org.jnegre.allthenews.pref;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.jnegre.allthenews.Plugin;

public class BanListEditor extends ListEditor {

    /**
     * Constructor for SiteListEditor
     */
    protected BanListEditor(String name, String labelText, Composite parent) {
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
        dialog = new InputDialog(this.getShell(),"All The News","Enter item title to ban","",null);
        dialog.open();
        if("".equals(dialog.getValue()) || dialog.getValue()==null) {
        	return null;
        } else {
            return dialog.getValue();
        }
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
        Plugin.getDefault().updateBanList();
    }


    /**
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    protected void doLoadDefault() {
        super.doLoadDefault();
        Plugin.getDefault().updateBanList();
    }
}

