package com.bqm.core.network.netty.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.HashMap;

import com.bqm.core.network.netty.http.NHTTPServer.Handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NHTTPServerHandler extends SimpleChannelInboundHandler<Object> {

	public HashMap<String, Handler> getHandlers = new HashMap<>();
	public HashMap<String, Handler> postHandlers = new HashMap<>();

	private NHTTPServerObject obj;

	public NHTTPServerHandler(HashMap<String, Handler> getHandlers, HashMap<String, Handler> postHandlers) {
		this.getHandlers = getHandlers;
		this.postHandlers = postHandlers;
		this.obj = new NHTTPServerObject();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {

			HttpRequest req = (HttpRequest) msg;
			obj.setHeaders(req.headers());
			obj.setUri(req.uri());
			obj.setMethod(req.method());

		} else if (msg instanceof LastHttpContent) {
			LastHttpContent req = (LastHttpContent) msg;

			obj.getBuf().writeBytes(req.content());
			Object result = null;
			if (obj.getMethod() == HttpMethod.GET) {
				result = getHandlers.getOrDefault(obj.getUri(), (data) -> {
					return null;
				}).handler(obj);
			} else if (obj.getMethod() == HttpMethod.POST) {
				result = postHandlers.getOrDefault(obj.getUri(), (data) -> {
					return null;
				}).handler(obj);
			}
			if (result != null) {

				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.wrappedBuffer(result.toString().getBytes()));
				response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH, response.content().readableBytes());
				ChannelFuture f = ctx.writeAndFlush(response);
				log.info("handler result ={}", result);
				f.addListener(ChannelFutureListener.CLOSE);
			} else {
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
				response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH, response.content().readableBytes());
				ChannelFuture f = ctx.writeAndFlush(response);
				log.info("handler is not exiest ,handler is {}, content is {}", obj.uri, obj.getContent());

				f.addListener(ChannelFutureListener.CLOSE);
			}

		} else if (msg instanceof HttpContent) {
			HttpContent req = (HttpContent) msg;
			obj.getBuf().writeBytes(req.content());

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
