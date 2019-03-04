package com.imaginaryrhombus.proctimer.application

import com.imaginaryrhombus.proctimer.BuildConfig
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * アップデートが必要かどうかを確認し、必要な場合はコンストラクタで渡したコールバックを呼ぶクラス.
 */
class UpdateChecker(private val updateRequiredListener: UpdateRequiredListener) : KoinComponent {

    private val remoteConfigClient: TimerRemoteConfigClientInterface by inject()

    /**
     * アップデートが必要なときに呼ばれるインターフェース.
     */
    interface UpdateRequiredListener {
        /**
         * アップデートが必要なときに checkUpdateRequired() を呼び出したときに呼び出される.
         * @param updateUrl アップデートがあるストアのURL.
         */
        fun onUpdateRequired(updateUrl: String)
    }

    /**
     * アップデートが必要か(実行中のアプリが強制アップデート対象か)を確認する.
     * アップデートが必要な場合, コールバックが呼ばれる.
     */
    fun checkUpdateRequired() {
        remoteConfigClient.fetchRemoteConfig(postApply = {
            val requiredVersion = remoteConfigClient.leastVersion

            if (isUpdateRequired(
                    BuildConfig.VERSION_NAME,
                    requiredVersion
                )) {
                updateRequiredListener.onUpdateRequired(remoteConfigClient.storeUrl)
            }
        })
    }

    /**
     * バージョン文字列を比較してアップデートが必要かを判断する.
     * @param currentVersion 現在のバージョン文字列.
     * @param requiredVersion 強制アップデートバージョン文字列.
     * @return アップデートが必要なバージョンの場合は true.
     * @note x.x.x.x のようにピリオドで区切られたバージョンで動作する.ただし、数字一桁でも問題ない.
     */
    private fun isUpdateRequired(currentVersion: String, requiredVersion: String): Boolean {

        val currentSubVersion = currentVersion.substringBefore(".", currentVersion)
        val requiredSubVersion = requiredVersion.substringBefore(".", requiredVersion)

        val currentSubIntVersion = currentSubVersion.toIntOrNull() ?: 0
        val requiredSubIntVersion = requiredSubVersion.toIntOrNull() ?: 0

        var ret = false

        when {
            requiredSubIntVersion > currentSubIntVersion -> {
                ret = true
            }
            requiredSubIntVersion == currentSubIntVersion -> {
                val nextCurrentVersion = currentVersion.substringAfter(".", "")
                val nextRequiredVersion = requiredVersion.substringAfter(".", "")
                ret = if (nextCurrentVersion.isEmpty() && nextRequiredVersion.isEmpty()) {
                    false
                } else {
                    isUpdateRequired(nextCurrentVersion, nextRequiredVersion)
                }
            }
        }

        return ret
    }
}
