package com.imaginaryrhombus.proctimer.ui

import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.constants.TimerConstants.Companion.TimerTheme

/**
 * テーマ関連の変換サポート.
 */
class TimerThemeConverter {
    companion object {

        /**
         * 変換の対応を行う HashMap.
         */
        private val resourceMap = hashMapOf(
            TimerTheme.Light to R.style.Light,
            TimerTheme.Dark to R.style.Dark
        )

        /**
         * 逆方向の変換に対応させるための HashMap.
         */
        private val themeMap =
            resourceMap.entries.associateBy({ it.value }) { it.key }

        /**
         * TimerTheme からリソースIDに変換する.
         * @param theme 変換元enum.
         */
        fun toResourceId(theme: TimerTheme): Int {
            return resourceMap.getValue(theme)
        }

        /**
         * リソースID から TimerTheme に変換する.
         */
        fun fromResourceId(resourceId: Int): TimerTheme {
            return themeMap.getValue(resourceId)
        }
    }
}
