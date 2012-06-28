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

function IDEAL_TTS_Speak(messageString, queueMode, params){
  var speechNode = document.getElementById("IDEAL_speechnode");
  if (speechNode){
    speechNode = speechNode.parentNode.removeChild(speechNode);
  }
  speechNode = document.createElement("script");
  var queueModeStr = "/0/";
  if (queueMode == 1){
    queueModeStr = "/1/";
  }
  speechNode.src = "content://org.t2health.pe.webaccess.tts" + queueModeStr + new Date().getTime() + "/" + messageString;
  document.body.appendChild(speechNode);
}

function IDEAL_TTS_Stop(){
  IDEAL_TTS_Speak(" ", 0, null);
}