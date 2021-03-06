package org.jboss.netty.util;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelUpstreamHandler;

/**
 * A dummy handler for a testing purpose.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 *
 * @version $Rev: 254 $, $Date: 2008-09-06 18:34:40 +0900 (Sat, 06 Sep 2008) $
 */
@ChannelPipelineCoverage("all")
public class DummyHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception {
        ctx.sendUpstream(e);
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception {
        ctx.sendDownstream(e);
    }
}
