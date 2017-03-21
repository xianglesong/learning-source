/*
 * JBoss, Home of Professional Open Source
 * 
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * software; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.netty.channel;

import java.net.SocketAddress;

/**
 * A {@link ChannelEvent} which represents the transmission or reception of a message. It can mean
 * the notification of a received message or the request for writing a message, depending on whether
 * it is an upstream event or a downstream event respectively. Please refer to the
 * {@link ChannelEvent} documentation to find out what an upstream event and a downstream event are
 * and what fundamental differences they have.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 *
 * @version $Rev: 1583 $, $Date: 2009-07-18 16:43:13 +0900 (Sat, 18 Jul 2009) $
 */
public interface MessageEvent extends ChannelEvent {

  /**
   * Returns the message.
   */
  Object getMessage();

  /**
   * Returns the remote address of the message.
   */
  SocketAddress getRemoteAddress();
}
