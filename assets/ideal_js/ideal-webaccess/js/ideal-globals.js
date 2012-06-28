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
 
// 0 for browse
// 1 for forms
// 2 for combobox (Also, wtf?!??! Why doesn't Android browser's trackball focus respect the actual page focus????)
var IDEAL_MODE = 0;

// Used to get out of forms mode
var IDEAL_SHIFTCOUNT = 0;

// Used to track where we are on the page
var IDEAL_CurrentElement = null;
var IDEAL_PreviousElement = null;