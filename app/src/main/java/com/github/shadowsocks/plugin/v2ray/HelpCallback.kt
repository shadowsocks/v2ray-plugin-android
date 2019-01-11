package com.github.shadowsocks.plugin.v2ray

import com.github.shadowsocks.plugin.PluginOptions

class HelpCallback : com.github.shadowsocks.plugin.HelpCallback() {
    override fun produceHelpMessage(options: PluginOptions): CharSequence =
    """
  host=string
        Host header for websocket. (default "cloudfront.com")

  mode=string
        Transport mode: ws/quic. (default "ws")

  path=string
        URL path for websocket. (default "/")

  security=string
        Transport security: none/tls. (default "none")

    """.trimIndent()

}
