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
package org.jboss.netty.example.http.tunnel;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.example.echo.EchoHandler;

/**
 * Deploy this in JBossAS 5 or other IoC container by adding the following bean.
 *
 * <pre>
 * &lt;bean name="org.jboss.netty.example.http.tunnel.LocalEchoServerRegistration"
 *       class="org.jboss.netty.example.http.tunnel.LocalEchoServerRegistration" /&gt;
 * </pre>
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @version $Rev: 1557 $, $Date: 2009-07-14 19:37:36 +0900 (Tue, 14 Jul 2009) $
 */
public class LocalEchoServerRegistration {

    private final ChannelFactory factory = new DefaultLocalServerChannelFactory();
    private volatile Channel serverChannel;

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap(factory);
        EchoHandler handler = new EchoHandler();
        serverBootstrap.getPipeline().addLast("handler", handler);

        // Note that "myLocalServer" is the endpoint which was specified in web.xml.
        serverChannel = serverBootstrap.bind(new LocalAddress("myLocalServer"));
    }

    public void stop() {
        serverChannel.close();
    }
}
