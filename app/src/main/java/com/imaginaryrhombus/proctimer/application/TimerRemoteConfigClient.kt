package com.imaginaryrhombus.proctimer.application

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.constants.TimerRemoteConfigClientConstants

/**
 * Firebase RemoteConfig からの情報取得用クライアントクラス.
 */
class TimerRemoteConfigClient : TimerRemoteConfigClientInterface {

    /**
     * 空白文字列のダミーテキスト.
     * 空白かの判断に使用する.
     * 空白かの判断に使用する.
     */
    private val emptyStringDummy = "_empty_"

    /**
     * RemoteConfig 本体.
     */
    private val remoteConfig =
        requireNotNull(FirebaseRemoteConfig.getInstance()).apply {
            setDefaultsAsync(R.xml.timer_remote_config_default)
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
            remoteConfig.activate().addOnCompleteListener {
                postApply.invoke()
            }
        }
    }

    /**
     * アップデート通知の判断のバージョンを取得する.
     */
    override val leastVersion: String
        get() {
            return remoteConfig.getString(TimerRemoteConfigClientConstants.versionKey)
        }

    /**
     * アップデート通知時の遷移先ストアアドレスを取得する.
     */
    override val storeUrl: String
        get() {
            val ret = remoteConfig.getString(TimerRemoteConfigClientConstants.storeUrlKey)
            return if (ret == emptyStringDummy) {
                ""
            } else {
                ret
            }
        }

    /**
     * アプリ内メニューから遷移するプライバシーポリシーのアドレスを取得する,
     */
    override val privacyPolicyUrl: String
        get() {
            val ret = remoteConfig.getString(TimerRemoteConfigClientConstants.privacyPolicyKey)
            return if (ret == emptyStringDummy) {
                ""
            } else {
                ret
            }
        }
}
