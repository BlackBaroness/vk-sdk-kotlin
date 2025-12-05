package io.github.blackbaroness.vk.model.method.groups

import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.internal.BooleanIntParameterConverter
import io.ktor.http.*
import kotlinx.serialization.serializer

// https://dev.vk.com/ru/method/groups.setLongPollSettings
class GroupsSetLongPollSettingsVkMethod : VkMethod<Int>() {

    override val name = "groups.setLongPollSettings"
    override val resultSerializer = serializer<Int>()
    override val httpMethod = HttpMethod.Post
    override val resultStyle = ResultStyle.WRAPPED_IN_RESPONSE

    var groupId by parameter<Long>("group_id")
    var enabled by parameter("enabled", BooleanIntParameterConverter)

    var messageNew by parameter("message_new", BooleanIntParameterConverter)
    var messageReply by parameter("message_reply", BooleanIntParameterConverter)
    var messageAllow by parameter("message_allow", BooleanIntParameterConverter)
    var messageDeny by parameter("message_deny", BooleanIntParameterConverter)
    var messageEdit by parameter("message_edit", BooleanIntParameterConverter)
    var messageTypingState by parameter("message_typing_state", BooleanIntParameterConverter)
    var photoNew by parameter("photo_new", BooleanIntParameterConverter)
    var audioNew by parameter("audio_new", BooleanIntParameterConverter)
    var videoNew by parameter("video_new", BooleanIntParameterConverter)
    var wallReplyNew by parameter("wall_reply_new", BooleanIntParameterConverter)
    var wallReplyEdit by parameter("wall_reply_edit", BooleanIntParameterConverter)
    var wallReplyDelete by parameter("wall_reply_delete", BooleanIntParameterConverter)
    var wallReplyRestore by parameter("wall_reply_restore", BooleanIntParameterConverter)
    var wallPostNew by parameter("wall_post_new", BooleanIntParameterConverter)
    var wallRepost by parameter("wall_repost", BooleanIntParameterConverter)
    var boardPostNew by parameter("board_post_new", BooleanIntParameterConverter)
    var boardPostEdit by parameter("board_post_edit", BooleanIntParameterConverter)
    var boardPostRestore by parameter("board_post_restore", BooleanIntParameterConverter)
    var boardPostDelete by parameter("board_post_delete", BooleanIntParameterConverter)
    var photoCommentNew by parameter("photo_comment_new", BooleanIntParameterConverter)
    var photoCommentEdit by parameter("photo_comment_edit", BooleanIntParameterConverter)
    var photoCommentDelete by parameter("photo_comment_delete", BooleanIntParameterConverter)
    var photoCommentRestore by parameter("photo_comment_restore", BooleanIntParameterConverter)
    var videoCommentNew by parameter("video_comment_new", BooleanIntParameterConverter)
    var videoCommentEdit by parameter("video_comment_edit", BooleanIntParameterConverter)
    var videoCommentDelete by parameter("video_comment_delete", BooleanIntParameterConverter)
    var videoCommentRestore by parameter("video_comment_restore", BooleanIntParameterConverter)
    var marketCommentNew by parameter("market_comment_new", BooleanIntParameterConverter)
    var marketCommentEdit by parameter("market_comment_edit", BooleanIntParameterConverter)
    var marketCommentDelete by parameter("market_comment_delete", BooleanIntParameterConverter)
    var marketCommentRestore by parameter("market_comment_restore", BooleanIntParameterConverter)
    var pollVoteNew by parameter("poll_vote_new", BooleanIntParameterConverter)
    var groupJoin by parameter("group_join", BooleanIntParameterConverter)
    var groupLeave by parameter("group_leave", BooleanIntParameterConverter)
    var groupChangeSettings by parameter("group_change_settings", BooleanIntParameterConverter)
    var groupChangePhoto by parameter("group_change_photo", BooleanIntParameterConverter)
    var groupOfficersEdit by parameter("group_officers_edit", BooleanIntParameterConverter)
    var userBlock by parameter("user_block", BooleanIntParameterConverter)
    var userUnblock by parameter("user_unblock", BooleanIntParameterConverter)
    var likeAdd by parameter("like_add", BooleanIntParameterConverter)
    var likeRemove by parameter("like_remove", BooleanIntParameterConverter)
    var messageEvent by parameter("message_event", BooleanIntParameterConverter)
    var donutSubscriptionCreate by parameter("donut_subscription_create", BooleanIntParameterConverter)
    var donutSubscriptionProlonged by parameter("donut_subscription_prolonged", BooleanIntParameterConverter)
    var donutSubscriptionCancelled by parameter("donut_subscription_cancelled", BooleanIntParameterConverter)
    var donutSubscriptionPriceChanged by parameter("donut_subscription_price_changed", BooleanIntParameterConverter)
    var donutSubscriptionExpired by parameter("donut_subscription_expired", BooleanIntParameterConverter)
    var donutMoneyWithdraw by parameter("donut_money_withdraw", BooleanIntParameterConverter)
    var donutMoneyWithdrawError by parameter("donut_money_withdraw_error", BooleanIntParameterConverter)
}
