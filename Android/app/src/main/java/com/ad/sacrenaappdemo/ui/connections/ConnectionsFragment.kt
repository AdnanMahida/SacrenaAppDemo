package com.ad.sacrenaappdemo.ui.connections

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ad.sacrenaappdemo.R

import com.ad.sacrenaappdemo.databinding.FragmentConnectionsBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

class ConnectionsFragment : Fragment() {

    private var _binding: FragmentConnectionsBinding? = null
    private val binding get() = _binding!!

    private val client = ChatClient.instance()
    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConnectionsBinding.inflate(inflater, container, false)

        setupUser()
        setupChannels()
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout_menu -> {
                    logout()
                    true
                }

                else -> false
            }
        }
        binding.channelsView.setChannelDeleteClickListener { channel ->
            deleteChannel(channel)
        }
        binding.channelsView.setChannelItemClickListener { channel ->
            val action =
                ConnectionsFragmentDirections.actionChannelFragmentToChatFragment(channel.cid)
            findNavController().navigate(action)
        }
    }

    private fun setupUser() {
        if (client.getCurrentUser() == null) {
            user = User(
                id = "Alice",
                extraData = mutableMapOf(
                    "name" to "Alice",
                    "county" to "US",
                    "image" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVJIj15sALjBruHk2y1bwsh0xOxPhNUg17rQ&s"
                )
            )
            val token = client.devToken(user.id)
            client.connectUser(
                user = user,
                token = token
            ).enqueue { result ->
                if (result.isSuccess) {
                    Log.d(
                        "ConnectionsFragment",
                        "Success Connecting the User ${result.getOrNull()}"
                    )
                } else {
                    Log.d("ConnectionsFragment", result.errorOrNull()?.message ?: "")
                }
            }
        }
    }

    private fun setupChannels() {
        val filters = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(client.getCurrentUser()?.id ?: ""))
        )
        val viewModelFactory = ChannelListViewModelFactory(
            filters,
            ChannelListViewModel.DEFAULT_SORT
        )
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        listViewModel.bindView(binding.channelsView, viewLifecycleOwner)
    }


    private fun deleteChannel(channel: Channel) {
        val channelClient = client.channel("messaging", channel.cid)

        channelClient.delete().enqueue { result ->
            if (result.isSuccess) {
                showToast("Channel: ${channel.name} removed!")
            } else {
                Log.e("ConnectionsFragment", result.errorOrNull()?.message.toString())
            }
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            client.disconnect(flushPersistence = true)
            findNavController().navigate(R.id.action_channelFragment_to_loginFragment)
            showToast("Logged out successfully")
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure you want to logout?")
        builder.create().show()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}









