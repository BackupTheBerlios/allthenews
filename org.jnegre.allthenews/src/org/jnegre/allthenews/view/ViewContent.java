package org.jnegre.allthenews.view;

import org.eclipse.swt.widgets.Composite;
import org.jnegre.allthenews.Channel;

/**
 * @author jnegre - http://www.jnegre.org/
 *
 * (c)Copyright 2002 Jérôme Nègre
 * 
 */
public interface ViewContent {

    public void setFocus();
    public void createPartControl(Composite parent);
    public void fillChannelTable();
    public void refreshChannelContent(Channel channel);
    public void refreshChannelIcon(Channel channel);

}
