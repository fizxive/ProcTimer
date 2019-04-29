package com.imaginaryrhombus.proctimer.application

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.imaginaryrhombus.proctimer.constants.TimerRemoteConfigCliantConstants

/**
 * Firebase RemoteConfig からの情報取得用クライアントクラス.
 */
class TimerRemoteConfigClient : TimerRemoteConfigClientInterface {

    /**
     * RemoteConfig 本体.
     */
    private val remoteConfig =
        requireNotNull(FirebaseRemoteConfig.getInstance()).apply {
            setDefaults(
                hashMapOf<String, Any>(
                    TimerRemoteConfigCliantConstants.versionKey to "0.0.0.0",
                    TimerRemoteConfigCliantConstants.storeUrlKey to "",
                    TimerRemoteConfigCliantConstants.privacyPolicyKey to ""
                )
            )
        }

    /**
     * RemoteConfig から情報を取得する.
     * @param cacheExpireSeconds キャッシュが無効化される時間を設定.
     * @param preApply 情報取得後、適用前に実行される.
     * @param postApply 情報取得後、適用後に実行される.
     * @note 情報取得に失敗した場合はどちらも実行されない.適用に失敗した場合は実行される.
     */
    override fun fetchRemoteConfig(
        cacheExpireSeconds: Long,
        preApply: () -> Unit,
        postApply: () -> Unit
    ) {
        remoteConfig.fetch(cacheExpireSeconds).addOnCompleteListener {
            preApply.invoke()
            remoteConfig.activate().addOnCompleteListener{
                postApply.invoke()
            }
        }
    }

    /**
     * アップデート通知の判断のバージョンを取得する.
     */
    override val leastVersion: String
    get() {
        return remoteConfig.getString(TimerRemoteConfigCliantConstants.versionKey)
    }

    /**
     * アップデート通知時の遷移先ストアアドレスを取得する.
     */
    override val storeUrl: String
    get() {
        return remoteConfig.getString(TimerRemoteConfigCliantConstants.storeUrlKey)
    }

    /**
     * アプリ内メニューから遷移するプライバシーポリシーのアドレスを取得する,
     */
    override val privacyPolicyUrl: String
    get() {
        return remoteConfig.getString(TimerRemoteConfigCliantConstants.privacyPolicyKey)
    }
}
