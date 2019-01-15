/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2019 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2019 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks.plugin.v2ray

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.shadowsocks.plugin.PluginOptions
import com.google.android.material.snackbar.Snackbar
import java.lang.RuntimeException

class ConfigFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    companion object {
        const val REQUEST_BROWSE_CERTIFICATE = 1
    }

    private val mode by lazy { findPreference<ListPreference>("mode") }
    private val host by lazy { findPreference<EditTextPreference>("host") }
    private val path by lazy { findPreference<EditTextPreference>("path") }
    private val certRaw by lazy { findPreference<EditTextPreference>("certRaw") }

    private fun readMode(value: String = mode.value) = when (value) {
        "websocket-http" -> Pair(null, null)
        "websocket-tls" -> Pair(null, "")
        "quic-tls" -> Pair("quic", null)
        else -> {
            check(false)
            Pair(null, null)
        }
    }

    val options get() = PluginOptions().apply {
        val (mode, tls) = readMode()
        putWithDefault("mode", mode)
        putWithDefault("tls", tls)
        putWithDefault("host", host.text, "cloudfront.com")
        putWithDefault("path", path.text, "/")
        putWithDefault("certRaw", certRaw.text.replace("\n", ""), "")
    }

    fun onInitializePluginOptions(options: PluginOptions) {
        mode.value = when {
            options["mode"] ?: "websocket" == "quic" -> "quic-tls"
            options["tls"] != null -> "websocket-tls"
            else -> "websocket-http"
        }.also { onPreferenceChange(null, it) }
        host.text = options["host"] ?: "cloudfront.com"
        path.text = options["path"] ?: "/"
        certRaw.text = options["certRaw"]
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.config)
        mode.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val (mode, tls) = readMode(newValue as String)
        path.isEnabled = mode == null
        certRaw.isEnabled = mode != null || tls != null
        return true
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference == certRaw) CertificatePreferenceDialogFragment().apply {
            setKey(certRaw.key)
            setTargetFragment(this@ConfigFragment, 0)
        }.show(fragmentManager ?: return, certRaw.key) else super.onDisplayPreferenceDialog(preference)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_BROWSE_CERTIFICATE -> {
                if (resultCode != Activity.RESULT_OK) return
                val activity = requireActivity()
                try {
                    // we read all its content here to avoid content URL permission issues
                    certRaw.text = activity.contentResolver.openInputStream(data!!.data!!)!!
                            .bufferedReader().readText()
                } catch (e: RuntimeException) {
                    Snackbar.make(activity.findViewById<View>(R.id.content), e.localizedMessage, Snackbar.LENGTH_LONG)
                            .show()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
