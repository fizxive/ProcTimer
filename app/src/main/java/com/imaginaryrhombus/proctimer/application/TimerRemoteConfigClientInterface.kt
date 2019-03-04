package com.imaginaryrhombus.proctimer.application

/**
 * Firebase RemoteConfig から情報を取得するクライアントのインターフェース.
 */
interface TimerRemoteConfigClientInterface {

    /**
     * RemoteConfig から情報を取得する.
     * @param cacheExpireSeconds キャッシュが無効化される時間を設定.
     * @param preApply 情報取得後、適用前に実行される.
     * @param postApply 情報取得後、適用後に実行される.
     * @note 情報取得に失敗した場合はどちらも実行されない.
     */
    fun fetchRemoteConfig(
        cacheExpireSeconds: Long = 60L,
        preApply: () -> Unit = {},
        postApply: () -> Unit = {}
    )

    /**
     * アップデート通知の判断のバージョンを取得する.
     */
    val leastVersion: String

    /**
     * アップデート通知時の遷移先ストアアドレスを取得する.
     */
    val storeUrl: String

    /**
     * アプリ内メニューから遷移するプライバシーポリシーのアドレスを取得する,
     */
    val privacyPolicyUrl: String
}
