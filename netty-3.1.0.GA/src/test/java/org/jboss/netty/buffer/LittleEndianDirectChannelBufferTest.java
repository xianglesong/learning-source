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
package org.jboss.netty.buffer;

import static org.junit.Assert.*;

import java.nio.ByteOrder;

/**
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 *
 * @version $Rev: 1311 $, $Date: 2009-05-30 15:50:09 +0900 (Sat, 30 May 2009) $
 */
public class LittleEndianDirectChannelBufferTest extends AbstractChannelBufferTest {

    private ChannelBuffer buffer;

    @Override
    protected ChannelBuffer newBuffer(int length) {
        buffer = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, length);
        assertSame(ByteOrder.LITTLE_ENDIAN, buffer.order());
        assertEquals(0, buffer.writerIndex());
        return buffer;
    }

    @Override
    protected ChannelBuffer[] components() {
        return new ChannelBuffer[] { buffer };
    }
}
