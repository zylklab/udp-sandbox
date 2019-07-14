package net.zylklab.sandbox.udp.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


public class IncommingPacketHandler extends  SimpleChannelInboundHandler<DatagramPacket> {

	private static int counter = 0;
    IncommingPacketHandler(){

    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		//final InetAddress srcAddr = msg.sender().getAddress();
        final ByteBuf buf = msg.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] rcvPktBuf = new byte[rcvPktLength];
        buf.readBytes(rcvPktBuf);
        byte[] bytes = new byte[rcvPktLength];
        buf.getBytes(0, bytes);
        System.out.println(String.format("%s - Inside incomming packet handler size of the message %s (bytes)", counter++, bytes.length));
	}
}
