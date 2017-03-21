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
package org.jboss.netty.channel.local;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;

/**
 * The default {@link LocalClientChannelFactory} implementation.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1482 $, $Date: 2009-06-20 02:48:17 +0900 (Sat, 20 Jun 2009) $
 */
public class DefaultLocalClientChannelFactory implements LocalClientChannelFactory {

    private final ChannelSink sink;

    /**
     * Creates a new instance.
     */
    public DefaultLocalClientChannelFactory() {
        sink = new LocalClientChannelSink();
    }

    public LocalChannel newChannel(ChannelPipeline pipeline) {
        return new DefaultLocalChannel(null, this, pipeline, sink, null);
    }

    /**
     * Does nothing because this implementation does not require any external
     * resources.
     */
    public void releaseExternalResources() {
        // No external resources.
    }
}
