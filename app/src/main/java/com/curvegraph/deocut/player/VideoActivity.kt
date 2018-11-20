/*
 *
 *  Copyright 2018 Mathankumar K. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


package com.curvegraph.deocut.player

import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSeekBar
import android.view.View
import com.curvegraph.frameselector.SelectView
import com.curvegraph.deocut.R
import com.curvegraph.deocut.home.ModelFolder
import com.curvegraph.deocut.interfaces.VideoTrimListener
import com.curvegraph.deocut.utils.CommonMethod
import com.curvegraph.deocut.utils.CommonObject
import com.curvegraph.deocut.utils.Dialogs
import com.curvegraph.deocut.utils.TimeConvert.milliToString
import com.curvegraph.deocut.utils.VideoTrimmer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.AnkoLogger
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.net.URLConnection
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * Allows playback of videos that are in a playlist, using [PlayerHolder] to load the and render
 * it to the [com.google.android.exoplayer2.ui.PlayerView] to render the video output. Supports
 * [MediaSessionCompat] and picture in picture as well.
 */

class VideoActivity : AppCompatActivity(), AnkoLogger, SelectView.OnMinMaxDurationListener, VideoTrimListener, Dialogs.DialogListener {
    var trimmedUrl = ""
    override fun skipped() {
        finish()
    }

    override fun playVideo() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmedUrl))
        intent.setDataAndType(Uri.parse(trimmedUrl), "video/mp4")
        startActivity(intent)
    }

    override fun shareVideo() {
        shareFile(File(trimmedUrl))
    }


    override fun draggingStarted() {
        // To get if video is playing when user starts to drag the frame selector
        isPlaying = videoIsPlaying()
        dragging = true
        videoPause()
    }

    override fun draggingFinished() {

        videoReady()
    }


    private fun shareFile(file: File) {

        val intentShareFile = Intent(Intent.ACTION_SEND)

        intentShareFile.type = URLConnection.guessContentTypeFromName(file.getName());
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse(file.absolutePath));

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
        startActivity(Intent.createChooser(intentShareFile, "Share video"));
    }


    override fun minDuration(minDuration: Long) {
        videoSeekTo(minDuration)
        getMediaProgressView().progress = minDuration.toInt()
        println("Minimum : ${milliToString(minDuration)}, player position : ${playerView.player.currentPosition}}")
    }

    override fun maxDuration(maxDuration: Long) {
        videoSeekTo(maxDuration)
    }

    override fun onFailed() {
        progress.visibility = View.INVISIBLE
    }

    override fun onStartTrim() {
        progress.visibility = View.VISIBLE
    }

    override fun onFinishTrim(url: String) {
        trimmedUrl = url
        progress.visibility = View.INVISIBLE
        lateinit var bitmap: Bitmap
        try {
            //   val f = File(stringUrl)
            //      retriever.setDataSource(f.absolutePath)
            val med = FFmpegMediaMetadataRetriever()
            med.setDataSource(url)

            bitmap = CommonMethod.getResizedBitmap(med.getFrameAtTime(1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST), resources.getDimensionPixelSize(R.dimen.dialog_width))
            //   bitmap = getResizedBitmap( retriever.getFrameAtTime(microsecond, MediaMetadataRetriever.OPTION_CLOSEST), view.get()!!.width)
            Dialogs.dialogTrimCompleted(this, bitmap, this)
        } catch (e: Exception) {
        }

    }


    override fun onCancel() {
        progress.visibility = View.INVISIBLE
    }

    private var isPlaying = false
    override fun minMaxDuration(minDuration: Long, maxDuration: Long) {
        var miniDuration = milliToString(minDuration)
        var maxiDuration = milliToString(maxDuration)
        if (maxiDuration.contains("00:")) {
            maxiDuration = maxiDuration.replace("00:", "")
            miniDuration = miniDuration.replace("00:", "")
        }
        val duration = "$miniDuration - $maxiDuration (${milliToString(maxDuration - minDuration).replace("00:", "")})";
        textView.text = duration
        //    println("Min : ${milliToString(minDuration)}, Max : ${milliToString(maxDuration)}")
    }

    private val mediaSession: MediaSessionCompat by lazy { createMediaSession() }
    private val mediaSessionConnector: MediaSessionConnector by lazy {
        createMediaSessionConnector()
    }
    private val playerState by lazy { PlayerState() }
    private lateinit var playerHolder: PlayerHolder
    private var shutDownService = false
    private lateinit var inputUrl: String
    private var service: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    // Android lifecycle hooks.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        // While the user is in the app, the volume controls should adjust the music volume.
        volumeControlStream = AudioManager.STREAM_MUSIC
        createMediaSession()
        createPlayer()
        timeLineBar.getSelectView().setMinMaxListener(this)

        progress.visibility = View.INVISIBLE
        ok.setOnClickListener {
            trimVideo()
        }
        cancel.setOnClickListener {
            finish()
        }
    }

    private fun getMediaProgressView(): AppCompatSeekBar {
        return timeLineBar.getMediaProgressView()
    }

    private var dragging = false
    fun makeProgress() {
        val seek: AppCompatSeekBar = getMediaProgressView()
        seek.max = videoTotalDuration().toInt()
        shutDownService = false
        if (!service.isShutdown) {
            service.shutdown()
        }
        service = Executors.newScheduledThreadPool(1)
        service.scheduleWithFixedDelay({
            if (!shutDownService) {
                runOnUiThread {
                    //        println("Minimum : ${milliToString(timeLineBar.getMinDuration())}, player position : ${playerView.player.currentPosition}}")
                    if (!dragging && videoCurrentPosition() <= timeLineBar.getMaxDuration())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            seek.setProgress(playerView.player.currentPosition.toInt(), true)
                        } else
                            seek.progress = (playerView.player.currentPosition.toInt())
                    else {
                        videoSeekTo(timeLineBar.getMinDuration())
                        videoPause()
                        service.shutdown()
                    }
                }
            } else {
                service.shutdown()
            }
        }, 1, 30, TimeUnit.MILLISECONDS)

    }


    override fun onStart() {
        super.onStart()
        startPlayer()
        activateMediaSession()
    }

    override fun onStop() {
        super.onStop()
        shutDownService = true
        stopPlayer()
        deactivateMediaSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        shutDownService = true
        releasePlayer()
        releaseMediaSession()
    }

    var handlerPassed = false

    override fun onPause() {
        super.onPause()
        shutDownService = true
        handlerPassed = true
        isPlaying = videoIsPlaying()
        videoPause()
        // workaround for https://github.com/google/ExoPlayer/issues/677
        // this hides the playerView when the activity is paused
        // and video is not playing, avoids showing the black box
        if (videoIsPlaying()) {
            playerView?.visibility = View.GONE
        }

        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (handlerPassed) {
            handlerPassed = false
            makeProgress()
        }
        if (playerView.visibility != View.VISIBLE) {
            playerView?.visibility = View.VISIBLE
        }

        getMediaProgressView().progress = videoCurrentPosition().toInt()
    }

    // MediaSession related functions.
    private fun createMediaSession(): MediaSessionCompat = MediaSessionCompat(this, packageName)

    private fun createMediaSessionConnector(): MediaSessionConnector =
            MediaSessionConnector(mediaSession).apply {
                // If QueueNavigator isn't set, then mediaSessionConnector will not handle following
                // MediaSession actions (and they won't show up in the minimized PIP activity):
                // [ACTION_SKIP_PREVIOUS], [ACTION_SKIP_NEXT], [ACTION_SKIP_TO_QUEUE_ITEM]
                setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                    override fun getMediaDescription(player: Player?, windowIndex: Int): MediaDescriptionCompat {

                        return list[windowIndex]
                    }


                })
            }


    // MediaSession related functions.
    private fun activateMediaSession() {
        // Note: do not pass a null to the 3rd param below, it will cause a NullPointerException.
        // To pass Kotlin arguments to Java varargs, use the Kotlin spread operator `*`.
        mediaSessionConnector.setPlayer(playerHolder.audioFocusPlayer, null)
        mediaSession.isActive = true
    }

    private fun deactivateMediaSession() {
        mediaSessionConnector.setPlayer(null, null)
        mediaSession.isActive = false
    }

    private fun releaseMediaSession() {
        mediaSession.release()
    }

    // ExoPlayer related functions.
    private fun createPlayer() {
        playerHolder = PlayerHolder(this, playerState, playerView)

        // Show toasts on state changes.
        playerHolder.audioFocusPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        shutDownService = true
                        handlerPassed = true
                    }
                    Player.STATE_READY -> when (playWhenReady) {
                        true -> {
                            if (dragging) {
                                dragging = false
                                videoSeekTo(timeLineBar.getMinDuration())
                            }
                            makeProgress()
                        }
                        false -> {
                            //    context.toast(R.string.msg_playback_paused)
                        }
                    }
                }
            }
        })
    }

    private fun startPlayer() {

        playerHolder.start(loadVideos())
    }

    private fun stopPlayer() {
        playerHolder.stop()
    }

    private fun releasePlayer() {
        playerHolder.release()
    }

    /*    // Picture in Picture related functions.
        override fun onUserLeaveHint() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(
                        with(PictureInPictureParams.Builder()) {
                            val width = 16
                            val height = 9
                            setAspectRatio(Rational(width, height))
                            build()
                        })
            }
        }

        override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean,
                                                   newConfig: Configuration?) {
           playerView.useController = !isInPictureInPictureMode
        }
        */
    private val list: ArrayList<MediaDescriptionCompat> = ArrayList()

    private fun loadVideos(): ArrayList<MediaDescriptionCompat> {

        var folderName = ""

        if (null != intent.getParcelableExtra(CommonObject.intentVideosList)) {
            val folder: ModelFolder = (intent.getParcelableExtra(CommonObject.intentVideosList))
            folderName = folder.folderName
        }

        //    val items: List<String> = folder.videoFiles
        inputUrl = intent.getStringExtra(CommonObject.intentVideo)
        timeLineBar.setLocalUrl(inputUrl)
        //     for (string in items)
        list.add(with(MediaDescriptionCompat.Builder()) {
            setDescription("Curvegraph business solutions")
            setMediaId("3")
            // License - https://mango.blender.org/sharing/
            setMediaUri(Uri.parse(inputUrl))
            setTitle(inputUrl.substring(inputUrl.lastIndexOf("/") + 1))
            setSubtitle(folderName)
            build()
        })

        return list
    }


    private fun trimVideo() {
        val startingDuration: String = milliToString(timeLineBar.getMinDuration())
        val endingDuration: String = milliToString(timeLineBar.getMaxDuration() - timeLineBar.getMinDuration())

        VideoTrimmer.trim(this, inputUrl, outputFileDirectory(), startingDuration, endingDuration, this)
    }


    private fun outputFileDirectory(): String {
        val directory = File(Environment.getExternalStorageDirectory(), "Deo Cut")
        if (!directory.exists()) {
            directory.mkdir()
        }
        return directory.absolutePath
    }

    private fun videoPause() {
        playerView.player.playWhenReady = false
    }

    private fun videoPlay() {
        videoReady()
        playerView.player.playWhenReady = true
    }

    private fun videoReady() {
        playerView.player.playbackState == Player.STATE_READY
    }

    private fun videoIsPlaying(): Boolean {
        return playerView.player.playWhenReady && playerView.player.playbackState == Player.STATE_READY
    }

    private fun videoSeekTo(duration: Long) {
        playerView.player.seekTo(duration)
    }

    private fun videoCurrentPosition(): Long {
        return playerView.player.currentPosition
    }

    private fun videoTotalDuration(): Long {
        return playerView.player.duration
    }

    private fun videoPlayer(): Player {
        return playerView.player
    }


}