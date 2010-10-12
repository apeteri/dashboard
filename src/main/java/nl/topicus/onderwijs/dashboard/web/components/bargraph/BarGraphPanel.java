package nl.topicus.onderwijs.dashboard.web.components.bargraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.NumberOfServers;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.Project;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.commons.WiQueryUIPlugin;
import org.odlabs.wiquery.ui.widget.WidgetJavascriptResourceReference;

@WiQueryUIPlugin
public class BarGraphPanel extends Panel implements IWiQueryPlugin {
	private static final long serialVersionUID = 1L;

	public BarGraphPanel(String id) {
		super(id);
		ListView<Project> bars = new ListView<Project>("bars",
				WicketApplication.get().getProjects()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Project> item) {

				ArrayList<Class<? extends DataSource<? extends Number>>> datasources = new ArrayList<Class<? extends DataSource<? extends Number>>>();
				datasources.add(NumberOfUsers.class);
				datasources.add(NumberOfServers.class);
				item
						.add(new BarGraphBarPanel(
								"bar",
								item.getModel(),
								new ListModel<Class<? extends DataSource<? extends Number>>>(
										datasources)));
			}
		};
		add(bars);
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(WidgetJavascriptResourceReference.get());
		manager.addJavaScriptResource(BarGraphBarPanel.class,
				"jquery.ui.dashboardbargraphmaster.js");
	}

	@Override
	public JsStatement statement() {
		ObjectMapper mapper = new ObjectMapper();
		List<BarDataSet> dataSets = new ArrayList<BarDataSet>();

		dataSets.add(new BarDataSet(NumberOfUsers.class, "Live sessions",
				"color-1"));
		dataSets.add(new BarDataSet(NumberOfServers.class, "Number of servers",
				"color-2"));

		Options options = new Options();
		try {
			options.put("dataSets", mapper.writeValueAsString(dataSets));
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardBarGraphMaster",
				options.getJavaScriptOptions());
	}
}
