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

package com.curvegraph.deocut.utils

import java.util.concurrent.TimeUnit

object TimeConvert {

    /**
     * convert a time string into the equivalent long milliseconds
     *
     * @param strTime string fomratted as HH:MM:SS:MSMS i.e. "23:59:59:999"
     * @return long integer like 86399999
     */
    fun strToMilli(strTime: String): Long {
        val retVal: Long
        val hour = strTime.substring(0, 2)
        val min = strTime.substring(3, 5)
        val sec = strTime.substring(6, 8)
        val milli = strTime.substring(9, 12)
        val h = Integer.parseInt(hour)
        val m = Integer.parseInt(min)
        val s = Integer.parseInt(sec)
        val ms = Integer.parseInt(milli)

       // val strDebug = String.format("%02d:%02d:%02d.%03d", h, m, s, ms)
        //System.out.println(strDebug);
        val lH = (h * 60 * 60 * 1000).toLong()
        val lM = (m * 60 * 1000).toLong()
        val lS = (s * 1000).toLong()

        retVal = lH + lM + lS + ms.toLong()
        return retVal
    }

    /**
     * convert time in milliseconds to the corresponding string, in case of day
     * rollover start from scratch 23:59:59:999 + 1 = 00:00:00:000
     *
     * @param millis the number of milliseconds corresponding to tim i.e.
     * 34137999 that can be obtained as follows;
     *
     *
     * long lH = h * 60 * 60 * 1000; //hour to milli
     *
     *
     * long lM = m * 60 * 1000; // minute to milli
     *
     *
     * long lS = s * 1000; //seconds to milli
     *
     *
     * millis = lH + lM + lS + ms;
     * @return a string formatted as HH:MM:SS:MSMS i.e. "23:59:59:999"
     */
     fun milliToString(millis: Long): String {

        val hrs = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val min = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        //millis = millis - (hrs * 60 * 60 * 1000); //alternative way
        //millis = millis - (min * 60 * 1000);
        //millis = millis - (sec * 1000);
        //long mls = millis ;
        val mls = millis % 1000
//System.out.println(toRet);
        return String.format("%02d:%02d:%02d.%03d", hrs, min, sec, mls)
    }
}