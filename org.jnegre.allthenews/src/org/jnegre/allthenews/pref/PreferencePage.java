package org.jnegre.allthenews.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jnegre.allthenews.Plugin;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected SiteListEditor siteListFE;
    protected BanListEditor banListFE;
    protected FileFieldEditor browserAppFE;
    protected RadioGroupFieldEditor fe9;
    protected IntegerFieldEditor refreshFE;
    protected RadioGroupFieldEditor viewTypeFE;
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
        fe9 =
            new RadioGroupFieldEditor(
                Plugin.BROWSER_TYPE_PREFERENCE,
                "Browser",
                1,
                new String[][] { { "Embedded Browser (win32 only)", "1" }, {
                "External Browser", "2" }
        }, getFieldEditorParent(), true);
        browserAppFE = new FileFieldEditor(Plugin.BROWSER_PREFERENCE, "External Browser", getFieldEditorParent());

        refreshFE = new IntegerFieldEditor(Plugin.REFRESH_INTERVAL_PREFERENCE,"Refresh interval (minutes)", getFieldEditorParent());
        refreshFE.setValidRange(0,10000);

        forceCacheFE = new BooleanFieldEditor(Plugin.FORCE_CACHE_PREFERENCE,"Force refresh from proxy", getFieldEditorParent());

        viewTypeFE =
            new RadioGroupFieldEditor(
                Plugin.VIEW_TYPE_PREFERENCE,
                "View Type (restart of Eclipse needed)",
                3,
                new String[][] { { "Split View", "1" }, {
                "Tab View", "2" }, { "Tree View", "3" }
        }, getFieldEditorParent(), true);

        addField(siteListFE);
        addField(banListFE);
        addField(fe9);
        addField(browserAppFE);
        addField(refreshFE);
        addField(forceCacheFE);
        addField(viewTypeFE);
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
