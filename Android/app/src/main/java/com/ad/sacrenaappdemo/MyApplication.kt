package com.ad.sacrenaappdemo

import android.app.Application
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Create a state plugin factory
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                // Enables background sync which syncs user actions performed while offline
                backgroundSyncEnabled = true,
                // Enables tracking online states for users
                userPresence = true
            ),
            appContext = this
        )

        ChatClient.Builder(getString(R.string.api_key), this).withPlugins(statePluginFactory)
            .logLevel(ChatLogLevel.ALL).build()
    }
}