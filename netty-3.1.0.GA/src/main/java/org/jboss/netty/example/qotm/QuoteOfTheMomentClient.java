/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.netty.example.qotm;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.oio.OioDatagramChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * A UDP broadcast client that asks for a quote of the moment (QOTM) to
 * {@link QuoteOfTheMomentServer}.
 *
 * Inspired by <a href="http://java.sun.com/docs/books/tutorial/networking/datagrams/clientServer.html">the official Java tutorial</a>.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1251 $, $Date: 2009-04-28 14:28:36 +0900 (Tue, 28 Apr 2009) $
 */
public class QuoteOfTheMomentClient {

    public static void main(String[] args) throws Exception {
        DatagramChannelFactory f =
            new OioDatagramChannelFactory(Executors.newCachedThreadPool());

        ConnectionlessBootstrap b = new ConnectionlessBootstrap(f);
        ChannelPipeline p = b.getPipeline();
        p.addLast("encoder", new StringEncoder("UTF-8"));
        p.addLast("decoder", new StringDecoder("UTF-8"));
        p.addLast("handler", new QuoteOfTheMomentClientHandler());

        b.setOption("broadcast", "true");
        DatagramChannel c = (DatagramChannel) b.bind(new InetSocketAddress(0));

        // Broadcast the QOTM request to port 8080.
        c.write("QOTM?", new InetSocketAddress("255.255.255.255", 8080));

        // QuoteOfTheMomentClientHandler will close the DatagramChannel when a
        // response is received.  If the channel is not closed within 5 seconds,
        // print an error message and quit.
        if (!c.getCloseFuture().awaitUninterruptibly(5000)) {
            System.err.println("QOTM request timed out.");
            c.close().awaitUninterruptibly();
        }

        f.releaseExternalResources();
    }
}
