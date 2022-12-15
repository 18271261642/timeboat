package net.sgztech.timeboat.netty;

import com.imlaidian.utilslibrary.utils.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyChannelHandlerAdapter extends BaseChannelHandlerAdapter {

    private String TAG  =NettyChannelHandlerAdapter.class.getSimpleName();
    private Session session = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtil.i(TAG, "CHANNEL_ACTIVITY");

        if (session == null) {
            session = new Session(ctx);
            session.setLastReadTimeToNow();
            ctx.channel().attr(NettyConstant.Key.bleCmd).set(session);
        } else {
            session.setLastReadTimeToNow();
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LogUtil.i(TAG, "CHANNEL_REGISTERED");

        if (session == null) {
            session = new Session(ctx);
            session.setLastReadTimeToNow();
            ctx.channel().attr(NettyConstant.Key.bleCmd).set(session);
        } else {
            session.setLastReadTimeToNow();
        }

        super.channelRegistered(ctx);
    }

    @Override
    public void action(ChannelHandlerContext ctx,
                       Object dataModel) {
        if (session == null) {
            session = new Session(ctx);
            session.setLastReadTimeToNow();

            ctx.channel().attr(NettyConstant.Key.bleCmd).set(session);
            receiveMessageToMachine( dataModel);
        } else {
            session.setLastReadTimeToNow();
            receiveMessageToMachine(dataModel);
        }
    }


    public void receiveMessageToMachine( Object dataModel) {

        LogUtil.d(TAG, " receive serve cmd  ,send to machine :start:  "
                + dataModel);

       NettyChannelMessageManager.getInstance().sendCommandByReceiveFromServer(dataModel);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent)evt).state();
            if (state == IdleState.ALL_IDLE) {
                // 在规定时间内没有写或者读数据 则写入一个数据
                ctx.writeAndFlush("heart");
            }
        }
    }
}