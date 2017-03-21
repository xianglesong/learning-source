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

import java.net.SocketAddress;

/**
 * An endpoint in the local transport.  Each endpoint is identified by a unique
 * case-insensitive string, except for the pre-defined value called
 * {@code "ephemeral"}.
 *
 * <h3>Ephemeral Address</h3>
 *
 * An ephemeral address is an anonymous address which is assigned temporarily
 * and is released as soon as the connection is closed.  All ephemeral addresses
 * have the same ID, {@code "ephemeral"}, but they are not equals to each other.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev: 1482 $, $Date: 2009-06-20 02:48:17 +0900 (Sat, 20 Jun 2009) $
 */
public final class LocalAddress extends SocketAddress implements Comparable<LocalAddress> {

    private static final long serialVersionUID = -3601961747680808645L;

    public static final String EPHEMERAL = "ephemeral";

    private final String id;
    private final boolean ephemeral;

    /**
     * Creates a new instance with the specified ID.
     */
    public LocalAddress(int id) {
        this(String.valueOf(id));
    }

    /**
     * Creates a new instance with the specified ID.
     */
    public LocalAddress(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        id = id.trim().toLowerCase();
        if (id.length() == 0) {
            throw new IllegalArgumentException("empty id");
        }
        this.id = id;
        ephemeral = id.equals("ephemeral");
    }

    /**
     * Returns the ID of this address.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns {@code true} if and only if this address is ephemeral.
     */
    public boolean isEphemeral() {
        return ephemeral;
    }

    @Override
    public int hashCode() {
        if (ephemeral) {
            return System.identityHashCode(this);
        } else {
            return id.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocalAddress)) {
            return false;
        }

        if (ephemeral) {
            return this == o;
        } else {
            return getId().equals(((LocalAddress) o).getId());
        }
    }

    public int compareTo(LocalAddress o) {
        // FIXME: Ephemeral port
        return getId().compareTo(o.getId());
    }

    @Override
    public String toString() {
        return "local:" + getId();
    }
}
