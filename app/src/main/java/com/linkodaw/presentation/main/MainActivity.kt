package com.linkodaw.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.requestPermissions
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.linkodaw.databinding.ActivityMainBinding
import com.linkodaw.domain.model.AudioState
import com.linkodaw.domain.model.Track
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            setupRecordingDirectory()
        } else {
            showPermissionDeniedDialog(permissions.entries.filter { !it.value }.map { it.key })
        }
    }

    private val recordAudioPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            setupRecordingDirectory()
        } else {
            Toast.makeText(this, "Se requiere permiso de micrófono para grabar", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()
        setupUI()
        observeState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }

        if (permissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            setupRecordingDirectory()
        }
    }

    private fun setupRecordingDirectory() {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "linkoDAW")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private fun showPermissionDeniedDialog(deniedPermissions: List<String>) {
        val message = "Permisos denegados: ${deniedPermissions.joinToString(", ")}. La app no funcionará correctamente."
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setupUI() {
        binding.apply {
            btnRecord.setOnClickListener { viewModel.startRecording() }
            btnPlay.setOnClickListener {
                val selectedTrack = tracksAdapter.selectedTrack
                selectedTrack?.let { viewModel.playTrack(it) }
            }
            btnStop.setOnClickListener { viewModel.stopAll() }
            btnPause.setOnClickListener {
                val state = viewModel.uiState.value
                when (state) {
                    is AudioState.Recording -> viewModel.pauseRecording()
                    is AudioState.Playing -> viewModel.pausePlayback()
                    is AudioState.Paused -> {
                        if (state is AudioState.Paused) {
                            viewModel.resumeRecording()
                        }
                    }
                    else -> {}
                }
            }

            rvTracks.layoutManager = LinearLayoutManager(this@MainActivity)
            rvTracks.adapter = tracksAdapter
        }
    }

    private val tracksAdapter = TracksAdapter(
        onTrackClick = { track -> viewModel.playTrack(track) },
        onDeleteClick = { track -> viewModel.deleteTrack(track) }
    )

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUIForState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                tracksAdapter.submitList(tracks)
            }
        }
    }

    private fun updateUIForState(state: AudioState) {
        binding.apply {
            when (state) {
                is AudioState.Idle -> {
                    tvStatus.text = state.message
                    btnRecord.isEnabled = true
                    btnRecord.text = "GRABAR"
                    btnPlay.isEnabled = tracksAdapter.currentList.isNotEmpty()
                    btnStop.isEnabled = false
                    btnPause.isEnabled = false
                    btnPause.text = "PAUSAR"
                }
                is AudioState.Recording -> {
                    tvStatus.text = "Grabando... ${state.currentDuration / 1000}s | Amplitud: ${(state.amplitude * 100).toInt()}%"
                    btnRecord.isEnabled = false
                    btnRecord.text = "GRABANDO"
                    btnPlay.isEnabled = false
                    btnStop.isEnabled = true
                    btnPause.isEnabled = true
                    btnPause.text = "PAUSAR"
                }
                is AudioState.Playing -> {
                    tvStatus.text = "Reproduciendo... ${state.currentPosition / 1000}s / ${state.totalDuration / 1000}s"
                    btnRecord.isEnabled = false
                    btnPlay.isEnabled = false
                    btnPlay.text = "REPRODUCIENDO"
                    btnStop.isEnabled = true
                    btnPause.isEnabled = true
                    btnPause.text = "PAUSAR"
                }
                is AudioState.Paused -> {
                    tvStatus.text = "Pausado en ${state.currentPosition / 1000}s"
                    btnRecord.isEnabled = false
                    btnPlay.isEnabled = true
                    btnPlay.text = "REANUDAR"
                    btnStop.isEnabled = true
                    btnPause.isEnabled = true
                    btnPause.text = "REANUDAR"
                }
                is AudioState.Error -> {
                    tvStatus.text = "Error: ${state.message}"
                    btnRecord.isEnabled = true
                    btnRecord.text = "GRABAR"
                    btnPlay.isEnabled = tracksAdapter.currentList.isNotEmpty()
                    btnStop.isEnabled = false
                    btnPause.isEnabled = false
                    Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                }
                is AudioState.PermissionRequired -> {
                    tvStatus.text = "Permiso requerido: ${state.permission}"
                    recordAudioPermissionLauncher.launch(state.permission)
                }
            }
        }
    }
}