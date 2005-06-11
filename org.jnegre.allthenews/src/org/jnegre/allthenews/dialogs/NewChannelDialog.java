/*******************************************************************************
 * Copyright (c) 2004 Jérôme Nègre.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.jnegre.org/cpl1_0.html
 * 
 * Contributors:
 *     Jérôme Nègre - initial API and implementation
 *******************************************************************************/

/*
 * Created on 19 déc. 2004
 */
package org.jnegre.allthenews.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Plugin;

/**
 * @author Jérôme Nègre
 */
public class NewChannelDialog extends Dialog {

	private Text titleText;
	private Text urlText;
	
	/**
	 * @param parentShell
	 */
	public NewChannelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM|SWT.RESIZE|SWT.SYSTEM_MODAL);
	}
	
	protected Control createDialogArea(Composite parent) {
	      Composite composite = (Composite)super.createDialogArea(parent);
	      GridLayout layout = new GridLayout(2,false);
	      composite.setLayout(layout);
	      //composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_CYAN));
	      
	      Label label = new Label(composite, SWT.NONE);
	      label.setText("Title: ");
	      titleText = new Text(composite, SWT.BORDER);
	      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	      titleText.setLayoutData(gd);

	      label = new Label(composite, SWT.NONE);
	      label.setText("Url: ");
	      urlText = new Text(composite, SWT.BORDER);
	      gd = new GridData(GridData.FILL_HORIZONTAL);
	      urlText.setLayoutData(gd);

	      return composite;
	   }

	protected void configureShell(Shell newShell) {
	      super.configureShell(newShell);
	      newShell.setText("Add New Channel");
	   }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		Channel channel = new Channel(titleText.getText(), urlText.getText());
		List channels = new ArrayList(Plugin.getDefault().getChannelList());
		channels.add(channel);
		Plugin.getDefault().updateChannelList(channels);
		Plugin.getDefault().update();
		this.close();
	}
	
}
