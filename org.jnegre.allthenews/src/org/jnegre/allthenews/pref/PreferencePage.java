package org.jnegre.allthenews.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jnegre.allthenews.Plugin;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected SiteListEditor siteListFE;
    protected BanListEditor banListFE;
    protected FileFieldEditor browserAppFE;
    protected IntegerFieldEditor refreshFE;
    protected BooleanFieldEditor forceCacheFE;

    public PreferencePage() {
        super("All The News", FieldEditorPreferencePage.GRID);
    }

    /**
     * @see FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {
        siteListFE = new SiteListEditor(Plugin.BACKENDS_PREFERENCE, "Sites", getFieldEditorParent());
        banListFE = new BanListEditor(Plugin.BANNED_ITEMS_PREFERENCE, "Banned items", getFieldEditorParent());
        /*
        browserAppFE = new FileFieldEditor(Plugin.BROWSER_PREFERENCE, "External Browser", getFieldEditorParent());
        */
        refreshFE = new IntegerFieldEditor(Plugin.REFRESH_INTERVAL_PREFERENCE,"Refresh interval (minutes)", getFieldEditorParent());
        refreshFE.setValidRange(0,10000);

        forceCacheFE = new BooleanFieldEditor(Plugin.FORCE_CACHE_PREFERENCE,"Force refresh from proxy", getFieldEditorParent());

        addField(siteListFE);
        addField(banListFE);
        /*
        addField(browserAppFE);
        */
        addField(refreshFE);
        addField(forceCacheFE);
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        boolean result = super.performOk();
        Plugin.getDefault().setTimer();
        return result;
    }

}
