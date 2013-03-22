package org.autoswitch;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.autoswitch.complier.SimpleReturnObjectComplier;
import org.autoswitch.config.ClassMethodStatusManager;

public class SwitchControlHttpServer {

	private int listenPort = 8080;

	public void init() throws Exception {
		Thread t = new RequestListenerThread(listenPort);
		t.setDaemon(false);
		t.start();
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
}

class HttpFileHandler implements HttpRequestHandler {

	public HttpFileHandler() {
		super();
	}

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}
		String classmethod = (String) request.getParams().getParameter(
				"classmethod");
		String methodstatus = (String) request.getParams().getParameter(
				"status");
		String jsonResult = (String) request.getParams().getParameter(
				"jsonResult");

		if (methodstatus != null && "open".equalsIgnoreCase(methodstatus)) {
			ClassMethodStatusManager.getInstance().open(classmethod);
			boolean ret = SimpleReturnObjectComplier.addJsonRet(classmethod,
					jsonResult);
		} else if (methodstatus != null
				&& "close".equalsIgnoreCase(methodstatus)) {
			ClassMethodStatusManager.getInstance().close(classmethod);
			SimpleReturnObjectComplier.remove(classmethod);
		}

	}

}

class RequestListenerThread extends Thread {

	private final ServerSocket serversocket;
	private final HttpParams params;
	private final HttpService httpService;

	public RequestListenerThread(int port) throws IOException {
		this.serversocket = new ServerSocket(port);
		this.params = new SyncBasicHttpParams();
		this.params
				.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
				.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
						8 * 1024)
				.setBooleanParameter(
						CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
				.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
				.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
						"HttpComponents/1.1");

		// Set up the HTTP protocol processor
		HttpProcessor httpproc = new ImmutableHttpProcessor(
				new HttpResponseInterceptor[] { new ResponseDate(),
						new ResponseServer(), new ResponseContent(),
						new ResponseConnControl() });

		// Set up request handlers
		HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
		reqistry.register("*", new HttpFileHandler());

		// Set up the HTTP service
		this.httpService = new HttpService(httpproc,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory(), reqistry, this.params);
	}

	@Override
	public void run() {
		System.out.println("Listening on port "
				+ this.serversocket.getLocalPort());
		while (!Thread.interrupted()) {
			try {
				// Set up HTTP connection
				Socket socket = this.serversocket.accept();
				DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
				System.out.println("Incoming connection from "
						+ socket.getInetAddress());
				conn.bind(socket, this.params);

				// Start worker thread
				Thread t = new WorkerThread(this.httpService, conn);
				t.setDaemon(true);
				t.start();
			} catch (InterruptedIOException ex) {
				break;
			} catch (IOException e) {
				System.err.println("I/O error initialising connection thread: "
						+ e.getMessage());
				break;
			}
		}
	}
}

class WorkerThread extends Thread {

	private final HttpService httpservice;
	private final HttpServerConnection conn;

	public WorkerThread(final HttpService httpservice,
			final HttpServerConnection conn) {
		super();
		this.httpservice = httpservice;
		this.conn = conn;
	}

	@Override
	public void run() {
		System.out.println("New connection thread");
		BasicHttpContext context = new BasicHttpContext(null);
		try {
			while (!Thread.interrupted() && this.conn.isOpen()) {
				this.httpservice.handleRequest(this.conn, context);
			}
		} catch (ConnectionClosedException ex) {
			System.err.println("Client closed connection");
		} catch (IOException ex) {
			System.err.println("I/O error: " + ex.getMessage());
		} catch (HttpException ex) {
			System.err.println("Unrecoverable HTTP protocol violation: "
					+ ex.getMessage());
		} finally {
			try {
				this.conn.shutdown();
			} catch (IOException ignore) {
			}
		}
	}

}
