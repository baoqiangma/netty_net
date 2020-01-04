package com.bqm.core.network.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NHTTPServerObject {

	ByteBuf buf;
	HttpHeaders headers;
	HttpMethod method;
	String uri;

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
