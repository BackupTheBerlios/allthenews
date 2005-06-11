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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.Transformation;

/**
 * @author Jérôme Nègre
 */
public class ChannelPropertiesDialog extends Dialog {

	private Channel channel;

	private Text titleText;
	private Text urlText;
	protected List list;
	private ArrayList transformations = new ArrayList();

	private SelectionListener selectionListener;
	protected Button addButton;
	protected Button editButton;
	protected Button removeButton;
	protected Button upButton;
	protected Button downButton;

	/**
	 * @param parentShell
	 */
	public ChannelPropertiesDialog(Shell parentShell, Channel channel) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM|SWT.RESIZE|SWT.SYSTEM_MODAL);
		this.channel = channel;
	}
	
	protected Control createDialogArea(Composite parent) {
	      Composite composite = (Composite)super.createDialogArea(parent);
	      
	      GridLayout layout = new GridLayout(2,false);
	      composite.setLayout(layout);
	      
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
	      
	      label = new Label(composite, SWT.NONE);
	      label.setText("Transf.: ");
	      gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	      label.setLayoutData(gd);
	      
	      Composite listBox = new Composite(composite, SWT.NONE);
	      gd = new GridData(GridData.FILL_BOTH);
	      listBox.setLayoutData(gd);
	      layout = new GridLayout(2, false);
	      layout.marginWidth = 0;
	      layout.marginHeight = 0;
	      listBox.setLayout(layout);
	      
	      list = new List(listBox, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	      gd = new GridData(GridData.FILL_BOTH);
	      list.setLayoutData(gd);
	      list.addSelectionListener(getSelectionListener());
	      
	      Composite buttonBox = new Composite(listBox, SWT.NONE);
	      gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	      buttonBox.setLayoutData(gd);
	      layout = new GridLayout();
	      layout.marginWidth = 0;
	      buttonBox.setLayout(layout);

	      addButton = createPushButton(buttonBox, "Add");
	      editButton = createPushButton(buttonBox, "Edit");
	      removeButton = createPushButton(buttonBox, "Remove");
	      upButton = createPushButton(buttonBox, "Up");
	      downButton = createPushButton(buttonBox, "Down");
	      
	      populateControls();
	      
	      return composite;
	   }

	private void populateControls() {
		titleText.setText(channel.getTitle());
		urlText.setText(channel.getUrl());
		Iterator iter = channel.getTransformations().iterator();
		while(iter.hasNext()) {
			Transformation transfo = (Transformation)iter.next();
			list.add(transfo.getName());
			transformations.add(transfo);
		}
		listSelectionChanged();
	}
	
	protected void configureShell(Shell newShell) {
	      super.configureShell(newShell);
	      newShell.setText("Channel Properties");
	   }

	protected void okPressed() {
		channel.setTitle(titleText.getText());
		channel.setUrl(urlText.getText());
		channel.setTransformations(transformations);
		//FIXME right event?
		Plugin.getDefault().notifyChannelStatusChanged(channel, null);
		this.close();
	}
	
	/**
	 * Helper method to create a push button.
	 */
	private Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		this.setButtonLayoutData(button);
		button.setText(label);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == editButton) {
					editPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				} else if (widget == list) {
					listSelectionChanged();
				}
			}
		};
	}

	/**
	 * 
	 */
	protected void listSelectionChanged() {
		int index = list.getSelectionIndex();
		int size = list.getItemCount();
		boolean enabled = index != -1;
		editButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		upButton.setEnabled(enabled && index!=0);
		downButton.setEnabled(enabled && index!=size-1);
	}

	protected void downPressed() {
		int index = list.getSelectionIndex();
		int size = list.getItemCount();
		if(index != -1 && index != size-1) {
			//get transfo at current index and following one
			Transformation currentTr = (Transformation)transformations.get(index);
			Transformation followTr = (Transformation)transformations.get(index+1);
			//replace current index by following one
			list.setItem(index, followTr.getName());
			transformations.set(index, followTr);
			//replace following one by current
			list.setItem(index+1, currentTr.getName());
			transformations.set(index+1, currentTr);
			//done!
			list.setSelection(index+1);
			listSelectionChanged();
			
		}
	}

	protected void upPressed() {
		int index = list.getSelectionIndex();
		if(index != -1 && index != 0) {
			//get transfo at current index and previous one
			Transformation currentTr = (Transformation)transformations.get(index);
			Transformation previousTr = (Transformation)transformations.get(index-1);
			//replace current index by previous one
			list.setItem(index, previousTr.getName());
			transformations.set(index, previousTr);
			//replace previous one by current
			list.setItem(index-1, currentTr.getName());
			transformations.set(index-1, currentTr);
			//done!
			list.setSelection(index-1);
			listSelectionChanged();
			
		}
	}

	protected void removePressed() {
		int index = list.getSelectionIndex();
		if(index != -1) {
			list.remove(index);
			transformations.remove(index);
			list.setSelection(Math.min(index, list.getItemCount()-1));
			listSelectionChanged();
		}
	}

	protected void editPressed() {
		int index = list.getSelectionIndex();
		if(index != -1) {
			Transformation init = (Transformation)transformations.get(index);
			TransformationDialog dialog = new TransformationDialog(this.getShell(), init);
			if(dialog.open() == TransformationDialog.OK) {
				Transformation transfo = dialog.getTransformation();
				list.setItem(index, transfo.getName());
				transformations.set(index, transfo);
				listSelectionChanged();
			}
		}
	}

	protected void addPressed() {
		TransformationDialog dialog = new TransformationDialog(this.getShell());
		if(dialog.open() == TransformationDialog.OK) {
			Transformation transfo = dialog.getTransformation();
			list.add(transfo.getName());
			transformations.add(transfo);
			list.setSelection(list.getItemCount()-1);
			listSelectionChanged();
		}
	}

	/**
	 * Returns this field editor's selection listener.
	 * The listener is created if nessessary.
	 *
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener == null)
			createSelectionListener();
		return selectionListener;
	}

}
