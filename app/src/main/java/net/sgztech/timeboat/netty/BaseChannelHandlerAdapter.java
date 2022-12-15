package net.sgztech.timeboat.netty;

import android.util.Log;

import com.imlaidian.utilslibrary.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * Created by zbo on 16/7/14.
 */
public abstract class BaseChannelHandlerAdapter extends ChannelInboundHandlerAdapter {
    private final String TAG = BaseChannelHandlerAdapter.class.getSimpleName();



    public BaseChannelHandlerAdapter() {
        super();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String cmd = null;
            if (msg instanceof String) {
                cmd = (String) msg;
            }


            if (StringUtils.isNotEmpty(cmd)) {
                List<CommandModel> cmdList = CommandUtil.parseCommand(cmd);
                if (cmdList.size() > 0) {
                    for (CommandModel commandModel : cmdList) {
                        action(ctx, commandModel);
                    }
                }
            }else{
                ctx.fireChannelRead(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "read exception = " + e.toString());
        }


    }

    public abstract void action(ChannelHandlerContext ctx,
                                 Object dataModel);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e(TAG,"CHANNEL_EXCEPTION " + cause);
        ChannelFuture future = ctx.close();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Log.e(TAG,  "exceptionCaught CHANNEL_CLOSED");
            }
        });
    }

    protected Session setupSession(ChannelHandlerContext ctx, AttributeKey<Session> key) {
        try {
            Session session = new Session(ctx);
            session.setLastReadTimeToNow();
            if (null != ctx && null != key) {
                if (null != ctx.channel()) {
                    if (null != ctx.channel().attr(key)) {
                        ctx.channel().attr(key).set(session);
                    } else {
                        Log.d(TAG, "ctx channel attr is null");
                    }
                } else {
                    Log.d(TAG, "ctx channel is null");
                }
            } else {
                Log.d(TAG, "ctx and key are null");
            }

            return session;
        } catch (Exception e) {
            e.printStackTrace();

            Log.d(TAG, "setup exception = " + e.toString());
        }

        return null;
    }

    protected void sendMsg( Object dataModel) {
        Log.d(TAG, "send msg type =");
        if (null != dataModel) {
//            if (common.getMessageType() == messageType) {
//                NettyReceiveMessageManager.getInstance().sendCommandByReceiveFromServer(common, dataModel);
//            }
        }
    }
}
