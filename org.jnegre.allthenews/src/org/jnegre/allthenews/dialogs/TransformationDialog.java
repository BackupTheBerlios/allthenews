/*******************************************************************************
 * Copyright (c) 2005 Jérôme Nègre.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.jnegre.org/cpl1_0.html
 * 
 * Contributors:
 *     Jérôme Nègre - initial API and implementation
 *******************************************************************************/

/*
 * Created on 10 jun. 2005
 */
package org.jnegre.allthenews.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jnegre.allthenews.Transformation;

/**
 * @author Jérôme Nègre
 */
public class TransformationDialog extends Dialog {

	private Text nameText;
	private Text patternText;
	private Text replacementText;
	private Button replaceAllButton;
	
	private final Transformation initTransfo;
	private Transformation result = null;

	/**
	 * @param parentShell
	 */
	public TransformationDialog(Shell parentShell, Transformation init) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.SYSTEM_MODAL);
		this.initTransfo = init;
	}
	
	public TransformationDialog(Shell parentShell) {
		this(parentShell, null);
	}


	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		//composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_CYAN));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Name: ");
		nameText = new Text(composite, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);

		label = new Label(composite, SWT.NONE);
		label.setText("Pattern: ");
		patternText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		patternText.setLayoutData(gd);

		label = new Label(composite, SWT.NONE);
		label.setText("Replace By: ");
		replacementText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		replacementText.setLayoutData(gd);

		label = new Label(composite, SWT.NONE);
		label.setText("Replace All? ");
		replaceAllButton = new Button(composite, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		replaceAllButton.setLayoutData(gd);

		//separation
		Composite hrule = new Composite(composite, SWT.NONE);
		hrule.setBackground(hrule.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.heightHint = 2;
		hrule.setLayoutData(gd);
		
		//test section
		label = new Label(composite, SWT.NONE);
		label.setText("Test input: ");
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(gd);
		final Text testInputText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.widthHint = 200;
		testInputText.setLayoutData(gd);

		//space filler
		Composite spacer = new Composite(composite, SWT.NONE);
		gd = new GridData(0,0);
		spacer.setLayoutData(gd);
		//test button
		Button testButton = new Button(composite, SWT.PUSH);
		testButton.setText("Test!");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		testButton.setLayoutData(gd);

		label = new Label(composite, SWT.NONE);
		label.setText("Test output: ");
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(gd);
		final Text testOutputText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		testOutputText.setLayoutData(gd);

		testButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String result;
				try {
					Transformation transfo = createTransformation();
					result = transfo.apply(testInputText.getText());
				} catch(Exception e) {
					result = e.getMessage();
				}
				testOutputText.setText(result);
			}
		});
		
		populateControls();

		return composite;
	}
	
	protected void populateControls() {
		if(initTransfo != null) {
			nameText.setText(initTransfo.getName());
			patternText.setText(initTransfo.getRegex());
			replaceAllButton.setSelection(initTransfo.isReplaceAll());
			replacementText.setText(initTransfo.getReplacement());
		}
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if(initTransfo == null) {
			newShell.setText("Add New Transformation");
		} else {
			newShell.setText("Edit Transformation");
		}
	}

	Transformation createTransformation() {
		return new Transformation(nameText.getText(), patternText.getText(),
				replacementText.getText(), replaceAllButton.getSelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		try {
			this.result = new Transformation(nameText.getText(), patternText.getText(),
					replacementText.getText(), replaceAllButton.getSelection());
			this.close();
		} catch(Exception e) {
			MessageDialog.openError(
					this.getShell(),
					"Error",
					"The regular expression is invalid. Correct it first.\n\n" +
					e.getMessage());
		}
	}

	protected Transformation getTransformation() {
		return result;
	}
	
}