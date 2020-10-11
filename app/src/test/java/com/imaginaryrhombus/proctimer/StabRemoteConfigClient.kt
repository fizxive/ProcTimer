package com.imaginaryrhombus.proctimer

import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClientInterface

/**
 * テスト時に使用する RemoteConfig のクライアントスタブ.
 */
class StabRemoteConfigClient : TimerRemoteConfigClientInterface {

    /**
     * RemoteConfig から情報を取得する(スタブ).
     * @param cacheExpireSeconds キャッシュが無効化される時間を設定.
     * @param preApply 情報取得後、適用前に実行される.
     * @param postApply 情報取得後、適用後に実行される.
     * @note 実際は preApply と postApply だけ実行される.
     */
    override fun fetchRemoteConfig(
        cacheExpireSeconds: Long,
        preApply: () -> Unit,
        postApply: () -> Unit
    ) {
        preApply.invoke()
        postApply.invoke()
    }

    override val leastVersion: String
        get() = ""

    override val storeUrl: String
        get() = ""

    override val privacyPolicyUrl: String
        get() = ""
}
