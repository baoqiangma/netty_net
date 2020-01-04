package com.bqm.core.network.netty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerConfig {
	
	private final String name;
	private final int port;
	private final String addr;

}
