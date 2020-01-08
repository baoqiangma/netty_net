package com.bqm.core.network.netty.http;

import java.util.HashMap;

import com.bqm.core.network.netty.http.NHTTPServer.Handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

public class NHTTPServerInitHandler extends ChannelInitializer<SocketChannel> {

	protected NHTTPEvent event;

	public NHTTPServerInitHandler(NHTTPEvent event) {
		this.event = event;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpServerExpectContinueHandler());
		p.addLast(new NHTTPServerHandler(event));
	}

}
