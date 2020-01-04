package com.bqm.core.network;

import com.bqm.core.network.netty.ServerConfig;
import com.bqm.core.network.netty.http.NHTTPServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpTest {

	public static void main(String[] args) {
		NHTTPServer server = new NHTTPServer();

		server.registGet("/test", handler -> {

			log.info(handler.toString());
			return "test success !";

		});
		server.registPost("/test", handler -> {
			
			log.info(handler.getContent().toString());
			return "test success !";
			
		});

		server.bind(new ServerConfig("http_test", 8090, "*"));
	}

}
