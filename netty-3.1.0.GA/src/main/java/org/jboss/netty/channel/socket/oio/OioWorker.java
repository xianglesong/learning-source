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

import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

/**
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 *
 * @version $Rev: 1497 $, $Date: 2009-06-25 18:18:14 +0900 (Thu, 25 Jun 2009) $
 *
 */
class OioWorker implements Runnable {

    private static final Pattern SOCKET_CLOSED_MESSAGE = Pattern.compile(
            "^.*(?:Socket.*closed).*$", Pattern.CASE_INSENSITIVE);

    private final OioSocketChannel channel;

    OioWorker(OioSocketChannel channel) {
        this.channel = channel;
    }

    public void run() {
        channel.workerThread = Thread.currentThread();
        final PushbackInputStream in = channel.getInputStream();

        while (channel.isOpen()) {
            synchronized (channel.interestOpsLock) {
                while (!channel.isReadable()) {
                    try {
                        // notify() is not called at all.
                        // close() and setInterestOps() calls Thread.interrupt()
                        channel.interestOpsLock.wait();
                    } catch (InterruptedException e) {
                        if (!channel.isOpen()) {
                            break;
                        }
                    }
                }
            }

            byte[] buf;
            int readBytes;
            try {
                int bytesToRead = in.available();
                if (bytesToRead > 0) {
                    buf = new byte[bytesToRead];
                    readBytes = in.read(buf);
                } else {
                    int b = in.read();
                    if (b < 0) {
                        break;
                    }
                    in.unread(b);
                    continue;
                }
            } catch (Throwable t) {
                if (!channel.socket.isClosed()) {
                    fireExceptionCaught(channel, t);
                }
                break;
            }

            ChannelBuffer buffer;
            if (readBytes == buf.length) {
                buffer = ChannelBuffers.wrappedBuffer(buf);
            } else {
                // A rare case, but it sometimes happen.
                buffer = ChannelBuffers.wrappedBuffer(buf, 0, readBytes);
            }

            fireMessageReceived(channel, buffer);
        }

        // Setting the workerThread to null will prevent any channel
        // operations from interrupting this thread from now on.
        channel.workerThread = null;

        // Clean up.
        close(channel, succeededFuture(channel));
    }

    static void write(
            OioSocketChannel channel, ChannelFuture future,
            Object message) {

        OutputStream out = channel.getOutputStream();
        if (out == null) {
            Exception e = new ClosedChannelException();
            future.setFailure(e);
            fireExceptionCaught(channel, e);
            return;
        }

        try {
            ChannelBuffer a = (ChannelBuffer) message;
            int bytes = a.readableBytes();
            synchronized (out) {
                a.getBytes(a.readerIndex(), out, bytes);
            }
            fireWriteComplete(channel, bytes);
            future.setSuccess();
        } catch (Throwable t) {
            // Convert 'SocketException: Socket closed' to
            // ClosedChannelException.
            if (t instanceof SocketException &&
                    SOCKET_CLOSED_MESSAGE.matcher(
                            String.valueOf(t.getMessage())).matches()) {
                t = new ClosedChannelException();
            }
            future.setFailure(t);
            fireExceptionCaught(channel, t);
        }
    }

    static void setInterestOps(
            OioSocketChannel channel, ChannelFuture future, int interestOps) {

        // Override OP_WRITE flag - a user cannot change this flag.
        interestOps &= ~Channel.OP_WRITE;
        interestOps |= channel.getInterestOps() & Channel.OP_WRITE;

        boolean changed = false;
        try {
            if (channel.getInterestOps() != interestOps) {
                if ((interestOps & Channel.OP_READ) != 0) {
                    channel.setInterestOpsNow(Channel.OP_READ);
                } else {
                    channel.setInterestOpsNow(Channel.OP_NONE);
                }
                changed = true;
            }

            future.setSuccess();
            if (changed) {
                synchronized (channel.interestOpsLock) {
                    channel.setInterestOpsNow(interestOps);

                    // Notify the worker so it stops or continues reading.
                    Thread currentThread = Thread.currentThread();
                    Thread workerThread = channel.workerThread;
                    if (workerThread != null && currentThread != workerThread) {
                        workerThread.interrupt();
                    }
                }

                fireChannelInterestChanged(channel);
            }
        } catch (Throwable t) {
            future.setFailure(t);
            fireExceptionCaught(channel, t);
        }
    }

    static void close(OioSocketChannel channel, ChannelFuture future) {
        boolean connected = channel.isConnected();
        boolean bound = channel.isBound();
        try {
            channel.socket.close();
            if (channel.setClosed()) {
                future.setSuccess();
                if (connected) {
                    // Notify the worker so it stops reading.
                    Thread currentThread = Thread.currentThread();
                    Thread workerThread = channel.workerThread;
                    if (workerThread != null && currentThread != workerThread) {
                        workerThread.interrupt();
                    }
                    fireChannelDisconnected(channel);
                }
                if (bound) {
                    fireChannelUnbound(channel);
                }
                fireChannelClosed(channel);
            } else {
                future.setSuccess();
            }
        } catch (Throwable t) {
            future.setFailure(t);
            fireExceptionCaught(channel, t);
        }
    }
}