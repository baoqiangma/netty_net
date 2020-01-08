package com.bqm.core.network.netty.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bqm.core.network.netty.http.NHTTPServer.Handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NHTTPEvent implements Runnable {

	private Queue<NHTTPServerObject> queue = new ConcurrentLinkedQueue<>();

	private Map<String, Handler> getHandlers;
	private Map<String, Handler> postHandlers;
	private ThreadPoolExecutor service;
	private int corePoolSize;

	public NHTTPEvent(int corePoolSize, Map<String, Handler> getHandlers, Map<String, Handler> postHandlers) {
		this.corePoolSize = corePoolSize;
		this.getHandlers = getHandlers;
		this.postHandlers = postHandlers;
		service = new ThreadPoolExecutor(2, corePoolSize, 10, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

	}

	public NHTTPEvent(int corePoolSize, ThreadFactory threadFactory) {
		Executors.newFixedThreadPool(corePoolSize, threadFactory);
	}

	public void run() {
		log.info("接收处理开启，处理最大线程数量为： {}", corePoolSize);
		while (true) {
			NHTTPServerObject obj = queue.poll();
			if (null != obj) {
				service.execute(() -> {
					executeHandler(obj);
				});
			}
		}
	}

	private void executeHandler(NHTTPServerObject obj) {
		log.info("处理操作：{}", obj.uri);
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

			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
					Unpooled.wrappedBuffer(result.toString().getBytes()));
			response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH, response.content().readableBytes());
			ChannelFuture f = obj.ctx.writeAndFlush(response);
			log.info("handler result ={}", result);
			f.addListener(ChannelFutureListener.CLOSE);
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
			response.headers().set(CONTENT_TYPE, TEXT_PLAIN).setInt(CONTENT_LENGTH, response.content().readableBytes());
			ChannelFuture f = obj.ctx.writeAndFlush(response);
			log.info("handler is not exiest ,handler is {}, content is {}", obj.uri, obj.getContent());

			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void offer(NHTTPServerObject o) {
		queue.offer(o);
	}

}
