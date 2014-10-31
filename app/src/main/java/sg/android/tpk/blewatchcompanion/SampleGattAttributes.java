package sg.android.tpk.blewatchcompanion;


/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;



/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    public static String HEART_RATE_ZONE = "22a70cdb-9b0e-490a-9f92-582b9fdb3bc6";

    public static String LOWER_VERY_LIGHT = "f572e376-7ab2-45ab-a4e2-b24e8192741b";
    public static String UPPER_VERY_LIGHT = "f572e376-7ab2-45ab-a4e2-7166df181614";

    public static String LOWER_LIGHT = "3620f089-b7e4-4acc-98e4-e15179b3bf84";
    public static String UPPER_LIGHT = "3620f089-b7e4-4acc-98e4-ed22976c6881";

    public static String LOWER_MODERATE = "f9e53bc8-17dd-4a3f-a2a3-c4428d2713bd";
    public static String UPPER_MODERATE = "f9e53bc8-17dd-4a3f-a2a3-5ed643d331e1";

    public static String LOWER_VIGOROUS = "5f270414-6ea1-46b6-8bdc-adc102ad9e95";
    public static String UPPER_VIGOROUS = "5f270414-6ea1-46b6-8bdc-70fa7416e947";

    public static String LOWER_MAXIMAL = "b908a3d7-68f5-4d0a-8004-f2b72f623d07";
    public static String UPPER_MAXIMAL = "b908a3d7-68f5-4d0a-8004-70fa7416e947";




    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(HEART_RATE_ZONE, "Heart Rate Zone");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");

        attributes.put(LOWER_LIGHT, "Lower Light Zone");
        attributes.put(UPPER_LIGHT, "Upper Light Zone");
        attributes.put(LOWER_VERY_LIGHT, "Lower Light Zone");
        attributes.put(UPPER_VERY_LIGHT, "Upper Light Zone");
        attributes.put(LOWER_MODERATE, "Lower Moderate Zone");
        attributes.put(UPPER_MODERATE, "Upper Moderate Zone");
        attributes.put(LOWER_VIGOROUS, "Lower Moderate Zone");
        attributes.put(UPPER_VIGOROUS, "Upper Moderate Zone");
        attributes.put(LOWER_MAXIMAL, "Lower Moderate Zone");
        attributes.put(UPPER_MAXIMAL, "Upper Moderate Zone");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
