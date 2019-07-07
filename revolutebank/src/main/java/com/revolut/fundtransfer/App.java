package com.revolut.fundtransfer;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.revolut.fundtransfer.dao.DBDAOFactory;
import com.revolut.fundtransfer.service.impl.AccountServiceImpl;
import com.revolut.fundtransfer.service.impl.FundTransferServiceImpl;
import com.revolut.fundtransfer.service.impl.UserServiceImpl;

/**
 * 
 * @author Vivek Shah
 *
 */
public class App {

	private static Logger log = Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		// Initialize H2 database with db content
		log.info("Initializing and creating db content...");
		DBDAOFactory h2DaoFactory = DBDAOFactory.getDAOFactory(DBDAOFactory.H2DB);
		h2DaoFactory.createDBContent();
		initService();
	}

	private static void initService() throws Exception {
		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/revolutbank/");
		server.setHandler(context);
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames",
				UserServiceImpl.class.getCanonicalName() + "," + AccountServiceImpl.class.getCanonicalName() + "," + FundTransferServiceImpl.class.getCanonicalName());
		try {
			server.start();
			server.join();
		} finally {
			server.destroy();
		}
	}

}
