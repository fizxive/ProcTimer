package com.imaginaryrhombus.proctimer.constants

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.imaginaryrhombus.proctimer.BuildConfig

/**
 * アプリケーション全体に関わるユーティリティ.
 * @todo このクラス全体のテスト.
 */
class ApplicationUtility {
    companion object {
        /**
         * アップデートが必要か(実行中のアプリが強制アップデート対象か)を確認する.
         * @return 強制アップデートならば true.
         */
        fun checkUpdateRequired(): Boolean {
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

            val configName = if (BuildConfig.DEBUG) "LEAST_VERSION_DEBUG" else "LEAST_VERSION"

            firebaseRemoteConfig.setDefaults(HashMap<String, Any>().apply {
                put(configName, "0.0.0.0")
            })

            var ret = false

            firebaseRemoteConfig.fetch().addOnCompleteListener {
                firebaseRemoteConfig.activateFetched()

                val requiredVersion = firebaseRemoteConfig.getString(configName)

                ret = isRequiredUpdate(BuildConfig.VERSION_NAME, requiredVersion)
            }

            return ret
        }

        /**
         * バージョン文字列を比較してアップデートが必要かを判断する.
         * @param currentVersion 現在のバージョン文字列.
         * @param requiredVersion 強制アップデートバージョン文字列.
         * @return アップデートが必要なバージョンの場合は true.
         * @note x.x.x.x のようにピリオドで区切られたバージョンで動作する.ただし、数字一桁でも問題ない.
         */
        private fun isRequiredUpdate(currentVersion: String, requiredVersion: String): Boolean {

            val currentSubVersion = currentVersion.substringBefore(".", "0")
            val requiredSubVersion = requiredVersion.substringBefore(".", "0")

            val currentSubIntVersion = currentSubVersion.toIntOrNull() ?: 0
            val requiredSubIntVersion = requiredSubVersion.toIntOrNull() ?: 0

            if (requiredSubIntVersion > currentSubIntVersion) {
                return true
            } else if (currentSubIntVersion > requiredSubIntVersion) {
                return false
            } else {
                return isRequiredUpdate(
                    currentVersion.substringAfter(".", ""),
                    requiredVersion.substringAfter(".", ""))
            }
        }
    }
}
