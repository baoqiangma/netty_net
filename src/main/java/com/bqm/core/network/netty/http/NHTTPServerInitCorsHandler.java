package com.bqm.core.network.netty.http;

import java.util.HashMap;

import com.bqm.core.network.netty.http.NHTTPServer.Handler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NHTTPServerInitCorsHandler extends NHTTPServerInitHandler {

	public NHTTPServerInitCorsHandler(NHTTPEvent event) {
		super(event);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		log.info("新连接：{}", ch.remoteAddress());
		CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowedRequestHeaders("content-type", "accept")
				.allowNullOrigin().allowCredentials().build();
		ChannelPipeline p = ch.pipeline();

		p.addLast(new HttpServerCodec());
		p.addLast(new HttpServerExpectContinueHandler());
//		p.addLast(new HttpResponseEncoder());
//		p.addLast(new HttpRequestDecoder());
//		p.addLast(new HttpObjectAggregator(65536));
		p.addLast(new ChunkedWriteHandler());
		p.addLast(new CorsHandler(corsConfig));
		p.addLast(new NHTTPServerHandler(event));
	}

}
