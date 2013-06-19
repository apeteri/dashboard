package nl.topicus.onderwijs.dashboard.web.components.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.topicus.onderwijs.dashboard.datasources.ProjectAlerts;
import nl.topicus.onderwijs.dashboard.datatypes.Alert;
import nl.topicus.onderwijs.dashboard.keys.Summary;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

public class AlertsPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<List<Alert>> dataResource;

	public AlertsPanel(String id) {
		super(id);

		this.dataResource = new JsonResourceBehavior<List<Alert>>(
				new AbstractReadOnlyModel<List<Alert>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Alert> getObject() {
						DashboardRepository repository = WicketApplication
								.get().getRepository();
						List<Alert> ret = new ArrayList<Alert>(repository
								.getData(ProjectAlerts.class)
								.get(Summary.get()).getValue());
						Collections.sort(ret, new Comparator<Alert>() {
							@Override
							public int compare(Alert o1, Alert o2) {
								return o1.getKey().compareTo(o2.getKey());
							}
						});
						return ret;
					}
				});
		add(dataResource);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(WidgetJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						AlertsPanel.class, "jquery.ui.dashboardalerts.js")));
		response.render(OnDomReadyHeaderItem.forScript(statement().render()));
	}

	private JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardAlerts", options.getJavaScriptOptions());
	}
}
