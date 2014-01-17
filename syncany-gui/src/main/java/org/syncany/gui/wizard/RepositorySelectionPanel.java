/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2013 Philipp C. Heckel <philipp.heckel@gmail.com> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.gui.wizard;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.syncany.connection.plugins.Plugin;
import org.syncany.connection.plugins.Plugins;
import org.syncany.gui.ApplicationResourcesManager;
import org.syncany.gui.CommonParameters;
import org.syncany.gui.UserInput;
import org.syncany.gui.WidgetDecorator;
import org.syncany.gui.panel.PluginPanel;
import org.syncany.util.I18n;
import org.syncany.util.StringUtil;

/**
 * @author Vincent Wiencek <vwiencek@gmail.com>
 *
 */
public class RepositorySelectionPanel extends WizardPanelComposite {
	private static final Logger log = Logger.getLogger(RepositorySelectionPanel.class.getSimpleName());
	
	private Combo repositorySelectionCombo;
	private List<Plugin> pluginList;
	private Composite pluginStackComposite;
	private StackLayout rootStackLayout;
	private String selectedPluginId;
	private Map<String, PluginPanel> panels = new HashMap<>();
	private StackLayout stackLayout;
	private Label chooseRepositoryLabel;
	private Composite rootComposite;
	private Composite createComposite;
	private Composite urlComposite;
	private Label urlLabel;
	private Text urlText;
	private Label urlIntroductionLabel;
	private Label urlIntroductionTitleLabel;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RepositorySelectionPanel(WizardDialog wizardParentDialog, Composite parent, int style) {
		super(wizardParentDialog, parent, style);
		this.pluginList = Plugins.list();
		initComposite();
	}
	
	private void initComposite(){
		setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginRight = 30;
		setLayout(gl_composite);
		
		rootComposite = new Composite(this, SWT.NONE);
		rootStackLayout = new StackLayout();
		
		rootComposite.setLayout(rootStackLayout);
		rootComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		urlComposite = new Composite(rootComposite, SWT.NONE);
		urlComposite.setLayout(new GridLayout(2, false));
		
		urlIntroductionTitleLabel =new Label(urlComposite, SWT.WRAP);
		urlIntroductionTitleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		urlIntroductionTitleLabel.setText(I18n.getString("dialog.chooseRepository.url.introduction.title"));
		
		urlIntroductionLabel =new Label(urlComposite, SWT.WRAP);
		urlIntroductionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		urlIntroductionLabel.setText(I18n.getString("dialog.chooseRepository.url.introduction"));
		
		urlLabel = new Label(urlComposite, SWT.NONE);
		GridData gd_urlLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_urlLabel.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		urlLabel.setLayoutData(gd_urlLabel);
		urlLabel.setText(I18n.getString("dialog.chooseRepository.url", true));
		
		urlText = new Text(urlComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		GridData gd_urlText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_urlText.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		gd_urlText.heightHint = 95;
		urlText.setLayoutData(gd_urlText);
		
		createComposite = new Composite(rootComposite, SWT.NONE);
		createComposite.setLayout(new GridLayout(1, false));
		
		Label introductionTitleLabel =new Label(createComposite, SWT.NONE);
		introductionTitleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		introductionTitleLabel.setSize(262, 17);
		introductionTitleLabel.setText(I18n.getString("dialog.chooseRepository.introduction.title"));
		
		Label introductionLabel =new Label(createComposite, SWT.WRAP);
		introductionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		introductionLabel.setSize(218, 17);
		introductionLabel.setText(I18n.getString("dialog.chooseRepository.introduction"));
		
		chooseRepositoryLabel =new Label(createComposite, SWT.NONE);
		chooseRepositoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		chooseRepositoryLabel.setSize(225, 17);
		chooseRepositoryLabel.setText(I18n.getString("dialog.chooseRepository.choosePlugin", true));
		
		repositorySelectionCombo = new Combo(createComposite, SWT.READ_ONLY);
		GridData gd_repositorySelectionCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_repositorySelectionCombo.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		repositorySelectionCombo.setLayoutData(gd_repositorySelectionCombo);
		repositorySelectionCombo.setSize(387, 25);
		repositorySelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String id = (String)repositorySelectionCombo.getData(repositorySelectionCombo.getItem(repositorySelectionCombo.getSelectionIndex()));
				showPLuginPanel(id);;
			}
		});
		
		pluginStackComposite = new Composite(createComposite, SWT.NONE);
		pluginStackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		pluginStackComposite.setSize(242, 154);
		stackLayout = new StackLayout();
		pluginStackComposite.setLayout(stackLayout);
		
		for (Plugin p : pluginList){
			repositorySelectionCombo.add(p.getName());
			repositorySelectionCombo.setData(p.getName(), p.getId());
		}
		
		for (Plugin p : pluginList){
			String pluginPanelClassName = String.format("org.syncany.gui.plugin.%sPluginPanel", StringUtil.toCamelCase(p.getId()));
			
			try {
				Class<?>[] type = { Composite.class, int.class };
				Class<?> classDefinition = Class.forName(pluginPanelClassName);
				Constructor<?> cons = classDefinition.getConstructor(type);
				Object[] obj = { pluginStackComposite, SWT.NONE};
				
				PluginPanel pluginPanel = (PluginPanel) cons.newInstance(obj);
				panels.put(p.getId(), pluginPanel);
			}
			catch (Exception e) {
				log.warning("Unable to instanciate plugin gui panel " + pluginPanelClassName);
			}
		}
		int idxSelectedPlugin = 0;
		repositorySelectionCombo.select(idxSelectedPlugin);

		showPLuginPanel(pluginList.get(idxSelectedPlugin).getId());
		
		WidgetDecorator.bold(urlIntroductionTitleLabel, introductionTitleLabel);
		WidgetDecorator.normal(urlIntroductionLabel, introductionLabel, chooseRepositoryLabel, urlText);
	}
	
	private void showPLuginPanel(String id){
		this.selectedPluginId = id;
		PluginPanel ppanel = panels.get(id);
		stackLayout.topControl = ppanel;
		pluginStackComposite.layout();
		ppanel.setAction(getParentWizardDialog().getUserInput().getCommonParameter(CommonParameters.COMMAND_ACTION));
	}

	@Override
	public boolean isValid() {
		PluginPanel ppanel = panels.get(selectedPluginId);
		return ppanel.isValid(); 
	}

	@Override
	public UserInput getUserSelection() {
		String id = (String)repositorySelectionCombo.getData(repositorySelectionCombo.getItem(repositorySelectionCombo.getSelectionIndex()));

		UserInput userInput = new UserInput();
		userInput.putCommonParameter(CommonParameters.PLUGIN_ID, id);
		UserInput pluginParameters = panels.get(id).getUserSelection();
		userInput.merge(pluginParameters);
		return userInput;
	}

	@Override
	public boolean hasNextButton() {
		return true;
	}

	@Override
	public boolean hasPreviousButton() {
		return true;
	}

	@Override
	public boolean hasFinishButton() {
		return false;
	}

	@Override
	public void updateData() {
		String action = getParentWizardDialog().getUserInput().getCommonParameter(CommonParameters.COMMAND_ACTION);
		boolean url = "yes".equals(getParentWizardDialog().getUserInput().getCommonParameter(CommonParameters.AVAILABLE_URL)) ? true : false;
		
		if (action.equals("create")  || (action.equals("connect") && !url)){
			rootStackLayout.topControl = createComposite;
		}
		else{
			rootStackLayout.topControl = urlComposite;
		}
		rootComposite.layout();
	}
}