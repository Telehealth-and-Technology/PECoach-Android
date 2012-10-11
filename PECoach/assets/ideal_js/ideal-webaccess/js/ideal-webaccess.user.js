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
 
// ==UserScript==
// @name          IDEAL Web Access Loader
// @description   Loads the IDEAL Web Access web reader.
// @author        IDEAL Group, Inc.
// @include       http://*
// ==/UserScript==

function load(){
  var scriptNode = document.createElement("script");
  scriptNode.src = "file:///android_asset/ideal_js/ideal-webaccess/js/ideal-loader.js";
  document.body.appendChild(scriptNode);
}

window.setTimeout(load, 1000);