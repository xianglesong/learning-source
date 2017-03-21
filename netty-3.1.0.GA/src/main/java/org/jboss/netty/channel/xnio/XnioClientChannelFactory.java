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
package org.jboss.netty.channel.xnio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.xnio.Connector;

/**
 * A client-side {@link ChannelFactory} which uses
 * <a href="http://www.jboss.org/xnio/">JBoss XNIO</a> as its I/O provider.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1428 $, $Date: 2009-06-18 18:42:12 +0900 (Thu, 18 Jun 2009) $
 */
@SuppressWarnings("unchecked")
public class XnioClientChannelFactory implements ChannelFactory {

    private final Connector xnioConnector;
    private final XnioClientChannelSink sink;

    public XnioClientChannelFactory(Connector xnioConnector) {
        if (xnioConnector == null) {
            throw new NullPointerException("xnioConnector");
        }
        this.xnioConnector = xnioConnector;
        sink = new XnioClientChannelSink();
    }

    public Channel newChannel(ChannelPipeline pipeline) {
        return new XnioClientChannel(this, pipeline, sink, xnioConnector);
    }

    public void releaseExternalResources() {
        // Nothing to release.
    }
}
