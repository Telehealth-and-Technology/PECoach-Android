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
function IDEAL_INTERFACE_ReadNext(){
  IDEAL_PreviousElement = IDEAL_CurrentElement;
  IDEAL_CurrentElement = IDEAL_DOM_GetNextLeafNode(IDEAL_CurrentElement);
  while (IDEAL_CurrentElement && !IDEAL_DOM_HasContent(IDEAL_CurrentElement)){
    IDEAL_CurrentElement = IDEAL_DOM_GetNextLeafNode(IDEAL_CurrentElement);
  }
  if (IDEAL_CurrentElement){
    IDEAL_TTS_Speak(IDEAL_DOM_GetMetaInfo(IDEAL_CurrentElement) + " " + IDEAL_DOM_GetContent(IDEAL_CurrentElement), 0, null);
    IDEAL_DOM_ScrollToElem(IDEAL_CurrentElement);
  } else {
    IDEAL_TTS_Speak("End of document", 0, null);
  }
}

function IDEAL_INTERFACE_ReadPrevious(){
  IDEAL_PreviousElement = IDEAL_CurrentElement;
  IDEAL_CurrentElement = IDEAL_DOM_GetPreviousLeafNode(IDEAL_CurrentElement);
  while (IDEAL_CurrentElement && !IDEAL_DOM_HasContent(IDEAL_CurrentElement)){
    IDEAL_CurrentElement = IDEAL_DOM_GetPreviousLeafNode(IDEAL_CurrentElement);
  }
  if (IDEAL_CurrentElement){  
    IDEAL_TTS_Speak(IDEAL_DOM_GetMetaInfo(IDEAL_CurrentElement) + " " + IDEAL_DOM_GetContent(IDEAL_CurrentElement), 0, null);
    IDEAL_DOM_ScrollToElem(IDEAL_CurrentElement);
  } else {
    IDEAL_TTS_Speak("Start of document", 0, null);
  }
}

// The majority of this function was taken from the AxsJAX project by Google.
// http://google-axsjax.googlecode.com
function IDEAL_INTERFACE_ActOnCurrentElem(){
  var targetNode = IDEAL_CurrentElement;
  var shiftKey = false;
  //Send a mousedown
  var evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('mousedown', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Send a mouse up
  evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('mouseup', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Send a click
  evt = document.createEvent('MouseEvents');
  evt.initMouseEvent('click', true, true, document.defaultView,
                     1, 0, 0, 0, 0, false, false, shiftKey, false, 0, null);
  //Use a try block here so that if the AJAX fails and it is a link,
  //it can still fall through and retry by setting the document.location.
  try{
    targetNode.dispatchEvent(evt);
  } catch (e){}
  //Clicking on a link does not cause traversal because of script
  //privilege limitations. The traversal has to be done by setting
  //document.location.
  var href = targetNode.getAttribute('href');
  if ((targetNode.tagName == 'A') &&
       href &&
      (href != '#')){
    if (shiftKey){
      window.open(targetNode.href);
    } else {
      document.location = targetNode.href;
    }
  }
  
  if (targetNode.tagName == 'INPUT'){
	var inputType = "";
	if (targetNode.type){
	  inputType = targetNode.type.toLowerCase();
	}
    if ((inputType == 'radio') || (inputType == 'checkbox')){
      window.setTimeout(function(){
	    var checkedStatus = "Not checked."
	    if (targetNode.checked){
		  checkedStatus = "Checked."
		}
        IDEAL_TTS_Speak(checkedStatus, 0, null);
	  }, 0);
	  return;
	}
	// Only switch modes if typing is involved
    IDEAL_MODE = 1;	
	IDEAL_TTS_Speak("Forms mode.", 0, null);
    window.setTimeout(function(){targetNode.focus();}, 0);
  }
  
  if (targetNode.tagName == 'SELECT'){
    IDEAL_MODE = 2;	
	IDEAL_TTS_Speak("Combo box mode.", 0, null);
  }
}

function IDEAL_INTERFACE_SwitchToBrowseMode(){
  IDEAL_MODE = 0;
  IDEAL_KEYHANDLER_FocusOnCommandArea();
  IDEAL_TTS_Speak("Browse mode", 0, null);
  IDEAL_SHIFTCOUNT = 0;
}

function IDEAL_INTERFACE_NextSelectOption(){
  if (IDEAL_CurrentElement &&
      IDEAL_CurrentElement.tagName &&
	  (IDEAL_CurrentElement.tagName == "SELECT")){
	  var selectedIndex = IDEAL_CurrentElement.selectedIndex + 1;
	  if (selectedIndex >= IDEAL_CurrentElement.length){
	    selectedIndex = 0;
	  }
	  IDEAL_CurrentElement.selectedIndex = selectedIndex;
  	  IDEAL_TTS_Speak(IDEAL_CurrentElement.value, 0, null);
  } else {
    IDEAL_INTERFACE_SwitchToBrowseMode();
  }
}

function IDEAL_INTERFACE_PreviousSelectOption(){
  if (IDEAL_CurrentElement &&
      IDEAL_CurrentElement.tagName &&
	  (IDEAL_CurrentElement.tagName == "SELECT")){
	var selectedIndex = IDEAL_CurrentElement.selectedIndex - 1;
	if (selectedIndex < 0){
	  selectedIndex = IDEAL_CurrentElement.length - 1;
	}
	IDEAL_CurrentElement.selectedIndex = selectedIndex;
  	IDEAL_TTS_Speak(IDEAL_CurrentElement.value, 0, null);
  } else {
    IDEAL_INTERFACE_SwitchToBrowseMode();
  }
}