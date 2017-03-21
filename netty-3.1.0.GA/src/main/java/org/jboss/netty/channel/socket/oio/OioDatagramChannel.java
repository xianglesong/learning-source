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
package org.jboss.netty.channel.socket.oio;

import static org.jboss.netty.channel.Channels.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;

import org.jboss.netty.channel.AbstractChannel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelConfig;
import org.jboss.netty.channel.socket.DefaultDatagramChannelConfig;

/**
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 *
 * @version $Rev: 1552 $, $Date: 2009-07-14 14:28:07 +0900 (Tue, 14 Jul 2009) $
 *
 */
final class OioDatagramChannel extends AbstractChannel
                                implements DatagramChannel {

    final MulticastSocket socket;
    final Object interestOpsLock = new Object();
    private final DatagramChannelConfig config;
    volatile Thread workerThread;
    private volatile InetSocketAddress localAddress;
    volatile InetSocketAddress remoteAddress;

    OioDatagramChannel(
            ChannelFactory factory,
            ChannelPipeline pipeline,
            ChannelSink sink) {

        super(null, factory, pipeline, sink);

        try {
            socket = new MulticastSocket(null);
        } catch (IOException e) {
            throw new ChannelException("Failed to open a datagram socket.", e);
        }

        try {
            socket.setSoTimeout(10);
        } catch (SocketException e) {
            throw new ChannelException(
                    "Failed to configure the datagram socket timeout.", e);
        }
        config = new DefaultDatagramChannelConfig(socket);

        fireChannelOpen(this);
    }

    public DatagramChannelConfig getConfig() {
        return config;
    }

    public InetSocketAddress getLocalAddress() {
        InetSocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                this.localAddress = localAddress =
                    (InetSocketAddress) socket.getLocalSocketAddress();
            } catch (Throwable t) {
                // Sometimes fails on a closed socket in Windows.
                return null;
            }
        }
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        InetSocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                this.remoteAddress = remoteAddress =
                    (InetSocketAddress) socket.getRemoteSocketAddress();
            } catch (Throwable t) {
                // Sometimes fails on a closed socket in Windows.
                return null;
            }
        }
        return remoteAddress;
    }

    public boolean isBound() {
        return isOpen() && socket.isBound();
    }

    public boolean isConnected() {
        return isOpen() && socket.isConnected();
    }

    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }

    @Override
    protected void setInterestOpsNow(int interestOps) {
        super.setInterestOpsNow(interestOps);
    }

    @Override
    public ChannelFuture write(Object message, SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(getRemoteAddress())) {
            return super.write(message, null);
        } else {
            return super.write(message, remoteAddress);
        }
    }

    public void joinGroup(InetAddress multicastAddress) {
        ensureBound();
        try {
            socket.joinGroup(multicastAddress);
        } catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public void joinGroup(
            InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        ensureBound();
        try {
            socket.joinGroup(multicastAddress, networkInterface);
        } catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    private void ensureBound() {
        if (!isBound()) {
            throw new IllegalStateException(
                    DatagramChannel.class.getName() +
                    " must be bound to join a group.");
        }
    }

    public void leaveGroup(InetAddress multicastAddress) {
        try {
            socket.leaveGroup(multicastAddress);
        } catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    public void leaveGroup(
            InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        try {
            socket.leaveGroup(multicastAddress, networkInterface);
        } catch (IOException e) {
            throw new ChannelException(e);
        }
    }
}
