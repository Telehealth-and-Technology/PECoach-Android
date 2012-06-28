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
 
 // These functions were taken from Charles L. Chen's Fire Vox extension.
 // http://firevox.clcworld.net
 
 
 
//------------------------------------------
//Returns an array of DOM objects that is the lineage
//of the target object.
//The array is ordered such that the target is the 
//last element in the array.
//
function CLC_GetLineage(target){
   var lineage = new Array();
   var object = target;
   while (object){
      lineage.push(object);      
      if (object.parentPointer){
         object = object.parentPointer;
         }
      else{
         object = object.parentNode;
         }
      }
   lineage.reverse();
   while(lineage.length && !lineage[0].tagName && !lineage[0].nodeValue){
      lineage.shift();
      }
   return lineage;
   }

//------------------------------------------
//Compares Lineage A with Lineage B and returns
//the index value in B at which B diverges from A.
//If there is no divergence, the result will be -1.
//Note that if B is the same as A except B has more nodes
//even after A has ended, that is considered a divergence.
//The first node that B has which A does not have will
//be treated as the divergence point.
//
function CLC_CompareLineages(lina, linb){
   var i = 0;
   while( lina[i] && linb[i] && (lina[i] == linb[i]) ){
       i++;
       }
   if ( !lina[i] && !linb[i] ){
      i = -1;
      }
   return i;
   }
//------------------------------------------
//Determines if DOM_obja contains DOM_objb
//
function CLC_AcontainsB(DOM_obja, DOM_objb){
   if(!DOM_obja){
      return false;
      }
   var lineage = CLC_GetLineage(DOM_objb);
   for (var i=0; i < lineage.length; i++){
      if (lineage[i] == DOM_obja){
         return true;
         }
      }
   return false;
   }


//------------------------------------------
//Determines if a lineage has any DOM object with 
//the specified HTML tag string
//
function CLC_TagInLineage(lineage, tag){
   tag = tag.toLowerCase();
   for (var i=0; i < lineage.length; i++){
      if ( lineage[i].localName && (lineage[i].localName.toLowerCase() == tag) ){
         return true;
         }
      }
   return false;
   }
//------------------------------------------
//Builds a logical lineage as opposed to a strict physical lineage.
//These are identical for the most part, except a logical lineage
//will include the label elements as parents of whatever
//they are a label for.
//If there are multiple labels, the earlier they appear in the HTML,
//the "older" they are. "Older"/"younger" means that if both exist, the 
//older one will be the parent of the younger one.
//
function CLC_GetLogicalLineage(target){
   if (!target.tagName){
      return CLC_GetLineage(target);
      }
   if (   (target.tagName.toLowerCase() != "input") && 
          (target.tagName.toLowerCase() != "button") && 
          (target.tagName.toLowerCase() != "select") && 
          (target.tagName.toLowerCase() != "textarea")    ) {
      return CLC_GetLineage(target);
      }
   if (!target.hasAttribute("id")){
      return CLC_GetLineage(target);
      }

    var tempLineage = CLC_GetLineage(target);
    var labelArray = tempLineage[0].getElementsByTagName("label"); 
    //Build up the Logical Lineage of an input element
    var logicalLineage = new Array();
    //Last element should be the target itself
    logicalLineage.push(tempLineage[tempLineage.length-1]);
    //Add labels that are attached to the target
    for (var i = labelArray.length - 1; i >= 0; i--){
       if (labelArray[i].htmlFor == target.id){
          logicalLineage.push(labelArray[i]);
          }
       }
    //Add the rest of the physical lineage
    for (var i = tempLineage.length - 2; i >= 0; i--){
       logicalLineage.push(tempLineage[i]);
       }
    logicalLineage.reverse();
    return logicalLineage;
    }