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
function IDEAL_DOM_GetNextLeafNode(targetNode){
  var currentElem = targetNode;
  var prevElem = currentElem;
  if (currentElem == null){
    currentElem = document.body;
  } else {
    while (!currentElem.nextSibling){
      currentElem = currentElem.parentNode;
      if (currentElem == document.body){
        currentElem = null;
        return currentElem;
      }
    }
    currentElem = currentElem.nextSibling;
  }
  while (!IDEAL_DOM_IsLeaf(currentElem)){
    currentElem = currentElem.firstChild;
  }
  return currentElem;  
}

function IDEAL_DOM_GetPreviousLeafNode(targetNode){
  var currentElem = targetNode;
  var prevElem = currentElem;
  if (currentElem == null){
    currentElem = document.body;
  } else {
    while (!currentElem.previousSibling){
      currentElem = currentElem.parentNode;
      if (currentElem == document.body){
        currentElem = null;
        return currentElem;
      }
    }
    currentElem = currentElem.previousSibling;
  }
  while (!IDEAL_DOM_IsLeaf(currentElem)){
    currentElem = currentElem.lastChild;
  }
  return currentElem;  
}


function IDEAL_DOM_IsLeaf(targetNode){
  if (!targetNode.firstChild){
    return true;
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "a")){
    return true;
  }
  if (CLC_TagInLineage(lineage, "input")){
    return true;
  }
  if (CLC_TagInLineage(lineage, "select")){
    return true;
  }
  return false;
}

function IDEAL_DOM_HasContent(targetNode){
  if (targetNode.nodeType == 8){
    return false;
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "head") || CLC_TagInLineage(lineage, "script")){
    return false;
  }
  if (IDEAL_DOM_IsHidden(targetNode)){
    return false;
  }
  if (IDEAL_DOM_GetContent(targetNode).length < 1){
    return false;
  }
  
  return true;  
}

function IDEAL_DOM_IsHidden(targetNode){
  while (targetNode && (targetNode.nodeType != 1)){
    targetNode = targetNode.parentNode;
  }
  // Our own scratch areas are always considered hidden
  if (targetNode.id && (targetNode.id == "IDEAL_CommandArea")){
    return true;
  }
  if (targetNode){
    var computedStyle = getComputedStyle(targetNode, "");
    var display = computedStyle.getPropertyValue("display");
    if (display == "none"){
      return true;
    }
    var visibility = computedStyle.getPropertyValue("visibility");
    if (visibility == "hidden"){
      return true;
    }
  }
  return false;
}

function IDEAL_DOM_GetContent(targetNode){
  if (!targetNode){
    return "";
  }
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "input")){
    var inputNode = targetNode;
	while (!inputNode.tagName || (inputNode.tagName != "INPUT")){
	  inputNode = inputNode.parentNode;
	}
	var inputType = "";
	if (inputNode.type){
	  inputType = inputNode.type.toLowerCase();
	}
	if (inputType == "radio"){
	  if (inputNode.checked){
	    return "Checked.";
	  } else {
	    return "Not checked.";
	  }
	} else if (inputType == "checkbox"){
	  if (inputNode.checked){
	    return "Checked.";
	  } else {
	    return "Not checked.";
	  }
	} else if (inputType == "password"){
	  var charCount = 0;
	  if (targetNode.value && (targetNode.value.length > 0)){
	    charCount = targetNode.value.length;
	  }
	  return charCount + " characters typed.";
	} else {
      if (targetNode.value && (targetNode.value.length > 0)){
        return targetNode.value;
      } else {
	    return "Blank.";
	  }
	}
  }
  if (CLC_TagInLineage(lineage, "select")){
    var selectNode = targetNode;
	while (!selectNode.tagName || (selectNode.tagName != "SELECT")){
	  selectNode = selectNode.parentNode;
	}
	return selectNode.value;
  }  
  if (CLC_TagInLineage(lineage, "img")){
    if (targetNode.alt){
      return targetNode.alt;
    }
    if (targetNode.title){
      return targetNode.title;
    }
	return ""
  }
  if (targetNode.textContent && (targetNode.textContent.length > 0)){
	var actualContent = targetNode.textContent;
	while ((actualContent.charAt(0) == '\n') || 
           (actualContent.charAt(0) == '\r') || 
           (actualContent.charAt(0) == '\t') || 
           (actualContent.charAt(0) == ' ')    ){
      actualContent = actualContent.substring(1, actualContent.length);
    }
	return actualContent;
  }
  return "";
}

function IDEAL_DOM_GetMetaInfo(targetNode){
  var metaInfo = ""
  var lineage = CLC_GetLineage(targetNode);
  if (CLC_TagInLineage(lineage, "a")){
    metaInfo = "Link.";
  } else if (CLC_TagInLineage(lineage, "select")){
    metaInfo = "Combo box.";
  } else if (CLC_TagInLineage(lineage, "h1")){
    metaInfo = "Heading One.";
  } else if (CLC_TagInLineage(lineage, "h2")){
    metaInfo = "Heading Two.";
  } else if (CLC_TagInLineage(lineage, "h3")){
    metaInfo = "Heading Three.";
  } else if (CLC_TagInLineage(lineage, "h4")){
    metaInfo = "Heading Four.";
  } else if (CLC_TagInLineage(lineage, "h5")){
    metaInfo = "Heading Five.";
  } else if (CLC_TagInLineage(lineage, "h6")){
    metaInfo = "Heading Six.";
  } else if (CLC_TagInLineage(lineage, "input")){
    var inputNode = targetNode;
	while (!inputNode.tagName || (inputNode.tagName != "INPUT")){
	  inputNode = inputNode.parentNode;
	}
	if (inputNode.type && (inputNode.type.toLowerCase() == "radio")){
	  metaInfo = "Radio button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "checkbox")){
	  metaInfo = "Checkbox.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "button")){
	  metaInfo = "Button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "submit")){
	  metaInfo = "Submit button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "password")){
	  metaInfo = "Password.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "image")){
	  metaInfo = "Image button.";
	} else if (inputNode.type && (inputNode.type.toLowerCase() == "reset")){
	  metaInfo = "Reset button.";
	} else {
      metaInfo = "Input.";
	}
  }
  return metaInfo;
}

function IDEAL_DOM_ScrollToElem(targetNode){
  while (!targetNode.offsetParent){
    targetNode = targetNode.parentNode;
  }
  var left = 0;
  var top = 0;
  while (targetNode.offsetParent) { 
    left += targetNode.offsetLeft;
    top += targetNode.offsetTop; 
    targetNode = targetNode.offsetParent;
  }
  left += targetNode.offsetLeft;
  top += targetNode.offsetTop;
  window.scrollTo(left, top);
}