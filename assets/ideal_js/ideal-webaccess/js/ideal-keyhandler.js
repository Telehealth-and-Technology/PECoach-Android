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
 
function IDEAL_KEYHANDLER_Watcher(event){
  IDEAL_TTS_Stop();
  
  // All hotkeys will be capitalized since we are watching keydown events.
  var hotkey = String.fromCharCode(event.keyCode);
  var isPrevKey = (hotkey == 'Q' || event.keyCode == 37 || event.keyCode == 38);
  var isNextKey = (hotkey == 'P' || event.keyCode == 39 || event.keyCode == 40);

  // Combo box mode
  // Wish we didn't have to do this, but Android browser's
  // trackball focus is immovable and refuses to respect the
  // page's actual focus.
  if (IDEAL_MODE == 2){
    if (isPrevKey){
      window.setTimeout("IDEAL_INTERFACE_PreviousSelectOption();", 0);
      return false;
    }
  
    if (isNextKey){
      window.setTimeout("IDEAL_INTERFACE_NextSelectOption();", 0);
      return false;
    }
  
    if (hotkey == ' '){
      IDEAL_INTERFACE_SwitchToBrowseMode();
	  return false;
    }
  }
  
  if (IDEAL_MODE != 0){
    if (event.keyCode == 16){
      IDEAL_SHIFTCOUNT = IDEAL_SHIFTCOUNT + 1;
    } else {
      IDEAL_SHIFTCOUNT = 0;
    }
    if (IDEAL_SHIFTCOUNT > 1){
      IDEAL_INTERFACE_SwitchToBrowseMode();
	  return false;
    }
	return true;
  }
  
  event.cancelBubble = true;
  if (event.stopPropagation) event.stopPropagation();
  
  if (isPrevKey){
    window.setTimeout("IDEAL_INTERFACE_ReadPrevious();", 0);
    return false;
  }
  
  if (isNextKey){
    window.setTimeout("IDEAL_INTERFACE_ReadNext();", 0);
    return false;
  }
  
  if (hotkey == ' '){
    window.setTimeout("IDEAL_INTERFACE_ActOnCurrentElem();", 0);
    return false;
  }
  return true;
}

function IDEAL_KEYHANDLER_FocusOnCommandArea(){
  var commandArea = document.getElementById("IDEAL_CommandArea");
  if (!commandArea){
    commandArea = document.createElement("input");
    commandArea.id = "IDEAL_CommandArea";
    commandArea.size = 1;
    commandArea.style.position = "absolute";
    commandArea.style.left = "-500px";
    commandArea.addEventListener("blur",
	  function(){
	    if (IDEAL_MODE == 0){
		  window.setTimeout("IDEAL_KEYHANDLER_FocusOnCommandArea()", 100);
		}
	  },
	  true);
	document.body.appendChild(commandArea);
  }
  commandArea.focus();
}

function IDEAL_KEYHANDLER_Init(){
  document.addEventListener("keydown", IDEAL_KEYHANDLER_Watcher, true);
  IDEAL_KEYHANDLER_FocusOnCommandArea();
}

