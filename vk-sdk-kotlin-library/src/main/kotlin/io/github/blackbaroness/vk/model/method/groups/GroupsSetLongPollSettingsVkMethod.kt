package io.github.blackbaroness.vk.model.method.groups

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.response.Ok
import io.ktor.http.*
import kotlinx.serialization.serializer

// https://dev.vk.com/ru/method/groups.setLongPollSettings
class GroupsSetLongPollSettingsVkMethod : VkMethod<Int>() {

    override val name = "groups.setLongPollSettings"
    override val resultSerializer = serializer<Int>()
    override val httpMethod = HttpMethod.Post

    var groupId by parameter<Long>("group_id")
    var enabled by parameter<Int>("enabled")

    var messageNew by parameter<Int>("message_new")
    var messageReply by parameter<Int>("message_reply")
    var messageAllow by parameter<Int>("message_allow")
    var messageDeny by parameter<Int>("message_deny")
    var messageEdit by parameter<Int>("message_edit")
    var messageTypingState by parameter<Int>("message_typing_state")
    var photoNew by parameter<Int>("photo_new")
    var audioNew by parameter<Int>("audio_new")
    var videoNew by parameter<Int>("video_new")
    var wallReplyNew by parameter<Int>("wall_reply_new")
    var wallReplyEdit by parameter<Int>("wall_reply_edit")
    var wallReplyDelete by parameter<Int>("wall_reply_delete")
    var wallReplyRestore by parameter<Int>("wall_reply_restore")
    var wallPostNew by parameter<Int>("wall_post_new")
    var wallRepost by parameter<Int>("wall_repost")
    var boardPostNew by parameter<Int>("board_post_new")
    var boardPostEdit by parameter<Int>("board_post_edit")
    var boardPostRestore by parameter<Int>("board_post_restore")
    var boardPostDelete by parameter<Int>("board_post_delete")
    var photoCommentNew by parameter<Int>("photo_comment_new")
    var photoCommentEdit by parameter<Int>("photo_comment_edit")
    var photoCommentDelete by parameter<Int>("photo_comment_delete")
    var photoCommentRestore by parameter<Int>("photo_comment_restore")
    var videoCommentNew by parameter<Int>("video_comment_new")
    var videoCommentEdit by parameter<Int>("video_comment_edit")
    var videoCommentDelete by parameter<Int>("video_comment_delete")
    var videoCommentRestore by parameter<Int>("video_comment_restore")
    var marketCommentNew by parameter<Int>("market_comment_new")
    var marketCommentEdit by parameter<Int>("market_comment_edit")
    var marketCommentDelete by parameter<Int>("market_comment_delete")
    var marketCommentRestore by parameter<Int>("market_comment_restore")
    var pollVoteNew by parameter<Int>("poll_vote_new")
    var groupJoin by parameter<Int>("group_join")
    var groupLeave by parameter<Int>("group_leave")
    var groupChangeSettings by parameter<Int>("group_change_settings")
    var groupChangePhoto by parameter<Int>("group_change_photo")
    var groupOfficersEdit by parameter<Int>("group_officers_edit")
    var userBlock by parameter<Int>("user_block")
    var userUnblock by parameter<Int>("user_unblock")
    var likeAdd by parameter<Int>("like_add")
    var likeRemove by parameter<Int>("like_remove")
    var messageEvent by parameter<Int>("message_event")
    var donutSubscriptionCreate by parameter<Int>("donut_subscription_create")
    var donutSubscriptionProlonged by parameter<Int>("donut_subscription_prolonged")
    var donutSubscriptionCancelled by parameter<Int>("donut_subscription_cancelled")
    var donutSubscriptionPriceChanged by parameter<Int>("donut_subscription_price_changed")
    var donutSubscriptionExpired by parameter<Int>("donut_subscription_expired")
    var donutMoneyWithdraw by parameter<Int>("donut_money_withdraw")
    var donutMoneyWithdrawError by parameter<Int>("donut_money_withdraw_error")
}
