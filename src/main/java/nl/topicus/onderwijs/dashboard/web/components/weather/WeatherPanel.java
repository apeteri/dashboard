package nl.topicus.onderwijs.dashboard.web.components.weather;

import nl.topicus.onderwijs.dashboard.datasources.Weather;
import nl.topicus.onderwijs.dashboard.datatypes.WeatherReport;
import nl.topicus.onderwijs.dashboard.keys.Key;
import nl.topicus.onderwijs.dashboard.modules.DashboardRepository;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.JsonResourceBehavior;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

public class WeatherPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private JsonResourceBehavior<WeatherReport> dataResource;
	private WebMarkupContainer panel;

	public WeatherPanel(String id, final Key key) {
		super(id);

		this.dataResource = new JsonResourceBehavior<WeatherReport>(
				new AbstractReadOnlyModel<WeatherReport>() {
					private static final long serialVersionUID = 1L;

					@Override
					public WeatherReport getObject() {
						DashboardRepository repository = WicketApplication
								.get().getRepository();
						return repository.getData(Weather.class).get(key)
								.getValue();
					}
				});
		add(dataResource);
		panel = new WebMarkupContainer("panel");
		panel.add(new BuienPanel("buienpanel", key));
		add(panel);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(WidgetJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						WeatherPanel.class, "jquery.ui.dashboardweather.js")));
		response.render(OnDomReadyHeaderItem.forScript(statement().render()));
	}

	private JsStatement statement() {
		Options options = new Options();
		options.putLiteral("dataUrl", dataResource.getCallbackUrl().toString());
		JsQuery jsq = new JsQuery(panel);
		return jsq.$()
				.chain("dashboardWeather", options.getJavaScriptOptions());
	}
}
