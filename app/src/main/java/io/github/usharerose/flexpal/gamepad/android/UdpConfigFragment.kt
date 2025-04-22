package io.github.usharerose.flexpal.gamepad.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.github.usharerose.flexpal.gamepad.android.databinding.FragmentUdpConfigBinding
class UdpConfigFragment : Fragment() {

    private lateinit var binding: FragmentUdpConfigBinding
    private val viewModel: UdpConfigViewModel by viewModels {
        UdpConfigViewModelFactory(
            requireActivity().getSharedPreferences("udp_config", Context.MODE_PRIVATE)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUdpConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.udpConfig.observe(viewLifecycleOwner) { config ->
            binding.hostInput.setText(config.host)
            binding.portInput.setText(config.port.toString())
        }

        binding.saveButton.setOnClickListener {
            val newConfig = UdpConfig(
                host = binding.hostInput.text.toString(),
                port = binding.portInput.text.toString().toIntOrNull() ?: 8080
            )
            viewModel.updateUdpConfig(newConfig)
        }
    }
}
