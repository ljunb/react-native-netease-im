package com.netease.im.session;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by dowin on 2017/5/2.
 */

public class SessionUtil {

    public static SessionTypeEnum getSessionType(String sessionType) {
        SessionTypeEnum sessionTypeE = SessionTypeEnum.None;
        try {
            sessionTypeE = SessionTypeEnum.typeOfValue(Integer.parseInt(sessionType));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sessionTypeE;
    }

    private static void appendPushConfig(IMMessage message) {
//        CustomPushContentProvider customConfig = NimUIKit.getCustomPushContentProvider();
//        if (customConfig != null) {
//            String content = customConfig.getPushContent(message);
//            Map<String, Object> payload = customConfig.getPushPayload(message);
//            message.setPushContent(content);
//            message.setPushPayload(payload);
//        }
    }

    /**
     * 设置最近联系人的消息为已读
     *
     * @param enable
     */
    private void enableMsgNotification(boolean enable) {
        if (enable) {
            /**
             * 设置最近联系人的消息为已读
             *
             * @param account,    聊天对象帐号，或者以下两个值：
             *                    {@link #MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
             *                    {@link #MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
             */
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    public static void sendMessage(IMMessage message) {

        appendPushConfig(message);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }
}
