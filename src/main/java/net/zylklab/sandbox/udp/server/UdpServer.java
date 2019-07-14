package net.zylklab.sandbox.udp.server;

import java.net.InetAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;

public class UdpServer {
	private final int port;
	private final Bootstrap b;
	private final NioEventLoopGroup group;
	private final InetAddress address;
	private ChannelFuture future = null;

	public UdpServer(InetAddress address, int port) {
		this.port = port;
		int threads = 10;
		final Executor executor = Executors.newCachedThreadPool(); //hay muchos más tipos de executors
		EventExecutorChooserFactory chooserFactory = DefaultEventExecutorChooserFactory.INSTANCE;
		SelectorProvider selectorProvider = SelectorProvider.provider();
		SelectStrategyFactory selectStrategyFactory = DefaultSelectStrategyFactory.INSTANCE;
		RejectedExecutionHandler rejectExecutionHandler = RejectedExecutionHandlers.reject();
		
		this.group = new NioEventLoopGroup(threads, executor, chooserFactory, selectorProvider, selectStrategyFactory, rejectExecutionHandler);
		this.b = new Bootstrap();
		this.address = address;
	}
	public void start() throws Exception {
		try {
			b.group(group)
						.channel(NioDatagramChannel.class)
						.option(ChannelOption.SO_BROADCAST, true)
						.handler(new ChannelInitializer<NioDatagramChannel>() {
							@Override
							public void initChannel(final NioDatagramChannel ch) throws Exception {
								ChannelPipeline p = ch.pipeline();
								p.addLast(new IncommingPacketHandler());
							}
						});
			future = b.bind(this.address, this.port).sync().channel().closeFuture().await();
		} finally {
			System.out.print("In Server Finally");
		}
	}
	public void stop() throws InterruptedException {
		this.future.channel().close().sync();
	}

	public static void main(String[] args) throws Exception {
		UdpServer udpServer = new UdpServer(InetAddress.getLocalHost(), 9956);
		udpServer.start();
	}
}
