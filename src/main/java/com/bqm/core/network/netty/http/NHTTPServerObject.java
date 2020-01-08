package com.bqm.core.network.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

@Setter
public class NHTTPServerObject {

	@Getter
	ByteBuf buf;
	@Getter
	HttpHeaders headers;
	@Getter
	HttpMethod method;
	@Getter
	String uri;
	ChannelHandlerContext ctx;

	public NHTTPServerObject() {
		this.buf = ByteBufAllocator.DEFAULT.buffer();
	}

	public String getContent() {
		ByteBuf buf = this.buf.slice();
		byte[] temp = new byte[buf.readableBytes()];
		buf.readBytes(temp);
		return new String(temp);
	}

}
