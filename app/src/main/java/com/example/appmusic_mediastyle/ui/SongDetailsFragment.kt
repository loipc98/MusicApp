package com.example.appmusic_mediastyle.ui

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_CLEAR
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_END_SONG
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_NEXT
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_PAUSE
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_PREVIOUS
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_RESUME
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_START
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_UPDATE_SEEKBAR
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_UPDATE_TIME
import com.example.appmusic_mediastyle.R
import com.example.appmusic_mediastyle.databinding.FragmentSongDetailsBinding
import com.example.appmusic_mediastyle.viewmodel.MusicViewModel
import com.example.appmusic_mediastyle.viewmodel.MusicViewModel.Companion.DETAILS_FRAGMENT_ID
import com.example.musicappmock.model.Song

class SongDetailsFragment : Fragment() {

    private lateinit var viewModel: MusicViewModel
    private var song: Song? = null
    private var _binding: FragmentSongDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var anim: ObjectAnimator

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle: Bundle? = intent?.extras
            if (bundle == null) return
            else {
                viewModel.isPlaying = bundle.getBoolean("status_player")
                val action = bundle.getInt("action_music", 0)
                handleActionMusic(action, bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongDetailsBinding.inflate(inflater, container, false)
        initData()

        //Local Broadcast
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mBroadcastReceiver,
            IntentFilter("send_music_data_to_activity")
        )

        viewModel.iDFragment = DETAILS_FRAGMENT_ID

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViewSong()

        //Play or pause music with button
        binding.imgDetailPlay.setOnClickListener {
            if (viewModel.isPlaying) viewModel.sendActionToService(ACTION_PAUSE, requireContext())
            else viewModel.sendActionToService(ACTION_RESUME, requireContext())
        }

        //Navigation icon back
        binding.toolbarDetails.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        //Tua nhac
        binding.sbTimeMusic.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.updateSeekBar = binding.sbTimeMusic.progress
                viewModel.sendActionToService(ACTION_UPDATE_SEEKBAR, requireContext())
            }
        })

        //Next and previous button
        binding.imgDetailNext.setOnClickListener {
            viewModel.nextSong()
            viewModel.sendSongToService(requireContext())
        }
        binding.imgDetailPrevious.setOnClickListener {
            viewModel.backSong()
            viewModel.sendSongToService(requireContext())
        }
    }

    private fun initData() {
        //init MainViewModel
        viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]

        // Init Rotate Animation
        anim = ObjectAnimator.ofFloat(binding.imgDetailCircleAvatar, View.ROTATION, 0f, 360f)
            .setDuration(15000)
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = LinearInterpolator()

    }

    //put data into view in screen, and play music
    private fun updateViewSong() {
        song = viewModel.currentListSongs[viewModel.currentPositionSong]

        //Button pause or resume
        binding.imgDetailPlay.setImageResource(R.drawable.ic_pause_circle)

        //Load name song, singer
        binding.tvDetailSongName.text = if (TextUtils.isEmpty(song?.title)) "" else song?.title

        binding.tvDetailSinger.text = if (TextUtils.isEmpty(song?.creator)) "" else song?.creator

        //Load total Time
        binding.tvTotalTime.text = viewModel.getStringTotalTimeSong()

        //Init seekbar
        binding.sbTimeMusic.max = viewModel.currentTotalTimeSong

        //Load current Time Song
        viewModel.currentPositionTimeSong = 0
        binding.tvCurrentTime.text = viewModel.getStringCurrentTimeSong()

        //Load image
        val urlAvatar = if (TextUtils.isEmpty(song?.avatar)) "" else song?.avatar

        Glide.with(binding.root).load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loaderror)
            .into(binding.imgDetailCircleAvatar)

        if (viewModel.isPlaying) anim.start()
        setButtonPausePlay()
    }

    fun handleActionMusic(action: Int, bundle: Bundle) {
        if(viewModel.iDFragment!= DETAILS_FRAGMENT_ID) return
        when (action) {
            ACTION_START -> {
                viewModel.currentTotalTimeSong = bundle.getInt("total_time_song", 0)
                viewModel.isDisplayLayoutPlayer = true
                updateViewSong()
            }
            ACTION_RESUME -> {
                viewModel.isDisplayLayoutPlayer = true
                setButtonPausePlay()
                anim.resume()
            }
            ACTION_PAUSE -> {
                viewModel.isDisplayLayoutPlayer = true
                setButtonPausePlay()
                anim.pause()
            }
            ACTION_CLEAR -> {
                viewModel.isDisplayLayoutPlayer = false
                viewModel.isPlaying = false
                setButtonPausePlay()
            }
            ACTION_NEXT -> {
                viewModel.nextSong()
                viewModel.sendSongToService(requireContext())
            }
            ACTION_PREVIOUS -> {
                viewModel.backSong()
                viewModel.sendSongToService(requireContext())
            }
            ACTION_UPDATE_TIME -> {
                viewModel.currentPositionTimeSong = bundle.getInt("current_position_time_song", 0)
                binding.tvCurrentTime.text = viewModel.getStringCurrentTimeSong()
                binding.sbTimeMusic.progress = viewModel.currentPositionTimeSong
            }
            ACTION_END_SONG -> {
                viewModel.nextSong()
                viewModel.sendSongToService(requireContext())
            }
        }
    }

    private fun setButtonPausePlay() {
        if (!viewModel.isPlaying) binding.imgDetailPlay.setImageResource(R.drawable.ic__play_circle)
        else binding.imgDetailPlay.setImageResource(R.drawable.ic_pause_circle)
    }
}