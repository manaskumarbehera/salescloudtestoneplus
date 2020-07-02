package dk.jyskit.salescloud.application;

import dk.jyskit.salescloud.application.apis.user.UserApiClient;
import org.apache.wicket.util.time.Duration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Start {
	private static final int PORT = 8080;

	/*
	 * Start the application with the following argument
	 *      -Dwaf.env=<env>
	 *      Make sure <env>.properties and common.properties exists in META-INF/env
	 *
	 *  Documentation about using Jetty embedded:
	 *  http://www.eclipse.org/jetty/documentation/9.0.4.v20130621/embedded-examples.html
	 */
	public static void main(String[] args) throws Exception {
		if (System.getProperty("waf.env") == null) {
			System.setProperty("waf.env", "dev");
		}

		int timeout = (int) Duration.ONE_HOUR.getMilliseconds();

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);

		connector.setIdleTimeout(timeout);
		connector.setSoLingerTime(-1);
		connector.setPort(PORT);
		server.addConnector(connector);

		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");
//		context.setWar("src/main/webapp");
		context.setWar("/home/jan/dev/java/projects/jyskit/tdc/salescloud-oneplus/salescloud-mobile/src/main/webapp");

		StopHandler stopHandler = new StopHandler(server);

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] {context, stopHandler});
		server.setHandler(handlers);

		try {
			System.out.print(">>> STARTING EMBEDDED JETTY SERVER ON PORT " + PORT);

			try {
				URL stopUrl = new URL("http://127.0.0.1:" + PORT + "/stop_server");
				BufferedReader in = new BufferedReader(
						new InputStreamReader(stopUrl.openStream()));
			} catch (Exception e) {
			}

			System.out.println(".");

			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static class StopHandler extends AbstractHandler {
		private Server server = null;
		public Boolean restartPlease = false;

		public StopHandler(Server server) {
			this.server = server;
		}

		private boolean stopServer(HttpServletResponse response)
				throws IOException {
			System.out.println("Stopping Jetty");
			response.setStatus(202);
			response.setContentType("text/plain");
			response.flushBuffer();
			try {
				// Stop the server.
				new Thread() {
					@Override
					public void run() {
						try {
							System.out.println("Shutting down Jetty...");
							server.stop();
							System.out.println("Jetty has stopped.");
						} catch (Exception ex) {
							System.out.println("Error when stopping Jetty: "
									+ ex.getMessage());
						}
					}
				}.start();
			} catch (Exception ex) {
				System.out.println("Unable to stop Jetty: " + ex);
				return false;
			}
			return true;
		}

		@Override
		public void handle(String string, Request baseRequest,
						   HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			String pathInfo = request.getPathInfo();
			// THIS SHOULD OBVIOUSLY BE SECURED!!!
			if ("/stop_server".equals(pathInfo)) {
				stopServer(response);
				return;
			}
			if ("/restart_server".equals(pathInfo)) {
				restartPlease = true;
				stopServer(response);
				return;
			}
		}
	}
}
