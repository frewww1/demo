package org.example;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    public static final NettyServerHandler INSTANCE = new NettyServerHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("接收到的信息：" + msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //空闲状态的事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            System.out.println(((IdleStateEvent) event).state() + ">>>" + ctx.channel().id());

            //已经10秒钟没有读时间了
            if (event.state().equals(IdleState.READER_IDLE)){
                // 心跳包丢失，10秒没有收到客户端心跳 (断开连接)
                ctx.channel().close().sync();
                System.out.println("已与 "+ctx.channel().remoteAddress()+" 断开连接");
            }
        }
    }
}