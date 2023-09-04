package com.aarogyaforworkers.aarogya.MediaPlayer

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.aarogyaforworkers.aarogya.R

class PlayerRepo {

    private val bpHandler = Handler(Looper.getMainLooper())

    private val ecgHandler = Handler(Looper.getMainLooper())

    private var ecgPlayer : MediaPlayer? = null

    private var bpPlayer : MediaPlayer? = null

    private var isEcgRunning = false

    private var isBpRunning = false

    private var contexts : Context? = null

    fun setPlayers(context: Context){
        ecgPlayer = getPlayer(context)
        bpPlayer = getPlayer(context)
        contexts = context
    }

    fun startEcgSound(){
        if(!isEcgRunning){
            setPlayers(contexts!!)
            startECGBeepSound(ecgPlayer!!, false)
            isEcgRunning = true
        }
    }

    fun stopEcgSound(){
        if(isEcgRunning){
            startECGBeepSound(ecgPlayer!!, true)
            isEcgRunning = false
        }
    }

    fun startBpSound(){
        if(!isBpRunning){
            setPlayers(contexts!!)
            startBPBeepSound(bpPlayer!!, false)
            isBpRunning = true
        }
    }

    fun stopBpSound(){
        if(isBpRunning){
            startBPBeepSound(bpPlayer!!, true)
            isBpRunning = false
        }
    }

    fun getPlayer(context : Context) : MediaPlayer{
        return MediaPlayer.create(context, R.raw.bp)
    }

    fun startBPBeepSound(player: MediaPlayer, isStop: Boolean) {
        if (isStop) {
            player.stop()
            player.release()
            bpHandler.removeCallbacksAndMessages(null)
        } else {
            player.start()
            bpHandler.postDelayed({
                startBPBeepSound(player, isStop)
            }, 1000)
        }
    }

    fun startECGBeepSound(player: MediaPlayer, isStop: Boolean) {
        if (isStop) {
            player.stop()
            player.release()
            ecgHandler.removeCallbacksAndMessages(null)
        } else {
            player.start()
            ecgHandler.postDelayed({
                startECGBeepSound(player, isStop)
            }, 1000)
        }
    }


    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: PlayerRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PlayerRepo().also { instance = it }
            }
    }
}