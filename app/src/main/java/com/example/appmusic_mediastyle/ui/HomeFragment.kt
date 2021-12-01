package com.example.appmusic_mediastyle.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_CLEAR
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_END_SONG
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_NEXT
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_PAUSE
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_PREVIOUS
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_RESUME
import com.example.appmusic_mediastyle.MyMusicService.Companion.ACTION_START
import com.example.appmusic_mediastyle.R
import com.example.appmusic_mediastyle.ZoomOutPageTransformer
import com.example.appmusic_mediastyle.adapter.IClickSongItemListener
import com.example.appmusic_mediastyle.adapter.PhotoAdapter
import com.example.appmusic_mediastyle.adapter.TopMusicOfCountryItemAdapter
import com.example.appmusic_mediastyle.databinding.FragmentHomeBinding
import com.example.appmusic_mediastyle.viewmodel.MusicViewModel
import com.example.appmusic_mediastyle.viewmodel.MusicViewModel.Companion.HOME_FRAGMENT_ID
import com.example.musicappmock.model.Photo
import com.example.musicappmock.model.Song
import com.example.musicappmock.model.Top100OfCountry
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var viewModel: MusicViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var song: Song
    private var listTopMusicPhoto: ArrayList<Photo> = ArrayList()
    val mHandle = Handler(Looper.getMainLooper())
    val mRunnable = Runnable {
        if (binding.vpg2ImgSliderTopMusic.currentItem == listTopMusicPhoto.size - 1) {
            binding.vpg2ImgSliderTopMusic.currentItem = 0
        } else {
            binding.vpg2ImgSliderTopMusic.currentItem += 1
        }
    }
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle: Bundle? = intent?.extras
            if (bundle == null) return
            else {
                viewModel.isPlaying = bundle.getBoolean("status_player")
                val action = bundle.getInt("action_music", 0)
                if(action==ACTION_START) viewModel.currentTotalTimeSong = bundle.getInt("total_time_song",0)
                handleActionMusic(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        viewModel.iDFragment = HOME_FRAGMENT_ID

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mBroadcastReceiver,
            IntentFilter("send_music_data_to_activity")
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Image Slider
        listTopMusicPhoto = getListTopMusicPhoto()
        binding.vpg2ImgSliderTopMusic.adapter = PhotoAdapter(listTopMusicPhoto)
        binding.cirIndicator3TopMusic.setViewPager(binding.vpg2ImgSliderTopMusic)
        binding.vpg2ImgSliderTopMusic.setPageTransformer(ZoomOutPageTransformer())

        binding.vpg2ImgSliderTopMusic.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandle.removeCallbacks(mRunnable)
                mHandle.postDelayed(mRunnable, DELAY_PHOTO_TIME)
            }
        })

        //Call Data Music
        if (!viewModel.isLoadData) {
            callDataMusic()
        }

        //Init recyclerView
        initRecyclerView(viewModel.listTop100Music!!)

        //Update layout music
        binding.layoutMusicPlayer.visibility =
            if (viewModel.isDisplayLayoutPlayer) View.VISIBLE else View.GONE
        setButtonPausePlay()
        updateLayoutMusicPlayer()

        //Refresh Data
        binding.swLayoutRefreshData.setOnRefreshListener {
            callDataMusic()
            binding.swLayoutRefreshData.isRefreshing = false
        }
        //
        binding.layoutMusicPlayer.setOnClickListener {
            gotoDetailsFragment()
        }
        //Pause, resume, next, back
        binding.imgPausePlay.setOnClickListener {
            if (viewModel.isPlaying) viewModel.sendActionToService(ACTION_PAUSE,requireContext())
            else viewModel.sendActionToService(ACTION_RESUME,requireContext())
        }
        binding.imgNext.setOnClickListener {
            viewModel.nextSong()
            viewModel.sendSongToService(requireContext())        }
        binding.imgPrevious.setOnClickListener {
            viewModel.backSong()
            viewModel.sendSongToService(requireContext())        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
    }

    private fun getListTopMusicPhoto(): ArrayList<Photo> {
        val listPhoto: ArrayList<Photo> = ArrayList()
        listPhoto.add(Photo(R.drawable.topmusic1))
        listPhoto.add(Photo(R.drawable.topmusic2))
        listPhoto.add(Photo(R.drawable.topmusic3))
        listPhoto.add(Photo(R.drawable.topmusic4))
        listPhoto.add(Photo(R.drawable.topmusic5))
        listPhoto.add(Photo(R.drawable.topmusic6))
        return listPhoto
    }


    private fun gotoDetailsFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frag_container, SongDetailsFragment())
            .addToBackStack("SongDetailsFragment")
            .commit()
    }

    private fun updateLayoutMusicPlayer() {
        if (viewModel.currentListSongs.isEmpty()) return
        song = viewModel.currentListSongs[viewModel.currentPositionSong]

        //Load name song, singer
        binding.tvSongName.text = if (TextUtils.isEmpty(song.title)) "" else song.title

        binding.tvSinger.text = if (TextUtils.isEmpty(song.creator)) "" else song.creator

        //Load image
        val urlAvatar = if (TextUtils.isEmpty(song.avatar)) "" else song.avatar

        Glide.with(requireContext()).load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loaderror)
            .into(binding.imgCircleAvatar)

        setButtonPausePlay()

    }

    private fun initRecyclerView(listTop100MusicCountry: List<Top100OfCountry>) {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerTop100Music.layoutManager = layoutManager
        binding.recyclerTop100Music.adapter = TopMusicOfCountryItemAdapter(listTop100MusicCountry,
            object : IClickSongItemListener {
                override fun onClickItemSong(listSongs: List<Song>, position: Int) {
                    viewModel.currentListSongs = listSongs
                    viewModel.currentPositionSong = position
                    //gotoDetailsFragment()
                    viewModel.sendSongToService(requireContext())
                }

            }
        )
    }

    fun handleActionMusic(action: Int) {
        if(viewModel.iDFragment!= HOME_FRAGMENT_ID) return
        when (action) {
            ACTION_START -> {
                viewModel.isDisplayLayoutPlayer = true
                binding.layoutMusicPlayer.visibility = View.VISIBLE
                updateLayoutMusicPlayer()
            }
            ACTION_RESUME -> {
                viewModel.isDisplayLayoutPlayer = true
                setButtonPausePlay()
            }
            ACTION_PAUSE -> {
                viewModel.isDisplayLayoutPlayer = true
                setButtonPausePlay()
            }
            ACTION_CLEAR -> {
                viewModel.isDisplayLayoutPlayer = false
                binding.layoutMusicPlayer.visibility = View.GONE
            }
            ACTION_NEXT -> {
                viewModel.nextSong()
                viewModel.sendSongToService(requireContext())
            }
            ACTION_PREVIOUS -> {
                viewModel.backSong()
                viewModel.sendSongToService(requireContext())
            }
            ACTION_END_SONG -> {
                viewModel.nextSong()
                viewModel.sendSongToService(requireContext())
            }
        }
    }

    private fun setButtonPausePlay() {
        if (!viewModel.isPlaying) binding.imgPausePlay.setImageResource(R.drawable.ic_play)
        else binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
    }

    private fun callDataMusic() {
        showSnackBar("Loading music data...")
        viewModel.liveDataMusicData.observe(requireActivity(), {
            if (it != null) {

                val listTop100: MutableList<Top100OfCountry> = arrayListOf(
                    Top100OfCountry("Top100 VN", it.songs.top100_VN),
                    Top100OfCountry("Top100 KL", it.songs.top100_KL),
                    Top100OfCountry("Top100 AM", it.songs.top100_AM),
                    Top100OfCountry("Top100 CA", it.songs.top100_CA)
                )

                viewModel.listTop100Music = listTop100
                initRecyclerView(viewModel.listTop100Music!!)
                viewModel.isLoadData = true
            } else {
                showSnackBar("Error in getting data")
            }
        })

        viewModel.makeCallMusicDataAPI()
    }

    private fun showSnackBar(str: String) {
        Snackbar.make(binding.root, str, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val DELAY_PHOTO_TIME: Long = 3000
    }

}