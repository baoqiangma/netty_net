package com.bqm.core.network.netty.http;

import java.util.HashMap;

import com.bqm.core.network.netty.http.NHTTPServer.Handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

public class NHTTPServerInitHandler extends ChannelInitializer<SocketChannel> {

	public HashMap<String, Handler> getHandlers;
	public HashMap<String, Handler> postHandlers;

	public NHTTPServerInitHandler(HashMap<String, Handler> getHandlers, HashMap<String, Handler> postHandlers) {
		this.getHandlers = getHandlers;
		this.postHandlers = postHandlers;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpServerExpectContinueHandler());
		p.addLast(new NHTTPServerHandler(getHandlers, postHandlers));
	}

}
