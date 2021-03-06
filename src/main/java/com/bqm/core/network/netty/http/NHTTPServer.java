package com.bqm.core.network.netty.http;

import java.util.HashMap;

import com.bqm.core.network.netty.ServerConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NHTTPServer {

	private boolean cors;
	private int acceptSize;
	private int workSize;
	private int handleSize;

	private ServerBootstrap server = new ServerBootstrap();
	private HashMap<String, Handler> getHandlers = new HashMap<>();
	private HashMap<String, Handler> postHandlers = new HashMap<>();

	public NHTTPServer() {
		this(false);

	}

	public NHTTPServer(boolean cors) {
		this(cors, 1, 4, 8);
	}

	public NHTTPServer(boolean cors, int acceptSize, int workSize, int handleSize) {
		super();
		this.cors = cors;
		this.acceptSize = acceptSize;
		this.workSize = workSize;
		this.handleSize = handleSize;
	}

	public void registGet(String path, Handler handler) {
		getHandlers.put(path, handler);
	}

	public void registPost(String path, Handler handler) {
		postHandlers.put(path, handler);
	}

	public ChannelFuture bind(ServerConfig sc) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(acceptSize);
		EventLoopGroup workerGroup = new NioEventLoopGroup(workSize);
		ChannelFuture future = null;
		try {
			server.option(ChannelOption.SO_BACKLOG, 1024);
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO));

			NHTTPEvent event = new NHTTPEvent(handleSize, getHandlers, postHandlers);

			if (cors) {
				server.childHandler(new NHTTPServerInitCorsHandler(event));
			} else {
				server.childHandler(new NHTTPServerInitHandler(event));
			}

			future = server.bind(sc.getPort()).sync();
			future.addListener(listener -> {
				if (listener.isSuccess()) {

					log.info("server start :[{}] is [{}] , listener port is [{}]", sc.getName(), listener.isSuccess(),
							sc.getPort());
					new Thread(event).start();
				}
			});
			return future;

		} catch (InterruptedException e) {
			return future;

		}
	}

	public void shutdown() {
		server.config().childGroup().shutdownGracefully();
		server.config().group().shutdownGracefully();
	}

	public static interface Handler {
		Object handler(NHTTPServerObject data);
	}
}
