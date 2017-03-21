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
package org.jboss.netty.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.*;
import static org.jboss.netty.handler.codec.http.HttpCodecUtil.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Encodes an {@link HttpMessage} or an {@link HttpChunk} into
 * a {@link ChannelBuffer}.
 *
 * <h3>Extensibility</h3>
 *
 * Please note that this encoder is designed to be extended to implement
 * a protocol derived from HTTP, such as
 * <a href="http://en.wikipedia.org/wiki/Real_Time_Streaming_Protocol">RTSP</a> and
 * <a href="http://en.wikipedia.org/wiki/Internet_Content_Adaptation_Protocol">ICAP</a>.
 * To implement the encoder of such a derived protocol, extend this class and
 * implement all abstract methods properly.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1586 $, $Date: 2009-07-20 12:37:35 +0900 (Mon, 20 Jul 2009) $
 *
 * @apiviz.landmark
 */
@ChannelPipelineCoverage("all")
public abstract class HttpMessageEncoder extends OneToOneEncoder {

    private static final ChannelBuffer LAST_CHUNK = copiedBuffer("0\r\n\r\n", "ASCII");

    /**
     * Creates a new instance.
     */
    protected HttpMessageEncoder() {
        super();
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof HttpMessage) {
            HttpMessage request = (HttpMessage) msg;
            ChannelBuffer header = ChannelBuffers.dynamicBuffer(
                    channel.getConfig().getBufferFactory());
            encodeInitialLine(header, request);
            encodeHeaders(header, request);
            header.writeBytes(CRLF);

            ChannelBuffer content = request.getContent();
            if (!content.readable()) {
                return header; // no content
            } else {
                return wrappedBuffer(header, content);
            }
        }

        if (msg instanceof HttpChunk) {
            HttpChunk chunk = (HttpChunk) msg;
            if (chunk.isLast()) {
                return LAST_CHUNK.duplicate();
            } else {
                ChannelBuffer content = chunk.getContent();
                int contentLength = content.readableBytes();

                return wrappedBuffer(
                        copiedBuffer(Integer.toHexString(contentLength), "ASCII"),
                        wrappedBuffer(CRLF),
                        content.slice(content.readerIndex(), contentLength),
                        wrappedBuffer(CRLF));
            }
        }

        // Unknown message type.
        return msg;
    }

    private void encodeHeaders(ChannelBuffer buf, HttpMessage message) {
        Set<String> headers = message.getHeaderNames();
        try {
            for (String header : headers) {
                List<String> values = message.getHeaders(header);
                for (String value : values) {

                    buf.writeBytes(header.getBytes("ASCII"));
                    buf.writeByte(COLON);
                    buf.writeByte(SP);
                    buf.writeBytes(value.getBytes("ASCII"));
                    buf.writeBytes(CRLF);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw (Error) new Error().initCause(e);
        }
    }

    protected abstract void encodeInitialLine(ChannelBuffer buf, HttpMessage message) throws Exception;
}
