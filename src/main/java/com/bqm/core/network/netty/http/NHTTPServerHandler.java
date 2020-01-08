package com.bqm.core.network.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NHTTPServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private NHTTPEvent event;
	private NHTTPServerObject obj = new NHTTPServerObject();

	public NHTTPServerHandler(NHTTPEvent event) {
		this.event = event;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			log.info("接收到请求： {}",req.uri());
			obj.setHeaders(req.headers());
			obj.setUri(req.uri());
			obj.setMethod(req.method());
			obj.ctx = ctx;
		} else if (msg instanceof LastHttpContent) {
			LastHttpContent req = (LastHttpContent) msg;
			obj.buf.writeBytes(req.content());
			event.offer(obj);
		} else if (msg instanceof HttpContent) {
			HttpContent req = (HttpContent) msg;
			obj.buf.writeBytes(req.content());
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
