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
package org.jboss.netty.handler.timeout;

/**
 * A {@link TimeoutException} raised by {@link ReadTimeoutHandler} when no data
 * was read within a certain period of time.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1462 $, $Date: 2009-06-19 20:17:38 +0900 (Fri, 19 Jun 2009) $
 */
public class ReadTimeoutException extends TimeoutException {

    private static final long serialVersionUID = -4596059237992273913L;

    /**
     * Creates a new instance.
     */
    public ReadTimeoutException() {
        super();
    }

    /**
     * Creates a new instance.
     */
    public ReadTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public ReadTimeoutException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public ReadTimeoutException(Throwable cause) {
        super(cause);
    }
}
