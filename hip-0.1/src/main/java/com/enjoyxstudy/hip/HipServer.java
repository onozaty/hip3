/*
 * Copyright (c) 2008 onozaty (http://www.enjoyxstudy.com)
 *
 *  The software is licensed under the GNU General Public License (GPL)
 *  For details, see http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 */
package com.enjoyxstudy.hip;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * @author onozaty
 */
public class HipServer {

    /** http server */
    private Server httpServer;

    /** config */
    private Config config;

    /** irc bot */
    private HipLogBot ircBot;

    /**
     * @param config
     */
    public HipServer(Config config) {
        this.config = config;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream("config.properties");
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }

        HipServer hipServer = new HipServer(new Config(properties));
        hipServer.startup();
    }

    /**
     * @throws Exception
     */
    public void startup() throws Exception {

        // log directory create.
        config.getOutputDir().mkdir();
        if (!config.getOutputDir().isDirectory()) {
            throw new IOException("Cannot make output directory ("
                    + config.getOutputDir() + ")");
        }

        // ircbot start.
        startIrcBot();

        // http server start.
        startHttpServer();
    }

    /**
     * @throws Exception
     */
    private void startHttpServer() throws Exception {

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(config.getHttpPort());
        httpServer = new Server();

        httpServer.setConnectors(new Connector[] { connector });

        ContextHandler contextHandler = new ContextHandler("/");

        ServletHandler servletHandler = new ServletHandler();

        TalkServlet talkServlet = new TalkServlet(ircBot, config);
        servletHandler.addServletWithMapping(new ServletHolder(talkServlet),
                "/talk/");

        LogViewerServlet logViewerServlet = new LogViewerServlet(config);
        servletHandler.addServletWithMapping(
                new ServletHolder(logViewerServlet), "/");

        contextHandler.addHandler(servletHandler);
        httpServer.addHandler(contextHandler);

        httpServer.start();
        httpServer.join();
    }

    /**
     * @return irc bot
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws IrcException
     * @throws NickAlreadyInUseException
     */
    private PircBot startIrcBot() throws UnsupportedEncodingException,
            IOException, IrcException, NickAlreadyInUseException {

        ircBot = new HipLogBot(config.getNick(), config.getOutputDir(), config
                .getJoinMessage());
        ircBot.setEncoding(config.getEncoding());
        ircBot.connect(config.getServerName(), config.getServerPort(), config
                .getServerPassword());
        ircBot.joinChannel(config.getChannel());

        return ircBot;
    }
}
