package nl.topicus.onderwijs.dashboard.web.components.plot;

import nl.topicus.onderwijs.dashboard.modules.PlotSource;
import nl.topicus.onderwijs.dashboard.modules.PlotSourcesService;
import nl.topicus.wqplot.components.JQPlot;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.options.Options;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

public class PlotPanel extends Panel {
	private class PlotUpdateBehavior extends AbstractDefaultAjaxBehavior {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			replace(plot = getPlotSource().createPlot(plot.getId()));
			target.add(plot);
		}

		@Override
		public CharSequence getCallbackScript() {
			return "function() {" + super.getCallbackScript() + "}";
		}
	}

	private static final long serialVersionUID = 1L;

	private PlotUpdateBehavior updateBehavior;
	private JQPlot plot;

	@SpringBean
	private PlotSourcesService plotSources;

	private int plotSourceIndex;

	public PlotPanel(String id, int plotSourceIndex) {
		super(id);
		this.plotSourceIndex = plotSourceIndex;
		updateBehavior = new PlotUpdateBehavior();
		add(updateBehavior);
		add(plot = getPlotSource().createPlot("plot"));
	}

	private PlotSource getPlotSource() {
		return plotSources.getPlotSources().get(plotSourceIndex);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem
				.forReference(WidgetJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(PlotPanel.class,
						"jquery.ui.dashboardplot.js")));
		response.render(OnDomReadyHeaderItem.forScript(statement().render()));
	}

	private JsStatement statement() {
		Options options = new Options();
		options.put("callback", updateBehavior.getCallbackScript().toString());
		JsQuery jsq = new JsQuery(this);
		return jsq.$().chain("dashboardPlot", options.getJavaScriptOptions());
	}
}
