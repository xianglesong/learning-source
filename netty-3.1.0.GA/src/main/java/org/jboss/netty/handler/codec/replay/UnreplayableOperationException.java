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
package org.jboss.netty.handler.codec.replay;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * An {@link Exception} which is thrown when a user calls an unsupported
 * operation on a {@link ChannelBuffer} in a {@link ReplayingDecoder}
 * implementation.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1455 $, $Date: 2009-06-19 18:15:48 +0900 (Fri, 19 Jun 2009) $
 */
public class UnreplayableOperationException extends
        UnsupportedOperationException {

    private static final long serialVersionUID = 8577363912862364021L;

    /**
     * Creates a new instance.
     */
    public UnreplayableOperationException() {
        super();
    }

    /**
     * Creates a new instance.
     */
    public UnreplayableOperationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public UnreplayableOperationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance.
     */
    public UnreplayableOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
