package org.magnum.soda.examples;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class HttpServer {
	
	private static Reports manager = new Reports();
	public static Queue<String> messageQueue = new LinkedList<String>();
	
	public static void main(String[] args) throws Exception {
		
		ContextHandler context = new ContextHandler("/get");
		context.setContextPath("/");
		context.setHandler(new HandlerGet());

		ContextHandler contextPost = new ContextHandler("/addlistener");
		contextPost.setHandler(new HandlerPost());

		ContextHandler contextAdd = new ContextHandler("/addreport");
		contextAdd.setHandler(new HandlerAdd());
		
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { context, contextPost, contextAdd });

		Server server = new Server(8080);
		server.setHandler(contexts);

		server.start();
		server.join();
	}

	public static class HandlerGet extends AbstractHandler {

		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			
			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer);
			String listenerId = writer.toString();
			String res = null;
			
			for (Iterator<String> iterator = messageQueue.iterator(); iterator.hasNext(); ) {
			    String s = iterator.next();
				String[] array = new String[2];
				array = s.split(",");
				System.err.println("queue:" + listenerId + " array:" + array[0] +" "+ array[1]);
				
				if(array[0].equals(listenerId)){
					res = array[1];
					iterator.remove();
				}
			}
			response.setContentType("text/plain;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println(res);
			
		}
	}

	public static class HandlerPost extends AbstractHandler {

		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer);
			String id = writer.toString();
			manager.addListener(new ReportsListener(id));

			System.err.println("listener added in server: " + id);
			
			response.setContentType("text/plain;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println("listener added: " + writer.toString());
		}
	}
	
	public static class HandlerAdd extends AbstractHandler {

		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
					
			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer);
			String content = writer.toString();
			
			manager.addReport(new Report(content));

			System.err.println("report added in server: " + content + " messageQueue size:" +
					messageQueue.size());
					
			response.setContentType("text/plain;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println("report added: " + content);
		}
	}

}
//100-4=96