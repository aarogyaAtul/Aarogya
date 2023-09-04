package com.aarogyaforworkers.aarogya.PC300
import android.util.Log

import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.draw.BackGround.height
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive


data class DataPoint(val x: Float, val y: Float)

val dataToDraw: MutableList<Int> = ArrayList(701)

var arrayCnt = 0

var gain = 2


fun addECGData(data: Int): List<Int> {
    if (dataToDraw.isNotEmpty()) {
        dataToDraw[arrayCnt] = data
        arrayCnt = (arrayCnt + 1) % dataToDraw.size
    }
    dataToDraw.add(data)
    return dataToDraw
}

fun cleanWaveData(): MutableList<Int> {
    arrayCnt = 0
    for (i in dataToDraw) {
        dataToDraw[i] = -1
    }
    Log.d("TAG", "RealtimeGraph: cleaning data $dataToDraw")
    return dataToDraw
}


fun gethMm(data: Int, heightMm: Float, zoomECGforMm: Float, yPX2MMUnit : Float): Float {
    // System.out.println("gethMm:" + data + " " + gain + " " +
    // StaticReceive.is128);
    var data = data
    var d = 0f
    if (StaticReceive.is128) { //wave Y nMax = 255
        data -= 128
        //d = heightMm / 2 - zoomECGforMm * (data * gain + 2048);
        //d = height - (data * gain + 128) / 256f * height;
        d = heightMm / 2 - zoomECGforMm * (data * gain)
        return d/yPX2MMUnit
    } else { //wave Y nMax = 4095
        data -= 2048
        d = height - (data * gain + 2048) / 4096f * height
    }
    return d
}



























