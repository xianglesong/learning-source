/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @author tags. See the COPYRIGHT.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.netty.channel.socket.nio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Daniel Bevenius (dbevenius@jboss.com)
 * @version $Rev: 1347 $, $Date: 2009-06-11 15:10:46 +0900 (Thu, 11 Jun 2009) $
 */
public class NioDatagramChannelTest {
    private static Channel sc;

    private static InetSocketAddress inetSocketAddress;

    @BeforeClass
    public static void setupChannel() {
        final NioDatagramChannelFactory channelFactory = new NioDatagramChannelFactory(
                Executors.newCachedThreadPool());
        final ConnectionlessBootstrap sb = new ConnectionlessBootstrap(channelFactory);
        inetSocketAddress = new InetSocketAddress("localhost", 9999);
        sc = sb.bind(inetSocketAddress);
        final SimpleHandler handler = new SimpleHandler();
        sc.getPipeline().addFirst("handler", handler);
    }

    @Test
    public void checkBoundPort() throws Throwable {
        final InetSocketAddress socketAddress = (InetSocketAddress) sc
                .getLocalAddress();
        assertEquals(9999, socketAddress.getPort());
    }

    @Test
    public void sendReciveOne() throws Throwable {
        final String expectedPayload = "some payload";
        sendRecive(expectedPayload);
    }

    @Test
    public void sendReciveMultiple() throws Throwable {
        final String expectedPayload = "some payload";
        for (int i = 0; i < 1000; i ++) {
            sendRecive(expectedPayload);
        }
    }

    public void clientBootstrap() {
        final NioDatagramChannelFactory channelFactory = new NioDatagramChannelFactory(
                Executors.newCachedThreadPool());
        final ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(channelFactory);
        bootstrap.getPipeline().addLast("test", new SimpleHandler());
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        InetSocketAddress clientAddress = new InetSocketAddress("localhost",
                8888);
        bootstrap.setOption("localAddress", clientAddress);

        ChannelFuture ccf = bootstrap.connect(inetSocketAddress);
        ccf.awaitUninterruptibly();

        Channel cc = ccf.getChannel();
        final String payload = "client payload";
        ChannelFuture write = cc.write(ChannelBuffers.wrappedBuffer(payload
                .getBytes(), 0, payload.length()));
        write.awaitUninterruptibly();
    }

    @AfterClass
    public static void closeChannel() {
        if (sc != null) {
            final ChannelFuture future = sc.close();
            if (future != null) {
                future.awaitUninterruptibly();
            }
        }
    }

    private void sendRecive(final String expectedPayload) throws IOException {
        final UdpClient udpClient = new UdpClient(inetSocketAddress
                .getAddress(), inetSocketAddress.getPort());
        final DatagramPacket dp = udpClient.send(expectedPayload.getBytes());

        dp.setData(new byte[expectedPayload.length()]);
        assertFalse("The payload should have been cleared", expectedPayload
                .equals(new String(dp.getData())));

        udpClient.receive(dp);

        assertEquals(expectedPayload, new String(dp.getData()));
        udpClient.close();
    }

}
