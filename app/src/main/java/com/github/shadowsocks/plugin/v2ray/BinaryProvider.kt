package com.github.shadowsocks.plugin.v2ray

import android.net.Uri
import android.os.ParcelFileDescriptor
import com.github.shadowsocks.plugin.NativePluginProvider
import com.github.shadowsocks.plugin.PathProvider
import java.io.File
import java.io.FileNotFoundException

class BinaryProvider : NativePluginProvider() {
    override fun populateFiles(provider: PathProvider) {
        provider.addPath("v2ray", 0b111101101)
    }
    override fun getExecutable() = context!!.applicationInfo.nativeLibraryDir + "/libv2ray.so"
    override fun openFile(uri: Uri?): ParcelFileDescriptor = when (uri?.path) {
        "/v2ray" -> ParcelFileDescriptor.open(File(getExecutable()), ParcelFileDescriptor.MODE_READ_ONLY)
        else -> throw FileNotFoundException()
    }
}
