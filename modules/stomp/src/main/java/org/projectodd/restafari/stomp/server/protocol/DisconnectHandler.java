package org.projectodd.restafari.stomp.server.protocol;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.projectodd.restafari.stomp.Headers;
import org.projectodd.restafari.stomp.Stomp;
import org.projectodd.restafari.stomp.common.AbstractControlFrameHandler;
import org.projectodd.restafari.stomp.common.StompControlFrame;
import org.projectodd.restafari.stomp.server.ServerContext;
import org.projectodd.restafari.stomp.server.StompConnection;

/**
 * @author Bob McWhirter
 */
public class DisconnectHandler extends AbstractControlFrameHandler {

    public DisconnectHandler(ServerContext serverContext) {
        super(Stomp.Command.DISCONNECT);
        this.serverContext = serverContext;
    }

    @Override
    protected void handleControlFrame(ChannelHandlerContext ctx, StompControlFrame frame) throws Exception {
        StompConnection stompConnection = ctx.attr(ConnectHandler.CONNECTION).get();
        this.serverContext.handleDisconnect(stompConnection);
        String receiptId = frame.getHeader(Headers.RECEIPT);
        ChannelFuture future = ctx.writeAndFlush(StompControlFrame.newReceiptFrame(receiptId));
        future.addListener((f) -> {
            ctx.close();
        });
    }

    private ServerContext serverContext;
}
