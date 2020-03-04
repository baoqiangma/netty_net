package com.bqm.core.network;

import com.bqm.core.network.netty.ServerConfig;
import com.bqm.core.network.netty.http.NHTTPServer;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpTest {

	public static void main(String[] args) {
		NHTTPServer server = new NHTTPServer(true);

		server.registGet("/test", handler -> {

			log.info(handler.toString());
			return "test success !";

		});
		server.registPost("/test", handler -> {

			log.info(handler.getContent().toString());
			return "test success !";

		});
		server.registPost("/api/auth/login", handler -> {

			log.info(handler.getContent().toString());
			return "{\"code\":2,\"message\":\"test.... \"}";

		});

		server.bind(new ServerConfig("http_test", 9090, "*"));
	}

}
