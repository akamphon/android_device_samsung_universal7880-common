/*
 * Copyright (C) 2018 Omnirom
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

package org.omnirom.device;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    // Preference keys
    public static final String BUTTON_SWAP_KEY = "button_swap";
    public static final String FP_HOME_KEY = "fp_home";
    public static final String FP_WAKEUP_KEY = "fp_wakeup";
    public static final String DT2W_KEY = "dt2w";

    // Nodes
    public static final String BUTTON_SWAP_NODE = "/proc/touchpanel/reversed_keys_enable";
    public static final String FP_HOME_KEY_NODE = "/sys/devices/soc/soc:fpc_fpc1020/enable_key_events";
    public static final String FP_WAKEUP_NODE = "/sys/devices/soc/soc:fpc_fpc1020/enable_wakeup";
    public static final String VIRTUAL_KEYS_NODE = "/proc/touchpanel/capacitive_keys_enable";
    public static final String DT2W_NODE = "/proc/touchpanel/double_tap_enable";

    // Intents
    public static final String CUST_INTENT = "org.omniriom.device.CUST_UPDATE";
    public static final String CUST_INTENT_EXTRA = "pocketmode_service";

    // Holds <preference_key> -> <proc_node> mapping
    public static final Map<String, String> sBooleanNodePreferenceMap = new HashMap<>();
    public static final Map<String, String> sStringNodePreferenceMap = new HashMap<>();

    // Holds <preference_key> -> <default_values> mapping
    public static final Map<String, Object> sNodeDefaultMap = new HashMap<>();

    // Holds <preference_key> -> <user_set_values> mapping
    public static final Map<String, Object[]> sNodeUserSetValuesMap = new HashMap<>();

    // Holds <preference_key> -> <dependency_check> mapping
    public static final Map<String, String[]> sNodeDependencyMap = new HashMap<>();

    public static final String[] sButtonPrefKeys = {
        BUTTON_SWAP_KEY,
        FP_HOME_KEY,
        FP_WAKEUP_KEY,
	DT2W_KEY
    };

    static {
        sBooleanNodePreferenceMap.put(BUTTON_SWAP_KEY, BUTTON_SWAP_NODE);
        sBooleanNodePreferenceMap.put(FP_HOME_KEY, FP_HOME_KEY_NODE);
        sBooleanNodePreferenceMap.put(FP_WAKEUP_KEY, FP_WAKEUP_NODE);
        sBooleanNodePreferenceMap.put(DT2W_KEY, DT2W_NODE);

        sNodeDefaultMap.put(BUTTON_SWAP_KEY, true);
        sNodeDefaultMap.put(DT2W_KEY, true);
        sNodeDefaultMap.put(FP_HOME_KEY, true);
        sNodeDefaultMap.put(FP_WAKEUP_KEY, true);

        sNodeDependencyMap.put(FP_HOME_KEY, new String[]{ VIRTUAL_KEYS_NODE, "1" });
    }
}
