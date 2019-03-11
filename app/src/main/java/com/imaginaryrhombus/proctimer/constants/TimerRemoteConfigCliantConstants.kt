package com.imaginaryrhombus.proctimer.constants

import com.imaginaryrhombus.proctimer.BuildConfig

/**
 * Firebase RemoteConfig 関連の定数を保持するクラス.
 */
class TimerRemoteConfigCliantConstants {
    companion object {
        /**
         * 強制アップデートバージョンを取得するキー.
         */
        val versionKey = if (BuildConfig.DEBUG) "LEAST_VERSION_DEBUG" else "LEAST_VERSION"

        /**
         * ストアアドレスのキー.
         */
        val storeUrlKey = "STORE_URL"

        /**
         * プライバシーポリシーアドレスのキー.
         */
        val privacyPolicyKey = "PRIVACY_POLICY_URL"
    }
}
