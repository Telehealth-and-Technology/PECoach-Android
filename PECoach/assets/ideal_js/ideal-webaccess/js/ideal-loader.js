/*
 * Copyright (C) 2010 The IDEAL Group
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
 
// All scripts must be located on the sdcard
// under /sdcard/ideal-webaccess/js/
function IDEAL_LOADER_Load(scriptName){
  var scriptNode = document.createElement("script");
  scriptNode.src = "file:///android_asset/ideal_js/ideal-webaccess/js/" + scriptName;
  document.body.appendChild(scriptNode);
}

IDEAL_LOADER_Load("ideal-globals.js");
IDEAL_LOADER_Load("ideal-tts.js");
IDEAL_LOADER_Load("ideal-keyhandler.js");
IDEAL_LOADER_Load("ideal-interface.js");
IDEAL_LOADER_Load("ideal-dom.js");
IDEAL_LOADER_Load("clc-domUtils.js");

window.setTimeout("IDEAL_KEYHANDLER_Init()", 500);
window.setTimeout("IDEAL_TTS_Speak(document.title, 0, null);", 500);
