package ru.allformine.afmcp.test;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import ru.allformine.afmcp.AFMCorePlugin;

public class PacketHandler extends ChannelDuplexHandler {
    public PacketHandler(ChannelPipeline pipeline) {
        pipeline.addBefore("packet_handler", "PacketHandler", this);
        AFMCorePlugin.logger.info("PacketHandler attached!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AFMCorePlugin.logger.info("[IN] " + msg.getClass().getName());
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        AFMCorePlugin.logger.info("[OUT] " + msg.getClass().getName());
        super.write(ctx, msg, promise);
    }
}
