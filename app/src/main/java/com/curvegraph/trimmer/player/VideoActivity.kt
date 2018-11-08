/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.curvegraph.trimmer.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSeekBar
import android.util.Rational
import com.curvegraph.trimmer.R
import com.curvegraph.trimmer.home.ModelFolder
import com.curvegraph.trimmer.utils.CommonObject
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.AnkoLogger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * Allows playback of videos that are in a playlist, using [PlayerHolder] to load the and render
 * it to the [com.google.android.exoplayer2.ui.PlayerView] to render the video output. Supports
 * [MediaSessionCompat] and picture in picture as well.
 */

class VideoActivity : AppCompatActivity(), AnkoLogger {
    private val mediaSession: MediaSessionCompat by lazy { createMediaSession() }
    private val mediaSessionConnector: MediaSessionConnector by lazy {
        createMediaSessionConnector()
    }
    private val playerState by lazy { PlayerState() }
    private lateinit var playerHolder: PlayerHolder
    private var shutDownService = false
    private var service : ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    // Android lifecycle hooks.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        // While the user is in the app, the volume controls should adjust the music volume.
        volumeControlStream = AudioManager.STREAM_MUSIC
        createMediaSession()
        createPlayer()


    }

    private fun getMediaProgressView(): AppCompatSeekBar {
        return timeLineBar.getMediaProgressView()
    }

    fun makeProgress() {
        val seek: AppCompatSeekBar = getMediaProgressView()
        seek.max = playerView.player.duration.toInt()
        shutDownService = false
        if(!service.isShutdown){
            service.shutdown()
        }
         service = Executors.newScheduledThreadPool(1)
        service.scheduleWithFixedDelay({
            if (!shutDownService) {
                runOnUiThread {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            seek.setProgress(playerView.player.currentPosition.toInt(), true)
                        } else
                             seek.progress = (playerView.player.currentPosition.toInt())
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
    }

    override fun onResume() {
        super.onResume()
        if (handlerPassed) {
            handlerPassed = false
            makeProgress()
        }
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
        playerHolder.audioFocusPlayer.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        shutDownService = true
                        handlerPassed = true
                    }
                    Player.STATE_READY -> when (playWhenReady) {
                        true -> {
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

    // Picture in Picture related functions.
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

    private val list: ArrayList<MediaDescriptionCompat> = ArrayList()
    private fun loadVideos(): ArrayList<MediaDescriptionCompat> {
        val folder: ModelFolder = intent.getParcelableExtra(CommonObject.intentVideosList)
        //    val items: List<String> = folder.videoFiles
        val string: String = intent.getStringExtra(CommonObject.intentVideo)
        timeLineBar.setLocalUrl(string)
        //     for (string in items)
        list.add(with(MediaDescriptionCompat.Builder()) {
            setDescription("Curvegraph business solutions")
            setMediaId("3")
            // License - https://mango.blender.org/sharing/
            setMediaUri(Uri.parse(string))
            setTitle(string.substring(string.lastIndexOf("/") + 1))
            setSubtitle(folder.folderName)
            build()
        })

        return list
    }
}